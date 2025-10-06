package com.example.accesibilidad

import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.accesibilidad.ui.theme.TextSizePref

class PrefsMappingTest {

    @Test
    fun textSizePref_returnsCorrectLabel() {
        val label = when (TextSizePref.Medium) {
            TextSizePref.Small -> "Pequeño"
            TextSizePref.Medium -> "Mediano"
            TextSizePref.Large -> "Grande"
        }
        assertEquals("Mediano", label)
    }
}
