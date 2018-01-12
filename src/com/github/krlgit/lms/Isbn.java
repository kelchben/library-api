package com.github.krlgit.lms;

public class Isbn {
	private final String isbn;

	public static boolean isValid(String str) {
	// TODO implement proper check digit algorithm for ISBN10 and ISBN13
		int length = str.length();
		if ( !( (length == 10 && str.matches("[0-9]+X")) ||
	    	    (length == 13 && str.matches("[0-9]+")))
	        ) {
			return false;
		}
		return true;
	}

	public static Isbn stripFrom(String hyphenatedStr) {
	  String str = hyphenatedStr.replaceAll("-", "");
	  return from(str);
	}


	public static Isbn from(String str) throws IllegalArgumentException {
		if (isValid(str)) {
			return new Isbn(str);
		}
		throw new IllegalArgumentException(str + " can not be converted to Isbn.") ;
	}


	private Isbn(String str) {
		this.isbn = str;
	}


	@Override
	public String toString() {
		return isbn; 
	}


	@Override
	public boolean equals(Object obj) {
		// if (obj == this)   // does this line make any difference?
		// 	return true;
		if (obj instanceof Isbn) {
			return isbn == ((Isbn)obj).toString();
		}
		return false;
	}

	
	private int hashCode; // TODO benchmark with and without caching

	@Override
	public int hashCode() {
		int result = hashCode;
		if (hashCode == 0) {
		result = isbn.hashCode();
		}
		return result;
	}

}
