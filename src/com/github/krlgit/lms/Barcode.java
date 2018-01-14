package com.github.krlgit.lms;

// TODO refactor: Barcodes should just be strings?
public final class Barcode {
	// final byte type // (patron or book)
	// final byte libraryId;
	// final int bookId;  
	private final Isbn isbn;
	private final int copyId;   // use byte, short?

	public static Barcode from(String barcode) {
		if (barcode.length() > 13 + 10 ) { // max digits: 13 (isbn) + 10 (int copyId) 
			throw new  IllegalArgumentException(
					"Input too long (" + barcode.length() + " digits of 23 allowed). Input was: \n" + barcode);
		}

		String[] param = barcode.split(":");

		try {
		Isbn isbn = Isbn.from(param[0]);
		int copyId = Integer.valueOf(param[1]);

		return new Barcode(isbn, copyId);

		} catch(RuntimeException e) {
			throw new IllegalArgumentException(
					"[" + barcode + "] is not a valid Barcode [Isbn isbn : int copyId]");
		}
	}

	public static Barcode from(Isbn isbn, int copyId) {
		return new Barcode(isbn, copyId);
	}

    private Barcode(Isbn isbn, int copyId) {
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
