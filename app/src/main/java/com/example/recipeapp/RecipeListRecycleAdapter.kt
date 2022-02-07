package com.example.recipeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.food_recycler_row.view.*

class RecipeListRecycleAdapter(val foodList:ArrayList<String>,val idList:ArrayList<Int>) : RecyclerView.Adapter<RecipeListRecycleAdapter.FoodHolder>() {

    class FoodHolder(itemview: View) : RecyclerView.ViewHolder(itemview){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val inflater =LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.food_recycler_row,parent,false)
        return FoodHolder(view)
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.itemView.food_recycle_item.text=foodList[position]
        holder.itemView.setOnClickListener{
            val action=RecipeListDirections.actionRecipeListToRecipeAdd("recycle",idList[position])
            Navigation.findNavController(it).navigate(action)

        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

}