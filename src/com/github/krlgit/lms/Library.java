package com.github.krlgit.lms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Library {
    private static final int MAX_TIMES_BORROWED = 50;
	public static final int BOOKS_ALLOWED_PER_PATRON = 10;

	private final Map<Isbn, BookEntry> catalog;
	private final Map<Username, AccountEntry> accounts;

	// TODO make configuration a non static class an pass to Library constructor

// CREATE ------------------------------------------------------>

	public static final BookDescription.Builder buildBook() {
		return new BookDescription.Builder();
	}

	public static final Patron.Builder buildPatron() {
		return new Patron.Builder();
	}

	public Library() {
		this.catalog = new HashMap<>();
		this.accounts = new HashMap<>();
	}

// REGISTER ----------------------------------------------------->


	public final BookEntry register(BookDescription description) {
		Isbn isbn = description.isbn();

		if (catalog.containsKey(isbn)) {
			throw new IllegalArgumentException("A Entry with Isbn " + isbn + " already exists in " + catalog);
		}

		catalog.put(isbn, BookEntry.from(description));
		return catalog.get(isbn); // TODO avoid unnecessary double query. Readability? :(
	}


    // TODO overload with extra bool for "unsave" adding (without Patron==Patron check)
	public final AccountEntry register(Patron unregisteredPatron) {
		Username username = unregisteredPatron.username();

		if (accounts.containsKey(username)) {
			throw new IllegalArgumentException("Username: " + username + " is already taken.");
		}

		// check if patron already exists with different username (see patron.equals())
		// -- costly? maybe CHECK database periodically instead?
		for (Map.Entry<Username, AccountEntry> entry : accounts.entrySet()) {
			Patron p = entry.getValue().patron();
			if (p.equals(unregisteredPatron)) {
				throw new IllegalArgumentException("This Patron is already registered with Username: " + p.username());
			}
		}

		accounts.put(username, AccountEntry.from(unregisteredPatron));
		return accounts.get(username);  // TODO avoid unnecessary double query vs Readability? :(
	}



// QUERY -------------------------------------------------->


	// overload "lookup" ? lookupAccount? does this even work without static library?
	public final AccountEntry account(String username) {
		return account(Username.from(username));
	}
	
	public final BookEntry book(String isbn) {
		return book(Isbn.from(isbn));
	}

	private AccountEntry account(Username usr) throws IllegalArgumentException {
		try {
		return accounts.get(usr);
		} catch(NullPointerException e) {
			throw new IllegalArgumentException("Username: " + usr + " not found.");
		}
	}

	private BookEntry book(Isbn isbn) throws IllegalArgumentException {
		try {
		return catalog.get(isbn);
		} catch(NullPointerException e) {
			throw new IllegalArgumentException("Isbn: " + isbn + " not found.");
		}
	}

// BORROW / RETURN / REQUEST ---------------------------------------------------->

	public final BookCopy borrow(String username, String isbn) {
		AccountEntry account = account(username);

		if (account.isAtCapacity(BOOKS_ALLOWED_PER_PATRON)) {
			throw new IllegalStateException(
					"User: " + username + " is at capacity (" + BOOKS_ALLOWED_PER_PATRON + " Books allowed).");
		}

		BookEntry entry = book(Isbn.from(isbn));  // uglaaaay!
	//TODO try? or hasCopy?	
		BookCopy copy = entry.getCopy();

		account.add(copy);
		copy.appendToCirculationHistory(account.patron());
		return copy;
	}

}


