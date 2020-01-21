package moe.meitantei.scraping

import moe.meitantei.model.ConanEpisode
import moe.meitantei.model.EpisodeStaff
import moe.meitantei.model.builders.EpisodeStaffBuilder
import moe.meitantei.model.builders.episode
import moe.meitantei.scraping.utils.cleanHtmlFromHeader
import moe.meitantei.scraping.utils.cleanHtmlFromValue
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class ConanScraper(private var baseUrl: String) {

    val document: (String) -> Document =
        {
            path: String -> Jsoup.connect(baseUrl+path).get()
        }

    fun getEpisodeNames(path: String): List<String> {
        val doc = Jsoup.connect(baseUrl+path).get()
        val tds: Elements = doc.select("td[style=background:#f2fde9;] a")
        return tds.asSequence().toList()
            .map {
                    element -> element.attr("href")
            }
            .distinct()
            .filter {
                    element -> !element.endsWith("redlink=1")
            }
            .toList()
    }

    fun transform(path: String): ConanEpisode {
        val doc = this.document(path)
        val infoMap = extractInformation(doc)
        return createEpisode(infoMap)
//        val href = doc.select("a[title=Edit section: Case]").eachAttr("href").single()
    }

    private fun createEpisode(infoMap: Map<ScrapeHeader, List<String>>): ConanEpisode =
        episode {
            with title infoMap.getValue(ScrapeHeader.TITLE)
            with japaneseTitle infoMap.getValue(ScrapeHeader.JAPANESE_TITLE)
            with broadcastRatings infoMap.getValue(ScrapeHeader.BROADCAST_RATING)
            caseNumber(infoMap.getValue(ScrapeHeader.MANGA_CASE), infoMap.getValue(ScrapeHeader.FILLER_CASE))
            with broadcastDate infoMap.getValue(ScrapeHeader.ORIGINAL_AIRDATE)
            with season infoMap.getValue(ScrapeHeader.SEASON)
            with mangaSource infoMap.getValue(ScrapeHeader.MANGA_SOURCE)
            with funimationInfo Pair(infoMap.getValue(ScrapeHeader.ENGLISH_TITLE), infoMap.getValue(ScrapeHeader.DUBBED_EPISODE))
            with cast infoMap.getValue(ScrapeHeader.CAST)
            with solvedBy infoMap.getOrElse(ScrapeHeader.CASES_SOLVED_BY, { infoMap.getOrDefault(ScrapeHeader.CASE_SOLVED_BY, emptyList()) }) //Maybe introduce extension function for this?
            with staff buildEpisodeStaff(infoMap)
            with openingSong infoMap.getValue(ScrapeHeader.OPENING_SONG)
            with closingSong infoMap.getValue(ScrapeHeader.CLOSING_SONG)
        }.build()

    private fun extractInformation(document: Document): Map<ScrapeHeader, List<String>> {
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
        return convertToNullSafeMap(map)
    }

    private fun buildEpisodeStaff(infoMap: Map<ScrapeHeader, List<String>>): EpisodeStaff =
        EpisodeStaffBuilder()
            .director(infoMap.getValue(ScrapeHeader.DIRECTOR).single())
            .organizers(infoMap.getValue(ScrapeHeader.ORGANIZER))
            .storyboarders(infoMap.getValue(ScrapeHeader.STORYBOARD))
            .episodeDirector(infoMap.getValue(ScrapeHeader.EPISODE_DIRECTOR))
            .animationDirector(infoMap.getValue(ScrapeHeader.ANIMATION_DIRECTOR))
            .build()

    private fun convertToNullSafeMap(unsafe: Map<String, List<String>>): Map<ScrapeHeader, List<String>> =
        ScrapeHeader.values().associate {
            header -> Pair(header, unsafe.getOrDefault(header.value, emptyList()))
        }

    private fun getVoiceCastHtml(header: Element) =
        header
            .parent()
            .parent()
            .getElementsByTag("p")
            .html()
            .replace("&nbsp;", " ")
}