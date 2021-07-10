# JCHIP8

MIT Licensed CHIP8 Emulator written in Java.

JCHIP8 works by reading the raw ROM files and executing the opcodes found in the ROM. As of current JCHIP8 supports all of the known opcodes for the system.

## Overview of directory structure:

- src
    - tech
        - raidtheweb
            - jchip8
                - emu (emulator files, includes main and display code)
                    - ChipFrame.java (frame data for JFrame)
                    - ChipPanel.java (custom JPanel)
                    - Main.java (emulator interface thread)
                - chip (core files, includes Virtual CHIP8 and fontset data)
                    - Chip.java (core Chip)
                    - ChipData.java (fontset)

- pong2.c8
- invaders.c8
- README.md
- .gitignore
- .classpath
- .project