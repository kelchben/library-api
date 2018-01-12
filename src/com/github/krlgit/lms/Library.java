package com.github.krlgit.lms;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

// TODO REFACTOR EVERYTHING: use Barcode as Key (Isbn is inside...) :(

public class Library {
	// TODO configuration should be passed to Library instance instead of static
    public static final int TIMES_BORROWED_BEFORE_REMOVAL = 50;
	public static final int BOOKS_ALLOWED_PER_PATRON = 10;
	public static final int REQUESTS_FOR_AQUISITION = 5;
	public static final int REQUESTS_FOR_RESTOCKING = 2;


	private final Map<Isbn, BookEntry> catalog;
	private final Map<Username, AccountEntry> accounts;

	private final Deque<Isbn> shoppingList;


//=============================================================================//
//                                   API                                       //
//=============================================================================//


// CREATE ------------------------------------------------------>

	public static final BookDescription.Builder bookBuilder() {
		return new BookDescription.Builder();
	}

	public static final Patron.Builder patronBuilder() {
		return new Patron.Builder();
	}

	public Library() {
		this.catalog = new HashMap<>();
		this.accounts = new HashMap<>();
		this.shoppingList = new ArrayDeque<>();
	}

// REGISTER ----------------------------------------------------->

	public final Barcode registerCopyOf(BookDescription descr) {
		return createEntry(descr)
			   .addBookCopy()
			   .barcode();
	}

	public final Username registerPatron(Patron patron) {
		return createEntry(patron)
			   .patron()
			   .username();
	}

// CHECKOUT / RETURN / REQUEST ----------------------------------->

	public final boolean checkoutItem(Barcode barcode, Username username) 
			throws IllegalArgumentException {

		AccountEntry account = fetchEntry(username);

		if (account.isAtCapacity(BOOKS_ALLOWED_PER_PATRON)) {
		return false;  // checkout failed: account at limit
		}

		BookCopy copy = fetchEntry(barcode.isbn())
			  	        .getCopyWith(barcode);

		if (copy.isCirculating()) {
			throw new IllegalStateException(
					"The copy " + barcode + " is not available. Borrowed by User " + copy.lastOwner().username());
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

		 if ( !entry.hasAvailableCopy() &&  // books can only be requested when there's no copy available
			   entry.addToRequests(patron) == entry.requestsNeeded() ) {  // addToRequests returns int 

			entry.clearRequests();
			shoppingList.push(isbn);
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

//=============================================================================//
//                            IMPLEMENTATION                                   //
//=============================================================================//


// CREATE -------------------------------------------------------->

// REGISTER ------------------------------------------------------>

	private final BookEntry createEntry(BookDescription description) {
		Isbn isbn = description.isbn();

		if (catalog.containsKey(isbn)) {
			throw new IllegalArgumentException(
					"A Entry with Isbn " + isbn + " already exists in " + catalog);
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
	private final AccountEntry account(String username) {
		return fetchEntry(Username.from(username));
	}

	private final AccountEntry fetchEntry(Username usr) {
		try {
		return accounts.get(usr);
		} catch(NullPointerException e) {
			throw new IllegalArgumentException(
					"Username: " + usr + " not found.");
		}
	}

	private final BookEntry book(String isbn) {
		return fetchEntry(Isbn.from(isbn));
	}

	private final BookEntry fetchEntry(Isbn isbn) {
		try {
		return catalog.get(isbn);
		} catch(NullPointerException e) {
			throw new IllegalArgumentException(
					"Isbn: " + isbn + " not found.");
		}
	}


}


