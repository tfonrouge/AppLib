package com.fonrouge.android.aLib.viewModel

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fonrouge.android.aLib.domain.BasePagingSource
import com.fonrouge.fsLib.model.state.ListState
import com.fonrouge.fsLib.model.state.SimpleState
import kotlinx.coroutines.flow.Flow

abstract class PagingDataViewModel<T : Any> : BaseViewModel<T>() {
    open val pageSize: MutableIntState = mutableIntStateOf(20)
    val refreshingList: MutableState<Boolean> = mutableStateOf(false)
    var requestRefresh by mutableStateOf(false)
    abstract suspend fun listStateGetter(pageNum: Int): ListState<T>
    open suspend fun deleteItem(item: T): SimpleState =
        SimpleState(isOk = false, msgError = "Not implemented...")

    val flowPagingData: Flow<PagingData<T>> by lazy {
        Pager(
            config = PagingConfig(
                pageSize = pageSize.intValue,
            ),
            pagingSourceFactory = {
                BasePagingSource(
                    viewModel = this,
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    open fun onEvent(uiBaseEvent: UIBaseEvent) {
        when (uiBaseEvent) {
            UIBaseEvent.UpdateList -> requestRefresh = true
        }
    }

    sealed class UIBaseEvent {
        data object UpdateList : UIBaseEvent()
    }
}