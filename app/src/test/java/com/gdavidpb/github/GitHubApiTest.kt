package com.gdavidpb.github

import com.gdavidpb.github.data.source.remote.GitHubApi
import com.gdavidpb.github.utils.getOrThrow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

class GitHubApiTest : AutoCloseKoinTest() {

    private val api: GitHubApi by inject()

    @Before
    fun `start koin`() {
        startKoin {
            modules(testModule)
        }
    }

    @Test
    fun `should get repositories from GitHub api`() {
        val repositories = runBlocking {
            api.getRepositories(page = 1).getOrThrow()
        }

        Assert.assertNotNull(repositories)

        Assert.assertTrue(repositories.total_count > 0)
    }

    @Test
    fun `should get pull requests from first repository`() {
        val repositories = runBlocking {
            api.getRepositories(page = 1).getOrThrow()
        }

        Assert.assertNotNull(repositories)

        Assert.assertTrue(repositories.total_count > 0)

        val firstRepository = repositories.items.first()

        val pulls = runBlocking {
            api.getPulls(repositoryName = firstRepository.full_name).getOrThrow()
        }

        Assert.assertNotNull(pulls)

        /* A repository could has not pull requests */
        Assert.assertTrue(pulls.size >= 0)
    }

    @After
    fun `stop koin`() {
        //StandAloneContext.stopKoin()
    }
}
