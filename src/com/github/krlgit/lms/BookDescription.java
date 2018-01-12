package com.github.krlgit.lms;

// TODO move Builder named "addBook()"..."submit() o.a. to Library class and make constructor package private
public class BookDescription {

		private final Isbn isbn;
		private final String title;
		private final String author;

		public static class Builder {
			// Required parameters
			private Isbn isbn;
			private String title;
			private String author;
			// Optional parameters initialization
			// Ex: private String summary = "No Summary available.";
			
			public Builder() {  }

			public Builder isbn(Isbn isbn) { this.isbn = isbn; return this; }
			public Builder title(String title) { this.title = title; return this; }
			public Builder author(String author) { this.author = author; return this; }

			public BookDescription build() {
				return new BookDescription(this);
			}

		}

	    BookDescription(Builder builder) {
			isbn = builder.isbn;
			title = builder.title;
			author = builder.author;
		}

		// Q: should these return defensive copies?
		public Isbn isbn() { return isbn; }
		public String title() { return title; }
		public String author() { return author; }

		@Override
		public String toString() {
			return "Isbn: " + isbn + "%nTitle: " + title + "%nAuthor: " + author;
			// TODO use reflection to loop over all fields?
		}

}