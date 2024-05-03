package com.example.transactions

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    @StringRes val title: Int,
    @StringRes val navRoute: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home: NavItem(
        R.string.homeTitle,
        R.string.homeRoute,
        Icons.Outlined.Home,
        Icons.Filled.Home
    )

    data object History: NavItem(
        R.string.historyTitle,
        R.string.historyRoute,
        Icons.Outlined.Analytics,
        Icons.Filled.Analytics
    )

    data object Settings: NavItem(
        R.string.settingsTitle,
        R.string.settingsRoute,
        Icons.Outlined.Settings,
        Icons.Filled.Settings
    )
}