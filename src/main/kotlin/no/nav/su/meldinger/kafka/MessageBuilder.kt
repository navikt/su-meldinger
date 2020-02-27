package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.soknad.NySoknad
import no.nav.su.meldinger.kafka.soknad.NySoknadHentGsak
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
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
                NySoknadHentGsak::class.java -> {
                    val jsonObject = JSONObject(record.value())
                    NySoknadHentGsak(
                            sakId = jsonObject.getString("sakId"),
                            aktoerId = jsonObject.getString("aktoerId"),
                            soknadId = jsonObject.getString("soknadId"),
                            soknad = jsonObject.getJSONObject("soknad").toString(),
                            gsakId = jsonObject.getString("gsakId")
                    ) as T
                }
                else -> throw RuntimeException("Could not build instance, class: $clazz is unknown to builder")
            }
        }

        inline fun <reified T : KafkaMessage> toProducerRecord(topic: String, instance: T): ProducerRecord<String, String> {
            return ProducerRecord(topic, instance.key(), instance.value())
        }

        inline fun <reified T : KafkaMessage> compatible(consumerRecord: ConsumerRecord<String, String>, clazz: Class<T>): Boolean {
            return messageResolver.compatible(consumerRecord, clazz)
        }
    }
}