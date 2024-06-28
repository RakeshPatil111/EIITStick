package com.android.stickerpocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.android.stickerpocket.databinding.FragmentSlideBinding
import com.android.stickerpocket.utils.Tutorials

class SlideFragment : Fragment() {

    private lateinit var binding: FragmentSlideBinding
    private val args: SlideFragmentArgs by navArgs()
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var infoList: ArrayList<Tutorials> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSlideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoList = args.informationList.toCollection(ArrayList())
        binding.apply {
            viewPagerAdapter = ViewPagerAdapter(infoList, requireActivity())
            viewPager.adapter = viewPagerAdapter
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateTipIndex(position)
                    btnBack.visibility = if (position == 0) View.GONE else View.VISIBLE
                    btnNext.visibility = if (position == viewPagerAdapter.itemCount - 1) View.GONE else View.VISIBLE
                }
            })

            btnNext.setOnClickListener {
                nextPage()
            }

            btnBack.setOnClickListener {
                prePage()
            }

            viewPager.setCurrentItem(0, true)
            updateTipIndex(0)
            btnBack.visibility = if (viewPager.currentItem == 0) View.GONE else View.VISIBLE
            btnNext.visibility = if (viewPager.currentItem == viewPagerAdapter.itemCount - 1) View.GONE else View.VISIBLE
        }
    }

    private fun updateTipIndex(position: Int) {
        binding.tvTutorialIndex.text = "${position + 1}/${infoList.size}"
    }

    private fun nextPage() {
        val nextItem = binding.viewPager.currentItem + 1
        if (nextItem < viewPagerAdapter.itemCount) {
            binding.viewPager.currentItem = nextItem
        }
    }

    private fun prePage() {
        val prevItem = binding.viewPager.currentItem - 1
        if (prevItem >= 0) {
            binding.viewPager.currentItem = prevItem
        }
    }
}