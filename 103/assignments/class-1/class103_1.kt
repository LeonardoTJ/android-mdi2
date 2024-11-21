fun main() {
    // Step 1: Define the library catalog as a multi-dimensional list
    val catalog = listOf(
        listOf( // Fiction genre
            mutableListOf("The Great Gatsby", "F. Scott Fitzgerald", true),
            mutableListOf("1984", "George Orwell", true),
        ),
        listOf( // Non-Fiction genre
            mutableListOf("Sapiens", "Yuval Noah Harari", true)
        )
    )

    // Step 2: Function to list all books
    fun listBooks() {
        val genres = listOf("Fiction", "Non-Fiction")
        println("--- Library Catalog ---")
        for (i in catalog.indices) {
            println("${genres[i]}:")
            for (book in catalog[i]) {
                val title = book[0] as String
                val author = book[1] as String
                val availability = if (book[2] as Boolean) "Available" else "Borrowed"
                println("  - $title by $author ($availability)")
            }
        }
    }

    // Step 3: Function to check if a book is available
    fun isAvailable(title: String): Boolean {
        for (genre in catalog) {
            for (book in genre) {
                if (book[0] == title) {
                    return book[2] as Boolean
                }
            }
        }
        return false // Book not found
    }

    // Step 5: Test the program
    println("Initial Catalog:")
    listBooks()

    println("\nChecking availability of '1984':")
    println("Available? ${isAvailable("1984")}")

    println("\nFinal Catalog:")
    listBooks()
}
