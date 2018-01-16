package com.github.krlgit.lms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * This class is the heart of the Library Managment System, as it
 * provides the main functionalities of a Library, like
 * <p><ul>
 * <li> registration of Patrons and Books,
 * <li> checking out and reclaiming Books,
 * <li> handling Book requests from Patrons, 
 * <li> various queries 
 * </ul><p> 
 * It uses and connects all classes of this package.
 * 
 * @author ag08
 * 
 */
/**
 * @author princess
 *
 */
public final class Library {


	/**
	 * The number of times ({@value}) a Book can be checked out and returned
	 * before it is removed from the System due to wear and tear.
	 * 
	 * @see checkout
	 * @see return
	 */
	public static final int TIMES_BORROWED_BEFORE_REMOVAL = 50;

	/**
	 * The max number of Books ({@value}) a Patron can have at 
	 * the same time.
	 */
	public static final int BOOKS_ALLOWED_PER_PATRON = 10;

	/**
	 * The number of requests needed ({@value}), till a <b>new</b>
	 * Book gets added to the Libraries shopping list.
	 * 
	 * @see requestExistingBook
	 */
	public static final int REQUESTS_FOR_AQUISITION = 5;

	/**
	 * The number of requests needed ({@value}), till a Book that had 
	 * <b>previously existing</b> copies in the system
	 * gets added to the Libraries shopping list.
	 * 
	 * @see requestNewBook
	 */
	public static final int REQUESTS_FOR_RESTOCKING = 2;

	private final Map<Isbn, BookEntry> catalog;
	private final Map<Username, AccountEntry> accounts;
	private final Set<Isbn> shoppingList;


	/**
	 * Constructs a new, empty Library. 
	 * Configuration in static class constants.
	 * 
	 * @see TIMES_BORROWED_BEFORE_REMOVAL
	 * @see BOOKS_ALLOWED_PER_PATRON
	 * @see REQUESTS_FOR_AQUISITION
	 * @see REQUESTS_FOR_RESTOCKING
	 */
	public Library() {
		this.catalog = new HashMap<>();
		this.accounts = new HashMap<>();
		this.shoppingList = new HashSet<>();
	}


// REGISTER ----------------------------------------------------->

	/** 
	 * Adds the specified Patron to this library if he is not 
	 * already registered. Returns his Username.
	 * 
	 * @param  patron  an unregistered Patron, not null
	 * @return the now registered Patrons Username
	 * @throws IllegalArgumentException if
	 *         patron.username() is already taken
	 * @throws IllegalArgumentException if patron is 
	 *		   already registered with another username
	 * 
	 */
	public final Username register(Patron patron)
			throws IllegalArgumentException {
		return createEntry(patron)
				.patron()
				.username();
	}

	/** 
	 * Adds a Book with the specified BookDescription
	 * to this library if a Book with the same Isbn
	 * is not already registered. 
	 * Generates and returns Barcode of the new Book.
	 * 
	 * @param  description  the BookDescription of the Book to be added, not null
	 * @return the Barcode generated for the added Book
	 * @throws IllegalArgumentException if books
	 *         Isbn is already in system
	 */
	public final Barcode register(BookDescription description) 
			throws IllegalArgumentException {
		return createEntry(description)
				.addBookCopy()
				.barcode();
	}

	/**
	 * Add another Copy of a Book that is already registered with the library. 
	 * Returns the newly generated Barcode for the added Book.
	 * 
	 * @param isbn  the Isbn number of the Book to be added, not null
	 * @return the Barcode generated for the added Book
	 * @throws IllegalArgumentException if books 
	 *         Isbn is not found in system
	 */
	public final Barcode registerAdditionalCopyOf(Isbn isbn) {
		return fetchEntry(isbn)
				.addBookCopy()
				.barcode();
	}


// CHECKOUT / RETURN / REQUEST ----------------------------------->


