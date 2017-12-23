package library;

public class Book extends InventoryItem implements Borrowable {

	public static final int INITIAL_BOOK_DURABILITY = 50;
	public static final int REQUESTS_TILL_RESTOCK = 2;
	public static final int REQUESTS_TILL_NEW = 5;

	private String title;
	private String author;
	private String isbn;

	public Book(String title, String author, String isbn, Holder holder) {
		super(isbn, INITIAL_BOOK_DURABILITY, holder);
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		System.out.println("Created: \"" + this.title + "\" by " + this.author + " in " + holder.toString() + "  ISBN: " + this.isbn);
	}

	// Constructor with durability (e.g. for adding already worn/used books)
	public Book(String title, String author, String isbn, int durability, Holder holder) {
		super(isbn, durability , holder);
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		System.out.println("Created: \"" + this.title + "\" by " + this.author + " in " + holder.toString() + "  ISBN: " + this.isbn);
	}

	@Override
	public void borrowTo(Holder nextHolder) {
		if (holder == owner) { 
			moveTo(nextHolder);
			durability--;  // borrowing reduces books health by 1
			System.out.println(holder.getName() + " borrowed " + title + " | Durability: " + durability);
		} else if (holder == Main.VOID) {
			System.out.println(title + " ist zur Zeit nicht verfügbar");
			durability--;  // negative durability = requests
			this.checkAndRestock();
		} else {
			System.out.println("Das Buch ist gerade ausgeliehen.");
		}
	}

	private void checkAndRestock() {
		if (durability <= -REQUESTS_TILL_NEW) {
			System.out.println("Restocking...");
			moveTo(owner);
			durability = INITIAL_BOOK_DURABILITY;
			System.out.println(title + " added to " + holder.toString());
		}
	}

	@Override
	public void returnToOwner() {
		holder.removeFromInventory(this);
		if (durability == 0) {  // TODO check if LIBRARY and VOID exist?
			moveTo(Main.VOID);
			durability = -Math.abs(REQUESTS_TILL_RESTOCK - REQUESTS_TILL_NEW);  // set to -3 
			System.out.println(this.title + " is too worn now. Moved it to " + holder.getName() + " with Durability " + durability);
		} else { 
			System.out.println(holder.getName() + " returned " + title + " | Durability: " + durability);
			moveTo(owner);
		}
	}

}
