package boundry;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import control.MemberControl;
import control.TripControl;
import entity.Member;
import entity.Place;
import entity.Trip;

import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionListener;

public class EditTripPanel extends JPanel {
    private JDateChooser startDateChooser, endDateChooser;
    private JButton confirmButton;
    private JButton addPlaceButton;
    private JButton inviteMemberButton;

    private TripDataListener tripDataListener;
    private Trip newTrip;
    private JButton btnRemoveMember;
    private JButton btnRemovePlace;
    private JScrollPane scrollPaneMembers;
    private JScrollPane scrollPanePlaces;
    private JList<Member> travelersList;
    
    private JList<Place> addedPlacesList;
    DefaultListModel<Member> travelersListModel;
    DefaultListModel<Place> placesListModel;
    
    JComboBox <Trip>comboBoxSelectTrip;
    private JButton btnSearchByEmail;
    private JTextField textFieldSearchByEmail;
    private JComboBox <Member>comboBoxMemberInvite;
    private JRadioButton rdbtnSearchMethod;
    
    public EditTripPanel(TripDataListener listener) {
        this.tripDataListener = listener;

        // Panel for date choosers
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBounds(160, 65, 463, 71);
        startDateChooser = new JDateChooser();
        startDateChooser.setMinSelectableDate(new Date()); // Restrict past dates
        endDateChooser = new JDateChooser();
        endDateChooser.setMinSelectableDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        // Set the preferred size of the date choosers
        startDateChooser.setPreferredSize(new Dimension(150, 20)); // Adjust width as needed
        endDateChooser.setPreferredSize(new Dimension(150, 20)); // Adjust width as needed

        datePanel.add(new JLabel("Start Date:"));
        datePanel.add(startDateChooser);
        datePanel.add(new JLabel("End Date:"));
        datePanel.add(endDateChooser);

        // Simplified member invitation section
        JPanel memberPanel = new JPanel();
        memberPanel.setBounds(160, 148, 463, 158);
        inviteMemberButton = new JButton("Invite Member");
        
        inviteMemberButton.setEnabled(false);
        inviteMemberButton.setBounds(10, 95, 151, 23);
        inviteMemberButton.addActionListener(e -> inviteMember()); // Placeholder action
        memberPanel.setLayout(null);
        
        btnSearchByEmail = new JButton("Search By Email");
        btnSearchByEmail.setEnabled(false);
        btnSearchByEmail.setBounds(10, 66, 151, 23);
        memberPanel.add(btnSearchByEmail);
        
        comboBoxMemberInvite = new JComboBox<>();
        comboBoxMemberInvite.setBounds(10, 7, 151, 22);
        
        memberPanel.add(comboBoxMemberInvite);

        rdbtnSearchMethod = new JRadioButton("Search Method");
        rdbtnSearchMethod.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(rdbtnSearchMethod.isSelected()) {
        			btnSearchByEmail.setEnabled(true);
        			textFieldSearchByEmail.setEnabled(true);
        			comboBoxMemberInvite.setEnabled(false);
        		}
        		else {
        			btnSearchByEmail.setEnabled(false);
        			textFieldSearchByEmail.setEnabled(false);
        			comboBoxMemberInvite.setEnabled(true);
        		}
        	}
        });
        rdbtnSearchMethod.setBounds(167, 7, 111, 23);
        memberPanel.add(rdbtnSearchMethod);
        
        textFieldSearchByEmail = new JTextField();
        textFieldSearchByEmail.setBounds(10, 36, 151, 23);
        memberPanel.add(textFieldSearchByEmail);
        textFieldSearchByEmail.setColumns(10);
        
        JLabel lblTripMembers = new JLabel("Trip Members");
        lblTripMembers.setBounds(254, 39, 84, 14);
        memberPanel.add(lblTripMembers);
        travelersListModel = new DefaultListModel<>();
        travelersList = new JList<Member>(travelersListModel);
        scrollPaneMembers = new JScrollPane(travelersList);
        scrollPaneMembers.setBounds(195, 52, 258, 89);
        scrollPaneMembers.setEnabled(false);
        placesListModel = new DefaultListModel<>();
        addedPlacesList = new JList<Place>(placesListModel);
        memberPanel.add(scrollPaneMembers);
        memberPanel.add(inviteMemberButton);
        
        // Simplified places addition section
        JPanel placePanel = new JPanel();
        placePanel.setBounds(160, 317, 463, 158);
        placePanel.setLayout(null);
        JLabel label = new JLabel("Added Places:");
        label.setBounds(10, 11, 70, 14);
        placePanel.add(label);
        
        scrollPanePlaces = new JScrollPane(addedPlacesList);
        scrollPanePlaces.setBounds(195, 9, 258, 130);
        placePanel.add(scrollPanePlaces);
        scrollPanePlaces.setEnabled(false);

        // Buttons for confirmation and trip creation
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBounds(160, 486, 463, 73);
        confirmButton = new JButton("Confirm Trip");
        confirmButton.setEnabled(false);
        //createTripButton.addActionListener(this::createTrip); // Ensure this method is defined to handle trip creation logic
        buttonPanel.add(confirmButton);
        setLayout(null);      
        
        // Adding sub-panels to the main panel
        add(datePanel);
        
        JButton updateDatesButton = new JButton("Update Dates");
        updateDatesButton.addActionListener(e -> updateDatesListener());
        updateDatesButton.setEnabled(false);
        datePanel.add(updateDatesButton);
        add(memberPanel);
        
        btnRemoveMember = new JButton("Remove Member");
        btnRemoveMember.setEnabled(false);
        btnRemoveMember.addActionListener(e -> removeSelectedMember());
        btnRemoveMember.setBounds(10, 124, 151, 23);
        memberPanel.add(btnRemoveMember);
        add(placePanel);
        
        btnRemovePlace = new JButton("Remove Place");
        btnRemovePlace.setBounds(10, 118, 151, 23);
        btnRemovePlace.setEnabled(false);
        btnRemovePlace.addActionListener(e -> removeSelectedPlace());

        placePanel.add(btnRemovePlace);
        addPlaceButton = new JButton("Add Place");
        addPlaceButton.setBounds(10, 32, 151, 23);
        addPlaceButton.setEnabled(false);
        addPlaceButton.addActionListener(e -> addPlaceToTripListener()); // Placeholder action
        placePanel.add(addPlaceButton);
        add(buttonPanel);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> cancel());
        buttonPanel.add(btnCancel);
        
        JLabel lblSelectTrip = new JLabel("Select Trip");
        lblSelectTrip.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblSelectTrip.setBounds(41, 65, 94, 27);
        add(lblSelectTrip);
        
        comboBoxSelectTrip = new JComboBox<>();
        
        comboBoxSelectTrip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newTrip = (Trip) comboBoxSelectTrip.getSelectedItem();
                if (newTrip != null) {
                    // Update date choosers
                    startDateChooser.setDate(newTrip.getStartDate());
                    endDateChooser.setDate(newTrip.getEndDate());
                    updateDatesButton.setEnabled(true);
                    TripControl.getInstance().getAllTrips().get(newTrip.getTripNumber()).notConfirm();
                    // Update travelers list
                    travelersListModel.clear(); // Clear existing list
                    for (Member member : newTrip.getTripMembers().values()) {
                        travelersListModel.addElement(member); // Add members of the new trip
                    }

                    // Update places list
                    placesListModel.clear(); // Clear existing list
                    for (Place place : newTrip.getTripPlaces().values()) {
                        placesListModel.addElement(place); // Add places of the new trip
                    }
                 // Update UI components if necessary
                    confirmButton.setEnabled(false);
                    inviteMemberButton.setEnabled(false);    
                    addPlaceButton.setEnabled(false);
                    btnRemovePlace.setEnabled(false);
                    btnRemoveMember.setEnabled(false);
                    scrollPanePlaces.setEnabled(false);
                    scrollPaneMembers.setEnabled(false);
                    rdbtnSearchMethod.setEnabled(false);
                    comboBoxMemberInvite.setEnabled(false);
                    textFieldSearchByEmail.setEnabled(false);
                    travelersList.setEnabled(false);
                    addedPlacesList.setEnabled(false);
                    
                    btnSearchByEmail.setEnabled(false);
                    updateDatesButton.setEnabled(true);
                    startDateChooser.setEnabled(true);
                    endDateChooser.setEnabled(true);
                }
                else{
                	confirmButton.setEnabled(false);
                    inviteMemberButton.setEnabled(false);    
                    addPlaceButton.setEnabled(false);
                    btnRemovePlace.setEnabled(false);
                    btnRemoveMember.setEnabled(false);
                    scrollPanePlaces.setEnabled(false);
                    scrollPaneMembers.setEnabled(false);
                    rdbtnSearchMethod.setEnabled(false);
                    comboBoxMemberInvite.setEnabled(false);
                    textFieldSearchByEmail.setEnabled(false);
                    btnSearchByEmail.setEnabled(false);
                    updateDatesButton.setEnabled(false);
                    startDateChooser.setEnabled(false);
                    endDateChooser.setEnabled(false);                	
                }
                revalidate();
                repaint();
            }
        });
        comboBoxSelectTrip.setBounds(10, 91, 140, 22);
        add(comboBoxSelectTrip);
        confirmButton.addActionListener(this::confirmTrip);
        
    }

