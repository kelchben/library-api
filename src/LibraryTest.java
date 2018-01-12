import com.github.krlgit.lms.*;

public class LibraryTest {

	public static void main(String[] args) {

	Library l = new Library();

	// try this
	Library.buildPatron()
	.username("bla")
	.birthdate(2012,5,3)
	.build();


	l.register(Library.buildPatron()  
			.username("krl")
			.firstName("Karl")
			.lastName("Misak")
			.birthdate(2012, 7, 1)
			.build()
			);

	l.register(Library.buildBook()  
			.isbn(Isbn.from("123456789X"))
			.title("Holla")
			.author("Miau")
			.build()  // submit?
			);
	
	l.account("krl").borrowBook(isbn);

	}
}
