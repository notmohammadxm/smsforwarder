package com.smsforwarding.local.data.local

import androidx.room.TypeConverter
import org.json.JSONArray

class Converters {
 @TypeConverter fun stringsToJson(value: List<String>): String = JSONArray(value).toString()
 @TypeConverter fun jsonToStrings(value: String): List<String> {
  val array = JSONArray(value)
  return List(array.length()) { array.optString(it) }
 }
 @TypeConverter fun longsToJson(value: List<Long>): String = JSONArray(value).toString()
 @TypeConverter fun jsonToLongs(value: String): List<Long> {
  val array = JSONArray(value)
  return List(array.length()) { array.optLong(it) }
 }
 @TypeConverter fun matchModeToString(value: MatchMode): String = value.name
 @TypeConverter fun stringToMatchMode(value: String): MatchMode = MatchMode.valueOf(value)
 @TypeConverter fun matchTypeToString(value: MatchType): String = value.name
 @TypeConverter fun stringToMatchType(value: String): MatchType = MatchType.valueOf(value)
 @TypeConverter fun statusToString(value: ForwardStatus): String = value.name
 @TypeConverter fun stringToStatus(value: String): ForwardStatus = ForwardStatus.valueOf(value)
}
