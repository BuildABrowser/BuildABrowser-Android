package net.buildabrowser.droided.network.imp

import net.buildabrowser.babbrowser.fetch.FetchRequest
import net.buildabrowser.babbrowser.fetch.UAChooser
import net.buildabrowser.babbrowser.renderer.RendererVersion
import net.buildabrowser.droided.BrowserVersion


const val CHROME_UA_STRING
        : String = ("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
        + " Chrome/146.0.0.0 Safari/537.36")

class UAChooserImp : UAChooser {

    override fun chooseUAString(request: FetchRequest): String {
        val browserString: String = BrowserVersion.asUAString()
        val rendererString = RendererVersion.asUAString()
        val verString = "$browserString $rendererString"

        val osName: String = "Linux; Android 10; K"
        return when (request.url().host) {
            // Unfortunately DDG captchas the user with the default UA (and captchas would require JS)
            "html.duckduckgo.com", "duckduckgo.com" -> CHROME_UA_STRING + " $verString"
            // Unfortunately, HN just shows a page showing "sorry" half the time when using a proper UA string
            "news.ycombinator.com" -> CHROME_UA_STRING
            "whatismybrowser.com", "www.whatismybrowser.com" -> "$verString ($osName)"
            "buildabrowser.net", "frogfind.de" -> "Mozilla/5.0 ($osName) $verString"
            else -> "Mozilla/5.0($osName) $verString Firefox/149.0 (Not actually Firefox)"
        }
    }

}