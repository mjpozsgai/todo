package mjpozsgai.todo.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import mjpozsgai.todo.R
import mjpozsgai.todo.data.ItemListSQLiteDBHelper
import mjpozsgai.todo.data.TodoItem


class ItemFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: ItemListRecyclerViewAdapter

    lateinit var databaseHelper: ItemListSQLiteDBHelper
    lateinit var todoList: ArrayList<TodoItem>

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_item, container, false)
        return root
    }


    override fun onResume() {
        super.onResume()

        recyclerView = view?.findViewById(R.id.todo_item_list)!!

        databaseHelper = ItemListSQLiteDBHelper(activity)
        this.todoList = databaseHelper.fetchItemData()

        recyclerViewAdapter = ItemListRecyclerViewAdapter(activity?.applicationContext!!, this.todoList)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext!!)


        swipeRefreshLayout = view?.findViewById(R.id.swipe_refresh_layout)!!
        swipeRefreshLayout.setOnRefreshListener {
            this.todoList.clear()

            this.todoList.addAll(databaseHelper.fetchItemData())
            recyclerViewAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(sectionNumber: Int): ItemFragment {

            return ItemFragment()
        }
    }
}