# EnergyLib

**EnergyLib** is a Spigot library that enables the creation and management of a complete electricity system in Minecraft. It includes generators, batteries, cables, and machines that consume energy, with dynamic and flexible network management.

## Features

- **Energy Generators**: Create blocks that produce electricity on a configurable basis (e.g., per tick).
- **Machines**: Add blocks that consume energy to perform various actions (mining, processing, etc.).
- **Batteries**: Store excess energy and discharge it when needed.
- **Cables**: Connect generators, batteries, and machines using cables to transport electricity.
- **Dynamic Electrical Networks**: Autonomous energy networks that update automatically when the world changes (block additions/removals).
- **Persistence**: Save and restore electrical networks across server restarts.

## Installation

1. Download the **EnergyLib** JAR file and place it in your server's `plugins` folder.
2. Start the server, then stop it. A configuration file will be generated automatically in the `plugins/EnergyLib` folder.
3. Customize the settings in the configuration file as needed.
4. Restart the server for the library to be fully functional.

## Usage

EnergyLib is designed as a library to help you create custom electrical systems within your Minecraft plugins. Below are examples of the key classes and concepts provided by the library.

## Contributing

Contributions to **EnergyLib** are welcome! Here's how you can contribute:

1. **Fork** this repository.
2. **Create a branch** for your feature (`git checkout -b my-feature`).
3. **Commit your changes** (`git commit -am 'Add new feature'`).
4. **Push the branch** (`git push origin my-feature`).
5. Create a **pull request**.

## Issues and Suggestions

If you encounter any issues or have suggestions, feel free to open an **issue** on GitHub.

## License

EnergyLib is licensed under the MIT License. You are free to use it in your personal and commercial projects, with or without modifications.

---

### Final Notes:

- **EnergyLib** is a flexible starting point for adding electrical systems to Minecraft servers.
- You can easily extend the library to add new types of blocks (generators, machines, etc.) and customize energy behaviors based on your specific needs.
