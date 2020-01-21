package moe.meitantei.model

import java.time.LocalDate

/*
 * Applies not only to single episodes, but whole arcs as well. Thus, a single ConanEpisode
 * might make up multiple actual episodes, with multiple titles, broadcast ratings and the like
 */
data class ConanEpisode(val title: MutableList<String>, val japaneseTitle: MutableList<JapaneseTitle>,
                   val broadcastRating: MutableList<Double>, val canonCase: Boolean,
                   val caseNumber: MutableList<Int> , val broadcastDate: List<LocalDate>,
                   val remasteredDate: List<LocalDate>, val season: Int,
                   var mangaSource: MutableList<String>, val englishInfo: FunimationInfo,
                   val castList: List<ConanCharacter>,  val solvedBy: List<ConanCharacter>,
                   val episodeStaff: EpisodeStaff, val openingSong: String, val closingSong: String) {}


