package com.example.lab149application.business.interactors

import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.business.domain.state.DataState
import com.example.lab149application.business.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetSnap
constructor (
    private val networkDataSource: NetworkDataSource
) {
    suspend fun execute(): Flow<DataState<List<SnapDao>>> = flow {
        // emit loading
        emit(DataState.Loading)

        // get the snaps
        try {
            val networkSnaps = networkDataSource.getAllSnaps()

            // emit the retrieved snaps
            emit(DataState.Success(networkSnaps))
        } catch (e: IOException) {
            emit(DataState.Error(e))
        }
    }
}
