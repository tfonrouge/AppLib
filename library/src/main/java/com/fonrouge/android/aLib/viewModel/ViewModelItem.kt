package com.fonrouge.android.aLib.viewModel

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.fonrouge.fsLib.config.ICommonViewItem
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
@Composable
fun <CV : ICommonViewItem<T, ID, FILT>, T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> CV.DecodeParams(
    navBackStackEntry: NavBackStackEntry,
    function: @Composable (ApiItem<T, ID, FILT>) -> Unit
) {
    val apiItem = navBackStackEntry.arguments?.getString("apiItem")?.let {
        if (it != "\"null\"") Json.decodeFromString(
            ApiItem.serializer(itemSerializer, idSerializer, apiFilterSerializer),
            it.removePrefix("\"").removeSuffix("\"")
        ) else null
    }
    apiItem?.let { function(it) }
}

@Suppress("unused")
fun <CV : ICommonViewItem<*, *, *>> CV.routeWithParams(): String {
    return "$name?apiItem={apiItem}"
}

@Suppress("unused")
fun <CV : ICommonViewItem<T, ID, FILT>, T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> CV.goToRouteWithParams(
    navHostController: NavHostController?,
    apiItem: ApiItem<T, ID, FILT>
) {
    navHostController ?: return
//    val serializedApiItem = Json.encodeToString(apiItem)
    val serializedApiItem = Json.encodeToString(
        ApiItem.serializer(itemSerializer, idSerializer, apiFilterSerializer),
        apiItem
    )
    navHostController.navigate(
        "$name?apiItem=\"${Uri.encode(serializedApiItem)}\""
    )
}

@Suppress("unused")
fun <T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> ViewModelItem<*, *, *>.callApi(
    commonView: ICommonViewItem<T, ID, FILT>,
    function: KSuspendFunction1<ApiItem<T, ID, FILT>, ItemState<T>>,
    onSuccess: (ICommonViewItem<T, ID, FILT>.(ItemState<T>) -> Unit)? = null,
    onFailure: (ICommonViewItem<T, ID, FILT>.(ItemState<T>) -> Unit)? = null,
    apiItemBuilder: () -> ApiItem<T, ID, FILT>?
) {
    apiItemBuilder()?.let { apiItem ->
        viewModelScope.launch {
            val itemState = function(apiItem)
            if (itemState.isOk) {
                onSuccess?.invoke(commonView, itemState)
            } else {
                onFailure?.invoke(commonView, itemState)
            }
        }
    }
}
