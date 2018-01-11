package com.github.krlgit.lms;

import java.util.HashMap;
import java.util.Map;

public class Library {

	private final Map<Isbn, BookEntry> catalog;
	private final Map<Username, AccountEntry> accounts;

	public static final Library create() {
		return new Library();
	}

	private Library() {
		this.catalog = new HashMap<>();
		this.accounts = new HashMap<>();
	}
	
	public final AccountEntry patron(String username) {
		return null;  // search patron
	}

	// returns package private Account Entry -> it's public methods can be used here!
	public final AccountEntry add(Patron patron) {

		if (accounts.containsKey(patron.username())) {
			throw new IllegalArgumentException("A User named" + patron.username() + "already exists.");
		}

		// TODO check if patron already exists with different username (see special patron.equals())
		// -- costly? CHECK data periodically!

		// TODO OR: AUTOGEN USERNAME from name + date! ugly but save?
		// TODO OR: AUTOGEN USERID and use as key... ?
		// TODO OR: why not use Patron as key? hard to search because whole Patron object has to be passed everytime?

		AccountEntry userAccount = new AccountEntry(patron);
		accounts.put(patron.username(), userAccount);
		return userAccount;
	}

	public final BookEntry add(BookDescription description) {
		return new BookEntry(description);
	}


}
