package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.soknad.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Headers

class MessageBuilder {
    companion object {
        val messageResolver = MessageResolver()

        fun fromConsumerRecord(record: ConsumerRecord<String, String>): SoknadMelding =
            NySoknadMedJournalId.fromJson(record.value())
                ?: NySoknadMedSkyggesak.fromJson(record.value())
                ?: NySoknad.fromJson(record.value())
                ?: UkjentFormat(record.key(), record.value())

        fun SoknadMelding.toProducerRecord(topic: String, headers: Map<String, String> = emptyMap()): ProducerRecord<String, String> =
            ProducerRecord(topic, this.key(), this.value()).also { producerRecord ->
                headers.forEach { producerRecord.headers().add(it.key, it.value.toByteArray()) }
            }

        @Deprecated(message = "No more class casts, use sealed class SoknadMelding instead", replaceWith = ReplaceWith("fromConsumerRecord(record: ConsumerRecord<String, String>): SoknadMelding"))
        inline fun <reified T : KafkaMessage> fromConsumerRecord(record: ConsumerRecord<String, String>, clazz: Class<T>): T {
            return when (clazz) {
                NySoknad::class.java -> {
                    NySoknad.fromJson(record.value()) as T
                }
                NySoknadMedSkyggesak::class.java -> {
                    NySoknadMedSkyggesak.fromJson(record.value()) as T
                }
                NySoknadMedJournalId::class.java -> {
                    NySoknadMedJournalId.fromJson(record.value()) as T
                }
                else -> throw RuntimeException("Could not build instance, class: $clazz is unknown to builder")
            }
        }

        @Deprecated(message = "No more class casts, use sealed class SoknadMelding instead", replaceWith = ReplaceWith("SoknadMelding.toProducerRecord(topic: String, headers: Map<String, String> = emptyMap()): ProducerRecord<String, String>"))
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