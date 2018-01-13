package com.github.krlgit.lms;

import com.github.krlgit.lms.BookDescription;

public interface Book {

	public static BookDescription.Builder with() {
		return new BookDescription.Builder();
	}

}
