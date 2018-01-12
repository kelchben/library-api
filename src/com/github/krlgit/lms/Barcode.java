package com.github.krlgit.lms;

// TODO refactor: Barcode should not rely on Isbn! Messy?!
class Barcode {
	// final byte type // (patron or book)
	// final byte libraryId;
	// final int bookId;  
	private final Isbn isbn;
	private final int copyId;   // use byte, short?

    Barcode(Isbn isbn, int copyId) {
		this.isbn = isbn; 
		this.copyId = copyId;
	}

    @Override
    public String toString() {
    	return isbn.toString() + ":" + Integer.toString(copyId);
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof Barcode) {
    		Barcode other = (Barcode) obj;
    		return other.isbn().equals(this.isbn)
    		    && other.copyId() == this.copyId;
    	}
    	return false;
    }

    @Override
    public int hashCode() {
    	return copyId + isbn.hashCode();
    }

    public final Isbn isbn() {
    	return isbn;
    }

    public final int copyId() {
    	return copyId;
    }
}
