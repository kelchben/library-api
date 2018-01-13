package com.github.krlgit.lms;

import java.util.HashSet;
import java.util.Set;

// TODO refactor this to an interface
public final class AccountEntry {

	// username should be here, conceptually? grr...
	private final Patron patron;
	private final Set<BookCopy> currentlyBorrowed;
	// private final Set<Isbn> requests;

	static final AccountEntry from(Patron patron) {
		return new AccountEntry(patron);
	}

	private AccountEntry(Patron patron) {
		this.patron = patron;
		this.currentlyBorrowed = new HashSet<>();
	}

	public final Patron patron() {
		return patron;
	}


	final boolean isAtCapacity(int BOOKS_ALLOWED) {
		return currentlyBorrowed.size() >= BOOKS_ALLOWED;
	}

	final AccountEntry add(BookCopy copy) { 
		currentlyBorrowed.add(copy);
		return this;
	}

	final AccountEntry remove(BookCopy copy) {
		currentlyBorrowed.add(copy);
		return this;
	}

	@Override
	public String toString() {
		return "Username: " + patron.username() + " | Currently borrowed Items: " + currentlyBorrowed.size();
		//TODO this prbly should be only username
	}

// TODO remove? not working like that without dependency on static Collections
//	public final AccountEntry borrowBook(BookEntry book) {
//		
//	}
//
//	public final AccountEntry returnBook(BookEntry book) {
//		return this;
//	}
//
//	public final AccountEntry requestBook(BookEntry book) {
//		return this;
//	}


}
