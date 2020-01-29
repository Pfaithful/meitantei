package moe.meitantei.mongo
import com.mongodb.client.model.Filters.*
import org.bson.conversions.Bson


class MongoParams(private val groupFilter: (List<Bson>) -> Bson,
                  private val filterMap: Map<MongoFilter, MongoPair>) {

    fun toFilter(): Bson {
        val bsonList = filterMap.map { (filter, mongoPair) ->  createBson(filter, mongoPair)}.filterNotNull()
        return groupFilter.invoke(bsonList)
    }

    private fun createBson(filter: MongoFilter, keyValue: MongoPair): Bson? = when (filter) {
        MongoFilter.EQ -> eq(keyValue.key, keyValue.value.first())
        MongoFilter.GT-> gt(keyValue.key, keyValue.value.first())
        MongoFilter.IN -> `in`(keyValue.key, keyValue.value)
        MongoFilter.EXISTS -> exists(keyValue.key, keyValue.value.first().toBoolean())
        MongoFilter.ALL -> all(keyValue.key, keyValue.value)
        MongoFilter.LT -> lt(keyValue.key, keyValue.value.first())
        MongoFilter.NE -> ne(keyValue.key, keyValue.value.first())
        MongoFilter.REGEX -> regex(keyValue.key, keyValue.value.first().toPattern())
    }

}