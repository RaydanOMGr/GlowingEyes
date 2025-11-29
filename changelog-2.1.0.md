Features:
- Redesigned editor menu
- Made it possible to apply glow anywhere on the skin
- Changed double blur to darknening in popup UIs (see technical section for details)
- Optimized color picker (see technical section for details)
- Redesigned preset menu
- Improved keyboard-only navigation, some UI elements may now be interacted with without using the mouse
- Merged `/dumpeyes` and `/glowingeyesinfo` into `/eyes` subcommands (`/eyes dump` and `/eyes info`)  
- Dump now uses JSON

Compatibility:
- Implemented compatibility with [Wildfire's Female Gender Mod](https://modrinth.com/mod/female-gender)
- Implemented compatibility with [Flashback](https://modrinth.com/mod/flashback)
- Implemented partial compatibility with [ReplayMod](https://modrinth.com/mod/replaymod)
- Implemented compatibility with [Epic Fight](https://modrinth.com/mod/epic-fight) (NeoForge 1.21.1 only)
- Changed pipette logic which makes it work on PojavLauncher and forks properly (see technical section for details)

Technical:
- Cleaned the code up (moved magic numbers to constants, change some UIs calculations)
- Due to problems with applying blur more than one time or on buttons, pop up UIs now darken the screens below them instead of blurring
- The color picker now uses shaders for rendering, this significantly improves performance, makes it look good on high resolution screens and potentially decreases the RAM footprint
- Changed texture retrieval code (which is used for the pipette) to use `NativeImage#download` instead of manually calling `RenderSystem`/`GlStateManager` stuff
