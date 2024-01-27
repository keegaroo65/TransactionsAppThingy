package com.example.transactions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {
    val navController = rememberNavController()

    val NAV_HOME = stringResource(NavItem.Home.navRoute)
    val NAV_HISTORY = stringResource(NavItem.History.navRoute)
    val NAV_SETTINGS = stringResource(NavItem.Settings.navRoute)

    // A surface container using the 'background' color from the theme
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.surfaceVariant)
                ),
                title = {
                    Text(
                        text = "Budgeting Tool <3",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        //style = MaterialTheme.typography.titleLarge
                    )
                },
            )
        },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = "home"
        ) {
            composable(
                route = NAV_HOME
            ) {
                Home(navController)
            }
            composable(
                route = NAV_HISTORY
            ) {
                History()
            }
            composable(
                route = NAV_SETTINGS
            ) {
                Settings()
            }

            composable(
                route = NAV_HOME + "/newTransaction"
            ) {
                NewTransaction()
            }
        }
    }
}

@Preview
@Composable
fun MainPreview() {
    MainContainer()
}