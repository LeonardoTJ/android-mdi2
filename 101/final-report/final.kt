class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Create a sample user instance
    val user = User(
      fullName = "Alice Johnson",
      age = 26,
      username = "@alice_j_new",
      isVerified = true,
      friendList = mutableListOf("Bob", "Charlie", "Diana")
    )

    setContent {
      ProfileScreen(user)
    }
  }
}

// Define the User class
class User(
  val fullName: String,
  val age: Int,
  val username: String,
  val isVerified: Boolean,
  val friendList: MutableList<String>
) {
  // Method to get the user's age group
  fun getUserAgeGroup(): String {
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
}

// Composable function to display the friend list
@Composable
fun FriendList(friendList: List<String>) {
  Text(text = "Friends", style = MaterialTheme.typography.headlineMedium)
  if (friendList.isEmpty()) {
    Text(text = "No friends added yet.")
  } else {
    LazyColumn {
      items(friendList) { friend ->
        FriendItem(friend)
      }
    }
  }
}

// Composable function for each friend item
@Composable
fun FriendItem(friendName: String) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    elevation = CardDefaults.cardElevation(2.dp)
  ) {
    Text(
      text = friendName,
      modifier = Modifier.padding(16.dp)
    )
  }
}

// Composable function to display user profile information
@Composable
fun UserProfile(user: User) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp),
    elevation = CardDefaults.cardElevation(4.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(text = "Name: ${user.fullName}", style = MaterialTheme.typography.headlineMedium)
      Text(text = "Age: ${user.age}")
      Text(text = "Username: ${user.username}")
      Text(text = "Verified: ${if (user.isVerified) "Yes" else "No"}")
      Text(text = "Age Group: ${user.getUserAgeGroup()}")
    }
  }
}

// Composable function to display the profile screen
@Composable
fun ProfileScreen(user: User) {
  Column(modifier = Modifier.padding(16.dp)) {
    UserProfile(user)
    Spacer(modifier = Modifier.height(16.dp))
    FriendList(user.friendList)
  }
}
