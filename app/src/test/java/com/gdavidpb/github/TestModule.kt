package com.gdavidpb.github

import com.gdavidpb.github.data.model.api.UserEntry
import com.gdavidpb.github.data.model.database.EmbeddedUser
import com.gdavidpb.github.data.source.remote.GitHubApi
import com.gdavidpb.github.domain.model.User
import com.gdavidpb.github.utils.create
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val testModule = module {
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

    /* User for testing */

    single {
        User(
            id = 1,
            login = "sample",
            url = "https://github.com",
            avatarUrl = "https://github.com/favicon.ico"
        )
    }

    single {
        UserEntry(
            id = 1,
            login = "sample",
            html_url = "https://github.com",
            avatar_url = "https://github.com/favicon.ico"
        )
    }

    single {
        EmbeddedUser(
            id = 1,
            login = "sample",
            url = "https://github.com",
            avatarUrl = "https://github.com/favicon.ico"
        )
    }
}