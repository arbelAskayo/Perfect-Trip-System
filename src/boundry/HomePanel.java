package boundry;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

class HomePanel extends JPanel implements UserStatusObserver {
    private JLabel welcomeLabel;

    public HomePanel() {
        UserSessionManager.getInstance().addObserver(this); // Subscribe to status changes
        setLayout(new BorderLayout());
        welcomeLabel = new JLabel("", JLabel.CENTER);
        add(welcomeLabel, BorderLayout.CENTER);
        updateWelcomeMessage(UserSessionManager.getInstance().isRegisteredUser()); // Initial update
    }

    @Override
    public void update(boolean isRegisteredUser) {
        updateWelcomeMessage(isRegisteredUser);
    }

    private void updateWelcomeMessage(boolean isRegisteredUser) {
        if (isRegisteredUser) {
            String userName = "User"; // Fetch the actual user name if available
            welcomeLabel.setText("Welcome back, " + userName + "!");
        } else {
            welcomeLabel.setText("Welcome! Please login or register for full access.");
        }
    }
    protected void finalize() throws Throwable {
        try {
            UserSessionManager.getInstance().removeObserver(this); // Unsubscribe from status changes
        } finally {
            super.finalize();
        }
    }
}