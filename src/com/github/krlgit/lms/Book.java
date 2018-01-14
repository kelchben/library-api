package com.github.krlgit.lms;

import java.util.List;

import com.github.krlgit.lms.BookDescription;

/**
 * The Interface Type Book represents the <b>unique copy of a Book</b>.
 * Books could have different underlying formats in the future
 * (e.g. AudioBook, ...)
 * 
 * <p> 
 * <b> THIS CLASS IS CURRENTLY UNDOCUMENTED,<br> FOR DEMONSTRATION PURPOSES ONLY,</br> AND MAY BE SUBJECT TO CHANGE</b>
 * <p>
 * 
 * @author ag08
 */
public interface Book {

	// TODO how to implement this redirection in a more generic way? does it even make sense here? 
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
