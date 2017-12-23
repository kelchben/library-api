package library;

public class Main {

	public static final Owner LIBRARY = new Owner("LIBRARY"); 
	public static final Owner VOID = new Owner("VOID"); // for Items that are not available


	public static void main(String[] args) {
	
	Book b2 = new Book("Zerfleddert & Gut", "Q. Used", "0999897999999", 2, LIBRARY);
	Book b1 = new Book("Holzige Holothurien", "P.P. Blop", "1234567891234", LIBRARY);
	Owner klaus = new Owner("Klaus Hertz");


	klaus.borrowItem(b1);
	klaus.borrowItem(b1);
	klaus.borrowItem(b2);
	klaus.returnItem(b2);
	klaus.borrowItem(b2);
	klaus.returnItem(b2);

	}

}
