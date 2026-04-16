# EECS3311 A2 - Connect Four GUI in JavaFX

## Marking rubric (100 marks)

### 1. MVC architecture — 25 marks
We are looking for:
- clear separation between model, view, and controller/event-handling code
- limited coupling between classes
- model code that is not tangled with JavaFX presentation logic
- sensible class responsibilities

### 2. Strategy pattern — 15 marks
We are looking for:
- clean abstraction of different player/opponent behaviours
- GUI-based selection of player/opponent type
- easy extensibility for additional strategies

### 3. Command pattern / undo-redo — 15 marks
We are looking for:
- moves represented as commands rather than ad hoc undo code
- correct undo/redo behaviour
- command history managed in a clean way

### 4. Connect Four functionality and correctness — 20 marks
We are looking for:
- valid move handling
- turn management
- win detection
- draw detection
- restart support
- save/load that restores game state reasonably correctly

### 5. JavaFX GUI quality — 10 marks
We are looking for:
- a usable and readable GUI
- correct board updates
- clear controls and status messages
- overall polish appropriate for a course assignment

### 6. Code quality and documentation — 10 marks
We are looking for:
- meaningful names
- good decomposition into classes and methods
- useful comments and/or JavaDoc
- brief explanation of how MVC, Strategy, and Command appear in the solution

### 7. Testing — 5 marks
We are looking for:
- JUnit tests that go beyond the supplied starter
- sensible coverage of important model behaviour

## General interpretation
- **Excellent**: complete, correct, clean, and clearly pattern-based
- **Good**: mostly complete and well structured, with only minor issues
- **Satisfactory**: core features present, but design quality or pattern use is uneven
- **Weak**: significant missing features, weak organization, or incorrect behaviour
