# RedstoneFlux

**RedstoneFlux** is a modern fork of [EnergyLib](https://github.com/Traqueur-dev/EnergyLib), rewritten with Kotlin DSLs
and optimized for performance and developer experience. Designed with simplicity, flexibility, and future-proofing in
mind, RedstoneFlux provides a universal energy system for Minecraft plugins.

---

## ✨ Features

* ⚡ Universal energy system for Bukkit-based Minecraft servers
* 🚀 Built with Kotlin and DSL-first design
* 📆 Easy integration with Paper and Folia
* 🔌 Modular and extensible API
* 🔍 Emphasis on performance and maintainability

---

## 🔧 Getting Started

### Requirements

* Minecraft 1.20.6 or newer (Paper/Folia)
* Java 17+
* Gradle or Maven for dependency management

### Installation

RedstoneFlux is planned to be published on JitPack.

Add the following to your `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}
```

And in your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Jadiefication:RedstoneFlux:<version>")
}
```

---

## 🧱 Status

This project is currently under active development. Usage documentation and public APIs will be stabilized and
documented in future releases.

For now, you can explore the codebase directly or check the [`Nodal`](https://github.com/Jadiefication/Nodal) for example usage.

---

## 💬 Credits

* Forked from [EnergyLib by Traqueur-dev](https://github.com/Traqueur-dev/EnergyLib)
* Uses [FoliaLib](https://github.com/TechnicallyCoded/FoliaLib) for Folia support

---

## 📄 License

MIT License. See `LICENSE` file for more information.

---

## 📦 Roadmap

*
