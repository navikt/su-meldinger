package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.consumerRecord
import no.nav.su.meldinger.kafka.headersAsString
import no.nav.su.meldinger.kafka.soknad.KafkaMessage.Companion.toProducerRecord
import no.nav.su.meldinger.kafka.soknad.NySoknad.Companion.fromJson
import no.nav.su.meldinger.kafka.soknad.SoknadMelding.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.soknadJson
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class NySoknadTest {

    private val nySoknad = NySoknad(
            sakId = "123", aktoerId = "1234567891011", soknadId = "222",
            soknad = soknadJson,
            fnr = "12345678910"
    )

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(nySoknad.value())
        }
    }

    @Test
    fun `should create from consumer record`() {
        val nySoknad = fromConsumerRecord(
                consumerRecord("123", """
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad": $soknadJson,
                "fnr":"12345678910"
            }    
        """.trimIndent()))

        when (nySoknad) {
            is NySoknad -> {
                assertEquals("123", nySoknad.sakId)
                assertEquals("54321", nySoknad.aktoerId)
                assertEquals("123", nySoknad.soknadId)
                assertEquals(parseString(soknadJson), parseString(nySoknad.soknad))
                assertEquals("12345678910", nySoknad.fnr)
            }
        }
    }

    @Test
    fun `should accept its own json`() {
        assertTrue(NySoknad.accept(nySoknad.value()))
    }

    @Test
    fun `json serialization`() {
        assertEquals(parseString(nySoknad.value()), parseString(fromJson(nySoknad.value())?.value()))
    }

    @Test
    fun `should add headers`() {
        val producerRecord1 = NySoknad("sakId", "aktoerId", "soknadId", "{}", "fnr")
                .toProducerRecord("TOPIC")
        assertEquals(0, producerRecord1.headers().count())

        val producerRecord2 = NySoknad("sakId", "aktoerId", "soknadId", "{}", "fnr")
                .toProducerRecord("TOPDIC", mapOf("X-Correlation-ID" to "abcdef"))
        assertEquals(1, producerRecord2.headers().count())
        assertEquals("abcdef", producerRecord2.headersAsString()["X-Correlation-ID"])
    }

}