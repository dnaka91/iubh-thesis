use crossterm::event::{KeyCode, KeyEvent};
use tui::{
    buffer::Buffer,
    layout::{Alignment, Constraint, Direction, Layout, Rect},
    widgets::{Block, Borders, Clear, Paragraph, Widget},
};

use crate::RectExt;

pub struct ConfirmDialog<'a> {
    pub title: &'a str,
    pub message: String,
}

impl<'a> ConfirmDialog<'a> {
    pub fn handle_event(event: KeyEvent) -> Option<bool> {
        match event.code {
            KeyCode::Enter => Some(true),
            KeyCode::Esc => Some(false),
            _ => None,
        }
    }
}

impl<'a> Widget for ConfirmDialog<'a> {
    fn render(self, area: Rect, buf: &mut Buffer) {
        let area = Rect::new(0, 0, 40, 10).center(area);

        Clear.render(area, buf);
        Block::default()
            .borders(Borders::ALL)
            .title(self.title)
            .render(area, buf);

        let area = area.inset(3, 2);

        let chunks = Layout::default()
            .direction(Direction::Vertical)
            .constraints([
                Constraint::Min(1),
                Constraint::Length(1),
                Constraint::Length(1),
            ])
            .split(area);

        Paragraph::new(self.message)
            .alignment(Alignment::Center)
            .render(chunks[0], buf);
        Paragraph::new("CONFIRM")
            .alignment(Alignment::Center)
            .render(chunks[2], buf);
    }
}
