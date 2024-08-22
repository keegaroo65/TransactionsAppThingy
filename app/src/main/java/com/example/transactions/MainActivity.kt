package com.example.transactions

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.transactions.Utility.Companion.LoadingPopup
import com.example.transactions.Utility.Companion.LoadingPopupCard
import com.example.transactions.data.AppDataContainer
import com.example.transactions.data.HistoryRepository
import com.example.transactions.data.RecurringRepository
import com.example.transactions.data.Transaction
import com.example.transactions.ui.history.HistoryDetail
import com.example.transactions.ui.history.HistoryScreen
import com.example.transactions.ui.history.HistoryViewModel
import com.example.transactions.ui.history.TopBarHistoryDropdown
import com.example.transactions.ui.home.HomeScreen
import com.example.transactions.ui.home.HomeViewModel
import com.example.transactions.ui.navigation.AppBottomNavigation
import com.example.transactions.ui.navigation.NavItem
import com.example.transactions.ui.newRecurring.NewRecurring
import com.example.transactions.ui.newRecurring.NewRecurringViewModel
import com.example.transactions.ui.newTransaction.NewTransaction
import com.example.transactions.ui.newTransaction.NewTransactionViewModel
import com.example.transactions.ui.recurring.RecurringDetail
import com.example.transactions.ui.recurring.RecurringScreen
import com.example.transactions.ui.recurring.RecurringViewModel
import com.example.transactions.ui.settings.SettingsScreen
import com.example.transactions.ui.theme.TransactionsTheme
import com.example.transactions.workers.SubscriptionsViewModel
import kotlinx.coroutines.launch

private val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    lateinit var container: AppDataContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context: Context = applicationContext

        container = AppDataContainer(context)

        val mainViewModel: MainViewModel by viewModels { MainViewModel.Factory }

        val recurringViewModel = RecurringViewModel(
            container.recurringRepository
        )

