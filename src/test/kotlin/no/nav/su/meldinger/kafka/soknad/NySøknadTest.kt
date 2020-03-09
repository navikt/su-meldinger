package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.consumerRecord
import no.nav.su.meldinger.kafka.headersAsString
import no.nav.su.meldinger.kafka.soknad.NySøknad.Companion.fromJson
import no.nav.su.meldinger.kafka.soknad.SøknadMelding.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.søknadJson
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class NySøknadTest {

    private val nySøknad = NySøknad(
        correlationId = "a correlate",
        sakId = "123",
        aktørId = "1234567891011",
        søknadId = "222",
        søknad = søknadJson,
        fnr = "12345678910"
    )

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(nySøknad.value())
        }
    }

    @Test
    fun `should create from consumer record`() {
        val nySøknad = fromConsumerRecord(
                consumerRecord("123", """
            {
                "sakId":"123",
                "aktørId":"54321",
                "søknadId":"123",
                "søknad": $søknadJson,
                "fnr":"12345678910"
            }    
        """.trimIndent()))

        when (nySøknad) {
            is NySøknad -> {
                assertEquals("123", nySøknad.sakId)
                assertEquals("54321", nySøknad.aktørId)
                assertEquals("123", nySøknad.søknadId)
                assertEquals(parseString(søknadJson), parseString(nySøknad.søknad))
                assertEquals("12345678910", nySøknad.fnr)
            }
        }
    }

    @Test
    fun `should accept its own json`() {
        assertTrue(NySøknad.accept(nySøknad.value()))
    }

    @Test
    fun `json serialization`() {
        assertEquals(parseString(nySøknad.value()), parseString(fromJson(nySøknad.value(), mapOf("X-Correlation-ID" to "1"))?.value()))
    }

    @Test
    fun `should add headers`() {
        val producerRecord1 = NySøknad("2", "sakId", "aktørId", "søknadId", "{}", "fnr")
                .toProducerRecord("TOPIC")
        assertEquals(0, producerRecord1.headers().count())

        val producerRecord2 = NySøknad("3", "sakId", "aktørId", "søknadId", "{}", "fnr")
                .toProducerRecord("TOPDIC", mapOf("X-Correlation-ID" to "abcdef"))
        assertEquals(1, producerRecord2.headers().count())
        assertEquals("abcdef", producerRecord2.headersAsString()["X-Correlation-ID"])
    }

}