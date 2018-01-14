package com.github.krlgit.lms;

import java.util.List;

import com.github.krlgit.lms.BookDescription;

public interface Book {

	// how to implement this redirection in a more generic way? does it even make sense here? 
	public static BookDescription.Builder with() {
		return new BookDescription.Builder();
	}

	public Barcode barcode();

	public BookDescription description();

	public boolean isCirculating();

	public Patron lastOwner();
	
	public List<Patron> circulationHistory();

	boolean isAtCapacity(int MAX_CIRCULATION_HISTORY_SIZE);
}
