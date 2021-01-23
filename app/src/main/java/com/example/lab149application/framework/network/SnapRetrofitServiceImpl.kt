package com.example.lab149application.framework.network

import com.example.lab149application.framework.network.model.snap.SnapMatch
import com.example.lab149application.framework.network.model.snap.SnapObject

class SnapRetrofitServiceImpl
constructor(
    private val snapRetrofit: SnapRetrofit
) : SnapRetrofitService {

    override suspend fun getAllSnaps(): List<SnapObject> {
        return snapRetrofit.getAllSnaps()
    }

    override suspend fun postSnap(ImageLabel: String, Image: String): SnapMatch {
        return snapRetrofit.postSnap(ImageLabel, Image)
    }
}
