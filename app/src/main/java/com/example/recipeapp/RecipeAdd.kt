package com.example.recipeapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_recipe_add.*
import java.io.ByteArrayOutputStream


class RecipeAdd : Fragment() {
    var selectedImage: Uri? = null
    var selectedBıtmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton.setOnClickListener {
            saveRecipe(it)
        }

        imageView.setOnClickListener {
            uploadImage(it)
        }

        arguments?.let {
            var info = RecipeAddArgs.fromBundle(it).info


            if(info == "menu"){
                foodNameText.setText("")
                ingredientsText.setText("")
                saveButton.visibility=View.VISIBLE

                val defaultImage=BitmapFactory.decodeResource(context?.resources,R.drawable.select_image)
                imageView.setImageBitmap(defaultImage)


            }else{
                saveButton.visibility=View.INVISIBLE

                val selectedId = RecipeAddArgs.fromBundle(it).id
                context?.let {
                    try {
                        val db=it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                        val cursor=db.rawQuery("Select * from foods Where id = ?", arrayOf(selectedId.toString()))

                        val foodNameIndex=cursor.getColumnIndex("foodname")
                        val ingredient=cursor.getColumnIndex("ingredient")
                        val image=cursor.getColumnIndex("image")

                        while (cursor.moveToNext()){
                            foodNameText.setText(cursor.getString(foodNameIndex))
                            ingredientsText.setText(cursor.getString(ingredient))

                            val byteArray=cursor.getBlob(image)
                            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()

                    }catch (e:Exception){
                        e.printStackTrace()

                    }

                }
            }
        }
    }


    fun saveRecipe(view: View) {
        val foodName = foodNameText.text.toString()
        val ingredients = ingredientsText.text.toString()

        if (selectedBıtmap != null) {
            val smallBitmap = createSmallBitmap(selectedBıtmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS foods (id integer primary key,foodname varchar,ingredient varchar,image BLOB)")
                    val sqlString="INSERT INTO foods (foodname,ingredient,image) VALUES (?,?,?)"
                    val statement=database.compileStatement(sqlString)
                    statement.bindString(1,foodName)
                    statement.bindString(2,ingredients)
                    statement.bindBlob(3,byteArray)
                    statement.execute()

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            val action = RecipeAddDirections.actionRecipeAddToRecipeList()
            Navigation.findNavController(view).navigate(action)
        }




    }

    fun uploadImage(view: View) {
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 2)

            }

            fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResult: IntArray
            ) {
                if (requestCode == 1) {
                    if (grantResult.size > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, 2)
                    }

                }


                super.onRequestPermissionsResult(requestCode, permissions, grantResult)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data

            try {
                context?.let {
                    if (selectedImage != null) {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source =
                                ImageDecoder.createSource(it.contentResolver, selectedImage!!)
                            selectedBıtmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(selectedBıtmap)

                        } else {
                            selectedBıtmap =
                                MediaStore.Images.Media.getBitmap(it.contentResolver, selectedImage)
                            imageView.setImageBitmap(selectedBıtmap)

                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun createSmallBitmap(userSelectedBitmap: Bitmap, maxSize: Int): Bitmap {

        var width = userSelectedBitmap.width
        var height = userSelectedBitmap.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            width = maxSize
            val shortedHeight = width / bitmapRatio
            height = shortedHeight.toInt()

        } else {
            height = maxSize
            val shortedWidth = height * bitmapRatio
            width = shortedWidth.toInt()

        }

        return Bitmap.createScaledBitmap(userSelectedBitmap, width, height, true)
    }

}