package moe.meitantei.model.builders

import moe.meitantei.model.EpisodeStaff

class EpisodeStaffBuilder(private var director: String = "") {
    private val organizers: MutableList<String> = mutableListOf()
    private val storyboard: MutableList<String> = mutableListOf()
    private val episodeDirectors: MutableList<String> = mutableListOf()
    private val animationDirectors: MutableList<String> = mutableListOf()

    fun director(director: String): EpisodeStaffBuilder = this.apply {
        this.director = director
    }

    fun organizers(organizers: List<String>): EpisodeStaffBuilder = this.apply {
        this.organizers.addAll(organizers)
    }

    fun storyboarders(storyboard: List<String>): EpisodeStaffBuilder = this.apply {
        this.storyboard.addAll(storyboard)
    }

    fun episodeDirector(eDirectors: List<String>): EpisodeStaffBuilder = this.apply {
        this.episodeDirectors.addAll(eDirectors)
    }

    fun animationDirector(aDirectors: List<String>): EpisodeStaffBuilder = this.apply {
        this.animationDirectors.addAll(aDirectors)
    }

    fun build(): EpisodeStaff = EpisodeStaff(director, organizers, storyboard, episodeDirectors, animationDirectors)
}