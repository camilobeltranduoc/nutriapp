package com.example.accesibilidad.data

object RecipeRepo {
    private const val MAX_RECIPES = 20
    private val _recipes = mutableListOf<Recipe>()
    val recipes: List<Recipe> get() = _recipes

    private var nextId = 1

    init {
        // Semillas demo (para la actividad)
        addRecipe("Avena con fruta", "Desayuno", 320, "Avena con plátano y arándanos")
        addRecipe("Ensalada de pollo", "Almuerzo", 480, "Lechuga, pechuga, palta, tomate")
        addRecipe("Salmón al horno", "Cena", 520, "Salmón con limón y verduras")
        addRecipe("Yogurt con nueces", "Snack", 220, "Yogurt natural con frutos secos")
    }

    fun canAddMore(): Boolean = _recipes.size < MAX_RECIPES

    fun addRecipe(title: String, category: String, calories: Int, description: String): Boolean {
        if (!canAddMore()) return false
        if (_recipes.any { it.title.equals(title.trim(), ignoreCase = true) }) return false
        _recipes.add(Recipe(nextId++, title.trim(), category.trim(), calories, description.trim()))
        return true
    }

    fun search(
        query: String,
        category: String?,          // null = todas
        maxCalories: Int?           // null = sin tope
    ): List<Recipe> {
        val q = query.trim().lowercase()
        return _recipes
            .asSequence()
            .filter { r ->
                (q.isBlank() || r.title.lowercase().contains(q) || r.description.lowercase().contains(q)) &&
                        (category.isNullOrBlank() || r.category.equals(category, ignoreCase = true)) &&
                        (maxCalories == null || r.calories <= maxCalories)
            }
            .sortedBy { it.title }
            .toList()
    }

    fun categories(): List<String> =
        _recipes.groupBy { it.category }
            .keys
            .sorted()

    fun updateRecipe(id: Int, newTitle: String, newCategory: String, newCalories: Int, newDescription: String): Boolean {
        val index = _recipes.indexOfFirst { it.id == id }
        return if (index >= 0) {
            _recipes[index] = Recipe(
                id = id,
                title = newTitle.trim(),
                category = newCategory.trim(),
                calories = newCalories,
                description = newDescription.trim()
            )
            true
        } else false
    }

    fun deleteRecipe(id: Int): Boolean =
        _recipes.removeIf { it.id == id }
}
