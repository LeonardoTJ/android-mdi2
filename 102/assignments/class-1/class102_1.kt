
fun main() {
    // Define a constant for the currency symbol
    val currencySymbol = "$"

    // Ask the user for the number of items sold
    println("Enter the number of cookies sold:")
    val cookiesSold = readLine()?.toIntOrNull() ?: 0

    println("Enter the price of a cookie:")
    val pricePerCookie = readLine()?.toDoubleOrNull() ?: 0.0

    println("Enter the number of muffins sold:")
    val muffinsSold = readLine()?.toIntOrNull() ?: 0

    println("Enter the price of a muffin:")
    val pricePerMuffin = readLine()?.toDoubleOrNull() ?: 0.0

    println("Enter the number of cakes sold:")
    val cakesSold = readLine()?.toIntOrNull() ?: 0

    println("Enter the price of a cake:")
    val pricePerCake = readLine()?.toDoubleOrNull() ?: 0.0

    // Calculate revenue for each item
    val cookieRevenue = cookiesSold * pricePerCookie
    val muffinRevenue = muffinsSold * pricePerMuffin
    val cakeRevenue = cakesSold * pricePerCake

    // Calculate total revenue
    val totalRevenue = cookieRevenue + muffinRevenue + cakeRevenue

    // Display the results
    println("\n--- Revenue Report ---")
    println("Revenue from cookies: $currencySymbol$cookieRevenue")
    println("Revenue from muffins: $currencySymbol$muffinRevenue")
    println("Revenue from cakes: $currencySymbol$cakeRevenue")
    println("Total revenue: $currencySymbol$totalRevenue")
}