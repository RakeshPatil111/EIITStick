package com.android.stickerpocket.presentation.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.android.stickerpocket.R
import com.android.stickerpocket.utils.Type
import com.android.stickerpocket.databinding.FragmentContactUsBinding

class ContactUsFragment : Fragment() {

    private lateinit var binding: FragmentContactUsBinding
    private lateinit var categoryAdapter: ArrayAdapter<Type>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

        }

        categoryAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.cv_type_item,
            arrayListOf(Type("1","One"), Type("1","Two"))
        )
        binding.actvChooseATopic.setAdapter(categoryAdapter)
    }
}