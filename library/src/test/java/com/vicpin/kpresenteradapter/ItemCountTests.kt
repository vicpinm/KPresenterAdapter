package com.vicpin.kpresenteradapter

import android.widget.Adapter
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.ParameterizedRobolectricTestRunner


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class ItemCountTests(val config: AdapterConfig) {

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters(name = "Test = {0}")

        @JvmStatic
        fun data() = listOf(
                arrayOf(AdapterConfig(0, 0, false)),
                arrayOf(AdapterConfig(0, 0, true)),
                arrayOf(AdapterConfig(1, 0, false)),
                arrayOf(AdapterConfig(0, 1, false)),
                arrayOf(AdapterConfig(1, 0, true)),
                arrayOf(AdapterConfig(0, 1, true)),
                arrayOf(AdapterConfig(1, 1, false)),
                arrayOf(AdapterConfig(1, 1, true)),
                arrayOf(AdapterConfig(0, 10, true)),
                arrayOf(AdapterConfig(0, 10, false)),
                arrayOf(AdapterConfig(10, 10, true)),
                arrayOf(AdapterConfig(10, 10, false)),
                arrayOf(AdapterConfig(10, 0, true)),
                arrayOf(AdapterConfig(10, 0, false)))

    }

    @Test
    fun adapterShouldCointainsItemCountExpected() {
        //Given
        val presenter = givenASimplePresenterWith(config)

        //Then
        val itemCountExpected = config.dataSize + config.headersSize + if (config.hasLoadMoreEnabled) 1 else 0
        assertThat(presenter.itemCount).isEqualTo(itemCountExpected)
    }

    @Test
    fun adapterShouldCointainsHeadersCountExpected() {
        //Given
        val presenter = givenASimplePresenterWith(config)

        //Then
        val itemCountExpected = config.headersSize
        assertThat(presenter.getHeadersCount()).isEqualTo(itemCountExpected)
    }


    @Test
    fun when_clearAdapterData_then_adapterShouldContainsOnlyHeadersAndLoadMore() {
        //Given
        val presenter = givenASimplePresenterWith(config)

        //When
        presenter.clearData()

        //Then
        val itemCountExpected = config.headersSize + + if (config.hasLoadMoreEnabled) 1 else 0
        assertThat(presenter.itemCount).isEqualTo(itemCountExpected)
    }

    @Test
    fun when_removeFirstItem_then_adapterShouldContainsReturnItemCountExpected() {
        //Given
        val presenter = givenASimplePresenterWith(config)

        //When
        presenter.removeItem(0)

        //Then
        val itemCountExpected = (if(config.dataSize > 0) config.dataSize - 1 else 0) + config.headersSize + + if (config.hasLoadMoreEnabled) 1 else 0
        assertThat(presenter.itemCount).isEqualTo(itemCountExpected)
    }

    @Test
    fun when_removeLastItem_then_adapterShouldContainsReturnItemCountExpected() {
        //Given
        val presenter = givenASimplePresenterWith(config)

        //When
        presenter.removeItem(config.dataSize - 1)

        //Then
        val itemCountExpected = (if(config.dataSize > 0) config.dataSize - 1 else 0) + config.headersSize + + if (config.hasLoadMoreEnabled) 1 else 0
        assertThat(presenter.itemCount).isEqualTo(itemCountExpected)
    }

    @Test
    fun when_removeItemInCollection_then_adapterShouldContainsReturnItemCountExpected() {
        //Given
        val presenter = givenASimplePresenterWith(config)

        //When
        presenter.removeItem(presenter.getData().firstOrNull() ?: "NULL")

        //Then
        val itemCountExpected = (if(config.dataSize > 0) config.dataSize - 1 else 0) + config.headersSize + + if (config.hasLoadMoreEnabled) 1 else 0
        assertThat(presenter.itemCount).isEqualTo(itemCountExpected)
    }

    @Test
    fun when_removeItemNotInCollection_then_adapterShouldContainsReturnItemCountExpected() {
        //Given
        val presenter = givenASimplePresenterWith(config)

        //When
        presenter.removeItem("NULL") //Try to remove non-existing item

        //Then
        val itemCountExpected = config.dataSize + config.headersSize + if (config.hasLoadMoreEnabled) 1 else 0
        assertThat(presenter.itemCount).isEqualTo(itemCountExpected)
    }

    private fun givenASimplePresenterWith(config: AdapterConfig): PresenterAdapter<String> {
        val mockStringViewholder = mock(ViewHolder::class.java) as ViewHolder<String>
        val presenter = SimplePresenterAdapter(mockStringViewholder::class, 0)
        presenter.setData(List(config.dataSize) { "$it" })
        for (i in 0 until config.headersSize) {
            presenter.addHeader(0)
        }
        if (config.hasLoadMoreEnabled) {
            presenter.enableLoadMore { }
        }
        return presenter
    }
}

data class AdapterConfig(val dataSize: Int, val headersSize: Int, val hasLoadMoreEnabled: Boolean)


