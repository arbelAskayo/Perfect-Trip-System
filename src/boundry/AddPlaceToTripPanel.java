package boundry;
import utils.*;
import control.SearchControl;
import control.TripControl;
import entity.City;
import entity.Place;
import entity.Trip;
import exceptions.NegativeNumberException;
import exceptions.OnlyNumbersException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class AddPlaceToTripPanel extends JPanel implements TripDataListener {
    private Long currentTripNumber; // To store the current trip number
    private Boolean flagForEditOrCreate = true;
    private JTextField textFieldSearchByName;
    private JComboBox<City> comboBoxCity;
    private JComboBox<Integer> comboBoxStarRating;
    private JComboBox<PriceLevel> comboBoxPriceLevel;
    private JComboBox<String> comboBoxPlaceType;
    private JComboBox<String> comboBoxKitchenStyle;
    private JComboBox<AccomodationStyles> comboBoxAccomodationStyle;
    private JButton btnSearch;
    private JButton btnResetSearch;
    private JButton btnGenerateRecommendations;
    private JButton btnExportReport;
    private JLabel lblAccomodationStyle;
    private JLabel lblKitchenStyle;
    private JTextField textFieldRadius;
    private JTable tablePlaces;
    private DefaultTableModel tableModel;
    private JDateChooser selectedDate;

    public AddPlaceToTripPanel() {
        setPreferredSize(new Dimension(800, 600)); // Adjusting to fit within a larger frame
        setLayout(null);
        
        
        selectedDate = new JDateChooser();
        selectedDate.setBounds(320, 392, 150, 20);
        this.add(selectedDate);
        
        JLabel SpecificSearchLbl = new JLabel("Specific Search");
		SpecificSearchLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		SpecificSearchLbl.setBounds(89, 273, 119, 20);
		this.add(SpecificSearchLbl);
		
		textFieldSearchByName = new JTextField();
		textFieldSearchByName.setBounds(100, 321, 96, 20);
		this.add(textFieldSearchByName);
		textFieldSearchByName.setColumns(10);
		
		JLabel lblPriceLevel = new JLabel("Price Level");
		lblPriceLevel.setBounds(154, 398, 96, 14);
		this.add(lblPriceLevel);
		
		comboBoxPriceLevel = new JComboBox<>(PriceLevel.values());
		comboBoxPriceLevel.setSelectedItem(null);
		comboBoxPriceLevel.setBounds(154, 413, 96, 20);
		this.add(comboBoxPriceLevel);
		
		JLabel lblSearchByName = new JLabel("Search By Name");
		lblSearchByName.setBounds(100, 306, 96, 14);
		this.add(lblSearchByName);
		
		
		
		JLabel lblPlaceType = new JLabel("Place Type");
		lblPlaceType.setBounds(154, 352, 96, 14);
		this.add(lblPlaceType);
		
		comboBoxKitchenStyle = new JComboBox<>();
		comboBoxKitchenStyle.setBounds(154, 459, 96, 20);
		comboBoxKitchenStyle.insertItemAt(null, 0);
		SearchControl.getInstance().getAllKitchenStyles().forEach((String k)->comboBoxKitchenStyle.addItem(k));
		this.add(comboBoxKitchenStyle);
		comboBoxKitchenStyle.setVisible(false);
		
		JLabel lblKitchenStyle = new JLabel("Kitchen Style");
		lblKitchenStyle.setBounds(154, 444, 108, 14);
		this.add(lblKitchenStyle);
		lblKitchenStyle.setVisible(false);
		
		JLabel lblAccomodationStyle = new JLabel("Accomodation Style");
		lblAccomodationStyle.setBounds(48, 444, 133, 14);
		this.add(lblAccomodationStyle);
		lblAccomodationStyle.setVisible(false);
		
		comboBoxAccomodationStyle = new JComboBox<>(AccomodationStyles.values());
		comboBoxAccomodationStyle.setBounds(48, 459, 96, 20);
		comboBoxAccomodationStyle.setSelectedItem(null);
		this.add(comboBoxAccomodationStyle);
		comboBoxAccomodationStyle.setVisible(false);
		
		comboBoxCity = new JComboBox<>();
		comboBoxCity.setBounds(48, 367, 96, 20);
		SearchControl.get_instance().getAllCities().values().forEach(comboBoxCity::addItem);
		comboBoxCity.setSelectedItem(null);
		this.add(comboBoxCity);
		
		JLabel lblCity = new JLabel("City");
		lblCity.setBounds(48, 352, 96, 14);
		this.add(lblCity);
		
		comboBoxStarRating = new JComboBox<>(new Integer[]{null,1, 2, 3, 4, 5});
		comboBoxStarRating.setBounds(48, 413, 96, 20);
		comboBoxStarRating.setSelectedItem(null);
		this.add(comboBoxStarRating);
		
		JLabel lblStarRating = new JLabel("Star Rating");
		lblStarRating.setBounds(48, 398, 96, 14);
		this.add(lblStarRating);
		
		comboBoxPlaceType = new JComboBox<>(new String [] {null,"Place","Hotel","Restaurant"});
		comboBoxPlaceType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(comboBoxPlaceType.getSelectedItem()!=null) {
					if(comboBoxPlaceType.getSelectedItem().equals("Hotel")) {
						lblAccomodationStyle.setVisible(true);
						comboBoxAccomodationStyle.setVisible(true);
						comboBoxKitchenStyle.setVisible(false);
						lblKitchenStyle.setVisible(false);
						comboBoxKitchenStyle.setSelectedItem(null);
						
					}
					else if(comboBoxPlaceType.getSelectedItem().equals("Restaurant")) {
						lblAccomodationStyle.setVisible(false);
						comboBoxAccomodationStyle.setVisible(false);
						comboBoxKitchenStyle.setVisible(true);
						lblKitchenStyle.setVisible(true);
						comboBoxAccomodationStyle.setSelectedItem(null);
 
					}
					else {
						lblAccomodationStyle.setVisible(false);
						comboBoxAccomodationStyle.setVisible(false);
						comboBoxKitchenStyle.setVisible(false);
						lblKitchenStyle.setVisible(false);
						comboBoxAccomodationStyle.setSelectedItem(null);
						comboBoxKitchenStyle.setSelectedItem(null);
 
					}
					
				}
				else {
					lblAccomodationStyle.setVisible(false);
					comboBoxAccomodationStyle.setVisible(false);
					comboBoxKitchenStyle.setVisible(false);
					lblKitchenStyle.setVisible(false);
					comboBoxAccomodationStyle.setSelectedItem(null);
					comboBoxKitchenStyle.setSelectedItem(null);
				}
				
				
				
			}
		});
		comboBoxPlaceType.setBounds(154, 367, 96, 20);
		this.add(comboBoxPlaceType);
		
		btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 String searchByName = textFieldSearchByName.getText();
				 City city = (City)comboBoxCity.getSelectedItem();
				 String cityCode=null;
				 if(city!=null) {
				  cityCode = city.getCityCode();
				 }
				 Integer starRating = (Integer)comboBoxStarRating.getSelectedItem();
				 PriceLevel priceLevel = (PriceLevel)comboBoxPriceLevel.getSelectedItem();
				 String placeType = (String)comboBoxPlaceType.getSelectedItem();
				 String kitchenStyle = (String)comboBoxKitchenStyle.getSelectedItem();
				 AccomodationStyles accomodationStyles = (AccomodationStyles)comboBoxAccomodationStyle.getSelectedItem();
				
				 List<Place> searchPlacesList = SearchControl.get_instance().searchByCriteria(searchByName, cityCode, priceLevel, accomodationStyles, starRating, kitchenStyle, placeType);			     
			     updateTableWithPlaces(searchPlacesList);
		            if(searchPlacesList.isEmpty()) {
						 JOptionPane.showMessageDialog(AddPlaceToTripPanel.this, "there are no places with such criteria", "Information", JOptionPane.INFORMATION_MESSAGE);
		            }
		            
		            btnExportReport.setEnabled(!searchPlacesList.isEmpty()); // Enable the export button if there are recommendations

			}
			
		});
		btnSearch.setBounds(47, 490, 96, 23);
		this.add(btnSearch); 
		
		btnResetSearch = new JButton("Reset Search");
		btnResetSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				textFieldSearchByName.setText(null);
				comboBoxCity.setSelectedItem(null);				
				comboBoxStarRating.setSelectedItem(null);
				comboBoxPriceLevel.setSelectedItem(null);
				comboBoxPlaceType.setSelectedItem(null);
				comboBoxKitchenStyle.setSelectedItem(null);
				comboBoxAccomodationStyle.setSelectedItem(null);
				lblAccomodationStyle.setVisible(false);
				lblKitchenStyle.setVisible(false);
				comboBoxAccomodationStyle.setVisible(false);
				comboBoxKitchenStyle.setVisible(false);
				MainApplicationFrame.getInstance().revalidate();
				MainApplicationFrame.getInstance().repaint();
				
			}
		});
		btnResetSearch.setBounds(153, 490, 119, 23);
		this.add(btnResetSearch);
		
		btnGenerateRecommendations = new JButton("Generate Recommendations");
		
		btnGenerateRecommendations.setBounds(507, 318, 207, 40);
		this.add(btnGenerateRecommendations);
		
		textFieldRadius = new JTextField();
		textFieldRadius.setBounds(365, 336, 78, 23);
		this.add(textFieldRadius);
		textFieldRadius.setColumns(10);
		
		JLabel lblRadiusForRecommendations = new JLabel("Radius For Recommendations");
		lblRadiusForRecommendations.setBounds(334, 318, 152, 14);
		this.add(lblRadiusForRecommendations);
		
		// Button for exporting report, initially disabled
        btnExportReport = new JButton("Export Report");
        btnExportReport.setBounds(549, 372, 120, 25);
        btnExportReport.setEnabled(false); // Initially disabled
        this.add(btnExportReport);

        setupTable(); // Setup table for displaying recommendations
        JScrollPane scrollPane = new JScrollPane(tablePlaces); // Scroll pane for table
        scrollPane.setBounds(15, 27, 762, 235);
        this.add(scrollPane);
        
        JButton btnAddPlace = new JButton("Add Place");
        
        btnAddPlace.setFont(new Font("Tahoma", Font.PLAIN, 18));
        btnAddPlace.setBounds(320, 451, 197, 62);
        add(btnAddPlace);
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		textFieldSearchByName.setText(null);
        		comboBoxCity.setSelectedItem(null);				
        		comboBoxStarRating.setSelectedItem(null);
        		comboBoxPriceLevel.setSelectedItem(null);
        		comboBoxPlaceType.setSelectedItem(null);
        		comboBoxKitchenStyle.setSelectedItem(null);
        		comboBoxAccomodationStyle.setSelectedItem(null);
        		lblKitchenStyle.setVisible(false);
        		lblAccomodationStyle.setVisible(false);
        		tableModel.setRowCount(0);
        		comboBoxAccomodationStyle.setVisible(false);
        		comboBoxKitchenStyle.setVisible(false);
        		if(!flagForEditOrCreate) {
        			MainApplicationFrame.getInstance().getCreateTripPanel().refreshPlacesList();
        			MainApplicationFrame.getInstance().showCreateTripAfterTripCreated();
        		}
        		else {
        			MainApplicationFrame.getInstance().getEditTripPanel().refreshPlacesList();
        			MainApplicationFrame.getInstance().showEditTripScreenAfterEdit();
        		}
            	revalidate();
            	repaint();
        	}
        });
        btnBack.setFont(new Font("Tahoma", Font.PLAIN, 18));
        btnBack.setBounds(637, 451, 96, 62);
        add(btnBack);
        
        JLabel lblSelectdate = new JLabel("Select Date");
        lblSelectdate.setBounds(320, 373, 96, 14);
        add(lblSelectdate);

        btnGenerateRecommendations.addActionListener(this::generateRecommendations);

        btnExportReport.addActionListener(this::exportReport);
        btnAddPlace.addActionListener(this::addPlaceToTrip);

	}
	
	private void setupTable() {
        // Define table columns based on the Place attributes
        String[] columnNames = {"Place ID", "Name", "Description", "Price Level", "Map URL", "City Code"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make table cells non-editable
                return false;
            }
        };
        tablePlaces = new JTable(tableModel);
    }

    private void generateRecommendations(ActionEvent e) {
        try {
            double radius = Double.parseDouble(textFieldRadius.getText());
            if (radius < 0) {
                throw new IllegalArgumentException("Radius cannot be negative.");
            }

            List<Place> recommendedPlaces = SearchControl.getInstance().getGenerateRecommendation(1, radius); 

            updateTableWithPlaces(recommendedPlaces);
            if(recommendedPlaces.isEmpty()) {
            	JOptionPane.showMessageDialog(this, "No recommendations available to generate a report", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
            btnExportReport.setEnabled(!recommendedPlaces.isEmpty()); // Enable the export button if there are recommendations
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the radius.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableWithPlaces(List<Place> places) {
        // Clear table and update with new places
        tableModel.setRowCount(0); // Clear the existing content
        for (Place place : places) {
            Vector<Object> row = new Vector<>();
            row.add(place.getPlaceId());
            row.add(place.getName());
            row.add(place.getDescription());
            row.add(place.getPriceLevel().toString());
            row.add(place.getMapURL());
            row.add(place.getCityCode());
            tableModel.addRow(row);
        }
    }
    
    private double validateAndParseRadius(String radiusStr) throws Exception {
        if (!radiusStr.chars().allMatch(Character::isDigit)) {
            throw new OnlyNumbersException();
        }
        double radius = Double.parseDouble(radiusStr);
        if (radius < 0) {
            throw new NegativeNumberException();
        }
        return radius;
    }

    private void exportReport(ActionEvent e) {
        // Validate input and export report based on the generated recommendations
        try {
            double radius = validateAndParseRadius(textFieldRadius.getText()); // Re-validate or store radius from previous validation

            List<Place> places = SearchControl.getInstance().getGenerateRecommendation(1, radius);
            SearchControl.get_instance().exportToWord(places, radius);
            JOptionPane.showMessageDialog(this, "Report exported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export report: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void addPlaceToTrip(ActionEvent e) {
        // Check if a date has been selected
        if (selectedDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select the date for traveling to the place.", "Date Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Assuming the selected row gives you the correct place ID
            int selectedRow = tablePlaces.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a place from the table.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Trip trip = TripControl.getInstance().getAllTrips().get(currentTripNumber);
            // Retrieve the place ID from the selected row. Adjust the column index as necessary.
            Long placeID = (Long) tablePlaces.getModel().getValueAt(selectedRow, 0);
            // Access the place from the control class
            Place place = SearchControl.getInstance().getAllPlaces().get(placeID);
            if (place == null) {
                JOptionPane.showMessageDialog(this, "Selected place is not available.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if(TripControl.getInstance().getAllTrips().get(currentTripNumber)!=null){
            	if(TripControl.getInstance().getAllTrips().get(currentTripNumber).getTripPlaces().containsKey(place.getPlaceId())) {
                	JOptionPane.showMessageDialog(this, "Selected place is already in the trip.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
            	
            }

            Date date = selectedDate.getDate();
            boolean success = TripControl.getInstance().addPlaceToTrip(trip, place, date); 
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Place added to trip successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add place to trip.", "Operation Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        finally {
        	refreshAddPlacePanel();
        }
    }
    
    public void refreshAddPlacePanel() {
    	textFieldSearchByName.setText(null);
		comboBoxCity.setSelectedItem(null);				
		comboBoxStarRating.setSelectedItem(null);
		comboBoxPriceLevel.setSelectedItem(null);
		comboBoxPlaceType.setSelectedItem(null);
		comboBoxKitchenStyle.setSelectedItem(null);
		comboBoxAccomodationStyle.setSelectedItem(null);
		selectedDate.setDate(null);
		if(lblKitchenStyle!=null) {
			lblKitchenStyle.setVisible(false);
		}
		if(lblAccomodationStyle!=null) {
			lblAccomodationStyle.setVisible(false);
		}
		tableModel.setRowCount(0);

		comboBoxAccomodationStyle.setVisible(false);
		comboBoxKitchenStyle.setVisible(false);
		revalidate();
    	repaint();
    }

	public Boolean getFlagForEditOrCreate() {
		return flagForEditOrCreate;
	}

	public void setFlagForEditOrCreate(Boolean flagForEditOrCreate) {
		this.flagForEditOrCreate = flagForEditOrCreate;
	}

	@Override
    public void onTripCreated(Long tripNumber, Boolean state) {
        this.currentTripNumber = tripNumber;
        this.flagForEditOrCreate = state;
        selectedDate.setSelectableDateRange(TripControl.getInstance().getAllTrips().get(currentTripNumber).getStartDate()
        		, TripControl.getInstance().getAllTrips().get(currentTripNumber).getEndDate());
        this.revalidate();
        this.repaint();
    }
}