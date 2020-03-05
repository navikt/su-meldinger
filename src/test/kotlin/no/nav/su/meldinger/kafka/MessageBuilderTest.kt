package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.MessageBuilder.Companion.toProducerRecord
import no.nav.su.meldinger.kafka.soknad.NySoknad
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MessageBuilderTest {

    @Test
    fun `should create producer record with headers`() {
        val producerRecord1 = NySoknad("sakId", "aktoerId", "soknadId", "{}", "fnr")
                .toProducerRecord("TOPIC")
        assertEquals(0, producerRecord1.headers().count())

        val producerRecord2 = NySoknad("sakId", "aktoerId", "soknadId", "{}", "fnr")
                .toProducerRecord("TOPDIC", mapOf("X-Correlation-ID" to "abcdef"))
        assertEquals(1, producerRecord2.headers().count())
        assertEquals("abcdef", producerRecord2.headersAsString()["X-Correlation-ID"])
    }
}