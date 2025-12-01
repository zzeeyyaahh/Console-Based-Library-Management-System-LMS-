import java.sql.*;
import java.util.Scanner;

public class ReportsManagement {
    private Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n--- Reports & Analytics ---");
            System.out.println("1. Books Report");
            System.out.println("2. Members Report");
            System.out.println("3. Transactions Report");
            System.out.println("4. Back");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            try {
                switch (choice) {
                    case 1: booksReport(); break;
                    case 2: membersReport(); break;
                    case 3: transactionsReportMenu(); break;
                    case 4: return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // === Books Report ===
    private void booksReport() throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT book_id,title,author,category,total_copies,available_copies FROM Books";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.println("\n--- Books Report ---");
        boolean found = false;

        while (rs.next()) {
            if (!found) {
                System.out.printf("%-5s %-25s %-20s %-15s %-8s %-10s %-8s%n",
                        "ID", "Title", "Author", "Category", "Total", "Available", "Issued");
                System.out.println("-----------------------------------------------------------------------------------");
                found = true;
            }
            int issued = rs.getInt("total_copies") - rs.getInt("available_copies");
            System.out.printf("%-5d %-25s %-20s %-15s %-8d %-10d %-8d%n",
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies"),
                    issued);
        }

        if (!found) {
            System.out.println("No books found.");
        }
        conn.close();
    }

    // === Members Report ===
    private void membersReport() throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT member_id,name,member_type,status FROM Members";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.println("\n--- Members Report ---");
        boolean found = false;

        while (rs.next()) {
            if (!found) {
                System.out.printf("%-5s %-20s %-12s %-10s%n",
                        "ID", "Name", "Type", "Status");
                System.out.println("---------------------------------------------------");
                found = true;
            }
            System.out.printf("%-5d %-20s %-12s %-10s%n",
                    rs.getInt("member_id"),
                    rs.getString("name"),
                    rs.getString("member_type"),
                    rs.getString("status"));
        }

        if (!found) {
            System.out.println("No members found.");
        }
        conn.close();
    }

    // === Transactions Report Menu ===
    private void transactionsReportMenu() throws Exception {
        while (true) {
            System.out.println("\n--- Transaction Reports ---");
            System.out.println("1. All Transactions");
            System.out.println("2. Daily Report (today)");
            System.out.println("3. Weekly Report (last 7 days)");
            System.out.println("4. Monthly Report (last 30 days)");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1: transactionsReport("ALL"); break;
                case 2: transactionsReport("DAILY"); break;
                case 3: transactionsReport("WEEKLY"); break;
                case 4: transactionsReport("MONTHLY"); break;
                case 5: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    // === Transactions Report ===
    private void transactionsReport(String filter) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT t.transaction_id, b.title, m.name, t.issue_date, t.due_date, t.return_date, t.fine " +
                     "FROM Transactions t " +
                     "JOIN Books b ON t.book_id=b.book_id " +
                     "JOIN Members m ON t.member_id=m.member_id ";

        switch (filter) {
            case "DAILY":
                sql += "WHERE t.issue_date = CURDATE()";
                break;
            case "WEEKLY":
                sql += "WHERE t.issue_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                break;
            case "MONTHLY":
                sql += "WHERE t.issue_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
                break;
            default: break; // ALL
        }

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.println("\n--- Transactions Report (" + filter + ") ---");
        boolean found = false;

        while (rs.next()) {
            if (!found) {
                System.out.printf("%-5s %-25s %-20s %-12s %-12s %-12s %-6s%n",
                        "TID", "Book", "Member", "Issued", "Due", "Returned", "Fine");
                System.out.println("-----------------------------------------------------------------------------------");
                found = true;
            }
            System.out.printf("%-5d %-25s %-20s %-12s %-12s %-12s %-6d%n",
                    rs.getInt("transaction_id"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getDate("issue_date"),
                    rs.getDate("due_date"),
                    rs.getDate("return_date"),
                    rs.getInt("fine"));
        }

        if (!found) {
            System.out.println("No transactions found.");
        }
        conn.close();
    }
}
