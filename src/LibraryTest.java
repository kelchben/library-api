import com.github.krlgit.lms.*;
import static java.lang.System.out;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// TODO reduce verbosity - most infos should be in doc

public class LibraryTest {


    private static void o(Object o) {   // bla
        out.println(o);
    }

    public static void main(String[] args) {


//--------------------------------> START HERE <----------------------------------


//Isbns
Isbn someIsbn = Isbn.from("978-3-16-148410-0");            // can be ISBN10 or ISBN13; "-" gets stripped for storage

//Usernames
Username someUsername = Username.from("krl");

//BookDescriptions
BookDescription someBookDescription = BookDescription.with()  // new BookDescription.Builder
                                      .isbn("3-11-345672-4")               // uses Isbn.from(String)
                                      .title("Arbeitsweise einer EDVA")
                                      .author("KOMBINAT DATENVERARBEITUNG")
                                      .build();
//Patrons
Patron somePatron = Patron.with()                           // new Patron.Builder
                    .username("mitsubishi666")              // uses Username.from(String)
                    .firstName("Arne")
                    .lastName("Autobahn")
                    .birthdate(2002, 2, 2)                  // birthdate needed for "uniqueness" check 
                    .build();               
            



//-------------------------------------------
    Library library = new Library();                    
//-------------------------------------------

    
    o("\n#register(Patron) -- add to library -> returns Username of added Patron:\n"+


    // register Patron
    //-------------------------------------------------------------------
            library.register(somePatron)								   // returns Username
    //-------------------------------------------------------------------


    );
    o(


    // combine registering and building
    //-------------------------------------------------------------------
            library.register(Patron.with()                                // init builder
                             .username("leiderfort123")
                             .firstName("Karl")
                             .lastName("Käfer")
                             .birthdate(1983, 7, 1)
                             .build()
                             )
    //-------------------------------------------------------------------


    );
    o("\n#register(BookDescription) -- add to library -> returns auto-generated Barcode (e.g. to put on physical BookCopy)\n" +


    // register Book for existing BookDescription
    //-------------------------------------------------------------------
            library.register(someBookDescription)						   // returns Barcode
    //-------------------------------------------------------------------


    );
    o(


    // combine registering and building a BookDescription
    //-------------------------------------------------------------------
            library.register(BookDescription.with()                       // Book.with() can be used too, but this may be subject to change
                             .isbn("0-00-000000-X")                       // Beware: CHECK bit validation is not implemented
                             .title("Holzige Holothurien")
                             .author("Hans Hobel")
                             .build()
                             )
    //-------------------------------------------------------------------


    );
    o("\n  *registering some more*  \n");

    o(library.register(Book.with().isbn("111-1-22-224444-3").title("Brudermord").author("Kain Able").build()));   
    o(library.register(Patron.with().username(someUsername).firstName("Karl-Xaver").lastName("Horstheimer").birthdate(1902, 11, 2).build()));
    o(library.register(Book.with().isbn("1-29-496789-3").title("Waldfeste unter Tage").author("Max Schraat").build()));
    o(library.register(Book.with().isbn("9-29-596789-3").title("Mi mi mi").author("O. Opera").build()));
    o(library.register(Book.with().isbn("333-3-11-440949-6").title("Dampfschifffahrt leicht gemacht").author("Spina Topp").build()));

    Barcode someBarcode = (library.register(Book.with().isbn(someIsbn).title("~Some Book Title~").author("some book author").build()));

    o("\n   * some validation tests *   \n");


    try {
    // wrong isbn format
    library.register(Book.with()
                     .isbn(someIsbn + "more")
                     .title ("~Some more Book Title~")
                     .author("some more book author")
                     .build());

    } catch(IllegalArgumentException validationFail) {
        o("IllegalArgumentException:" + validationFail.getMessage());
    }

    try {
    // missing parameter
    library.register(Book.with()
                     .title("Incomplete")
                     .author("Me")
                     .build());

    } catch(IllegalStateException validationFail) {
        o("IllegalStateException:" + validationFail.getMessage());
    }


    // silently registering some more Patrons
        String[] zauberUsernames = {"hanscas", "mynpeep", "clachau", "leona", "ludosett"};   // visible for later tests
    {
        String[] firstNames = {"Hans", "Mynheer", "Clawdia", "Leo", "Lodovico"};
        String[] lastNames = {"Castorp", "Peeperkorn", "Chauchat", "Naphta", "Settembrini"};
        int[][] birthdates = { {1889, 4, 1}, {1881, 6, 19}, {1892, 12, 3}, {1870, 12, 12}, {1868, 7, 3} };
        
        for (int i = 0; i < zauberUsernames.length; i++) {
            library.register(Patron.with()
                             .username(zauberUsernames[i])
                             .firstName(firstNames[i])
                             .lastName(lastNames[i])
                             .birthdate(birthdates[i][0], birthdates[i][1], birthdates[i][2])
                             .build());
        }
    }


    //        
    o("\n#registerAdditionalCopy(Isbn) adds another BookCopy to an existing BookEntry --> returns it's Barcode\n" +


    // add more copies for a Book that is already registered in library
    //-------------------------------------------------------------------
            library.registerAdditionalCopyOf(someIsbn)
    //-------------------------------------------------------------------


    );
    o("\n *adding 10 additional copies of Book \"0-00-000000-X\" to catalog*");


    // add some more copies
    for (int i = 0; i < 10; i++) {
        o(library.registerAdditionalCopy("0-00-000000-X"));
    }
    

    o("\n#ceckoutItem(Barcode) -> returns true on success and false when the patron is at 50 book limit");
    o("User mitsubishi666 tries to check out Book with barcode 3-11-345672-4:1\n" +


    // checkout (borrow) a book with it's barcode and the patrons username
    //-------------------------------------------------------------------
            library.checkoutBook("mitsubishi666", "3-11-345672-4:1")
    //-------------------------------------------------------------------


    );
    o("\nUser leiderfort123 tries to borrow the same book (should not be possible, because the barcode is on the physical copy)");


    // exception test - this could be an attempt to check out the same book twice or duplicate barcodes from printer error, etc
    //-------------------------------------------------------------------
        try {

            library.checkoutBook("leiderfort123", "3-11-345672-4:1");

        } catch(IllegalStateException maybeBorrowed) {
            o("IllegalStateException: " + maybeBorrowed.getMessage());
        }
    //-------------------------------------------------------------------


    //
    o("\n#returnBook(Barcode) return book [3-11-345672-4:1] to the library  -> true if succesfull | false if Book is removed from system due to wear and tear... \n" +


    // return a book by scanning it's barcode
    //-------------------------------------------------------------------
            library.returnBook("3-11-345672-4:1")
    //-------------------------------------------------------------------


    );
    o("\n  *running a checkout/return loop for TIMES_BORROWED_BEFORE_REMOVAL (50)*  \n");


    // test removal of books due to wear and tear
    for (int i = 1; i <= Library.TIMES_BORROWED_BEFORE_REMOVAL; i++) {           
    o(
            " checkout: " + library.checkoutBook("krl", "3-11-345672-4:1") +
            ", return: " + library.returnBook("3-11-345672-4:1") +
            " ---- " + i
    );
    }
         

    o("\n  ... false means: book is removed... \n\n  *trying checkout one more time* ");

            try {
            // this should not work anymore - book was removed
             library.checkoutBook("krl", "3-11-345672-4:1");

            } catch(IllegalArgumentException mayBeNotFound) {
                o("IllegalArgumentException: " + mayBeNotFound.getMessage());
            }
    

    o("\n#requestRegisteredBook(Isbn, Username) ---> returns false | true when REQUESTS_FOR_RESTOCKING (2) is reached and the book is moved to a shopping list");
    o("\n   *mitsubishi666 requests 3-11-345672-4*\n" + 


    // patrons can now request a new copy with the isbn
    //-------------------------------------------------------------------
            library.requestRegisteredBook("mitsubishi666", "3-11-345672-4")
    //-------------------------------------------------------------------
        

    );
    o("\n\n#getRequestsList(Isbn) and #getRequestsAsInt(Isbn) ---> get the number of filed requests for an Isbn \n");
    o("Testing... how often was 3-11-345672-4 requested ?\n Requests: " +
    


    // get a List<Patron> that holds the successful requesters
    //-------------------------------------------------------------------
        library.getRequestsList("3-11-345672-4")
                .size()                                                   // 1,  this is the same as getRequestsListAsInt(Isbn)
    //-------------------------------------------------------------------
    

    );
    o("\n try multiple requests from the same patron");


    // multiple requests of same patron should not be counted
    o(library.requestRegisteredBook("mitsubishi666", "3-11-345672-4"));  // false
    o(library.requestRegisteredBook("mitsubishi666", "3-11-345672-4"));  // false
    o(library.requestRegisteredBook("mitsubishi666", "3-11-345672-4"));  // false
    o("Requests:" + library.getRequestsAsInt("3-11-345672-4"));           // still 1


    o("\n but a request from a different patron should work:");
    o("\n   *krl requests 3-11-345672-4*\n" + 

        // another patron requests
        library.requestRegisteredBook("krl", "3-11-345672-4")              // true
            + "\n returns true if REQUESTS_FOR_RESTOCK (2) is reached -> item added to shopping list, request reset"

    );
    o("Requests:" + library.getRequestsAsInt("3-11-345672-4"));  // has been reset to zero
    o("\n#requestUnregisteredBook(Username, BookDescription) --> request new Book (= create entry with 0 copies, returns true if entry was created) \n" +


    // requests for unregistered books have to be submitted with the BookDescription
    //----------------------------------------------------------------------------------------
            library.requestUnregisteredBook("mitsubishi666", BookDescription.with()          // TODO method name is misleading (request vs register)
                                                               .title("Mein Mitsubishi")
                                                               .author("Arne Autobahn")
                                                               .isbn("4-44-444444-4")  
                                                               .build())                    // book is now registered with 0 copies
    //----------------------------------------------------------------------------------------
         

    );
    o("\nNow this Book needs 4 more requests till REQUESTS_FOR_AQUISITION (5) is reached and the isbn is added to the shopping list (true).");
                // TODO would be nicer to still count them for shopping list ordering
    
    // trying if this needs 5 requests 
    for (String username : zauberUsernames)  // 5 
        o(username + " requests... " + library.requestRegisteredBook(username, "4-44-444444-4")); 
    

    o("Requests for a book already on shopping list have no effect -> false"); 
    o("\n#getCirculationHistory(Barcode) --> see how many times a copy was borrowed and by whom");

    
    // borrowing the same book a couple of times...
    for (String str : zauberUsernames) {
        library.checkoutBook(Username.from(str), someBarcode);
        library.returnBook(someBarcode);
    }


    // get a books circulation history 
    //----------------------------------------------------------------------------------------
    List<Patron> someBooksHistory = library.getCirculationHistory(someBarcode);                    // BEWARE: deep copy!
    //----------------------------------------------------------------------------------------


    o(
    // now you can to stuff like
            someBarcode + " has been borrowed " + someBooksHistory.size() + " times:  " + someBooksHistory
    );


    o("\n#getCurrentOwners(Isbn) ---> list all circulating copies of a book with the corresponding Patrons");

    // borrow a book that has multiple copies available
    {
        int copyId = 1;
        for (String username : zauberUsernames) {
        library.checkoutBook(username, "0-00-000000-X:" + copyId);
        copyId++;
        }
    }

    o(
    // get the owners of the circulating copies as Map<Barcode,Patron>
    //---------------------------------------------------------------------
            library.getCurrentOwners("0-00-000000-X")                                 // BEWARE: deep copy!
    //--------------------------------------------------------------------
    );


    o("\n#getAllBookEntries() --> get all Books that are registered in the library:\n");

            // TODO leaking implementation detail, this should not return BookEntry Objects
            // TODO why not return a nested Map<BookDescription, Map<Book, Patron>> ?

    o(
    //-------------------------------------------------------------------
            library.getAllBookEntries()                                        
    //-------------------------------------------------------------------
    );


    // TODO print shopping list


    o("\n *registering 100_000 books and 50_000 patrons --- please wait -- this could take a while*\n");

    // test if system can handle registering a high number of books/patrons
    {
        for (int i = 0; i < 100_000; i++) {
            library.register(BookDescription.with()
                                 .isbn("1234" + Integer.toString(100_000 + i))
                                 .title("Generic Title" + i)
                                 .author("Generic Author" + i)
                                 .build());
        }
        
        for (int i = 0; i < 50_000; i++) {
            library.register(Patron.with()
                                   .username("genuser" + i)
                                   .firstName("Gene" + i)
                                   .lastName("Rated" + i)
                                   .birthdate(i, 4, 1)
                                   .build());
        }
    }
    
    o("done");

    o("\nlet 5000 random patrons checkout random books...\n");

            for (int i = 0; i < 5000; i++) {
                if (i % 20 == 0) out.print("\n");  // linebreaks
                String randUsername = "genuser" + ThreadLocalRandom.current().nextInt(50_000);
                String randBarcode = "1234"
                        + (100_000 + ThreadLocalRandom.current().nextInt(100_000))
                        + ":1";
                try {
                out.print(library.checkoutBook(randUsername, randBarcode) + "|");
                } catch(RuntimeException e) {
                    out.print("An Exception was thrown: " + e.getMessage() + "|");
                }
            }
    }
}

