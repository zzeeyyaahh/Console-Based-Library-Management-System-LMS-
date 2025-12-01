import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BookManagement bm = new BookManagement();
        MemberManagement mm = new MemberManagement();
        TransactionManagement tm = new TransactionManagement();
        ReportsManagement rm = new ReportsManagement();

        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Book Management");
            System.out.println("2. Member Management");
            System.out.println("3. Transaction Management");
            System.out.println("4. Reports & Analytics");
            System.out.println("5. Exit");
            System.out.print("Enter your choice:");
            
            int choice = sc.nextInt();
            switch (choice) {
                case 1: bm.menu(); break;
                case 2: mm.menu(); break;
                case 3: tm.menu(); break;
                case 4: rm.menu(); break;
                case 5: System.exit(0);
                default: System.out.println("Invalid choice!");
            }
        }
    }
}
