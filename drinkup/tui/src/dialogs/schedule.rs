use chrono::{prelude::*, Duration};
use crossterm::event::{KeyCode, KeyEvent};
use drinkup::models::Schedule;
use tui::{
    buffer::Buffer,
    layout::{Alignment, Rect},
    style::{Color, Modifier, Style},
    widgets::{Block, Borders, Clear, Paragraph, StatefulWidget, Widget},
};

use crate::RectExt;

#[derive(Copy, Clone)]
pub struct ScheduleDialog;

pub struct ScheduleDialogState {
    schedule: Schedule,
    selected: Selection,
    editing: bool,
    time_selected: TimeSelection,
}

impl ScheduleDialogState {
    pub fn reset(&mut self) {
        self.selected = Selection::Start;
        self.editing = false;
        self.time_selected = TimeSelection::Hour;
    }
}

impl Default for ScheduleDialogState {
    fn default() -> Self {
        Self {
            schedule: Schedule {
                start: NaiveTime::from_hms(0, 0, 0),
                end: NaiveTime::from_hms(0, 0, 0),
                goal: 2400,
            },
            selected: Selection::Start,
            editing: false,
            time_selected: TimeSelection::Hour,
        }
    }
}

#[derive(Copy, Clone, Eq, PartialEq)]
enum Selection {
    Start,
    End,
    Goal,
    Save,
}

#[derive(Copy, Clone, Eq, PartialEq)]
enum TimeSelection {
    Hour,
    Minute,
}

impl ScheduleDialog {
    pub fn handle_event(event: KeyEvent, state: &mut ScheduleDialogState) -> Option<bool> {
        match event.code {
            KeyCode::Down => {
                if state.editing {
                    match state.selected {
                        Selection::Start => {
                            state.schedule.start -= match state.time_selected {
                                TimeSelection::Hour => Duration::hours(1),
                                TimeSelection::Minute => Duration::minutes(1),
                            };
                        }
                        Selection::End => {
                            state.schedule.end -= match state.time_selected {
                                TimeSelection::Hour => Duration::hours(1),
                                TimeSelection::Minute => Duration::minutes(1),
                            };
                        }
                        _ => {}
                    }
                } else {
                    state.selected = match state.selected {
                        Selection::Start => Selection::End,
                        Selection::End => Selection::Goal,
                        Selection::Goal => Selection::Save,
                        Selection::Save => state.selected,
                    }
                }
            }
            KeyCode::Up => {
                if state.editing {
                    match state.selected {
                        Selection::Start => {
                            state.schedule.start += match state.time_selected {
                                TimeSelection::Hour => Duration::hours(1),
                                TimeSelection::Minute => Duration::minutes(1),
                            };
                        }
                        Selection::End => {
                            state.schedule.end += match state.time_selected {
                                TimeSelection::Hour => Duration::hours(1),
                                TimeSelection::Minute => Duration::minutes(1),
                            };
                        }
                        _ => {}
                    }
                } else {
                    state.selected = match state.selected {
                        Selection::Start => state.selected,
                        Selection::End => Selection::Start,
                        Selection::Goal => Selection::End,
                        Selection::Save => Selection::Goal,
                    }
                }
            }
            KeyCode::Left => {
                if state.editing
                    && (state.selected == Selection::Start || state.selected == Selection::End)
                    && state.time_selected == TimeSelection::Minute
                {
                    state.time_selected = TimeSelection::Hour;
                }
            }

            KeyCode::Right => {
                if state.editing
                    && (state.selected == Selection::Start || state.selected == Selection::End)
                    && state.time_selected == TimeSelection::Hour
                {
                    state.time_selected = TimeSelection::Minute;
                }
            }
            KeyCode::Char(c @ '0'..='9') => {
                if state.editing
                    && state.selected == Selection::Goal
                    && state.schedule.goal <= 10000
                {
                    state.schedule.goal = state.schedule.goal * 10 + (c as u32 - '0' as u32)
                }
            }
            KeyCode::Backspace => {
                if state.editing && state.selected == Selection::Goal && state.schedule.goal > 0 {
                    state.schedule.goal /= 10
                }
            }
            KeyCode::Enter => match state.selected {
                Selection::Start | Selection::End | Selection::Goal => {
                    state.editing = !state.editing;
                }
                Selection::Save => return Some(true),
            },
            KeyCode::Esc => return Some(false),
            _ => {}
        }

        None
    }
}

impl StatefulWidget for ScheduleDialog {
    type State = ScheduleDialogState;

    fn render(self, area: Rect, buf: &mut Buffer, state: &mut Self::State) {
        let area = Rect::new(0, 0, 23, 9).center(area);

        let mut style = Style::default().fg(Color::Green);
        if state.editing {
            style = style.add_modifier(Modifier::BOLD);
        }

        Clear.render(area, buf);
        Block::default()
            .borders(Borders::ALL)
            .title("Edit Settings")
            .render(area, buf);

        let mut area = area.inset(3, 2);
        area.height = 1;

        let mut start =
            Paragraph::new(format!("Start: [{}]", state.schedule.start.format("%H:%M")));
        if state.selected == Selection::Start {
            start = start.style(style);
        }

        start.render(area, buf);
        area.y += 1;

        let mut end = Paragraph::new(format!("End:   [{}]", state.schedule.end.format("%H:%M")));
        if state.selected == Selection::End {
            end = end.style(style);
        }

        end.render(area, buf);
        area.y += 1;

        let mut goal = Paragraph::new(format!("Goal:  [{:<5}] ml", state.schedule.goal));
        if state.selected == Selection::Goal {
            goal = goal.style(style);
        }

        goal.render(area, buf);
        area.y += 2;

        let mut save = Paragraph::new("SAVE").alignment(Alignment::Center);
        if state.selected == Selection::Save {
            save = save.style(style);
        }

        save.render(area, buf);
    }
}
