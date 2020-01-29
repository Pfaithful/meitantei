package moe.meitantei.mongo

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import org.bson.Document

class MongoDAO() {

    private val connection: String = System.getenv("MONGO_CONNECTION")
    private val database: String = System.getenv("DATABASE_NAME")

    private val mongoDatabase: MongoDatabase =
        MongoClients.create(connection).getDatabase(database)

    lateinit var collection: MongoCollection<Document>

    fun setCollection(collectionName: String) {
        collection = mongoDatabase.getCollection(collectionName)
    }

    fun getDocument(id: String): Document =
        collection.find(eq("id", id)).single()

    fun getDocuments(params: MongoParams): Iterable<Document> {
        return collection.find(params.toFilter())
    }

    fun deleteDocument(id: String): Boolean =
        collection.deleteOne(eq("id", id)).wasAcknowledged()

}