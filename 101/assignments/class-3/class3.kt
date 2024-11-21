fun main() {
  // Create a new user
  val user = User("Alice Johnson", 26, "@alice_j_new", true)

  // Display profile information
  user.displayProfile()

  // Manage friends
  user.addFriend("Bob")
  user.addFriend("Charlie")
  user.addFriend("Diana")
  user.displayFriends()

  user.removeFriend("Charlie")
  user.removeFriend("Eve") // Attempting to remove a friend not in the list
  user.displayFriends()
}

class User(
  val fullName: String,
  val age: Int,
  val username: String,
  val isVerified: Boolean
  ) {
  // Friend list for this user
  private val friendList = mutableListOf<String>()

  // Method to get the user's age group
  private fun getUserAgeGroup(): String {
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

  // Method to add a friend to the friend list
  fun addFriend(friendName: String) {
    friendList.add(friendName)
    println("$friendName has been added to your friend list.")
  }

  // Method to remove a friend from the friend list
  fun removeFriend(friendName: String) {
    if (friendList.contains(friendName)) {
      friendList.remove(friendName)
      println("$friendName has been removed from your friend list.")
    } else {
      println("$friendName is not in your friend list.")
    }
  }

  // Method to display the friend list
  fun displayFriends() {
    if (friendList.isEmpty()) {
      println("No friends added yet.")
    } else {
      println("Your Friends:")
      for (friend in friendList) {
        println("- $friend")
      }
    }
  }

  // Method to display the user's profile information
  fun displayProfile() {
    println("User Profile")
    println("Name: $fullName")
    println("Age: $age")
    println("Username: $username")
    println("Verified: $isVerified")
    println("Age Group: ${getUserAgeGroup()}")
  }
}

