package hu.bme.aut.android.sudokuapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import hu.bme.aut.android.sudokuapp.TopListAdapter
import hu.bme.aut.android.sudokuapp.databinding.TopListBinding
import java.io.FileNotFoundException

class TopListFragment: Fragment() {
    private lateinit var adapter: TopListAdapter

    private lateinit var binding: TopListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TopListBinding.inflate(inflater, container, false)

        adapter = TopListAdapter()

        try{adapter.parseList(activity?.filesDir!!.path)}catch(ex: FileNotFoundException){}
        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.mainRecyclerView.adapter = adapter
        //adapter.clearList(activity?.filesDir!!.path)
    }
    companion object {
        const val TAG = "Toplist"
    }


}