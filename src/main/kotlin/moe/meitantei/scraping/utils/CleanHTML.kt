package moe.meitantei.scraping.utils

import org.jsoup.Jsoup

fun cleanHtmlFromValue(html: String): List<String> {
    val tokens = html.split("<br>".toRegex())
    return tokens.map {
        token -> Jsoup.parse(token).text()
    }.filter{
        token -> !token!!.contentEquals("(Remastered version)")
    }.toList()
}

fun cleanHtmlFromHeader(html: String): String = cleanHtmlFromValue(html).single().dropLast(1)