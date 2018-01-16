package com.github.krlgit.lms;

import com.github.krlgit.lms.BookDescription;

/**
 * Book represents the <b>unique copy of a Book</b>.
 * Books could have different underlying formats in the future.
 * (e.g. AudioBook, ...)
 * <p>
 * (<i>this Interface is what someone who uses the API sees, when internally BookCopy objects are used</i>)
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

//	public boolean isCirculating();

//	public Patron lastOwner();
	
//	public List<Patron> circulationHistory();

//	boolean isAtCapacity(int MAX_CIRCULATION_HISTORY_SIZE);
}
