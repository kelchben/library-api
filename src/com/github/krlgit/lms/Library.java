package com.github.krlgit.lms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO use Barcode as Key instead of Isbn (Isbn is inside...)?
// but... how to handle BookEntries with no existing copies? special barcode?

public class Library {

	// TODO configuration should be passed to Library instance instead of static
    private static final int TIMES_BORROWED_BEFORE_REMOVAL = 50;
	private static final int BOOKS_ALLOWED_PER_PATRON = 10;
	private static final int REQUESTS_FOR_AQUISITION = 5;
	private static final int REQUESTS_FOR_RESTOCKING = 2;

	private final Map<Isbn, BookEntry> catalog;
	private final Map<Username, AccountEntry> accounts;
	private final Set<Isbn> shoppingList;

//=============================================================================//
//                                   API                                       //
//=============================================================================//




// CREATE ------------------------------------------------------>

	public Library() {
		this.catalog = new HashMap<>();
		this.accounts = new HashMap<>();
		this.shoppingList = new HashSet<>();
	}

// REGISTER ----------------------------------------------------->

	public final Username register(Patron patron) {
		return createEntry(patron)
			   .patron()
			   .username();
	}

	public final Barcode register(BookDescription descr) {
		return createEntry(descr)
			   .addBookCopy()
			   .barcode();
	}

	public final Barcode registerAdditionalCopy(Isbn isbn) {
		return fetchEntry(isbn)
				.addBookCopy()
				.barcode();
	}


// CHECKOUT / RETURN / REQUEST ----------------------------------->

//	public final boolean checkoutItem(String barcode, String username) {
//		return checkoutItem(Barcode.from(barcode), Isbn.from(barcode));
//	}


	public final boolean checkoutItem(Barcode barcode, Username username) 
			throws IllegalArgumentException {

		AccountEntry account = fetchEntry(username);

		if (account.isAtCapacity(BOOKS_ALLOWED_PER_PATRON)) {
		return false;  // checkout failed: account at limit
		}

		BookCopy copy = fetchEntry(barcode.isbn())
			  	        .getCopyWith(barcode);

		if (copy.isCirculating()) {   // this should not be possible (duplicate barcode)
			throw new IllegalStateException(    
					"The copy " + barcode + " is not available. Borrowed by Patron " + copy.lastOwner().username());
		}

		copy.setIsCirculating(true) 
		.appendCirculationHistory(account.patron());
	
		account.add(copy);

		return true; // checkout succeded
	}


	public final boolean returnItem(Barcode barcode) {
		BookCopy copy = fetchEntry(barcode.isbn())
			        	.getCopyWith(barcode)
			            	.setIsCirculating(false);

		AccountEntry account = fetchEntry(copy.lastOwner().username())
							   .remove(copy);

		if (copy.isAtCapacity(TIMES_BORROWED_BEFORE_REMOVAL)) {
			fetchEntry(barcode.isbn())
			.removeCopy(copy)
			.setRequestsNeeded(REQUESTS_FOR_RESTOCKING);
			return false;  // book removed from system
		}

		return true;
	}


	public final boolean requestExistingItem(Isbn isbn, Username username) {
		Patron patron = fetchEntry(username).patron();
		BookEntry entry = fetchEntry(isbn);

		 if ( !shoppingList.contains(isbn) && 
			  !entry.hasAvailableCopy() &&  
			   entry.addToRequests(patron) == entry.requestsNeeded() ) { // addToRequests returns requests.size( 

			entry.clearRequests();
			shoppingList.add(isbn);  
			return true; 
		 }

		 return false;
	}

	public final boolean requestNewItem(BookDescription description, Username username) {
		try {
		 createEntry(description)  // throws IllegalArgumentException when isbn already in system
		.setRequestsNeeded(REQUESTS_FOR_AQUISITION)
		.addToRequests(fetchEntry(username).patron());  // set takes care of duplicates
		 return true;  

		} catch(IllegalArgumentException e) {
			return false; // request failed
		}
	}


// QUERY ----------------------------------------------------------------->

	/* 
	 *  Placeholder for Methods getting Patrons, Books. 
	 *  Patron<->AccountEntry should be refactored/merged first, with appropiate Interface
	 */


// *SPECIAL QUERIES DEMANDED BY HOMEWORK ASSIGNMENT* ----------------------------->


	public final List<BookEntry> getAllBookEntries() {  
//		return catalog.values();  // bad, new collection is backed by map (map can be changed)!
		return new ArrayList<BookEntry>(catalog.values());  // devensive copy vs performance? 
	}


