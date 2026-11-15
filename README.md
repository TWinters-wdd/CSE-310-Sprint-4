# AdaptArt

AdaptArt is a Kotlin/JVM desktop drawing application built with Java Swing and AWT. The program provides a simple canvas where users can create freehand strokes and geometric shapes. The interface is organized into three toolbar sections: Brushes, Shapes, and Colors.

The project demonstrates Kotlin variables, conditionals, loops, functions, classes, data classes, collections, and `when` expressions in the context of an interactive application.

## Features

AdaptArt currently includes:

* A resizable white drawing canvas.
* Pencil, brush, and pen tools for freehand drawing.
* Line, rectangle, and oval shape tools.
* Adjustable brush and shape stroke size.
* Optional fill support for rectangles and ovals.
* A color palette with clickable color swatches.
* Icon-based brush and shape selectors.
* Tooltips that describe the available tools and colors.
* Anti-aliased drawing for smoother lines and shapes.
* Data-backed drawing storage so artwork is redrawn after the window is covered or resized.

## Project Structure

The source code is organized by responsibility rather than placing the entire application in one file:

```text
AdaptArt/src/
├── Main.kt
└── painting/
    ├── canvas/
    │   └── DrawingCanvas.kt
    ├── controller/
    │   └── DrawingController.kt
    ├── model/
    │   ├── DrawingState.kt
    │   ├── DrawingTool.kt
    │   ├── ShapeItem.kt
    │   └── Stroke.kt
    └── ui/
        ├── BrushPanel.kt
        ├── ColorPanel.kt
        ├── ColorPickerDialog.kt
        ├── MenuBar.kt
        ├── ShapePanel.kt
        ├── ToolbarIcons.kt
        └── ToolGroup.kt
```

Important classes and files include:

* `Main.kt` creates the shared drawing state, menu bar, canvas, and application window.
* `DrawingState` stores the selected tool, drawing color, stroke width, and fill setting.
* `DrawingTool` defines the available drawing tools and identifies freehand and shape tools.
* `Stroke` stores the points and visual properties of a freehand drawing.
* `ShapeItem` stores the data needed to redraw a line, rectangle, or oval.
* `DrawingCanvas` handles mouse input and renders strokes and shapes.
* `BrushPanel`, `ShapePanel`, and `ColorPanel` provide the toolbar controls.
* `ToolbarIcons` creates the icon-based controls and custom tool icons.
* `DrawingController` contains operations for changing drawing settings and clearing the canvas.

## Instructions for Build and Use

### Building and running the software

1. Open the `AdaptArt` module in IntelliJ IDEA.
2. Configure a Kotlin/JVM SDK for the project.
3. Open `AdaptArt/src/Main.kt`.
4. Run the `main` function in `Main.kt`.

The application uses the standard Kotlin/JVM runtime together with Java Swing and AWT, so no external drawing libraries are required.

### Using the application

1. Use the **Brushes** selector to choose Pencil, Brush, or Pen.
2. Use the slider beneath the brush selector to adjust stroke size.
3. Use the **Shapes** selector to choose Line, Rectangle, or Oval.
4. Select **Fill** to fill rectangles and ovals instead of drawing only their outlines.
5. Select a color from the color-swatch grid.
6. Click and drag on the white canvas to draw.
7. For freehand tools, drag across the canvas to create a stroke.
8. For shape tools, drag from one corner of the desired shape to the opposite corner.

## Kotlin Concepts Demonstrated

The application satisfies the required Kotlin programming concepts:

* **Variables:** `val` is used for values that should not be reassigned, while `var` is used for changeable drawing settings in `DrawingState`.
* **Conditionals:** `if` and `else if` determine whether mouse input should create a freehand stroke or a geometric shape.
* **Loops:** `forEach` iterates over stored strokes, shapes, and color options when drawing or creating controls.
* **Functions:** Functions such as `clearCanvas`, `paintComponent`, `drawStroke`, and `drawShape` organize reusable behavior.
* **Classes:** The application is divided into classes for the canvas, state, controls, tools, shapes, and controller logic.
* **Data classes:** `Stroke`, `ShapeItem`, and `ToolOption` store related data in a concise form.
* **Collections:** Mutable lists store strokes and shapes, while a linked map stores the color palette.
* **`when` expressions:** `when` selects the correct drawing behavior, stroke width, and tool response.

## Development Environment

To recreate the development environment, install or configure:

* Kotlin/JVM.
* Java Development Kit compatible with the Kotlin/JVM installation.
* IntelliJ IDEA with Kotlin support.
* Java Swing and AWT, including `JFrame`, `JPanel`, `JButton`, `JSlider`, `JCheckBox`, `Graphics2D`, and mouse-event classes.

The project does not currently require a third-party dependency manager or external graphics library.

## Useful Websites to Learn More

The following resources were used to learn about Kotlin, Swing, custom painting, and the design of this application:

* [Kotlin documentation](https://kotlinlang.org/docs/home.html)
* [Kotlin control flow and `when` expressions](https://kotlinlang.org/docs/control-flow.html)
* [Kotlin collections overview](https://kotlinlang.org/docs/collections-overview.html)
* [Kotlin data classes](https://kotlinlang.org/docs/data-classes.html)
* [Programiz Kotlin variables](https://www.programiz.com/kotlin-programming/variable-types)
* [Oracle Swing custom painting tutorial](https://docs.oracle.com/javase/tutorial/uiswing/painting/step2.html)
* [Kotlin Swing examples](https://zetcode.com/kotlin/swing/)
* [Freehand and shapes walkthrough](Tutorials/FreeDrawAndShapesTutorial.md)
* [This ChatGPT conversation](https://chatgpt.com/) — used as a reference for the Kotlin architecture, toolbar design, code organization, and project documentation.

## Future Work

The following items are planned improvements for AdaptArt:

* [ ] Add undo and redo using a drawing-history collection.
* [ ] Add a visible Clear Canvas control to the toolbar.
* [ ] Add PNG image export and the ability to save drawings.
* [ ] Add the custom color-picker dialog to the visible Colors section.
* [ ] Add an eraser tool that removes or overwrites existing strokes.
* [ ] Add text, polygon, arrow, and additional shape tools.
* [ ] Add keyboard shortcuts for common tools and actions.
* [ ] Connect all toolbar actions through `DrawingController`.
* [ ] Add automated tests for drawing state, tool selection, and shape calculations.
* [ ] Improve accessibility with keyboard navigation and more descriptive control labels.

## Attributions

* The custom painting approach is based on Oracle's Swing painting tutorial and was adapted for this Kotlin application.
* Kotlin and Java Swing documentation were used to organize the application and implement the toolbar controls.
* The AdaptArt source code and this README were created by OpenAI Codex in collaboration with the project author during this ChatGPT conversation.
