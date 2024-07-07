package com.android.stickerpocket.utils

import android.content.Context
import com.android.stickerpocket.BuildConfig
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.drawables.ImageFormat
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.views.GiphyGridView

object GiphyConfigure {
    const val SPAN_COUNT = 3
    const val CELL_PADDING = 24
    const val DIRECTION = GiphyGridView.VERTICAL
    fun configGiphy(context: Context) {
        Giphy.configure(
            context,
            "UE20qxVTbBaoaVzWtCnKV5ZBuhEfuWkm",
            verificationMode = false,
//            frescoHandler = object : GiphyFrescoHandler {
//                override fun handle(imagePipelineConfigBuilder: ImagePipelineConfig.Builder) {
//                    val previewsDiskConfig = DiskCacheConfig.newBuilder(context)
//                        .setMaxCacheSize(250L * ByteConstants.MB).build()
//                    val qualityDiskConfig = DiskCacheConfig.newBuilder(context)
//                        .setMaxCacheSize(250L * ByteConstants.MB).build()
//                    imagePipelineConfigBuilder
//                        .setSmallImageDiskCacheConfig(previewsDiskConfig)
//                        .setMainDiskCacheConfig(qualityDiskConfig)
//                        .setCacheKeyFactory(DefaultCacheKeyFactory.getInstance())
//                        .build()
//                }
//                override fun handle(okHttpClientBuilder: OkHttpClient.Builder) {
//                }
//            })
        )
        val settings = GPHSettings(GPHTheme.Dark)
        settings.apply {
            imageFormat = ImageFormat.WEBP
            showSuggestionsBar = false
            renditionType = RenditionType.fixedWidth
            confirmationRenditionType = RenditionType.original
            selectedContentType = GPHContentType.gif
            showConfirmationScreen = false
        }
    }

    fun configGiphyGridView(view: GiphyGridView) {
        view.apply {
            showViewOnGiphy = false
            spanCount = SPAN_COUNT
            direction = DIRECTION
            showCheckeredBackground = true
            fixedSizeCells = true
            cellPadding = CELL_PADDING
        }
    }
}