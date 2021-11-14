package com.example.taskyourtime

import DefaultFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.taskyourtime.calendar.CalendarViewActivity
import com.example.taskyourtime.note.ListNoteActivity
import com.example.taskyourtime.todolist.ToDoListActivity
import com.google.android.material.tabs.TabLayout

class DefaultActivity : FragmentActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(mPager)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

    }

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            mPager.currentItem = mPager.currentItem - 1
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        //NB pages
        override fun getCount(): Int = 5

        override fun getItem(position: Int): Fragment {
            Log.d("Note","GetItem called")
            val view : Fragment
            when(position) {
                0 -> view = CalendarViewActivity()
                1 -> view = ListNoteActivity()
                2 ->  view = ToDoListActivity()
                3 -> view = DefaultFragment()
                4 -> view = DefaultFragment()
                else -> view = DefaultFragment()
            }
            return view

        }

        override fun getPageTitle(position: Int): CharSequence {
            Log.d("Note","GetPageTitle called")
            val title : CharSequence = when(position) {
                0 -> "Calendrier"
                1 -> "Notes"
                2 -> "ToDoList"
                3 -> "Mode productif"
                4 -> "Groupes"
                else -> {
                    "Default"
                }
            }
            return title
        }

        //TODO : convert activities to fragment and get title
    }

}
