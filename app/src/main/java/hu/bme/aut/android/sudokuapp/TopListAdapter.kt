package hu.bme.aut.android.sudokuapp

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import hu.bme.aut.android.sudokuapp.databinding.ListRowBinding
import hu.bme.aut.android.sudokuapp.game.GameData
import java.io.File

class TopListAdapter: RecyclerView.Adapter<TopListAdapter.TopListViewHolder>() {

    private var element: MutableList<Array<String>> = emptyArray<Array<String>>().toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_row, parent, false)
        return TopListViewHolder(view)

    }

    override fun onBindViewHolder(holder: TopListViewHolder, position: Int) {
        holder.bind(element[position][0],element[position][1], element[position][2], element[position][3])

    }

    override fun getItemCount(): Int {
        return element.size
    }

    inner class TopListViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ListRowBinding.bind(itemView)

        fun bind(name: String, diff: String, time: String, order: String) {
            binding.playerTextview.text = name
            binding.diffTextview.text = diff
            binding.timeTextview.text = ts2S(time.toLong())
            binding.orderTextview.text = order
            if(order.toInt() %2 != 1 ){binding.row.setBackgroundColor(Color.rgb(68, 61, 69))}else{binding.row.setBackgroundColor(Color.rgb(0x28, 0x28, 0x28))}
        }
    }

    fun parseList(pref:String) {
        var gson = Gson()
        var path: String = pref+"toplist.json"
        element = gson.fromJson(File(path).readText(), Array<Array<String>>::class.java).toMutableList()
    }

    fun writeList(pref:String){
        var gson = Gson()
        val data = gson.toJson(element.toTypedArray())
        var path: String = pref+"toplist.json"
        File(path).writeText(data)
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

    fun clearList(pr:String){
        element = emptyArray<Array<String>>().toMutableList()
        notifyDataSetChanged()
        writeList(pr)
    }


}