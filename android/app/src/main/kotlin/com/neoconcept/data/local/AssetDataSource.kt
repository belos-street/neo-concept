package com.neoconcept.data.local

import java.io.IOException
import java.io.InputStream

interface AssetDataSource {
    @Throws(IOException::class)
    fun open(path: String): InputStream

    fun exists(path: String): Boolean
}
