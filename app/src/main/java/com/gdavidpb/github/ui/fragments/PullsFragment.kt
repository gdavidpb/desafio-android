package com.gdavidpb.github.ui.fragments

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gdavidpb.github.MainNavigationDirections
import com.gdavidpb.github.R
import com.gdavidpb.github.domain.model.Pull
import com.gdavidpb.github.domain.usecase.coroutines.Result
import com.gdavidpb.github.presentation.model.PullItem
import com.gdavidpb.github.presentation.viewModels.PullsViewModel
import com.gdavidpb.github.ui.adapters.PullAdapter
import com.gdavidpb.github.utils.CircleTransform
import com.gdavidpb.github.utils.isNetworkAvailable
import com.gdavidpb.github.utils.observe
import com.gdavidpb.github.utils.toPullItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_pulls.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.support.v4.longToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PullsFragment : Fragment() {
    private val viewModel: PullsViewModel by viewModel()

    private val picasso: Picasso by inject()

    private val connectivityManager: ConnectivityManager by inject()

    private val pullsAdapter: PullAdapter by inject {
        parametersOf(PullManager())
    }

    private val args by navArgs<PullsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pulls, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sRefreshPull.isEnabled = false

        with(rViewPull) {
            layoutManager = LinearLayoutManager(context)
            adapter = pullsAdapter

            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        with(viewModel) {
            observe(pulls, ::pullsObserver)

            getPulls(repository = args.name)
        }
    }

    private fun pullsObserver(result: Result<List<Pull>>?) {
        when (result) {
            is Result.OnLoading -> {
                sRefreshPull.isRefreshing = true
            }
            is Result.OnSuccess -> {
                sRefreshPull.isRefreshing = false

                val pulls = result.value.map { it.toPullItem() }

                pullsAdapter.swapItems(new = pulls)

                if (pulls.isNotEmpty()) {
                    tViewPullEmpty.visibility = View.GONE
                    rViewPull.visibility = View.VISIBLE
                } else {
                    rViewPull.visibility = View.GONE
                    tViewPullEmpty.visibility = View.VISIBLE
                }
            }
            is Result.OnError -> {
                sRefreshPull.isRefreshing = false

                val errorString = if (connectivityManager.isNetworkAvailable())
                    R.string.toast_error_unexpected
                else
                    R.string.toast_error_network

                longToast(errorString)
            }
            else -> {
                sRefreshPull.isRefreshing = false
            }
        }
    }

    inner class PullManager : PullAdapter.AdapterManager {
        override fun onPullClicked(item: PullItem) {
            val action = MainNavigationDirections.actionToNavBrowser(
                title = item.title,
                url = item.url
            )

            findNavController().navigate(action)
        }

        override fun onUserClicked(item: PullItem) {
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
                imageView.imageResource = R.mipmap.ic_launcher
        }
    }
}