import com.github.krlgit.lms.*;

public class LibraryTest {

	public static void main(String[] args) {

	Library l = Library.create();

	// try this
	l.registerPatron()
	.username("bla")
	.birthdate(2012,5,3)
	.submit();

	l.add(new Patron.Builder()
			.username("krl")
			.firstName("Karl")
			.lastName("Misak")
			.birthdate(2012, 7, 1)
			.build()
			);

	l.add(new BookDescription.Builder()   // new 
			.isbn(new Isbn())
			.title("Holla")
			.author("Miau")
			.build()  // submit
			);


	}
}
