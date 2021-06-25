use tui::{
    buffer::Buffer,
    layout::{Alignment, Rect},
    style::{Color, Style},
    widgets::{Block, Paragraph, Widget},
};

pub struct GoalProgress<'a> {
    current: u32,
    max: u32,
    block: Option<Block<'a>>,
    style: Style,
}

impl<'a> GoalProgress<'a> {
    pub fn new(current: u32, max: u32) -> Self {
        Self {
            current,
            max,
            block: None,
            style: Default::default(),
        }
    }

    pub fn block(mut self, block: Block<'a>) -> Self {
        self.block = Some(block);
        self
    }

    pub fn style(mut self, style: Style) -> Self {
        self.style = style;
        self
    }
}

impl<'a> Widget for GoalProgress<'a> {
    fn render(self, area: Rect, buf: &mut Buffer) {
        let mut area = match self.block {
            Some(block) => {
                let inner = block.inner(area);
                block.render(area, buf);
                inner
            }
            None => area,
        };

        let fill =
            area.x + (area.width as f64 * self.current as f64 / self.max as f64).round() as u16;

        let fg = self.style.fg.unwrap_or(Color::Reset);
        let bg = self.style.bg.unwrap_or(Color::Reset);

        for x in area.left()..area.right() {
            for y in area.top()..area.bottom() {
                buf.get_mut(x, y)
                    .set_char(' ')
                    .set_bg(if x <= fill { fg } else { bg })
                    .set_fg(if x <= fill { bg } else { fg });
            }
        }

        area.y += area.height / 2;

        Paragraph::new(format!(
            "{}/{} ml ({} %)",
            self.current,
            self.max,
            self.current * 100 / self.max
        ))
        .alignment(Alignment::Center)
        .render(area, buf);
    }
}
