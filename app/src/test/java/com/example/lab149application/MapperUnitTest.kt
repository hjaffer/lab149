package com.example.lab149application

import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.framework.network.mappers.SnapNetworkMapper
import com.example.lab149application.framework.network.model.snap.SnapObject
import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Mapper unit tests
 *
 */

private const val SNAP_ID = 1234
private const val SNAP_NAME = "snap name"

class MapperUnitTest {
    private val allSnapsMapper = SnapNetworkMapper()

    @Test
    fun test_all_snaps_mapFromEntity() {
        val snap = SnapObject()
            .apply {
            id = SNAP_ID
            name = SNAP_NAME
        }

        val snapDao = allSnapsMapper.mapFromEntity(snap)

        assertEquals(SNAP_ID, snapDao.id)
        assertEquals(SNAP_NAME, snapDao.name)
    }

    @Test
    fun test_all_snaps_mapToEntity() {
        val snapDao = SnapDao(
            id = SNAP_ID,
            name = SNAP_NAME)

        val snapObj = allSnapsMapper.mapToEntity(snapDao)

        assertEquals(SNAP_ID, snapObj.id)
        assertEquals(SNAP_NAME, snapObj.name)
    }
}