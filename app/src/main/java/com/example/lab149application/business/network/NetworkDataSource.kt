package com.example.lab149application.business.network

import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.framework.network.model.snap.SnapMatch

interface NetworkDataSource {

    suspend fun getAllSnaps(): List<SnapDao>
    suspend fun postSnap(ImageLabel: String, Image: String): SnapMatch
}
