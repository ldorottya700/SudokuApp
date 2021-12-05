package hu.bme.aut.android.sudokuapp.fragments

import android.app.Dialog

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.sudokuapp.game.GameData
import hu.bme.aut.android.sudokuapp.R
import hu.bme.aut.android.sudokuapp.databinding.DialogNewGameBinding

class NewGameDialogFragment : DialogFragment(){
    interface NewGameDialogListener {
        fun newGame(gamedata: GameData)
    }
    private lateinit var binding: DialogNewGameBinding
    private lateinit var listener: NewGameDialogListener
    var gameDifficulty = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewGameDialogListener
            ?: throw RuntimeException("Activity must implement the NewGameDialogListener interface!")
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            binding = DialogNewGameBinding.inflate(LayoutInflater.from(context))
            binding.difficultyChose.adapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                resources.getStringArray(R.array.category_items)
            )


            var spinner: Spinner = binding.difficultyChose
            spinner.setSelection(0)
            spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?){}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (spinner.selectedItemPosition) {
                        0 -> gameDifficulty = "Easy"
                        1 -> gameDifficulty = "Medium"
                        2 -> gameDifficulty = "Hard"
                    }
                }
            }
            binding.btnCreate.setOnClickListener{
                if(binding.gamerName.text.toString().isEmpty()){
                    //String resource
                    Toast.makeText(activity, "Write your name there", Toast.LENGTH_LONG).show()
                }else{
                    if(binding.gamerName.text.toString().length > 12){
                        Toast.makeText(activity, "Name can't be more than 12 characters", Toast.LENGTH_LONG).show()
                    }else{listener.newGame(gameData())}
                }
            }


        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

    }

    override fun onStart() {
        super.onStart()
        val window: Window? = dialog!!.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)


    }

    private fun gameData() = GameData(
        st = true,
        name = binding.gamerName.text.toString(),
        diff = gameDifficulty,
        timer = 0
    )

    companion object {
        const val TAG = "New Game"
    }


}