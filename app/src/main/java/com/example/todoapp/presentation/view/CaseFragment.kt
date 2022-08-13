package com.example.todoapp.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.presentation.TodoApplication
import com.example.todoapp.presentation.di.CaseFragmentComponent
import javax.inject.Inject

class CaseFragment : Fragment(R.layout.case_fragment) {
    private lateinit var caseFragmentComponent: CaseFragmentComponent

    @Inject
    lateinit var caseViewControllerFactory: CaseViewController.Factory
    private var caseViewController: CaseViewController? = null
    private val args: CaseFragmentArgs by navArgs()

    @Inject
    lateinit var caseViewModelFactory: CaseViewModelFactory
    private val caseViewModel: CaseViewModel by viewModels {
        caseViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        caseFragmentComponent =
            (requireContext().applicationContext as TodoApplication).appComponent.caseFragmentComponent()
                .create()
        caseFragmentComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        caseViewController = caseViewControllerFactory.create(this, view, caseViewModel, args)
        caseViewController?.setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        caseViewController = null
    }
}