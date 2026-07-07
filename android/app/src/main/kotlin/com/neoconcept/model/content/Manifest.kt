package com.neoconcept.model.content

import kotlinx.serialization.Serializable

@Serializable
data class Manifest(
    val version: String,
    val schemaVersion: Int,
    val minAppVersion: String,
    val updatedAt: String,
    val books: List<ManifestBook>,
)

@Serializable
data class ManifestBook(
    val id: String,
    val title: String,
    val subtitle: String,
    val order: Int,
    val totalLessons: Int,
    val path: String,
)
