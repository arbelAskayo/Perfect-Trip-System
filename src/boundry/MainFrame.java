package boundry;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    private static MainFrame instance; // Singleton instance
    
    CardLayout cardLayout;
    JPanel cardPanel;

    // Make the constructor private to prevent instantiation from outside
    private MainFrame() {
        initializeUI();
    }

    // Public method to get the instance
    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    private void initializeUI() {
        setTitle("Perfect Trip - Welcome");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize panels here
        WelcomePanel welcomePanel = new WelcomePanel(cardLayout, cardPanel);
        RegisterPanel registerPanel = new RegisterPanel(cardLayout, cardPanel);
        LoginPanel loginPanel = new LoginPanel(cardLayout, cardPanel); // Adjusted to use getInstance()
        JPanel limitedAccessPanel = new JPanel();

        cardPanel.add(welcomePanel, "Welcome");
        cardPanel.add(registerPanel, "Register");
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(limitedAccessPanel, "Limited Access");

        add(cardPanel);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MainFrame mainFrame = MainFrame.getInstance();
            mainFrame.setVisible(true);
        });
    }
    
    public void showWelcomePanel() {
    	this.cardLayout.show(cardPanel, "Welcome");
    	
    }
}
