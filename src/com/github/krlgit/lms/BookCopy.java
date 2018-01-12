package com.github.krlgit.lms;

import java.util.ArrayList;
import java.util.List;

class BookCopy {
	private BookDescription description;
	private List<Patron> circulationHistory;
	private boolean isCirculating;

	BookCopy(BookDescription descr) {
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

}
