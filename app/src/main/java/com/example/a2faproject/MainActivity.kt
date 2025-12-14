package com.example.a2faproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
<<<<<<< Updated upstream
=======
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.data.AppDatabase
import com.data.CloudTokenService
import com.data.TokenRepository
import com.example.a2faproject.ui.screens.AddTokenScreen
import com.example.a2faproject.ui.screens.EditTokenScreen
import com.example.a2faproject.ui.screens.TokenListScreen
import com.example.a2faproject.ui.theme._2FAProjectTheme
import com.example.a2faproject.viewmodel.AuthViewModel
import com.example.a2faproject.viewmodel.TokenViewModel

import androidx.compose.foundation.layout.Box
>>>>>>> Stashed changes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a2faproject.ui.theme._2FAProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
<<<<<<< Updated upstream
            _2FAProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
=======
            var isDarkMode by remember { mutableStateOf(false) }
            
            _2FAProjectTheme(darkTheme = isDarkMode) {
                AuthenticatorApp(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

// Navigation routes
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object TokenList : Screen("token_list")
    object AddToken : Screen("add_token")
    object EditToken : Screen("edit_token/{tokenId}") {
        fun createRoute(tokenId: Int) = "edit_token/$tokenId"
    }
    object Scanner : Screen("scanner")
}

@Composable
fun AuthenticatorApp(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize database and repository
    val database = remember { AppDatabase.getDatabase(context) }
    val authViewModel: AuthViewModel = viewModel()
    val cloud = remember { CloudTokenService() }
    val repository = remember {
        TokenRepository(
            tokenDao = database.tokenDao(),
            cloudTokenService = cloud,
            uidProvider = { authViewModel.currentUid },
            isAnonymousProvider = { authViewModel.isAnonymous }
        )
    }

    // Create ViewModel with the repository
    val viewModel: TokenViewModel = viewModel(
        factory = TokenViewModel.Factory(repository)
    )

    AppNavHost(
        navController = navController,
        tokenViewModel = viewModel,
        authViewModel = authViewModel,
        isDarkMode = isDarkMode,
        onToggleDarkMode = onToggleDarkMode
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    tokenViewModel: TokenViewModel,
    authViewModel: AuthViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Screen.Login.route) {
            com.example.a2faproject.ui.screens.LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { uid ->
                    // Only sync from cloud for non-anonymous users
                    tokenViewModel.syncFromCloud(uid, authViewModel.isAnonymous)
                    navController.navigate(Screen.TokenList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TokenList.route) {
            Box(modifier = Modifier.fillMaxSize()) {

                TokenListScreen(
                    viewModel = tokenViewModel,
                    onNavigateToAddToken = {
                        navController.navigate(Screen.AddToken.route)
                    },
                    onNavigateToScanner = {
                        launchQrScanner(context)
                    },
                    onEditToken = { tokenId ->
                        navController.navigate(Screen.EditToken.createRoute(tokenId))
                    },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode,
                    onLogout = {
                        // Clear local tokens when logging out (fresh start for next user)
                        tokenViewModel.clearLocalTokens()
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )

                FloatingActionButton(
                    onClick = {
                        launchFlutterApp(context)
                    },
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = "Check Password")
                }
            }
        }
        // ==========================================

        composable(Screen.AddToken.route) {
            AddTokenScreen(
                viewModel = tokenViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EditToken.route) { backStackEntry ->
            val tokenId = backStackEntry.arguments?.getString("tokenId")?.toIntOrNull()
            if (tokenId != null) {
                EditTokenScreen(
                    tokenId = tokenId,
                    viewModel = tokenViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Scanner.route) {
            // ScannerScreen placeholder
        }
>>>>>>> Stashed changes
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _2FAProjectTheme {
        Greeting("Android")
    }
}