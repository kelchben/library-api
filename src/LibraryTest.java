import com.github.krlgit.lms.*;

public class LibraryTest {

	public static void main(String[] args) {

	Library myLib = new Library();

	// try this
	Patron p =  Library.patronBuilder()
			.username("bla")
			.birthdate(2012,5,3)
			.build();

	myLib.registerPatron(p);

	myLib.registerPatron(new Patron.Builder() 
			.username("krl")
			.firstName("Karl")
			.lastName("Misak")
			.birthdate(2012, 7, 1)
			.build()
			);

	myLib.registerCopyOf(new BookDescription.Builder()  
			.isbn(Isbn.from("123456789X"))
			.title("Holla")
			.author("Miau")
			.build()  
			);

	

	}
}
