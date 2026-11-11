# AdaptArt

AdaptArt is a Kotlin Swing drawing application. It supports freehand brush strokes plus draggable lines, rectangles, and ovals. Drawings are stored as data and redrawn in `paintComponent`, so they remain visible after the window is covered or resized.

## Instructions for Build and Use

Steps to build and/or run the software:

1. Open the `AdaptArt` module in IntelliJ IDEA with a Kotlin/JVM SDK configured.
2. Run `AdaptArt/src/Main.kt`.
3. Use the toolbar to choose a tool, color, brush size, and whether shapes should be filled.

Instructions for using the software:

1. Choose **Freehand** and drag across the canvas to draw.
2. Choose **Line**, **Rectangle**, or **Oval**, then drag from one corner to another.
3. Choose **Clear** to remove the current drawing.

## Development Environment

To recreate the development environment, you need the following software and/or libraries with the specified versions:

* Kotlin/JVM
* Java Swing and AWT (`Graphics2D`, mouse events, and Swing controls)

## Useful Websites to Learn More

I found these websites useful in developing this software:

* [Oracle Swing Custom Painting Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/painting/step2.html)
* [Kotlin Swing tutorial examples](https://zetcode.com/kotlin/swing/)
* [Freehand and shapes walkthrough](Tutorials/FreeDrawAndShapesTutorial.md)

## Future Work

The following items I plan to fix, improve, and/or add to this project in the future:

* [x] Add freehand drawing and preset shape tools
* [x] Add a tutorial explaining Swing repainting and Kotlin implementation choices
* [ ] Add undo/redo and image export

## Attributions

* The custom painting approach is based on Oracle's Swing tutorial and has been adapted to Kotlin for this project.
