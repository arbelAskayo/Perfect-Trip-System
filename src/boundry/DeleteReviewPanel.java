package boundry;

import javax.swing.*;

import control.ReviewControl;
import entity.Review;

import java.awt.*;
import java.awt.event.ActionEvent;

public class DeleteReviewPanel extends JPanel {
    private JComboBox<Review> comboBoxReviews;
    private JButton btnDeleteReview;
    private JButton btnCancel;

    public DeleteReviewPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Components for selecting a review to delete
        JPanel topPanel = new JPanel();
        comboBoxReviews = new JComboBox<>();
        topPanel.add(new JLabel("Select Review:"));
        topPanel.add(comboBoxReviews);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        btnDeleteReview = new JButton("Delete Review");
        btnCancel = new JButton("Cancel");
        buttonPanel.add(btnDeleteReview);
        buttonPanel.add(btnCancel);

        // Adding action listeners to buttons
        btnDeleteReview.addActionListener(this::deleteReview);
        btnCancel.addActionListener(e -> clearSelection());

        // Adding panels to main panel
        add(topPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    // Method to delete a review
    private void deleteReview(ActionEvent e) {
        Review review = (Review) comboBoxReviews.getSelectedItem();
        if (review == null) {
            JOptionPane.showMessageDialog(this, "Please select a review to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this review?", "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = ReviewControl.getInstance().deleteReview(review);
            if (success) {
                JOptionPane.showMessageDialog(this, "Review deleted successfully!");
                comboBoxReviews.removeItem(review);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete review.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to clear the combo box selection
    private void clearSelection() {
        comboBoxReviews.setSelectedIndex(-1);
    }

    // Method to populate the reviews combo box
    public void setReviewList(Review[] reviews) {
        DefaultComboBoxModel<Review> model = new DefaultComboBoxModel<>(reviews);
        comboBoxReviews.setModel(model);
    }
}
