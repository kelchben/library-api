package com.github.krlgit.lms;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

// TODO refactor to interface: private class BookEntry implements Book Interface @override?

class BookEntry {

	private final BookDescription description;
	private final Set<BookCopy> copies;
	private final Set<Patron> requests;
	private int requestsNeeded;

	final static BookEntry from(BookDescription description) {
		return new BookEntry(description);
	} 


	private BookEntry(BookDescription description) {
		this.description = description;
		copies = new HashSet<>();
		requests = new HashSet<>() ;
		requestsNeeded = 0;
	}

	public final List<BookCopy> copies() {
		return new ArrayList<BookCopy>(copies);
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

	final BookCopy getCopyWith(Barcode barcode) {
		for (BookCopy copy : copies) {
			if (copy.barcode().equals(barcode)) {
				return copy;
			}
		}
		throw new IllegalArgumentException(
				"No copy of \"" + description.title() + "\" with Barcode: " + barcode + " found.");
	}

	final BookCopy getFirstAvailableCopy() {
		for (BookCopy copy : copies) {
			if (!(copy.isCirculating()))
					return copy;
		}
		throw new IllegalStateException(
				"No copy of " + description.title() + " is available.");
	}

	// should return boolean?
	final BookEntry addCopy() {
		copies.add(new BookCopy(description, generateBarcode()));
		return this;
	}

	final BookEntry removeCopy(BookCopy copy) {
		copies.remove(copy);
		return this;
	}

	final BookDescription bookDescription() {
		return description;
	}

	private final Barcode generateBarcode() {
		return new Barcode(description.isbn(), copies.size() + 1);
	}


	final BookEntry setRequestsNeeded(int n) {
		requestsNeeded = n;
		return this;
	}

	final int requestsNeeded() {
		return Integer.valueOf(requestsNeeded);
	}


	final int addToRequests(Patron patron) {
		requests.add(patron); 
		return requests.size();
	}

	final BookEntry clearRequests() {
		requests.clear();
		return this;
	}

	final List<Patron> requests() {
		return new ArrayList<>(requests);
	}

	@Override
	public String toString() {
		return description.title() + " ISBN: " + description.isbn() + " Copies: " + copies.size();
    //TODO Copies:  available | unavailable
	}

}

