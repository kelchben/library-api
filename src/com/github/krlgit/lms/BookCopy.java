package com.github.krlgit.lms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.SortedSet;
import java.util.List;
import java.util.Set;

class BookCopy {
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
	
	final boolean isCirculating() {
		return isCirculating;
	}

	final BookCopy setIsCirculating(boolean bool) {
		isCirculating = bool;
		return this;
	}

	final BookCopy appendCirculationHistory(Patron patron) {
		circulationHistory.add(patron);
		return this;
	}

	final Barcode barcode() {
		return barcode;
	}

	final Deque<Patron> circulationHistory() {
		return new ArrayDeque<>(circulationHistory);  // defensive copy
	}

	final Patron lastOwner() {
		return circulationHistory.peek();
	}

	final boolean isAtCapacity(int MAX_CIRCULATION_HISTORY_SIZE) {  // should this be in caps? parameter is not a constant
		return circulationHistory.size() >= MAX_CIRCULATION_HISTORY_SIZE;
	}

}
