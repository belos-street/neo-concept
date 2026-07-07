package com.neoconcept.data.local

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class FakeAssetDataSource(private val root: File) : AssetDataSource {
    override fun open(path: String): InputStream {
        val file = File(root, path)
        if (!file.exists()) {
            throw FileNotFoundException("Asset not found: $path")
        }
        return file.inputStream()
    }

    override fun exists(path: String): Boolean = File(root, path).exists()
}
