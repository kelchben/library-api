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
    public static final int TIMES_BORROWED_BEFORE_REMOVAL = 50;
	public static final int BOOKS_ALLOWED_PER_PATRON = 10;
	public static final int REQUESTS_FOR_AQUISITION = 5;
	public static final int REQUESTS_FOR_RESTOCKING = 2;


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

	public final Barcode registerNewCopy(Isbn isbn) {
		return fetchEntry(isbn)
				.addBookCopy()
				.barcode();
	}

	//TODO  put all to String adapters in extra section OR make generic adapter?
	// OR only use Strings as input! <----
	public final Barcode registerCopy(String isbn) {
		return registerNewCopy(Isbn.from(isbn));
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

	// TODO move comments to doc
	public final boolean requestExistingItem(Isbn isbn, Username username) {
		Patron patron = fetchEntry(username).patron();
		BookEntry entry = fetchEntry(isbn);

		 if ( !shoppingList.contains(isbn) && 
			  !entry.hasAvailableCopy() &&  
			   entry.addToRequests(patron) == entry.requestsNeeded() ) {  

			entry.clearRequests();
			shoppingList.add(isbn);  
			return true; 
		 }

		 return false;
	}

	public final void requestNewItem(BookDescription description, Username username) {
		 createEntry(description)  // throws IllegalArgumentException when isbn already in system
		.setRequestsNeeded(REQUESTS_FOR_AQUISITION)
		.addToRequests(fetchEntry(username).patron());  // set takes care of duplicates
	}


// QUERY --------------------------------------------------------->

	public final Collection<BookEntry> getAllBookEntries() {
		return catalog.values();
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

	public final Deque<Patron> getCirculationHistory(Barcode barcode) {
		return fetchEntry(barcode.isbn())
				.getCopyWith(barcode)
				.circulationHistory();

	}

	public final Set<Patron> requestsList(Isbn isbn) {
		return fetchEntry(isbn)
				.requests();
	}

	public final int getRequestsAsInt(Isbn isbn) {
		return requestsList(isbn)
				.size();
	}
	public final int getRequestsAsInt(String isbn) {
		return getRequestsAsInt(Isbn.from(isbn));
	}

//	public final List<Book> getAllCopies(Isbn isbn) {  
//		Set<BookCopy> copies = fetchEntry(isbn).copies();
//		return new ArrayList<Book>(copies); // return only interface type Book to client
//	}
//	public final List<Book> getAllCopies(String isbn) {
//		return getAllCopies(Isbn.from(isbn));
//	}

	@SuppressWarnings("unchecked") 
	public final Set<Book> getAllCopies(Isbn isbn) {  
		return (Set<Book>)(Set<?>)fetchEntry(isbn).copies(); // hacky unchecked double cast - find a better way with generics

	}
	public final Set<Book> getAllCopies(String isbn) {
		return getAllCopies(Isbn.from(isbn));
	}

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


