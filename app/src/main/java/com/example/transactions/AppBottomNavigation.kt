package com.example.transactions

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppBottomNavigation(
    navController: NavController
) {
    val navItems = listOf(NavItem.Home, NavItem.History, NavItem.Settings)

    val containerColor = MaterialTheme.colorScheme.surface

    NavigationBar(
        containerColor = containerColor,
        contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navItems.forEach{ item ->
            val navRoute: String = stringResource(item.navRoute)
            val title: String = stringResource(id = item.title)

            val selected = currentRoute == navRoute

            val icon: ImageVector
            if (selected) {
                icon = item.selectedIcon
            }
            else {
                icon = item.unselectedIcon
            }

            NavigationBarItem(
                icon = { Icon(icon, "") },
                selected = selected,
                label = { Text(text = title) },
                onClick = {
                    navController.navigate(navRoute)
                }
            )
        }
    }
}

@Preview
@Composable
fun AppBottomNavPreview() {
    AppBottomNavigation(rememberNavController())
}