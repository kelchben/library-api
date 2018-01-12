import com.github.krlgit.lms.*;

public class LibraryTest {

	public static void main(String[] args) {

	Library myLib = new Library();

	// try this
	Patron p =  Library.buildPatron()
			.username("bla")
			.birthdate(2012,5,3)
			.build();

	myLib.register(p);

	myLib.requestItem(isbn, username);
	myLib.checkoutItem(barcode, username);
	myLib.returnItem(barcode);

	myLib.listAllBooks();
	myLib.whoHas(isbn);
	myLib.timesBorrowed(isbn);

	myLib.lookupAccount(p.username()).checkout(barcode);


	myLib.register(new Patron.Builder() 
			.username("krl")
			.firstName("Karl")
			.lastName("Misak")
			.birthdate(2012, 7, 1)
			.build()
			);

	myLib.register(new BookDescription.Builder()  
			.isbn(Isbn.from("123456789X"))
			.title("Holla")
			.author("Miau")
			.build()  
			);


	}
}
