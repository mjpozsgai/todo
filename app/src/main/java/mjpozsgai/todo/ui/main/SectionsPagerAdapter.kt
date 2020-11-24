package mjpozsgai.todo.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mjpozsgai.todo.R

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        var fragmentToPresent = when (position) {
            0 -> ItemFragment.newInstance(0)
            1 -> WeeklyFragment.newInstance(position + 1)
            2 -> DailyFragment.newInstance(position + 1)
            else -> DailyFragment.newInstance(position+1)
        }

        return fragmentToPresent
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }


    override fun getCount(): Int {
        return 3
    }
}