package net.buildabrowser.babbrowser.embedding.android.ui

import net.buildabrowser.babbrowser.html.navigation.Navigable
import net.buildabrowser.babbrowser.html.ua.TabManager

class NoOpTabManager : TabManager {

    override fun addTopLevelTraversable(sourceNavigable: Navigable): Navigable {
        // TODO: Implement
        return sourceNavigable
    }

}