package com.example.accesibilidad.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.accesibilidad.accessibility.TtsController
import com.example.accesibilidad.screens.ForgotScreen
import com.example.accesibilidad.screens.HomeScreen
import com.example.accesibilidad.screens.LoginScreen
import com.example.accesibilidad.screens.PreferencesScreen
import com.example.accesibilidad.screens.RegisterScreen
import com.example.accesibilidad.screens.SearchRecipesScreen
import com.example.accesibilidad.screens.CreateRecipeScreen
import com.example.accesibilidad.ui.theme.AppThemeMode
import com.example.accesibilidad.ui.theme.TextSizePref
import com.example.accesibilidad.screens.EscribirScreen
import com.example.accesibilidad.screens.BuscarDispositivoScreen
import com.example.accesibilidad.screens.HablarScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val HOME = "home"
    const val PREFERENCES = "preferences"
    const val SEARCH = "search_recipes"
    const val CREATE = "create_recipe"
    const val ESCRIBIR = "escribir"
    const val BUSCAR_DISPOSITIVO = "buscar_dispositivo"
    const val HABLAR = "hablar"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    highContrast: Boolean,
    onHighContrastChange: (Boolean) -> Unit,
    textSizePref: TextSizePref,
    onTextSizeChange: (TextSizePref) -> Unit,
    ttsEnabled: Boolean,
    onTtsChange: (Boolean) -> Unit,
    ttsController: TtsController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onGoRegister = { navController.navigate(Routes.REGISTER) },
                onGoForgot   = { navController.navigate(Routes.FORGOT) },
                onLoginOk    = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                // ↓↓↓ nuevos para TTS
                ttsEnabled = ttsEnabled,
                speak = ttsController::speak
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                ttsEnabled = ttsEnabled,
                speak = ttsController::speak
            )
        }

        composable(Routes.FORGOT) {
            ForgotScreen(
                onBack = { navController.popBackStack() },
                ttsEnabled = ttsEnabled,
                speak = ttsController::speak
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoPreferences = { navController.navigate(Routes.PREFERENCES) },
                onGoSearchRecipes = { navController.navigate(Routes.SEARCH) },
                onGoCreateRecipe = { navController.navigate(Routes.CREATE) },
                onGoBuscarDispositivo = { navController.navigate(Routes.BUSCAR_DISPOSITIVO) },
                ttsEnabled = ttsEnabled,
                speak = ttsController::speak
            )
        }

        composable(Routes.PREFERENCES) {
            PreferencesScreen(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                highContrast = highContrast,
                onHighContrastChange = onHighContrastChange,
                textSizePref = textSizePref,
                onTextSizeChange = onTextSizeChange,
                ttsEnabled = ttsEnabled,
                onTtsChange = onTtsChange,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SEARCH) {
            SearchRecipesScreen(
                onBack = { navController.popBackStack() },
                ttsEnabled = ttsEnabled,
                speak = ttsController::speak
            )
        }
        composable(Routes.CREATE) {
            CreateRecipeScreen(
                onBack = { navController.popBackStack() },
                ttsEnabled = ttsEnabled,
                speak = ttsController::speak
            )
        }
        composable(Routes.ESCRIBIR) {
            EscribirScreen() // si necesitas onBack, pásalo y haz popBackStack
        }

        composable(Routes.BUSCAR_DISPOSITIVO) {
            BuscarDispositivoScreen()
        }
    }
}
