package com.neoconcept.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object AssetUtils {

    fun copyAssetToDir(context: Context, assetPath: String, targetDir: File): Boolean {
        try {
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            val assetManager = context.assets
            val assets = assetManager.list(assetPath) ?: emptyArray()

            if (assets.isEmpty()) {
                val targetFile = File(targetDir, File(assetPath).name)
                copyAssetFile(context, assetPath, targetFile)
            } else {
                for (asset in assets) {
                    val subAssetPath = "$assetPath/$asset"
                    val subAssets = assetManager.list(subAssetPath) ?: emptyArray()
                    if (subAssets.isEmpty()) {
                        val targetFile = File(targetDir, asset)
                        copyAssetFile(context, subAssetPath, targetFile)
                    } else {
                        val subDir = File(targetDir, asset)
                        copyAssetToDir(context, subAssetPath, subDir)
                    }
                }
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun copyAssetFile(context: Context, assetPath: String, targetFile: File): Boolean {
        try {
            targetFile.parentFile?.mkdirs()
            context.assets.open(assetPath).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun isAssetExists(context: Context, assetPath: String): Boolean {
        return try {
            context.assets.open(assetPath).use { }
            true
        } catch (e: IOException) {
            false
        }
    }
}