//        recurringViewModel.updateAllNextCharges()

        setContent {
            TransactionsTheme {
                MainContainer(
                    mainViewModel,
                    HomeViewModel(
                        container.dataStore
                    ),
                    HistoryViewModel(
                        container.historyRepository
                    ),
                    recurringViewModel,
                    container.historyRepository,
                    container.recurringRepository,
                    SubscriptionsViewModel(
                        application
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    historyViewModel: HistoryViewModel,
    recurringViewModel: RecurringViewModel,
    historyRepository: HistoryRepository,
    recurringRepository: RecurringRepository,
    subscriptionsViewModel: SubscriptionsViewModel
) {
    val uiState = mainViewModel.uiState.collectAsState().value

    val navController = rememberNavController()

    // TODO move string resources
    val NAV_HOME = stringResource(NavItem.Home.navRoute)
    val NAV_HISTORY = stringResource(NavItem.History.navRoute)
    val NAV_RECURRING = stringResource(NavItem.Recurring.navRoute)
    val NAV_SETTINGS = stringResource(NavItem.Settings.navRoute)
    val NAV_NEW_TRANSACTION = "newTransaction"
    val NAV_NEW_RECURRING = "newRecurring"

    val coroutineScope = rememberCoroutineScope()

    var dropdownShown by rememberSaveable { mutableStateOf(false) }

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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "Budgeting Tool <3",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        if (uiState.deleteButtonShown) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    Icons.Outlined.MoreVert,
                                    "More Options",
                                    modifier = Modifier
                                        .clickable {
                                            dropdownShown = !dropdownShown
                                        }
                                )

                                if (dropdownShown) {
                                    Log.d(TAG, "showing history dropdown")
                                    TopBarHistoryDropdown(
                                        historyViewModel,
                                        {
                                            historyViewModel.deselectAll()

                                            var netGain = 0

                                            for (transaction in it) {
                                                when (transaction.type) {
                                                    0 -> netGain += transaction.amount
                                                    1 -> netGain -= transaction.amount
                                                    // TODO: savings
                                                }
                                            }

                                            coroutineScope.launch {
                                                mainViewModel.dataStore.removeBalance(netGain)
                                                historyRepository.deleteTransactions(it)
                                                // TODO: move deleted transactions into a "trash bin" for 7 days instead of instant deletion?
                                            }
                                        },
                                        {
                                            dropdownShown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
            )
        },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { innerPadding ->

//        val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition? = {
//            scaleIn(
//                animationSpec = tween(
//                    100,
//                    easing = LinearEasing,
//                )
//            )
//        }
//        val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition? = {
//            scaleOut(
//                animationSpec = tween(
//                    100,
//                    easing = LinearEasing
//                )
//            )
//        }
        val popupEnter: (AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition? = {
            fadeIn(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + expandIn(
                animationSpec = tween(300, easing = EaseIn),
                expandFrom = Alignment.TopCenter// = AnimatedContentTransitionScope.SlideDirection.Start
            )
        }

        val popupExit: (AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition? = {
            fadeOut(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + it.slideOutOfContainer(
                animationSpec = tween(300, easing = EaseOut),
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        }

        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = NAV_HOME,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            // Home page
            composable(
                route = NAV_HOME
            ) {
                mainViewModel.clearEditTransaction()
                HomeScreen(
                    homeViewModel,
                    subscriptionsViewModel
                ) {
                    navController.navigate("$NAV_HOME/$NAV_NEW_TRANSACTION")
                }
            }
            // History page
            composable(
                route = NAV_HISTORY
            ) {
                mainViewModel.clearEditTransaction()
                HistoryScreen(
                    historyViewModel,

                    // newTransaction
                    {
                        navController.navigate("$NAV_HISTORY/$NAV_NEW_TRANSACTION")
                    },

                    // editTransaction
                    { transaction: Transaction ->
                        Log.d(TAG, "navigating to ${transaction.id}")
                        navController.navigate("$NAV_HISTORY/${transaction.id}")
                    },

                    // showDeleteButton
                    {
                        mainViewModel.showDeleteButton()
                    },

                    // hideDeleteButton
                    {
                        mainViewModel.hideDeleteButton()
                    }
                )
            }
            // Recurring page
            composable(
                route = NAV_RECURRING
            ) {
                mainViewModel.clearEditTransaction()
                RecurringScreen(
                    recurringViewModel,
                    {
                        navController.navigate(NAV_NEW_RECURRING)
                    },
                    {
                        navController.navigate("recurring/${it.id}")
                    },
                    {
                        mainViewModel.deleteAllTransactions()
                    }
                )
            }
            // Settings page
            composable(
                route = NAV_SETTINGS
            ) {
                mainViewModel.clearEditTransaction()
                SettingsScreen()
            }


            // New transaction from Home page
            composable(
                route = "$NAV_HOME/$NAV_NEW_TRANSACTION",
                enterTransition = popupEnter,
                exitTransition = popupExit
            ) {
                mainViewModel.clearEditTransaction()

                NewTransaction(
                    viewModel(factory = NewTransactionViewModel.Companion.NewTransactionViewModelFactory(null))
                ) {
                    navController.navigate(NAV_HOME)
                }

            }
            // New transaction from History page
            composable(
                route = "$NAV_HISTORY/$NAV_NEW_TRANSACTION",
                enterTransition = popupEnter,
                exitTransition = popupExit
            ) {
                mainViewModel.clearEditTransaction()
                NewTransaction(
                    viewModel(factory = NewTransactionViewModel.Companion.NewTransactionViewModelFactory(null))
                ) {
                    navController.navigate(NAV_HISTORY)
                }
            }
            // Edit transaction
            composable(
                route = "$NAV_HISTORY/{transactionId}",
                enterTransition = popupEnter,
                exitTransition = popupExit
            ) {
                val transactionId = it.arguments?.getString("transactionId")?.toInt() ?: -1

                mainViewModel.getTransactionAsync(transactionId)

                val transaction = uiState.editTransaction
                if (transaction == null) {
                    LoadingPopupCard()
                }
                else {
                    HistoryDetail(
                        viewModel(factory = NewTransactionViewModel.Companion.NewTransactionViewModelFactory(transaction))
                    ) {
                        navController.navigate(NAV_HISTORY)
                    }
                }
            }

            // New Recurring from Recurring page
            composable(
                route = NAV_NEW_RECURRING,
                enterTransition = popupEnter,
                exitTransition = popupExit
            ) {
                NewRecurring(
                    viewModel(factory = NewRecurringViewModel.Companion.NewRecurringViewModelFactory(null))
                ) {
                    navController.navigate(NAV_RECURRING)
                }
            }
            // View Recurring from Recurring page
            composable(
                route = "recurring/{recurringId}",
                enterTransition = popupEnter,
                exitTransition = popupExit
            ) {
                val recurringId = it.arguments?.getString("recurringId")?.toInt() ?: -1

                mainViewModel.getRecurringAsync(recurringId)

                val recurring = uiState.viewRecurring
                if (recurring == null) {
                    LoadingPopup()
                }
                else {
                    RecurringDetail(
                        recurring,
//                        viewModel(factory = NewTransactionViewModel.Companion.NewTransactionViewModelFactory(transaction)),
                        { // deleteRecurring
                            coroutineScope.launch {
                                recurringRepository.deleteRecurring(recurring)
                            }
                            navController.navigate(NAV_RECURRING)
                        },
                        { // editRecurring

                        }
                    )
                }
            }
        }
    }
}