package com.prismwin.apps.data.web

interface WebConfigRepository {
    suspend fun getWebViewUrl(): String?
}
