package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.soknad.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Headers

class MessageBuilder {
    companion object {
        fun fromConsumerRecord(record: ConsumerRecord<String, String>): SoknadMelding =
                NySoknadMedJournalId.fromJson(record.value())
                        ?: NySoknadMedSkyggesak.fromJson(record.value())
                        ?: NySoknad.fromJson(record.value())
                        ?: UkjentFormat(record.key(), record.value())

        fun SoknadMelding.toProducerRecord(topic: String, headers: Map<String, String> = emptyMap()): ProducerRecord<String, String> =
                ProducerRecord(topic, this.key(), this.value()).also { producerRecord ->
                    headers.forEach { producerRecord.headers().add(it.key, it.value.toByteArray()) }
                }
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