	/**
	 * Checks out (borrows) a Book from the Library to a Patron,
	 * referenced by it's Barcode and the Patrons Username.
	 * A cross reference is created:
	 * <p><ul>
	 * <li> the Book is added to the Patrons Account
	 * <li> the Patron is added to the Books circulationHistory 
	 * </ul><p>
	 * Returns <tt>true</tt> if the checkout succeded and 
	 * <tt>false</tt> if the checkout fails, due to the Patrons 
	 * Account currently holding the max number of Books allowed 
	 * at the same time ({@value #BOOKS_ALLOWED_PER_PATRON}).
	 * <p>
	 * If a barcode from an already borrowed book is entered,
	 * an IllegalStateException is thrown. 
	 * <p> 
	 * The method changes the state of the Book to 
	 * <code>isCirculating == true.</code>
	 * It can be reversed with {@link #returnBook(Barcode)}.
	 * 
	 * @param barcode  the Barcode of the Book to borrow
	 * @param username  the Username of the involved Patron
	 * @return <tt>true</tt> if Patron is not at {@link BOOKS_ALLOWED_PER_PATRON} limit
	 * @throws IllegalStateException if book.isCirculating == true
	 * @throws IllegalArgumentException if the Barcode is not found
	 * @throws IllegalArgumentException if the Username is not found
	 * @see {@link Book#isCirculating()}
	 * @see {@link Book#circulationHistory()}
	 * @see {@link #returnBook(Barcode)}
	 */
	public final boolean checkoutBook(Barcode barcode, Username username) 
			throws IllegalStateException {

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


	/**
	 * "Returns" borrowed Book with specified Barcode to Library 
	 * (<code>isCirculating=false</code>), or removes it from the system,
	 * when {@link TIMES_BORROWED_BEFORE_REMOVAL} was reached.
	 * <p>
	 * It is removed from the Patrons Account, but a reference to the Patron is
	 * kept in the Books circulationHistory.
	 * 
	 * For Books that get removed from the System by this Method,
	 * the number of User requests needed, before the Library reaquires it 
	 * is {@link REQUESTS_FOR_RESTOCKING} ({@value #REQUESTS_FOR_RESTOCKING}).
	 * 
	 * @param barcode  Barcode of the Book to be returned
	 * @return true, if Book is returned | false, if Book is removed from system
	 * @throws IllegalArgumentException if Barcode is not found in system
	 * @throws IllegalStateException if Book <code>isCirculating==false</code>
	 */
	public final boolean returnBook(Barcode barcode) {
		BookCopy copy = fetchEntry(barcode.isbn()).getCopyWith(barcode);

		if (!copy.isCirculating()) {   // this should not be possible (duplicate barcode)
			throw new IllegalStateException(    
					"The copy " + barcode + " has already been returned.");
		}

				copy.setIsCirculating(false);

		AccountEntry account = fetchEntry(copy.lastOwner().username())
				.remove(copy);  // remove BookCopy from Patrons AccountEntry

		if (copy.isAtCapacity(TIMES_BORROWED_BEFORE_REMOVAL)) {
			fetchEntry(barcode.isbn())
			.removeCopy(copy)
			.setRequestsNeeded(REQUESTS_FOR_RESTOCKING);
			return false;  // BOOK REMOVED FROM SYSTEM (borrowed too often)
		}

		return true;
	}


	/**
	 * CURRENTLY UNDOCUMENTED
	 * 
	 * @param isbn
	 * @param username
	 * @return
	 */
	public final boolean requestExistingBook(Isbn isbn, Username username) {
		Patron patron = fetchEntry(username).patron();
		BookEntry entry = fetchEntry(isbn);

		if (
				   !shoppingList.contains(isbn)  	
				&& !entry.hasAvailableCopy()		
				&& entry.addToRequests(patron) == entry.requestsNeeded()  
				     	// adds patron to requests Set and returns it's size as int
												  

	 	   ) {

			entry.clearRequests();
			shoppingList.add(isbn);  
			return true;   // book will be aquired now
		}

		return false;  //
	}


	/**
	 * CURRENTLY UNDOCUMENTED
	 * 
	 * @param description
	 * @param username
	 * @return
	 */
	public final boolean requestNewBook(BookDescription description, Username username) {
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


	/**
	 *  CURRENTLY UNDOCUMENTED
	 */
	public final List<BookEntry> getAllBookEntries() {  
		//		return catalog.values();  // bad, new collection is backed by map (map can be changed)!
		return new ArrayList<BookEntry>(catalog.values());  // devensive copy vs performance? 
	}


	/**
	 *  CURRENTLY UNDOCUMENTED
	 */
	public final Map<Barcode, Patron> getCurrentOwners(Isbn isbn) {
		Map<Barcode, Patron> owners = new HashMap<>();
		List<BookCopy> copies = fetchEntry(isbn).copies();

		for (BookCopy copy : copies) {
			if (copy.isCirculating()) {
				owners.put(copy.barcode(), copy.lastOwner());
			}
		}
		return owners;
	}


	/**
	 *  CURRENTLY UNDOCUMENTED
	 */
	public final List<Patron> getCirculationHistory(Barcode barcode) {
		return fetchEntry(barcode.isbn())
				.getCopyWith(barcode)
				.circulationHistory();

	}


	/**
	 *  CURRENTLY UNDOCUMENTED
	 */
	public final List<Patron> getRequestsList(Isbn isbn) {
		return fetchEntry(isbn)
				.requests();
	}


	/**
	 * CURRENTLY UNDOCUMENTED
	 */
	public final int getRequestsAsInt(Isbn isbn) {
		return getRequestsList(isbn)
				.size();
	}


	/**
	 * CURRENTLY UNDOCUMENTED
	 */
	@SuppressWarnings("unchecked") 
	public final List<Book> getAllCopies(Isbn isbn) {   // Book hides BookCopy
		return (List<Book>)(List<?>)fetchEntry(isbn).copies(); // hacky unchecked double cast - find a better way with generics

	}

// STRING ADAPTERS FOR CONVENIENCE --------------------------------------------------->

	/**
	*
	* String adapter for convenience; see {@link #registerAdditionalCopyOf(Isbn)} for documentation
	*
	* @param isbn String
	* @return Barcode
	*/
	public final Barcode registerAdditionalCopy(String isbn) { return registerAdditionalCopyOf(Isbn.from(isbn)); }

	/**
	*
	* String adapter for convenience; see {@link #checkoutBook(Barcode, Username)} for documentation
	*
	* @param barcode  String
	* @param username  String
	* @return boolean
	*/
	public final boolean checkoutBook(String barcode, String username) { return checkoutBook(Barcode.from(barcode), Username.from(username)); }

	/**
	*
	* String adapter for convenience; see {@link #returnBook(Barcode)} for documentation
	*
	* @param barcode  String
	* @return boolean
	*/
	public final boolean returnBook(String barcode) { return returnBook(Barcode.from(barcode)); }

	/**
	*
	* String adapter for convenience; see {@link #requestExistingBook(Isbn, Username)} for documentation
	*
	* @param isbn  String
	* @param   String
	* @return boolean
	*/
	public final boolean requestExistingBook(String isbn, String username) { return requestExistingBook(Isbn.from(isbn), Username.from(username)); }

	/**
	*
	* String adapter for convenience; see {@link #requestNewBook(BookDescription, Username)} for documentation
	*
	* @param description  BookDescription
	* @param username  String
	* @return boolean
	*/
	public final boolean requestNewBook(BookDescription description, String username) { return requestNewBook(description, Username.from(username)); }

	/**
	*
	* String adapter for convenience; see {@link #getCurrentOwners(Isbn)} for documentation
	*
	* @param isbn String
	* @return Map<Barcode, Patron>
	*/
	public final Map<Barcode, Patron> getCurrentOwners(String isbn) { return getCurrentOwners(Isbn.from(isbn)); }

	/**
	*
	* String adapter for convenience; see {@link #getCirculationHistory(Barcode)} for documentation
	*
	* @param barcode  String
	* @return List<Patron>
	*/
	public final List<Patron> getCirculationHistory(String barcode) { return getCirculationHistory(Barcode.from(barcode)); }

	/**
	*
	* String adapter for convenience; see {@link #getAllCopies(Isbn)} for documentation
	*
	* @param isbn  String
	* @return List<Book>
	*/
	public final List<Book> getAllCopies(String isbn) { return getAllCopies(Isbn.from(isbn)); }

	/**
	*
	* String adapter for convenience; see {@link #getRequestsList(Isbn)} for documentation
	*
	* @param isbn String
	* @return List<Patron>
	*/
	public final List<Patron> getRequestsList(String isbn) { return getRequestsList(Isbn.from(isbn)); }

	/**
	*
	* String adapter for convenience; see {@link #getRequestsAsInt(Isbn)} for documentation
	*
	* @param isbn  String
	* @return int requests
	*/
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
		AccountEntry entry = accounts.get(usr);
		if (entry == null) {
			throw new IllegalArgumentException(
					"Username: " + usr + " not found.");
		}
		return entry;
	}

	private final BookEntry fetchEntry(Isbn isbn) {
		BookEntry entry = catalog.get(isbn);
		if (entry == null) {
			throw new IllegalArgumentException(
					"Isbn: " + isbn + " not in catalog.");
		}
		return entry;
	}


	private final Patron fetchUser(String username) {
		return fetchEntry(Username.from(username)).patron();
	}

	private final BookDescription fetchBookDescription(String isbn) {
		return fetchEntry(Isbn.from(isbn)).bookDescription();
	}

}


