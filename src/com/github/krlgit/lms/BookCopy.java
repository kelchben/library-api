package com.github.krlgit.lms;

import java.util.ArrayList;
import java.util.List;

class BookCopy {
	private final Barcode barcode;
	private final BookDescription description;
	private final List<Patron> circulationHistory;
	private final boolean isCirculating;


	BookCopy(BookDescription descr, Barcode barcode) {
		this.barcode = barcode;
		this.description = descr;
		this.circulationHistory = new ArrayList<>();
		this.isCirculating = false;
	}
	
	final boolean isCirculating() {
		return isCirculating;
	}

	final boolean appendToCirculationHistory(Patron patron) {
		return circulationHistory.add(patron);
	}
	
	final Barcode barcode() {
		return barcode;
	}

}
