package mjpozsgai.todo.ui.main

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import mjpozsgai.todo.R
import mjpozsgai.todo.data.ItemListSQLiteDBHelper
import mjpozsgai.todo.data.TodoItem


class ItemListRecyclerViewAdapter(context: Context, todoItems: ArrayList<TodoItem>) :
    RecyclerView.Adapter<ItemListRecyclerViewAdapter.ItemListViewHolder>() {
    lateinit var databaseHelper: ItemListSQLiteDBHelper

    var context: Context
    var todoItems: ArrayList<TodoItem>

    init {
        this.context = context
        this.todoItems = todoItems
    }

    inner class ItemListViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        var taskTextView: TextView
        var scheduledTextView: TextView
        var checkbox : CheckBox
        var priority: ImageView
        var priorityText: TextView

        init {
            taskTextView = itemView.findViewById(R.id.task_name)
            scheduledTextView = itemView.findViewById(R.id.task_scheduled)
            checkbox = itemView.findViewById(R.id.checkbox_completed)
            priority = itemView.findViewById(R.id.priority)
            priorityText = itemView.findViewById(R.id.priority_text)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val view: View = inflater
            .inflate(R.layout.item_list_todo_item, parent, false)

        databaseHelper = ItemListSQLiteDBHelper(context)

        var check = view.findViewById<CheckBox>(R.id.checkbox_completed)
        check?.setOnClickListener {
            var id = check.text
            var newVal = if (check.isChecked) 1 else 0

            databaseHelper.updateCheck(id, newVal)
            var rep = databaseHelper.getTableAsString()
            Log.i("RESULTS", rep)

        }

        return ItemListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        if (todoItems.get(position).scheduled != 0L){
            holder.scheduledTextView.text = TodoItem.getFormattedScheduledTime(todoItems.get(position).scheduled)
        }else{
            holder.scheduledTextView.text = ""
        }
        val buttonList = listOf(
            R.drawable.green_uncircled,
            R.drawable.yellow_uncircled,
            R.drawable.red_uncircled
        )
        val priorityTextList = listOf(
            "low",
            "medium",
            "high"
        )
        holder.priority.setBackgroundResource(buttonList[todoItems.get(position).priority])
        holder.priorityText.text=priorityTextList[todoItems.get(position).priority]
        holder.taskTextView.text = todoItems.get(position).task
        holder.checkbox.text = todoItems.get(position).id.toString()
        if (todoItems.get(position).completed ==1){
            holder.checkbox.isChecked = true
        }

    }



    override fun getItemCount(): Int = todoItems.size

}