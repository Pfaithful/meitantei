package moe.meitantei.model

class JapaneseTitle(
    private var originalTitle: String = "Unavailable",
    private var romanjiTitle: String = "Unavailable"
) {
    override fun toString(): String = "Original Title: $originalTitle, Romanji Title: $romanjiTitle"
}