package com.fonrouge.android.aLib.viewModel

import android.util.Log
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
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KSuspendFunction1

abstract class ViewModelPagingData<T : BaseDoc<*>, FILT : ApiFilter> : ViewModelBase<T>() {
    companion object {
        var lastRequest: Long = 0L
        var requestDelay: Long = 50L
    }

    private var filterSerialized: FILT? = null
    open val pageSize: MutableIntState = mutableIntStateOf(20)
    val refreshingList: MutableState<Boolean> = mutableStateOf(false)
    var requestRefresh by mutableStateOf(false)
    val refreshByFilter = mutableStateOf(false)
    abstract val apiFilter: MutableState<FILT>
    abstract val listStateFunc: KSuspendFunction1<ApiList<FILT>, ListState<T>>
    open val onBeforeListStateGet: (() -> Unit)? = null
    suspend fun listStateGetter(pageNum: Int): ListState<T> {
        onBeforeListStateGet?.invoke()
        val delay = System.currentTimeMillis() - lastRequest
        Log.d("DELAY 1", "millis: $delay")
        if (delay <= requestDelay) {
            Log.d("DELAY 2", "millis: $delay")
//            delay(delay + requestDelay - delay) // this doesn't work
        }
        /* TODO: Check why this is needed to avoid a TIMEOUT on continuous fast requests */
        delay(requestDelay)
        lastRequest = System.currentTimeMillis()
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
            UIBaseEvent.EditingFilter -> {
                if (!refreshByFilter.value) {
                    filterSerialized = apiFilter.value
                    refreshByFilter.value = true
                }
            }

            UIBaseEvent.RefreshByFilter -> {
                refreshByFilter.value = false
                if (filterSerialized?.equals(apiFilter.value) != true) {
                    filterSerialized = apiFilter.value
                    onEvent(UIBaseEvent.UpdateList)
                }
            }
        }
    }

    sealed class UIBaseEvent {
        data object UpdateList : UIBaseEvent()
        data object RefreshByFilter : UIBaseEvent()
        data object EditingFilter : UIBaseEvent()
    }
}

@OptIn(InternalSerializationApi::class)
inline fun <reified FILT : ApiFilter> encodeApiFilter(apiFilter: FILT): String {
    val a = FILT::class.serializer()
    return Json.encodeToString(apiFilter)
}