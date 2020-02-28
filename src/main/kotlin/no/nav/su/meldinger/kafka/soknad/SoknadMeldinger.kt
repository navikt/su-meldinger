package no.nav.su.meldinger.kafka.soknad

import org.json.JSONObject

interface KafkaMessage {
    fun key(): String
    fun value(): String
}

interface Visitor<T> {
    fun accept(json: String): Boolean
    fun fromJson(json: String): T?
}

sealed class SoknadMelding : KafkaMessage {
    companion object {
        internal const val sakIdKey = "sakId"
        internal const val aktoerIdKey = "aktoerId"
        internal const val soknadIdKey = "soknadId"
        internal const val soknadKey = "soknad"
        internal const val gsakIdKey = "gsakId"
        internal const val journalIdKey = "journalId"
    }
}

class NySoknad(
    val sakId: String,
    val aktoerId: String,
    val soknadId: String,
    val soknad: String
) : SoknadMelding() {
    override fun key() = sakId
    override fun value() = toJson()
    private fun toJson(): String {
        return """
            {
                "$sakIdKey":"$sakId",
                "$aktoerIdKey":"$aktoerId",
                "$soknadIdKey":"$soknadId",
                "$soknadKey":$soknad
            }
        """.trimIndent()
    }

    companion object : Visitor<NySoknad> {
        override fun accept(json: String): Boolean {
            val jsonObject = JSONObject(json)
            return !jsonObject.optString(sakIdKey).isNullOrEmpty()
                && !jsonObject.optString(aktoerIdKey).isNullOrEmpty()
                && !jsonObject.optString(soknadIdKey).isNullOrEmpty()
                && !jsonObject.optJSONObject(soknadKey).isEmpty
        }

        override fun fromJson(json: String): NySoknad? {
            return if (accept(json)) {
            val jsonObject = JSONObject(json)
            NySoknad(
                sakId = jsonObject.getString(sakIdKey),
                aktoerId = jsonObject.getString(aktoerIdKey),
                soknadId = jsonObject.getString(soknadIdKey),
                soknad = jsonObject.getJSONObject(soknadKey).toString()
            )
            } else {
                null
            }

        }
    }
}

class NySoknadMedSkyggesak(
    val sakId: String,
    val aktoerId: String,
    val soknadId: String,
    val soknad: String,
    val gsakId: String
) : SoknadMelding() {
    override fun key(): String = sakId
    override fun value(): String = toJson()
    private fun toJson(): String {
        return """
            {
                "$sakIdKey":"$sakId",
                "$aktoerIdKey":"$aktoerId",
                "$soknadIdKey":"$soknadId",
                "$soknadKey":$soknad,
                "$gsakIdKey":"$gsakId"
            }
        """.trimIndent()
    }

    companion object : Visitor<NySoknadMedSkyggesak> {
        override fun accept(json: String): Boolean {
            return NySoknad.accept(json)
                && !JSONObject(json).optString(gsakIdKey).isNullOrEmpty()
        }

        override fun fromJson(json: String): NySoknadMedSkyggesak? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySoknadMedSkyggesak(
                    sakId = jsonObject.getString(sakIdKey),
                    aktoerId = jsonObject.getString(aktoerIdKey),
                    soknadId = jsonObject.getString(soknadIdKey),
                    soknad = jsonObject.getJSONObject(soknadKey).toString(),
                    gsakId = jsonObject.getString(gsakIdKey).toString()
                )
            } else {
                null
            }
        }
    }
}

class NySoknadMedJournalId(
    val sakId: String,
    val aktoerId: String,
    val soknadId: String,
    val soknad: String,
    val gsakId: String,
    val journalId: String
) : SoknadMelding() {
    override fun key(): String = sakId
    override fun value(): String = toJson()
    private fun toJson(): String {
        return """
            {
                "$sakIdKey":"$sakId",
                "$aktoerIdKey":"$aktoerId",
                "$soknadIdKey":"$soknadId",
                "$soknadKey":$soknad,
                "$gsakIdKey":"$gsakId",
                "$journalIdKey":"$journalId"
            }
    """.trimIndent()
    }

    companion object : Visitor<NySoknadMedJournalId> {
        override fun accept(json: String): Boolean {
            return NySoknadMedSkyggesak.accept(json)
                && !JSONObject(json).optString(journalIdKey).isNullOrEmpty()
        }

        override fun fromJson(json: String): NySoknadMedJournalId? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySoknadMedJournalId(
                    sakId = jsonObject.getString(sakIdKey),
                    aktoerId = jsonObject.getString(aktoerIdKey),
                    soknadId = jsonObject.getString(soknadIdKey),
                    soknad = jsonObject.getJSONObject(soknadKey).toString(),
                    gsakId = jsonObject.getString(gsakIdKey).toString(),
                    journalId = jsonObject.getString(journalIdKey).toString()
                )
            } else {
                null
            }
        }
    }
}

class UkjentFormat(private val key: String, private val json: String) : SoknadMelding() {
    override fun key() = key
    override fun value(): String = json
}

