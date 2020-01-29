package moe.meitantei.mongo.utils

import moe.meitantei.model.ConanEpisode
import moe.meitantei.model.builders.episode
import org.bson.Document

fun episodeToDocument(episode: ConanEpisode): Document {
    val map = mapOf<String, Any> ("Title" to episode.title, "Japanese Title" to episode.japaneseTitle,
        "Broadcast Rating" to episode.broadcastRating, "Canon?" to episode.canonCase,
        "Case Number" to episode.caseNumber, "Broadcast Date" to episode.broadcastDate,
        "Remastered Date" to episode.remasteredDate, "Season" to episode.season, "Manga Source" to episode.mangaSource,
        "English Info" to episode.englishInfo, "Cast" to episode.castList, "Solved by" to episode.solvedBy,
        "Staff" to episode.episodeStaff, "Opening" to episode.openingSong, "Ending" to episode.closingSong)
    return Document(map)
}
