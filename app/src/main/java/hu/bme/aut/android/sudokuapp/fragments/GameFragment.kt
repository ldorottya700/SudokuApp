package hu.bme.aut.android.sudokuapp.fragments

import android.app.GameManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.sudokuapp.MainActivity
import hu.bme.aut.android.sudokuapp.R
import hu.bme.aut.android.sudokuapp.databinding.GameBinding
import hu.bme.aut.android.sudokuapp.game.ManageGame
import java.io.FileNotFoundException
import kotlin.concurrent.thread


class GameFragment: Fragment() {
    interface GameListener{
        fun changeNumTable(cl: IntArray, idx: Int)
        fun writeTimer(t: Long)
        fun victory()
    }
    private lateinit var binding: GameBinding
    private var cellMatrix: Array<Array<Button?>> = Array(9){Array(9){null} }
    private var numButtons : Array<Button?> = Array(9){null}
    private var writeableCells: Array<IntArray> = Array(9){IntArray(9){0}}
    private lateinit var gameListener: GameListener

    var noFocus = true
    var clicked : IntArray = intArrayOf(0,0)
    var wasClicked : IntArray = intArrayOf(-1,-1)

    lateinit var count: Thread
    lateinit var timer : Chronometer
    lateinit var gameManager:ManageGame

