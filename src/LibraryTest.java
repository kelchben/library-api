import com.github.krlgit.lms.*;
import static java.lang.System.out;

public class LibraryTest {

	private static void o(Object o) {   // bla
		out.println(o);
	}

	public static void main(String[] args) {

		


//Isbns
Isbn someIsbn = Isbn.from("978-3-16-148410-0");			// can be ISBN10 or ISBN13; "-" gets stripped for storage

//Usernames
Username someUsername = Username.from("krl");

//BookDescriptions
	BookDescription someBookDescription = BookDescription.with()  // new BookDescription.Builder()
			.isbn("3-11-345672-4") 							     // uses Isbn.from(String)
			.title("Arbeitsweise einer EDVA")
			.author("KOMBINAT DATENVERARBEITUNG")
			.build();
//Patrons
	Patron somePatron = Patron.with()								// new Patron.Builder()
			.username("mitsubishi666") 							// uses Username.from(String)
		    .firstName("Arne")
			.lastName("Autobahn")
			.birthdate(2002, 2, 2)  							// birthdate needed for "uniqueness" check 
			.build();               
			

	


////////Library 

//-------------------------------------------
	Library library = new Library();					
//-------------------------------------------

	
	o("\n#register(Patron) -- add to library database -> returns Username of added Patron:\n"+


	// registering existing Patron
	//-------------------------------------------------------------------
			library.register(somePatron)
	//-------------------------------------------------------------------


	);
	o(


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
	o("\n#register(BookDescription) -- add to library database -> returns auto-generated Barcode (e.g. to put on physical BookCopy)" +


	// register Book for existing BookDescription
	//-------------------------------------------------------------------
			library.register(someBookDescription)
	//-------------------------------------------------------------------


	);
	o(


	// combining registering and building a BookDescription
	//-------------------------------------------------------------------
			library.register(BookDescription.with()   					// Book.with() can be used too, but this may be subject to change
					.isbn("0-00-000000-X") 							    // Beware: CHECK bit validation is not implemented
					.title("Holzige Holothurien")
					.author("Hans Hobel")
					.build()
					)
	//-------------------------------------------------------------------


	);
	o("\n	*registering more stuff*  \n");


	// registering
			o(library.register(Book.with().isbn("111-1-22-224444-3").title("Brudermord").author("Kain Able").build()));   
			o(library.register(Patron.with().username(someUsername).firstName("Karl-Xaver").lastName("Horstheimer").birthdate(1902, 11, 2).build()));
			o(library.register(Book.with().isbn("1-29-496789-3").title("Waldfeste unter Tage").author("Max Schraat").build()));
			o(library.register(Book.with().isbn("9-29-596789-3").title("Mi mi mi").author("O. Opera").build()));
			o(library.register(Book.with().isbn("333-3-11-440949-6").title("Dampfschifffahrt leicht gemacht").author("Spina Topp").build()));
			o(library.register(Book.with().isbn(someIsbn).title("~Some Book Title~").author("some book author").build()));

			o("\n   * some validation tests *   \n");

			try {
			// wrong format
			library.register(Book.with().isbn(someIsbn + "more").title ("~Some more Book Title~").author("some more book author").build());

			} catch(IllegalArgumentException validationFail) {
				o("IllegalArgumentException:" + validationFail.getMessage());
			}

			try {
			// missing parameter
			library.register(Book.with().title("Incomplete").author("Me").build());

			} catch(IllegalStateException validationFail) {
				o("IllegalStateException:" + validationFail.getMessage());
			}


	//		
	o("\n#registerAdditionalCopy(Isbn) adds another BookCopy to an existing BookEntry --> returns it's Barcode\n" +


	// add more copies for a Book that is already registered in library
	//-------------------------------------------------------------------
			library.registerAdditionalCopyOf(someIsbn)
	//-------------------------------------------------------------------


	);
	o("\n   *  adding 10 additional copies of Book \"0-00-000000-X\" to catalog  *");


    // add some more copies
	for (int i = 0; i < 10; i++) {
		o(library.registerAdditionalCopy("0-00-000000-X"));
	}
	

	o("\n#ceckoutItem(Barcode) -> returns true on success and false when the patron is at 50 book limit");
	o("User mitsubishi666 tries to check out Book with barcode 3-11-345672-4:1\n" +


	// checkout (borrow) a book with it's barcode and the patrons username
	//-------------------------------------------------------------------
			library.checkoutBook("3-11-345672-4:1", "mitsubishi666")
	//-------------------------------------------------------------------


	);
	o("\nUser leiderfort123 tries to borrow the same book (should not be possible, because the barcode is on the physical copy)");


	// exception test - this could be an attempt to check out the same book twice or duplicate barcodes from printer error, etc
	//-------------------------------------------------------------------
		try {

			library.checkoutBook("3-11-345672-4:1", "leiderfort123");

		} catch(IllegalStateException maybeBorrowed) {
			o("IllegalStateException: " + maybeBorrowed.getMessage());
		}
	//-------------------------------------------------------------------


	//
	o("\n#returnBook(Barcode) return book [3-11-345672-4:1] to the library  -> true if succesfull | false if Book is removed from system due to wear and tear... \n" +


	// return a book by scanning it's barcode
	//-------------------------------------------------------------------
			library.returnBook("3-11-345672-4:1")
	//-------------------------------------------------------------------


	);
	o("\n  *running a checkout/return loop for TIMES_BORROWED_BEFORE_REMOVAL (50)*  \n");


	// checkout/return loop till 
	for (int i = 1; i <= Library.TIMES_BORROWED_BEFORE_REMOVAL; i++) {   		
	o(
			" checkout: " + library.checkoutBook("3-11-345672-4:1", "krl") +
			", return: " + library.returnBook("3-11-345672-4:1") +
			" ---- " + i
	);
	}
		 

	o("\n  ... false means: book is removed... \n\n  *trying checkout one more time* ");

			try {
			// this should not work anymore - book was removed
 			library.checkoutBook("3-11-345672-4:1", "krl");

			} catch(IllegalArgumentException mayBeNotFound) {
				o("IllegalArgumentException: " + mayBeNotFound.getMessage());
			}
	

	o("\n#requestExistingBook(Isbn, Username) ---> returns false | true when REQUESTS_FOR_RESTOCKING (2) is reached and the book is moved to a shopping list");
	o("\n   *mitsubishi666 requests 3-11-345672-4*\n" + 


	// it's gone, patrons can now request a new copy with the isbn
	//-------------------------------------------------------------------
			library.requestExistingBook("3-11-345672-4", "mitsubishi666")
	//-------------------------------------------------------------------
		

	);o("\n\n#getRequestsList(Isbn) and #getRequestsAsInt(Isbn) ---> get the number of filed requests for an Isbn \n");
	o("Testing... how often was 3-11-345672-4 requested ?\n Requests: " +
	


	// get a List<Patron> that holds the successful requesters
	//-------------------------------------------------------------------
		library.getRequestsList("3-11-345672-4")
				.size()														// this is the same as getRequestsListAsInt(Isbn)
	//-------------------------------------------------------------------
	

	);
	o("\n try multiple requests from the same patron");


	// multiple requests of same patron should not be counted
	o(library.requestExistingBook("3-11-345672-4", "mitsubishi666"));
	o(library.requestExistingBook("3-11-345672-4", "mitsubishi666"));
	o(library.requestExistingBook("3-11-345672-4", "mitsubishi666"));
	o("Requests:" + library.getRequestsAsInt("3-11-345672-4"));


	o("\n but a request from a different patron should work:");
	o("\n   *krl requests 3-11-345672-4*\n" + 


		library.requestExistingBook("3-11-345672-4", "krl")
			+ "\n returns true if REQUESTS_FOR_RESTOCK (2) is reached -> item added to shopping list, request reset"


	);
	o("Requests:" + library.getRequestsAsInt("3-11-345672-4"));  // should have been reset to zero



/////////////// other


	o("\nGet all Books that are registered in the library:\n" +

			// TODO this HAS to use Book class. should use BookEntry implementation class
			// TODO why not return a Map<BookDescription, Map<Book, Patron>> conversion with streams?

	//-------------------------------------------------------------------
			library.getAllBookEntries()										
	//-------------------------------------------------------------------


	);

//	for(BookDescription entry : library.getAllBookEntries()) {
//		
//	}


	}
}

