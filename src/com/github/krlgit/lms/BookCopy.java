package com.github.krlgit.lms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Implementation Class
 *
 */
class BookCopy implements Book {
	private final Barcode barcode;
	private final BookDescription description;  // needed? can get this through barcode/isbn with query
	private final Deque<Patron> circulationHistory;
	private boolean isCirculating;

	BookCopy(BookDescription descr, Barcode barcode) {
		this.barcode = barcode;
		this.description = descr;
		this.circulationHistory = new ArrayDeque<>();
		this.isCirculating = false;
	}
	

	final BookCopy setIsCirculating(boolean bool) {
		isCirculating = bool;
		return this;
	}

	final BookCopy appendCirculationHistory(Patron patron) {
		circulationHistory.add(patron);
		return this;
	}

	@Override
	public final Barcode barcode() {
		return barcode;
	}
	
	@Override
	public final BookDescription description() {
		return description;
	}

	public final boolean isCirculating() {
		return isCirculating;
	}

	public final Patron lastOwner() {
		return circulationHistory.peek();
	}

	public final List<Patron> circulationHistory() {
		return new ArrayList<>(circulationHistory);  // defensive copy
	}

	public final boolean isAtCapacity(int MAX_CIRCULATION_HISTORY_SIZE) {  // should this be in caps? parameter is not a constant
		return circulationHistory.size() > MAX_CIRCULATION_HISTORY_SIZE;
	}

	@Override
	public String toString() {
		return "Barcode: " + barcode + " | Title: " + description.title() + 
				" | Times borrowed: " + circulationHistory.size() + " | Circulating: " + isCirculating;
	}

}
