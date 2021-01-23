package com.example.lab149application.framework.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.lab149application.R

class MainNavRootFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val navView = inflater.inflate(R.layout.snap_fragment_nav_host, container, false)
        (childFragmentManager.findFragmentById(R.id.membership_nav_host_fragment) as NavHostFragment).navController
            .apply {
                setGraph(R.navigation.snap_nav_graph)
            }

        return navView
    }
}
