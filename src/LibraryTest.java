import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.github.krlgit.lms.*;

public class LibraryTest {

	public static void main(String[] args) {

	Library myLib = new Library();
	Library library = new Library();

	// try this
	library.register(User.with().username("xyz").firstName("xxx").submit());

	Patron p = Patron.with()
			.username("Buntspecccht")
			.firstName("Merle")
			.lastName("Spockstadt")
			.birthdate(2012,5,3)
			.create();

	myLib.register(p);

	myLib.register(User.with() 
			.username("krl")
			.firstName("Karl")
			.lastName("Misak")
			.birthdate(2012, 7, 1)
			.create()
			);

	myLib.register(Book.with()
			.isbn(Isbn.from("123456789X"))    
			.title("Holzige Holothurien")
			.author("Hans Hobel")
			.create()  
			);

	myLib.register(Book.with()
			.isbn(Isbn.from("1234567893"))    
			.title("Mi mi mi")
			.author("O. Opera")
			.create()  
			);

	myLib.registerCopy("123456789X");

	myLib.register(Book.with()
			.isbn(Isbn.stripFrom("1111-2222-4444-3")).title("Brudermord").author("Kain Able").build());

	myLib.register(Book.with()
			.isbn(Isbn.stripFrom("3333-1111-4444-6"))
			.title("Dampfschifffahrt leicht gemacht")
			.author("Spina Topp")
			.create()  
			);

	// myLib.checkoutItem("123456789X:2", "krl");

	// TODO this sucks! return arraylist not collection
	// TODO DIRECT STRING ENTRY
	Set<Book> mycopies = myLib.getAllCopies("123456789X");

	BookCopy mycopy = (BookCopy)(mycopies.toArray())[0] ;

	myLib.checkoutItem(mycopy.barcode(), Username.from("krl"));
	myLib.returnItem(mycopy.barcode());


	System.out.println(myLib.checkoutItem(mycopy.barcode(), p.username()));

	
	//mycopies[0].
	for (BookCopy copy : mycopies) {
		System.out.println(copy);
	}

	System.out.println("\nHistory:");
	System.out.println(myLib.getCirculationHistory(mycopy.barcode()));

	}
}
