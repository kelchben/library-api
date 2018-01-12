package com.github.krlgit.lms;

import java.util.HashSet;
import java.util.Set;

// TODO refactor this to an interface
public class BookEntry {

	private final BookDescription description;
	private final Set<BookCopy> copies;
	private final int requests;


	final static BookEntry from(BookDescription description) {
		return new BookEntry(description);
	} 


	private BookEntry(BookDescription description) {
		this.description = description;
		copies = new HashSet<>();
		requests = 0;
	}


	// TODO remove this?
	final boolean hasAvailableCopy() {
		for (BookCopy copy : copies) {
			if (!(copy.isCirculating()))
					return true;
		}
		return false;
	}


	final BookCopy getCopy() {
		for (BookCopy copy : copies) {
			if (!(copy.isCirculating()))
					return copy;
		}
		throw new IllegalStateException("No copy of " + description.title() + " is available.");
	}


	final boolean removeCopy(BookCopy copy) {
		return copies.remove(copy);
	}


}

