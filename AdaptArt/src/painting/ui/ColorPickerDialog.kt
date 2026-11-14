package painting.ui

import java.awt.Color
import java.awt.Component
import javax.swing.JColorChooser

object ColorPickerDialog {
    fun chooseColor(parent: Component?, initialColor: Color): Color? =
        JColorChooser.showDialog(parent, "Choose a color", initialColor)
}
