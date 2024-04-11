package boundry;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class PlaceholderFocusListener implements FocusListener {
    private final JTextField textField;
    private final String placeholder;
    private final Color placeholderColor;
    private final Color textColor;

    public PlaceholderFocusListener(JTextField textField, String placeholder, Color placeholderColor, Color textColor) {
        this.textField = textField;
        this.placeholder = placeholder;
        this.placeholderColor = placeholderColor;
        this.textColor = textColor;
        // Set the placeholder color and text if the field is empty.
        if (textField.getText().isEmpty()) {
            textField.setForeground(placeholderColor);
            textField.setText(placeholder);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Clear the placeholder text when the text field gains focus.
        if (textField.getText().equals(placeholder)) {
            textField.setText("");
            textField.setForeground(textColor);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        // If the text field loses focus and is empty, set the placeholder text.
        if (textField.getText().isEmpty()) {
            textField.setForeground(placeholderColor);
            textField.setText(placeholder);
        }
    }
}