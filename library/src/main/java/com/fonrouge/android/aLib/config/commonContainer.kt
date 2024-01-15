@file:Suppress("unused")

package com.fonrouge.android.aLib.config

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.fonrouge.fsLib.config.ICommonContainer
import com.fonrouge.fsLib.model.apiData.ApiItem
import com.fonrouge.fsLib.model.apiData.IApiFilter
import com.fonrouge.fsLib.model.base.BaseDoc
import kotlinx.serialization.json.Json

val ICommonContainer<*, *, *>.routeItem: String get() = "ViewItem$name?apiItem={apiItem}"
val ICommonContainer<*, *, *>.routeList: String get() = "ViewList$name?apiFilter={apiFilter}"

@Suppress("unused")
@Composable
fun <CV : ICommonContainer<T, ID, FILT>, T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> CV.DecodeRouteItemParams(
    navBackStackEntry: NavBackStackEntry,
    function: @Composable (apiItem: ApiItem<T, ID, FILT>) -> Unit
) {
    val apiItem = navBackStackEntry.arguments?.getString("apiItem")?.let {
        if (it != "\"null\"") Json.decodeFromString(
            ApiItem.serializer(itemSerializer, idSerializer, apiFilterSerializer),
            it.removePrefix("\"").removeSuffix("\"")
        ) else null
    }
    apiItem?.let { function(it) }
}

/*
@Composable
fun <CV : ICommonContainer<T, ID, FILT>, T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> CV.DecodeRouteListParams(
    navBackStackEntry: NavBackStackEntry,
    function: @Composable (apiFilter: FILT) -> Unit
) {
    navBackStackEntry.arguments?.getString("apiFilter")?.let {
        if (it != "\"null\"") {
            function(
                Json.decodeFromString(
                    apiFilterSerializer,
                    it.removePrefix("\"").removeSuffix("\"")
                )
            )
        }
    }
}
*/

@Suppress("unused")
fun <CV : ICommonContainer<T, ID, FILT>, T : BaseDoc<ID>, ID : Any, FILT : IApiFilter> CV.goToRouteItemWithParams(
    navHostController: NavHostController?,
    apiItem: ApiItem<T, ID, FILT>
) {
    navHostController ?: return
    val serializedApiItem = Json.encodeToString(
        ApiItem.serializer(itemSerializer, idSerializer, apiFilterSerializer),
        apiItem
    )
    navHostController.navigate(
        "$name?apiItem=\"${Uri.encode(serializedApiItem)}\""
    )
}
