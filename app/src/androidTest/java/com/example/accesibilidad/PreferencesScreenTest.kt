package com.example.accesibilidad

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.accesibilidad.screens.PreferencesScreen
import com.example.accesibilidad.ui.theme.AppThemeMode
import com.example.accesibilidad.ui.theme.TextSizePref
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun preferences_showsAccount_andCanScrollToSave() {
        composeRule.setContent {
            PreferencesScreen(
                themeMode = AppThemeMode.System,
                onThemeModeChange = {},
                highContrast = false,
                onHighContrastChange = {},
                textSizePref = TextSizePref.Small,
                onTextSizeChange = {},
                ttsEnabled = false,
                onTtsChange = {},
                onBack = {}
            )
        }

        composeRule.onNodeWithText("Cuenta").assertExists()
        composeRule.onNodeWithText("Guardar cambios")
            .performScrollTo()
            .assertExists()
    }
}
