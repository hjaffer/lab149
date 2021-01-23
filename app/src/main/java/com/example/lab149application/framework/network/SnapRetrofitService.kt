package com.example.lab149application.framework.network

import com.example.lab149application.framework.network.model.snap.SnapMatch
import com.example.lab149application.framework.network.model.snap.SnapObject

interface SnapRetrofitService {

    suspend fun getAllSnaps(): List<SnapObject>
    suspend fun postSnap(ImageLabel: String, Image: String): SnapMatch
}