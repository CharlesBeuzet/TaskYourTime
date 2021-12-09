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
import com.example.taskyourtime.group.ListGroupActivity
import com.example.taskyourtime.note.ListNoteActivity
import com.example.taskyourtime.productivity.ProductivityActivity
import com.example.taskyourtime.todolist.ToDoListActivity
import com.google.android.material.tabs.TabLayout

class DefaultActivity : FragmentActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        mPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(mPager)

        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

    }

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            mPager.currentItem = mPager.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 5

        override fun getItem(position: Int): Fragment {
            Log.d("Note","GetItem called")
            val view : Fragment
            when(position) {
                0 -> view = CalendarViewActivity()
                1 -> view = ListNoteActivity()
                2 ->  view = ToDoListActivity()
                3 -> view = ProductivityActivity()
                4 -> view = ListGroupActivity()
                else -> view = DefaultFragment()
            }
            return view

        }

        override fun getPageTitle(position: Int): CharSequence {
            Log.d("Note","GetPageTitle called")
            val title : CharSequence = when(position) {
                0 -> "Calendar"
                1 -> "Notes"
                2 -> "ToDoList"
                3 -> "Productive Mode"
                4 -> "Groups"
                else -> {
                    "Default"
                }
            }
            return title
        }
    }

}
