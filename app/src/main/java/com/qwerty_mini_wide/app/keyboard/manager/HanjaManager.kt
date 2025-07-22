package com.qwerty_mini_wide.app.keyboard.manager


import android.content.Context
import android.util.Log
import com.qwerty_mini_wide.app.keyboard.model.HanjaEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

typealias HanjaDictionary = Map<String, List<HanjaEntry>>

object HanjaManager {
    private const val TAG = "HanjaManager"
    private const val FILE_NAME = "hanja.js"

    /** 앱 전체에서 한 번만 로드해두고 재사용할 사전 */
    private var dictionary: HanjaDictionary = emptyMap()

    /**
     * 반드시 앱 시작 시 (예: Application.onCreate) 혹은
     * 실제 사용 직전에 한 번만 호출해주세요.
     */
    fun init(context: Context) {
        if (dictionary.isEmpty()) {
            loadDictionary(context)
        }
    }

    private fun loadDictionary(context: Context) {
        try {
            // assets/hanja.json 열기
            context.assets.open(FILE_NAME).use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    val type = object : TypeToken<HanjaDictionary>() {}.type
                    dictionary = Gson().fromJson(reader, type)
                }
            }
            Log.d(TAG, "✅ HanjaDictionary loaded: ${dictionary.size} entries")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to load hanja.json from assets", e)
        }
    }

    /**
     * 한글(예: "가")에 매핑된 한자 목록을 반환.
     * 없으면 null.
     */
    fun entries(kor: String): List<HanjaEntry>? =
        dictionary[kor]
}
