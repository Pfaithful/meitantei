package moe.meitantei.common

data class EpisodeStaff(val director: String = "", val organizers: List<String> = emptyList(), val storyboarders: List<String> = emptyList(),
                   val episodeDirectors: List<String> = emptyList(), val animationDirectors: List<String> = emptyList())