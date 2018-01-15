import com.github.krlgit.lms.*;
import static java.lang.System.out;

public class LibraryTest {

	public static void main(String[] args) {

		

// Isbns
Isbn testIsbn = Isbn.from("333-4-444-42");

///// Barcodes
Barcode testBarcode = Barcode.from("333-4-444-42:1");

/////BookDescriptions
	BookDescription testBookDescription = BookDescription.with()
			.title("Arbeitsweise einer EDVA")
			.author("KOMBINAT DATENVERARBEITUNG")
			.isbn("3-11-345672-4")  // only basic isbn-validation implemented (length, type of digits); "-" stripped for storage
			.build();

	Patron testPatron = Patron.with()
			.username("mitsubishi666")
			.firstName("Arne")
			.lastName("Autobahn")
			.birthdate(2002, 2, 2)  // needed for "uniqueness" check 
			.build();               // ---> a Patron cannot register at same library with multiple Usernames
			

///////////////////////////// Library //////////////////////////////////
	Library library = new Library();


// REGISTER 

	out.println("\n#register(Patron) -- add to library database -> returns Username of added Patron:");

	out.println(
	// registering existing Patron
	//-------------------------------------------------------------------
			library.register(testPatron)
	//-------------------------------------------------------------------
			);



	out.println(
	// combining registering and building
	//-------------------------------------------------------------------
			library.register(Patron.with()  // with vs build... ? has to be consistent with Book.buildDescription
					.username("leiderfort123")
					.firstName("Karl")
					.lastName("Käfer")
					.birthdate(1983, 7, 1)
					.build()
					)
	//-------------------------------------------------------------------
			);

	out.println("\n#register(Book) -- add to library database -> returns auto-generated Barcode to put on physical BookCopy:");


	out.println(
	// register existing Book for existing BookDescription
	//-------------------------------------------------------------------
			library.register(testBookDescription)
	//-------------------------------------------------------------------
			);


	out.println(
	// combining registering and building
	//-------------------------------------------------------------------
			library.register(Book.with()   // Book.with() links to BookDescription.with()... messy :(
					.isbn("1-23-456789-X") // ISBN10 or ISBN13 possible
					.title("Holzige Holothurien")
					.author("Hans Hobel")
					.build()
					)
	//-------------------------------------------------------------------
			);

	// silently register some more Books for testing
			library.register(Book.with().isbn("111-1-22-224444-3").title("Brudermord").author("Kain Able").build());
			library.register(Book.with().isbn("1-29-496789-3").title("Waldfeste unter Tage").author("Max Schraat").build());
			library.register(Book.with().isbn("9-29-596789-3").title("Mi mi mi").author("O. Opera").build());
			library.register(Book.with().isbn("333-3-11-440949-6").title("Dampfschifffahrt leicht gemacht").author("Spina Topp").build());



	// extra: add more copies for a BookDescription already in catalog
	out.println("\n#registerAdditionalCopy(Isbn) adds another BookCopy to an existing BookEntry --> returns it's Barcode\n" +
	//-------------------------------------------------------------------
			library.registerAdditionalCopy("333-3-11-440949-6")
	//-------------------------------------------------------------------
			);

	out.println("\nAdd 10 additional copies of Book \"123456789X\" to catalog");
	for (int i = 0; i < 10; i++) {
		out.println(library.registerAdditionalCopy("1-23-456789-X"));
	}
	

// CHECKOUT

	out.println("\n#ceckoutItem(Barcode) -> returns true on success and false when the patron is at 50 book limit\n");

	out.println("User mitsubishi666 tries to check out Book with barcode 3-11-345672-4:1\n" +
	//-------------------------------------------------------------------
			library.checkoutBook("3-11-345672-4:1", "mitsubishi666")
	//-------------------------------------------------------------------
			);

	try {
		out.println("\nUser leiderfort123 tries to borrow the same book (should not be possible, because the barcode is on the physical copy)");
		out.println(
	//-------------------------------------------------------------------
				library.checkoutBook("3-11-345672-4:1", "leiderfort123")
	//-------------------------------------------------------------------
				);
	} catch(IllegalStateException e) {
		out.println("IllegalStateException: " + e.getMessage());
	}


	library.getAllBookEntries();


	}
}

