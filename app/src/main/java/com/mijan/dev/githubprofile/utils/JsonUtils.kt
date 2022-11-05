package com.mijan.dev.githubprofile.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.lang.reflect.Type

inline fun <reified T> Any.fromJson(filename: String): T {
    val inputStream = javaClass.classLoader?.getResourceAsStream(filename) ?: error("File '$filename' not found")
    val isList = List::class.java.isAssignableFrom(T::class.java)
    return if (isList) {
        val type: Type = object : TypeToken<T>() {}.type
        Gson().fromJson(InputStreamReader(inputStream, "UTF-8"), type)
    } else {
        Gson().fromJson(InputStreamReader(inputStream, "UTF-8"), T::class.java)
    }
}