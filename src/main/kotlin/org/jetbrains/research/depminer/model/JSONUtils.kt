package org.jetbrains.research.depminer.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

fun convertToJsonString(dependenciesMap: Collection<Dependency>): String {
    val gson = Gson()
    return gson.toJson(dependenciesMap)
}

fun convertSingleDependencyToJSON(dependency: Dependency): String {
    val gson = Gson()
    return gson.toJson(dependency)
}

fun parseReviewHistory(pathToFile: String): MutableList<Review> {
    val gson = Gson()
    val rawJsonString = File(pathToFile).readText()
    val sType = object : TypeToken<List<Review>>() { }.type
    return gson.fromJson<List<Review>>(rawJsonString, sType) as MutableList<Review>
}



