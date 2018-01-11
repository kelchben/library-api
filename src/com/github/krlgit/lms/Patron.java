package com.github.krlgit.lms;

import java.time.DateTimeException;
import java.time.LocalDate;

public class Patron {
	private final Username username;
	private final String firstName;
	private final String lastName;
	private final LocalDate birthdate; 

	public Username username() { return username; }
	public String firstName() { return firstName; }
	public String lastName() { return lastName; }
	public LocalDate birthdate() { return birthdate; } 

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
		Patron p = (Patron) obj;
		return p.firstName().equals(firstName) &&
			   p.lastName().equals(lastName) &&
			   p.birthdate().equals(birthdate) ?
		       true : false;
	}
	
	@Override
	public int hashCode() {
		// TODO
		return 0;
	}
			   
// TODO move Builder named "registerPatron()"..."submit() o.a. to Library class and make constructor package private
	public static class Builder {
		private Username username;
		private String firstName;
		private String lastName;
		private LocalDate birthdate;

		public Builder() { }
		
		// TODO basic input validation needed here?
		public Builder username(String username) { this.username = new Username(username); return this; }
		public Builder firstName(String firstName) { this.firstName = firstName; return this; }
		public Builder lastName(String lastName) { this.lastName = lastName; return this; }
		// TODO exception chaining may not be very usefull here? validate all parameters in build() instead
		public Builder birthdate(int year, int month, int day) throws IllegalArgumentException { 
			try {
				birthdate = LocalDate.of(year, month, day);
			} catch(DateTimeException cause) {
				throw new IllegalArgumentException(cause);
			}
			return this;
		}
		
		public Patron build() {
			// TODO validation for all parameters
			return new Patron(this);
		}
	}

	private Patron(Builder builder) {
		username = builder.username;
		firstName = builder.firstName;
		lastName = builder.lastName;
		birthdate = builder.birthdate;
	}

}
