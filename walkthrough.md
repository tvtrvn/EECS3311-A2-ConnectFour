# A2 Connect Four (EECS 3311) — Complete Codebase Walkthrough

A comprehensive guide for anyone (graders, classmates, or future-you) who wants to understand every part of this Connect Four implementation: what it does, how it works, which design patterns power which feature, and what every term means.

---

## 1. Introduction

This is **Connect Four** built in Java + JavaFX, submitted as **Assignment 2** for EECS 3311 (Software Design) at York University. Two players take turns dropping tokens into a 6-row × 7-column grid; gravity pulls each token to the lowest open cell of the chosen column. The first player to line up four tokens — horizontally, vertically, or diagonally — wins. A full board with no four-in-a-row ends in a draw.

The repository started life as a **third-pass starter skeleton** (see `README.md`) with `TODO A2-*` markers throughout the code. Every `TODO` has been resolved (each is annotated `// TODO A2-N: done`), so what is here now is the finished assignment.

The assignment was structured around three architectural themes, and the codebase exercises each one explicitly:

| Theme | Why it is here | Where to look |
|---|---|---|
| **MVC** | Separates game data, rendering, and user input. | `model/` vs `viewcontroller/` packages, `util/Observable.java` |
| **Strategy** | AI opponents must be swappable at runtime. | `model/Player.java` + `PlayerHuman/Random/Greedy/Defensive` |
| **Command** | Undo/redo must capture each action as an object. | `model/GameCommand.java`, `DropTokenCommand`, `CommandManager` |

What the player actually gets:

- A JavaFX window with a dark blue board background and a row of toolbar controls.
- **Human vs Human** by default; switch the **opponent ComboBox** to play against Random, Greedy, or Defensive AI (the AI always plays as Player 2).
- **Undo / Redo** buttons that step through the move history bidirectionally.
- **Save / Load** buttons that open a JavaFX `FileChooser` for plain-text save files.
- A status label that says who is to move, the move number, and the win/draw result.
- A drop animation that visibly slides each new token down from the top of its column.

The project ships with **JUnit tests** for the board logic and game model in `src/ca/yorku/eecs3311/connect4/test/`, plus a small standalone MVC counter example in `mvcexample/` that is unrelated to Connect Four — it is a teaching exhibit for the same pattern, included by the course.

---

## 2. High-Level Architecture

The architecture is a textbook **Model–View–Controller** triangle, glued together with the **Observer** pattern.

```
+----------------------------------------------------+
|                 ConnectFourApplication             |
|              (composition root / wiring)           |
+----+-----------------------+-----------------------+
     |                       |                       |
     | creates               | creates               | creates
     v                       v                       v
+----------+         +-------------+         +-------------------+
|  Model   |<-Observer- BoardView |         | DropTokenEventHandler |
| (Connect |  attach  | StatusView|<--read--+      (Controller)     |
|  Four)   |---------->          |         |     uses Player        |
+----+-----+         +-------------+         +-----+--------------+
     ^                                             |
     | execute / undo / redo                       | strategy
     |                                             v
+----+--------------+                       +-------------+
| CommandManager    |                       | OpponentFactory |
| (Invoker, stacks) |---creates--> Command->|  → PlayerXxx    |
+-------------------+   DropToken           +-------------+
```

**One user click → full data-flow lifecycle:**

1. User clicks a cell in column 3. The `CellView`'s `setOnAction(boardHandler)` routes the event to `DropTokenEventHandler.handle()`.
2. The handler constructs a `DropTokenCommand(game, 3)` and passes it to `CommandManager.execute(cmd)`.
3. `DropTokenCommand.execute()` first calls `game.copy()` to snapshot the entire model, then calls `game.move(3)`.
4. `ConnectFour.move(3)` validates the move, drops the token via `ConnectFourBoard.drop`, increments `numMoves`, swaps `whosTurn`, and ends with `notifyObservers()`.
5. `notifyObservers()` calls `update(this)` on every attached `Observer`: `BoardView` repaints all 42 circles and runs the drop `TranslateTransition`; `StatusView` updates its label.
6. If an AI is set, the controller now asks `opponent.getMove(game)` and builds a second `DropTokenCommand` for the AI's move — which goes through the same cycle.

The Model never imports JavaFX. The View never modifies the Model. The Controller is the only class that calls mutating model methods, and even then only through Command objects.

---

## 3. Tech Stack at a Glance

