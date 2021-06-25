use crossterm::event::{KeyCode, KeyEvent};
use drinkup::models::IntakeSize;
use tui::{
    buffer::Buffer,
    layout::{Alignment, Rect},
    style::{Color, Modifier, Style},
    widgets::{Block, Borders, Clear, Paragraph, StatefulWidget, Widget},
};

use crate::RectExt;

#[derive(Copy, Clone)]
pub struct IntakeSizeDialog;

pub struct IntakeSizeDialogState {
    sizes: Vec<IntakeSize>,
    selected: Selection,
    line_selection: LineSelection,
    editing: bool,
}

impl IntakeSizeDialogState {
    pub fn reset(&mut self) {
        self.selected = Selection::Size(0);
        self.line_selection = LineSelection::Value;
        self.editing = false;
    }
}

impl Default for IntakeSizeDialogState {
    fn default() -> Self {
        Self {
            sizes: vec![
                IntakeSize {
                    id: 1,
                    name: "Small cup".to_owned(),
                    amount: 150,
                },
                IntakeSize {
                    id: 2,
                    name: "Big cup".to_owned(),
                    amount: 250,
                },
                IntakeSize {
                    id: 3,
                    name: "Bottle".to_owned(),
                    amount: 500,
                },
            ],
            selected: Selection::Size(0),
            line_selection: LineSelection::Value,
            editing: false,
        }
    }
}

#[derive(Copy, Clone, Eq, PartialEq)]
enum Selection {
    Size(usize),
    Save,
}

#[derive(Copy, Clone, Eq, PartialEq)]
enum LineSelection {
    Name,
    Value,
}

impl IntakeSizeDialog {
    pub fn handle_event(event: KeyEvent, state: &mut IntakeSizeDialogState) -> Option<bool> {
        match event.code {
            KeyCode::Down => {
                state.selected = match state.selected {
                    Selection::Size(i) => {
                        if i == state.sizes.len() - 1 {
                            Selection::Save
                        } else {
                            Selection::Size(i + 1)
                        }
                    }
                    Selection::Save => state.selected,
                }
            }
            KeyCode::Up => {
                state.selected = match state.selected {
                    Selection::Size(i) => Selection::Size(i.saturating_sub(1)),
                    Selection::Save => Selection::Size(state.sizes.len().saturating_sub(1)),
                }
            }
            //     KeyCode::Left => {
            //         if state.editing
            //             && (state.selected == Selection::Start || state.selected == Selection::End)
            //             && state.time_selected == TimeSelection::Minute
            //         {
            //             state.time_selected = TimeSelection::Hour;
            //         }
            //     }

            //     KeyCode::Right => {
            //         if state.editing
            //             && (state.selected == Selection::Start || state.selected == Selection::End)
            //             && state.time_selected == TimeSelection::Hour
            //         {
            //             state.time_selected = TimeSelection::Minute;
            //         }
            //     }
            KeyCode::Char(c @ '0'..='9') => {
                if state.editing && state.line_selection == LineSelection::Value {
                    if let Selection::Size(i) = state.selected {
                        let size = &mut state.sizes[i];
                        if size.amount <= 10000 {
                            size.amount = size.amount * 10 + (c as u32 - '0' as u32);
                        }
                    }
                }
            }
            KeyCode::Backspace => {
                if state.editing && state.line_selection == LineSelection::Value {
                    if let Selection::Size(i) = state.selected {
                        let size = &mut state.sizes[i];
                        if size.amount > 0 {
                            size.amount /= 10;
                        }
                    }
                }
            }
            KeyCode::Enter => match state.selected {
                Selection::Size(_) => {
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

impl StatefulWidget for IntakeSizeDialog {
    type State = IntakeSizeDialogState;

    fn render(self, area: Rect, buf: &mut Buffer, state: &mut Self::State) {
        let area = Rect::new(0, 0, 36, 6 + state.sizes.len() as u16).center(area);

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

        for (i, size) in state.sizes.iter().enumerate() {
            let mut goal = Paragraph::new(format!("[{:15}]:  [{:<5}] ml", size.name, size.amount));
            if state.selected == Selection::Size(i) {
                goal = goal.style(style);
            }

            goal.render(area, buf);
            area.y += 1;
        }

        area.y += 1;

        let mut save = Paragraph::new("SAVE").alignment(Alignment::Center);
        if state.selected == Selection::Save {
            save = save.style(style);
        }

        save.render(area, buf);
    }
}
