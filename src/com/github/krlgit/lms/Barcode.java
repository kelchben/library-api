package com.github.krlgit.lms;


/**
 * This immutable class provides handling of Barcode Objects that are used as 
 * identifier for individual Book copies. 
 * <p> 
 * A Barcode consists of an Isbn and an int value, that is
 * automatically created on Book registration.
 * <p> 
 * Its format is [Isbn:int], for example: [3333114409496:2]
 * 
 * @author ag08
 */
public final class Barcode {

	private final Isbn isbn;
	private final int copyId; 


	/**
	 * Converts a String to the Barcode type.<br>
	 * The input String has to be of the format <b>[isbn:copyId]</b>, 
	 * with ISBN10 and ISBN13 being allowed and copy Id being an <tt>Integer > 0</tt>.
	 * <p>
	 * Examples:
	 * <p>
	 * <code>
	 * Isbn.from("3333114409496:2");<br>
	 * Isbn.from("3-11-345672-4:1");<br>
	 * Isbn.from("111-1-22-224444-3");
	 * </code>
	 * <p>
	 * <b>Beware:</b> Only basic validations are performed, 
	 * Isbn CHECK digit is ignored. 
	 * 
	 * @param barcode a String following the contract <b>[isbn:copyId]</b>
	 * @return the Barcode Object constructed from the input String
	 * @throws IllegalArgumentException if <code>String.length() > 23 digits</code>
	 * @throws IllegalArgumentException if <code>copyId <= 0</code>
	 * @throws IllegalArgumentException if Isbn validation fails
	 * @see Isbn#from(String)
	 * @see Username#from(String)
	 */
	public static Barcode from(String barcode) {
		if (barcode.length() > 13 + 10 ) { // max digits: 13 (isbn) + 10 (int copyId) 
			throw new  IllegalArgumentException(
					"Input too long (" + barcode.length() + " digits of 23 allowed). Input was: \n" + barcode);
		}

		String[] param = barcode.split(":");

		try {
		Isbn isbn = Isbn.from(param[0]);
		int copyId = Integer.valueOf(param[1]);

		if (copyId <= 0) {
			throw new IllegalArgumentException(
					"copyId can not be <= 0, was: " + copyId);
		}

		return new Barcode(isbn, copyId);

		} catch(RuntimeException e) {
			throw new IllegalArgumentException(
					"[" + barcode + "] is not a valid Barcode [Isbn isbn : int copyId]");
		}
	}

	public static Barcode from(Isbn isbn, int copyId) {
		return new Barcode(isbn, copyId);
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

    private Barcode(Isbn isbn, int copyId) {
		this.isbn = isbn; 
		this.copyId = copyId;
	}



}
