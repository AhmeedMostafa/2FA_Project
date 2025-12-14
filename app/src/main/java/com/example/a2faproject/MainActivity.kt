package com.example.a2faproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.data.AppDatabase
import com.data.TokenRepository
import com.example.a2faproject.ui.screens.AddTokenScreen
import com.example.a2faproject.ui.screens.TokenListScreen
import com.example.a2faproject.ui.theme._2FAProjectTheme
import com.example.a2faproject.viewmodel.TokenViewModel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _2FAProjectTheme {
                AuthenticatorApp()
            }
        }
    }
}

// Navigation routes
sealed class Screen(val route: String) {
    object TokenList : Screen("token_list")
    object AddToken : Screen("add_token")
    object Scanner : Screen("scanner")
}

@Composable
fun AuthenticatorApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize database and repository
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { TokenRepository(database.tokenDao()) }

    // Create ViewModel with the repository
    val viewModel: TokenViewModel = viewModel(
        factory = TokenViewModel.Factory(repository)
    )

    AppNavHost(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: TokenViewModel
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.TokenList.route,
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
        composable(Screen.TokenList.route) {
            Box(modifier = Modifier.fillMaxSize()) {

                TokenListScreen(
                    viewModel = viewModel,
                    onNavigateToAddToken = {
                        navController.navigate(Screen.AddToken.route)
                    },
                    onNavigateToScanner = {
                        launchQrScanner(context)
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
            TokenListScreen(
                viewModel = viewModel,
                onNavigateToAddToken = {
                    navController.navigate(Screen.AddToken.route)
                },
                onNavigateToScanner = {
                    // Placeholder for Member 3's QR scanner
                    // navController.navigate(Screen.Scanner.route)
                    Toast.makeText(
                        context,
                        "QR Scanner coming soon (Member 3's task)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        composable(Screen.AddToken.route) {
            AddTokenScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Scanner.route) {
            // ScannerScreen placeholder
        }
    }
}

fun launchFlutterApp(context: android.content.Context) {
    val flutterPackageName = "com.example.flutter_password_app"

    val launchIntent = context.packageManager.getLaunchIntentForPackage(flutterPackageName)

    if (launchIntent != null) {
        context.startActivity(launchIntent)
    } else {
        android.widget.Toast.makeText(context, "Error happened", android.widget.Toast.LENGTH_SHORT).show()
    }
}

fun launchQrScanner(context: android.content.Context) {
    val scannerPackageName = "com.example.flutter_qr_scanner"
    val launchIntent = context.packageManager.getLaunchIntentForPackage(scannerPackageName)

    if (launchIntent != null) {
        context.startActivity(launchIntent)
    } else {
        android.widget.Toast.makeText(context, "Error happened", android.widget.Toast.LENGTH_SHORT).show()
        // Placeholder for Member 3's scanner screen
        composable(Screen.Scanner.route) {
            // ScannerScreen will be implemented by Member 3
            // For now, this is a placeholder
        }
    }
}
