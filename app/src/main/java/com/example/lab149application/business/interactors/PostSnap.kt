package com.example.lab149application.business.interactors

import com.example.lab149application.business.domain.state.DataState
import com.example.lab149application.business.network.NetworkDataSource
import com.example.lab149application.framework.network.model.snap.SnapMatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class PostSnap
constructor (
    private val networkDataSource: NetworkDataSource
) {
    suspend fun execute(ImageLabel: String, Image: String): Flow<DataState<SnapMatch>> = flow {
        // emit loading
        emit(DataState.Loading)

        // get the snaps
        try {
            val networkSnaps = networkDataSource.postSnap(ImageLabel, Image)

            // emit the retrieved snaps
            emit(DataState.Success(networkSnaps))
        } catch (e: IOException) {
            emit(DataState.Error(e))
        }
    }
}
