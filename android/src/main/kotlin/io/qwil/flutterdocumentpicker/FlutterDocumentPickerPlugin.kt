package io.qwil.flutterdocumentpicker

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.webkit.MimeTypeMap
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.filter.CompositeFilter
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import com.nbsp.materialfilepicker.utils.FileTypeUtils
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.io.FileFilter


class FlutterDocumentPickerPlugin(private val registrar: Registrar) : MethodCallHandler,
        PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {
    private var result: Result? = null

    private var call: MethodCall? = null
    override fun onMethodCall(call: MethodCall, result: Result) {
        when {
            call.method == "show" -> {
                val packageManager = registrar.context().packageManager
                val hasPermission = packageManager.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, registrar.context().packageName)

                this.result = result
                this.call = call
                if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                    startIntent()
                } else {
                    ActivityCompat.requestPermissions(registrar.activity(),
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE)
                }
            }
            else -> result.notImplemented()
        }
    }

    private fun startIntent() {
        val category = call?.argument<String>("fileType")

        val intent = MaterialFilePicker().withActivity(registrar.activity()).intent

        if (category != "*/*") {
            val extensions: Array<String> = when (category) {
                "text/plain" -> arrayOf("txt")
                "application/pdf" -> FileTypeUtils.FileType.PDF.extensions
                "image/*" -> FileTypeUtils.FileType.IMAGE.extensions
                "audio/*" -> FileTypeUtils.FileType.MUSIC.extensions
                "video/*" -> FileTypeUtils.FileType.VIDEO.extensions
                else -> FileTypeUtils.FileType.ARCHIVE.extensions
            }

            val filters = extensions.map { ext -> FileFilter { it.extension == ext } } as ArrayList
            intent.putExtra(FilePickerActivity.ARG_FILTER, CompositeFilter(filters))
        }

        startActivityForResult(registrar.activity(), intent, READ_REQUEST_CODE, null)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        if (requestCode != PERMISSION_REQUEST_CODE) return false
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startIntent()
        } else {
            result?.error("Storage permission denied.", "We don't have permission to access the External file storage", null)
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode != READ_REQUEST_CODE) return false
        if (resultCode != Activity.RESULT_OK) {
            result?.success(null)
            result = null
            call = null
            return true
        }
        if (data == null) {
            result?.success(null)
            result = null
            call = null
            return true
        }

        val filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
        val map = metaDataFromFile(File(filePath))
        map["path"] = filePath
        result?.success(map)

        result = null
        call = null
        return true
    }


    private fun metaDataFromFile(file: File): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        if (!file.exists()) return map

        map[FILE_SIZE] = file.length()
        map[FILE_NAME] = file.name
        map[TYPE] = mimeTypeFromName(file.absolutePath) ?: ""

        return map
    }

    companion object {
        private const val READ_REQUEST_CODE = 41
        private const val PERMISSION_REQUEST_CODE = 45

        private const val FILE_SIZE = "fileSize"
        private const val FILE_NAME = "fileName"
        private const val TYPE = "type"

        private const val TAG = "FlutterDocumentPicker"

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_document_picker")
            val documentPickerPlugin = FlutterDocumentPickerPlugin(registrar)
            registrar.addActivityResultListener(documentPickerPlugin)
            registrar.addRequestPermissionsResultListener(documentPickerPlugin)
            channel.setMethodCallHandler(documentPickerPlugin)
        }

        private fun mimeTypeFromName(absolutePath: String): String? {
            val extension = MimeTypeMap.getFileExtensionFromUrl(absolutePath)
            return if (extension != null) MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) else null
        }
    }
}
