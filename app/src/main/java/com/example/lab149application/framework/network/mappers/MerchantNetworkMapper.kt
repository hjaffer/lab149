package com.example.lab149application.framework.network.mappers

import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.business.domain.util.EntityMapper
import com.example.lab149application.framework.network.model.snap.SnapObject
import javax.inject.Inject

class SnapNetworkMapper
@Inject
constructor() : EntityMapper<SnapObject, SnapDao> {

    override fun mapFromEntity(entity: SnapObject): SnapDao {
        return SnapDao(
            id = entity.id,
            name = entity.name,
            bmp = null
        )
    }
    override fun mapToEntity(domainModel: SnapDao): SnapObject {
        return SnapObject()
            .apply {
            id = domainModel.id
            name = domainModel.name
        }
    }

    fun mapFromEntityList(entities: List<SnapObject>): List<SnapDao> {
        val snapList = ArrayList<SnapDao>()
        for (curSnap in entities) {
            snapList.add(mapFromEntity(curSnap))
        }
        return snapList
    }
}
