package com.github.krlgit.lms;

/**
 * This class describes the <b>immutable properties of a "Book", not 
 * a physical copy</b>. 
 * <p> 
 * It is needed for the creation of *real* Book representations,
 * that takes place when a Book is registered with the Library.
 * <p>
 * <i>In Theory</i> this is meant to be <b>subclassed</b>,
 * to add more Properties that can be optionals,
 * for example:<br>
 * publisher, publication date, version, summary, etc.
 * This functionality is currently untested and unsupported.
 * <p>
 * BE AWARE THOUGH, to keep any subclasses immutable and to follow 
 * the rule: the properties defined here have to fit <b>every</b> possible 
 * representation of the described. Not allowed would be something
 * in the likes of <i>color, smell,... </i> (obviously). But also properties like
 * <i>numberOfPages</i> could be a border case.
 *
 * @see Library#register(BookDescription)
 */
public class BookDescription {

		private final Isbn isbn;
		private final String title;
		private final String author;

		/**
		 * Initiates a <b>static Builder</b> for a BookDescription Object.
		 * <p>
		 * This Method is the entry point for adding a Book to the Library.
		 * <p> 
		 * Currently, all provided parameters are mandatory but 
		 * can be set in random order before building the BookDescription.<br>
		 * <b>Beware:</b> no input validations are performed, 
		 * except for Isbn, which can be provided as Isbn Object or String.
		 * <p>
		 * Example usage:
         * <p>
		 * <code>
		 * BookDescription.with()<br>
			.title("Arbeitsweise einer EDVA")<br>
			.author("KOMBINAT DATENVERARBEITUNG")<br>
			.isbn("3-11-345672-4")<br>
			.build();
           </code>
		 * <p>
		 *
		 * @return a BookDescription.Builder to initiate object construction
		 */
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

			/**
			 * <b>Mandatory</b><br>
			 * Set before build()
			 * @param str  ISBN10 or ISBN13 as String
			 * @return  the BookDescription.Builder Object
			 */
			public Builder isbn(String str) { 
				this.isbn = Isbn.from(str);
				return this;
			}

			/**
			 * <b>Mandatory</b><br>
			 * Set before build()
			 * @param isbn  an Isbn Object
			 * @return  the BookDescription.Builder Object
			 */
			public Builder isbn(Isbn isbn) { 
				this.isbn = isbn;
				return this;
			}

			/**
			 * <b>Mandatory</b><br>
			 * Set before build()
			 * @param title  a String that represents the books title
			 * @return  the BookDescription.Builder Object
			 */
			public Builder title(String title) {
				this.title = title;
				return this;
			}

			/**
			 * <b>Mandatory</b><br>
			 * Set before build()
			 * @param author  a String that represents the books author
			 * @return  the BookDescription.Builder Object
			 */
			public Builder author(String author) {
				this.author = author;
				return this;
			}



			/**
			 * <b>Creates a BookDescription Object</b>, if all mandatory parameters
			 * are set. 
			 * <p>
			 * Throws an IllegalStateException, indicating which parameter is missing.
			 * 
			 * @return  a BookDescription Object
			 * @throws IllegalStateException when not all mandatory parameters are set
			 */
			public BookDescription build() {  //TODO refactor to... create? submit?  --> no. every programmer knows build, this is not for endusers

			if (isbn == null) {
				throw new IllegalStateException(
						"Required parameter isbn is missing.");
			}

			if (title == null) {
				throw new IllegalStateException(
						"Required parameter title is missing");
			}

			if (author == null) {
				throw new IllegalStateException(
						"Required parameter author is missing");
			}

				return new BookDescription(this);
			}

		}

		private BookDescription(Builder builder) {
			isbn = builder.isbn;
			title = builder.title;
			author = builder.author;
		}


// Q: should these return defensive copies?

		/** 
		 * Simple Getter.
		 * @return  the Isbn of BookDescription
		 */
		public final Isbn isbn() {
			return isbn;
		}

		/** 
		 * Simple Getter.
		 * @return  the title String of BookDescription
		 */
		public final String title() {
			return title;
		}

		/** 
		 * Simple Getter.
		 * @return  the author String of BookDescription
		 */
		public final String author() {
			return author;
		}

		@Override
		public String toString() {
			return "Isbn: " + isbn + "\nTitle: " + title + "\nAuthor: " + author;
			// TODO use reflection to loop over all future fields? bad idea...?
		}

}