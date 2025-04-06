package org.example.model.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.util.concurrent.TimeUnit

object DbClient {

    private val connectionString = ConnectionString(
        "mongodb://localhost:27017/github_db?" +
                "authSource=admin&" +
                "directConnection=true&" +
                "serverSelectionTimeoutMS=5000"
    )

    val client = KMongo.createClient(
        MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToSocketSettings { builder ->
                builder.connectTimeout(5, TimeUnit.SECONDS)
            }
            .build()
    ).coroutine

}