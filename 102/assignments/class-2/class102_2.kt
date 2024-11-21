fun main() {
    // Initial stock levels
    var cookies = 10
    var muffins = 15
    var cakes = 20

    // Menu options
    while (true) {
        println("\n--- Grocery Store Inventory ---")
        println("1. View stock levels")
        println("2. Restock items")
        println("3. Sell items")
        println("4. Exit")
        print("Enter your choice: ")

        val choice = readLine()?.toIntOrNull() ?: 0

        when (choice) {
            1 -> displayStock(cookies, muffins, cakes)
            2 -> {
                val (item, qty) = getItemAndQuantity()
                when (item.lowercase()) {
                    "cookies" -> cookies += qty
                    "muffins" -> muffins += qty
                    "cakes" -> cakes += qty
                    else -> println("Invalid item")
                }
            }
            3 -> {
                val (item, qty) = getItemAndQuantity()
                when (item.lowercase()) {
                    "cookies" -> cookies = sellItem(cookies, qty)
                    "muffins" -> muffins = sellItem(muffins, qty)
                    "cakes" -> cakes = sellItem(cakes, qty)
                    else -> println("Invalid item")
                }
            }
            4 -> {
                println("Exiting the program")
                break
            }
            else -> println("Invalid choice. Please try again.")
        }
    }
}

// Function to display stock
fun displayStock(cookies: Int, muffins: Int, cakes: Int) {
    println("\nCurrent Stock Levels:")
    println("Cookies: $cookies")
    println("Muffins: $muffins")
    println("Cakes: $cakes")
}

// Function to sell items
fun sellItem(stock: Int, quantity: Int): Int {
    return if (stock >= quantity) {
        println("Sold $quantity items!")
        stock - quantity
    } else {
        println("Insufficient stock! Only $stock items available.")
        stock
    }
}

// Function to get item and quantity from the user
fun getItemAndQuantity(): Pair<String, Int> {
    print("Enter item name (cookies, muffins, cakes): ")
    val item = readLine() ?: ""
    print("Enter quantity: ")
    val quantity = readLine()?.toIntOrNull() ?: 0
    return Pair(item, quantity)
}
