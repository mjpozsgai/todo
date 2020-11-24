package mjpozsgai.todo.ui.main

import android.content.DialogInterface
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import mjpozsgai.todo.R
import mjpozsgai.todo.Updatable
import mjpozsgai.todo.data.ItemListSQLiteDBHelper
import mjpozsgai.todo.data.TodoItem
import java.util.*


class TaskEntryFragment : DialogFragment() {

    var priority: Int = 0
    lateinit var dismissListener: Updatable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_task_entry, container, false)
    }

    override fun onResume() {
        super.onResume()

        val notesEditText = view?.findViewById<EditText>(R.id.task_notes)
        val logTaskButton = view?.findViewById<Button>(R.id.log_task_button)
        val scheduled = view!!.findViewById<DatePicker>(R.id.scheduled)
        assemblePriorityButtons()
        logTaskButton?.setOnClickListener { view ->
            submitTaskEntry(notesEditText, scheduled)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (dismissListener != null) {
            dismissListener.onDismissal()
        }
    }

    private fun submitTaskEntry(
        notesEditText: EditText?,
        scheduled: DatePicker

    ) {
        var task = notesEditText?.text.toString()
        var priority = priority
        var month = scheduled.month
        var day = scheduled.dayOfMonth
        var year = scheduled.year
        var date = Calendar.getInstance()
        date.set(year,month,day)

        var todoItem = TodoItem(task, priority, 0, date.timeInMillis )


        Log.i("SUBMITTED MOOD ENTRY", todoItem.toString())

        val databaseHelper = ItemListSQLiteDBHelper(activity)
        databaseHelper.save(todoItem)
        onResume()
    }

    private fun instructGuestToChooseAMood(view: View) {
        val formValidationMessage = Snackbar.make(
            view,
            "Please select a mood icon to record your mood!",
            Snackbar.LENGTH_LONG
        )
        formValidationMessage.show()
    }

    private fun informGuestOfMoodEntryCreation(view: View) {
        val confirmationMessage = Snackbar.make(
            view,
            "We've logged your mood!",
            Snackbar.LENGTH_LONG
        )
        confirmationMessage.show()
    }

    private fun assemblePriorityButtons() {
        val low = view?.findViewById<ImageView>(R.id.low)
        val medium = view?.findViewById<ImageView>(R.id.medium)
        val high = view?.findViewById<ImageView>(R.id.high)
        var buttonList = listOf(
            R.drawable.green_uncircled,
            R.drawable.yellow_uncircled,
            R.drawable.red_uncircled
        )
        var buttonListCircled =
            listOf(R.drawable.green_circled, R.drawable.yellow_circled, R.drawable.red_circled)

        val priorityButtons = listOf(low, medium, high)
        for ((index, button) in priorityButtons.withIndex()) {
            button?.setOnClickListener { view ->
                for ((i,b) in priorityButtons.withIndex()) {
                    if (b==button){
                        Log.i("BUTTON", "EQUAL")
                        b?.setBackgroundResource(buttonListCircled[i])
                        priority = i
                    }
                     else {
                        Log.i("BUTTON", "NOT EQUAL")
                        b?.setBackgroundResource(buttonList[i])
                    }
                }
            }
        }
    }

}
