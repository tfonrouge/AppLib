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
import com.fonrouge.fsLib.model.apiData.ApiFilter
import com.fonrouge.fsLib.model.apiData.ApiList
import com.fonrouge.fsLib.model.base.BaseDoc
import com.fonrouge.fsLib.model.state.ListState
import com.fonrouge.fsLib.model.state.SimpleState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KSuspendFunction1

abstract class PagingDataViewModel<T : BaseDoc<*>, FILT : ApiFilter> : BaseViewModel<T>() {
    open val pageSize: MutableIntState = mutableIntStateOf(20)
    val refreshingList: MutableState<Boolean> = mutableStateOf(false)
    var requestRefresh by mutableStateOf(false)
    abstract val apiFilter: MutableState<FILT>
    abstract val listStateFunc: KSuspendFunction1<ApiList<FILT>, ListState<T>>
    open val onBeforeListStateGet: (() -> Unit)? = null
    suspend fun listStateGetter(pageNum: Int): ListState<T> {
        onBeforeListStateGet?.invoke()
        delay(50)
        return listStateFunc(
            ApiList(
                tabPage = pageNum,
                tabSize = pageSize.intValue,
                apiFilter = apiFilter.value
            )
        )
    }

    open suspend fun deleteItem(item: T): SimpleState =
        SimpleState(isOk = false, msgError = "Not implemented...")

    val flowPagingData: Flow<PagingData<T>> by lazy {
        Pager<Int, T>(
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