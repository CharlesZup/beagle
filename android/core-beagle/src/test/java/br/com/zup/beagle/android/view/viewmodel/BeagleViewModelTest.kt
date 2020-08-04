/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.android.view.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.data.ActionRequester
import br.com.zup.beagle.android.data.ComponentRequester
import br.com.zup.beagle.android.exception.BeagleException
import br.com.zup.beagle.android.networking.exception.BeagleApiException
import br.com.zup.beagle.android.testutil.CoroutineTestRule
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.view.ScreenRequest
import br.com.zup.beagle.core.ServerDrivenComponent
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BeagleViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val scope = CoroutineTestRule()

    @MockK
    private lateinit var component: ServerDrivenComponent

    @MockK
    private lateinit var action: Action

    @MockK
    private lateinit var componentRequester: ComponentRequester

    @MockK
    private lateinit var actionRequester: ActionRequester

    @MockK
    private lateinit var observer: Observer<ViewState>

    @InjectMockKs
    private lateinit var beagleUIViewModel: BeagleViewModel


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { componentRequester.fetchComponent(any()) } returns component
        coEvery { actionRequester.fetchAction(any()) } returns action
        every { observer.onChanged(any()) } just Runs
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun fetch_should_return_render_ViewState() {
        // Given
        val screenRequest = ScreenRequest(RandomData.httpUrl())

        // When
        beagleUIViewModel.fetchComponent(screenRequest).observeForever(observer)

        // Then
        verifyOrder {
            observer.onChanged(ViewState.Loading(true))
            observer.onChanged(ViewState.DoRender(screenRequest.url, component))
            observer.onChanged(ViewState.Loading(false))
        }
    }

    @Test
    fun fetch_should_return_a_error_ViewState() {
        // Given
        val screenRequest = ScreenRequest(RandomData.httpUrl())
        val exception = BeagleApiException(mockk())
        coEvery { componentRequester.fetchComponent(any()) } throws exception

        // When
        beagleUIViewModel.fetchComponent(screenRequest).observeForever(observer)

        // Then
        verifyOrder {
            observer.onChanged(ViewState.Loading(true))
            observer.onChanged(any<ViewState.Error>())
            observer.onChanged(ViewState.Loading(false))
        }
    }

    @Test
    fun fetch_should_return_a_error_ViewState_retry() {
        // Given
        val screenRequest = ScreenRequest(RandomData.httpUrl())
        val exception = BeagleApiException(mockk())
        val slotViewState = mutableListOf<ViewState>()
        every { observer.onChanged(capture(slotViewState)) } just Runs
        coEvery { componentRequester.fetchComponent(any()) } throws exception andThen component

        // When
        beagleUIViewModel.fetchComponent(screenRequest,null).observeForever(observer)
        (slotViewState[1] as ViewState.Error).retry.invoke()

        // Then
        verifyOrder {
            observer.onChanged(ViewState.Loading(true))
            observer.onChanged(any<ViewState.Error>())
            observer.onChanged(ViewState.Loading(false))
            observer.onChanged(ViewState.Loading(true))
            observer.onChanged(ViewState.DoRender(screenRequest.url, component))
            observer.onChanged(ViewState.Loading(false))
        }
    }
}
