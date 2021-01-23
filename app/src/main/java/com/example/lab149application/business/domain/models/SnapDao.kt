package com.example.lab149application.business.domain.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SnapDao(

    var id: Int,
    var name: String,
    var bmp: Bitmap?
) : Parcelable
