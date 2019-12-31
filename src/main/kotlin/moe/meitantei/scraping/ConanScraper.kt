package moe.meitantei.scraping

import moe.meitantei.common.ConanEpisode
import moe.meitantei.common.episode
import moe.meitantei.scraping.utils.cleanHtmlFromHeader
import moe.meitantei.scraping.utils.cleanHtmlFromValue
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class ConanScraper(var baseUrl: String) {

    val document: (String) -> Document = {
            path: String -> Jsoup.connect(baseUrl+path).get()
    }

    fun getEpisodeNames(path: String): List<String> {
        val doc = Jsoup.connect(baseUrl+path).get()
        val tds: Elements = doc.select("td[style=background:#f2fde9;] a")
        return tds.asSequence().toList()
            .map {
                    element -> element.attr("href")
            }.distinct()
            .filter {
                    element -> !element.endsWith("redlink=1")
            }.toList()
    }

    fun transform(path: String): ConanEpisode {
        val doc = this.document(path)
        val infoMap = extractInformationBlock(doc)
        return createEpisode(infoMap)
//        val href = doc.select("a[title=Edit section: Case]").eachAttr("href").single()
    }

    private fun createEpisode(infoMap: Map<String, List<String>>): ConanEpisode = episode {
        with title infoMap["Title"]
        with japaneseTitle infoMap["Japanese title"]
        with broadcastRatings infoMap["Broadcast rating"]
        caseNumber(infoMap["Manga case"], infoMap["Filler case"])
        with broadcastDate infoMap["Original airdate"]
        with season infoMap["Season"]
        with mangaSource infoMap["Manga source"]
    }

    private fun extractInformationBlock(document: Document): MutableMap<String, List<String>> {
        val infoTable = document.select("tbody")
        val headers = if (infoTable[0].children().size > 1)
            infoTable[0].getElementsByTag("th")
        else
            infoTable[1].getElementsByTag("th")
        var cleanedHeader: String
        var cleanedData: List<String>
        val map: MutableMap<String, List<String>> = mutableMapOf()
        for (header in headers) {
            val headerData: String = if (header.html().contains("Voice Cast"))
                getVoiceCastHtml(header)
            else
                header.nextElementSibling().html()
            cleanedData = cleanHtmlFromValue(headerData)
            cleanedHeader = cleanHtmlFromHeader(header.html())
            map[cleanedHeader] = cleanedData
        }
        return map
    }

    private fun getVoiceCastHtml(header: Element) = header
        .parent()
        .parent()
        .getElementsByTag("p")
        .html()
        .replace("&nbsp;", " ")
}