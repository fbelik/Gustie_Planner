package com.bignerdranch.android.gustieplanner

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class HomeFragment: Fragment() {

    private lateinit var darkThemeSwitch: Switch

    interface Callbacks {
        fun onSwitchTheme(toDark: Boolean)
    }

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        darkThemeSwitch = view.findViewById(R.id.toggle_dark_theme)

        return view
    }

    override fun onStart() {
        super.onStart()
        if (AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeSwitch.isChecked = true
        }

        darkThemeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                callbacks?.onSwitchTheme(true)
            } else {
                callbacks?.onSwitchTheme(false)
            }
        }

    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
}