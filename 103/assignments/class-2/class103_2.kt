// Step 1: Define the base class
open class Book(
    val title: String,
    val author: String,
    private var available: Boolean = true
) {
    open fun displayInfo() {
        println("Title: $title, Author: $author, Available: $available")
    }

    open fun borrow() {
        if (available) {
            available = false
            println("You borrowed: $title")
        } else {
            println("Sorry, $title is currently unavailable.")
        }
    }

    open fun returnBook() {
        available = true
        println("You returned: $title")
    }
}

// Step 2: Define the EBook subclass
class EBook(
    title: String,
    author: String,
    private var downloadLimit: Int = 5
) : Book(title, author) {
    override fun borrow() {
        if (downloadLimit > 0) {
            downloadLimit--
            println("You borrowed the e-book: $title. Downloads left: $downloadLimit")
        } else {
            println("Download limit reached for $title.")
        }
    }

    fun getDownloadLimit(): Int = downloadLimit
}

// Step 3: Define the PrintedBook subclass
class PrintedBook(
    title: String,
    author: String,
    private var lateReturnFine: Double = 0.0
) : Book(title, author) {
    override fun returnBook() {
        println("You returned: $title.")
        println("Fine for late return: $$lateReturnFine")
    }

    fun setLateFine(fine: Double) {
        if (fine >= 0) lateReturnFine = fine
    }
}

// Step 4: Test the program
fun main() {
    // Create a list of books
    val books = listOf(
        PrintedBook("The Catcher in the Rye", "J.D. Salinger", 2.5),
        EBook("The Da Vinci Code", "Dan Brown", 3),
        EBook("Clean Code", "Robert C. Martin"),
        PrintedBook("To Kill a Mockingbird", "Harper Lee", 1.0)
    )

    // Display book information and test borrowing
    println("--- Library Catalog ---")
    for (book in books) {
        book.displayInfo()
        book.borrow()
    }

    // Test returning books
    println("\n--- Returning Books ---")
    for (book in books) {
        book.returnBook()
    }
}
