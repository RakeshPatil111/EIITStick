package com.android.stickerpocket.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.FragmentContactUsBinding
import com.android.stickerpocket.utils.CustomDialog
import com.android.stickerpocket.utils.Type


class ContactUsFragment : Fragment() {

    private lateinit var binding: FragmentContactUsBinding
    private lateinit var categoryAdapter: ArrayAdapter<Type>
    var selectedmedia= MutableLiveData(ArrayList<Uri>())
    var selectedItem=""
    var mediaAttached=false
    // Registers a photo picker activity launcher in multi-select mode.
    // In this example, the app lets the user select up to 5 media files.
    var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult<PickVisualMediaRequest, List<Uri>>(PickMultipleVisualMedia(5),
            ActivityResultCallback<List<Uri>> { uris: List<Uri> ->
                // Callback is invoked after the user selects media items or closes the
                // photo picker.
                if (!uris.isEmpty()) {
                    selectedmedia.value?.addAll(uris)
                    Log.d("PhotoPicker", "Number of items selected: " + uris.size)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            })
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
            imagePickerLL.setOnClickListener {
                /*For this example, launch the photo picker and let the user choose images and videos.
                 If you want the user to select a specific type of media file,use the overloaded versions of launch(),
                 as shown in the section about howto select a single media item.*/
                pickMultipleMedia.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ImageAndVideo)
                        .build()
                )
            }

            actvChooseATopic.setOnItemClickListener { adapterView, view, position, l ->

                selectedItem = categoryAdapter.getItem(position)?.typeName.toString()
                actvChooseATopic.clearFocus()

                if (selectedItem=="Other"){
                    tilOther.visibility=View.VISIBLE
                }else{
                    tilOther.visibility=View.GONE
                }

            }

            btnSubmit.setOnClickListener {

                if (selectedmedia.value?.size != 0) {
                    mediaAttached = true
                }
                if (selectedItem.isEmpty()) {
                    CustomDialog.showCustomDialog(
                        requireContext(),
                        resources.getString(R.string.select_topic),
                        resources.getString(R.string.ok)
                    )
                } else if (selectedItem == "Other" && tietOther.text.toString().isEmpty()) {
                    CustomDialog.showCustomDialog(
                        requireContext(),
                        resources.getString(R.string.enter_other_topic),
                        resources.getString(R.string.ok)
                    )
                } else if (tietSubject.getText().toString().isEmpty()) {
                    CustomDialog.showCustomDialog(
                        requireContext(),
                        resources.getString(R.string.please_enter_subject),
                        resources.getString(R.string.ok)
                    )
                } else if (tietDescription.getText().toString().isEmpty()) {
                    CustomDialog.showCustomDialog(
                        requireContext(),
                        resources.getString(R.string.please_enter_description),
                        resources.getString(R.string.ok)
                    )
                } else if (!mediaAttached) {
                    if (selectedmedia.value?.size == 0) {
                        CustomDialog.showCustomDialog(
                            requireContext(),
                            resources.getString(R.string.please_attach_media),
                            resources.getString(R.string.ok)
                        )
                        mediaAttached = true
                    }
                } else {

                    val emailsend: String = tietSubject.getText().toString()
                    val emailsubject: String = tietSubject.getText().toString()
                    val emailbody: String = tietDescription.getText().toString()


                    // define Intent object with action attribute as ACTION_SEND
                    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)


                    // add three fields to intent using putExtra function
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailsend))
                    intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject)
                    intent.putExtra(Intent.EXTRA_TEXT, emailbody)
                    //intent.putExtra(Intent.EXTRA_STREAM, selectedmedia.value?.get(0))

                    // set type of intent
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedmedia.value);
                    intent.setType("*/*");


                    // startActivity with intent with chooser as Email client using createChooser function
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"))

                }
            }

        }


        categoryAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.cv_type_item,
            arrayListOf(Type("1","Bug"),
                  Type("1","Suggestion"),
                  Type("1","Feedback"),
                  Type("1","Comment"),
                  Type("1","Other")

            )
        )
        binding.actvChooseATopic.setAdapter(categoryAdapter)
    }


    }