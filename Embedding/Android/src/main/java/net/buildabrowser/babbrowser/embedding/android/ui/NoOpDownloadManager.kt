package net.buildabrowser.babbrowser.embedding.android.ui

import net.buildabrowser.babbrowser.fetch.FetchRequest
import net.buildabrowser.babbrowser.fetch.FetchResponse
import net.buildabrowser.babbrowser.html.ua.DownloadManager

class NoOpDownloadManager : DownloadManager {
    override fun allowDownload(request: FetchRequest) = false
    override fun allowDownload(response: FetchResponse) = false

    override fun startDownload(
        response: FetchResponse,
        suggestedFilename: String
    ) {}
}