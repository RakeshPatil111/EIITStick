package com.android.stickerpocket.presentation.sticker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.usecase.SaveEmojiOnLocalUseCase
import com.android.stickerpocket.network.response.Emojis
import com.android.stickerpocket.presentation.Sticker
import com.android.stickerpocket.utils.toEmoji
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URL

class StickerActivityViewModel: ViewModel() {
    private val _liveData = MutableLiveData<Result>()
    val liveData: MutableLiveData<Result> = _liveData

    private var saveEmojiOnLocalUseCase =
        SaveEmojiOnLocalUseCase(StickerApplication.instance.emojisRepository)

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

    suspend fun loadAndSaveEmoji(resourceId: Int) {
        val jsonString = loadJSONFromResource(resourceId)
        jsonString?.let {
            try {
                val gson = Gson()
                val emojis = gson.fromJson(jsonString, Emojis::class.java)
                val emojiList = arrayListOf<Emoji>()
                for (emoji in emojis.emojis){
                    emojiList.add(emoji.toEmoji())
                }
                saveEmojiOnLocalUseCase.execute(emojiList)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun loadJSONFromResource(resourceId: Int): String? {
        return try {
            val inputStream = StickerApplication.instance.resources.openRawResource(resourceId)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}