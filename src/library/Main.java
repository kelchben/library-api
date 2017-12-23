package library;

public class Main {

	public static final Holder LIBRARY = new Holder("LIBRARY"); 
	public static final Holder VOID = new Holder("VOID"); // for Items that are not available


	public static void main(String[] args) {
	
	Book b2 = new Book("Zerfleddert & Gut", "Q. Used", "0999897999999", 2, LIBRARY);
	Book b1 = new Book("Holzige Holothurien", "P.P. Blop", "1234567891234", LIBRARY);
	Holder klaus = new Holder("Klaus Hertz");
	Holder kron = new Holder("Kron Kegel");


	klaus.borrowItem(b1);
	klaus.borrowItem(b1);
	klaus.borrowItem(b2);
	klaus.returnItem(b2);
	klaus.borrowItem(b2);
	klaus.returnItem(b2);
	klaus.borrowItem(b2);
	klaus.borrowItem(b2);
	kron.borrowItem(b2);
	}

}
