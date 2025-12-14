package com.example.a2faproject

import com.data.TokenRepository
import com.example.a2faproject.viewmodel.TokenViewModel
import com.model.Token
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class TokenViewModelMockitoTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun addToken_normalizesSecret_andCallsRepositoryInsert() = runTest {
        val tokensFlow = MutableStateFlow<List<Token>>(emptyList())

        val repo = mock<TokenRepository> {
            on { allTokens } doReturn tokensFlow
        }

        val viewModel = TokenViewModel(
            repository = repo,
            startTicker = false
        )

        viewModel.addToken(
            issuer = "GitHub",
            accountName = "me@example.com",
            secretKey = " abcd ef  "
        )

        advanceUntilIdle()

        val captor = argumentCaptor<Token>()
        verify(repo).insert(captor.capture())

        val inserted = captor.firstValue
        org.junit.Assert.assertEquals("GitHub", inserted.issuer)
        org.junit.Assert.assertEquals("me@example.com", inserted.accountName)
        org.junit.Assert.assertEquals("ABCDEF", inserted.secretKey)
        org.junit.Assert.assertTrue("remoteId should be generated", inserted.remoteId.isNotBlank())
    }

    @Test
    fun updateToken_callsRepositoryUpdate_withNormalizedFields() = runTest {
        val tokensFlow = MutableStateFlow<List<Token>>(emptyList())

        val repo = mock<TokenRepository> {
            on { allTokens } doReturn tokensFlow
        }

        val viewModel = TokenViewModel(
            repository = repo,
            startTicker = false
        )

        val existing = Token(
            id = 10,
            remoteId = "rid-1",
            issuer = "Old",
            accountName = "old@x.com",
            secretKey = "AAAAAA"
        )

        viewModel.updateToken(
            token = existing,
            issuer = "  New Issuer ",
            accountName = " new@x.com ",
            secretKey = " abcd ef "
        )

        advanceUntilIdle()

        val captor = argumentCaptor<Token>()
        verify(repo).update(captor.capture())

        val updated = captor.firstValue
        org.junit.Assert.assertEquals(10, updated.id)
        org.junit.Assert.assertEquals("rid-1", updated.remoteId)
        org.junit.Assert.assertEquals("New Issuer", updated.issuer)
        org.junit.Assert.assertEquals("new@x.com", updated.accountName)
        org.junit.Assert.assertEquals("ABCDEF", updated.secretKey)
    }
}


