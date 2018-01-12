package com.github.krlgit.lms;

import java.util.HashSet;
import java.util.Set;

// TODO refactor to interface: private class BookEntry implements Book Interface @override?

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

	public final BookCopy addBookCopy() {
		Barcode barcode = generateBarcode();
		BookCopy copy = new BookCopy(description, barcode);

		copies.add(copy);
		return copy;

	}

	public final Set<BookCopy> addBookCopy(int numberOfCopies) {
		Set<BookCopy> freshCopies = new HashSet<>();
		
		for (int i = 0; i < numberOfCopies; i++) {
			freshCopies.add(addBookCopy());
		}

		return freshCopies;
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

	// should return boolean?
	final boolean addCopy() {
		return copies.add(new BookCopy(description, generateBarcode()));
	}

	final boolean removeCopy(BookCopy copy) {
		return copies.remove(copy);
	}

	final BookDescription bookDescription() {
		return description;
	}

	private final Barcode generateBarcode() {
		return new Barcode(description.isbn(), copies.size() + 1);
	}

}

