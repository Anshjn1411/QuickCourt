package com.project.odoo_235.utils

import android.content.Context
import android.widget.Toast

object AppUtils {

    fun showToast(context: Context, message: String, longDuration: Boolean = false) {
        Toast.makeText(
            context,
            message,
            if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()
    }
}