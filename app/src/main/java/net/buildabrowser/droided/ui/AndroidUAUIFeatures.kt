package net.buildabrowser.droided.ui

import net.buildabrowser.babbrowser.html.navigation.Navigable
import net.buildabrowser.babbrowser.html.ua.DownloadManager
import net.buildabrowser.babbrowser.html.ua.UAUIFeatures

class AndroidUAUIFeatures : UAUIFeatures {

    override fun addTopLevelTraversable(sourceNavigable: Navigable): Navigable {
        // TODO: Implement
        return sourceNavigable
    }

    override fun downloadManager(): DownloadManager? {
        // TODO: Implement
        TODO("Not yet implemented")
    }

}