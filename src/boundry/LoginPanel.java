package boundry;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import control.MemberControl;
import control.TripControl;
import entity.Member;

public class LoginPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BUTTON_COLOR = new Color(100, 149, 237);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    
    private JTextField memberNumberField; 

    public LoginPanel(CardLayout cardLayout, JPanel cardPanel) {
        setBackground(BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 50, 5, 50);

        JLabel titleLabel = new JLabel("User Login", JLabel.CENTER);
        titleLabel.setFont(LABEL_FONT);
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(titleLabel, constraints);

        memberNumberField = new JTextField(20);
        setupTextField(memberNumberField, "Email or Member Number", constraints, 1, 0);

        JButton loginButton = createButton("Login");
        loginButton.addActionListener(e -> attemptLogin());

        constraints.gridy = 3;
        constraints.gridwidth = 1;
        add(loginButton, constraints);

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
        
        // Adjusting to use the external PlaceholderFocusListener with custom colors
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
    
    private void attemptLogin() {
        try {
            Long memberNumber = Long.parseLong(memberNumberField.getText().trim());
            
            if (MemberControl.getInstance().getAllMembers().containsKey(memberNumber)) {
                // Before logging in, ensure any previous session data is cleared and the application is reset
                
                if (memberNumber == 100) {
                    // Logic for VP of Culture
                    java.awt.EventQueue.invokeLater(() -> {
                        UserSessionManager.getInstance().login(memberNumber);
                        MainFrame.getInstance().setVisible(false);
                        MainApplicationFrame.getInstance().setVisible(true);
                        MainApplicationFrame.getInstance().showVPCultureScreen();
                    });
                } else if (memberNumber == 200) {
                    java.awt.EventQueue.invokeLater(() -> {
                        UserSessionManager.getInstance().login(memberNumber);
                        MainFrame.getInstance().setVisible(false);
                        MainApplicationFrame.getInstance().setVisible(true);
                        MainApplicationFrame.getInstance().showVPContentScreen();
                    });
                } else {
                    UserSessionManager.getInstance().login(memberNumber);
                    java.awt.EventQueue.invokeLater(() -> {
                        MainFrame.getInstance().setVisible(false);
                        MainApplicationFrame.getInstance().setVisible(true);
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Login Failed: Member not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Login Failed: Member Number must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            memberNumberField.setText(""); // Ensure the field is reset after each attempt
        }
    }
     
    
}


