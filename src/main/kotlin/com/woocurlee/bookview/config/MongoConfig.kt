package com.woocurlee.bookview.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter

@Configuration
class MongoConfig {
    @Value("\${spring.mongodb.uri}")
    private lateinit var mongoUri: String

    @Value("\${spring.mongodb.database}")
    private lateinit var databaseName: String

    @Bean
    fun mongoClient(): MongoClient = MongoClients.create(mongoUri)

    @Bean
    fun mongoDatabaseFactory(mongoClient: MongoClient): MongoDatabaseFactory =
        SimpleMongoClientDatabaseFactory(mongoClient, databaseName)

    @Bean
    fun mongoTemplate(
        mongoDatabaseFactory: MongoDatabaseFactory,
        mappingMongoConverter: MappingMongoConverter,
    ): MongoTemplate {
        // _class 필드 제거
        mappingMongoConverter.setTypeMapper(DefaultMongoTypeMapper(null))
        return MongoTemplate(mongoDatabaseFactory, mappingMongoConverter)
    }
}
