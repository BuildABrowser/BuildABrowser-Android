package net.buildabrowser.droided

object BrowserVersion {
    const val NAME: String = "BuildABrowser Browser"
    const val NAME_SHORT: String = "BABBrowser"
    const val MAJOR_VERSION: Int = 0
    const val MINOR_VERSION: Int = 1
    const val PATCH_VERSION: Int = 0

    fun asVersionString(): String {
        return "$NAME v$MAJOR_VERSION.$MINOR_VERSION.$PATCH_VERSION"
    }

    fun asUAString(): String {
        return "$NAME_SHORT/$MAJOR_VERSION.$MINOR_VERSION.$PATCH_VERSION"
    }
}