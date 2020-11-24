package mjpozsgai.todo

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import mjpozsgai.todo.ui.main.SectionsPagerAdapter
import mjpozsgai.todo.ui.main.TaskEntryFragment

class MainActivity : AppCompatActivity(), Updatable {
    lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    lateinit var viewPager: ViewPager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        viewPager = findViewById(R.id.view_pager) as ViewPager
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val existingTaskEntryFragment = supportFragmentManager.findFragmentByTag("task_entry")
            if (existingTaskEntryFragment != null) {
                fragmentTransaction.remove(existingTaskEntryFragment)
            }
            fragmentTransaction.addToBackStack(null)

            val dialogFragment = TaskEntryFragment()
            dialogFragment.dismissListener = this
            dialogFragment.show(fragmentTransaction, "task_entry")
        }

    }

    override fun onDismissal() {
        val currentFragment =
            supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem());
        currentFragment?.onResume()
    }

    fun showInfo(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("To add new item to your today list, press the + button at the bottom of the page." +
                "\n \n" +
                "To view all of your tasks, select the All Items tab" +
                "\n" +
                " \n" +
                "To view tasks for a period of days, select the Weekly View tab and select a range of dates" +
                "\n" +
                " \n" +
                "To view tasks for a single day, select the Daily View tab and select a date ")
                .setNegativeButton("Ok", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
                })
        val alert = dialogBuilder.create()
        alert.setTitle("Information")
        alert.show()
    }
}