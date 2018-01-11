package com.github.krlgit.lms;

import java.util.HashSet;
import java.util.Set;

class BookEntry {

	private final BookDescription description;
	private final Set<BookCopy> copies;
	private final int requests;

	BookEntry(BookDescription description) {
		this.description = description;
		copies = new HashSet<>();
		requests = 0;
	}


}

