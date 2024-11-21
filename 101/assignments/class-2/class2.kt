fun main() {
  // Define user profile information
  val fullName = "Alice Johnson"
  val age = 26
  val username = "@alice_j_new"
  val isVerified = true

  // Determine user age group based on age
  val ageGroup = getUserAgeGroup(age)

  // Display profile information
  displayProfileInfo(fullName, age, username, isVerified, ageGroup)

  // Initialize the friend list
  val friendList = mutableListOf<String>()

  // Add and remove friends
  addFriend(friendList, "Bob")
  addFriend(friendList, "Charlie")
  addFriend(friendList, "Diana")
  displayFriendList(friendList)

  removeFriend(friendList, "Charlie")
  removeFriend(friendList, "Eve") // Trying to remove a friend not in the list
  displayFriendList(friendList)
}

fun displayProfileInfo(
  fullName: String,
  age: Int,
  username: String,
  verified: Boolean,
  ageGroup: String
) {
  // Print user profile information
  println("User Profile")
  println("Name: $fullName")
  println("Age: $age")
  println("Username: $username")
  println("Verified: $verified")
  println("Age Group: $ageGroup")
}

// Function to determine user age group based on age
fun getUserAgeGroup(age: Int): String {
  return if (age < 13) {
    "Child"
  } else if (age in 13..17) {
    "Teenager"
  } else if (age in 18..59) {
    "Adult"
  } else {
    "Senior"
  }
}

// Modified displayFriendList function to show the total number of friends
fun displayFriendList(friendList: List<String>) {
  if (friendList.isEmpty()) {
    println("No friends added yet.")
  } else {
    println("Your Friends:")
    for (friend in friendList) {
      println("- $friend")
    }
    println("You have ${friendList.size} friends.")
  }
}

// Function to add a friend to the friend list
fun addFriend(friendList: MutableList<String>, friendName: String) {
  friendList.add(friendName)
  println("$friendName has been added to your friend list.")
}

// Function to remove a friend from the friend list
fun removeFriend(friendList: MutableList<String>, friendName: String) {
  if (friendList.contains(friendName)) {
    friendList.remove(friendName)
    println("$friendName has been removed from your friend list.")
  } else {
    println("$friendName is not in your friend list.")
  }
}
