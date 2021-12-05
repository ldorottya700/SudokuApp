package hu.bme.aut.android.sudokuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.gson.Gson

import hu.bme.aut.android.sudokuapp.databinding.ActivityMainBinding
import hu.bme.aut.android.sudokuapp.fragments.BackGameFragment
import hu.bme.aut.android.sudokuapp.fragments.EndGameDialogFragment
import hu.bme.aut.android.sudokuapp.fragments.GameFragment
import hu.bme.aut.android.sudokuapp.fragments.NewGameDialogFragment
import hu.bme.aut.android.sudokuapp.game.GameData
import hu.bme.aut.android.sudokuapp.game.ManageGame
import kotlinx.serialization.json.Json
import java.io.File


class MainActivity : AppCompatActivity() , NewGameDialogFragment.NewGameDialogListener, BackGameFragment.BackToGameListener, EndGameDialogFragment.EndGameDialogListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    var gameManager : ManageGame = ManageGame(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameManager.parseFromJSON(filesDir.path)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

    }


    override fun newGame(gamedata: GameData){
        gameManager.tableVaules(gamedata)
        gameManager.writeToJSON(filesDir.path)
        navController.navigate(R.id.game)
    }

    override fun backToGame(){
        navController.navigate(R.id.game)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }

    override fun onBackPressed() {
    }

    override fun endGameDialog(){
        gameManager.getData().st = false
        navController.navigate(R.id.backGameFragment)

    }

}