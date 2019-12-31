package moe.meitantei.scraping.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

val monthTable: HashMap<String, String> = hashMapOf("Jan" to "January","Feb" to "February",
    "Mar" to "March","Apr" to "April", "Jun" to "June","Jul" to "July", "Aug" to "August",
    "Sep" to "September", "Oct" to "October", "Nov" to "November","Dec" to "December")

fun stringToDate(date: String): LocalDate {
    val formattedMonth = if (monthTable.containsKey(date.split(" ")[0]))
        monthTable[date.take(3)] + date.slice(3 until date.length)
    else
        date
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
    return LocalDate.parse(formattedMonth, formatter)
}