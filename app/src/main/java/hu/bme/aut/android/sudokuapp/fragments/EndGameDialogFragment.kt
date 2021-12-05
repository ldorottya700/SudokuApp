package hu.bme.aut.android.sudokuapp.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.sudokuapp.MainActivity
import hu.bme.aut.android.sudokuapp.R
import hu.bme.aut.android.sudokuapp.databinding.DialogEndGameBinding
import hu.bme.aut.android.sudokuapp.databinding.DialogNewGameBinding
import hu.bme.aut.android.sudokuapp.game.GameData

class EndGameDialogFragment(): DialogFragment() {
    interface EndGameDialogListener {
        fun endGameDialog()
    }
    private lateinit var binding: DialogEndGameBinding
    private lateinit var listener: EndGameDialogFragment.EndGameDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? EndGameDialogFragment.EndGameDialogListener
            ?: throw RuntimeException("Activity must implement the NewGameDialogListener interface!")
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEndGameBinding.inflate(LayoutInflater.from(context))

        binding.btnEndGame.setOnClickListener{
            listener.endGameDialog()
        }
        return AlertDialog.Builder(requireContext())
            //String resource
            .setView(binding.root)
            .create()
    }
    override fun onStart() {
        super.onStart()
        val window: Window? = dialog!!.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)

    }
    companion object {
        const val TAG = "End Game"
    }
}