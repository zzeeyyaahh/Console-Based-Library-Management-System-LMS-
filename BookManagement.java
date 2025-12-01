import java.sql.*;
import java.util.Scanner;

public class BookManagement {
    private Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n--- Book Management ---");
            System.out.println("1. Add Book");
            System.out.println("2. Search Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Update Book");
            System.out.println("5. Track Copies");
            System.out.println("6. Display All Books");
            System.out.println("7. Back");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            try {
                switch (choice) {
                    case 1: addBook(); break;
                    case 2: searchBook(); break;
                    case 3: deleteBook(); break;
                    case 4: updateBook(); break;
                    case 5: trackCopies(); break;
                    case 6: displayBooks(); break;
                    case 7: return; // Exit to main menu
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // 1. Add Book
    private void addBook() throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO Books(title, author, isbn, category, total_copies, available_copies) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        System.out.print("Title: "); ps.setString(1, sc.nextLine());
        System.out.print("Author: "); ps.setString(2, sc.nextLine());
        System.out.print("ISBN: "); ps.setString(3, sc.nextLine());
        System.out.print("Category: "); ps.setString(4, sc.nextLine());
        System.out.print("Total copies: ");
        int total = sc.nextInt(); sc.nextLine();

        ps.setInt(5, total);
        ps.setInt(6, total);

        ps.executeUpdate();
        conn.close();
        System.out.println("Book added successfully!");
    }

    // 2. Search Book
    private void searchBook() throws Exception {
        Connection conn = DBConnection.getConnection();

        System.out.println("Search by: ");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.println("4. Category");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt(); sc.nextLine();

        String column = "";
        switch (choice) {
            case 1: column = "title"; break;
            case 2: column = "author"; break;
            case 3: column = "isbn"; break;
            case 4: column = "category"; break;
            default: 
                System.out.println("Invalid choice."); 
                conn.close(); 
                return;
        }

        System.out.print("Enter search keyword: ");
        String keyword = sc.nextLine();

        String sql = "SELECT * FROM Books WHERE " + column + " LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();

        printBookTable(rs);

        conn.close();
    }

    // 3. Delete Book
    private void deleteBook() throws Exception {
        Connection conn = DBConnection.getConnection();
        System.out.print("Enter Book ID to delete: ");
        int id = sc.nextInt(); sc.nextLine();

        String sql = "DELETE FROM Books WHERE book_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int rows = ps.executeUpdate();
        conn.close();

        if (rows > 0) System.out.println("Book deleted successfully!");
        else System.out.println("Book not found.");
    }

    // 4. Update Book
    private void updateBook() throws Exception {
        Connection conn = DBConnection.getConnection();
        System.out.print("Enter Book ID to update: ");
        int bookId = sc.nextInt(); sc.nextLine();

        System.out.print("Enter new Title: ");
        String title = sc.nextLine();
        System.out.print("Enter new Author: ");
        String author = sc.nextLine();
        System.out.print("Enter new ISBN: ");
        String isbn = sc.nextLine();
        System.out.print("Enter new Category: ");
        String category = sc.nextLine();
        System.out.print("Enter total copies: ");
        int totalCopies = sc.nextInt(); sc.nextLine();

        String sql = "UPDATE Books SET title=?, author=?, isbn=?, category=?, total_copies=?, available_copies=? WHERE book_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, author);
        ps.setString(3, isbn);
        ps.setString(4, category);
        ps.setInt(5, totalCopies);
        ps.setInt(6, totalCopies); // reset available copies to match total
        ps.setInt(7, bookId);

        int rows = ps.executeUpdate();
        conn.close();

        if (rows > 0) System.out.println("Book updated successfully!");
        else System.out.println("Book not found.");
    }

    // 5. Track Copies
    private void trackCopies() throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT book_id, title, total_copies, available_copies, " +
                     "(total_copies - available_copies) AS issued FROM Books";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        if (!rs.isBeforeFirst()) {
            System.out.println("No books available in the system.");
            conn.close();
            return;
        }

        System.out.printf("%-5s %-25s %-8s %-10s %-8s%n", 
                          "ID", "Title", "Total", "Available", "Issued");
        System.out.println("------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-5d %-25s %-8d %-10d %-8d%n",
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies"),
                    rs.getInt("issued"));
        }

        conn.close();
    }

    // 6. Display All Books
    private void displayBooks() throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Books";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        printBookTable(rs);

        conn.close();
    }

    // Utility: Print books in table format
    private void printBookTable(ResultSet rs) throws SQLException {
        if (!rs.isBeforeFirst()) {
            System.out.println("No books found.");
            return;
        }

        System.out.printf("%-5s %-25s %-20s %-12s %-15s %-8s %-10s%n", 
                          "ID", "Title", "Author", "ISBN", "Category", "Total", "Available");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-5d %-25s %-20s %-12s %-15s %-8d %-10d%n",
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("category"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies"));
        }
    }
}
