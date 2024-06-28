package com.android.stickerpocket

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.stickerpocket.utils.Tutorials

class ViewPagerAdapter(private val tutorials: ArrayList<Tutorials>, fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = tutorials.size

    override fun createFragment(position: Int): Fragment = ViewpagerFragment.newInstance(tutorials[position], position)
}