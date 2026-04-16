# Connect Four — Complete Project Summary

## Table of Contents

1. [What This Project Is](#what-this-project-is)
2. [Project File Structure](#project-file-structure)
3. [Build System and Dependencies](#build-system-and-dependencies)
4. [The MVC Architecture](#the-mvc-architecture)
5. [Design Patterns Used](#design-patterns-used)
6. [Package-by-Package Breakdown](#package-by-package-breakdown)
   - [Utility Package (`ca.yorku.eecs3311.util`)](#utility-package)
   - [Model Package (`ca.yorku.eecs3311.connect4.model`)](#model-package)
   - [View/Controller Package (`ca.yorku.eecs3311.connect4.viewcontroller`)](#viewcontroller-package)
   - [Test Package (`ca.yorku.eecs3311.connect4.test`)](#test-package)
   - [MVC Example Package (`ca.yorku.eecs3311.mvcexample`)](#mvc-example-package)
7. [How a Single Move Flows Through the System](#how-a-single-move-flows-through-the-system)
8. [Game Logic Details](#game-logic-details)
9. [Undo/Redo System](#undoredo-system)
10. [AI Opponents and Strategy Swapping](#ai-opponents-and-strategy-swapping)
11. [Save and Load Persistence](#save-and-load-persistence)
12. [Testing Approach](#testing-approach)
13. [Class Dependency Map](#class-dependency-map)
14. [How to Run](#how-to-run)

---

## What This Project Is

This is a **Connect Four** game built in **Java** with a **JavaFX** graphical user interface, developed as an assignment for EECS 3311 (Software Design) at York University. The project demonstrates proper application of software engineering design patterns within an **MVC (Model-View-Controller)** architecture. Players take turns dropping tokens into a 6-row by 7-column grid; the first to get four in a row (horizontally, vertically, or diagonally) wins.

Key features:
- Human vs Human and Human vs AI gameplay
- Four AI strategies (Human, Random, Greedy, Defensive) swappable at runtime
- Full undo/redo support via the Command pattern
- Save/load game state to/from text files
- Animated token drops and win highlighting in the GUI

---

## Project File Structure

```
A2/
├── .classpath                          # Eclipse classpath (source, JRE, JUnit, JavaFX)
├── .gitignore                          # Ignores bin/, .DS_Store
├── .project                            # Eclipse project metadata
├── index.html                          # Full assignment handout
├── MARKING_RUBRIC.md                   # Grading rubric
├── README.md                           # Starter overview and VM args
├── TODO_GUIDE.md                       # TODO workflow guide
├── summary.md                          # This file
├── lib/
│   └── javafx.properties               # JavaFX 21.0.10 runtime metadata
└── src/
    └── ca/
        └── yorku/
            └── eecs3311/
                ├── util/
                │   ├── Observable.java          # Observer pattern — subject side
                │   └── Observer.java            # Observer pattern — interface
                ├── mvcexample/
                │   ├── MCounter.java            # Demo model (counter)
                │   ├── VCount.java              # Demo view (numeric display)
                │   ├── VParity.java             # Demo view (even/odd label)
                │   ├── CButtonPressEventHandler.java  # Demo controller
                │   └── MVCApplication.java      # Demo app entry point
                └── connect4/
                    ├── model/
                    │   ├── ConnectFourBoard.java     # Board state + rules (6×7 grid)
                    │   ├── ConnectFour.java           # Game model (Observable)
                    │   ├── Move.java                  # Value object for a column choice
                    │   ├── Player.java                # Strategy interface for AI
                    │   ├── PlayerHuman.java            # Human strategy (returns null)
                    │   ├── PlayerRandom.java           # Random legal column strategy
                    │   ├── PlayerGreedy.java           # Heuristic-based greedy strategy
                    │   ├── PlayerDefensive.java        # Block-first defensive strategy
                    │   ├── OpponentFactory.java        # Factory for Player instances
                    │   ├── GameCommand.java            # Command interface (undo/redo)
                    │   ├── DropTokenCommand.java       # Concrete command for dropping tokens
                    │   └── CommandManager.java         # Invoker with undo/redo stacks
                    ├── viewcontroller/
                    │   ├── ConnectFourApplication.java # JavaFX entry point, wires MVC
                    │   ├── BoardView.java              # Grid view (Observer)
                    │   ├── CellView.java               # Single cell (Button + Circle)
                    │   ├── StatusView.java             # Status label (Observer)
                    │   ├── ControlPanel.java           # Toolbar (buttons, combo box)
                    │   └── DropTokenEventHandler.java  # Primary controller
                    └── test/
                        ├── ConnectFourBoardTest.java   # Unit tests for board logic
                        └── ConnectFourTest.java        # Integration tests for game model
```

---

## Build System and Dependencies

This is an **Eclipse JDT** project (no Maven or Gradle). All configuration lives in Eclipse metadata files:

- **`.project`** — Project name `A2Fall2025_Connect4FX`, uses the JDT Java builder.
- **`.classpath`** — Defines:
  - **Source folder:** `src`
  - **Output folder:** `bin`
  - **JRE:** Modular Java runtime
  - **JUnit 5** container (test classes use JUnit 4 annotations, which Eclipse resolves)
  - **JavaFX JARs** on the module path: `javafx-base`, `javafx-controls`, `javafx-graphics`, `javafx-fxml`, etc. from `lib/`
- **JavaFX version:** 21.0.10 (per `lib/javafx.properties`)

---

## The MVC Architecture

MVC separates an application into three interconnected roles so that the internal representation of data is decoupled from the way it is presented or interacted with.

### Model — "What the data is"

The Model contains all game state and rules. It knows nothing about buttons, colours, or JavaFX. It is completely self-contained and could be used with a console UI, a web frontend, or no UI at all.

**Classes:** `ConnectFour`, `ConnectFourBoard`, `Move`, `Player` (and subclasses), `OpponentFactory`, `GameCommand`, `DropTokenCommand`, `CommandManager`

Key responsibilities:
- Store the 6×7 grid of tokens
- Enforce game rules (turn order, gravity drops, win/draw detection)
- Provide undo/redo through the Command pattern
- Save and load game state to files
- Notify attached views when state changes (via the Observer pattern)

### View — "What the user sees"

The View renders the model's state visually and updates itself automatically when the model changes. Views implement the `Observer` interface and are attached to the `ConnectFour` model. They never modify the model directly.

**Classes:** `BoardView`, `CellView`, `StatusView`, `ControlPanel`

Key responsibilities:
- Display the board as a grid of coloured circles
- Show status text (whose turn, winner, move count)
- Provide buttons/controls for user interaction (but not handle the logic)
- Animate token drops and highlight winning cells

### Controller — "How the user interacts"

The Controller translates user actions (clicks, button presses) into model operations. It sits between the View and Model, receiving events from the View and calling methods on the Model.

**Classes:** `DropTokenEventHandler`, inline lambda handlers in `ConnectFourApplication`

Key responsibilities:
- Map cell clicks to `DropTokenCommand` executions
- Trigger AI moves after human moves
- Handle restart, undo, redo, save, load, and opponent switching

### The Cycle

```
User clicks a cell
       │
       ▼
  Controller (DropTokenEventHandler)
  ─── creates DropTokenCommand ───▶ CommandManager.execute()
                                          │
                                          ▼
                                  Model (ConnectFour.move())
                                  ─── updates board, turn, etc.
                                  ─── calls notifyObservers()
                                          │
                                          ▼
                                  Views (BoardView.update(), StatusView.update())
                                  ─── read model state
                                  ─── repaint circles, update label
                                          │
                                          ▼
                                    User sees the new state
```

The key insight: **the Model never references any View or Controller class.** It only knows about `Observable` and `Observer` (generic interfaces). This means you could swap the entire GUI without changing a single line of model code.

---

## Design Patterns Used

### 1. Observer Pattern

**Problem:** When the model changes, all views need to update — but the model shouldn't have to know about specific view classes.

**Solution:** `Observable` (subject) maintains a list of `Observer` objects. When state changes, it calls `notifyObservers()`, which iterates through the list and calls each observer's `update(Observable)` method.

- **Subject:** `ConnectFour` extends `Observable`
- **Observers:** `BoardView` and `StatusView` implement `Observer`
- **Registration:** `game.attach(boardView)` and `game.attach(statusView)` in `ConnectFourApplication`
- **Trigger points:** `ConnectFour.move()`, `reset()`, `restoreFrom()`, and `load()` all end with `notifyObservers()`

### 2. Strategy Pattern

**Problem:** Different AI behaviours (random, greedy, defensive) need to be interchangeable at runtime without modifying the controller.

**Solution:** The `Player` interface defines a single method `getMove(ConnectFour)`. Each concrete strategy implements its own algorithm. The controller holds a `Player` reference and calls `getMove()` without knowing or caring which strategy it is.

- **Interface:** `Player` with `getMove()` and `getName()`
- **Concrete strategies:** `PlayerHuman`, `PlayerRandom`, `PlayerGreedy`, `PlayerDefensive`
- **Context:** `DropTokenEventHandler` holds a `Player opponent` field that can be swapped via `setOpponent()`

### 3. Factory Pattern

**Problem:** Creating the correct `Player` subclass from a string name (selected in a combo box) would require a chain of `if/else` statements scattered across UI code.

**Solution:** `OpponentFactory.create(String type, char token)` centralises the instantiation logic. The UI code just calls `OpponentFactory.create(selected, P2)`.

### 4. Command Pattern

**Problem:** Supporting undo/redo requires encapsulating each action as an object that can be executed, undone, and stored in a history.

**Solution:**
- **Command interface:** `GameCommand` with `execute()`, `undo()`, `getName()`
- **Concrete command:** `DropTokenCommand` — saves a full snapshot of the game before executing, restores it on undo
- **Invoker:** `CommandManager` — maintains `undoStack` and `redoStack`, handles the undo/redo lifecycle

### 5. MVC (Architectural Pattern)

Described in detail in the [MVC Architecture section](#the-mvc-architecture) above.

---

## Package-by-Package Breakdown

### Utility Package

**`ca.yorku.eecs3311.util`** — Reusable Observer pattern infrastructure, independent of Connect Four.

#### `Observable.java`
The subject side of the Observer pattern. Any class extending this can register observers and broadcast changes.

```java
public class Observable {
    private ArrayList<Observer> observers = new ArrayList<>();
    public void attach(Observer o)    { observers.add(o); }
    public void detach(Observer o)    { observers.remove(o); }
    public void notifyObservers()     { for (Observer o : observers) o.update(this); }
}
```

#### `Observer.java`
A single-method interface that views implement to receive notifications.

```java
public interface Observer {
    void update(Observable o);
}
```

---

### Model Package

**`ca.yorku.eecs3311.connect4.model`** — All game logic, state, AI, and command infrastructure. No JavaFX imports anywhere in this package.

#### `ConnectFourBoard.java` — The board and rules engine

A pure data class representing the 6×7 grid. Handles:

- **Constants:** `EMPTY = ' '`, `P1 = 'X'`, `P2 = 'O'`, `DRAW = 'D'`, `ROWS = 6`, `COLS = 7`
- **`drop(col, player)`** — Simulates gravity: finds the lowest empty row in the column and places the token. Returns the landing row, or -1 if the column is full.
- **`hasWon(player)`** — Scans every cell in all four directions (horizontal, vertical, both diagonals) for four consecutive matching tokens.
- **`getWinner()`** — Returns `P1`, `P2`, `DRAW`, or `EMPTY` (game still in progress).
- **`getWinningCells()`** — Returns the coordinates of the four winning cells for UI highlighting.
- **`copy()`** — Deep copy for snapshot-based undo and AI simulations.
- **`landingRow(col)`** — Preview where a token would land without placing it.
- **`columnHasSpace(col)`** — Check if a column accepts more tokens.

#### `ConnectFour.java` — The game model (Observable)

The main model class. Wraps `ConnectFourBoard` and adds:

- **Turn management:** Tracks whose turn it is (`whosTurn`), alternates after each legal move, freezes when the game ends.
- **Move counting:** `numMoves` increments with each successful move.
- **Last move tracking:** `lastMoveRow`/`lastMoveCol` record where the most recent token landed (used for drop animation).
- **`move(col)`** — The primary action: validates, drops the token, increments count, switches turns, and calls `notifyObservers()`.
- **`reset()`** — Clears everything back to the initial state and notifies observers.
- **`copy()` / `restoreFrom(other)`** — Deep copy and full state restoration (used by the Command pattern for undo).
- **`save(filename)` / `load(filename)`** — Plain-text file persistence.
- **`getStatusMessage()`** — Returns a human-readable string like "Player X to move" or "Player O wins".

#### `Move.java` — Value object

An immutable wrapper around a column index. Returned by `Player.getMove()` so that AI strategies communicate their choice in a structured way rather than a raw `int`.

#### `Player.java` — Strategy interface

```java
public interface Player {
    Move getMove(ConnectFour game);
    String getName();
}
```

Each implementation encapsulates a different algorithm for choosing a column.

#### `PlayerHuman.java`

Returns `null` from `getMove()`. Human moves come from the GUI (the controller), not from this class. It exists to make the strategy abstraction consistent — every player type, including human, is a `Player`.

#### `PlayerRandom.java`

Collects all legal (non-full) columns and picks one at random. Also serves as the base class for smarter strategies, providing the `legalColumns()` helper method and a random fallback.

#### `PlayerGreedy.java` (extends `PlayerRandom`)

A heuristic AI with this priority:
1. **Win immediately** if any column produces a four-in-a-row.
2. **Block the opponent** if they would win next turn.
3. **Score all columns** using a heuristic (longest consecutive line through the dropped position + centre-column preference) and pick the best.

Uses `ConnectFour.copy()` to simulate moves on a temporary copy without mutating the live game.

#### `PlayerDefensive.java` (extends `PlayerRandom`)

A cautious AI with reversed priorities:
1. **Block first** — if the opponent has a winning move, block it.
2. **Win second** — if this AI has a winning move, take it.
3. **Random fallback** — otherwise, play a random legal column.

#### `OpponentFactory.java`

```java
public static Player create(String type, char token) {
    if ("random".equalsIgnoreCase(type))    return new PlayerRandom();
    if ("greedy".equalsIgnoreCase(type))    return new PlayerGreedy(token);
    if ("defensive".equalsIgnoreCase(type)) return new PlayerDefensive(token);
    return new PlayerHuman();  // default
}
```

Called from the GUI's combo box handler. The UI passes a string like `"Greedy"` and gets back the right `Player` instance.

#### `GameCommand.java` — Command interface

```java
public interface GameCommand {
    boolean execute();
    boolean undo();
    String getName();
}
```

#### `DropTokenCommand.java` — Concrete command

Before executing, it saves a **full snapshot** of the entire `ConnectFour` state via `game.copy()`. On undo, it restores that snapshot via `game.restoreFrom(snapshot)`. This snapshot approach is simpler and more reliable than trying to manually reverse individual fields.

```java
public boolean execute() {
    this.snapshot = this.game.copy();     // save state
    return this.game.move(this.column);   // make the move
}

public boolean undo() {
    this.game.restoreFrom(this.snapshot); // restore saved state
    return true;
}
```

#### `CommandManager.java` — Command invoker

Maintains two `Stack<GameCommand>` objects:
- **`undoStack`** — successfully executed commands, most recent on top
- **`redoStack`** — undone commands waiting to be re-executed

Rules:
- `execute(cmd)`: runs the command, pushes to undo stack, **clears the redo stack** (no branching history).
- `undo()`: pops from undo stack, calls `cmd.undo()`, pushes to redo stack.
- `redo()`: pops from redo stack, calls `cmd.execute()`, pushes back to undo stack.
- `clear()`: empties both stacks (called on restart or load).

---

### View/Controller Package

**`ca.yorku.eecs3311.connect4.viewcontroller`** — All JavaFX GUI code. This package depends on the model; the model never depends on this package.

#### `ConnectFourApplication.java` — The composition root

This is the **JavaFX entry point** (`extends Application`). Its `start()` method is where the entire MVC architecture is wired together:

1. **Creates the Model:** `new ConnectFour()` and `new CommandManager()`
2. **Creates the Views:** `new BoardView()`, `new StatusView()`, `new ControlPanel()`
3. **Creates the Controller:** `new DropTokenEventHandler(game, commandManager, opponent)`
4. **Hooks Views to Controller:** Every `CellView` in the board grid gets `setOnAction(boardHandler)`
5. **Hooks Model to Views:** `game.attach(boardView)` and `game.attach(statusView)`
6. **Wires toolbar buttons:** Restart, Undo, Redo, Save, Load, and opponent ComboBox — each with inline lambda handlers
7. **Builds the layout:** `BorderPane` with a `VBox` (control panel + status) on top and the board view in the centre
8. **Applies styling:** Dark blue theme with CSS inline styles

#### `BoardView.java` — The board view (Observer)

A `GridPane` containing a 6×7 grid of `CellView` buttons. Implements `Observer` so it refreshes automatically when the model changes.

In `update(Observable)`:
1. **Repaints all 42 cells:** Reads each token from the model and sets the circle fill — red for P1 (`'X'`), yellow for P2 (`'O'`), white for empty.
2. **Highlights the winning line:** If `getWinningCells()` returns non-null, those four cells get a lime-green stroke border.
3. **Plays a drop animation:** For the most recent move, uses a JavaFX `TranslateTransition` to animate the circle falling from the top of the column to its landing row.

#### `CellView.java` — A single board cell

A JavaFX `Button` containing a `Circle` graphic (radius 25px). Each cell knows its own `row` and `col` indices. When clicked, the shared `DropTokenEventHandler` reads `cell.getColIndex()` to determine which column to drop into. The circle's fill colour is controlled externally by `BoardView.update()`.

#### `StatusView.java` — The status label (Observer)

A `Label` that implements `Observer`. On each update, it reads the model's status message and move count, and changes its text colour to red when the game is over.

#### `ControlPanel.java` — The toolbar

An `HBox` containing:
- **ComboBox** — opponent selector: "Human", "Random", "Greedy", "Defensive"
- **Restart button**
- **Undo button**
- **Redo button**
- **Save button**
- **Load button**

This class only creates and lays out the widgets. It exposes each via getters so that `ConnectFourApplication` can attach the actual event handlers — maintaining MVC separation.

#### `DropTokenEventHandler.java` — The primary controller

Implements `EventHandler<ActionEvent>`. Handles all cell click events. Flow:

1. Verifies the click source is a `CellView` and the game is not over.
2. Sets a `processing` flag to block re-entrant clicks during AI turns.
3. Creates a `DropTokenCommand` with the clicked column and executes it via `CommandManager`.
4. If an AI opponent is set and the game is still in progress, calls `opponent.getMove()` and executes a second `DropTokenCommand` for the AI's move.
5. Clears the `processing` flag.

The `setOpponent(Player)` method allows hot-swapping the AI strategy at runtime when the user changes the combo box selection.

---

### Test Package

**`ca.yorku.eecs3311.connect4.test`** — JUnit test suites.

#### `ConnectFourBoardTest.java`

Unit tests for the low-level board logic:
- **`testDropPlacesTokenAtBottom`** — Gravity: token lands in bottom row of empty column
- **`testVerticalWin`** — Four stacked in one column = win
- **`testHorizontalWin`** — Four in consecutive columns = win
- **`testDiagonalWin`** — Diagonal line of four = win
- **`testDropInFullColumnReturnsMinusOne`** — Full column rejection
- **`testCopyIsIndependent`** — Deep copy does not share state with original
- **`testDrawBoardDetected`** — Full board with no winner = draw

#### `ConnectFourTest.java`

Higher-level integration tests for the game model:
- **`testMoveAlternatesTurn`** — Turn switches from P1 to P2 after a move
- **`testResetClearsBoardAndState`** — Reset restores initial state
- **`testInvalidColumnMoveFails`** — Out-of-range columns are rejected
- **`testGameOverPreventsFurtherMoves`** — No moves accepted after a win
- **`testCommandUndoRedoRestoresGameState`** — Full undo/redo cycle with `CommandManager`
- **`testSaveLoadRoundTrip`** — Save to temp file, load into new game, verify equality

---

### MVC Example Package

**`ca.yorku.eecs3311.mvcexample`** — A minimal MVC demo (separate from Connect Four) that illustrates the pattern with a simple counter.

- **`MCounter`** — Model (extends `Observable`): holds an integer count, has `increment()` and `decrement()`.
- **`VCount`** — View (implements `Observer`): a `Label` that displays the numeric count.
- **`VParity`** — View (implements `Observer`): a `Label` that displays "EVEN" or "ODD".
- **`CButtonPressEventHandler`** — Controller: reads button text to decide increment vs decrement.
- **`MVCApplication`** — Wires everything together, like `ConnectFourApplication` does for the real game.

This package exists as a teaching example and does not interact with the Connect Four game.

---

## How a Single Move Flows Through the System

Here is the complete lifecycle of a human player clicking column 3:

### Step 1: User Click → Controller

The user clicks any cell in column 3. Since all cells have `setOnAction(boardHandler)`, JavaFX fires `DropTokenEventHandler.handle(ActionEvent)`.

### Step 2: Controller → Command

The handler creates `new DropTokenCommand(game, 3)` and passes it to `commandManager.execute(cmd)`.

### Step 3: Command → Model

Inside `DropTokenCommand.execute()`:
- A full snapshot of the game is saved: `this.snapshot = this.game.copy()`
- The move is made: `this.game.move(3)`

Inside `ConnectFour.move(3)`:
- Checks the game is not over.
- Calls `board.drop(3, 'X')` which finds the lowest empty row (say row 5) and places `'X'` there.
- Updates `lastMoveRow = 5`, `lastMoveCol = 3`, increments `numMoves`.
- If the game is not over, switches `whosTurn` from `'X'` to `'O'`.
- Calls `notifyObservers()`.

### Step 4: Model → Views (Observer notification)

`notifyObservers()` iterates through all attached observers and calls `update(this)`:

**`BoardView.update()`:**
- Loops through all 42 cells, reading each token from the model and setting the corresponding circle colour (red/yellow/white).
- Checks for winning cells and highlights them if found.
- Sees that `lastMoveRow=5, lastMoveCol=3` has a non-empty token, so it plays a drop animation on that cell's circle.

**`StatusView.update()`:**
- Reads `game.getStatusMessage()` → "Player O to move"
- Reads `game.getNumMoves()` → 1
- Sets label text to: "Player O to move  |  Move #1"

### Step 5: Command History

Back in `CommandManager.execute()`, since the command succeeded, it's pushed onto the `undoStack` and the `redoStack` is cleared.

### Step 6: AI Response (if applicable)

Back in `DropTokenEventHandler.handle()`, if an AI opponent is set:
- Calls `opponent.getMove(game)` which returns a `Move` (e.g., column 4).
- Creates and executes another `DropTokenCommand(game, 4)`.
- This triggers another full cycle: snapshot → move → notify → views update.

### Step 7: Processing flag released

The `processing` flag is set to `false`, allowing the next human click.

---

## Game Logic Details

### Board Representation

The board is a `char[6][7]` array. Row 0 is the **top** row; row 5 is the **bottom** row. Column 0 is the **leftmost** column; column 6 is the rightmost.

- `' '` (space) = empty cell
- `'X'` = Player 1 token
- `'O'` = Player 2 token

### Gravity Drop

When a token is dropped into a column, it falls to the lowest available row — just like a real Connect Four board. The `landingRow(col)` method scans from the bottom up (row 5 → 0) and returns the first empty row.

### Win Detection

`hasWon(player)` scans every cell on the board. For each cell belonging to `player`, it checks four directions:
- **Horizontal** (0, +1) — rightward
- **Vertical** (+1, 0) — downward
- **Diagonal down-right** (+1, +1)
- **Diagonal down-left** (+1, -1)

For each direction, `hasLineFrom()` checks if the next 3 cells (4 total) all belong to the same player. Only "positive" directions are checked to avoid counting each line twice.

### Draw Detection

If no player has won and `hasMove()` returns `false` (all 42 cells occupied), `getWinner()` returns `'D'` (draw).

### Turn Management

`ConnectFour` starts with `whosTurn = 'X'`. After each successful move, if the game is not over, the turn switches via `otherPlayer()`. If the game *is* over (someone won or it's a draw), the turn does *not* switch — it freezes on the winning player.

---

## Undo/Redo System

The undo/redo system uses the **Command pattern** with a **snapshot-based** approach.

### Why Snapshots?

Instead of trying to reverse a move by removing the token and un-switching the turn (which is fragile and error-prone with derived state), each `DropTokenCommand` saves a **complete deep copy** of the entire `ConnectFour` model before executing. Undo simply replaces the current state with that saved copy.

### The Two Stacks

```
Execute move(3):    undoStack: [cmd1]           redoStack: []
Execute move(5):    undoStack: [cmd1, cmd2]     redoStack: []
Undo:               undoStack: [cmd1]           redoStack: [cmd2]
Undo:               undoStack: []               redoStack: [cmd2, cmd1]
Redo:               undoStack: [cmd1]           redoStack: [cmd2]
Execute move(0):    undoStack: [cmd1, cmd3]     redoStack: []  ← redo cleared!
```

A new command always clears the redo stack, preventing a branching history.

---

## AI Opponents and Strategy Swapping

### Runtime Strategy Swap

The GUI's opponent `ComboBox` contains: "Human", "Random", "Greedy", "Defensive". When the user selects a new value:

1. The combo box handler fires.
2. If "Human" is selected, `boardHandler.setOpponent(null)`.
3. Otherwise, `OpponentFactory.create(selected, 'O')` creates the appropriate AI, and `boardHandler.setOpponent(ai)` installs it.

The AI always plays as Player 2 (`'O'`). Player 1 (`'X'`) is always the human clicking the board.

### How AI Moves Work

After the human's move succeeds, `DropTokenEventHandler.maybeMakeComputerMove()`:
1. Checks if an opponent is set and the game is not over.
2. Calls `opponent.getMove(game)` — the strategy reads the model and returns a `Move`.
3. Executes a `DropTokenCommand` for the AI's chosen column (this also goes through `CommandManager`, so it's undoable).

### Strategy Comparison

| Strategy | Win Priority | Block Priority | Fallback |
|----------|-------------|----------------|----------|
| Random | — | — | Random legal column |
| Greedy | 1st (win immediately) | 2nd (block opponent) | Best heuristic score |
| Defensive | 2nd (win if no block needed) | 1st (block opponent) | Random legal column |

---

## Save and Load Persistence

### File Format

Plain text, 8 lines:

```
O               ← whose turn (char)
3               ← number of moves (int)
       
       
       
       
  X    
XOX    
```

Lines 3–8 are the board rows (7 characters each), where spaces represent empty cells.

### Save Flow

`ConnectFour.save(filename)` writes `whosTurn`, `numMoves`, and every cell of the board using a `PrintWriter`.

### Load Flow

`ConnectFour.load(filename)` reads the file back with a `BufferedReader`, sets `whosTurn`, `numMoves`, and every cell of the board, then calls `notifyObservers()` to refresh the GUI. After loading, `commandManager.clear()` is called to wipe the undo/redo history (since it no longer applies to the loaded state).

### Save/Load in the GUI

Both buttons open a `FileChooser` dialog. Save defaults to `.txt` extension. The handlers are wired as inline lambdas in `ConnectFourApplication`.

---

## Testing Approach

Tests use **JUnit 4** annotations (`@Test`, `Assert.*`). They test the model layer only — no GUI testing.

### Board Tests (`ConnectFourBoardTest`)

These are pure **unit tests** for the `ConnectFourBoard` class, verifying:
- Gravity mechanics (tokens fall to bottom)
- Win detection in all orientations (vertical, horizontal, diagonal)
- Edge cases (full column rejection, draw detection)
- Deep copy independence

### Game Model Tests (`ConnectFourTest`)

These are **integration tests** that exercise `ConnectFour` alongside `CommandManager` and `DropTokenCommand`:
- Turn alternation and reset
- Invalid move handling
- Game-over enforcement
- Full undo/redo cycle verification (snapshot correctness)
- Save/load round-trip (write to temp file, read back, compare all state)

---

## Class Dependency Map

```
ConnectFourApplication (composition root)
├── creates → ConnectFour (model)
│             ├── contains → ConnectFourBoard (board + rules)
│             └── extends → Observable (observer subject)
├── creates → CommandManager (invoker)
│             └── manages → DropTokenCommand (concrete command)
│                            ├── implements → GameCommand (interface)
│                            └── operates on → ConnectFour
├── creates → BoardView (view)
│             ├── implements → Observer
│             ├── contains → CellView[][] (cell grid)
│             └── reads from → ConnectFour
├── creates → StatusView (view)
│             ├── implements → Observer
│             └── reads from → ConnectFour
├── creates → ControlPanel (toolbar)
│             └── exposes → Buttons, ComboBox
├── creates → DropTokenEventHandler (controller)
│             ├── uses → CommandManager
│             ├── uses → DropTokenCommand
│             └── uses → Player (strategy, via OpponentFactory)
└── wires → Observer registration: game.attach(boardView), game.attach(statusView)
```

**Dependency direction:** View/Controller depends on Model. Model depends on nothing except `util` (Observable/Observer). The model package has zero JavaFX imports.

---

## How to Run

### In Eclipse

1. Import the project (`File → Import → Existing Projects into Workspace`).
2. Ensure JavaFX JARs are on the module path (check `.classpath`).
3. Add VM arguments to the run configuration:
   ```
   --module-path "/usr/share/openjfx/lib" --add-modules javafx.controls,javafx.fxml
   ```
   (On macOS with local JARs, the path may differ — check the `lib/` folder.)
4. Run `ConnectFourApplication.java` as a Java Application.
5. Uncheck `XStartOnFirstThread` on macOS if prompted.

### Playing the Game

- Click any cell in a column to drop your token there.
- Use the combo box to switch between Human, Random, Greedy, and Defensive opponents.
- Use Undo/Redo to step through move history.
- Use Save/Load to persist and restore game state.
- Use Restart to begin a new game.
