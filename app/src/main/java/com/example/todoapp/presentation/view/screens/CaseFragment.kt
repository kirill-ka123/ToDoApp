package com.example.todoapp.presentation.view.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.databinding.CaseFragmentBinding
import com.example.todoapp.presentation.TodoApplication
import com.example.todoapp.presentation.di.CaseFragmentComponent
import com.example.todoapp.presentation.view.viewmodels.CaseViewModel
import com.example.todoapp.presentation.view.viewmodels.CaseViewModelFactory
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

    private var _binding: CaseFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        caseFragmentComponent =
            (requireContext().applicationContext as TodoApplication).appComponent.caseFragmentComponent()
                .create()
        caseFragmentComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CaseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        caseViewController = caseViewControllerFactory.create(this, view, binding, caseViewModel, args)
        caseViewController?.setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        caseViewController = null
        _binding = null
    }
}