| Layer | Technology | Purpose |
|---|---|---|
| Language | Java (compiled to a modular JRE) | Game logic, AI, GUI |
| GUI toolkit | **JavaFX 21.0.10** | Window, `GridPane`, buttons, animations, `FileChooser` |
| Build system | **Eclipse JDT** (no Maven/Gradle) | `.classpath` + `.project` files |
| Test framework | **JUnit** (4-style annotations under the JUnit 5 container) | Board + game model tests |
| Persistence | Plain text via `java.io.PrintWriter` / `BufferedReader` | Save / load game state |
| Concurrency | Single JavaFX Application Thread | All updates happen on the JavaFX thread; no manual threading |

JavaFX is supplied as a folder of JARs and native dylibs under `lib/`; the `.classpath` puts them on the module path. There are no external libraries on top of JavaFX and JUnit — everything else is the JDK.

---

## 4. Project Layout

```
A2/
├── .classpath                 # Eclipse JDT: src/, bin/, JRE, JUnit, JavaFX
├── .project                   # Eclipse project metadata
├── .gitignore                 # Ignores bin/, .DS_Store
├── index.html                 # Student-facing assignment handout
├── MARKING_RUBRIC.md          # Grading rubric (criteria + point values)
├── README.md                  # Starter readme + VM-arg hint
├── TODO_GUIDE.md              # Maps `TODO A2-*` markers to rubric items
├── summary.md                 # Existing project-wide summary (companion doc)
├── lib/                       # JavaFX 21 runtime: JARs + native dylibs
└── src/
    └── ca/yorku/eecs3311/
        ├── util/
        │   ├── Observable.java                       # Subject side of Observer
        │   └── Observer.java                         # Observer interface
        ├── mvcexample/                               # Standalone MVC demo
        │   ├── MCounter.java
        │   ├── VCount.java
        │   ├── VParity.java
        │   ├── CButtonPressEventHandler.java
        │   └── MVCApplication.java
        └── connect4/
            ├── model/                                # NO JavaFX imports
            │   ├── ConnectFourBoard.java             # 6×7 grid + win detection
            │   ├── ConnectFour.java                  # Game model (Observable)
            │   ├── Move.java                         # Immutable value object
            │   ├── Player.java                       # Strategy interface
            │   ├── PlayerHuman.java                  # Returns null
            │   ├── PlayerRandom.java                 # Random legal col + helpers
            │   ├── PlayerGreedy.java                 # Win/Block/Heuristic
            │   ├── PlayerDefensive.java              # Block/Win/Random
            │   ├── OpponentFactory.java              # String → Player
            │   ├── GameCommand.java                  # Command interface
            │   ├── DropTokenCommand.java             # Snapshot-based command
            │   └── CommandManager.java               # Undo/redo invoker
            ├── viewcontroller/                       # JavaFX layer
            │   ├── ConnectFourApplication.java       # JavaFX Application + wiring
            │   ├── BoardView.java                    # 6×7 grid of CellViews
            │   ├── CellView.java                     # Button + Circle
            │   ├── StatusView.java                   # Status label (Observer)
            │   ├── ControlPanel.java                 # Toolbar with widgets
            │   └── DropTokenEventHandler.java        # Main controller
            └── test/
                ├── ConnectFourBoardTest.java         # Unit tests
                └── ConnectFourTest.java              # Integration tests
```

**Why two packages under `connect4`?** Splitting `model` from `viewcontroller` is what enforces MVC at the compile-time level: a misplaced JavaFX import in `model/` would jump out instantly.

---

## 5. How to Run Locally

### Prerequisites

