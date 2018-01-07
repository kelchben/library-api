package library;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Main {

	public static final Holder LIBRARY = new Holder("LIBRARY"); 

	public static void main(String[] args) {
	

	Book b2 = new Book("Zerfleddert & Gut", "Q. Used", "0999897999999", 2, LIBRARY);
	Book b1 = new Book("Holzige Holothurien", "P.P. Blop", "1234567891234", LIBRARY);
	Holder klaus = new Holder("Klaus Hertz");
	Holder kron = new Holder("Kron Kegel");
	Holder keks = new Holder("Keks Krumel");

	keks.borrowItem(b1);
	keks.returnItem(b1);
	klaus.borrowItem(b1);
	klaus.borrowItem(b1);
	klaus.borrowItem(b2);
	klaus.returnItem(b2);
	klaus.borrowItem(b2);
	klaus.returnItem(b2);
	klaus.borrowItem(b2);
	klaus.borrowItem(b2);
	kron.borrowItem(b2);

	// package-private mutable companion class example
	StringBuilder s = new StringBuilder();
	/* use in loops for better performance/memory profile
	 * otherwise JVM creates a StringBuilder for each loop!
	 * BUT probably optimized in most scenarios
	 */
	for (int i = 0; i < 1000; i++) {
		s.append(i).append(" -> ");
	}

	/* Mutable, but very small state space -> TODO use this for book, to make book immutable?
	 * (not reusable)
	 */
	CountDownLatch c = new CountDownLatch(10);
	c.countDown();
	System.out.println(c);

	List<Object> l = new LinkedList<>();

	}

}
