
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (resources.getBoolean(R.bool.isTablet)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_list, RecipeListFragment())
                .replace(R.id.fragment_container_detail, RecipeDetailFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecipeListFragment())
                .commit()
        }
    }
}


class RecipeDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_detail, container, false)
        val detailTextView: TextView = view.findViewById(R.id.recipe_detail_text)
        detailTextView.text = "Recipe detail screen"
        return view
    }
}

class RecipeListFragment : Fragment() {
    private val recipes = listOf("Spaghetti", "Tacos", "Salad", "Pizza", "Pancakes")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_list, container, false)
        val listView: TextView = view.findViewById(R.id.recipe_list)
        listView.text = recipes.joinToString { "${it}\n" }

        return view
    }
}