    override fun onAttach(context: Context) {
        super.onAttach(context)
        gameListener = ( context as? MainActivity )!!.gameManager
            ?: throw RuntimeException("Activity must implement the GameListener interface!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = GameBinding.inflate(inflater, container, false)
        gameManager= (activity as? MainActivity)!!.gameManager

        //init data!

        var gametable = gameManager.getData().table
        var gamer = gameManager.getData().name
        writeableCells = gameManager.getData().puzzle
        binding.gamerNameTextview.setText(gamer)

        timer = binding.timer

        timer.setOnChronometerTickListener(Chronometer.OnChronometerTickListener {
            val minutes: Long = (SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60
            val seconds: Long = (SystemClock.elapsedRealtime() - timer.getBase()) / 1000 % 60
        })

        //Table buttons
        for(i in 0..8){
            for(j in 0..8) {
                var btn = btnSettings()

                //Writeable or fixed
                if(gametable[i][j] != -1) {
                    btn.text = gametable[i][j].toString()
                }
                buttonStyle(intArrayOf(i,j), btn, buttonType(intArrayOf(i,j)))

                //Clicklistener on writeables
                //Disable click on fixed
                if(writeableCells[i][j] == 0){
                    btn.setOnClickListener {
                    noFocus = false
                    wasClicked[0] = clicked[0]
                    wasClicked[1] = clicked[1]
                    clicked[0] = i
                    clicked[1] = j
                        onClicked()
                    }
                    btn.setOnLongClickListener{
                        gameManager.deleteTableCell(intArrayOf(i,j))
                        btn.text = ""
                        return@setOnLongClickListener true
                    }


                }else{btn.isClickable = false}

                cellMatrix[i][j] = btn
                binding.gridLayout.addView(btn)
            }
        }
        //Bottom buttons
        for(i in 0..8){
            var btn = btnSettings()
            var txt = i+1
            btn.text = txt.toString()
            btn.setBackgroundResource(R.drawable.button_style_2)
            btn.setTextColor(Color.rgb(209,208,216))

            btn.setOnClickListener {
                changeNum(i)
                checkNumber(clicked[0], clicked[1] )

                gameListener.writeTimer(SystemClock.elapsedRealtime()-timer.base)
                if(gameManager.checkWin()){
                    gameListener.victory()
                    EndGameDialogFragment().show(childFragmentManager, EndGameDialogFragment.TAG)
                }

            }
            numButtons[i] = btn
            binding.gridNumbers.addView(btn)
        }

        count = thread(start = false){}

        return binding.root
    }

    private fun btnSettings(): Button{
        var btn = Button(activity)
        var w=Resources.getSystem().getDisplayMetrics().widthPixels / Resources.getSystem().displayMetrics.xdpi;
        var h=Resources.getSystem().getDisplayMetrics().heightPixels / Resources.getSystem().displayMetrics.ydpi;
        val di = (if (w < h) w else h)/10
        val dix = (di * Resources.getSystem().displayMetrics.xdpi).toInt()
        val diy = (di * Resources.getSystem().displayMetrics.ydpi).toInt()
        btn.minWidth = 0
        btn.minimumWidth = 0
        btn.minHeight = 0
        btn.minimumHeight = 0
        btn.setPadding(2,2,2,2)
        btn.height = diy
        btn.width = dix
        return btn
    }
    private fun resetColors(){
        for(i in 0..8){
            for (j in 0..8){
                buttonStyle(intArrayOf(i,j),cellMatrix[i][j], buttonType(intArrayOf(i,j)))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try{gameManager.parseFromJSON(requireActivity().filesDir.path)}catch(ex: FileNotFoundException){}

        timer.base = SystemClock.elapsedRealtime() - gameManager.getData().timer
        timer.start()
        Toast.makeText(activity, R.string.deleteinfo, Toast.LENGTH_LONG).show()

    }

    override fun onPause() {
        super.onPause()
        gameListener.writeTimer( SystemClock.elapsedRealtime() - timer.base )
        gameManager.writeToJSON(requireActivity().filesDir.path)
        timer.stop()
    }

    companion object {
        const val TAG = "Sudoku"
    }

    private fun onClicked(){
        buttonStyle(intArrayOf(clicked[0],clicked[1]), cellMatrix[clicked[0]][clicked[1]], "on_selected")
        if( ((wasClicked[0]/3)-(wasClicked[1]/3))%2 != 0 ){
            buttonStyle(intArrayOf(wasClicked[0],wasClicked[1]), cellMatrix[wasClicked[0]][wasClicked[1]], "dark_writeable")
        }
        else {
            buttonStyle(intArrayOf(wasClicked[0],wasClicked[1]), cellMatrix[wasClicked[0]][wasClicked[1]], "light_writeable")
        }
    }

    private fun changeNum(num : Int){
        if(noFocus == false){
            cellMatrix[clicked[0]][clicked[1]]!!.setText((num+1).toString())
            gameListener.changeNumTable(clicked, num+1)
        }else{Toast.makeText(activity, R.string.choose_cell, Toast.LENGTH_SHORT).show()} //String resource

    }

    private fun checkNumber(x: Int, y: Int){
        var error = false
        resetColors()
        if(cellMatrix[x][y] != null){
            for(i in 0..8){
                if(y != i &&  gameManager.getData().table[x][i] == gameManager.getData().table[x][y]){

                    if(buttonType(intArrayOf(x,i)) == "light_writeable"){

                        cellMatrix[x][i]?.setBackgroundResource(R.drawable.button_style_bad)
                        error = true
                    }else if(buttonType(intArrayOf(x,i)) == "dark_writeable"){
                        cellMatrix[x][i]?.setBackgroundResource(R.drawable.button_style_bad_2)
                        error = true
                    }else{
                        cellMatrix[x][i]?.setBackgroundResource(R.drawable.button_style_bad_fixed)
                        error = true
                    }
                }
            }
            for(i in 0..8){
                if(x != i &&  gameManager.getData().table[i][y] == gameManager.getData().table[x][y]){
                    if(buttonType(intArrayOf(i, y)) == "light_writeable"){
                        cellMatrix[i][y]?.setBackgroundResource(R.drawable.button_style_bad)
                        error = true
                    }else if(buttonType(intArrayOf(i, y)) == "dark_writeable"){
                        cellMatrix[i][y]?.setBackgroundResource(R.drawable.button_style_bad_2)
                        error = true
                    }else{
                        cellMatrix[i][y]?.setBackgroundResource(R.drawable.button_style_bad_fixed)
                        error = true
                    }
                }
            }
            var idxrow = x / 3 as Int
            var idxcol = y / 3 as Int
            for(i in (idxrow * 3 )..((idxrow * 3) + 2)){
                for(j in(idxcol * 3 )..((idxcol * 3 ) + 2)){
                    if(x != i && y != j &&  gameManager.getData().table[i][j] == gameManager.getData().table[x][y]){
                        if(buttonType(intArrayOf(i, j)) == "light_writeable"){
                            cellMatrix[i][j]?.setBackgroundResource(R.drawable.button_style_bad)
                            error = true
                        }else if(buttonType(intArrayOf(i, j)) == "dark_writeable"){
                            cellMatrix[i][j]?.setBackgroundResource(R.drawable.button_style_bad_2)
                            error = true
                        }else{
                            cellMatrix[i][j]?.setBackgroundResource(R.drawable.button_style_bad_fixed)
                            error = true
                        }
                    }

                }
            }
        }
        if(error == false){resetColors()}



    }
    private fun buttonType(idx : IntArray): String{
        if(((idx[0]/3)-(idx[1]/3))%2 != 0){
            if(writeableCells[idx[0]][idx[1]] == 0){
                return "dark_writeable"
            }else{ return "light_fixed"}

        }else{
            if(writeableCells[idx[0]][idx[1]] == 0){
                return "light_writeable"
            }else{ return "dark_fixed"}
        }
        return ""
    }
    private fun buttonStyle(idx: IntArray, button: Button?, type: String){
        when(type){
            "light_writeable" -> { button!!.setBackgroundResource(R.drawable.writeable_background)
                                   button.setTextColor(Color.rgb(181,170,176)) }
            "light_fixed" -> { button!!.setBackgroundResource(R.drawable.button_style)
                               button.setTextColor(Color.rgb(191,170,176)) }
            "dark_writeable" -> { button!!.setBackgroundResource(R.drawable.writeable_background_2)
                                  button.setTextColor(Color.rgb(181,170,176)) }
            "dark_fixed" -> { button!!.setBackgroundResource(R.drawable.button_style_2)
                              button.setTextColor(Color.rgb(191,170,176)) }
            "on_selected" -> { button!!.setBackgroundResource(R.drawable.on_selected)
                               button.setTextColor(Color.rgb(40,40,40)) }
        }
    }


}