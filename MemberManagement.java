import java.sql.*;
import java.util.Scanner;

public class MemberManagement {
    private Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n--- Member Management ---");
            System.out.println("1. Add Member");
            System.out.println("2. Update Member Info");
            System.out.println("3. Deactivate Member");
            System.out.println("4. Display All Members");
            System.out.println("5. Delete Member");
            System.out.println("6. Back");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt(); 
            sc.nextLine(); // consume newline

            try {
                switch (choice) {
                    case 1: addMember(); break;
                    case 2: updateMember(); break;
                    case 3: deactivateMember(); break;
                    case 4: displayMembers(); break;
                    case 5: deleteMember(); break;
                    case 6: return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // 1. Add Member
    private void addMember() throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO Members(name, member_type, contact_info, status) VALUES (?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        System.out.print("Name: "); 
        ps.setString(1, sc.nextLine());
        System.out.print("Type (student/teacher/staff): "); 
        ps.setString(2, sc.nextLine());
        System.out.print("Contact: "); 
        ps.setString(3, sc.nextLine());
        ps.setString(4, "active");

        int rows = ps.executeUpdate();

        if (rows > 0) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int memberId = rs.getInt(1);
                System.out.println("Member added successfully! \nAssigned Member ID: " + memberId);
            }
        } else {
            System.out.println("Failed to add member.");
        }

        conn.close();
    }

    // 2. Update Member
    private void updateMember() throws Exception {
        Connection conn = DBConnection.getConnection();

        System.out.print("Enter Member ID: "); 
        int id = sc.nextInt(); sc.nextLine();

        System.out.print("New contact info: "); 
        String contact = sc.nextLine();

        String sql = "UPDATE Members SET contact_info=? WHERE member_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, contact);
        ps.setInt(2, id);

        int rows = ps.executeUpdate();
        conn.close();

        if (rows > 0) 
            System.out.println("Member updated!");
        else 
            System.out.println("Member not found.");
    }

    // 3. Deactivate Member
    private void deactivateMember() throws Exception {
        Connection conn = DBConnection.getConnection();

        System.out.print("Enter Member ID to deactivate: ");
        int id = sc.nextInt(); sc.nextLine();

        String sql = "UPDATE Members SET status='inactive' WHERE member_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int rows = ps.executeUpdate();
        conn.close();

        if (rows > 0) 
            System.out.println("Member deactivated.");
        else 
            System.out.println("Member not found.");
    }

    // 4. Display All Members
    private void displayMembers() throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Members";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        boolean found = false;

        while (rs.next()) {
            if (!found) {
                // Print table header once
                System.out.printf("%-5s %-20s %-12s %-25s %-10s%n", 
                                  "ID", "Name", "Type", "Contact", "Status");
                System.out.println("-------------------------------------------------------------------------------");
                found = true;
            }

            // Print row
            System.out.printf("%-5d %-20s %-12s %-25s %-10s%n",
                    rs.getInt("member_id"),
                    rs.getString("name"),
                    rs.getString("member_type"),
                    rs.getString("contact_info"),
                    rs.getString("status")
            );
        }

        if (!found) {
            System.out.println("No members available in the system.");
        }

        conn.close();
    }

    // 5. Delete Member (permanent)
    private void deleteMember() throws Exception {
        Connection conn = DBConnection.getConnection();

        System.out.print("Enter Member ID to delete: ");
        int id = sc.nextInt(); sc.nextLine();

        String sql = "DELETE FROM Members WHERE member_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int rows = ps.executeUpdate();
        conn.close();

        if (rows > 0) 
            System.out.println("Member deleted successfully.");
        else 
            System.out.println("Member not found.");
    }
}
