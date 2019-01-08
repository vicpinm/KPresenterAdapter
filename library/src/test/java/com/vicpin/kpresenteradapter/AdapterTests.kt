package com.vicpin.kpresenteradapter

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(AndroidJUnit4::class)
class AdapterTests {

    @Test(expected = IllegalArgumentException::class)
    fun when_tryToSwapFromHeaderPosition_then_exceptionExpected() {
        //Given
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 1, false))

        //When
        presenter.swapItems(0, 1)

        //Then exception expected
    }

    @Test(expected = IllegalArgumentException::class)
    fun when_tryToSwapToHeaderPosition_then_exceptionExpected() {
        //Given
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 1, false))

        //When
        presenter.swapItems(1, 0)

        //Then exception expected
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun when_tryToSwapToPositionBeyondDataSize_then_exceptionExpected() {
        //Given
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 1, false))

        //When
        presenter.swapItems(10, 11
        )

        //Then exception expected
    }

    @Test
    fun when_swapToItems_then_dataCollectionIsUpdated() {
        //Given
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 0, false))

        //When swap item 0 with item 1
        presenter.swapItems(0, 1)

        //Then collection is updated
        assertThat(presenter.getData()[0]).isEqualTo("1")
        assertThat(presenter.getData()[1]).isEqualTo("0")
        assertThat(presenter.getData()).hasSize(10)
    }

    @Test
    fun when_swapFirstAndLastItemWithNoHeader_then_dataCollectionIsUpdated() {
        //Given
        val size = 100
        val lastPosition = size - 1
        val presenter = givenASimplePresenterWith(AdapterConfig(size, 0, false))

        //When swap items
        presenter.swapItems(0, lastPosition)

        //Then collection is updated
        assertThat(presenter.getData()[0]).isEqualTo("$lastPosition")
        assertThat(presenter.getData()[lastPosition]).isEqualTo("0")
        assertThat(presenter.getData()).hasSize(size)
    }

    @Test
    fun when_swapFirstAndLastItemWithHeader_then_dataCollectionIsUpdated() {
        //Given
        val size = 100
        val headersSize = 10
        val lastPosition = size - 1
        val presenter = givenASimplePresenterWith(AdapterConfig(size, headersSize, false))

        //When swap items
        presenter.swapItems(presenter.getPositionWithHeaders(0),
                presenter.getPositionWithHeaders(lastPosition))

        //Then collection is updated
        assertThat(presenter.getData()[0]).isEqualTo("$lastPosition")
        assertThat(presenter.getData()[lastPosition]).isEqualTo("0")
        assertThat(presenter.getData()).hasSize(size)
    }

    @Test
    fun when_swapToSamePosition_then_dataCollectionIsNotUpdated() {
        //Given
        val size = 100
        val positionToMove = 10
        val presenter = givenASimplePresenterWith(AdapterConfig(size, 0, false))

        //When swap items
        presenter.swapItems(positionToMove, positionToMove)

        //Then collection is updated
        assertThat(presenter.getData()[positionToMove]).isEqualTo("$positionToMove")
        assertThat(presenter.getData()).hasSize(size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun when_tryToMoveFromHeaderPosition_then_exceptionExpected() {
        //Given
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 1, false))

        //When
        presenter.moveItem(0, 1)

        //Then exception expected
    }

    @Test(expected = IllegalArgumentException::class)
    fun when_tryToMoveToHeaderPosition_then_exceptionExpected() {
        //Given
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 1, false))

        //When
        presenter.moveItem(1, 0)

        //Then exception expected
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun when_tryToMoveToPositionBeyondDataSize_then_exceptionExpected() {
        //Given
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 1, false))

        //When
        presenter.moveItem(10, 11)

        //Then exception expected
    }

    @Test
    fun when_moveToItems_then_dataCollectionIsUpdated() {
        //Given
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 0, false))

        //When swap item 0 with item 1
        presenter.moveItem(0, 1)

        //Then collection is updated
        assertThat(presenter.getData()[0]).isEqualTo("1")
        assertThat(presenter.getData()[1]).isEqualTo("0")
        assertThat(presenter.getData()).hasSize(10)
    }

    @Test
    fun when_moveFirstAndLastItemWithNoHeader_then_dataCollectionIsUpdated() {
        //Given
        val size = 100
        val lastPosition = size - 1
        val presenter = givenASimplePresenterWith(AdapterConfig(size, 0, false))

        //When swap items
        presenter.moveItem(0, lastPosition)

        //Then collection is updated
        assertThat(presenter.getData()[0]).isEqualTo("1")
        assertThat(presenter.getData()[lastPosition]).isEqualTo("0")
        assertThat(presenter.getData()).hasSize(size)
    }

    @Test
    fun when_moveFirstAndLastItemWithHeader_then_dataCollectionIsUpdated() {
        //Given
        val size = 100
        val headersSize = 10
        val lastPosition = size - 1
        val presenter = givenASimplePresenterWith(AdapterConfig(size, headersSize, false))

        //When swap items
        presenter.moveItem(presenter.getPositionWithHeaders(0),
                presenter.getPositionWithHeaders(lastPosition))

        //Then collection is updated
        assertThat(presenter.getData()[0]).isEqualTo("1")
        assertThat(presenter.getData()[lastPosition]).isEqualTo("0")
        assertThat(presenter.getData()).hasSize(size)
    }

    @Test
    fun when_moveToSamePosition_then_dataCollectionIsNotUpdated() {
        //Given
        val size = 100
        val positionToMove = 10
        val presenter = givenASimplePresenterWith(AdapterConfig(size, 0, false))

        //When swap items
        presenter.moveItem(positionToMove, positionToMove)

        //Then collection is updated
        assertThat(presenter.getData()[positionToMove]).isEqualTo("$positionToMove")
        assertThat(presenter.getData()).hasSize(size)
    }

    @Test
    fun when_enableLoadMore_then_itemSizeIsIncremented() {
        //Given an adapter with load more disabled
        val position = 10
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 0, false))

        //When enable load more
        presenter.enableLoadMore { }

        //Then adapter is updated
        assertThat(presenter.itemCount).isEqualTo(position + 1)
        assertThat(presenter.getItemViewType(position)).isEqualTo(PresenterAdapter.LOAD_MORE_TYPE)
    }

    @Test
    fun when_disabledLoadMore_then_itemSizeIsCorrect() {
        //Given an adapter with load more enabled
        val position = 10
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 0, true))

        //When disable load more
        presenter.disableLoadMore()

        //Then adapter is updated
        assertThat(presenter.itemCount).isEqualTo(position)
        assertThat(presenter.getItemViewType(position)).isNotEqualTo(PresenterAdapter.LOAD_MORE_TYPE)
    }

    @Test
    fun when_checkItemViewTypeForHeaderPositions_then_headerTypesAreCorrect() {
        //Given an adapter with headers
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 10, true))

        //When check headers position types
        val types = (0 until 10).map { presenter.getItemViewType(it) }.toList()

        //Then header types are correct
        types.forEachIndexed { index, type -> assertThat(type).isEqualTo(PresenterAdapter.HEADER_TYPE + index) }
    }

    @Test
    fun when_checkItemViewTypeForNormalPositions_then_typesAreCorrect() {
        //Given an adapter with headers
        val presenter = givenASimplePresenterWith(AdapterConfig(10, 10, true))

        //When check normal position types
        val types = (10 until 20).map { presenter.getItemViewType(it) }.toList()

        //Then normal position types are correct
        types.forEach { type ->
            assertThat(type).apply {
                isAtLeast(0)
                isNotEqualTo(PresenterAdapter.LOAD_MORE_TYPE)
            }
        }
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



