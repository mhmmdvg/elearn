package com.elearn.presentation.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest

private class LRUCache<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(0, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > maxSize
    }
}

private val inMemoryCache = LRUCache<String, Bitmap>(50) // Limit to 50 images

@Composable
fun CacheImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    description: String?,
    contentScale: ContentScale = ContentScale.Crop,
    maxWidth: Dp? = null,
    maxHeight: Dp? = null,
    onLoadingComplete: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val density = LocalDensity.current

    LaunchedEffect(imageUrl) {
        if (imageUrl.isBlank()) {
            isLoading = false
            hasError = true
            onError?.invoke()
            return@LaunchedEffect
        }

        isLoading = true
        hasError = false

        try {
            // Check in-memory cache first
            val cachedBitmap = inMemoryCache[imageUrl]
            if (cachedBitmap != null && !cachedBitmap.isRecycled) {
                bitmap = cachedBitmap
                isLoading = false
                onLoadingComplete?.invoke()
                return@LaunchedEffect
            }

            // Calculate target dimensions for scaling
            val targetWidth = maxWidth?.let { with(density) { it.toPx().toInt() } }
            val targetHeight = maxHeight?.let { with(density) { it.toPx().toInt() } }

            // Load from cache or network on IO thread
            val loadedBitmap = withContext(Dispatchers.IO) {
                loadImageWithCache(context, imageUrl, targetWidth, targetHeight)
            }

            if (loadedBitmap != null) {
                // Cache in memory
                inMemoryCache[imageUrl] = loadedBitmap
                bitmap = loadedBitmap
                onLoadingComplete?.invoke()
            } else {
                hasError = true
                onError?.invoke()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hasError = true
            onError?.invoke()
        } finally {
            isLoading = false
        }
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

        hasError -> Box(
            modifier = modifier
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            // You can add an error icon here
        }
    }
}

private suspend fun loadImageWithCache(
    context: Context,
    url: String,
    targetWidth: Int? = null,
    targetHeight: Int? = null
): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val cacheFile = getCacheFile(context, url)

            // Try loading from disk cache first
            if (cacheFile.exists()) {
                val diskBitmap =
                    decodeBitmapFromFile(cacheFile.absolutePath, targetWidth, targetHeight)
                if (diskBitmap != null) {
                    return@withContext diskBitmap
                }
            }

            // Load from network
            val networkBitmap = loadImageFromUrl(url, targetWidth, targetHeight)
            if (networkBitmap != null) {
                // Save to disk cache
                saveToDiskCache(cacheFile, networkBitmap)
            }

            networkBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

private fun decodeBitmapFromFile(
    filePath: String,
    targetWidth: Int? = null,
    targetHeight: Int? = null
): Bitmap? {
    return try {
        if (targetWidth != null && targetHeight != null) {
            // First decode bounds only
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(filePath, options)

            // Calculate sample size
            options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
            options.inJustDecodeBounds = false

            BitmapFactory.decodeFile(filePath, options)
        } else {
            BitmapFactory.decodeFile(filePath)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private suspend fun loadImageFromUrl(
    url: String,
    targetWidth: Int? = null,
    targetHeight: Int? = null
): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            if (targetWidth != null && targetHeight != null) {
                // Download the image data first
                val connection = URL(url).openConnection().apply {
                    connectTimeout = 10000
                    readTimeout = 10000
                    setRequestProperty("User-Agent", "Android App")
                }

                val imageData = connection.getInputStream().use { it.readBytes() }

                // First decode bounds only
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeByteArray(imageData, 0, imageData.size, options)

                // Calculate sample size and decode
                options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
                options.inJustDecodeBounds = false

                BitmapFactory.decodeByteArray(imageData, 0, imageData.size, options)
            } else {
                // Simple decode without scaling
                val connection = URL(url).openConnection().apply {
                    connectTimeout = 10000
                    readTimeout = 10000
                    setRequestProperty("User-Agent", "Android App")
                }

                connection.getInputStream().use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
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

private fun saveToDiskCache(cacheFile: File, bitmap: Bitmap) {
    try {
        FileOutputStream(cacheFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out) // Use JPEG with compression
            out.flush()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}