package com.example.verochallenge.features.tasks

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verochallenge.R
import com.example.verochallenge.data.model.Task
import com.example.verochallenge.databinding.FragmentTasksBinding
import com.example.verochallenge.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment() {
    private val viewModel: TasksViewModel by viewModels()
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var tasksAdapter: TasksAdapter
    private var taskList: List<Task> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val view = binding.root
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
        }
        lifecycleScope.launch {
            viewModel.taskItems.collect { result ->
                val tasks =
                    result.data ?: emptyList() // Use Elvis operator to provide default empty list
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
        val searchView = searchItem.actionView as SearchView
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
    }


    private fun filterList(query: String) {
        val filteredList = taskList.filter { task ->
            (task.tasks.title?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.description?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.businessUnitKey?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.businessUnit?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.colorCode?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.parentTaskID?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.prePlanningBoardQuickSelect?.toString()?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.sort?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.task?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.wageType?.contains(query, ignoreCase = true) == true) ||
                    (task.tasks.workingTime?.toString()?.contains(query, ignoreCase = true) == true)

        }
        tasksAdapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}