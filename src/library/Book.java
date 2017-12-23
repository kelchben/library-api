package library;

public class Book extends InventoryItem implements Borrowable {

	private static int INIT_BOOK_DURABILITY = 50;
	private static final int REQUESTS_TILL_RESTOCK = 2;
	private static final int REQUESTS_TILL_NEW = 5;

	private String title;
	private String author;
	private String isbn;

	public Book(String title, String author, String isbn, Owner owner) {
		super(isbn, INIT_BOOK_DURABILITY, owner);
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		System.out.println("Created: \"" + this.title + "\" by " + this.author + " in " + owner.toString() + "  ISBN: " + this.isbn);
	}

	// Constructor with durability (e.g. for adding already worn/used books)
	public Book(String title, String author, String isbn, int durability, Owner owner) {
		super(isbn, durability , owner);
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		System.out.println("Created: \"" + this.title + "\" by " + this.author + " in " + owner.toString() + "  ISBN: " + this.isbn);
	}

	@Override
	public void borrowTo(Owner nextOwner) {
		if (owner == Main.LIBRARY) { 
			this.owner.removeFromInventory(this);
			nextOwner.addToInventory(this);
			durability--;  // borrowing reduces books health by 1
			this.owner = nextOwner;
			System.out.println(owner.getName() + " borrowed " + title + " | Durability: " + durability);
		} else if (owner == Main.VOID) {
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
			Main.LIBRARY.addToInventory(this);   // refactor
			Main.VOID.removeFromInventory(this); // ...
			owner = Main.LIBRARY;				 // this!
			System.out.println(title + "added to " + owner.toString());
		}
	}

	@Override
	public void returnTo(Owner nextOwner) {
		this.owner.removeFromInventory(this);
		if (durability == 0) {  // check if LIBRARY and VOID exist?
			Main.VOID.addToInventory(this);
			owner = Main.VOID;
			durability = -Math.abs(REQUESTS_TILL_RESTOCK - REQUESTS_TILL_NEW);  // set to -3 
			System.out.println(this.title + " is too worn now. Moved it to " + owner.getName() + " with Durability " + durability);
		} else { 
			nextOwner.addToInventory(this);
			System.out.println(owner.getName() + " returned " + title + " | Durability: " + durability);
			this.owner = nextOwner;
		}
	}

}
