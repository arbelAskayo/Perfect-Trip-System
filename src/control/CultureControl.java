package control;

import entity.Consts;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CultureControl {
    
    // Singleton instance
    private static CultureControl instance;

    public static CultureControl getInstance() {
        if (instance == null) {
            instance = new CultureControl();
        }
        return instance;
    }

 // Insert a new kitchen style into the database
    public boolean addKitchenStyle(String styleName) {
        if (SearchControl.getInstance().getAllKitchenStyles().contains(styleName)) {
            System.out.println("Kitchen style already exists.");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_INS_KITCHEN_STYLE)) {

            pstmt.setString(1, styleName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1) {
                SearchControl.getInstance().getAllKitchenStyles().add(styleName);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

 // Delete a kitchen style from the database
    public boolean deleteKitchenStyle(String styleName) {
        if (!SearchControl.getInstance().getAllKitchenStyles().contains(styleName)) {
            System.out.println("Kitchen style does not exist.");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_DEL_KITCHEN_STYLE)) {

            pstmt.setString(1, styleName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1) {
                SearchControl.getInstance().getAllKitchenStyles().remove(styleName);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


 // Update a kitchen style in the database and the RestaurantKitchenStyle table
    public boolean updateKitchenStyle(String oldName, String newName) {
        if (!SearchControl.getInstance().getAllKitchenStyles().contains(oldName)) {
            System.out.println("Old kitchen style does not exist.");
            return false;
        }

        if (SearchControl.getInstance().getAllKitchenStyles().contains(newName)) {
            System.out.println("New kitchen style already exists.");
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        try {
            conn = DriverManager.getConnection(Consts.CONN_STR);
            // Turn off auto-commit for transaction block
            conn.setAutoCommit(false);

            // Update KitchenStyles table
            pstmt1 = conn.prepareStatement(Consts.SQL_UPD_KITCHEN_STYLE);
            pstmt1.setString(1, newName);
            pstmt1.setString(2, oldName);
            
            // Update RestaurantKitchenStyle table
            pstmt2 = conn.prepareStatement(Consts.SQL_UPD_REST_KITCHEN_STYLE);
            pstmt2.setString(1, newName);
            pstmt2.setString(2, oldName);

            int affectedRows1 = pstmt1.executeUpdate();
            int affectedRows2 = pstmt2.executeUpdate();

            // Only commit if both updates are successful
            if (affectedRows1 == 1 && affectedRows2 >= 0) {
                conn.commit(); // Commit transaction
                SearchControl.getInstance().getAllKitchenStyles().remove(oldName);
                SearchControl.getInstance().getAllKitchenStyles().add(newName);
                return true;
            } else {
                conn.rollback(); // Rollback transaction
            }
        } catch (SQLException e) { 
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on error
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
                if (pstmt2 != null) {
                    pstmt2.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit to true
                    conn.close();
                }
            } catch (SQLException finalEx) {
                finalEx.printStackTrace();
            }
        }
        return false;
    }



    // Retrieve all kitchen styles from the database
    public List<String> getAllKitchenStyles() {
        List<String> styles = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_SEL_KITCHEN_STYLES);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                styles.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return styles;
    }

    // Search for a kitchen style in the database
    public List<String> searchKitchenStyles(String searchTerm) {
        List<String> styles = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_SEARCH_KITCHEN_STYLES)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    styles.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return styles;
    }
}
