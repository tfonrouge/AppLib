package com.fonrouge.android.aLib.viewModel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import com.fonrouge.fsLib.config.ICommonViewItem
import com.fonrouge.fsLib.model.CrudTask
import com.fonrouge.fsLib.model.apiData.ApiItem
import com.fonrouge.fsLib.model.apiData.IApiFilter
import com.fonrouge.fsLib.model.base.BaseDoc
import com.fonrouge.fsLib.model.state.ItemState
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.reflect.KSuspendFunction1

@Suppress("unused")
abstract class ViewModelItem<T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> : ViewModelBase()

@Suppress("unused")
fun <CV : ICommonViewItem<T, ID, FILT>, T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> CV.decodeParams(
    navBackStackEntry: NavBackStackEntry
) {
    val crudTask: CrudTask = navBackStackEntry.arguments?.getString("action")?.let {
        Json.decodeFromString(it)
    } ?: throw Exception("No action param defined in route")
    val id: ID? = navBackStackEntry.arguments?.getString("id")?.let {
        if (it != "\"null\"") Json.decodeFromString(
            itemIdSerializer
                ?: throw Exception("No itemIdSerializer defined in '${this::class.simpleName}'"),
            it
        ) else null
    }
    val apiFilter: FILT? = navBackStackEntry.arguments?.getString("apiFilter")?.let {
        if (it != "\"null\"") Json.decodeFromString(
            apiFilterSerializer
                ?: throw Exception("No apiFilterSerializer defined in '${this::class.simpleName}'"),
            it
        ) else null
    }
    itemState = navBackStackEntry.arguments?.getString("itemState")?.let {
        if (it != "\"null\"") Json.decodeFromString(
            ItemState.serializer(
                itemSerializer
                    ?: throw Exception("No itemSerializer defined in '${this::class.simpleName}'"),
            ),
            it
        ) else null
    }

    apiItem = ApiItem(
        id = id,
        item = itemState?.item,
        crudTask = crudTask,
        apiFilter = apiFilter
    )
}

@Suppress("unused")
fun <CV : ICommonViewItem<*, *, *>> CV.routeWithParams(): String {
    return "$name?action={action}&id={id}&apiFilter={apiFilter}&itemState={itemState}"
}

@Suppress("unused")
fun <CV : ICommonViewItem<T, ID, FILT>, T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> CV.goToRouteWithParams(): String {
    return apiItem?.let { apiItem ->
        val serializedApiFilter = apiItem.apiFilter?.let { iApiFilter ->
            Json.encodeToString(
                apiFilterSerializer
                    ?: throw Exception("No apiFilterSerializer defined in '${this::class.simpleName}'"),
                iApiFilter
            ).also { Uri.encode("\"$it\"") }
        }
        val serializedId = apiItem.id?.let { id ->
            Json.encodeToString(
                itemIdSerializer
                    ?: throw Exception("No itemIdSerializer defined in '${this::class.simpleName}'"),
                id
            ).also { Uri.encode("\"$it\"") }
        }
        val serializedItemState = itemState?.let { itemState ->
            Json.encodeToString(
                ItemState.serializer(
                    itemSerializer
                        ?: throw Exception("No itemSerializer defined in '${this::class.simpleName}'")
                ),
                itemState
            ).also { Uri.encode("\"$it\"") }
        }
        "$name?action=\"${apiItem.crudTask}\"&id=\"${serializedId}\"&apiFilter=\"${serializedApiFilter}\"&itemState=\"$serializedItemState\""
    } ?: name
}

@Suppress("unused")
fun <T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> ViewModelItem<*, *, *>.callApi(
    commonView: ICommonViewItem<T, ID, FILT>,
    function: KSuspendFunction1<ApiItem<T, ID, FILT>, ItemState<T>>
) {
    val apiItem = commonView.apiItem ?: return
    viewModelScope.launch {
        commonView.itemState = function(apiItem)
        if (commonView.itemState?.isOk != true) {
            when (apiItem.callType) {
                ApiItem.CallType.Query -> commonView.onQueryFail?.invoke(commonView)
                ApiItem.CallType.Action -> commonView.onActionFail?.invoke(commonView)
            }
        } else {
            when (apiItem.callType) {
                ApiItem.CallType.Query -> commonView.onQuerySuccess?.invoke(commonView)
                ApiItem.CallType.Action -> commonView.onActionSuccess?.invoke(commonView)
            }
        }
    }
}
