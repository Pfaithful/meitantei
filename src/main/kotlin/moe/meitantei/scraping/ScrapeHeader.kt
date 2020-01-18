package moe.meitantei.scraping

enum class ScrapeHeader(val value: String) {

    TITLE("Title"),
    JAPANESE_TITLE("Japanese title"),
    BROADCAST_RATING("Broadcast rating"),
    MANGA_CASE("Manga case"),
    FILLER_CASE("Filler case"),
    ORIGINAL_AIRDATE("Original airdate"),
    SEASON("Season"),
    MANGA_SOURCE("Manga source"),

    ENGLISH_TITLE("English title"),
    DUBBED_EPISODE("Dubbed episode"),

    CAST("Cast"),
    CASES_SOLVED_BY("Cases solved by"),
    CASE_SOLVED_BY("Case solved by"),

    DIRECTOR("Director"),
    ORGANIZER("Organizer"),
    STORYBOARD("Storyboard"),
    EPISODE_DIRECTOR("Episode director"),
    ANIMATION_DIRECTOR("Animation director"),

    OPENING_SONG("Opening song"),
    CLOSING_SONG("Closing song")

}