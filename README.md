<div align="center">

# 🛸 DronesCraft

**An autonomous drone companion & a buildable energy grid for Minecraft**

Spawn an AI-driven drone that follows you, defends you, and recharges itself —
powered by a complete in-game electrical system you build from generators,
cables, storage units and charging stations.

<br>

![Minecraft](https://img.shields.io/badge/Minecraft-1.18.2-62B47A?style=for-the-badge&logo=minecraft&logoColor=white)
![Forge](https://img.shields.io/badge/Forge-40.3.10-1E2D42?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![GeckoLib](https://img.shields.io/badge/GeckoLib-3.0.57-6E4AA2?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-3DA639?style=for-the-badge)

</div>

---

## Overview

**DronesCraft** turns the humble drone into a real companion. It is a Minecraft
Forge mod built around two connected systems: an **autonomous flying drone** with
its own AI, battery and combat behaviour, and a **modular energy grid** that keeps
that drone in the air. Build the power infrastructure, deploy your drone, and let
it patrol at your side.

---

## ✨ Features

### 🤖 Autonomous drone
- Custom flying entity (`PathfinderMob` + `FlyingPathNavigation`) driven by its own AI goals.
- **Follows its owner** automatically — and **teleports back** if it ever falls more than 64 blocks behind.
- **Combat ready** — locks onto hostile mobs, fires projectiles, and respects an attack cooldown.
- **Battery system** — ~20 minutes of flight time, a low-battery warning at 15%, and an automatic return-to-charge behaviour.
- Bound to its owner by UUID and **persisted across world reloads** via NBT.

### ⚡ Energy grid
A small but complete power network, modelled after real tech mods:

| Block | Role |
|-------|------|
| **Generator** | Burns fuel to produce energy |
| **Energy Cable** | Transmits power between blocks with auto-connecting models |
| **Energy Storage** | Buffers energy, with a capacity GUI |
| **Charging Station** | Recharges a docked drone |
| **Drone Block** | Deployable drone dock / spawn point |

### 🎛️ Items & control
- **Drone** — deploy your personal drone into the world.
- **Remote Controller** — open a GUI to monitor battery, toggle flight, and command the drone (lift / land) over networked packets.
- Custom **key bindings** for quick, in-flight control.

### 🎨 Presentation
- **GeckoLib** animated drone model (idle / flight / combat animations).
- Hand-made block, item and GUI textures, with active/inactive states for powered machines.
- Full custom **sound set** — lift-off, charging, low battery, shooting, target spotted, generator hum.
- **Bilingual** — English (`en_us`) and Russian (`ru_ru`) localization.

---

## 🎮 Gameplay loop

1. Craft a **Generator** and feed it fuel to produce energy.
2. Run **Energy Cables** to an **Energy Storage** and a **Charging Station**.
3. Deploy your **Drone** and pair it with the **Remote Controller**.
4. The drone follows and protects you — and when its battery runs low it heads back to the **Charging Station**, tops up, and returns to the skies.

---

## 📥 Installation

1. Install **Minecraft Forge `1.18.2 - 40.3.10`**.
2. Download the **GeckoLib `3.0.57`** (Forge 1.18) dependency and place it in your `mods/` folder.
3. Download the latest **DronesCraft** jar from the [Releases](https://github.com/NickStS/DronesCraft/releases) page and drop it in `mods/`.
4. Launch the game and create a world — the **Basic Drone** creative tab holds everything.

---

## 🔧 Build from source

Requires **JDK 17**.

```bash
git clone https://github.com/NickStS/DronesCraft.git
cd DronesCraft

# Windows
gradlew.bat build

# Linux / macOS
./gradlew build
```

The compiled jar lands in `build/libs/`. To launch a development client, run
`gradlew runClient`.

---

## 🗂️ Project structure

```
src/main/java/com/example/basicdrone/
├── block/            # Generator, Cable, EnergyStorage, ChargingStation, DroneBlock
│   └── entity/       # block entities (energy logic, ticking) + GeckoLib block renderer
├── entity/           # DroneEntity (AI, battery, combat), BulletEntity
│   └── client/       # GeckoLib model + renderer
├── item/             # DroneItem, RemoteControllerItem, animated item rendering
├── menu/             # container menus (Generator, EnergyStorage, RemoteController)
├── screen/ + client/gui/   # GUI screens
├── network/          # client/server packets (toggle flight, lift, land, status)
├── server/           # DroneManager / DroneKeeper — server-side drone registry
├── client/event/     # client setup, key bindings, render events
├── Registries.java   # deferred registers (blocks, items, entities, menus, sounds)
└── BasicDroneMod.java # mod entry point

src/main/resources/
├── assets/basicdrone/   # models, blockstates, textures, animations, sounds, lang
└── data/basicdrone/     # recipes, loot tables
```

---

## 🧰 Tech stack

| | |
|---|---|
| **Language** | Java 17 |
| **Platform** | Minecraft Forge 40.3.10 (MC 1.18.2) |
| **Animations** | GeckoLib 3.0.57 |
| **Build** | Gradle (ForgeGradle) |

---

## 📜 License

Released under the **[MIT License](LICENSE)**.

---

<div align="center">

### 👤 Author

**danger_videograph**

[![GitHub](https://img.shields.io/badge/GitHub-NickStS-181717?style=flat-square&logo=github)](https://github.com/NickStS)
[![Instagram](https://img.shields.io/badge/Instagram-danger__videograph-E4405F?style=flat-square&logo=instagram&logoColor=white)](https://www.instagram.com/danger_videograph/)

<sub>Built with Forge & GeckoLib · Minecraft 1.18.2</sub>

</div>
