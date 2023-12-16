package com.fonrouge.applib.ui.theme

sealed class Routes {
    data object Home : Routes()
    data object NavDrawerAsSheetScreen : Routes()
    data object BarcodeReeaderScreen1 : Routes()

    val destination: String get() = "${this::class.simpleName}"
}
