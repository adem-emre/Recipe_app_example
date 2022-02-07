package com.example.recipeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.add_food,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.food_add_item){
            val action = RecipeListDirections.actionRecipeListToRecipeAdd("menu",0)
            Navigation.findNavController(this,R.id.fragmentContainerView).navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }
}