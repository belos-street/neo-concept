package com.neoconcept.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAssetDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : AssetDataSource {
        override fun open(path: String): InputStream = context.assets.open(path)

        @Suppress("SwallowedException")
        override fun exists(path: String): Boolean =
            try {
                context.assets.open(path).close()
                true
            } catch (e: IOException) {
                false
            }
    }
