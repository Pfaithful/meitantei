package moe.meitantei.common

import moe.meitantei.scraping.utils.stringToDate
import java.time.LocalDate

fun episode(episodeInfo: ConanEpisode.() -> Unit): ConanEpisode =
    ConanEpisode().apply(episodeInfo)

/*
 * Applies not only to single episodes, but whole arcs as well. Thus, a single ConanEpisode
 * might make up multiple actual episodes, with multiple titles, broadcast ratings and the like
 */
class ConanEpisode {
    var title = emptyList<String>()
    var japaneseTitle = mutableListOf<JapaneseTitle>()
    var broadcastRating = mutableListOf<Double>()
    var canonCase: Boolean = false
    var caseNumber: MutableList<Int> = mutableListOf()
    var broadcastDate: List<LocalDate> = mutableListOf()
    var remasteredDate: List<LocalDate> = mutableListOf()
    var season: Int = 0
    var mangaSource: List<String> = mutableListOf()
    val with = this

    infix fun title(episodeTitle: List<String>?): ConanEpisode {
        if (episodeTitle != null) {
            title = episodeTitle
        }
        return this
    }

    infix fun japaneseTitle(episodeTitle: List<String>?): ConanEpisode {
        if (episodeTitle != null) {
            for (x in episodeTitle.indices step 2)
                japaneseTitle.add(JapaneseTitle(episodeTitle[0], episodeTitle[1]))
        }
        return this
    }

    infix fun broadcastRatings(ratings: List<String>?): ConanEpisode {
        if (ratings != null) {
            for (rating in ratings) {
                val doubleRating: Double = ratingPercentage(rating)
                broadcastRating.add(doubleRating)
            }
        }
        return this
    }

    fun caseNumber(canon: List<String>?, filler: List<String>?): ConanEpisode {
        if (canon != null) {
            canonCase = true
            caseNumber.addAll(convertCaseNumber(canon))
        } else if (filler != null) {
            caseNumber.addAll(convertCaseNumber(filler))
        }
        return this
    }

    infix fun broadcastDate(dates: List<String>?): ConanEpisode {
        if (dates != null) {
            broadcastDate = dates.filter { date ->
                !date.contains("\\(Remastered Version\\)|\\*".toRegex())
            }.map { date ->
                date.replace("\\(.*\\)".toRegex(), "").trim()
            }.map { date ->
                stringToDate(date)
            }.toList()
            remasteredDate = dates.filter { date ->
                date.contains("\\(Remastered version\\)".toRegex())
            }.map { date ->
                date.replace("\\(.*\\)".toRegex(), "").trim()
            }.map { date ->
                stringToDate(date)
            }.toList()
        }
        return this
    }

    infix fun season(airedSeason: List<String>?): ConanEpisode {
        season = airedSeason?.single()?.toInt()!!
        return this
    }

    infix fun mangaSource(source: List<String>?): ConanEpisode {
        mangaSource = source!!
        return this
    }


    private fun ratingPercentage(rating: String): Double {
        val arr = rating.replace("%", "").replace(",", ".").split(" ")
        return if (arr[0] == "?.?")
            0.0
        else
            arr[0].toDouble()
    }

    private fun convertCaseNumber(caseNumbers: List<String>): List<Int> =
        caseNumbers.map { case ->
            case.replace("#", "").toInt()
        }.toList()

    override fun toString(): String =
        "Title(s): ${formatArrayToString(title)}" +
                "Japanese Title(s): ${formatArrayToString(japaneseTitle)}" +
                "Broadcast Rating(s): ${formatArrayToString(broadcastRating)}" +
                if (canonCase) {
                    "Manga Case: "
                } else {
                    "Filler Case: "
                }.plus(formatArrayToString(caseNumber)) +
                "Original Broadcast Date(s): ${formatArrayToString(broadcastDate)}"

    private fun <T> formatArrayToString(input: List<T>): String {
        return if (input.size == 1) {
            input.single().toString() + "\n"
        } else
            input.joinToString("\n") + "\n"
    }
}

class JapaneseTitle(
    private var originalTitle: String = "Unavailable",
    private var romanjiTitle: String = "Unavailable"
) {

    override fun toString(): String = "Original Title: $originalTitle, Romanji Title: $romanjiTitle"
}
