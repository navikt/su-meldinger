package no.nav.su.meldinger.kafka.soknad

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.json.JSONObject

interface KafkaMessage {
    fun key(): String
    fun value(): String

    companion object {
        fun KafkaMessage.toProducerRecord(topic: String, headers: Map<String, String> = emptyMap()): ProducerRecord<String, String> =
                ProducerRecord(topic, key(), value()).also { record ->
                    headers.forEach { record.headers().add(it.key, it.value.toByteArray()) }
                }
    }
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
        internal const val fnrKey = "fnr"
        fun accept(json: String, requiredFields: List<String>, forbiddenFields: List<String>): Boolean {
            val jsonObject = JSONObject(json)
            val hasRequiredFields = requiredFields.map { !jsonObject.optString(it).isNullOrEmpty() }.all { it }
            val hasForbiddenFields = forbiddenFields.map { !jsonObject.optString(it).isNullOrEmpty() }.any { it }
            return hasRequiredFields && !hasForbiddenFields
        }

        fun fromConsumerRecord(record: ConsumerRecord<String, String>): SoknadMelding =
                NySoknadMedJournalId.fromJson(record.value())
                        ?: NySoknadMedSkyggesak.fromJson(record.value())
                        ?: NySoknad.fromJson(record.value())
                        ?: UkjentFormat(record.key(), record.value())
    }

    override fun toString(): String = "class: ${this::class.java.simpleName}, key: ${key()}, value: ${value()}"
    override fun equals(other: Any?): Boolean = other is SoknadMelding && this::class == other::class && JSONObject(value()).similar(JSONObject(other.value()))
    override fun hashCode(): Int = key().hashCode() + 31 * value().hashCode()
}

class NySoknad(
        val sakId: String,
        val aktoerId: String,
        val soknadId: String,
        val soknad: String,
        val fnr: String
) : SoknadMelding() {
    override fun key() = sakId
    override fun value() = toJson()
    private fun toJson(): String {
        return """
            {
                "$sakIdKey":"$sakId",
                "$aktoerIdKey":"$aktoerId",
                "$soknadIdKey":"$soknadId",
                "$soknadKey":$soknad,
                "$fnrKey":"$fnr"
            }
        """.trimIndent()
    }

    companion object : Visitor<NySoknad> {
        val requiredFields = listOf(sakIdKey, aktoerIdKey, soknadIdKey, soknadKey, fnrKey)
        val forbiddenFields = listOf(gsakIdKey)
        override fun accept(json: String): Boolean = accept(json, requiredFields, forbiddenFields)

        override fun fromJson(json: String): NySoknad? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySoknad(
                        sakId = jsonObject.getString(sakIdKey),
                        aktoerId = jsonObject.getString(aktoerIdKey),
                        soknadId = jsonObject.getString(soknadIdKey),
                        soknad = jsonObject.getJSONObject(soknadKey).toString(),
                        fnr = jsonObject.getString(fnrKey)
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
        val fnr: String,
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
                "$fnrKey":"$fnr",
                "$gsakIdKey":"$gsakId"
            }
        """.trimIndent()
    }

    companion object : Visitor<NySoknadMedSkyggesak> {
        val requiredFields = NySoknad.requiredFields + gsakIdKey
        val forbiddenFields = listOf(journalIdKey)
        override fun accept(json: String): Boolean = accept(json, requiredFields, forbiddenFields)

        override fun fromJson(json: String): NySoknadMedSkyggesak? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySoknadMedSkyggesak(
                        sakId = jsonObject.getString(sakIdKey),
                        aktoerId = jsonObject.getString(aktoerIdKey),
                        soknadId = jsonObject.getString(soknadIdKey),
                        soknad = jsonObject.getJSONObject(soknadKey).toString(),
                        fnr = jsonObject.getString(fnrKey),
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
        val fnr: String,
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
                "$fnrKey":"$fnr",
                "$gsakIdKey":"$gsakId",
                "$journalIdKey":"$journalId"
            }
    """.trimIndent()
    }

    companion object : Visitor<NySoknadMedJournalId> {
        val requiredFields = NySoknadMedSkyggesak.requiredFields + journalIdKey
        val forbiddenFields = emptyList<String>()
        override fun accept(json: String): Boolean = accept(json, requiredFields, forbiddenFields)

        override fun fromJson(json: String): NySoknadMedJournalId? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySoknadMedJournalId(
                        sakId = jsonObject.getString(sakIdKey),
                        aktoerId = jsonObject.getString(aktoerIdKey),
                        soknadId = jsonObject.getString(soknadIdKey),
                        soknad = jsonObject.getJSONObject(soknadKey).toString(),
                        fnr = jsonObject.getString(fnrKey),
                        gsakId = jsonObject.getString(gsakIdKey).toString(),
                        journalId = jsonObject.getString(journalIdKey).toString()
                )
            } else {
                null
            }
        }
    }
}

class UkjentFormat(private val key: String?, private val json: String?) : SoknadMelding() {
    override fun key(): String = key ?: "mangler nøkkel"
    override fun value(): String = json ?: "mangler innhold"
}

