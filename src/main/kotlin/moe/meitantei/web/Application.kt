package moe.meitantei.web

import kotlinx.coroutines.*
import moe.meitantei.common.ConanEpisode
import moe.meitantei.scraping.ConanScraper
import kotlin.system.measureTimeMillis



fun main(args: Array<String>) {
    val scraper = ConanScraper("https://www.detectiveconanworld.com/")
    val episodeNames = scraper.getEpisodeNames("wiki/Anime").subList(0, 100)
    val finishedList = mutableListOf<ConanEpisode>()
    val coroutines: MutableList<Deferred<List<ConanEpisode>>> = mutableListOf()
    val numEpisodes = episodeNames.size
    val time = measureTimeMillis {
        runBlocking {
            val eachThread = numEpisodes / 10
            var remaining = numEpisodes % 10
            var start = 0
            var end = if (remaining > 0)
                eachThread
            else
                eachThread - 1
            for (i in 0..9) {
                val threadStart = start
                val threadEnd = end
                coroutines.add(GlobalScope.async {
                    val sublist: List<String> = episodeNames.subList(threadStart, threadEnd)
                    sublistScrape(sublist, scraper)
                })
                remaining -= 1
                start = end + 1
                end = if (remaining > 0)
                    start + eachThread
                else
                    start + eachThread - 1
            }
            for (i in 0..9)
                finishedList.addAll(coroutines[i].await())
        }
    }
    println("Multi-threaded execution took $time milliseconds")
    val list = finishedList.filter {
        it.canonCase
    }.sortedByDescending {
        ep -> ep.broadcastRating.max()
    }
    list.forEach {
        ep -> println(ep.mangaSource)
    }
}

fun sublistScrape(arr: List<String>, scraper: ConanScraper): List<ConanEpisode> =
    arr.map {
        name -> scraper.transform(name)
    }.toList()