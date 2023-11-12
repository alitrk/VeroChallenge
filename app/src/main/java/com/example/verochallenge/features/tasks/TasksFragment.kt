package com.example.verochallenge.features.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verochallenge.R
import com.example.verochallenge.data.model.Task
import com.example.verochallenge.databinding.FragmentTasksBinding
import com.example.verochallenge.util.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment() {
    private val viewModel: TasksViewModel by viewModels()
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var tasksAdapter: TasksAdapter
    private var taskList: List<Task> = emptyList()
    private lateinit var searchView: SearchView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val view = binding.root

        /*val periodicWorkRequest = PeriodicWorkRequestBuilder<CustomWorker>(
            repeatInterval = 60, // repeat every 60 minutes
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setInitialDelay(1, timeUnit = TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            CustomWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )*/

        tasksAdapter = TasksAdapter(requireContext())
        binding.apply {
            recyclerView.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.onManualRefresh()
            }
            buttonRetry.setOnClickListener {
                viewModel.onManualRefresh()
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is TasksViewModel.Event.ShowErrorMessage -> {
                                Snackbar.make(
                                    requireView(),
                                    getString(
                                        R.string.could_not_refresh,
                                        event.error.localizedMessage
                                            ?: getString(R.string.unknown_error_occurred)
                                    ),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.taskItems.collect { result ->
                val tasks =
                    result.data ?: emptyList()
                binding.apply {
                    swipeRefreshLayout.isRefreshing = result is Resource.Loading
                    recyclerView.isVisible = tasks.isNotEmpty()
                    textViewError.isVisible = result.error != null && tasks.isEmpty()
                    buttonRetry.isVisible = result.error != null && tasks.isEmpty()
                    textViewError.text = getString(
                        R.string.could_not_refresh,
                        result.error?.localizedMessage
                            ?: getString(R.string.unknown_error_occurred)
                    )
                    taskList = tasks
                    tasksAdapter.submitList(result.data) {
                        if (viewModel.pendingScrollToTopAfterRefresh) {
                            recyclerView.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }

                }

            }
        }
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query.orEmpty())
                binding.recyclerView.scrollToPosition(0)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterList(query.orEmpty())
                return true
            }
        })
        val qrCodeItem = menu.findItem(R.id.action_qr_code)
        qrCodeItem.setOnMenuItemClickListener {
            // Handle QR code scanning here
            searchView.isIconified = false
            startQRCodeScanner()
            searchItem.expandActionView()
            true
        }
    }

    private fun filterList(query: String) {
        val filteredList = taskList.filter { task ->
            (task.tasks.title?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.description?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.businessUnitKey?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.businessUnit?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.colorCode?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.parentTaskID?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.prePlanningBoardQuickSelect?.toString()
                        ?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.sort?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.task?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.wageType?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.workingTime?.toString()?.contains(query, ignoreCase = true) == true)
        }
        tasksAdapter.submitList(filteredList.toList())
    }

    private fun startQRCodeScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val scannedContent = result.contents
            searchView.onActionViewExpanded()

            searchView.isIconified = false
            searchView.requestFocus()
            searchView.setQuery(scannedContent, false)
            filterList(scannedContent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}