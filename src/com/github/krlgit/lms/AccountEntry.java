package com.github.krlgit.lms;

import java.util.HashSet;
import java.util.Set;

class AccountEntry {

	// username should be here, conceptually? grr...
	private final Patron patron;
	private final Set<BookCopy> currentlyBorrowed;
	private final Set<Isbn> requests;  // does this belong here, conceptually?

	AccountEntry(Patron patron) {
		this.patron = patron;
		this.currentlyBorrowed = new HashSet<>();
		this.requests = new HashSet<>();
	}

	public final AccountEntry borrowBook(String isbn) {
		return this;
	}

	public final AccountEntry returnBook(String isbn) {
		return this;
	}

	public final AccountEntry requestBook(String string) {
		return this;
	}

}
