package com.redmadrobot.recyclerviewmarginitemdecorators

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.redmadrobot.recyclerviewmarginitemdecorators.grid.GridExamplePageFragment
import com.redmadrobot.recyclerviewmarginitemdecorators.linear.LinearExamplePageFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = Adapter()
        tabLayout.setupWithViewPager(viewPager)

    }

    inner class Adapter : FragmentPagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> LinearExamplePageFragment.create(true)
                1 -> LinearExamplePageFragment.create(false)
                2 -> GridExamplePageFragment.create(true)
                3 -> GridExamplePageFragment.create(false)
                else -> Fragment()
            }

        override fun getCount(): Int = 4

        override fun getPageTitle(position: Int): CharSequence? =
            when (position) {
                0 -> "Linear vertical"
                1 -> "Linear horizontal"
                2 -> "Grid vertical"
                3 -> "Grid horizontal"
                else -> ""
            }
    }
}
