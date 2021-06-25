# IUBH Thesis

> Performance optimization and source code sharing in Android applications by using the Rust
> programming language.

This project is the source code and provided binary files for my thesis at the IUBH in Germany.

It contains three specific folders that are important in regards to the written document handed out
to the university:

- **bender** is a project that shows how to combine an Android application with Rust by using a
  variety of different technologies and allows to compare performance and application size as well
  as other details.
- **drinkup** is a project using Rust as the main language and implements a drink reminder
  application with the main logic and a terminal interface written in Rust. A full Android app is
  included that reuses the core logic as external library and provides a different user interface.
  It helps to show how well a full application can be developed by using Rust in comparison to a
  fully in Kotlin written app. Additionally it shows how well source code can be shared between
  multiple projects across language boundaries.
- **fdroid** contains a custom F-Droid repository that is available [here](https://dnaka91.github.io/iubh-thesis/fdroid/repo) and can be added to F-Droid to easily install the pre-compiled
  applications of the other projects.

## Setup

Both the **bender** and **drinkup** project require `Android Studio v4.2` or later and the latest
`Rust toolchain` installed through `rustup`. With both set up the projects can be opened in Android
Studio for editing the Android apps and any preferred IDE to edit the Rust components.

## Build

After a successful setup, projects can be compiled with the respective `cargo` or `gradle` commands.

## License

All works of this repository is licensed under the [GPL-3.0-only](LICENSE).