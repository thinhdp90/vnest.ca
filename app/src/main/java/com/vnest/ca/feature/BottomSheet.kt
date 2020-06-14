package com.vnest.ca.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vnest.ca.R
import com.vnest.ca.activities.MainActivity
import com.vnest.ca.feature.home.AdapterHomeItemDefault
import com.vnest.ca.feature.home.AdapterHomeItemDefault.ItemClickListener
import com.vnest.ca.feature.home.AdapterHomeItemDefault.OnProcessingText
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import java.util.*

class BottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.mRecyclerView.adapter
        val adapter = AdapterHomeItemDefault(context, (Objects.requireNonNull(activity) as MainActivity).getTextToSpeech(), OnProcessingText { text: String? -> (Objects.requireNonNull(activity) as MainActivity).processing_text(text) })
        adapter.itemClickListener = ItemClickListener { _: Int, name: String? -> (Objects.requireNonNull(activity) as MainActivity).processing_text(name) }
        view.mRecyclerView.adapter = adapter
        view.mRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
    }
}