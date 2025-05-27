package com.elearn.presentation.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.elearn.presentation.ui.theme.MutedForegroundColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest

private val inMemoryCache = mutableMapOf<String, Bitmap>()

@Composable
fun CacheImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    description: String?,
    contentScale: ContentScale = ContentScale.Crop,
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var context = LocalContext.current

    LaunchedEffect(imageUrl) {
        isLoading = true
        val cachedBitmap = inMemoryCache[imageUrl]

        if (bitmap != null) {
            bitmap = cachedBitmap
            isLoading = false
            return@LaunchedEffect
        }

        val cacheFile = getCacheFile(context, imageUrl)

        if (cacheFile.exists()) {
            bitmap = try {
                BitmapFactory.decodeFile(cacheFile.absolutePath)?.also {
                    inMemoryCache[imageUrl] = it
                }
            } catch (error: Exception) {
                null
            }

            if (bitmap != null) {
                isLoading = false

                return@LaunchedEffect
            }
        }

        bitmap = loadImageFromUrl(imageUrl)?.also { downloadBitmap ->
            inMemoryCache[imageUrl] = downloadBitmap

            saveToDiskCache(context, imageUrl, downloadBitmap)
        }

        isLoading = false
    }

    when {
        isLoading -> Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        bitmap != null -> Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = description,
            contentScale = contentScale,
            modifier = modifier
        )

        else -> Box(
            modifier = modifier
                .background(Color.Gray)
        )
    }
}

suspend fun loadImageFromUrl(url: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.connect()

            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            bitmap
        } catch (error: Exception) {
            error.printStackTrace()

            null
        }
    }
}

private fun getCacheFileName(url: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(url.toByteArray())

    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

private fun getCacheFile(context: Context, url: String): File {
    val cacheDir = File(context.cacheDir, "image_cache")

    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }

    return File(cacheDir, getCacheFileName(url))
}

private fun saveToDiskCache(context: Context, url: String, bitmap: Bitmap) {
    try {
        val cacheFile = getCacheFile(context, url)

        FileOutputStream(cacheFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

            out.flush()
        }
    } catch (error: Exception) {
        error.printStackTrace()
    }
}