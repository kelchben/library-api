package com.github.krlgit.lms;

import java.time.DateTimeException;
import java.time.LocalDate;

public final class Patron {
	private final Username username;
	private final String firstName;
	private final String lastName;
	private final LocalDate birthdate; 

// ---- BUILD -------------------------------------->

	public static Patron.Builder with() {
		return new Patron.Builder();
	}

	public static class Builder {  
		private Username username;
		private String firstName;
		private String lastName;
		private LocalDate birthdate;

		public Builder() { }
		
		public Builder username(String username) { 
			this.username = Username.from(username); 
			return this;
			}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
			}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
			}

		// TODO exception chaining may not be very usefull here? validate all parameters in build() instead
		public Builder birthdate(int year, int month, int day) throws IllegalArgumentException { 
			try {
				birthdate = LocalDate.of(year, month, day);
			} catch(DateTimeException cause) {
				throw new IllegalArgumentException(cause);
			}
			return this;
		}
		
		public Patron build() {  // create?
			// TODO validation for all parameters
			return new Patron(this);
		}


	}

//---- CREATE ------------------------------------>

	private Patron(Builder builder) {
		username = builder.username;
		firstName = builder.firstName;
		lastName = builder.lastName;
		birthdate = builder.birthdate;
	}

//---- GET/SET ----------------------------------->

	public Username username() { return username; }
	public String firstName() { return firstName; }
	public String lastName() { return lastName; }
	public LocalDate birthdate() { return birthdate; } 


//---- OTHER ------------------------------------->

	/*
	 * check only based on (effectively) immutable Information
	 * from the Patrons ID. Username ommited!
	 * 
	 * - DOES THIS VIOLATE EQUALS CONTRACT?
	 * - should it be possible for a Patron to have different usernames? (there could be different libraries)
	 * - should username be in AccountEntry? Created from Patron fields?
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Patron)) return false;	
		Patron other = (Patron) obj;
		return other.firstName().equals(firstName) &&
			   other.lastName().equals(lastName) &&
			   other.birthdate().equals(birthdate) ?
		       true : false;
	}
	

	private int hashCode;  // auto inits to 0

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = firstName.hashCode();
			result = 31 * result + lastName.hashCode();
			result = 31 * result + birthdate.hashCode();
			hashCode = result;
		}
		return result;
	}
			   
	@Override
	public String toString() {
		return username + " (" + firstName + " " + lastName + ")";
	}


}
