package moe.meitantei.model.builders

import moe.meitantei.model.*
import moe.meitantei.scraping.utils.stringToDate
import java.time.LocalDate

/*
 * Allows for an easy DSL for building a ConanEpisode Object
 */
fun episode(episodeInfo: ConanEpisodeBuilder.() -> Unit): ConanEpisodeBuilder =
    ConanEpisodeBuilder().apply(episodeInfo)

class ConanEpisodeBuilder(val title: MutableList<String> = mutableListOf(), val japaneseTitle: MutableList<JapaneseTitle> = mutableListOf(),
                          val broadcastRating: MutableList<Double> = mutableListOf(), var canonCase: Boolean = false,
                          var caseNumber: MutableList<Int> = mutableListOf(), var broadcastDate: List<LocalDate> = mutableListOf(),
                          var remasteredDate: List<LocalDate> = mutableListOf(), var season: Int = 0,
                          var mangaSource: MutableList<String> = mutableListOf<String>(), var englishInfo: FunimationInfo = FunimationInfo(),
                          var castList: List<ConanCharacter> = emptyList(),  var solvedBy: List<ConanCharacter> = emptyList(),
                          var episodeStaff: EpisodeStaff = EpisodeStaff(), var openingSong: String = "", var closingSong: String = "") {
    val with: ConanEpisodeBuilder = this

    infix fun title(episodeTitle: List<String>): ConanEpisodeBuilder {
        title.addAll(episodeTitle)
        return this
    }

    infix fun japaneseTitle(episodeTitle: List<String>): ConanEpisodeBuilder {
        for (x in episodeTitle.indices step 2)
            japaneseTitle.add(JapaneseTitle(episodeTitle[0], episodeTitle[1]))
        return this
    }

    infix fun broadcastRatings(ratings: List<String>): ConanEpisodeBuilder {
        for (rating in ratings) {
            val doubleRating: Double = ratingPercentage(rating)
            broadcastRating.add(doubleRating)
        }
        return this
    }

    fun caseNumber(canon: List<String>, filler: List<String>): ConanEpisodeBuilder {
        if (canon.isNotEmpty()) {
            canonCase = true
            caseNumber.addAll(convertCaseNumber(canon))
        } else if (filler.isNotEmpty()) {
            caseNumber.addAll(convertCaseNumber(filler))
        }
        return this
    }

    infix fun broadcastDate(dates: List<String>): ConanEpisodeBuilder {
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

    infix fun season(airedSeason: List<String>): ConanEpisodeBuilder {
        season = airedSeason.single().toInt()
        return this
    }

    infix fun mangaSource(source: List<String>): ConanEpisodeBuilder {
        mangaSource.addAll(source)
        return this
    }

    infix fun funimationInfo(pair: Pair<List<String>, List<String>>): ConanEpisodeBuilder {
        if (pair.first.isNotEmpty() && pair.second.isNotEmpty()) {
            englishInfo = englishInfo.fromPair(Pair(pair.first, pair.second))
        }
        return this
    }

    infix fun cast(cast: List<String>): ConanEpisodeBuilder {
        this.castList = cast.map{name ->
            ConanCharacter(name)
        }
        return this
    }

    infix fun solvedBy(people: List<String>): ConanEpisodeBuilder {
        this.solvedBy = people.map {
                name -> cleanSolvedBy(name)
        }.map { detective ->
            ConanCharacter(detective)
        }
        return this
    }

    infix fun staff(staff: EpisodeStaff): ConanEpisodeBuilder {
        this.episodeStaff = staff
        return this
    }

    infix fun openingSong(song: List<String>): ConanEpisodeBuilder {
        this.openingSong = song.single()
        return this
    }

    infix fun closingSong(song: List<String>): ConanEpisodeBuilder {
        this.closingSong = song.single()
        return this
    }

    fun build(): ConanEpisode = ConanEpisode(title, japaneseTitle, broadcastRating, canonCase, caseNumber,
                                            broadcastDate, remasteredDate, season, mangaSource, englishInfo,
                                            castList, solvedBy, episodeStaff, openingSong, closingSong)

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