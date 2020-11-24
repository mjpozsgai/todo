package mjpozsgai.todo.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*
import java.util.Calendar.*
import kotlin.collections.ArrayList

class ItemListSQLiteDBHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    val context = context


    fun getTableAsString(): String {
        Log.d("DB", "getTableAsString called")
        val db: SQLiteDatabase = ItemListSQLiteDBHelper(context).getReadableDatabase()

        var tableString = String.format("Table %s:\n", ITEM_LIST_TABLE_NAME)
        val allRows = db.rawQuery("SELECT * FROM $ITEM_LIST_TABLE_NAME", null)
        if (allRows.moveToFirst()) {
            val columnNames = allRows.columnNames
            do {
                for (name in columnNames) {
                    tableString += String.format(
                        "%s: %s\n", name,
                        allRows.getString(allRows.getColumnIndex(name))
                    )
                }
                tableString += "\n"
            } while (allRows.moveToNext())
        }
        return tableString
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createItemListTableQuery)
//        save(TodoItem("Take out the trash",1, 0L, 0, 1589842800000), db)
//        save(TodoItem("Do the dishes", 2, 0L, 0, 1589896800000), db)
//        save(TodoItem("Do laundry", 3, 0L, 0, 1589911200000, 0), db)
//        save(TodoItem("walk the dog", 2, 0L, 0,1590084000000,  0), db)
//        save(TodoItem("clean room", 2,0L, 0, 1590170400000, 0), db)
//        save(TodoItem("finish homework", 1,0L, 0, 1590256800000, 0), db)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(dropItemListTableQuery)
        onCreate(sqLiteDatabase)
    }


    fun updateCheck(
        id: CharSequence,
        newVal: Int
    ) {
        val database: SQLiteDatabase = ItemListSQLiteDBHelper(context).getWritableDatabase()

        val values = ContentValues()

        values.put(ITEM_LIST_COLUMN_ID, id.toString())
        values.put(ITEM_LIST_COLUMN_COMPLETED, newVal)


        val updatedRow = database?.update(ITEM_LIST_TABLE_NAME, values, "_id=$id", null)

        if (updatedRow == -1) {
            Log.i("SQLITE INSERTION FAILED", "We don't know why")
        } else {
            Log.i("MOOD ENTRY SAVED!", "Saved in row $updatedRow")
        }
    }

    fun save(
        todoItem: TodoItem,
        database: SQLiteDatabase?
    ) {
        var confirmedDatabase = database
        if(confirmedDatabase == null) {
            confirmedDatabase = ItemListSQLiteDBHelper(context).getWritableDatabase()
        }
        val values = ContentValues()

        values.put(ITEM_LIST_COLUMN_TASK, todoItem.task)
        values.put(ITEM_LIST_COLUMN_CREATED, todoItem.created)
        values.put(ITEM_LIST_COLUMN_PRIORITY, todoItem.priority)
        values.put(ITEM_LIST_COLUMN_COMPLETED, todoItem.completed)
        values.put(ITEM_LIST_COLUMN_SCHEDULED, todoItem.scheduled)


        val newRowId = confirmedDatabase?.insert(ITEM_LIST_TABLE_NAME, null, values)

        if (newRowId == -1.toLong()) {
            Log.i("SQLITE INSERTION FAILED", "We don't know why")
        } else {
            Log.i("MOOD ENTRY SAVED!", "Saved in row $newRowId")
        }
    }
    fun save(todoItem: TodoItem) {
        save(todoItem, null)
    }


    fun listItems(): Cursor {
        val database: SQLiteDatabase = ItemListSQLiteDBHelper(context).getReadableDatabase()

        val cursor: Cursor = database.query(
            ITEM_LIST_TABLE_NAME,
            allItemColumns,
            null,
            null,
            null,
            null,
            ITEM_LIST_COLUMN_COMPLETED + " DESC"
        )
        Log.i("DATA FETCHED!", "Number of mood entries returned: " + cursor.getCount())
        return cursor
    }

    fun create() {
        onCreate(writableDatabase)
    }

    fun clear() {
        writableDatabase.execSQL("DROP TABLE IF EXISTS $ITEM_LIST_TABLE_NAME")
    }


    private fun withinDates(weekStart: List<Int>, weekEnd: List<Int>, todoItem: TodoItem) : Boolean{
        val cal = Calendar.getInstance()
        cal.timeInMillis = todoItem.scheduled
        val day = cal.get(DAY_OF_MONTH)
        val month = cal.get(MONTH) +1
        val year = cal.get(YEAR)


        val afterStart = ((year >= weekStart[2]) && (month >=weekStart[1]) && (day >=weekStart[0]))
        val beforeEnd =   ((year <= weekEnd[2]) && (month <=weekEnd[1]) && (day <=weekEnd[0]))

        val bool = (afterStart && beforeEnd)
        return afterStart && beforeEnd
    }


    fun fetchItemData(weekStart: List<Int>, weekEnd: List<Int>): ArrayList<TodoItem> {

        var todoItems = ArrayList<TodoItem>()
        val cursor = listItems()

        val fromIdColumn = cursor.getColumnIndex(ITEM_LIST_COLUMN_ID)
        val fromScheduledColumn = cursor.getColumnIndex(ITEM_LIST_COLUMN_SCHEDULED)

        val fromTaskColumn = cursor.getColumnIndex(ITEM_LIST_COLUMN_TASK)
        val fromCompletedColumn = cursor.getColumnIndex(ITEM_LIST_COLUMN_COMPLETED)
        val fromCreatedColumn =
            cursor.getColumnIndex(ITEM_LIST_COLUMN_CREATED)
        val fromPriorityColumn =
            cursor.getColumnIndex(ITEM_LIST_COLUMN_PRIORITY)

        if (cursor.getCount() == 0) {
            Log.i("NO TASK ENTRIES", "Fetched data and found none.")
        } else {
            while (cursor.moveToNext()) {
                val nextItem = TodoItem(
                    cursor.getString(fromTaskColumn),
                    cursor.getInt(fromPriorityColumn),
                    cursor.getLong(fromCreatedColumn),
                    cursor.getInt(fromCompletedColumn),
                    cursor.getLong(fromScheduledColumn),
                    cursor.getInt(fromIdColumn)
                )
                if (weekStart != weekEnd){
                    var bool = withinDates(weekStart, weekEnd, nextItem)
                    if (bool){
                        todoItems.add(nextItem)
                    }
                }else{
                    var bool = sameDate(weekStart, nextItem)
                    if (bool){
                        todoItems.add(nextItem)
                    }
                }


            }
        }
        return todoItems
    }

    private fun sameDate(weekStart: List<Int>, todoItem: TodoItem): Boolean {
        val cal = Calendar.getInstance()
        cal.timeInMillis = todoItem.scheduled
        val day = cal.get(DAY_OF_MONTH)
        val month = cal.get(MONTH) +1
        val year = cal.get(YEAR)

        val sameDay = ((year == weekStart[2]) && (month ==weekStart[1]) && (day ==weekStart[0]))

        return sameDay
    }


    fun fetchItemData(): ArrayList<TodoItem> {
        var todoItems = ArrayList<TodoItem>()
        val cursor = listItems()

        val fromIdColumn = cursor.getColumnIndex(ITEM_LIST_COLUMN_ID)
        val fromScheduledColumn = cursor.getColumnIndex(ITEM_LIST_COLUMN_SCHEDULED)

        val fromTaskColumn = cursor.getColumnIndex(ITEM_LIST_COLUMN_TASK)
        val fromCompletedColumn = cursor.getColumnIndex(ITEM_LIST_COLUMN_COMPLETED)
        val fromCreatedColumn =
            cursor.getColumnIndex(ITEM_LIST_COLUMN_CREATED)
        val fromPriorityColumn =
            cursor.getColumnIndex(ITEM_LIST_COLUMN_PRIORITY)

        if (cursor.getCount() == 0) {
            Log.i("NO TASK ENTRIES", "Fetched data and found none.")
        } else {
            Log.i("TASKS FETCHED!", "Fetched data and found task entries.")
            while (cursor.moveToNext()) {
                val nextItem = TodoItem(
                    cursor.getString(fromTaskColumn),
                    cursor.getInt(fromPriorityColumn),
                    cursor.getLong(fromCreatedColumn),
                    cursor.getInt(fromCompletedColumn),
                    cursor.getLong(fromScheduledColumn),
                    cursor.getInt(fromIdColumn)

                    )
                todoItems.add(nextItem)
            }
        }
        Log.i("HERE", "CALLED")
        return todoItems
    }

    companion object {
        private const val DATABASE_VERSION = 16
        const val DATABASE_NAME = "todo_database"
        const val ITEM_LIST_TABLE_NAME = "item_list"
        const val ITEM_LIST_COLUMN_ID = "_id"
        const val ITEM_LIST_COLUMN_TASK = "task"
        const val ITEM_LIST_COLUMN_COMPLETED = "completed"
        const val ITEM_LIST_COLUMN_PRIORITY = "priority"
        const val ITEM_LIST_COLUMN_CREATED = "created_at"
        const val ITEM_LIST_COLUMN_SCHEDULED = "scheduled"

        val allItemColumns = arrayOf<String>(
            ITEM_LIST_COLUMN_ID,
            ITEM_LIST_COLUMN_TASK,
            ITEM_LIST_COLUMN_SCHEDULED,
            ITEM_LIST_COLUMN_CREATED,
            ITEM_LIST_COLUMN_PRIORITY,
            ITEM_LIST_COLUMN_COMPLETED
        )

        val createItemListTableQuery = "CREATE TABLE $ITEM_LIST_TABLE_NAME " +
                "(${ITEM_LIST_COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ITEM_LIST_COLUMN_TASK TEXT, " +
                "$ITEM_LIST_COLUMN_SCHEDULED INTEGER, " +
                "$ITEM_LIST_COLUMN_CREATED INTEGER, " +
                "$ITEM_LIST_COLUMN_PRIORITY INTEGER, " +
                "$ITEM_LIST_COLUMN_COMPLETED INTEGER);"

        val dropItemListTableQuery = "DROP TABLE IF EXISTS $ITEM_LIST_TABLE_NAME"

    }

}