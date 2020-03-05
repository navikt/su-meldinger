package no.nav.su.meldinger.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Headers

fun ConsumerRecord<String, String>.headersAsString(): Map<String, String> = getHeadersAsString(this.headers())
fun ProducerRecord<String, String>.headersAsString(): Map<String, String> = getHeadersAsString(this.headers())

private fun getHeadersAsString(headers: Headers): MutableMap<String, String> {
    val map = mutableMapOf<String, String>()
    headers.forEach {
        map[it.key()] = String(it.value())
    }
    return map
}