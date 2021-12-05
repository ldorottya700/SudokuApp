package hu.bme.aut.android.sudokuapp.game

import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import hu.bme.aut.android.sudokuapp.MainActivity
import hu.bme.aut.android.sudokuapp.fragments.GameFragment
import hu.bme.aut.android.sudokuapp.generate.Difficulty
import hu.bme.aut.android.sudokuapp.generate.PrintStyle
import hu.bme.aut.android.sudokuapp.generate.QQWing
import hu.bme.aut.android.sudokuapp.generate.QQWingMobGen
import hu.bme.aut.android.sudokuapp.generate.Symmetry
import java.io.File
import java.io.FileNotFoundException

class ManageGame(cntx: Context): GameFragment.GameListener {

    private var gamedata: GameData = GameData()
    private var generator: QQWing = QQWing()
    private val context = cntx

    fun getData(): GameData {Log.i("game", "getData"); return gamedata}
    fun setMeta(gd: GameData){
        gamedata.st = gd.st
        gamedata.timer = gd.timer
        gamedata.diff = gd.diff
        gamedata.name = gd.name
    }

    fun tableVaules( gd: GameData){
        gamedata.table = Array(9){IntArray(9)}
        gamedata.solution = Array(9){IntArray(9)}
        gamedata.puzzle = Array(9){IntArray(9){0}}

        setMeta(gd)
        generatePuzzle(gd.diff)
        puzzleValues()
    }
    fun puzzleValues(){

        for(i in 0..8){
            for(j in 0..8){
                if(gamedata.table[i][j] != -1){gamedata.puzzle[i][j] = 1}
            }
        }
    }

    fun checkWin(): Boolean{
        for(i in 0..8){
            for(j in 0..8){
                if(gamedata.table[i][j] != gamedata.solution[i][j]) return false
            }
        }
        return true
    }

    override fun changeNumTable(cl: IntArray, idx: Int) {
        gamedata.table[cl[0]][cl[1]] = idx
    }

    override fun writeTimer(t: Long){
        gamedata.timer = t
    }

     fun generatePuzzle(diff: String){

         generator = QQWingMobGen.gen(diff)

         val tmptable = generator.puzzle
         for(l in 0..8) {
             for (c in 0..8) {
                 if (tmptable[9 * l + c] == 0) {
                     gamedata.table[l][c] = -1
                 }else{gamedata.table[l][c] = tmptable[9 * l + c]}

             }
         }

         val soltab = generator.solution
         for(l in 0..8)
             for (c in 0..8)
                 gamedata.solution[l][c] = soltab[9 * l + c]

         generator.printSolution()

     }

    fun writeToJSON(pref:String){
        var gson = Gson()
        val data = gson.toJson(gamedata)
        var path: String = pref+"gamedata.json"
        File( path).writeText(data)
        Log.i("gamedata save", data)
    }
    fun parseFromJSON(pref:String){
        var gson = Gson()
        var path: String = pref+"gamedata.json"
        try {
            gamedata = gson.fromJson(File(path).readText(),GameData::class.java)
        }catch (ex: FileNotFoundException){}

    }


    override fun victory() {
        if(checkWin()){
            /*Toplist Update*/
            var gson = Gson()
            var path: String = context.filesDir.path+"toplist.json"

            var topListData: MutableList<Array<String>> = emptyArray<Array<String>>().toMutableList()
            try {
                topListData = gson.fromJson(File(path).readText(), Array<Array<String>>::class.java).toMutableList()
            } catch (e: Exception) { }

            topListData.add(arrayOf(gamedata.name, gamedata.diff, gamedata.timer.toString(), ""))
            topListData.sortBy { it[2].toLong() }
            var i=0; while(i<topListData.size) topListData[i][3]=(++i).toString()
            val data = gson.toJson(topListData.toTypedArray())
            File(path).writeText(data)

        }
    }
    fun deleteTableCell(idx: IntArray){
        gamedata.table[idx[0]][idx[1]] = -1

    }
}