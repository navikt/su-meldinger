package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.MessageBuilder.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.MessageBuilder.Companion.toProducerRecord
import no.nav.su.meldinger.kafka.MessageResolverTest.Companion.consumerRecord
import no.nav.su.meldinger.kafka.soknad.KafkaMessage
import no.nav.su.meldinger.kafka.soknad.NySoknad
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MessageBuilderTest {
    @Test
    fun `should throw exception for unknown class`() {
        assertThrows<RuntimeException> {
            fromConsumerRecord(consumerRecord("key", "value"), Unknown::class.java)
        }
    }

    @Test
    fun `should create producer record with headers`() {
        val producerRecord1 = toProducerRecord(
                "TOPIC",
                NySoknad("sakId", "aktoerId", "soknadId", "{}"))
        assertEquals(0, producerRecord1.headers().count())

        val producerRecord2 = toProducerRecord(
                "TOPIC",
                NySoknad("sakId", "aktoerId", "soknadId", "{}"),
                mapOf("X-Correlation-ID" to "abcdef"))
        assertEquals(1, producerRecord2.headers().count())
        assertEquals("abcdef", producerRecord2.headersAsString()["X-Correlation-ID"])
    }

    class Unknown : KafkaMessage {
        override fun key(): String = "key"
        override fun value(): String = "value"
    }
}