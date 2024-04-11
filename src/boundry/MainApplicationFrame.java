package boundry;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import control.MemberControl;

public class MainApplicationFrame extends JFrame implements UserStatusObserver {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private VPofContentPanel contentPanel;
    private VPofCulturePanel culturePanel;
    private HomePanel homePanel;
    private CreateTripPanel createTripPanel;
    private AddPlaceToTripPanel addPlaceToTripPanel;
    private EditTripPanel editTripPanel;
    private WriteReviewPanel writeReviewPanel;
    private UpdateReviewPanel updateReviewPanel;
    private DeleteReviewPanel deleteReviewPanel;
    private PlacesViewPanel placesViewPanel;
    
    // Singleton instance
    private static MainApplicationFrame instance;

    private MainApplicationFrame() {
        initializeFrame();
        UserSessionManager.getInstance().addObserver(this); // Subscribe to user status changes
    }

    // Ensures only one instance of MainApplicationFrame is created
    public static synchronized MainApplicationFrame getInstance() {
        if (instance == null) {
            instance = new MainApplicationFrame();
        }
        return instance;
    }

    private void initializeFrame() {
        setTitle("Perfect Trip Application");
        setSize(800, 600);
        setLocationRelativeTo(null);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        // Initialize panels and add them to cardPanel here
     // Instantiate and add HomePanel
        
        homePanel = new HomePanel(); // Assume constructor exists
        culturePanel = new VPofCulturePanel(cardLayout, cardPanel);
        contentPanel = new VPofContentPanel(cardLayout, cardPanel);        
        addPlaceToTripPanel = new AddPlaceToTripPanel();
        createTripPanel = new CreateTripPanel(addPlaceToTripPanel);
        editTripPanel = new EditTripPanel(addPlaceToTripPanel);
        writeReviewPanel = new WriteReviewPanel();
        updateReviewPanel = new UpdateReviewPanel(MemberControl.getInstance().getMemberById((UserSessionManager.getInstance().getCurrentUser())));
        deleteReviewPanel = new DeleteReviewPanel();
        placesViewPanel = new PlacesViewPanel();
        cardPanel.add(placesViewPanel, "placesViewPanel");
        cardPanel.add(updateReviewPanel, "updateReview");
        cardPanel.add(deleteReviewPanel, "deleteReview");
        cardPanel.add(culturePanel,"VPofCulture");
        cardPanel.add(contentPanel,"VPofContent");
        cardPanel.add(createTripPanel, "createTrip");
        cardPanel.add(addPlaceToTripPanel, "addPlaceToTrip");
        cardPanel.add(editTripPanel, "editTrip");
        cardPanel.add(writeReviewPanel, "writeReview");

        


        
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
        // Initialize with the current user status
        boolean isRegisteredUser = UserSessionManager.getInstance().isRegisteredUser();
        setJMenuBar(createMenuBar(isRegisteredUser));       
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                UserSessionManager.getInstance().removeObserver(MainApplicationFrame.this);
            }
        });
    }

    public CreateTripPanel getCreateTripPanel() {
		return createTripPanel;
	}

	public EditTripPanel getEditTripPanel() {
		return editTripPanel;
	}	

	@Override
    public void update(boolean isRegisteredUser) {
    	SwingUtilities.invokeLater(() -> {
            // Update the menu based on the user's status
            setJMenuBar(createMenuBar(isRegisteredUser));

            // If the user is not registered (i.e., logged out), hide this frame and show the MainFrame
            if (!isRegisteredUser) {
                this.setVisible(false); // Hide MainApplicationFrame
                // Ensure the MainFrame is visible for the user to log in again
                MainFrame.getInstance().setVisible(true); // Show MainFrame
            }

            // Ensure the UI is updated to reflect changes
            this.validate();
            this.repaint();
        });
    }
 // Updated createMenuBar method to include the new menus and action listeners
    private JMenuBar createMenuBar(boolean isRegisteredUser) {
        JMenuBar menuBar = new JMenuBar();

        // Home Menu
        JMenu homeMenu = new JMenu("Home");
        homeMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "placesViewPanel");
            }
        });
        menuBar.add(homeMenu);

        // Managing Trips Menu for Registered Users
        if(UserSessionManager.getInstance().getCurrentUser()!=null&&(UserSessionManager.getInstance().getCurrentUser()==100||UserSessionManager.getInstance().getCurrentUser()==200)) {
        	
       
        } else if (isRegisteredUser) {
            // Trips Management Menu
            JMenu tripsMenu = new JMenu("Manage Trips");
            JMenuItem createTrip = new JMenuItem("Create a New Trip");
            JMenuItem editTrip = new JMenuItem("Edit a Trip");

            // Adding Action Listeners for Trip Management
            createTrip.addActionListener(e -> {
            	showCreateTripScreen();
            });
            editTrip.addActionListener(e -> {
                showEditTripScreen();
            });


            tripsMenu.add(createTrip);
            tripsMenu.add(editTrip);
            menuBar.add(tripsMenu);

 


         // Manage Reviews Menu
            JMenu manageReviewsMenu = new JMenu("Manage Reviews");

            // Create menu items for managing reviews
            JMenuItem writeReviewItem = new JMenuItem("Write a Review");
            JMenuItem editReviewItem = new JMenuItem("Edit a Review");

            // Adding Action Listeners for Review Management
            writeReviewItem.addActionListener(e -> {
            	showWriteReviewScreen();
            });

            editReviewItem.addActionListener(e -> {
            showUpdateReviewScreen();
            });

            
            // Add the menu items to the "Manage Reviews" menu
            manageReviewsMenu.add(writeReviewItem);
            manageReviewsMenu.add(editReviewItem);

            // Add the "Manage Reviews" menu to the menu bar
            menuBar.add(manageReviewsMenu);
        	
        }else {
        	
            // Limited Menu for non-registered users
            JMenu loginMenu = new JMenu("Limited Menu");
            menuBar.add(loginMenu);
        }

        // Logout Menu Item
        JMenuItem logoutMenuItem = new JMenuItem("Logout");
        logoutMenuItem.addActionListener(e -> logout());
        menuBar.add(logoutMenuItem);

        return menuBar;
    }

    private void logout() {
        // Implement the logout functionality here
        UserSessionManager.getInstance().logout();
        this.setVisible(false);
        showPlacesViewScreen();
        MainFrame.getInstance().setVisible(true);
        MainFrame.getInstance().showWelcomePanel();
    }

    public void showVPCultureScreen() {
    		this.getCardLayout().show(cardPanel, "VPofCulture");
    }
    public void showCreateTripScreen() {
    	createTripPanel.refreshCreateTriptoShow();
		this.getCardLayout().show(cardPanel, "createTrip");
    }
    public void showAddPlaceToTripScreen() {
    	addPlaceToTripPanel.refreshAddPlacePanel();
		this.getCardLayout().show(cardPanel, "addPlaceToTrip");
    }
    public void showPlacesViewScreen() {
		this.getCardLayout().show(cardPanel, "placesViewPanel");
	}
    public void showVPContentScreen() {
		this.getCardLayout().show(cardPanel, "VPofContent");	
	}
    public void showWriteReviewScreen() {
    	writeReviewPanel.refreshWriteReviewPanel();
		this.getCardLayout().show(cardPanel, "writeReview");	
	}
	public void showEditTripScreen() {
			editTripPanel.addTripsToComboBox();
			this.getCardLayout().show(cardPanel, "editTrip");
	    }
	public void showUpdateReviewScreen() {
	    // Optionally add code to refresh or initialize the update review panel
	    cardLayout.show(cardPanel, "updateReview");
	}

	public void showDeleteReviewScreen() {
	    // Optionally add code to refresh or initialize the delete review panel
	    cardLayout.show(cardPanel, "deleteReview");
	}
	
	public void showCreateTripAfterTripCreated() {
		createTripPanel.refreshBackFromInTripCreated();
		this.getCardLayout().show(cardPanel, "createTrip");
	}
	
	public void showEditTripScreenAfterEdit() {
		
		this.getCardLayout().show(cardPanel, "editTrip");		
	}
	
	public CardLayout getCardLayout() {
		return cardLayout;
	}

	public JPanel getCardPanel() {
		return cardPanel;
	}

	

	

	

}