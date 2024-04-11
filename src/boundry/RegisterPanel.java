package boundry;

import java.awt.CardLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Member;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import control.MemberControl;

public class RegisterPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BUTTON_COLOR = new Color(100, 149, 237);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    private JTextField memberNumberField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    
    public RegisterPanel(CardLayout cardLayout, JPanel cardPanel) {
        setBackground(BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 50, 5, 50);

        JLabel titleLabel = new JLabel("Register New Account", JLabel.CENTER);
        titleLabel.setFont(LABEL_FONT);
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(titleLabel, constraints);

        // Adding memberNumber field
        memberNumberField = new JTextField(20);
        setupTextField(memberNumberField, "Member Number (Username)", constraints, 1, 0);

        firstNameField = new JTextField(20);
        setupTextField(firstNameField, "First Name", constraints, 2, 0);

        lastNameField = new JTextField(20);
        setupTextField(lastNameField, "Last Name", constraints, 3, 0);

        emailField = new JTextField(20);
        setupTextField(emailField, "Email", constraints, 4, 0);

        JButton registerButton = createButton("Register");
        registerButton.addActionListener(e -> create());
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        add(registerButton, constraints);

        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "Welcome"));
        constraints.gridx = 1;
        add(backButton, constraints);
    }

    private void setupTextField(JTextField textField, String placeholder, GridBagConstraints constraints, int gridy, int gridx) {
        textField.setFont(LABEL_FONT);
        constraints.gridy = gridy;
        constraints.gridx = gridx;
        add(textField, constraints);
        
        textField.addFocusListener(new PlaceholderFocusListener(textField, placeholder, Color.GRAY, Color.BLACK));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }
    
    private void create() {
    	// Extract text from the text fields
        Long memberNumber = null;
        try {
            memberNumber = Long.parseLong(memberNumberField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member Number must be numeric.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();

        // Validate the extracted information (e.g., non-empty, valid email format)
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call MemberControl to create the new member
        entity.Member newMember =  MemberControl.getInstance().createMember(memberNumber, firstName, lastName, email);
        if (newMember != null) {
            JOptionPane.showMessageDialog(this, "Registration successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Member ID might be taken.", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}