package net.buildabrowser.babbrowser.embedding.android.network.imp

import net.buildabrowser.babbrowser.fetch.FetchRequest
import net.buildabrowser.babbrowser.fetch.UAChooser
import net.buildabrowser.babbrowser.renderer.RendererVersion

class BasicUAChooserImp : UAChooser {

    override fun chooseUAString(request: FetchRequest): String {
        val rendererString = RendererVersion.asUAString()

        val osName = "Linux; Android 10; K"
        return when (request.url().host) {
            "whatismybrowser.com", "www.whatismybrowser.com" -> "$rendererString ($osName)"
            "buildabrowser.net", "frogfind.de" -> "Mozilla/5.0 ($osName) $rendererString"
            else -> "Mozilla/5.0($osName) $rendererString Firefox/149.0 (Not actually Firefox)"
        }
    }

}