# Corrosion

> Corrosion was formerly called RedOx, but required a name change due to naming overlap with another project ([See issue #24](https://github.com/LucasBullen/redOx/issues/24))

Support for Rust editing in Eclipse IDE.

Corrosion is a Rust development plugin for the Eclipse IDE. Both [issue reports](https://github.com/LucasBullen/corrosion/issues) and [pull requests](https://github.com/LucasBullen/corrosion/pulls) are greatly appreciated.

[![Build Status](https://travis-ci.org/LucasBullen/corrosion.svg?branch=master)](https://travis-ci.org/LucasBullen/corrosion)

![Screenshot](images/editorOverview.png "Screenshot of Corrosion editor")

## Installation
Refer to our [Installation Guide](documentation/Installation.md)

## Prerequisites

The Rustup and Cargo commands are required for accessing the language server and performing most tasks. Go into the Rust preferences and either install the commands or input their paths if not automatically found.

## Contributing
[Issue reports](https://github.com/LucasBullen/corrosion/issues) and [pull requests](https://github.com/LucasBullen/corrosion/pulls) are always appreciated.

For setting up Corrosion for testing and development follow the [Using Github Installation Instructions](documentation/Installation.md#using-github)

The p2 repository is not kept up to date with the master branch. Releases to the p2 repository will be made in more controlled released after the initial release of Corrosion v1.

## Concept

Corrosion uses the [lsp4e](https://projects.eclipse.org/projects/technology.lsp4e) project to integrate with the [Rust Language Server](https://github.com/rust-lang-nursery/rls) and [TM4E](https://projects.eclipse.org/projects/technology.tm4e) project to provide syntax highlighting in order to provide a rich Rust editor in the Eclipse IDE.

The Rust and Cargo logos are owned by Mozilla and distributed under the terms of the [Creative Commons Attribution license (CC-BY)](https://creativecommons.org/licenses/by/4.0/). [More Info](https://www.rust-lang.org/en-US/legal.html)
