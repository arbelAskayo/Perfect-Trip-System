package boundry;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
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

public class CreateTripPanel extends JPanel {
    private JDateChooser startDateChooser, endDateChooser;
    private JButton confirmButton, createTripButton;
    private JButton addPlaceButton;
    private JButton inviteMemberButton;
    private TripDataListener tripDataListener;
    private JButton btnRemoveMember;
    private JButton btnRemovePlace;
    private JScrollPane scrollPaneMembers;
    private JScrollPane scrollPanePlaces;
    private JList<Member> travelersList;
    private JList<Place> addedPlacesList;
    DefaultListModel<Member> travelersListModel;
    DefaultListModel<Place> placesListModel;
    private JButton btnSearchByEmail;
    private JTextField textFieldSearchByEmail;
    private JComboBox <Member>comboBoxMemberInvite;
    private JRadioButton rdbtnSearchMethod;
    private Trip newTrip;

    public CreateTripPanel(TripDataListener listener) {
        this.tripDataListener = listener;

        // Panel for date choosers
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBounds(160, 65, 463, 71);
        startDateChooser = new JDateChooser();
        startDateChooser.setMinSelectableDate(new Date()); // Restrict past dates
        endDateChooser = new JDateChooser();
        endDateChooser.setMinSelectableDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        // Set the preferred size of the date choosers
        startDateChooser.setPreferredSize(new Dimension(150, 20));
        endDateChooser.setPreferredSize(new Dimension(150, 20));

        datePanel.add(new JLabel("Start Date:"));
        datePanel.add(startDateChooser);
        datePanel.add(new JLabel("End Date:"));
        datePanel.add(endDateChooser);

        // Simplified member invitation section
        JPanel memberPanel = new JPanel();
        memberPanel.setBounds(160, 148, 463, 158);
        inviteMemberButton = new JButton("Invite Member");
        inviteMemberButton.addActionListener(e -> inviteMember());
        inviteMemberButton.setBounds(10, 95, 151, 23);
        inviteMemberButton.setEnabled(false);
        memberPanel.setLayout(null);
        
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
        label.setBounds(10, 11, 151, 14);
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
        buttonPanel.add(confirmButton);
        setLayout(null);      
        
        // Adding sub-panels to the main panel
        add(datePanel);
        //confirmButton.addActionListener(this::confirmTrip); // Ensure this method is defined to handle confirmation logic
        createTripButton = new JButton("Create Trip");
        datePanel.add(createTripButton);
        add(memberPanel);
        
        btnRemoveMember = new JButton("Remove Member");
        btnRemoveMember.addActionListener(e -> removeSelectedMember());
        btnRemoveMember.setBounds(10, 124, 151, 23);
        btnRemoveMember.setEnabled(false);
        memberPanel.add(btnRemoveMember);
        
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
        add(placePanel);
        
        btnRemovePlace = new JButton("Remove Place");
        btnRemovePlace.setBounds(10, 118, 151, 23);
        btnRemovePlace.setEnabled(false);
        btnRemovePlace.addActionListener(e -> removeSelectedPlace());

        placePanel.add(btnRemovePlace);
        addPlaceButton = new JButton("Add Place");
        addPlaceButton.setBounds(10, 32, 151, 23);
        addPlaceButton.setEnabled(false);
        addPlaceButton.addActionListener(e -> MainApplicationFrame.getInstance().showAddPlaceToTripScreen()); // Placeholder action
        placePanel.add(addPlaceButton);
        add(buttonPanel);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> cancel());
        buttonPanel.add(btnCancel);
        
        createTripButton.addActionListener(this::createTrip);
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
            
            startDateChooser.setEnabled(true);
            endDateChooser.setEnabled(true);
            createTripButton.setEnabled(true);
            
