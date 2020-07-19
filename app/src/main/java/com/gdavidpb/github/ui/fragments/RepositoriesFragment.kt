package com.gdavidpb.github.ui.fragments

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gdavidpb.github.MainNavigationDirections
import com.gdavidpb.github.R
import com.gdavidpb.github.domain.usecase.coroutines.Completable
import com.gdavidpb.github.presentation.model.RepositoryItem
import com.gdavidpb.github.presentation.viewModels.RepositoriesViewModel
import com.gdavidpb.github.ui.adapters.PagedRepositoryAdapter
import com.gdavidpb.github.utils.CircleTransform
import com.gdavidpb.github.utils.isNetworkAvailable
import com.gdavidpb.github.utils.longToast
import com.gdavidpb.github.utils.observe
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_repositories.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RepositoriesFragment : Fragment() {

    private val viewModel: RepositoriesViewModel by viewModel()

    private val picasso: Picasso by inject()

    private val connectivityManager: ConnectivityManager by inject()

    private val repositoryAdapter: PagedRepositoryAdapter by inject {
        parametersOf(RepositoryManager())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_repositories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sRefreshRepository.isEnabled = false

        with(rViewRepository) {
            layoutManager = LinearLayoutManager(context)
            adapter = repositoryAdapter

            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        with(viewModel) {
            observe(fetchRepositories, ::fetchObserver)
            observe(pagedRepositories, ::repositoriesObserver)
        }
    }

    private fun repositoriesObserver(result: PagedList<RepositoryItem>?) {
        repositoryAdapter.submitList(result)
    }

    private fun fetchObserver(result: Completable?) {
        when (result) {
            is Completable.OnLoading -> {
                sRefreshRepository.isRefreshing = true
            }
            is Completable.OnError -> {
                sRefreshRepository.isRefreshing = false

                val errorString = if (connectivityManager.isNetworkAvailable())
                    R.string.toast_error_unexpected
                else
                    R.string.toast_error_network

                longToast(errorString)
            }
            else -> {
                sRefreshRepository.isRefreshing = false
            }
        }
    }

    inner class RepositoryManager : PagedRepositoryAdapter.AdapterManager {
        override fun onRepositoryClicked(item: RepositoryItem) {
            val action = RepositoriesFragmentDirections.actionToNavPulls(
                title = item.name,
                name = item.fullName
            )

            findNavController().navigate(action)
        }

        override fun onUserClicked(item: RepositoryItem) {
            val action = MainNavigationDirections.actionToNavBrowser(
                title = item.userLogin,
                url = item.userUrl
            )

            findNavController().navigate(action)
        }

        override fun loadImage(url: String, imageView: ImageView) {
            if (url.isNotEmpty())
                picasso.load(url)
                    .placeholder(R.mipmap.ic_launcher)
                    .transform(CircleTransform())
                    .into(imageView)
            else
                imageView.setImageResource(R.mipmap.ic_launcher)
        }
    }
}