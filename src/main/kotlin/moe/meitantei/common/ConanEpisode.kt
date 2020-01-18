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
    private var title = emptyList<String>()
    private var japaneseTitle = mutableListOf<JapaneseTitle>()
    var broadcastRating = mutableListOf<Double>()
    var canonCase = false
    private var caseNumber: MutableList<Int> = mutableListOf()
    private var broadcastDate: List<LocalDate> = mutableListOf()
    private var remasteredDate: List<LocalDate> = mutableListOf()
    private var season: Int = 0
    var mangaSource: List<String> = mutableListOf<String>()
    private var englishInfo: FunimationInfo = FunimationInfo()
    var castList: List<ConanCharacter> = emptyList()
    var solvedBy: List<ConanCharacter> = emptyList()
    var episodeStaff: EpisodeStaff = EpisodeStaff()
    var openingSong: String = ""
    var closingSong: String = ""
    val with = this

    infix fun title(episodeTitle: List<String>): ConanEpisode {
            title = episodeTitle

        return this
    }

    infix fun japaneseTitle(episodeTitle: List<String>): ConanEpisode {
            for (x in episodeTitle.indices step 2)
                japaneseTitle.add(JapaneseTitle(episodeTitle[0], episodeTitle[1]))

        return this
    }

    infix fun broadcastRatings(ratings: List<String>): ConanEpisode {
            for (rating in ratings) {
                val doubleRating: Double = ratingPercentage(rating)
                broadcastRating.add(doubleRating)
            }

        return this
    }

    fun caseNumber(canon: List<String>, filler: List<String>): ConanEpisode {
        if (canon.isNotEmpty()) {
            canonCase = true
            caseNumber.addAll(convertCaseNumber(canon))
        } else if (filler.isNotEmpty()) {
            caseNumber.addAll(convertCaseNumber(filler))
        }
        return this
    }

    infix fun broadcastDate(dates: List<String>): ConanEpisode {
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
        return this
    }

    infix fun season(airedSeason: List<String>): ConanEpisode {
        season = airedSeason.single().toInt()
        return this
    }

    infix fun mangaSource(source: List<String>): ConanEpisode {
        mangaSource = source
        return this
    }

    infix fun funimationInfo(pair: Pair<List<String>, List<String>>): ConanEpisode {
        if (pair.first.isNotEmpty() && pair.second.isNotEmpty()) {
            englishInfo = englishInfo.fromPair(Pair(pair.first, pair.second))
        }
        return this
    }

    infix fun cast(cast: List<String>): ConanEpisode {
        this.castList = cast.map{name ->
            ConanCharacter(name)
        }
        return this
    }

    infix fun solvedBy(people: List<String>): ConanEpisode {
        this.solvedBy = people.map {
            name -> cleanSolvedBy(name)
        }.map { detective ->
            ConanCharacter(detective)
        }
        return this
    }

    infix fun staff(staff: EpisodeStaff): ConanEpisode {
        this.episodeStaff = staff
        return this
    }

    infix fun openingSong(song: List<String>): ConanEpisode {
        this.openingSong = song.single()
        return this
    }

    infix fun closingSong(song: List<String>): ConanEpisode {
        this.closingSong = song.single()
        return this
    }

    private fun ratingPercentage(rating: String): Double {
        val arr = rating.replace("%", "")
            .replace(",", ".")
            .split(" ")
        return if (arr[0] == "?.?")
            0.0
        else
            arr[0].toDouble()
    }

    private fun convertCaseNumber(caseNumbers: List<String>): List<Int> =
        caseNumbers.map { case ->
            case.replace("#", "").toInt()
        }.toList()

    private fun cleanSolvedBy(input: String): String =
        input.replace("\\(x\\d\\)".toRegex(), "").trim()

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
