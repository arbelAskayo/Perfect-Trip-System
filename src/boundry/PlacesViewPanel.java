package boundry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import entity.Place;
import control.SearchControl;
import control.ReviewControl;

public class PlacesViewPanel extends JPanel {
    private JTable placesTable;
    private DefaultTableModel model;
    private SearchControl searchControl;
    private ReviewControl reviewControl;

    public PlacesViewPanel() {
        searchControl = SearchControl.getInstance();
        reviewControl = ReviewControl.getInstance();
        initializeUI();
        loadPlaces();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"Place ID", "Name", "Description", "Price Level", "View Details"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the "View Details" column is clickable
            }
        };
        placesTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(placesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Adjust column widths
        placesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Name
        placesTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Description
        placesTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Price Level
        placesTable.getColumnModel().getColumn(4).setPreferredWidth(100); // View Details

        // Hide the Place ID column from view
        placesTable.removeColumn(placesTable.getColumnModel().getColumn(0));

        // Add button renderer and editor for "View Details" column
        placesTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        placesTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void loadPlaces() {
        // Clear existing data
        model.setRowCount(0);

        // Fetch places
        List<Place> places =  searchControl.getAllPlaces().values().stream().toList();
        for (Place place : places) {
            model.addRow(new Object[]{place.getPlaceId(), place.getName(), place.getDescription(), place.getPriceLevel().toString(), "View Details"});
        }
    }
    // Custom button renderer
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

 // Inside PlacesViewPanel class

 // Custom button editor to handle "View Details" action
 private class ButtonEditor extends DefaultCellEditor {
     private JButton button;
     private String label;
     private boolean isPushed;
     private Long placeId; // Store the place ID for fetching details

     public ButtonEditor(JCheckBox checkBox) {
         super(checkBox);
         button = new JButton();
         button.setOpaque(true);
         button.addActionListener(e -> fireEditingStopped());
     }

     public Component getTableCellEditorComponent(JTable table, Object value,
                                                  boolean isSelected, int row, int column) {
         if (isSelected) {
             button.setForeground(table.getSelectionForeground());
             button.setBackground(table.getSelectionBackground());
         } else {
             button.setForeground(table.getForeground());
             button.setBackground(table.getBackground());
         }
         label = (value == null) ? "" : value.toString();
         button.setText(label);
         isPushed = true;

         // Correctly fetch the placeId considering hidden columns
         placeId = (Long) table.getModel().getValueAt(row, 0); // Directly access model data

         return button;
     }

     public Object getCellEditorValue() {
         if (isPushed) {
             // Fetch the Place object and display its details
             Place selectedPlace = searchControl.getAllPlaces().get(placeId);
             displayPlaceDetails(selectedPlace);
         }
         isPushed = false;
         return label;
     }

     private void displayPlaceDetails(Place place) {
         if (place != null) {
             PlaceDetailsDialog detailsDialog = new PlaceDetailsDialog(SwingUtilities.getWindowAncestor(PlacesViewPanel.this), place, reviewControl.getReviewsByPlace(place));
             detailsDialog.setVisible(true);
         }
     }
 }

}
