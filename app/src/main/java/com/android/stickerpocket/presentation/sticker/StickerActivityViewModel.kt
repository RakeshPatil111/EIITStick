package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.presentation.Sticker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class StickerActivityViewModel: ViewModel() {
    private val _liveData = MutableLiveData<Result>()
    val liveData: MutableLiveData<Result> = _liveData
    sealed class Result {
        data class StickerDownloaded(val gifFile: File) : Result()

    }
    fun downloadSticker(sticker: Sticker) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(sticker.thumbnail)
                val connection = url.openConnection()
                connection.connect()

                // Create a temporary file in the cache directory
                val gifFile = File(StickerApplication.instance.cacheDir, "temp.gif")

                val inputStream = connection.getInputStream()
                val outputStream = FileOutputStream(gifFile)

                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()
                _liveData.postValue(Result.StickerDownloaded(gifFile))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}