package com.example.accesibilidad.accessibility

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

class TtsController(private val appContext: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(appContext, this)
    @Volatile var ready: Boolean = false
        private set
    private var pendingMessage: String? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val okCl = tts?.setLanguage(Locale("es","CL"))
            ready = okCl == TextToSpeech.LANG_AVAILABLE || okCl == TextToSpeech.LANG_COUNTRY_AVAILABLE
            if (!ready) {
                val okEs = tts?.setLanguage(Locale("es","ES"))
                ready = okEs == TextToSpeech.LANG_AVAILABLE || okEs == TextToSpeech.LANG_COUNTRY_AVAILABLE
            }
            tts?.setSpeechRate(1.0f)
            tts?.setPitch(1.0f)

            pendingMessage?.let {
                tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, "nutriapp-init")
                pendingMessage = null
            }
        } else {
            ready = false
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) return
        val engine = tts
        if (ready && engine != null) {
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nutriapp-${System.currentTimeMillis()}")
        } else {
            pendingMessage = text
        }
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        ready = false
        pendingMessage = null
    }
}

/** Hook Compose para crear y limpiar el TTS automáticamente. */
@Composable
fun rememberTtsController(): TtsController {
    val ctx = LocalContext.current.applicationContext
    val controller = remember { TtsController(ctx) }
    DisposableEffect(Unit) {
        onDispose { controller.shutdown() }
    }
    return controller
}
