package com.github.krlgit.lms;

// TODO move Builder named "addBook()"..."submit() o.a. to Library class and make constructor package private
// TODO THIS IS DESIGNED FOR INHERITANCE
public class BookDescription {

		private final Isbn isbn;
		private final String title;
		private final String author;

		public static BookDescription.Builder with() {
			return new BookDescription.Builder();
		}

		public static class Builder {
			// Required parameters
			private Isbn isbn;
			private String title;
			private String author;
			// Optional parameters initialization
			// Ex: private String summary = "No Summary available.";
			
			public Builder() {  }

			public Builder isbn(String str) { 
				this.isbn = Isbn.from(str);
				return this;
			}

			public Builder isbn(Isbn isbn) { 
				this.isbn = isbn;
				return this;
			}

			public Builder title(String title) {
				this.title = title;
				return this;
			}
			public Builder author(String author) {
				this.author = author;
				return this;
			}

			public BookDescription build() {  // create? submit?  --> no. every programmer knows build, this is not for endusers
			if (this.isbn != null) {
				throw new IllegalStateException(
						"Required parameter isbn is missing.");
			}
			if (this.title != null) {
				throw new IllegalStateException(
						"Required parameter title is missing");
			}
			if (this.author != null) {
				throw new IllegalStateException(
						"Required parameter author is missing");
			}
				return new BookDescription(this);
			}

		}

		public BookDescription(Builder builder) {
			isbn = builder.isbn;
			title = builder.title;
			author = builder.author;
		}

		// Q: should these return defensive copies?
		public final Isbn isbn() { return isbn; }
		public final String title() { return title; }
		public final String author() { return author; }

		@Override
		public String toString() {
			return "Isbn: " + isbn + "%nTitle: " + title + "%nAuthor: " + author;
			// TODO use reflection to loop over all fields?
		}

}