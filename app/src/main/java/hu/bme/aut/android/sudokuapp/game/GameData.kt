package hu.bme.aut.android.sudokuapp.game

import hu.bme.aut.android.sudokuapp.R
import kotlinx.serialization.Serializable
@Serializable
data class GameData(
    var st: Boolean = false,
    var name: String = "Player",
    var diff: String = "Easy",
    var timer: Long = 0,
    var solution : Array<IntArray> = Array(9){IntArray(9)},
    var puzzle : Array<IntArray> = Array(9){IntArray(9)},
    var table : Array<IntArray> = Array(9){IntArray(9)},
)
