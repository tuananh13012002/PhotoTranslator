package com.image.photo.translator.common

import android.app.Activity
import com.image.photo.translator.CustomApplication

val Activity.customApplication: CustomApplication
get() = application as CustomApplication