- A modular **JDK 17+** (any version Eclipse can pair with the modular JRE; JDK 21 matches the bundled JavaFX 21).
- **Eclipse IDE for Java Developers** (this project uses Eclipse's `.project`/`.classpath` build files rather than Maven/Gradle).
- JavaFX 21 — already vendored in `lib/`, so no separate install is needed.

### Open in Eclipse

1. `File → Import → General → Existing Projects into Workspace`.
2. Pick the `A2/` directory. Eclipse reads `.classpath` and sets up the module path.
3. Refresh the project (`F5`) and confirm there are no build errors. If JavaFX shows red squiggles, see Troubleshooting.

### Run

1. Right-click `ca.yorku.eecs3311.connect4.viewcontroller.ConnectFourApplication` → `Run As → Java Application`.
2. **If you get a JavaFX runtime error**, open `Run → Run Configurations`, select the launch you just created, switch to the **Arguments** tab, and paste this into **VM arguments**:
   ```
   --module-path "/usr/share/openjfx/lib" --add-modules javafx.controls,javafx.fxml
   ```
   On macOS, replace `/usr/share/openjfx/lib` with the absolute path to this project's `lib/` directory (e.g., `/Users/<you>/Desktop/WORKSPACE/A2/lib`).
3. macOS only: untick `-XstartOnFirstThread` if Eclipse adds it (JavaFX manages its own UI thread).
4. The window opens. Click a cell to drop a token.

### Run the tests

In Eclipse: right-click `ConnectFourBoardTest.java` or `ConnectFourTest.java` → `Run As → JUnit Test`. The test classes use `@Test` annotations and `Assert.*` assertions; they exercise only the model layer, so no display server / JavaFX initialization is required.

### Use the app

| Control | Effect |
|---|---|
| Click any cell in column N | Drop your token in column N (gravity decides the row) |
| **Opponent ComboBox** | Choose Human / Random / Greedy / Defensive — applied to Player 2 |
| **Restart** | Clears the board, resets turn to X, wipes undo/redo history |
| **Undo** | Restores the snapshot saved by the most recent move |
| **Redo** | Re-executes a previously undone move |
| **Save** | Opens a `FileChooser`, writes whose-turn, move count, and the board to a `.txt` file |
| **Load** | Opens a `FileChooser`, replaces the current state with the file's contents, clears history |

---

## 6. How to Submit / Hand In

This is coursework, not a deployable service. Typical submission flow:

1. Confirm every `TODO A2-*` marker says `done`. Run `git grep -n "TODO A2-"` from the project root.
2. Run both JUnit test classes; both should pass.
3. Run the JavaFX app and exercise every requirement on the rubric (`MARKING_RUBRIC.md`) at least once — drop tokens, undo, redo, switch AI, save, load, restart.
4. Make sure `bin/` is empty (it is gitignored) and `lib/` is intact (graders expect to find the JavaFX JARs locally).
5. Zip the `A2/` folder and submit to eClass, **or** push to the course's GitHub Classroom repo — whichever the instructor specified.

`TODO_GUIDE.md` cross-references each TODO marker against the rubric, so it doubles as a pre-submission checklist.

---

## 7. Code Deep-Dive

### 7.1 Utility package — `ca.yorku.eecs3311.util`

#### `Observable.java`

The subject side of the Observer pattern. Maintains a list of attached observers and calls `update(this)` on each one when `notifyObservers()` is invoked. It is a plain Java class (not the deprecated `java.util.Observable`) so the project owns its contract.

```java
public class Observable {
    private final List<Observer> observers = new ArrayList<>();
    public void attach(Observer o) { observers.add(o); }
    public void detach(Observer o) { observers.remove(o); }
    public void notifyObservers() {
        for (Observer o : observers) o.update(this);
    }
}
```

#### `Observer.java`

A one-method interface:

```java
public interface Observer {
    void update(Observable o);
}
```

`BoardView`, `StatusView`, and the `VCount`/`VParity` views in `mvcexample/` all implement this.

---

### 7.2 Model package — `ca.yorku.eecs3311.connect4.model`

The entire model layer is JavaFX-free. Anything in here can be unit-tested headless.

#### `ConnectFourBoard.java`

Pure data + rules. A `char[6][7]` array holds the board state with `EMPTY = ' '`, `P1 = 'X'`, `P2 = 'O'`. `DRAW = 'D'` is a sentinel returned by `getWinner()` when the board is full with no four-in-a-row.

Key methods:

| Method | What it does |
|---|---|
| `drop(col, player)` | Scans bottom-up for the lowest empty row and places `player` there. Returns the landing row, or `-1` if column is full or out of range. |
| `landingRow(col)` | Same scan as `drop` but read-only — used by AI strategies that want to *simulate* a drop without mutating. |
| `hasWon(player)` | Iterates every cell belonging to `player` and checks four directions: right `(0, +1)`, down `(+1, 0)`, down-right `(+1, +1)`, down-left `(+1, -1)`. Only positive directions are checked to avoid double-counting each line. |
| `getWinner()` | Returns `P1`, `P2`, `DRAW`, or `EMPTY` (game still in progress). |
| `getWinningCells()` | When there is a winner, returns the four `(row, col)` pairs forming the winning line so the view can highlight them. |
| `copy()` | Deep copy. Mutations on the copy do not affect the original — essential for snapshot-undo and AI lookahead. |
| `otherPlayer(p)` | Toggles `P1 ↔ P2`. |
| `hasMove()` | True if any column still has space. |

#### `ConnectFour.java`

Extends `Observable`. Wraps a `ConnectFourBoard` and adds:

- **Turn state.** `whosTurn` (`'X'` initially) and `numMoves`.
- **Last-move tracking.** `lastMoveRow` / `lastMoveCol` are recorded on each successful drop so `BoardView` knows which circle to animate.
- **`move(col)`.** Validates that the game isn't over and the column accepts the token; drops the token; updates last-move; switches `whosTurn` *unless* the game just ended (so the status freezes on the winner); calls `notifyObservers()`.
- **`reset()`.** Clears the board, resets turn to `P1`, zeros the move count, calls `notifyObservers()`.
- **`copy()` and `restoreFrom(other)`.** The pair used by `DropTokenCommand` for snapshot-based undo. Both copy `board`, `whosTurn`, `numMoves`, and the last-move trackers.
- **`save(filename)` / `load(filename)`.** Plain-text persistence (see Section 7.6).
- **`getStatusMessage()`.** "Player X to move" / "Player O wins" / "Draw" depending on `getWinner()`.

The class deliberately keeps every state-changing method ending in `notifyObservers()` so the views never miss an update.

#### `Move.java`

A trivial value object: a final `int col` plus `getCol()`. Returning a `Move` from `Player.getMove()` instead of a raw `int` keeps the strategy interface forward-compatible — a future strategy could attach metadata (score, confidence) without breaking callers.

#### `Player.java` — the Strategy interface

```java
public interface Player {
    Move getMove(ConnectFour game);
    String getName();
}
```

A strategy receives the live model (read-only, by convention) and returns the column it wants to play.

#### `PlayerHuman.java`

```java
public Move getMove(ConnectFour game) { return null; }
```

Returning `null` is a deliberate sentinel: the controller branches on whether the opponent's `getMove()` returned non-null to decide whether to auto-play after a human move. `PlayerHuman` exists only so that "Human" is selectable in the ComboBox alongside the AIs without special-casing the type.

#### `PlayerRandom.java`

A base AI plus the random fallback. The key helper, `legalColumns(game)`, returns a `List<Integer>` of every column with space. `getMove()` picks one of those at random.

#### `PlayerGreedy.java` (extends `PlayerRandom`)

A short-sighted attacker. Priority order:

1. **Win immediately.** Try each legal column on a `game.copy()`. If `move(col)` produces a win for the AI, play that column.
2. **Block opponent.** Try each legal column on a copy *as the opponent*. If they would win, play that column to block.
3. **Score and pick best.** For each legal column, compute a heuristic (`scoreColumn`) that combines the length of the longest consecutive line through the landing cell with a center-column preference.

The use of `ConnectFour.copy()` for the lookahead is what makes the deep-copy contract so important: a buggy `copy()` would either let the AI mutate the live game or miss its own move.

#### `PlayerDefensive.java` (extends `PlayerRandom`)

The same primitives in flipped priority:

1. **Block first** — if the opponent has a winning move, block it.
2. **Win second** — if I have a winning move, take it.
3. **Random legal column** — otherwise.

Defensive is *strictly weaker* than Greedy in a 1v1 match because it sometimes passes up an immediate win to block a non-winning threat. That's a deliberate flavor difference — the assignment wants multiple distinct strategies, not just one optimal AI.

#### `OpponentFactory.java`

Maps the ComboBox string to a concrete `Player`:

```java
public static Player create(String type, char token) {
    if ("random".equalsIgnoreCase(type))    return new PlayerRandom();
    if ("greedy".equalsIgnoreCase(type))    return new PlayerGreedy(token);
    if ("defensive".equalsIgnoreCase(type)) return new PlayerDefensive(token);
    return new PlayerHuman();
}
```

Centralizing instantiation means the controller never needs to know about concrete `PlayerXxx` classes.

#### `GameCommand.java`

```java
public interface GameCommand {
    boolean execute();
    boolean undo();
    String getName();
}
```

#### `DropTokenCommand.java`

The only concrete `GameCommand`. Snapshot-based, so reversal is trivial:

```java
public boolean execute() {
    this.snapshot = this.game.copy();   // freeze state
    return this.game.move(this.column); // make move
}

public boolean undo() {
    this.game.restoreFrom(this.snapshot);
    return true;
}
```

This pattern trades memory (one full game snapshot per move) for simplicity (no need to manually un-drop a token, un-switch turns, decrement counts, or re-evaluate the win condition). For a 6×7 board this trade-off is obviously the right call.

#### `CommandManager.java`

Holds two `Stack<GameCommand>` instances:

- `undoStack` — successfully executed commands (top of stack = most recent).
- `redoStack` — commands that have been undone.

Rules of engagement:

| Action | undoStack | redoStack |
|---|---|---|
| `execute(cmd)` returns true | push `cmd` | **cleared** (no branching history) |
| `undo()` | pop → `cmd.undo()` | push `cmd` |
| `redo()` | push back | pop → `cmd.execute()` |
| `clear()` | empty | empty |

Clearing the redo stack on a new command is what makes Connect Four match Microsoft Word: once you make a new move, the "redo" arrow stops working — because the future the redo stack used to point to no longer exists.

---

### 7.3 View/Controller package — `ca.yorku.eecs3311.connect4.viewcontroller`

This package depends on the model. The model has no reverse dependency on this package.

#### `ConnectFourApplication.java` — composition root

`extends Application`. The `start(Stage)` method does the **MVC wiring** in five phases:

1. **Construct the model.** `ConnectFour game = new ConnectFour();` and `CommandManager commandManager = new CommandManager();`.
2. **Construct the views.** `BoardView`, `StatusView`, `ControlPanel`.
3. **Construct the controller.** `DropTokenEventHandler boardHandler = new DropTokenEventHandler(game, commandManager, null);` (`null` opponent = Human vs Human).
4. **Wire view → controller.**
   - Every `CellView` calls `setOnAction(boardHandler)`.
   - Each toolbar button gets a lambda handler (`Restart`, `Undo`, `Redo`, `Save`, `Load`).
   - The opponent `ComboBox`'s `setOnAction` reads the selected value, asks `OpponentFactory.create(...)`, and calls `boardHandler.setOpponent(...)` (or `null` for Human).
   - Save and Load use `javafx.stage.FileChooser` to pick a `.txt` path before delegating to `game.save(...)` / `game.load(...)`.
5. **Wire model → view (Observer).** `game.attach(boardView)` and `game.attach(statusView)`; then a one-time `game.notifyObservers()` so the initial paint reflects the empty model.

The class also applies the dark blue color theme (`-fx-background-color: #1a1a2e;` on the root, lighter `rgb(15, 57, 96)` on the board) and sets a `minWidth/minHeight` so the window never collapses too small.

#### `BoardView.java`

A `GridPane` of 42 `CellView`s. Implements `Observer`. On `update(Observable)`:

1. **Repaint every cell.** Each cell's `Circle` fill is set based on the token at `(row, col)`: red for `P1`, yellow for `P2`, white for `EMPTY`.
2. **Highlight winning line.** If `board.getWinningCells()` returns non-null, those four cells get a lime stroke border.
3. **Animate the last drop.** If `lastMoveRow / lastMoveCol` are set, run a `javafx.animation.TranslateTransition` that starts the circle above the top of its column and slides it down to the landing row.

Exposes `getCell(row, col)` so `ConnectFourApplication` can attach the same handler to every cell.

#### `CellView.java`

A `Button` overridden to contain a `Circle` (radius 25). The button itself is what receives the click; the circle is purely visual. Each cell stores its `(row, col)` and exposes them via `getRowIndex()` / `getColIndex()` so the controller can read the column from the click source.

#### `StatusView.java`

A `Label`. `update(Observable)` reads `game.getStatusMessage()` and `game.getNumMoves()`, builds a string like `"Player O to move  |  Move #5"`, and re-styles in red when the game is over.

#### `ControlPanel.java`

An `HBox` of widgets: ComboBox (opponents), Restart, Undo, Redo, Save, Load. The class only **lays out** the widgets and exposes them via getters — it does not own any logic. `ConnectFourApplication` is the one that attaches the actual handlers, which keeps `ControlPanel` reusable.

#### `DropTokenEventHandler.java`

Implements `EventHandler<ActionEvent>`. The control flow per click:

1. **Source check.** Reject the event if its source is not a `CellView` or the game is already over.
2. **Re-entrancy guard.** A `processing` flag blocks new clicks while an AI move is in flight. (Without this, mashing the board during the AI's animation could enqueue duplicate moves.)
3. **Human move.** `new DropTokenCommand(game, cell.getColIndex())` → `commandManager.execute(cmd)`.
4. **AI response.** If `opponent != null && !game.isGameOver()`, call `opponent.getMove(game)` and execute a second `DropTokenCommand` for the returned column.
5. **Release the flag.**

`setOpponent(Player)` is the hot-swap entrypoint: changing it mid-game is allowed.

---

### 7.4 Tests — `ca.yorku.eecs3311.connect4.test`

Pure model-layer tests, no JavaFX initialization. JUnit 4 style annotations.

**`ConnectFourBoardTest`** (unit tests):

| Test | Verifies |
|---|---|
| `testDropPlacesTokenAtBottom` | Gravity: first drop lands at row 5 (bottom). |
| `testVerticalWin` | Four stacked tokens in a column = win. |
| `testHorizontalWin` | Four in a row across columns = win. |
| `testDiagonalWin` | Four-in-a-diagonal = win. |
| `testDropInFullColumnReturnsMinusOne` | Full column rejects drops. |
| `testCopyIsIndependent` | `copy()` produces a deep copy; mutations don't leak. |
| `testDrawBoardDetected` | Full board with no winner returns `'D'`. |

**`ConnectFourTest`** (integration tests):

| Test | Verifies |
|---|---|
| `testMoveAlternatesTurn` | After a successful `move`, `whosTurn` swaps. |
| `testResetClearsBoardAndState` | `reset()` returns to initial state. |
| `testInvalidColumnMoveFails` | Out-of-range or full column → `move` returns `false`. |
| `testGameOverPreventsFurtherMoves` | After a win, subsequent `move` calls are no-ops. |
| `testCommandUndoRedoRestoresGameState` | Full execute → undo → redo cycle restores correctly. |
| `testSaveLoadRoundTrip` | Save to a temp file, load into a fresh game, compare every field. |

---

### 7.5 MVC example — `ca.yorku.eecs3311.mvcexample`

A tiny standalone counter demo that illustrates the Observer/MVC dance with the bare minimum of moving parts. Not used by Connect Four; included as a course teaching artefact.

- `MCounter` — model, extends `Observable`; has `increment()` / `decrement()`.
- `VCount` — view, implements `Observer`; renders a numeric label.
- `VParity` — view, implements `Observer`; renders "EVEN" / "ODD".
- `CButtonPressEventHandler` — controller; reads button text to decide which counter method to call.
- `MVCApplication` — JavaFX `Application` wiring it all together, mirroring how `ConnectFourApplication` wires the real game.

Read this package first if MVC is unfamiliar — the moving parts there are small enough to hold in your head.

---

## 7.6 Save File Format

Plain text, 8 lines:

```
O               ← line 1: whose turn (single char)
3               ← line 2: number of moves (integer)
       
       
       
       
  X    
XOX    
```

Lines 3–8 are the six board rows, top to bottom, each exactly **7 characters** long. Empty cells are spaces; tokens are `X` or `O`. The format is intentionally human-readable so you can hand-author edge-case save files for testing (full board, near-win, etc.).

`save()` uses `PrintWriter`; `load()` uses `BufferedReader`. Both wrap the file in try-with-resources so the handle closes on exception. `load()` ends with `notifyObservers()` so the GUI repaints, and the calling code in `ConnectFourApplication` then runs `commandManager.clear()` so the undo history can't hop back into a game that no longer exists.

---

## 8. Design Patterns at a Glance

| Pattern | Role here | Key classes |
|---|---|---|
| **Observer** | Model broadcasts state changes to attached views. | `Observable`, `Observer`, `ConnectFour`, `BoardView`, `StatusView` |
| **MVC** | Model / View / Controller separation enforced at the package level. | `model/` vs `viewcontroller/`, `DropTokenEventHandler` |
| **Strategy** | Interchangeable AI behaviour for Player 2. | `Player`, `PlayerHuman/Random/Greedy/Defensive` |
| **Factory** | One place that turns a string ("Greedy") into a concrete `Player`. | `OpponentFactory` |
| **Command** | Each action is an object; undo/redo built on a stack of those objects. | `GameCommand`, `DropTokenCommand`, `CommandManager` |

The Command pattern + a snapshot in each command also means the AI's "look ahead" via `game.copy()` is *the same mechanism* used by undo — both pivot on `ConnectFour.copy()` producing a fully independent state.

---

## 9. Data Flow End-to-End

A single human-then-AI exchange:

```
User                    Controller             Command/Manager           Model
 |                          |                         |                    |
 |-- click cell @ col 3 ----|                         |                    |
 |                          |                         |                    |
 |                          |-- new DropTokenCommand(game, 3) ------------>|
 |                          |-- CM.execute(cmd) ----->|                    |
 |                          |                         |-- cmd.execute()    |
 |                          |                         |    snapshot = copy |
 |                          |                         |    game.move(3) -->|
 |                          |                         |                    |-- board.drop
 |                          |                         |                    |-- update last-move
 |                          |                         |                    |-- numMoves++
 |                          |                         |                    |-- whosTurn swap
 |                          |                         |                    |-- notifyObservers()
 |                          |                         |                    |     -> BoardView.update()
 |                          |                         |                    |        repaint + animate
 |                          |                         |                    |     -> StatusView.update()
 |                          |                         |    push to undo    |
 |                          |                         |    clear redo      |
 |                          |                         |                    |
 |                          |-- opponent != null ?    |                    |
 |                          |   yes:                  |                    |
 |                          |   move = opponent.getMove(game)              |
 |                          |     (reads game, possibly via copies)        |
 |                          |-- new DropTokenCommand(game, move.col) ------|
 |                          |-- CM.execute(cmd2) ---->| (same cycle)       |
 |                          |                         |                    |
 |   sees new board + status|                         |                    |
```

**Restart** clears the command stacks first, then resets the model. **Load** does the inverse — model first, then `commandManager.clear()` — because the loaded state has its own history we don't track.

---

## 10. Plain-English Glossary

**AI strategy:** The algorithm a non-human `Player` uses to choose a column. This project ships three (`Random`, `Greedy`, `Defensive`), each implementing the same `Player` interface.

**Block (in this codebase):** A move that occupies a column the opponent would otherwise use to win. `PlayerDefensive` prioritizes blocks above wins.

**Command:** An object that encapsulates an action so it can be executed, undone, queued, or logged. `DropTokenCommand` is the only concrete command here; `GameCommand` is the interface.

**Command stack:** The list `CommandManager` keeps of past commands. Two stacks total: `undoStack` (executed) and `redoStack` (undone).

**Composition root:** The single place that constructs and connects all the major objects in an application. Here, `ConnectFourApplication.start()`.

**Deep copy:** A copy whose internal arrays/fields are themselves duplicates, so mutating the copy never affects the original. `ConnectFourBoard.copy()` and `ConnectFour.copy()` both produce deep copies.

**FileChooser:** JavaFX's native file dialog. Used by Save/Load.

**Greedy heuristic:** A short-sighted scoring function — picks the move that looks best *right now* without simulating future opponent responses. `PlayerGreedy` uses one.

**GridPane:** A JavaFX layout that arranges its children in a row/column grid. The board view is one.

**JUnit:** A Java testing framework. The tests in `connect4/test/` use the JUnit 4 annotation style.

**Module path:** Java 9+'s replacement for the classpath for modular dependencies. JavaFX modules (`javafx.controls`, `javafx.fxml`, etc.) ship on the module path.

**MVC:** Model–View–Controller — an architectural split that keeps state, rendering, and input handling in three separate roles.

**Observable / Observer:** A pattern where one object (Observable) notifies a list of dependents (Observers) when it changes. Here, `ConnectFour` is the observable; the views are observers.

**Player (strategy):** Any class implementing `Player.getMove(ConnectFour)`. The Player 2 slot is occupied by either a `PlayerHuman` (no AI) or one of the AI strategies.

**Snapshot-based undo:** Undo implemented by saving the entire state before each change, then restoring on undo. Easier to write correctly than reversing each operation field by field.

**TranslateTransition:** A JavaFX animation that slides a node from one (x, y) to another over a duration. Used for the drop animation in `BoardView`.

**Win-line highlight:** A coloured stroke painted around the four cells that comprise the winning line. Driven by `ConnectFourBoard.getWinningCells()`.

---

## 11. Common Tasks

### Add a new AI strategy

1. Create `PlayerSomething.java` in `model/`, implementing `Player` (or extending `PlayerRandom` to inherit `legalColumns(...)` and the random fallback).
2. Add a branch to `OpponentFactory.create(...)`:
   ```java
   if ("something".equalsIgnoreCase(type)) return new PlayerSomething(token);
   ```
3. Add `"Something"` to the `ControlPanel`'s opponent `ComboBox`.

The controller, model, and views require no other changes — the Strategy + Factory patterns absorb the addition.

### Add a new control button (e.g., "Hint")

1. Add a `Button hintBtn` field and getter to `ControlPanel`; include it in the `HBox`.
2. In `ConnectFourApplication`, wire it with `controlPanel.getHintButton().setOnAction(e -> { … })`.
3. Implement the logic. If the hint reuses an AI: `Player coach = new PlayerGreedy(game.getWhosTurn()); Move m = coach.getMove(game); ` — then highlight column `m.getCol()` in the view.

### Change the win length from 4 to 5

The win detection is hard-coded to 4 in `hasLineFrom(...)` inside `ConnectFourBoard`. Search for the literal `4` (or a `WIN_LENGTH` constant) and update consistently. You may also want to grow the board (`ROWS`, `COLS`) so a 5-in-a-row is achievable.

### Change the colors

Token fills are set inside `BoardView.update(...)` — look for the `setFill(Color.RED)` / `setFill(Color.YELLOW)` lines. The background is set inline in `ConnectFourApplication.start(...)` with `-fx-background-color`.

### Add a new save-file field (e.g., timestamp)

1. Add the field to `ConnectFour`.
2. Update `save(...)` to write it on its own line.
3. Update `load(...)` to read that line, in the same order.
4. Update `copy()` / `restoreFrom()` so undo still works.
5. Bump the save format version if you care about backward compatibility (or just keep an in-code constant indicating the format).

### Add a new test

1. Create a method annotated `@Test` in either `ConnectFourBoardTest` (board-level) or `ConnectFourTest` (game-level).
2. Construct a `ConnectFourBoard` or `ConnectFour`, run the operation under test, assert with `Assert.assertEquals(...)`, `Assert.assertTrue(...)`, etc.
3. Run via `Run As → JUnit Test`.

---

## 12. Troubleshooting

**`Error: JavaFX runtime components are missing, and are required to run this application`**

The VM arguments in the launch configuration are missing or pointing at the wrong folder. Open `Run → Run Configurations → Arguments` and paste:

```
--module-path "/absolute/path/to/A2/lib" --add-modules javafx.controls,javafx.fxml
```

Use the absolute path to *this project's* `lib/` directory.

**`UnsatisfiedLinkError: no glass in java.library.path`**

JavaFX needs its native `.dylib` files (macOS) or `.dll` / `.so` files (Windows / Linux) at runtime. They live in `lib/`. Make sure your launch's module path points to that folder. On macOS, also uncheck `-XstartOnFirstThread` if Eclipse added it.

**Window is blank or controls overlap**

This usually means the JavaFX scene isn't being laid out at the expected size. The app sets `stage.setMinWidth(520); stage.setMinHeight(580);` — if you shrink below that the BorderPane will clip. Resize the window.

**Undo / Redo seem to skip moves**

Each AI response is its own command. So if you played one move and the AI responded, **one Undo** will undo only the AI's reply; another Undo will reverse your move. This is intentional — it lets you change your move without also having to re-issue the AI's.

**Restart doesn't work after a Load**

Restart calls `commandManager.clear()` first, then `game.reset()`. If you observe stale tokens after Restart, you probably have a stale `Observer` registration — verify in `ConnectFourApplication` that `game.attach(boardView)` and `game.attach(statusView)` run exactly once.

**Saved file fails to load**

Save files must be exactly 8 lines: char, integer, then six lines of 7 characters each. Editing the file by hand (especially with editors that auto-strip trailing whitespace) can clip the row width to fewer than 7 chars and break the loader. Use a "show whitespace" editor view, or re-save from the app.

**Tests fail with "JavaFX runtime missing"**

The tests in `connect4/test/` are model-only and should not require JavaFX. If you see this error, you are probably running the wrong test class or the JUnit runner is incidentally initializing a JavaFX class via static linkage — verify the test class only references `model/` types.

**`bin/` directory keeps reappearing in commits**

It is gitignored; Eclipse rebuilds it on every save. If it shows in `git status`, your `.gitignore` was not honored at the time of `git add`; remove it from the index with `git rm -r --cached bin/` and recommit.

**`TODO A2-*` markers still say "TODO"**

Any TODO that isn't appended with `// TODO A2-N: done` is unfinished. Run `git grep -nE "TODO A2-[0-9]+( *)$"` to spot the laggards. Cross-reference `TODO_GUIDE.md` to see which rubric item the missing TODO corresponds to.
