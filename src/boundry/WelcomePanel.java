package boundry;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

class WelcomePanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BUTTON_COLOR = new Color(100, 149, 237);
    private static final Font LABEL_FONT = new Font("Helvetica Neue", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    public WelcomePanel(CardLayout cardLayout, JPanel cardPanel) {
        setBackground(BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 50, 10, 50); // Top, left, bottom, right padding

        JLabel welcomeLabel = new JLabel("Welcome to Perfect Trip!", JLabel.CENTER);
        welcomeLabel.setFont(LABEL_FONT);
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(welcomeLabel, constraints);

        JButton registerButton = createButton("Register");
        constraints.gridy++;
        constraints.gridwidth = 1;
        add(registerButton, constraints);

        JButton loginButton = createButton("Login");
        constraints.gridx = 1;
        add(loginButton, constraints);

        JButton continueButton = createButton("Continue without registering");
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        add(continueButton, constraints);

        // Action Listeners for the buttons
        registerButton.addActionListener(e -> cardLayout.show(cardPanel, "Register"));
        loginButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        continueButton.addActionListener(e -> limitedAccess());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setOpaque(true);
        button.setBorderPainted(false); // Remove border to look like a modern flat button
        return button;
    }
    
    private void limitedAccess() {
    	UserSessionManager.getInstance().switchToLimitedAccess();
    	
    	 // Close MainFrame and open MainApplicationFrame
        java.awt.EventQueue.invokeLater(() -> {
        	MainFrame.getInstance().setVisible(false);
        	MainApplicationFrame.getInstance().setVisible(true);
        });
    }
}