            endDateChooser.setDate(null);
            startDateChooser.setDate(null);
            newTrip.isConfirmed();
            JOptionPane.showMessageDialog(this, "Trip confirmed. Enjoy!!.", "TRIP CONFIRMED", JOptionPane.INFORMATION_MESSAGE);
            MainApplicationFrame.getInstance().showPlacesViewScreen();
        } else if(newTrip.getTripPlaces().isEmpty()) {
        	JOptionPane.showMessageDialog(this, "Please make sure there's atleast one place in the trip.", "TRIP NOT CONFIRMED", JOptionPane.ERROR_MESSAGE);   
        }
        
    }

    private void createTrip(ActionEvent e) {
        
    	// Check if dates are selected
    	if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
    	    JOptionPane.showMessageDialog(this, "Please select both start and end dates.", "Date Selection Error", JOptionPane.ERROR_MESSAGE);
    	    return;
    	} else if (startDateChooser.getDate().after(endDateChooser.getDate())) {
    	    // Additional check if start date is after the end date
    	    JOptionPane.showMessageDialog(this, "The start date must be before the end date.", "Date Validation Error", JOptionPane.ERROR_MESSAGE);
    	    return;
    	}

        Date startDateStr = startDateChooser.getDate();
        Date endDateStr = endDateChooser.getDate();
        

        // Assuming you have a method or way to obtain the current user as the trip's creator
        Member creator = MemberControl.getInstance().getMemberById(UserSessionManager.getInstance().getCurrentUser()); 

        // Now, call the createTrip method with the formatted date strings and creator
        try {
            newTrip = TripControl.getInstance().createTrip(startDateStr, endDateStr, creator);
            if (newTrip != null) {
                inviteMemberButton.setEnabled(true);
                confirmButton.setEnabled(true);
                addPlaceButton.setEnabled(true);
                btnRemovePlace.setEnabled(true);
                btnRemoveMember.setEnabled(true);
                scrollPanePlaces.setEnabled(true);
                scrollPaneMembers.setEnabled(true);
                btnSearchByEmail.setEnabled(false);
                textFieldSearchByEmail.setEnabled(false);
                comboBoxMemberInvite.setEnabled(true);
                rdbtnSearchMethod.setEnabled(true);
                startDateChooser.setEnabled(false);
                endDateChooser.setEnabled(false);
                createTripButton.setEnabled(false);
                Long newTripNumber = newTrip.getTripNumber();
                        // Notify the listener (AddPlaceToTripPanel) about the new trip
                        if(tripDataListener != null) {
                            tripDataListener.onTripCreated(newTripNumber, false);
                        }
                JOptionPane.showMessageDialog(this, "Trip created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.revalidate();
                this.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create the trip.", "Creation Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating the trip: " + ex.getMessage(), "Creation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void addTravelersToList(){
        DefaultListModel<Member> model = (DefaultListModel<Member>) travelersList.getModel();
        newTrip.getTripMembers().values().forEach(model::addElement);
    }
    
    public void addPlacesToList(){
        DefaultListModel<Place> model = (DefaultListModel<Place>) addedPlacesList.getModel();
        newTrip.getTripPlaces().values().forEach(model::addElement);
    }
    
    public void refreshBackFromInTripCreated() {
    	inviteMemberButton.setEnabled(true);
        confirmButton.setEnabled(true);
        addPlaceButton.setEnabled(true);
        btnRemovePlace.setEnabled(true);
        btnRemoveMember.setEnabled(true);
        scrollPanePlaces.setEnabled(true);
        scrollPaneMembers.setEnabled(true);
        btnSearchByEmail.setEnabled(false);
        textFieldSearchByEmail.setEnabled(false);
        comboBoxMemberInvite.setEnabled(true);
        rdbtnSearchMethod.setEnabled(true);
        startDateChooser.setEnabled(false);
        endDateChooser.setEnabled(false);
        createTripButton.setEnabled(false);
        refreshPlacesList();
        this.revalidate();
        this.repaint();
    }
    public void refreshCreateTriptoShow() {
    	 confirmButton.setEnabled(false);
         inviteMemberButton.setEnabled(false);    
         addPlaceButton.setEnabled(false);
         btnRemovePlace.setEnabled(false);
         btnRemoveMember.setEnabled(false);
         scrollPanePlaces.setEnabled(false);
         scrollPaneMembers.setEnabled(false);
         btnSearchByEmail.setEnabled(false);
         textFieldSearchByEmail.setEnabled(false);
         comboBoxMemberInvite.setEnabled(false);
         rdbtnSearchMethod.setEnabled(false);
         startDateChooser.setEnabled(true);
         endDateChooser.setEnabled(true);
         createTripButton.setEnabled(true);
         
         endDateChooser.setDate(null);
         startDateChooser.setDate(null);
         addMembersToComboBox();
         this.revalidate();
         this.repaint();
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
    	            setText(member.getEmail());
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

        // Assuming your Trip object has a method to get all associated places
        // and it returns a collection of Place objects
        if (newTrip != null && newTrip.getTripPlaces() != null) {
            for (Place place : newTrip.getTripPlaces().values()) {
                placesListModel.addElement(place);
            }
        }
        revalidate();
        repaint();
    }

    public void cancel() {
    	 confirmButton.setEnabled(false);
         inviteMemberButton.setEnabled(false);    
         addPlaceButton.setEnabled(false);
         btnRemovePlace.setEnabled(false);
         btnRemoveMember.setEnabled(false);
         scrollPanePlaces.setEnabled(false);
         scrollPaneMembers.setEnabled(false);
         btnSearchByEmail.setEnabled(false);
         textFieldSearchByEmail.setEnabled(false);
         comboBoxMemberInvite.setEnabled(false);
         rdbtnSearchMethod.setEnabled(false);
         startDateChooser.setEnabled(true);
         endDateChooser.setEnabled(true);
         createTripButton.setEnabled(true);
         
         endDateChooser.setDate(null);
         startDateChooser.setDate(null);
        if(newTrip!=null) {
            TripControl.getInstance().cancelTrip(newTrip);
        }
    	MainApplicationFrame.getInstance().showPlacesViewScreen();
    	this.revalidate();
    	this.repaint();
    }
}
