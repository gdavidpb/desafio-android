package com.gdavidpb.github.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gdavidpb.github.BuildConfig
import com.gdavidpb.github.data.source.local.GitHubDatabase
import com.gdavidpb.github.domain.usecase.FetchRepositoriesUseCase
import com.gdavidpb.github.presentation.model.RepositoryItem
import com.gdavidpb.github.utils.LiveCompletable
import com.gdavidpb.github.utils.toRepositoryItem

class RepositoriesViewModel(
    private val gitHubDatabase: GitHubDatabase,
    private val fetchRepositoriesUseCase: FetchRepositoriesUseCase
) : ViewModel() {

    val fetchRepositories = LiveCompletable()
    val pagedRepositories = buildLivePagedList()

    private fun buildLivePagedList(): LiveData<PagedList<RepositoryItem>> {
        val boundaryCallback = RepositoryBoundaryCallback()

        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(BuildConfig.API_PAGE_SIZE)
            .build()

        val dataSource =
            gitHubDatabase.repositories.browse().map { it.toRepositoryItem() }

        return LivePagedListBuilder<Int, RepositoryItem>(dataSource, pagedListConfig)
            .setBoundaryCallback(boundaryCallback)
            .setInitialLoadKey(1)
            .build()
    }

    private inner class RepositoryBoundaryCallback : PagedList.BoundaryCallback<RepositoryItem>() {
        override fun onItemAtEndLoaded(itemAtEnd: RepositoryItem) =
            fetchRepositories(itemAtEnd.page + 1)

        override fun onZeroItemsLoaded() =
            fetchRepositories(page = 1)

        private fun fetchRepositories(page: Int) =
            fetchRepositoriesUseCase.execute(liveData = fetchRepositories, params = page)
    }
}