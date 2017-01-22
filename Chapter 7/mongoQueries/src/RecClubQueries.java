import java.math.BigDecimal;
import java.util.Scanner;

public class RecClubQueries {
    Scanner scanner = new Scanner(System.in);

    public static void main(String[ ] args) {
	new RecClubQueries().demo();
    }

    private void demo() {
	RecClubCRUD rcc = new RecClubCRUD();

	// test the 'R' in CRUD
	System.out.println("\n");
	System.out.println("\ngetAll customers:");
	pause();
	rcc.getAll("customers");
	pause();

	System.out.println("\ngetAll activities:");
	pause();
	rcc.getAll("activities");
	pause();

	System.out.println("\ngetAll sessions:");
	pause();
	rcc.getAll("sessions"); 
	pause();

	System.out.println("\ngetOne session");
	pause();
	rcc.getOne(70);
	pause();

	System.out.println("\ngetOne activity regex");
	pause();
	rcc.getOneRegex("do$"); // activity name that ends with 'do'
	pause();
	
	// test the 'C' in CRUD
	rcc.createCustomer(9876, "JamesGosling");
	System.out.println("Revised customers:");
	pause();
	rcc.getAll("customers");
	pause();

	// test the 'U' in CRUD
	rcc.updateActivity("go", 9999999.99F);
	System.out.println("Revised cost of go:");
	pause();
	rcc.getAll("activities");
	pause();

	// test the 'D' in CRUD
	rcc.delete("JamesGosling");
	System.out.println("JamesGosling deleted:");
	pause();
	rcc.getAll("customers");
    }

    private void pause() {
	System.out.println("Press Enter to continue...");
	scanner.nextLine();
    }
}
