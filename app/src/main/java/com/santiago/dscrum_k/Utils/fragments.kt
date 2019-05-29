package com.santiago.dscrum_k.Utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment, frameId: Int) {

    val transaction = manager.beginTransaction()
    transaction.replace(frameId, fragment)
    transaction.addToBackStack(null)
    transaction.commit()

}

