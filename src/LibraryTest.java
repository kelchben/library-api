import com.github.krlgit.lms.*;
import static java.lang.System.out;

public class LibraryTest {

	public static void main(String[] args) {

		

//Isbns
Isbn myIsbn = Isbn.from("111-1-22-224444-3");					// "-" gets stripped for storage

///Barcodes
Barcode myBarcode = Barcode.from("3-11-345672-4:1");   			// [Isbn isbn:int copyId]

////Usernames

Username myUsername = Username.from("pPlop77");

/////BookDescriptions
	BookDescription myBookDescription = BookDescription.with()  // new BookDescription.Builder()
			.isbn("3-11-345672-4")			     				// uses Isbn.from(String)
			.title("Arbeitsweise einer EDVA")
			.author("KOMBINAT DATENVERARBEITUNG")
			.build();

//////Patrons
	Patron myPatron = Patron.with()								// new Patron.Builder()
			.username("mitsubishi666") 							// uses Username.from(String)
		    .firstName("Arne")
			.lastName("Autobahn")
			.birthdate(2002, 2, 2)  							// birthdate needed for "uniqueness" check 
			.build();               
			


////////////////////Library 

//-------------------------------------------
	Library library = new Library();					
//-------------------------------------------

	out.println("\n#register(Patron) -- add to library database -> returns Username of added Patron:"); out.println(

	// registering existing Patron
	//-------------------------------------------------------------------
			library.register(myPatron)
	//-------------------------------------------------------------------

	);out.println(
			
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

	); out.println("\n#register(BookDescription) -- add to library database -> returns auto-generated Barcode to put on physical BookCopy:" +


	// register Book for existing BookDescription
	//-------------------------------------------------------------------
			library.register(myBookDescription)
	//-------------------------------------------------------------------


	); out.println(

	// combining registering and building a BookDescription
	//-------------------------------------------------------------------
			library.register(BookDescription.with()   						
					.isbn("1-23-456789-X") // ISBN10 or ISBN13 possible
					.title("Holzige Holothurien")
					.author("Hans Hobel")
					.build()
					)
	//-------------------------------------------------------------------

	);


	// silently register some more Books for testing (Book.with() is a shortcut to BookDescription.with())
			library.register(Book.with().isbn("111-1-22-224444-3").title("Brudermord").author("Kain Able").build());
			library.register(Book.with().isbn("1-29-496789-3").title("Waldfeste unter Tage").author("Max Schraat").build());
			library.register(Book.with().isbn("9-29-596789-3").title("Mi mi mi").author("O. Opera").build());
			library.register(Book.with().isbn("333-3-11-440949-6").title("Dampfschifffahrt leicht gemacht").author("Spina Topp").build());


	out.println("\n#registerAdditionalCopy(Isbn) adds another BookCopy to an existing BookEntry --> returns it's Barcode\n" +

	// add more copies for a Book that is already registered in library
	//-------------------------------------------------------------------
			library.registerAdditionalCopy("333-3-11-440949-6")
	//-------------------------------------------------------------------

	);


	out.println("\nAdd 10 additional copies of Book \"123456789X\" to catalog");

    // add some more copies
	for (int i = 0; i < 10; i++) {
		out.println(library.registerAdditionalCopy("1-23-456789-X"));
	}
	

	out.println("\n#ceckoutItem(Barcode) -> returns true on success and false when the patron is at 50 book limit\n");
	out.println("User mitsubishi666 tries to check out Book with barcode 3-11-345672-4:1\n" +

	// checkout a book (referenced by barcode) to a patron
	//-------------------------------------------------------------------
			library.checkoutBook("3-11-345672-4:1", "mitsubishi666")
	//-------------------------------------------------------------------

	);

	out.println("\nUser leiderfort123 tries to borrow the same book (should not be possible, because the barcode is on the physical copy)");

	// exception example - this could be an attempt to check out the same book twice or duplicate barcodes from printer error, etc
	//-------------------------------------------------------------------
		try {
			library.checkoutBook("3-11-345672-4:1", "leiderfort123");
		} catch(IllegalStateException e) {
			out.println("IllegalStateException: " + e.getMessage());
		}
	//-------------------------------------------------------------------


	out.println("\nGet all BookDescriptions that are registered in the library as Map<BookDescription, Book>:\n" +

			// TODO this HAS to use Book class. should use BookEntry implementation class
			// TODO why not return a Map<BookDescription, Map<Book, Patron>> conversion with streams?

	//-------------------------------------------------------------------
			library.getAllBookEntries()
	//-------------------------------------------------------------------

	);

//	for(BookDescription entry : library.getAllBookEntries()) {
//		
//	}

// blabliblu

	}
}

