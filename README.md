# Multiplayer Guessing Game (Java)

A multiplayer guessing game implemented in Java using TCP socket programming.  
This project was developed as a final project for the **Network Programming** course (Spring 2025).

The system allows multiple players to connect to a central server, create game challenges, and participate in interactive guessing sessions in real time.

---

##  Project Overview

The game follows a **client-server architecture** where:
- One player acts as the **Challenger**
- Two players act as **Guessers**
- The Challenger selects a secret name from a category
- Guessers ask **yes/no questions** and attempt to guess the secret name

The server supports **multiple concurrent games**, with each game running independently.

---

##  Technologies Used

- Java
- TCP Socket Programming
- Multithreading
- Object Serialization
- Synchronization (`wait` / `notify`)
- Java Collections Framework

---

##  System Architecture

- **Server**
  - Listens for incoming client connections
  - Spawns a dedicated thread for each client
  - Manages all active challenges

- **Client**
  - Connects to the server
  - Allows users to create or join challenges
  - Handles game interaction and user input

---

##  Main Classes

- **Server**
  - Accepts client connections
  - Manages global game state

- **ClientThread**
  - Handles communication with a single client
  - Runs in its own thread

- **Player**
  - Represents a connected user
  - Stores nickname and socket information

- **Challenge**
  - Represents a game session
  - Manages players, turns, attempts, and win conditions

- **Questions**
  - Manages question–answer synchronization
  - Uses `wait()` and `notify()` for thread coordination

---

##  Game Features

- Multiple players connected simultaneously
- Support for multiple active challenges
- Limited or unlimited guessing attempts
- Turn-based gameplay
- Thread-safe game state management
- Graceful handling of client disconnections

---

##  How to Run

1. Compile all Java files:
   ```bash
   javac *.java
