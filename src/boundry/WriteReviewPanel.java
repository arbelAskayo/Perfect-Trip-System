package boundry;

import javax.swing.*;

import control.MemberControl;
import control.ReviewControl;
import control.TripControl;
import entity.Member;
import entity.Place;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.awt.event.ActionListener;

public class WriteReviewPanel extends JPanel {
    private JComboBox<Place> comboBoxPlaces;
    private JTextArea textAreaComment;
    private JSpinner spinnerScore;
    private JButton btnSubmitReview;
    private JButton btnBack;

    public WriteReviewPanel() {
        initComponents();
    }

    private void initComponents() {

        // Components for selecting a place
        JPanel topPanel = new JPanel();
        topPanel.setBounds(178, 28, 450, 32);
        comboBoxPlaces = new JComboBox<>();
        topPanel.add(new JLabel("Select Place:"));
        topPanel.add(comboBoxPlaces);

        // Components for writing a comment
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(178, 71, 450, 369);
        textAreaComment = new JTextArea(5, 20);
        textAreaComment.setWrapStyleWord(true);
        textAreaComment.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textAreaComment);
        centerPanel.add(new JLabel("Write your comment:"), BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Components for setting a score
        JPanel scorePanel = new JPanel();
        scorePanel.setBounds(332, 455, 113, 38);
        spinnerScore = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); // Assuming scores go from 0 to 10
        spinnerScore.setBounds(64, 5, 39, 20);
        scorePanel.setLayout(null);
        JLabel label = new JLabel("Score:");
        label.setBounds(7, 8, 47, 14);
        scorePanel.add(label);
        scorePanel.add(spinnerScore);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(178, 494, 450, 33);
        btnSubmitReview = new JButton("Submit Review");
        btnBack = new JButton("Back");
        buttonPanel.add(btnSubmitReview);
        buttonPanel.add(btnBack);

        // Adding action listeners to buttons
        btnSubmitReview.addActionListener(this::submitReview);
        btnBack.addActionListener(e -> back());
        setLayout(null);

        // Adding panels to main panel
        add(topPanel);
        add(centerPanel);
        add(scorePanel);
        add(buttonPanel);
    }

    // Method to submit a review
    private void submitReview(ActionEvent e) {
        Member member = MemberControl.getInstance().getMemberById(UserSessionManager.getInstance().getCurrentUser());
        Place place = (Place) comboBoxPlaces.getSelectedItem();
        String comment = textAreaComment.getText();
        int score = (Integer) spinnerScore.getValue();

        boolean success = ReviewControl.getInstance().addReview(member, place, comment, score);
        if (success) {
            JOptionPane.showMessageDialog(this, "Review submitted successfully!");
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit review.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to clear the form
    private void clearForm() {
        comboBoxPlaces.setSelectedIndex(-1);
        textAreaComment.setText("");
        spinnerScore.setValue(0);
        revalidate();
        repaint();
    }
    
    public void refreshWriteReviewPanel() {
    	setPlaceList();
    	comboBoxPlaces.setSelectedIndex(-1);
        textAreaComment.setText("");
        spinnerScore.setValue(0);
        
        this.revalidate();
        this.repaint();
    }
    
    public void back() {
    	clearForm();
    	MainApplicationFrame.getInstance().showPlacesViewScreen();
    }

    // Method to populate the places combo box
    public void setPlaceList() {
    	Member member = MemberControl.getInstance().getMemberById(UserSessionManager.getInstance().getCurrentUser());
    	// Using a Map to ensure uniqueness based on placeId
    	
    	TripControl.getInstance().getAllTrips().values().stream()
        // Filter to include only trips where the specific member is a part of and the trip has ended
        .filter(trip -> trip.getTripMembers().containsKey(member.getMemberNumber()) && trip.getEndDate().before(new java.sql.Date(System.currentTimeMillis())))
        .forEach(trip -> {
            // For each trip that passes the filter, add its places to the comboBox
            trip.getTripPlaces().values().stream()
                .distinct() // Ensure distinct places
                .forEach(place -> comboBoxPlaces.addItem(place));
        });
    	
    }
}