private void confirmTrip(ActionEvent e) {
        
        if (!newTrip.getTripPlaces().isEmpty()) {
        	confirmButton.setEnabled(false);
            inviteMemberButton.setEnabled(false);    
            addPlaceButton.setEnabled(false);
            btnRemovePlace.setEnabled(false);
            btnRemoveMember.setEnabled(false);
            scrollPanePlaces.setEnabled(false);
            scrollPaneMembers.setEnabled(false);
            rdbtnSearchMethod.setEnabled(false);
            comboBoxMemberInvite.setEnabled(false);
            textFieldSearchByEmail.setEnabled(false);
            btnSearchByEmail.setEnabled(false);
            startDateChooser.setEnabled(false);
            endDateChooser.setEnabled(false);
            
            endDateChooser.setDate(null);
            startDateChooser.setDate(null);
            newTrip.confirm();
            JOptionPane.showMessageDialog(this, "Trip confirmed. Enjoy!!.", "TRIP CONFIRMED", JOptionPane.INFORMATION_MESSAGE);
            MainApplicationFrame.getInstance().showPlacesViewScreen();
        } else if(newTrip.getTripPlaces().isEmpty()) {
        	JOptionPane.showMessageDialog(this, "Please make sure there's atleast one place in the trip.", "TRIP NOT CONFIRMED", JOptionPane.ERROR_MESSAGE);   
        }
        
    }

    public void addTripsToComboBox() {
    	Member member = MemberControl.getInstance().getMemberById(UserSessionManager.getInstance().getCurrentUser());
    	comboBoxSelectTrip.removeAllItems();

    	TripControl.getInstance().getAllTrips().values().stream()
    	.filter(trip -> trip.getTripMembers().containsKey(member.getMemberNumber())).forEach(trip -> comboBoxSelectTrip.addItem(trip));
    			
    	comboBoxSelectTrip.addItem(null); 
    	
        
        	confirmButton.setEnabled(false);
            inviteMemberButton.setEnabled(false);    
            addPlaceButton.setEnabled(false);
            btnRemovePlace.setEnabled(false);
            btnRemoveMember.setEnabled(false);
            scrollPanePlaces.setEnabled(false);
            scrollPaneMembers.setEnabled(false);
            rdbtnSearchMethod.setEnabled(false);
            comboBoxMemberInvite.setEnabled(false);
            textFieldSearchByEmail.setEnabled(false);
            btnSearchByEmail.setEnabled(false);
            startDateChooser.setEnabled(false);
            endDateChooser.setEnabled(false);
    	this.revalidate();
    	this.repaint();
    }
    
    public void addTravelersToList(){
        DefaultListModel<Member> model = (DefaultListModel<Member>) travelersList.getModel();
        newTrip.getTripMembers().values().forEach(model::addElement);
    }
    
    public void addPlacesToList(){
        DefaultListModel<Place> model = (DefaultListModel<Place>) addedPlacesList.getModel();
        newTrip.getTripPlaces().values().forEach(model::addElement);
    }
    public void addPlaceToTripListener() {
    	MainApplicationFrame.getInstance().showAddPlaceToTripScreen();
    }
    public void updateDatesListener() {
    	if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
		    JOptionPane.showMessageDialog(this, "Please select both start and end dates.", "Date Selection Error", JOptionPane.ERROR_MESSAGE);
		    return;
		} else if (startDateChooser.getDate().after(endDateChooser.getDate())) {
		    // Additional check if start date is after the end date
		    JOptionPane.showMessageDialog(this, "The start date must be before the end date.", "Date Validation Error", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		else if(!TripControl.getInstance().areDatesFreeForTrip(newTrip, startDateChooser.getDate(), endDateChooser.getDate())) {
    		JOptionPane.showMessageDialog(this, "There are places that are not between the new dates! you must delete them first.", "Date Validation Error", JOptionPane.ERROR_MESSAGE);
    		confirmButton.setEnabled(true);
            inviteMemberButton.setEnabled(true);    
            addPlaceButton.setEnabled(true);
            btnRemovePlace.setEnabled(true);
            btnRemoveMember.setEnabled(true);
            scrollPanePlaces.setEnabled(true);
            scrollPaneMembers.setEnabled(true);
            rdbtnSearchMethod.setEnabled(true);
            comboBoxMemberInvite.setEnabled(true);
            textFieldSearchByEmail.setEnabled(false);
            btnSearchByEmail.setEnabled(false);
            startDateChooser.setEnabled(false);
            endDateChooser.setEnabled(false);
            travelersListModel.clear(); // Clear existing list
            for (Member member : newTrip.getTripMembers().values()) {
                travelersListModel.addElement(member); // Add members of the new trip
            }

            // Update places list
            placesListModel.clear(); // Clear existing list
            for (Place place : newTrip.getTripPlaces().values()) {
                placesListModel.addElement(place); // Add places of the new trip
            }
            addMembersToComboBox();
            refreshMembers();
            
            refreshPlacesList();
    		return;
    	}
		newTrip.setStartDate(startDateChooser.getDate());
		newTrip.setEndDate(endDateChooser.getDate());
		
		TripControl.getInstance().changeTripDates(newTrip, startDateChooser.getDate(), endDateChooser.getDate());
		Long newTripNumber = newTrip.getTripNumber();
        // Notify the listener (AddPlaceToTripPanel) about the new trip
        if(tripDataListener != null) {	
            tripDataListener.onTripCreated(newTripNumber, true);
        }
        
        	confirmButton.setEnabled(true);
            inviteMemberButton.setEnabled(true);    
            addPlaceButton.setEnabled(true);
            btnRemovePlace.setEnabled(true);
            btnRemoveMember.setEnabled(true);
            scrollPanePlaces.setEnabled(true);
            scrollPaneMembers.setEnabled(true);
            rdbtnSearchMethod.setEnabled(true);
            comboBoxMemberInvite.setEnabled(true);
            textFieldSearchByEmail.setEnabled(false);
            btnSearchByEmail.setEnabled(false);
            startDateChooser.setEnabled(false);
            endDateChooser.setEnabled(false);
            addMembersToComboBox();
            refreshMembers();
            refreshPlacesList();
        
    }
    
    
    private void inviteMember() {
        try {
            Member memberToInvite = null;

            // Check if the invitation method is by email
            if (rdbtnSearchMethod.isSelected()) {
                String email = textFieldSearchByEmail.getText().trim();
                // Validate email format if necessary
                if (!email.isEmpty() && email.contains("@")) {
                    memberToInvite = MemberControl.getInstance().getMemberByEmail(email);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Invite by selection from comboBox
                memberToInvite = (Member) comboBoxMemberInvite.getSelectedItem();
                if (memberToInvite == null) {
                    JOptionPane.showMessageDialog(this, "Please select a member to invite.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (memberToInvite != null && !travelersListModel.contains(memberToInvite)) {
                // Prevent inviting VP of Culture or VP of Content by their member numbers
                if (100==memberToInvite.getMemberNumber() || 200 == memberToInvite.getMemberNumber()) {
                    JOptionPane.showMessageDialog(this, "VPs cannot be invited to trips.", "Invitation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                travelersListModel.addElement(memberToInvite);
                TripControl.getInstance().addTravelerToTrip(newTrip, memberToInvite);
                refreshMembers();
                JOptionPane.showMessageDialog(this, "Member invited successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Member not found, already added, or not eligible for invitation.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    // Method to refresh and show current trip members
    public void refreshMembers() {
    	if(newTrip!=null) {
    		List<Member> currentMembers = newTrip.getTripMembers().values().stream().toList();
        	travelersListModel.clear();
            for (Member member : currentMembers) {
            	// Check if the selected member is the trip's creator
                if (member.getMemberNumber().equals(newTrip.getTripCreatorId())||member.getMemberNumber()==100||member.getMemberNumber()==200) {
                    
                }else {
                	travelersListModel.addElement(member);
                }
            }
    	}
    	
        revalidate();
        repaint();
    }
    
    private void removeSelectedMember() {
        // Get the selected member from the JList
        Member selectedMember = travelersList.getSelectedValue();

        if (selectedMember == null) {
            // No member selected
            JOptionPane.showMessageDialog(this, "Please select a member to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if the selected member is the trip's creator
        if (selectedMember.getMemberNumber().equals(newTrip.getTripCreatorId())) {
            JOptionPane.showMessageDialog(this, "The trip creator cannot be removed from the trip.", "Removal Not Allowed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm removal
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove " + selectedMember.getFirstName() + " " + selectedMember.getLastName() + " from the trip?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Remove from the model (and thus from the GUI list)
                travelersListModel.removeElement(selectedMember);

                // Assuming you have a method in TripControl to handle this
                boolean success = TripControl.getInstance().removeTravelerFromTrip(newTrip, selectedMember);
                if (!success) {
                    // Handle failure (e.g., member not found in trip or database error)
                    JOptionPane.showMessageDialog(this, "Failed to remove the member from the trip.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Optional: Refresh list or UI if necessary
                    JOptionPane.showMessageDialog(this, selectedMember.getFirstName() + " " + selectedMember.getLastName() + " was successfully removed from the trip.", "Member Removed", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred while removing the member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    public void addMembersToComboBox() {
    	List<Member> allMembers = new ArrayList<>(MemberControl.getInstance().getAllMembers().values());
    	for (Member member : allMembers) {
    		comboBoxMemberInvite.addItem(member);
        }
    	comboBoxMemberInvite.setRenderer(new DefaultListCellRenderer() {
    	    @Override
    	    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    	        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    	        if (value instanceof Member) {
    	            Member member = (Member) value;
    	            setText(member.getEmail()); // Assuming Member has a getEmail() method
    	        }
    	        return this;
    	    }
    	});
    }
    
    private void removeSelectedPlace() {
        // Get the selected place from the JList
        Place selectedPlace = addedPlacesList.getSelectedValue();

        if (selectedPlace == null) {
            // No place selected
            JOptionPane.showMessageDialog(this, "Please select a place to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm removal
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove " + selectedPlace.getName() + " from the trip?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Remove from the model (and thus from the GUI list)
                placesListModel.removeElement(selectedPlace);


                boolean success = TripControl.getInstance().removePlaceFromTrip(newTrip, selectedPlace);
                if (!success) {
                    // Handle failure (e.g., place not found in trip or database error)
                    JOptionPane.showMessageDialog(this, "Failed to remove the place from the trip.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    
                    JOptionPane.showMessageDialog(this, selectedPlace.getName() + " was successfully removed from the trip.", "Place Removed", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred while removing the place: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void refreshPlacesList() {
        // Clear the existing model
        placesListModel.clear();

        if (newTrip != null && newTrip.getTripPlaces() != null) {
            for (Place place : newTrip.getTripPlaces().values()) {
            	System.out.println(place);
                placesListModel.addElement(place);
            }
        }
        revalidate();
        repaint();
    }
    public void cancel() {
    	
    	startDateChooser.setDate(null);
    	endDateChooser.setDate(null);
    	confirmButton.setEnabled(true);
        if(newTrip!=null) {
            TripControl.getInstance().cancelTrip(newTrip);
        }
    	MainApplicationFrame.getInstance().showPlacesViewScreen();
    	this.revalidate();
    	this.repaint();
    }
}
