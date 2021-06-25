pub type Result<T> = std::result::Result<T, Error>;

#[derive(Debug, thiserror::Error)]
pub enum Error {
    #[error("Error during file I/O")]
    Io(#[from] std::io::Error),
    #[error("Failed processing JSON data")]
    Json(#[from] serde_json::Error),
    #[error("TOML deserialization failed")]
    TomlDe(#[from] toml::de::Error),
    #[error("TOML serialization failed")]
    TomlSer(#[from] toml::ser::Error),
}
