use std::{
    io,
    sync::mpsc::{self, Receiver},
    thread,
};

use anyhow::{Context, Result};
use chrono::prelude::*;
use crossterm::{
    event::{self, Event, KeyCode, KeyModifiers},
    execute,
    terminal::{self, EnterAlternateScreen, LeaveAlternateScreen},
};
use directories_next::ProjectDirs;
use drinkup::{
    models::{IntakeSize, Record, Schedule},
    DrinkUp, DrinkUpDefault,
};
use tui::{
    backend::{Backend, CrosstermBackend},
    layout::{Constraint, Direction, Layout, Rect},
    style::{Color, Style},
    widgets::{Block, Borders, List, ListItem, ListState, Paragraph},
    Terminal,
};

use crate::{dialogs::*, widgets::*};

mod dialogs;
mod widgets;

fn main() -> Result<()> {
    let dirs = ProjectDirs::from("com.github", "dnaka91", "drinkup")
        .context("failed finding project dirs")?;
    std::fs::create_dir_all(dirs.config_dir())?;
    std::fs::create_dir_all(dirs.data_dir())?;

    println!("{:?}", dirs.config_dir());

    let mut drinkup = DrinkUp::new_default(dirs.config_dir())?;

    drinkup.save_schedule(&Schedule {
        start: NaiveTime::from_hms(8, 20, 0),
        end: NaiveTime::from_hms(23, 30, 0),
        goal: 2100,
    })?;

    drinkup.save_intake_size(&IntakeSize {
        id: 4,
        name: "Huge Bottle".to_owned(),
        amount: 1500,
    })?;

    if drinkup.history(Utc::now().date())?.is_empty() {
        for i in 1..=10 {
            drinkup.add_record(&Record {
                id: 0,
                timestamp: Utc::now(),
                name: format!("Test {}", i),
                amount: i * 20,
            })?;
        }
    }

    run(create_terminal()?, create_event_listener(), drinkup)?;
    shutdown()?;

    Ok(())
}

fn create_terminal() -> Result<Terminal<impl Backend>> {
    terminal::enable_raw_mode()?;

    let mut stdout = io::stdout();
    execute!(stdout, EnterAlternateScreen)?;

    let backend = CrosstermBackend::new(io::stdout());
    Terminal::new(backend).map_err(Into::into)
}

fn shutdown() -> Result<()> {
    execute!(io::stdout(), LeaveAlternateScreen)?;
    terminal::disable_raw_mode()?;

    Ok(())
}

#[derive(Eq, PartialEq)]
enum ShowingDialog {
    None,
    ConfirmIntake,
}

fn create_event_listener() -> Receiver<Event> {
    let (tx, rx) = mpsc::channel();

    thread::spawn(move || {
        while let Ok(event) = event::read() {
            if matches!(event, Event::Key(_) | Event::Resize(_, _)) {
                tx.send(event).ok();
            }
        }
    });

    rx
}

