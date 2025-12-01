import java.sql.Date;       
import java.time.LocalDate; 
import java.sql.*;
import java.util.*;
import java.time.*;

public class TransactionManagement {
    private Scanner sc = new Scanner(System.in);
    private static final int FINE_PER_DAY = 10;

    public void menu() {
        while (true) {
            System.out.println("\n--- Transaction Management ---");
            System.out.println("1. Issue Book");
            System.out.println("2. Return Book");
            System.out.println("3. View Member Transactions");
            System.out.println("4. Back");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            try {
                switch (choice) {
                    case 1: issueBook(); break;
                    case 2: returnBook(); break;
                    case 3: viewHistory(); break;
                    case 4: return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // 1. Issue Book
    private void issueBook() throws Exception {
        Connection conn = DBConnection.getConnection();

        // Display available books
        String displayBooks = "SELECT book_id, title, author, available_copies FROM Books WHERE available_copies > 0";
        Statement st = conn.createStatement();
        ResultSet rsBooks = st.executeQuery(displayBooks);
        boolean found = false;
        System.out.println("\nAvailable Books:");
        while (rsBooks.next()) {
            if (!found) {
                System.out.printf("%-5s %-25s %-20s %-10s%n", "ID", "Title", "Author", "Available");
                System.out.println("-----------------------------------------------------------");
                found = true;
            }
            System.out.printf("%-5d %-25s %-20s %-10d%n",
                    rsBooks.getInt("book_id"),
                    rsBooks.getString("title"),
                    rsBooks.getString("author"),
                    rsBooks.getInt("available_copies"));
        }
        if (!found) {
            System.out.println("No books available for issue.");
            conn.close();
            return;
        }

        // Get user input
        System.out.print("\nBook ID: "); int bookId = sc.nextInt();
        System.out.print("Member ID: "); int memberId = sc.nextInt(); sc.nextLine();

        // Check member status
        String memCheck = "SELECT name, status FROM Members WHERE member_id=?";
        PreparedStatement psMem = conn.prepareStatement(memCheck);
        psMem.setInt(1, memberId);
        ResultSet rsMem = psMem.executeQuery();
        if (!rsMem.next()) {
            System.out.println("Member not found.");
            conn.close();
            return;
        }
        String memberName = rsMem.getString("name");
        String status = rsMem.getString("status");
        if ("inactive".equalsIgnoreCase(status)) {
            String activate = "UPDATE Members SET status='active' WHERE member_id=?";
            PreparedStatement psAct = conn.prepareStatement(activate);
            psAct.setInt(1, memberId);
            psAct.executeUpdate();
            System.out.println("Member was inactive. Activated now.");
        }

        // Check availability
        String checkSql = "SELECT title, author, available_copies FROM Books WHERE book_id=?";
        PreparedStatement ps1 = conn.prepareStatement(checkSql);
        ps1.setInt(1, bookId);
        ResultSet rs = ps1.executeQuery();
        if (rs.next() && rs.getInt("available_copies") > 0) {
            String bookTitle = rs.getString("title");
            String bookAuthor = rs.getString("author");

            LocalDate today = LocalDate.now();
            LocalDate due = today.plusDays(14); // 2 weeks due date

            String sql = "INSERT INTO Transactions(book_id,member_id,issue_date,due_date) VALUES (?,?,?,?)";
            PreparedStatement ps2 = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps2.setInt(1, bookId);
            ps2.setInt(2, memberId);
            ps2.setDate(3, Date.valueOf(today));
            ps2.setDate(4, Date.valueOf(due));
            ps2.executeUpdate();

            ResultSet genKeys = ps2.getGeneratedKeys();
            int transId = 0;
            if (genKeys.next()) transId = genKeys.getInt(1);

            String updateSql = "UPDATE Books SET available_copies=available_copies-1 WHERE book_id=?";
            PreparedStatement ps3 = conn.prepareStatement(updateSql);
            ps3.setInt(1, bookId);
            ps3.executeUpdate();

            System.out.println("\nBook issued successfully!");
            System.out.println("Transaction ID: " + transId);
            System.out.println("Member: " + memberName + " (ID: " + memberId + ")");
            System.out.println("Book: " + bookTitle + " by " + bookAuthor);
            System.out.println("Due Date: " + due);
        } else {
            System.out.println("Book not available.");
        }
        conn.close();
    }

    // 2. Return Book
    private void returnBook() throws Exception {
        Connection conn = DBConnection.getConnection();
        System.out.print("Transaction ID: "); int transId = sc.nextInt();

        String sql = "SELECT book_id, due_date FROM Transactions WHERE transaction_id=? AND return_date IS NULL";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, transId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int bookId = rs.getInt("book_id");
            LocalDate due = rs.getDate("due_date").toLocalDate();
            LocalDate today = LocalDate.now();
            long daysLate = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(due, today));
            int fine = (int) daysLate * FINE_PER_DAY;

            String upd = "UPDATE Transactions SET return_date=?, fine=? WHERE transaction_id=?";
            PreparedStatement ps2 = conn.prepareStatement(upd);
            ps2.setDate(1, Date.valueOf(today));
            ps2.setInt(2, fine);
            ps2.setInt(3, transId);
            ps2.executeUpdate();

            String updBook = "UPDATE Books SET available_copies=available_copies+1 WHERE book_id=?";
            PreparedStatement ps3 = conn.prepareStatement(updBook);
            ps3.setInt(1, bookId);
            ps3.executeUpdate();

            System.out.println("Book returned. Fine: " + fine);
        } else {
            System.out.println("Invalid or already returned transaction.");
        }
        conn.close();
    }

    // 3. View Member Transactions
    private void viewHistory() throws Exception {
        Connection conn = DBConnection.getConnection();
        System.out.print("Enter Member ID: "); int memberId = sc.nextInt();

        String sql = "SELECT t.transaction_id, b.title, t.issue_date, t.due_date, t.return_date, t.fine " +
                     "FROM Transactions t JOIN Books b ON t.book_id=b.book_id WHERE t.member_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, memberId);
        ResultSet rs = ps.executeQuery();

        boolean found = false;
        while (rs.next()) {
            if (!found) {
                System.out.printf("%-5s %-25s %-12s %-12s %-12s %-6s%n",
                                  "TID", "Book", "Issued", "Due", "Returned", "Fine");
                System.out.println("-------------------------------------------------------------------------");
                found = true;
            }
            System.out.printf("%-5d %-25s %-12s %-12s %-12s %-6d%n",
                    rs.getInt("transaction_id"),
                    rs.getString("title"),
                    rs.getDate("issue_date"),
                    rs.getDate("due_date"),
                    rs.getDate("return_date"),
                    rs.getInt("fine"));
        }

        if (!found) {
            System.out.println("This member has not taken any books.");
        }

        conn.close();
    }
}
