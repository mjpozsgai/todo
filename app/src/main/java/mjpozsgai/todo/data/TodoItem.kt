package mjpozsgai.todo.data

import android.icu.util.Calendar.DAY_OF_YEAR
import android.provider.Settings
import android.provider.Settings.System.DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class TodoItem(task: String, priority: Int,  completed: Int, scheduled: Long = 0) {

    var task = task
    var completed = completed
    var priority = priority
    var scheduled = scheduled
    var created: Long = Calendar.getInstance().timeInMillis
    var id  = 0


    constructor(task: String, priority: Int, createdTime: Long, completed: Int, scheduled: Long=0, id: Int) :
            this(task, priority, completed, scheduled) {
        this.created= createdTime
        this.id = id
    }

    companion object {


        @JvmStatic
        fun getFormattedCreatedTime(createdTime: Long): String? {
            val DATE_FORMAT = "EEEE, dd MMM, yyyy, hh:mm a"
            val dateFormat = SimpleDateFormat(DATE_FORMAT)
            dateFormat.setTimeZone(TimeZone.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = createdTime
            return dateFormat.format(calendar.getTime())
        }

        @JvmStatic
        fun getFormattedScheduledTime(scheduledTime: Long): String? {
            val today = Calendar.getInstance().get(DAY_OF_YEAR)
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = scheduledTime
//            var DATE_FORMAT = "hh:mm a"
            var DATE_FORMAT = "EEE MMM dd"
            if (cal1.get(DAY_OF_YEAR) != today) {
                DATE_FORMAT = "EEE MMM dd"
            }
            val dateFormat = SimpleDateFormat(DATE_FORMAT)
            dateFormat.setTimeZone(TimeZone.getDefault())
            val calendar = Calendar.getInstance()
            return if (cal1.get(DAY_OF_YEAR) != today) {
                dateFormat.format(cal1.getTime())
            }else{
                "Today" + dateFormat.format(cal1.getTime())
            }
        }



    }

}