fn run(
    mut terminal: Terminal<impl Backend>,
    events: Receiver<Event>,
    mut drinkup: DrinkUpDefault,
) -> Result<()> {
    let schedule = drinkup.schedule().to_owned();
    let items = drinkup.intake_sizes().to_owned();
    let mut history = drinkup.history(Utc::now().date())?.into_owned();

    let mut ml = drinkup.progress()?;
    let mut percent = ml * 100 / schedule.goal;
    let mut list_state = ListState::default();
    let mut showing_dialog = ShowingDialog::None;

    let schedule_text = format!(
        "\
        Start: {}\n\
        End:   {}\n\
        Goal:  {} ml\
    ",
        schedule.start.format("%H:%M"),
        schedule.end.format("%H:%M"),
        schedule.goal,
    );

    list_state.select(Some(0));

    loop {
        terminal.draw(|f| {
            let area = f.size();

            let chunks = Layout::default()
                .direction(Direction::Horizontal)
                .constraints([Constraint::Length(35), Constraint::Min(25)])
                .split(area);

            let chunks_left = Layout::default()
                .direction(Direction::Vertical)
                .constraints([
                    Constraint::Length(5),
                    Constraint::Length(8),
                    Constraint::Percentage(50),
                ])
                .split(chunks[0]);

            let schedule_block = Paragraph::new(schedule_text.as_ref())
                .block(Block::default().borders(Borders::ALL).title("Schedule"));
            f.render_widget(schedule_block, chunks_left[0]);

            let text = "\
            esc - cancel dialog\n\
            ";

            let help =
                Paragraph::new(text).block(Block::default().borders(Borders::ALL).title("Help"));
            f.render_widget(help, chunks_left[1]);

            let text = history.iter().fold(String::new(), |mut s, h| {
                s.push_str(&format!(
                    "{}: {} ({} ml)",
                    h.timestamp
                        .with_timezone(&Local)
                        .naive_local()
                        .format("%H:%M:%S"),
                    h.name,
                    h.amount
                ));
                s.push('\n');
                s
            });
            let history =
                Paragraph::new(text).block(Block::default().borders(Borders::ALL).title("History"));
            f.render_widget(history, chunks_left[2]);

            let chunks_right = Layout::default()
                .direction(Direction::Vertical)
                .constraints([Constraint::Length(5), Constraint::Min(5)])
                .split(chunks[1]);

            let gauge_color = match percent {
                0..=30 => Color::LightRed,
                31..=70 => Color::LightYellow,
                _ => Color::LightGreen,
            };

            let progress = GoalProgress::new(ml, schedule.goal)
                .block(Block::default().borders(Borders::ALL).title("Today's Goal"))
                .style(Style::default().fg(gauge_color).bg(Color::Black));
            f.render_widget(progress, chunks_right[0]);

            let list = List::new(
                items
                    .iter()
                    .map(|i| ListItem::new(i.name.as_ref()))
                    .collect::<Vec<_>>(),
            )
            .block(
                Block::default()
                    .title("Record Intake")
                    .borders(Borders::ALL),
            )
            .highlight_symbol(">>");
            f.render_stateful_widget(list, chunks_right[1], &mut list_state);

            match showing_dialog {
                ShowingDialog::ConfirmIntake => {
                    let item = &items[list_state.selected().unwrap()];
                    let dialog = ConfirmDialog {
                        title: "Confirm Intake",
                        message: format!("Record {} ({} ml)?", item.name, item.amount),
                    };
                    f.render_widget(dialog, area);
                }
                ShowingDialog::None => {}
            }
        })?;

        if let Ok(event) = events.recv() {
            match event {
                Event::Resize(_, _) => {}
                Event::Key(ke) => {
                    if ke.code == KeyCode::Char('c') && ke.modifiers == KeyModifiers::CONTROL {
                        break;
                    }

                    match showing_dialog {
                        ShowingDialog::ConfirmIntake => {
                            if let Some(result) = ConfirmDialog::handle_event(ke) {
                                showing_dialog = ShowingDialog::None;
                                if result {
                                    let item = &items[list_state.selected().unwrap()];
                                    ml += item.amount;
                                    percent = ml * 100 / schedule.goal;

                                    let record = Record {
                                        id: 0,
                                        timestamp: Utc::now(),
                                        name: item.name.clone(),
                                        amount: item.amount,
                                    };
                                    drinkup.add_record(&record)?;
                                    history.push(record);
                                }
                            }
                        }
                        ShowingDialog::None => match ke.code {
                            KeyCode::Esc | KeyCode::Char('q') => break,
                            KeyCode::Up => {
                                list_state.select(list_state.selected().map(|i| {
                                    if i == 0 {
                                        items.len() - 1
                                    } else {
                                        i - 1
                                    }
                                }));
                            }
                            KeyCode::Down => {
                                list_state
                                    .select(list_state.selected().map(|i| (i + 1) % items.len()));
                            }
                            KeyCode::Enter => {
                                showing_dialog = ShowingDialog::ConfirmIntake;
                            }
                            _ => {}
                        },
                    }
                }
                _ => {}
            }
        } else {
            break;
        }
    }

    Ok(())
}

trait RectExt {
    fn center(self, other: Self) -> Self;
    fn inset(self, w: u16, h: u16) -> Self;
}

impl RectExt for Rect {
    fn center(self, other: Self) -> Self {
        Self {
            x: if other.width <= self.width {
                0
            } else {
                other.width / 2 - self.width / 2
            },
            y: if other.height <= self.height {
                0
            } else {
                other.height / 2 - self.height / 2
            },
            width: self.width.min(other.width),
            height: self.height.min(other.height),
        }
    }

    fn inset(self, w: u16, h: u16) -> Self {
        Self {
            x: if self.x >= self.right() {
                self.x
            } else {
                self.x + w
            },
            y: if self.y >= self.bottom() {
                self.y
            } else {
                self.y + h
            },
            width: if self.x >= self.right() {
                self.width
            } else {
                self.width.saturating_sub(w * 2)
            },
            height: if self.y >= self.bottom() {
                self.height
            } else {
                self.height.saturating_sub(h * 2)
            },
        }
    }
}
