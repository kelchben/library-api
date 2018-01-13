package com.github.krlgit.lms;

public interface Patron {

	public static User.Builder with() {
		return new User.Builder();
	}

}
