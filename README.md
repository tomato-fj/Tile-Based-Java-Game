# Tile-Based-Java-Game



# 2D Procedural Tile-Based Adventure Game (CS 61B Project 3)

This repository contains the source code for a Java-based 2D adventure game developed as part of the CS 61B Data Structures course at UC Berkeley.

## 🧠 Project Overview

The game features:
- Procedural world generation using randomized room placement and zigzag hallway connections
- Avatar character selection with unique visuals and assets
- Real-time gameplay with keyboard input and animated graphics using StdDraw
- Dynamic AI spirits using BFS-based pathfinding and activation logic
- Deterministic save/load functionality via input history replay
- A HUD displaying tile information and gameplay status

## 🔧 Features

- **World Generation**: Rooms connected by L-shaped or zigzag corridors via MST-based logic
- **AI Movement**: Spirits track the player with BFS pathfinding, moving every few steps
- **Game Mechanics**: Collect items, avoid enemies, and reach the portal to win
- **File I/O**: Save and load game state with deterministic replays
- **Graphics**: Built on top of a custom tile engine and `StdDraw` UI toolkit

## 📁 Directory Structure

```
/core
├── Main.java         # Entry point
├── Game.java         # Main game logic
├── HUD.java          # Heads-up display
├── Spirit.java       # AI movement and behavior
├── Room.java         # Room generation
├── Position.java     # Coordinate representation
├── InitialScreen.java# Animated start screen
```

## 🛠️ Tech Stack

- Java
- StdDraw (Princeton)
- Custom tile rendering engine
- Object-Oriented Programming
- Algorithms: BFS, MST (Kruskal's), procedural generation

## 🚀 Getting Started

To compile and run the game:

```bash
javac core/*.java
java core.Main
```

Ensure that image assets and tile engine dependencies (e.g., `TERenderer`, `Tileset`) are correctly linked.

## 📚 Acknowledgments

Developed as part of the CS 61B: Data Structures course. Inspired by retro tile-based games and designed for educational use.
