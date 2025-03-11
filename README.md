# Glowing Eyes

This mod allows you to make your eyes glow in Minecraft, inspired by the **Vampirism** mod.
It is compatible with both **Fabric** and **Forge** mod loaders,
and no additional setup should be required to get started.

## Download

You can download the latest release of the mod from the following platforms:
- **[CurseForge](https://www.curseforge.com/minecraft/mc-mods/glowing-eyes)**
- **[Modrinth](https://modrinth.com/mod/glowing-eyes)**

Alternatively, you can build the mod from source by following the steps outlined below.

---

## Requirements

- **Java 17** or higher
- **Minecraft 1.19.4**
- **Fabric** or **Forge** mod loader (both are supported)

---

## Building the Mod

To build the mod from source, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/RaydanOMGr/GlowingEyes.git
   ```

2. **Navigate to the project directory**:
   ```bash
   cd GlowingEyes
   ```

3. **Build the mod**:
   After you have made changes to the mod, you want to build it.
   The project includes the Gradle wrapper, so you don’t need to install Gradle manually. Simply run:
   ```bash
   ./gradlew build
   ```

   This will compile the mod and package it into `.jar` files. The resulting files can be found in the `build/libs` directories for each loader (excluding `common`).

4. **Run and test the mod**:
   You can run Minecraft with the mod using:
   ```bash
   ./gradlew :fabric:runClient
   ```

   This does not require you to build the mod before, 
   so you can use this for testing and debugging.

---

## Contributing

Contributions are welcome! If you'd like to contribute to the **Glowing Eyes** mod, please follow these steps:

1. **Fork the repository** and clone it to your machine.
2. **Make your changes** on a new branch.
3. **Submit a pull request (PR)** with a clear description of your changes and the reasoning behind them.

If you have any questions, bugs, or suggestions, feel free to open an issue.

---

## Credits

- **Olivo**: A huge thank you for helping with the editor GUI screen! [GitHub - Olivo](https://github.com/Olivoz)
- **Vampirism Mod**: Thanks to the [Vampirism mod](https://github.com/TeamLapen/Vampirism) and its authors and contributors for the idea, inspiration, and some code used in this mod.
- **Modded Minecraft Discord**: A massive thank you to the [Modded Minecraft Discord Server](https://discord.gg/moddedmc) for their help with coding and troubleshooting!