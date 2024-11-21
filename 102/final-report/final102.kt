fun main() {
    val bakery = Bakery()

    // Add sample menu items
    bakery.addMenuItem(BakeryItem("Cake", 15.0, 10))
    bakery.addMenuItem(BakeryItem("Muffin", 3.0, 20))
    bakery.addMenuItem(BakeryItem("Cookie", 1.0, 50))

    // Simulate the menu and orders
    bakery.displayMenu()

    bakery.placeOrder("Alice", listOf(Pair("Cake", 2), Pair("Cookie", 5)))
    bakery.placeOrder("Bob", listOf(Pair("Muffin", 10), Pair("Cake", 1)))

    bakery.displayMenu() // Check remaining stock

    bakery.generateSummary()
}

// BakeryItem Class
class BakeryItem(val name: String, var price: Double, var stock: Int) {
    fun displayInfo() {
        println("Item: $name, Price: $$price, Stock: $stock")
    }

    fun reduceStock(quantity: Int) {
        if (stock >= quantity) {
            stock -= quantity
        } else {
            println("Insufficient stock for $name")
        }
    }
}

// Order Class
class Order(val customerName: String, val items: List<Pair<BakeryItem, Int>>) {
    private val taxRate = 0.08

    fun calculateTotal(): Double {
        val subtotal = items.sumOf { it.first.price * it.second } // Price * Quantit
        return subtotal + (subtotal * taxRate)
    }

    fun displayOrder() {
        println("\nOrder for $customerName:")
        items.forEach { (item, quantity) ->
            println("- ${item.name} x $quantity @ $${item.price} each")
        }
        println("Total (with tax): $${"%.2f".format(calculateTotal())}")
    }
}

// Bakery Class
class Bakery {
    private val menu = mutableListOf<BakeryItem>()
    private val orders = mutableListOf<Order>()

    // Add items to the menu
    fun addMenuItem(item: BakeryItem) {
        menu.add(item)
        println("${item.name} added to the menu!")
    }

    // Display the menu
    fun displayMenu() {
        println("\n--- Bakery Menu ---")
        menu.forEach { it.displayInfo() }
    }

    // Place an order
    fun placeOrder(customerName: String, orderDetails: List<Pair<String, Int>>) {
        val itemsOrdered = mutableListOf<Pair<BakeryItem, Int>>()

        for ((itemName, quantity) in orderDetails) {
            val item = menu.find { it.name.equals(itemName, ignoreCase = true) }

            if (item != null && item.stock >= quantity) {
                item.reduceStock(quantity)
                itemsOrdered.add(Pair(item, quantity))
            } else {
                println("Unable to process $itemName (insufficient stock or not found).")
            }
        }

        if (itemsOrdered.isNotEmpty()) {
            val order = Order(customerName, itemsOrdered)
            orders.add(order)
            println("Order placed for $customerName!")
            order.displayOrder()
        } else {
            println("No valid items in order. Order not placed.")
        }
    }

    // Generate a daily summary
    fun generateSummary() {
        println("\n--- Daily Summary ---")
        if (orders.isEmpty()) {
            println("No orders were placed today.")
        } else {
            orders.forEach { it.displayOrder() }
            val totalSales = orders.sumOf { it.calculateTotal() }
            println("\nTotal Sales: $${"%.2f".format(totalSales)}")
        }
    }
}