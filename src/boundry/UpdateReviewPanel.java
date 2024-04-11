package boundry;

import javax.swing.*;
import control.ReviewControl;
import entity.Member;
import entity.Review;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.awt.event.ActionListener;

public class UpdateReviewPanel extends JPanel {
    private JList<Review> listReviews;
    private JTextArea textAreaComment;
    private JSpinner spinnerScore;
    private JButton btnUpdateReview;
    private JButton btnDelete;

    // Member for whom the reviews are being updated
    private Member currentMember;
    private JButton btnBack;

    public UpdateReviewPanel(Member member) {
        this.currentMember = member;
        initComponents();
        loadMemberReviews();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        listReviews = new JList<>();
        listReviews.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(listReviews), BorderLayout.WEST);

        textAreaComment = new JTextArea(5, 20);
        textAreaComment.setWrapStyleWord(true);
        textAreaComment.setLineWrap(true);
        add(new JScrollPane(textAreaComment), BorderLayout.CENTER);

        spinnerScore = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        
        btnUpdateReview = new JButton("Update Review");
        btnUpdateReview.addActionListener(this::updateReview);

        btnDelete = new JButton("Delete Review");
        
        btnDelete.addActionListener(e -> deleteReview());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Score:"));
        bottomPanel.add(spinnerScore);
        bottomPanel.add(btnUpdateReview);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
        
        btnBack = new JButton("Back");
        btnBack.addActionListener(e -> back());
        bottomPanel.add(btnBack);

        // Add list selection listener
        listReviews.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listReviews.getSelectedValue() != null) {
                Review selectedReview = listReviews.getSelectedValue();
                textAreaComment.setText(selectedReview.getComment());
                spinnerScore.setValue(selectedReview.getScore());
            }
        });
    }

    private void updateReview(ActionEvent e) {
        Review selectedReview = listReviews.getSelectedValue();
        if (selectedReview != null) {
            selectedReview.setComment(textAreaComment.getText());
            selectedReview.setScore((Integer) spinnerScore.getValue());
            boolean success = ReviewControl.getInstance().updateReview(selectedReview);
            if (success) {
                JOptionPane.showMessageDialog(this, "Review updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update review.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadMemberReviews() {
        DefaultListModel<Review> model = new DefaultListModel<>();
        List<Review> reviews = ReviewControl.getInstance().getReviewsByMember(currentMember);
        for (Review review : reviews) {
            model.addElement(review);
        }
        listReviews.setModel(model);
    }
    
    private void deleteReview() {
        Review selectedReview = listReviews.getSelectedValue();
        if (selectedReview != null) {
            int response = JOptionPane.showConfirmDialog(
                    this, 
                    "Are you sure you want to delete this review?", 
                    "Confirm deletion", 
                    JOptionPane.YES_NO_OPTION);
 
            if (response == JOptionPane.YES_OPTION) {
                boolean success = ReviewControl.getInstance().deleteReview(selectedReview);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Review deleted successfully!");
                    loadMemberReviews(); // Refresh the list of reviews
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete review.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void clearForm() {
        listReviews.clearSelection();
        textAreaComment.setText("");
        spinnerScore.setValue(0);
    }
    public void back() {
    	clearForm();
    	MainApplicationFrame.getInstance().showPlacesViewScreen();
    }
}
