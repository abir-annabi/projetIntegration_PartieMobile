package com.example.projetintegration

import com.example.projetintegration.data.models.Plat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun main() {
    val jsonResponse = """[{"id":1,"nom":"Salade Quinoa & Avocat","description":"Salade quinoa, avocat, pois chiches et citron.","ingredients":["Quinoa","Avocat","Pois chiches","Citron","Concombre"],"calories":220,"categorie":"dejeuner","imageUrl":"https://example.com/images/quinoa_salad.jpg","tempsPreparation":15},{"id":2,"nom":"Veggie Bowl Tahini","description":"Bowl de légumes crus avec sauce tahini légère.","ingredients":["Carottes","Chou rouge","Pois chiches","Concombre","Sauce tahini"],"calories":180,"categorie":"dejeuner","imageUrl":"https://example.com/images/veggie_bowl.jpg","tempsPreparation":12}]"""
    
    try {
        val gson = Gson()
        val listType = object : TypeToken<List<Plat>>() {}.type
        val plats: List<Plat> = gson.fromJson(jsonResponse, listType)
        
        println("Successfully parsed ${plats.size} plats:")
        plats.forEach { plat ->
            println("- ${plat.nom} (${plat.categorie}) - ${plat.calories} cal")
        }
    } catch (e: Exception) {
        println("Error parsing JSON: ${e.message}")
        e.printStackTrace()
    }
}