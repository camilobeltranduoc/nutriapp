package com.example.accesibilidad.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.accesibilidad.screens.ForgotScreen
import com.example.accesibilidad.screens.HomeScreen
import com.example.accesibilidad.screens.LoginScreen
import com.example.accesibilidad.screens.RegisterScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val HOME = "home"
}

@Composable
fun AppNavHost(navController: NavHostController) {
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
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.FORGOT) {
            ForgotScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true } // limpia todo el stack
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
