package moe.meitantei.mongo

enum class MongoFilter(val value: String) {
    EQ("eq"),
    LT("lt"),
    GT("gt"),
    IN("in"),
    NE("ne"),
    REGEX("regex"),
    EXISTS("exists"),
    ALL("all")
}