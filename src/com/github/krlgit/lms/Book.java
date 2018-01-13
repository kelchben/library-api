package com.github.krlgit.lms;

import com.github.krlgit.lms.BookDescription;

public interface Book {

	// how to implement this in a more generic way?
	public static BookDescription.Builder with() {
		return new BookDescription.Builder();
	}

}
