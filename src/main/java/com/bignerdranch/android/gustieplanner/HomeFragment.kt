package com.bignerdranch.android.gustieplanner

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class HomeFragment: Fragment() {

    private lateinit var doNotShowMsgBtn: Switch
    private lateinit var darkThemeSwitch: Switch

    interface Callbacks {
        fun onDoNotShowMsg(dontShow: Boolean)
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

        doNotShowMsgBtn = view.findViewById(R.id.do_not_show_msg)
        darkThemeSwitch = view.findViewById(R.id.toggle_dark_theme)

        return view
    }

    override fun onStart() {
        super.onStart()
        if (AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeSwitch.isChecked = true
        }
        val sharedPrefs = context?.getSharedPreferences(Keys.sharedPreferencesKey, Context.MODE_PRIVATE)
        if (sharedPrefs?.getBoolean("show-message", true) == false) {
            doNotShowMsgBtn.isChecked = true
        }

        doNotShowMsgBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            callbacks?.onDoNotShowMsg(isChecked)
        }

        darkThemeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            callbacks?.onSwitchTheme(isChecked)
        }

    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
}