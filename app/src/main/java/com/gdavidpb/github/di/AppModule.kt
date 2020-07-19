package com.gdavidpb.github.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.gdavidpb.github.BuildConfig
import com.gdavidpb.github.data.source.GitHubDataRepository
import com.gdavidpb.github.data.source.GitHubDataStoreFactory
import com.gdavidpb.github.data.source.local.GitHubCacheDataStore
import com.gdavidpb.github.data.source.local.GitHubDatabase
import com.gdavidpb.github.data.source.remote.GitHubApi
import com.gdavidpb.github.data.source.remote.GitHubRemoteDataStore
import com.gdavidpb.github.domain.repository.VCSRepository
import com.gdavidpb.github.domain.usecase.FetchRepositoriesUseCase
import com.gdavidpb.github.domain.usecase.GetPullsUseCase
import com.gdavidpb.github.presentation.viewModels.PullsViewModel
import com.gdavidpb.github.presentation.viewModels.RepositoriesViewModel
import com.gdavidpb.github.ui.adapters.PagedRepositoryAdapter
import com.gdavidpb.github.ui.adapters.PullAdapter
import com.gdavidpb.github.utils.DATABASE_NAME
import com.gdavidpb.github.utils.create
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import org.koin.experimental.builder.factoryBy
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    /* Android Services */

    single {
        androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    /* Retrofit */

    single {
        OkHttpClient.Builder()
            .callTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>()
            .create<GitHubApi>()
    }

    /* Database */

    single {
        Room.databaseBuilder(
            androidContext(),
            GitHubDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    /* Picasso */

    single {
        Picasso.get()
    }

    /* View models */

    viewModel<RepositoriesViewModel>()
    viewModel<PullsViewModel>()

    /* Use cases */

    factory<FetchRepositoriesUseCase>()
    factory<GetPullsUseCase>()

    /* Repositories */

    factoryBy<VCSRepository, GitHubDataRepository>()

    /* Data stores */

    factory<GitHubCacheDataStore>()
    factory<GitHubRemoteDataStore>()

    /* Factory */

    factory<GitHubDataStoreFactory>()

    /* Adapters */

    factory { (manager: PagedRepositoryAdapter.AdapterManager) ->
        PagedRepositoryAdapter(manager)
    }

    factory { (manager: PullAdapter.AdapterManager) ->
        PullAdapter(manager)
    }
}