// Base class: Book
open class Book(
    val title: String,
    val author: String,
    private var available: Boolean = true
) {
    fun isAvailable(): Boolean = available

    open fun borrow() {
        if (available) {
            available = false
            println("You borrowed: $title")
        } else {
            println("Sorry, $title is unavailable.")
        }
    }

    open fun returnBook() {
        available = true
        println("You returned: $title")
    }
}

// Subclass: PrintedBook
class PrintedBook(
    title: String,
    author: String,
    private var lateReturnFine: Double = 0.0
) : Book(title, author) {
    fun getLateFine(): Double = lateReturnFine
}

// Subclass: EBook
class EBook(
    title: String,
    author: String,
    private var downloadLimit: Int = 5
) : Book(title, author) {
    fun hasDownloadsLeft(): Boolean = downloadLimit > 0

    override fun borrow() {
        if (hasDownloadsLeft()) {
            downloadLimit--
            println("You borrowed the e-book: $title. Downloads left: $downloadLimit")
        } else {
            println("Download limit reached for $title.")
        }
    }
}

// Step 1: Declarative Operations
fun main() {
    // Step 2: Create the library catalog
    val catalog = listOf(
        PrintedBook("To Kill a Mockingbird", "Harper Lee", 2.0),
        EBook("Clean Code", "Robert C. Martin"),
        PrintedBook("1984", "George Orwell", 1.5),
        EBook("The Da Vinci Code", "Dan Brown", 3),
        PrintedBook("The Great Gatsby", "F. Scott Fitzgerald", 0.0)
    )

    catalog[1].borrow() // Borrow Clean Code
    catalog[3].borrow() // Borrow The Da Vinci Code
    catalog[4].borrow() // Borrow The Great Gatsby

    // Step 3: Filter unavailable books
    val unavailableBooks = catalog.filter { !it.isAvailable() }
    println("--- Unavailable Books ---")
    unavailableBooks.forEach { println(it.title) }

    // Step 4: Map to titles and statuses
    val bookTitlesAndStatuses = catalog.map { "${it.title} (${if (it.isAvailable()) "Available" else "Unavailable"})" }
    println("\n--- Book Titles and Statuses ---")
    bookTitlesAndStatuses.forEach { println(it) }

    // Step 5: Reduce to calculate total fines for overdue printed books
    val totalFines = catalog
        .filterIsInstance<PrintedBook>() // Only PrintedBook objects
        .map { it.getLateFine() }       // Extract fine amounts
        .reduce { acc, fine -> acc + fine } // Sum up all fines
    println("\nTotal fines for overdue books: $$totalFines")
}