	public final Map<Barcode, Patron> getCurrentOwners(Isbn isbn) {
		Map<Barcode, Patron> owners = new HashMap<>();

		for (BookCopy copy : fetchEntry(isbn).copies()) {
			if (copy.isCirculating()) {
				owners.put(copy.barcode(), copy.lastOwner());
			}
		}
		return owners;
	}


	public final List<Patron> getCirculationHistory(Barcode barcode) {
		return fetchEntry(barcode.isbn())
				.getCopyWith(barcode)
				.circulationHistory();

	}


	public final List<Patron> getRequestsList(Isbn isbn) {
		return fetchEntry(isbn)
				.requests();
	}


	public final int getRequestsAsInt(Isbn isbn) {
		return getRequestsList(isbn)
				.size();
	}


	@SuppressWarnings("unchecked") 
	public final List<Book> getAllCopies(Isbn isbn) {  
		return (List<Book>)(List<?>)fetchEntry(isbn).copies(); // hacky unchecked double cast - find a better way with generics

	}

// STRING ADAPTERS FOR CONVENIENCE --------------------------------------------------->


	public final Barcode registerAdditionalCopy(String isbn) { return registerAdditionalCopy(Isbn.from(isbn)); }
	public final boolean checkoutItem(String barcode, String username) { return checkoutItem(Barcode.from(barcode), Username.from(username)); }
	public final boolean returnItem(String barcode) { return returnItem(Barcode.from(barcode)); }
	public final boolean requestExistingItem(String isbn, String username) { return requestExistingItem(Isbn.from(isbn), Username.from(username)); }
	public final boolean requestNewItem(BookDescription description, String username) { return requestNewItem(description, Username.from(username)); }
	public final Map<Barcode, Patron> getCurrentOwners(String isbn) { return getCurrentOwners(Isbn.from(isbn)); }
	public final List<Patron> getCirculationHistory(String barcode) { return getCirculationHistory(Barcode.from(barcode)); }
	public final List<Book> getAllCopies(String isbn) { return getAllCopies(Isbn.from(isbn)); }
	public final List<Patron> getRequestsList(String isbn) { return getRequestsList(Isbn.from(isbn)); }
	public final int getRequestsAsInt(String isbn) { return getRequestsAsInt(Isbn.from(isbn)); }

	//=============================================================================//
	//                            IMPLEMENTATION                                   //
	//=============================================================================//


	// CREATE -------------------------------------------------------->

	// REGISTER ------------------------------------------------------>

	private final BookEntry createEntry(BookDescription description) {
		Isbn isbn = description.isbn();

		if (catalog.containsKey(isbn)) {
			throw new IllegalArgumentException(
					"An Entry with Isbn " + isbn + " already exists in " + catalog);
		}

		catalog.put(isbn, BookEntry.from(description));
		return catalog.get(isbn); // TODO avoid unnecessary double query. Readability? :(
	}


	// TODO overload with extra bool for "unsave" adding (without Patron==Patron check)
	private final AccountEntry createEntry(Patron unregisteredPatron) {
		Username username = unregisteredPatron.username(); 

		if (accounts.containsKey(username)) {
			throw new IllegalArgumentException(
					"Username: " + username + " is already taken.");
		}

		// check if patron already exists with different username (see patron.equals())
		// -- costly? maybe CHECK database periodically instead?
		for (Map.Entry<Username, AccountEntry> entry : accounts.entrySet()) {
			Patron p = entry.getValue().patron();
			if (p.equals(unregisteredPatron)) {
				throw new IllegalArgumentException(
						"This Patron is already registered with Username: " + p.username());
			}
		}

		accounts.put(username, AccountEntry.from(unregisteredPatron));
		return accounts.get(username);  // TODO avoid unnecessary double query vs Readability? :(
	}


	// CHECKOUT / RETURN / REQUEST ---------------------------------------------------->


	// TODO this is wrong conceptually: first find a book, then return copy, THEN borrow 

	// QUERY -------------------------------------------------->


	// overload "lookup" ? lookupAccount? does this even work without static library?
	private final AccountEntry fetchEntry(Username usr) {
		try {
			return accounts.get(usr);
		} catch(NullPointerException e) {
			throw new IllegalArgumentException(
					"Username: " + usr + " not found.");
		}
	}

	private final BookEntry fetchEntry(Isbn isbn) {
		try {
			return catalog.get(isbn);
		} catch(NullPointerException e) {
			throw new IllegalArgumentException(
					"Isbn: " + isbn + " not found.");
		}
	}


	private final Patron fetchUser(String username) {
		return fetchEntry(Username.from(username)).patron();
	}

	private final BookDescription fetchBookDescription(String isbn) {
		return fetchEntry(Isbn.from(isbn)).bookDescription();
	}

}


