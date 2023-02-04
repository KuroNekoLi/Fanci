package com.cmoney.kolfanci.extension

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

@Throws(JsonSyntaxException::class, JsonParseException::class)
inline fun <reified T> Gson.fromJson(json: String) : T = this.fromJson<T>(json, T::class.java)

@Throws(JsonSyntaxException::class, JsonParseException::class)
inline fun <reified T> Gson.fromJsonTypeToken(json: String) : T = this.fromJson(json, object : TypeToken<T>(){}.type)

@Throws(JsonSyntaxException::class, JsonParseException::class)
inline fun <reified T> Gson.toJsonTypeToken(src: Any) : String = this.toJson(src, object : TypeToken<T>(){}.type)