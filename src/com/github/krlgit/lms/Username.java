package com.github.krlgit.lms;

/**
 * This immutable class is a String Wrapper Type for Usernames. In the future it 
 * could provide validation mechanisms, etc.
 * 
 */
public final class Username {
	private final String username;


    public static Username from(String str) {
		return new Username(str);
	}


	private Username(String str) {
		this.username = str;
	}


	public final String username() {  // obsolete because toString is the same?
		return username; 
	}


	@Override
	public String toString() {
		return username;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Username)) return false;
		Username u = (Username) obj;
		return u.username().equals(this.username);  // with or without "this." ? whats more readable?
	}
 

	private int hashCode;  // is caching necessary here - String already caches?

	@Override
	public int hashCode() {   
		int result = hashCode;
		if (hashCode == 0) {
		result = username.hashCode();
		hashCode = result;
		}
		return result;
	}

}
