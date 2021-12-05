package hu.bme.aut.android.sudokuapp.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.bme.aut.android.sudokuapp.MainActivity
import hu.bme.aut.android.sudokuapp.R
import hu.bme.aut.android.sudokuapp.databinding.BackGameBinding
import android.os.SystemClock
import android.util.Log

import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import hu.bme.aut.android.sudokuapp.game.ManageGame


class BackGameFragment: Fragment() {
    interface BackToGameListener{
        fun backToGame()
    }
    private lateinit var backListener: BackToGameListener
    private lateinit var binding: BackGameBinding
    private lateinit var gameManager: ManageGame

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backListener = context as? BackToGameListener?: throw RuntimeException("Activity must implement the BackToGameListener interface!")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BackGameBinding.inflate(inflater, container, false)

        binding.backToGameBtn.setOnClickListener { backListener.backToGame() }


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameManager = (requireActivity() as MainActivity).gameManager
        if(gameManager.getData().st){
            binding.timerBtgbtn.text = ts2S((activity as? MainActivity)!!.gameManager.getData().timer)
            binding.backGame.visibility = View.VISIBLE
            binding.noGame.visibility = View.GONE
        }else{
            binding.noGame.visibility = View.VISIBLE
            binding.backGame.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

    }
    private fun ts2S(ts: Long):String {
        var ts2 = ts/1000
        val s = ts2%60
        ts2 /= 60
        val m = ts2%60
        ts2 /= 60
        val h = ts2%24
        ts2 /= 24
        val d = ts2
        var r = "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        if(d != 0L) return "${d} days ${r}"
        return r
    }
    companion object {
        const val TAG = ""
    }


}