package moe.meitantei.common

class FunimationInfo(var titles: List<String> = listOf(), var dubbedCount: List<Int> = listOf()) {

    fun fromPair(pairInfo: Pair<List<String>, List<String>>): FunimationInfo {
        this.titles = pairInfo.first
        this.dubbedCount = parseDubs(pairInfo.second)
        return this
    }

    private fun parseDubs(dubStrings: List<String>) = dubStrings
        .map { dub ->
            dub.replace("Episode ", "") }
        .map { dub ->
            dub.toInt()
        }
}