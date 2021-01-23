package com.example.lab149application.business.network

import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.framework.network.SnapRetrofitService
import com.example.lab149application.framework.network.mappers.SnapNetworkMapper
import com.example.lab149application.framework.network.model.snap.SnapMatch

class NetworkDataSourceImpl
constructor(
    private val snapRetrofitService: SnapRetrofitService,
    private val snapsNetworkMapper: SnapNetworkMapper
) : NetworkDataSource {

    override suspend fun getAllSnaps(): List<SnapDao> {
        return snapsNetworkMapper.mapFromEntityList(snapRetrofitService.getAllSnaps())
    }

    override suspend fun postSnap(ImageLabel: String, Image: String): SnapMatch {
        return snapRetrofitService.postSnap(ImageLabel, Image)
    }
}
