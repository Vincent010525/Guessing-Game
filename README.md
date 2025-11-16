# Grid Duel

A two-player network-based guessing game built with **Java** and **JavaFX**, using **TCP sockets** for communication. Each player secretly places “correct” and “wrong” nodes on their opponent’s grid, then both take turns trying to guess the correct positions. The first player to find all three correct nodes wins, while clicking the wrong node results in an instant loss.

---

## Features

- Two-player network gameplay (host and client)
- JavaFX user interface with two 5×5 grids
- Real-time communication using ServerSocket and Socket
- Placement phase followed by turn-based guessing
- Win and loss detection with end-of-game screen
- Multithreaded networking to avoid blocking the UI

---

## How the Game Works

### Connecting to a Game
- **Host a game:** Enter a port number and start the server.
- **Join a game:** Enter the host’s IP address and port number.

Once both players are connected, the placement phase begins.

### Placement Phase
Each player places hidden nodes on the opponent’s grid:

- One wrong node  
- Three correct nodes  

The joining player places their nodes first. After both players have placed their nodes, the guessing phase begins.

### Guessing Phase
Players take turns selecting nodes on their own grid:

- Selecting a correct node reveals it and moves the player closer to winning.
- Selecting the wrong node ends the game immediately with a loss.
- Selecting a normal node clears it and passes the turn.

A player wins by finding all three correct nodes.  
A player loses by selecting the wrong node.

---

## Installation

Clone the repository:

```bash
git clone https://github.com/Vincent010525/Grid-Duel.git
cd Grid-Duel/src
```

Run the program:

```bash
javac GridDuel.java
java GridDuel
```

Note: Grid-Duel uses JavaFX. JavaFX must be installed and correctly referenced on your system.

---

## Project Structure

```
/src
 ├── GridDuel.java         # Main application, UI, and game logic
 ├── Connector.java        # Networking (host, join, send, receive)
 ├── ReceiverHandler.java  # Interface for connection and message callbacks
 └── Node.java             # Representation of each grid node
```

---

## Future Improvements

- Improved UI and visual feedback
- Chat system
- Multi-round gameplay or scoring
- Configurable grid sizes

---

## Author

Vincent Bejbom
