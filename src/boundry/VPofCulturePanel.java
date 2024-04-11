package boundry;

import javax.swing.*;
import control.CultureControl; // Import the CultureControl
import control.SearchControl;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;

class VPofCulturePanel extends JPanel {
    private JList<String> kitchenStyleList;
    private DefaultListModel<String> kitchenStyleListModel;
    private JTextField kitchenStyleField;
    private JButton addButton, deleteButton, updateButton, searchButton, resetButton; // Add resetButton
    private CultureControl cultureControl; // Instance of CultureControl

    public VPofCulturePanel(CardLayout cardLayout, JPanel cardPanel) {
        cultureControl = CultureControl.getInstance(); // Initialize CultureControl
        setLayout(new BorderLayout());
        initializeUIComponents();
        attachEventHandlers();
        loadKitchenStyles();
    }
 
    private void initializeUIComponents() {
        kitchenStyleListModel = new DefaultListModel<>();
        kitchenStyleList = new JList<>(kitchenStyleListModel);
        kitchenStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        kitchenStyleList.setVisibleRowCount(8);
        add(new JScrollPane(kitchenStyleList), BorderLayout.CENTER);
 
        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        kitchenStyleField = new JTextField();
        controlPanel.add(kitchenStyleField);
 
        JPanel buttonsPanel = new JPanel();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        searchButton = new JButton("Search");
        resetButton = new JButton("Reset"); 

 
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(searchButton);
        buttonsPanel.add(resetButton);

 
        controlPanel.add(buttonsPanel);
 
        add(controlPanel, BorderLayout.SOUTH);
    }
 
    private void attachEventHandlers() {
        addButton.addActionListener(e -> addKitchenStyle());
        deleteButton.addActionListener(e -> deleteKitchenStyle());
        updateButton.addActionListener(e -> updateKitchenStyle());
        searchButton.addActionListener(e -> searchKitchenStyle());
        resetButton.addActionListener(e -> resetSearch()); 

    }
    private void resetSearch() {
        kitchenStyleField.setText(""); // clear the search text field
        loadKitchenStyles(); // reload kitchen styles
    }
    private void loadKitchenStyles() {
        kitchenStyleListModel.clear(); // Clear the list model before loading
        kitchenStyleListModel.addAll(SearchControl.getInstance().getAllKitchenStyles());
    }
 
    private void addKitchenStyle() {
        String newStyle = kitchenStyleField.getText().trim();
        if (!newStyle.isEmpty() && cultureControl.addKitchenStyle(newStyle)) {
            kitchenStyleListModel.addElement(newStyle);
            kitchenStyleField.setText("");
        }
    }
 
    private void deleteKitchenStyle() {
        String selectedStyle = kitchenStyleList.getSelectedValue();
        if (selectedStyle != null && cultureControl.deleteKitchenStyle(selectedStyle)) {
            kitchenStyleListModel.removeElement(selectedStyle);
        }
    }
 
    private void updateKitchenStyle() {
        String selectedStyle = kitchenStyleList.getSelectedValue();
        String newName = kitchenStyleField.getText().trim();
        if (selectedStyle != null && !newName.isEmpty() && cultureControl.updateKitchenStyle(selectedStyle, newName)) {
            kitchenStyleListModel.set(kitchenStyleList.getSelectedIndex(), newName);
            kitchenStyleField.setText("");
        }
    }
 
    private void searchKitchenStyle() {
        String searchTerm = kitchenStyleField.getText().trim();
        if (!searchTerm.isEmpty()) {
            List <String> searchResults = cultureControl.searchKitchenStyles(searchTerm);
            kitchenStyleListModel.clear(); // Clear the list model before adding search results
            kitchenStyleListModel.addAll(searchResults);
        }
    }
}
