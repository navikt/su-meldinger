package no.nav.su.meldinger.kafka.soknad

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.json.JSONObject

interface KafkaMessage {
    fun key(): String
    fun value(): String
    fun toProducerRecord(topic: String, headers: Map<String, String> = emptyMap()): ProducerRecord<String, String> =
        ProducerRecord(topic, key(), value()).also { record ->
            headers.forEach { record.headers().add(it.key, it.value.toByteArray()) }
        }
}

interface Visitor<T> {
    fun accept(json: String): Boolean
    fun fromJson(json: String): T?
}

sealed class SøknadMelding : KafkaMessage {
    companion object {
        internal const val sakIdKey = "sakId"
        internal const val aktørIdKey = "aktørId"
        internal const val søknadIdKey = "søknadId"
        internal const val søknadKey = "søknad"
        internal const val gsakIdKey = "gsakId"
        internal const val journalIdKey = "journalId"
        internal const val fnrKey = "fnr"
        fun accept(json: String, requiredFields: List<String>, forbiddenFields: List<String>): Boolean {
            val jsonObject = JSONObject(json)
            val hasRequiredFields = requiredFields.map { !jsonObject.optString(it).isNullOrEmpty() }.all { it }
            val hasForbiddenFields = forbiddenFields.map { !jsonObject.optString(it).isNullOrEmpty() }.any { it }
            return hasRequiredFields && !hasForbiddenFields
        }

        fun fromConsumerRecord(record: ConsumerRecord<String, String>): SøknadMelding =
                NySøknadMedJournalId.fromJson(record.value())
                        ?: NySøknadMedSkyggesak.fromJson(record.value())
                        ?: NySøknad.fromJson(record.value())
                        ?: UkjentFormat(record.key(), record.value())
    }

    override fun toString(): String = "class: ${this::class.java.simpleName}, key: ${key()}, value: ${value()}"
    override fun equals(other: Any?): Boolean = other is SøknadMelding && this::class == other::class && JSONObject(value()).similar(JSONObject(other.value()))
    override fun hashCode(): Int = key().hashCode() + 31 * value().hashCode()
}

class NySøknad(
    val sakId: String,
    val aktørId: String,
    val søknadId: String,
    val søknad: String,
    val fnr: String
) : SøknadMelding() {
    override fun key() = sakId
    override fun value() = toJson()
    private fun toJson(): String {
        return """
            {
                "$sakIdKey":"$sakId",
                "$aktørIdKey":"$aktørId",
                "$søknadIdKey":"$søknadId",
                "$søknadKey":$søknad,
                "$fnrKey":"$fnr"
            }
        """.trimIndent()
    }

    fun medSkyggesak(gsakId: String) = NySøknadMedSkyggesak(sakId = sakId, aktørId = aktørId, søknadId = søknadId, søknad = søknad, fnr = fnr, gsakId = gsakId)

    companion object : Visitor<NySøknad> {
        val requiredFields = listOf(sakIdKey, aktørIdKey, søknadIdKey, søknadKey, fnrKey)
        val forbiddenFields = listOf(gsakIdKey)
        override fun accept(json: String): Boolean = accept(json, requiredFields, forbiddenFields)

        override fun fromJson(json: String): NySøknad? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySøknad(
                        sakId = jsonObject.getString(sakIdKey),
                        aktørId = jsonObject.getString(aktørIdKey),
                        søknadId = jsonObject.getString(søknadIdKey),
                        søknad = jsonObject.getJSONObject(søknadKey).toString(),
                        fnr = jsonObject.getString(fnrKey)
                )
            } else {
                null
            }

        }
    }
}

class NySøknadMedSkyggesak(
    val sakId: String,
    val aktørId: String,
    val søknadId: String,
    val søknad: String,
    val fnr: String,
    val gsakId: String
) : SøknadMelding() {
    override fun key(): String = sakId
    override fun value(): String = toJson()
    private fun toJson(): String {
        return """
            {
                "$sakIdKey":"$sakId",
                "$aktørIdKey":"$aktørId",
                "$søknadIdKey":"$søknadId",
                "$søknadKey":$søknad,
                "$fnrKey":"$fnr",
                "$gsakIdKey":"$gsakId"
            }
        """.trimIndent()
    }

    fun medJournalId(journalId: String) = NySøknadMedJournalId(sakId = sakId, aktørId = aktørId, søknadId = søknadId, søknad = søknad, fnr = fnr, gsakId = gsakId, journalId = journalId)

    companion object : Visitor<NySøknadMedSkyggesak> {
        val requiredFields = NySøknad.requiredFields + gsakIdKey
        val forbiddenFields = listOf(journalIdKey)
        override fun accept(json: String): Boolean = accept(json, requiredFields, forbiddenFields)

        override fun fromJson(json: String): NySøknadMedSkyggesak? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySøknadMedSkyggesak(
                        sakId = jsonObject.getString(sakIdKey),
                        aktørId = jsonObject.getString(aktørIdKey),
                        søknadId = jsonObject.getString(søknadIdKey),
                        søknad = jsonObject.getJSONObject(søknadKey).toString(),
                        fnr = jsonObject.getString(fnrKey),
                        gsakId = jsonObject.getString(gsakIdKey).toString()
                )
            } else {
                null
            }
        }
    }
}

class NySøknadMedJournalId(
    val sakId: String,
    val aktørId: String,
    val søknadId: String,
    val søknad: String,
    val fnr: String,
    val gsakId: String,
    val journalId: String
) : SøknadMelding() {
    override fun key(): String = sakId
    override fun value(): String = toJson()
    private fun toJson(): String {
        return """
            {
                "$sakIdKey":"$sakId",
                "$aktørIdKey":"$aktørId",
                "$søknadIdKey":"$søknadId",
                "$søknadKey":$søknad,
                "$fnrKey":"$fnr",
                "$gsakIdKey":"$gsakId",
                "$journalIdKey":"$journalId"
            }
    """.trimIndent()
    }

    companion object : Visitor<NySøknadMedJournalId> {
        val requiredFields = NySøknadMedSkyggesak.requiredFields + journalIdKey
        val forbiddenFields = emptyList<String>()
        override fun accept(json: String): Boolean = accept(json, requiredFields, forbiddenFields)

        override fun fromJson(json: String): NySøknadMedJournalId? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySøknadMedJournalId(
                        sakId = jsonObject.getString(sakIdKey),
                        aktørId = jsonObject.getString(aktørIdKey),
                        søknadId = jsonObject.getString(søknadIdKey),
                        søknad = jsonObject.getJSONObject(søknadKey).toString(),
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

class UkjentFormat(private val key: String?, private val json: String?) : SøknadMelding() {
    override fun key(): String = key ?: "mangler nøkkel"
    override fun value(): String = json ?: "mangler innhold"
}

