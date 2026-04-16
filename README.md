# EECS3311 A2 starter - Connect Four (JavaFX, MVC, design patterns)

This project is the **third-pass starter skeleton** for a JavaFX/MVC Connect Four assignment.

It is based on the original A2 structure:
- `ca.yorku.eecs3311.util` preserves the observable/observer utility classes
- `ca.yorku.eecs3311.mvcexample` preserves the small JavaFX MVC sample
- `ca.yorku.eecs3311.connect4` replaces the old Othello package with Connect Four starter code

## Important
This is **not** intended to be a finished solution.
It contains explicit `TODO A2-*` markers so students can clearly see where to extend and refactor the code.

## Main JavaFX application
- `ca.yorku.eecs3311.connect4.viewcontroller.ConnectFourApplication`

## Required architectural themes
Students are expected to organize the solution around:
- **MVC** for application structure
- **Strategy** for player/opponent behaviour
- **Command** for undo/redo support

## Where students should work first
1. `viewcontroller/ConnectFourApplication.java`
2. `viewcontroller/BoardView.java`
3. `viewcontroller/DropTokenEventHandler.java`
4. `model/ConnectFour.java`
5. `model/CommandManager.java`
6. `model/OpponentFactory.java`

## Included handout and rubric
- `index.html` contains the student-facing handout
- `MARKING_RUBRIC.md` contains the rubric in plain text/markdown form
- `TODO_GUIDE.md` explains how the TODO markers align with the assignment expectations

## Running JavaFX in the lab
Use VM arguments such as:
`--module-path "/usr/share/openjfx/lib" --add-modules javafx.controls,javafx.fxml`
