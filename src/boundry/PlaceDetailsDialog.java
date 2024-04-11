package boundry;

import javax.swing.*;

import entity.Place;
import entity.Review;

import java.awt.*;
import java.util.List;

public class PlaceDetailsDialog extends JDialog {
    public PlaceDetailsDialog(Window parent, Place place, List<Review> reviews) {
        super(parent);
        setTitle("Place Details");
        setModal(true);
        setSize(400, 300); // You might adjust this size based on your content
        setLocationRelativeTo(parent);

        // Create the main content panel but don't set it as the content pane yet
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Add Place details to the content panel
        contentPanel.add(new JLabel("Name: " + place.getName()));
        contentPanel.add(new JLabel("Description: " + place.getDescription()));
        contentPanel.add(new JLabel("Price Level: " + place.getPriceLevel()));
        contentPanel.add(new JLabel("Map URL: " + place.getMapURL()));
        contentPanel.add(new JLabel("City Code: " + place.getCityCode()));
        
        // Add Reviews to the content panel
        contentPanel.add(new JLabel("Reviews:"));
        for (Review review : reviews) {
            JPanel reviewPanel = new JPanel();
            reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
            reviewPanel.add(new JLabel("Score: " + review.getScore()));
            reviewPanel.add(new JLabel("Comment: " + review.getComment()));
            contentPanel.add(reviewPanel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        }

        // Wrap the content panel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER); // Now set the scroll pane as the main content

        // Close button at the bottom
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> setVisible(false));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
