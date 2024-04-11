package boundry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import control.ContentControl;
import control.ReviewControl;

public class VPofContentPanel extends JPanel {
    private JButton importXMLButton;
    private JButton exportReviewsButton;
    private JTextField yearField;

    public VPofContentPanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        initializeUIComponents();
        attachEventHandlers();
    }

    private void initializeUIComponents() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        importXMLButton = new JButton("Import XML");
        exportReviewsButton = new JButton("Export Reviews");
        yearField = new JTextField(4); // To hold the year, assuming it's 4 digits

        controlPanel.add(importXMLButton);
        controlPanel.add(new JLabel("Year: "));
        controlPanel.add(yearField);
        controlPanel.add(exportReviewsButton);
        
        add(controlPanel, BorderLayout.NORTH);
    }

    private void attachEventHandlers() {
        importXMLButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                ContentControl.getInstance().importPlacesFromXML(selectedFile.getAbsolutePath());
            }
        });

        exportReviewsButton.addActionListener(e -> {
            String yearStr = yearField.getText().trim();
            try {
                int year = Integer.parseInt(yearStr);
                boolean success = ReviewControl.getInstance().exportReviewsToWord(year);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Reviews exported successfully to Word for the year: " + year);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to export reviews for the year: " + year, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid year", "Invalid Year", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
