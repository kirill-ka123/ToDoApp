package com.example.todoapp.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.TodoApplication
import com.example.todoapp.ioc.CaseFragmentComponent
import com.example.todoapp.ioc.CaseFragmentViewComponent

class CaseFragment : Fragment(R.layout.case_fragment) {
    private val applicationComponent
        get() = TodoApplication.get(requireContext()).applicationComponent
    private lateinit var fragmentComponent: CaseFragmentComponent
    private var fragmentViewComponent: CaseFragmentViewComponent? = null


    private val caseViewModel: CaseViewModel by viewModels {
        applicationComponent.caseViewModelFactory
    }
    private val args: CaseFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent = CaseFragmentComponent(
            applicationComponent,
            fragment = this,
            viewModel = caseViewModel,
            args = args
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentViewComponent = CaseFragmentViewComponent(
            fragmentComponent,
            root = view,
        ).apply {
            caseViewController.setupViews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentViewComponent = null
    }
}