package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.stickerpocket.R
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.dao.CategoryDAO
import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.usecase.AddEmojiIfNotExistUseCase
import com.android.stickerpocket.domain.usecase.FetchCategoriesUseCase
import com.android.stickerpocket.domain.usecase.InsertCategoriesUseCase
import com.android.stickerpocket.dtos.getCategories
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

    private var addEmojiIfNotExistUseCase =
        AddEmojiIfNotExistUseCase(StickerApplication.instance.emojisRepository)

    private var insertCategoriesUseCase =
        InsertCategoriesUseCase(StickerApplication.instance.categoryRepository)
    sealed class Result {
        data class StickerDownloaded(val gifFile: File) : Result()

    }
    init {
        // Check emojis exist in DB
        // If not exist, add them from JSON
        // Also create default categories
        insertCategories()
        loadAndSaveEmoji(R.raw.emojis)
    }

    private fun insertCategories() {
        CoroutineScope(Dispatchers.Default).launch {
            insertCategoriesUseCase.execute(getCategories())
        }
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

    fun loadAndSaveEmoji(resourceId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonString = loadJSONFromResource(resourceId)
            jsonString?.let {
                try {
                    val gson = Gson()
                    val emojis = gson.fromJson(jsonString, Emojis::class.java)
                    val emojiList = arrayListOf<Emoji>()
                    for (emoji in emojis.emojis){
                        emojiList.add(emoji.toEmoji())
                    }
                    addEmojiIfNotExistUseCase.execute(emojiList)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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