package com.example.accesibilidad.fragments

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.accesibilidad.R
import com.example.accesibilidad.screens.HablarScreen

class HablarFragment : Fragment(R.layout.fragment_hablar) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView: ComposeView = view.findViewById(R.id.composeHablar)
        composeView.setContent {
            HablarScreen()
        }
    }
}
