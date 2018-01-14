package com.github.krlgit.lms;

// TODO this should probably only be a static helper class for validatin and Isbn stored as String
public final class Isbn {
	private final String isbn;

	public static boolean isValid(String str) {
	// TODO implement proper check digit algorithm for ISBN10 and ISBN13
		int length = str.length();
		if ( !( (length == 10 && str.matches("[0-9]+[X|0-9]$")) ||
	    	    (length == 13 && str.matches("[0-9]+")))
	        ) {
			return false;
		}
		return true;
	}

//	public static Isbn stripFrom(String hyphenatedStr) {
//	  String str = hyphenatedStr.replaceAll("-", "");
//	  return from(str);
//	}


	public static Isbn from(String isbn) {
	  String str  = isbn.replaceAll("-", "");  // strip "-"
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
			Isbn other = (Isbn)obj;
			return other.isbn.equals(isbn);
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
