package mjpozsgai.todo.ui.main

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener
import mjpozsgai.todo.R
import mjpozsgai.todo.data.ItemListSQLiteDBHelper
import mjpozsgai.todo.data.TodoItem
import kotlin.properties.Delegates

class WeeklyFragment : Fragment(), OnRangeSelectedListener {

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: ItemListRecyclerViewAdapter

    lateinit var databaseHelper: ItemListSQLiteDBHelper
    var todoList: ArrayList<TodoItem> = ArrayList()

    var startTime : List<Int> = listOf(0,0,0)
    var endTime : List<Int> = listOf(0,0,0)


//    lateinit var currentCalendar: MaterialCalendarView

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRangeSelected(widget: MaterialCalendarView, dates: MutableList<CalendarDay>) {

        val selectedDates = widget.getSelectedDates()

            val year1 = selectedDates[0].year
            val month1 = selectedDates[0].month
            val day1 = selectedDates[0].day
            this.startTime = listOf(day1, month1, year1)
            val year2 = selectedDates[selectedDates.size-1].year
            val month2 = selectedDates[selectedDates.size-1].month
            val day2 = selectedDates[selectedDates.size-1].day
            this.endTime = listOf(day2, month2, year2)
            this.todoList = databaseHelper.fetchItemData(this.startTime, this.endTime)
            this.todoList
            this.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_weekly, container, false)

        return root
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        Log.i("CALLED", "ON RESUME")


        recyclerView = view?.findViewById(R.id.weekly_list)!!

        databaseHelper = ItemListSQLiteDBHelper(activity)
//        this.todoList = databaseHelper.fetchItemData(startTime,startTime)

        recyclerViewAdapter = ItemListRecyclerViewAdapter(activity?.applicationContext!!, this.todoList)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext!!)


        swipeRefreshLayout = view?.findViewById(R.id.swipe_refresh_layout)!!
        swipeRefreshLayout.setOnRefreshListener {
            this.todoList.clear()

            this.todoList.addAll(databaseHelper.fetchItemData(this.startTime, this.endTime))
            recyclerViewAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }


        val currentCalendar = view?.findViewById<MaterialCalendarView>(R.id.calendar_view)
        if (currentCalendar != null) {
            Log.i("CALLED", "ON CREATE")

            currentCalendar.setOnRangeSelectedListener(this)
        }


    }
//
//    fun onFinishInflate(){
//        this.currentCalendar = view?.findViewById<MaterialCalendarView>(R.id.calendar_view)!!
//
//    }



    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): WeeklyFragment {
            Log.i("CALLED", "NEW INSTANCE")

            return WeeklyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}