package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.headersAsString
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.json.JSONObject

interface KafkaMessage {
    fun key(): String
    fun value(): String
}

interface Visitor<T> {
    fun accept(json: String): Boolean
    fun fromJson(json: String, headers: Map<String, String>): T?
}

sealed class SøknadMelding(val correlationId: String) : KafkaMessage {
    companion object {
        internal const val sakIdKey = "sakId"
        internal const val aktørIdKey = "aktørId"
        internal const val søknadIdKey = "søknadId"
        internal const val søknadKey = "søknad"
        internal const val journalIdKey = "journalId"
        internal const val fnrKey = "fnr"
        internal const val correlationKey = "X-Correlation-ID"
        fun accept(json: String, requiredFields: List<String>, forbiddenFields: List<String>): Boolean {
            val jsonObject = JSONObject(json)
            val hasRequiredFields = requiredFields.map { !jsonObject.optString(it).isNullOrEmpty() }.all { it }
            val hasForbiddenFields = forbiddenFields.map { !jsonObject.optString(it).isNullOrEmpty() }.any { it }
            return hasRequiredFields && !hasForbiddenFields
        }

        fun fromConsumerRecord(record: ConsumerRecord<String, String>): SøknadMelding =
                NySøknadMedJournalId.fromJson(record.value(), record.headersAsString())
                        ?: NySøknad.fromJson(record.value(), record.headersAsString())
                        ?: UkjentFormat(record.key(), record.value(), record.headersAsString())
    }

    fun toProducerRecord(topic: String): ProducerRecord<String, String> =
        ProducerRecord(topic, key(), value()).also { record ->
            record.headers().add(correlationKey, correlationId.toByteArray())
        }

    override fun toString(): String = "class: ${this::class.java.simpleName}, key: ${key()}, value: ${value()}"
    override fun equals(other: Any?): Boolean = other is SøknadMelding && this::class == other::class && JSONObject(value()).toString() == JSONObject(other.value()).toString()
    override fun hashCode(): Int = key().hashCode() + 31 * value().hashCode()
}

class NySøknad(
    correlationId: String,
    val sakId: String,
    val aktørId: String,
    val søknadId: String,
    val søknad: String,
    val fnr: String
) : SøknadMelding(correlationId) {
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

    fun medJournalId(journalId: String) = NySøknadMedJournalId(correlationId = correlationId, sakId = sakId, aktørId = aktørId, søknadId = søknadId, søknad = søknad, fnr = fnr, journalId = journalId)

    companion object : Visitor<NySøknad> {
        val requiredFields = listOf(sakIdKey, aktørIdKey, søknadIdKey, søknadKey, fnrKey)
        val forbiddenFields = listOf(journalIdKey)
        override fun accept(json: String): Boolean = accept(json, requiredFields, forbiddenFields)

        override fun fromJson(json: String, headers: Map<String, String>): NySøknad? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySøknad(
                        correlationId = headers[correlationKey]?: "correlation id missing",
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

class NySøknadMedJournalId(
    correlationId: String,
    val sakId: String,
    val aktørId: String,
    val søknadId: String,
    val søknad: String,
    val fnr: String,
    val journalId: String
) : SøknadMelding(correlationId) {
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
                "$journalIdKey":"$journalId"
            }
    """.trimIndent()
    }

    /** Er denne meldingen lik sin forgjenger? */
    fun følger(original: NySøknad): Boolean = this.correlationId == original.correlationId && this.sakId == original.sakId && this.aktørId == original.aktørId && this.søknadId == original.søknadId && this.søknad == original.søknad

    companion object : Visitor<NySøknadMedJournalId> {
        val requiredFields = NySøknad.requiredFields + journalIdKey
        val forbiddenFields = emptyList<String>()
        override fun accept(json: String): Boolean = accept(json, requiredFields, forbiddenFields)

        override fun fromJson(json: String, headers: Map<String, String>): NySøknadMedJournalId? {
            return if (accept(json)) {
                val jsonObject = JSONObject(json)
                NySøknadMedJournalId(
                        correlationId = headers[correlationKey]?: "correlation id missing",
                        sakId = jsonObject.getString(sakIdKey),
                        aktørId = jsonObject.getString(aktørIdKey),
                        søknadId = jsonObject.getString(søknadIdKey),
                        søknad = jsonObject.getJSONObject(søknadKey).toString(),
                        fnr = jsonObject.getString(fnrKey),
                        journalId = jsonObject.getString(journalIdKey).toString()
                )
            } else {
                null
            }
        }
    }
}

class UkjentFormat(private val key: String?, private val json: String?, headers: Map<String, String>) : SøknadMelding(headers[correlationKey]?: "correlation id missing") {
    override fun key(): String = key ?: "mangler nøkkel"
    override fun value(): String = json ?: "mangler innhold"
}

