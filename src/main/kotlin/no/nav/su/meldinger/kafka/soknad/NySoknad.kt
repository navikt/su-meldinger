package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.KafkaMessage
import no.nav.su.meldinger.kafka.MessageResolver.Companion.compatible
import no.nav.su.meldinger.kafka.Topics.SOKNAD_TOPIC
import org.apache.kafka.clients.producer.ProducerRecord


data class NySoknad(
        val sakId: String,
        val soknadId: String,
        val soknad: String
) : KafkaMessage {

    override fun key() = sakId
    override fun value() = toJson()

    private fun toJson(): String {
        return """
            {
                "sakId":"$sakId",
                "soknadId":"$soknadId",
                "soknad":$soknad
            }
        """.trimIndent()
    }

    fun toRecord(topic: String = SOKNAD_TOPIC): ProducerRecord<String, String> {
        return ProducerRecord(topic, key(), value())
    }

    companion object {
        infix fun compatible(string: String): Boolean {
            return compatible(NySoknad::class.java, string)
        }
    }
}

