package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository

class FetchAllDownloadedUseCase(private val repository: StickerRepository) {

    suspend fun execute(): List<Sticker> = repository.getDownloaded()
}