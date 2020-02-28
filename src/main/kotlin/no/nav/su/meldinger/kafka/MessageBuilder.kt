package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.soknad.NySoknad
import no.nav.su.meldinger.kafka.soknad.NySoknadMedJournalId
import no.nav.su.meldinger.kafka.soknad.NySoknadMedSkyggesak
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Headers
import org.json.JSONObject

class MessageBuilder {
    companion object {
        val messageResolver = MessageResolver()
        inline fun <reified T : KafkaMessage> fromConsumerRecord(record: ConsumerRecord<String, String>, clazz: Class<T>): T {
            return when (clazz) {
                NySoknad::class.java -> {
                    val jsonObject = JSONObject(record.value())
                    NySoknad(
                            sakId = jsonObject.getString("sakId"),
                            aktoerId = jsonObject.getString("aktoerId"),
                            soknadId = jsonObject.getString("soknadId"),
                            soknad = jsonObject.getJSONObject("soknad").toString()
                    ) as T
                }
                NySoknadMedSkyggesak::class.java -> {
                    val jsonObject = JSONObject(record.value())
                    NySoknadMedSkyggesak(
                            sakId = jsonObject.getString("sakId"),
                            aktoerId = jsonObject.getString("aktoerId"),
                            soknadId = jsonObject.getString("soknadId"),
                            soknad = jsonObject.getJSONObject("soknad").toString(),
                            gsakId = jsonObject.getString("gsakId")
                    ) as T
                }
                NySoknadMedJournalId::class.java -> {
                    val jsonObject = JSONObject(record.value())
                    NySoknadMedJournalId(
                        sakId = jsonObject.getString("sakId"),
                        aktoerId = jsonObject.getString("aktoerId"),
                        soknadId = jsonObject.getString("soknadId"),
                        soknad = jsonObject.getJSONObject("soknad").toString(),
                        gsakId = jsonObject.getString("gsakId"),
                        journalId = jsonObject.getString("journalId")
                    ) as T
                }
                else -> throw RuntimeException("Could not build instance, class: $clazz is unknown to builder")
            }
        }

        inline fun <reified T : KafkaMessage> toProducerRecord(topic: String, instance: T, headers: Map<String, String> = emptyMap()): ProducerRecord<String, String> =
                ProducerRecord(topic, instance.key(), instance.value()).also { producerRecord ->
                    headers.forEach { producerRecord.headers().add(it.key, it.value.toByteArray()) }
                }

        inline fun <reified T : KafkaMessage> compatible(consumerRecord: ConsumerRecord<String, String>, clazz: Class<T>) =
                messageResolver.compatible(consumerRecord, clazz)
    }
}

fun ConsumerRecord<String, String>.headersAsString(): Map<String, String> = getHeadersAsString(this.headers())
fun ProducerRecord<String, String>.headersAsString(): Map<String, String> = getHeadersAsString(this.headers())

private fun getHeadersAsString(headers: Headers): MutableMap<String, String> {
    val map = mutableMapOf<String, String>()
    headers.forEach {
        map[it.key()] = String(it.value())
    }
    return map
}