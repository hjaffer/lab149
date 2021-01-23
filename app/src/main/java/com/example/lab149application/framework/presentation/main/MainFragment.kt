package com.example.lab149application.framework.presentation.main

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.lab149application.R
import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.business.domain.state.DataState
import com.example.lab149application.framework.network.model.snap.SnapMatch
import com.example.lab149application.framework.presentation.adapter.SnapAdapter
import com.example.lab149application.framework.presentation.main.MainActivity.Companion.requestImageCapture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_snap_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayOutputStream


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_snap_list) {
    private var gameOver = false
    private var curSnapNdx = 0
    private var curSnapDao: SnapDao? = null
    private val viewModel: MainViewModel by activityViewModels()
    private var listOfSnaps: List<SnapDao>? = null
    private lateinit var snapAdapter: SnapAdapter
    private val timerMaxMillisec:Long = 10000//2 * 60 * 1000 // 2 minutes

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snapAdapter = SnapAdapter(
            onClickListener = { snapDao, snapNdx -> handleSnapClick(snapDao, snapNdx) })
        snap_list.adapter = snapAdapter
        subscribeObservers()
        resetUI()
        if (!isPermissionsAllowed()) {
            askForPermissions()
        }
    }

    private fun isPermissionsAllowed(): Boolean {
        context?.let {
            return ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            activity?.let {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.CAMERA
                    )
                ) {
                    showPermissionDeniedDialog()
                } else {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.CAMERA),
                        requestImageCapture
                    )
                }
            }
            return false
        }
        return true
    }

    private fun showPermissionDeniedDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Permission Denied")
                .setMessage("Permission is denied, Please allow permissions from App Settings.")
                .setPositiveButton("App Settings"
                ) { _, _ ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", activity?.packageName ?: "", null)
                    intent.data = uri
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null).show()
        }
    }

    private fun resetUI() {
        viewModel.fetchSnaps()
        gameOver = false
    }

    private fun subscribeObservers() {
        viewModel.getSnapsDataState.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<SnapDao>> -> {
                    displayProgressBar(false)
                    // new list of Snap Objects from server
                    listOfSnaps = dataState.data.distinctBy { it.id }
                    snapAdapter.submitList(listOfSnaps?.map { it.copy(bmp = null) })
                    snapAdapter.notifyDataSetChanged()
                    viewModel.startTimer(timerMaxMillisec)
                }
                is DataState.Error -> {
                    displayProgressBar(false)
                    displayToast(dataState.exception.message)
                }
                is DataState.Loading -> {
                    displayProgressBar(true)
                }
            }
        })

        viewModel.snapResult.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<SnapMatch> -> {
                    displayProgressBar(false)
                    if (dataState.data.getmatched()) {
                        displayToast(getString(R.string.snap_match_success))
                    } else {
                        displayToast(getString(R.string.snap_match_failure))
                    }
                }
                is DataState.Error -> {
                    displayProgressBar(false)
                    displayToast(dataState.exception.message)
                }
                is DataState.Loading -> {
                    displayProgressBar(true)
                }
            }
        })

        viewModel.timerValue.observe(viewLifecycleOwner,
            { timerStr ->
                snap_counter.text = timerStr
            }
        )

        viewModel.timerExpired.observe(viewLifecycleOwner,
            { timerExpired ->
                if (timerExpired) {
                    gameOver = true
                    context?.let { showPlayAgainDialog(it) }
                }
            }
        )
    }

    private fun handleSnapClick(snapDao: SnapDao, snapNdx: Int) {
        if (gameOver) {
            displayToast(getString(R.string.snap_list_timer_expired_no_play_again))
            return
        }

        curSnapNdx = snapNdx
        curSnapDao = snapDao
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, requestImageCapture)
        } catch (e: Exception) {
            displayToast("Error: Could not start camera")
        }
    }

    private fun displayToast(message: String?) {
        context?.let {
            Toast.makeText(it, "$message", Toast.LENGTH_SHORT).show()
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                requestImageCapture -> {
                    if (resultCode == RESULT_OK) {
                        val selectedImage: Bitmap = data?.extras?.get("data") as Bitmap
                        val scaledBmp = Bitmap.createScaledBitmap(
                            selectedImage,
                            selectedImage.width,
                            selectedImage.height,
                            true
                        )
                        snapAdapter.setImage(curSnapNdx, scaledBmp)
                        postASnap(selectedImage)
                    }
                }
            }
        }
    }

    private fun postASnap(selectedImage: Bitmap) {
        val stream = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteArray: ByteArray = stream.toByteArray()
        viewModel.postASnap(
            curSnapDao?.name ?: "",
            byteArray.toString())
    }

    private fun displayProgressBar(isDisplayed: Boolean) {
        spinner.visibility = if (isDisplayed) View.VISIBLE else View.GONE
    }

    // Show an alert dialog with yes, no and cancel button
    private fun showPlayAgainDialog(context: Context) {
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context)

        // Set a title for alert dialog
        builder.setTitle(getString(R.string.snap_list_timer_expired_title))

        // Set a message for alert dialog
        builder.setMessage(R.string.snap_list_timer_expired_message)

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> resetUI()
                DialogInterface.BUTTON_NEGATIVE -> displayToast(getString(R.string.snap_list_timer_expired_no_play_again))
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton(getString(R.string.yes), dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton(R.string.no, dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }
}
