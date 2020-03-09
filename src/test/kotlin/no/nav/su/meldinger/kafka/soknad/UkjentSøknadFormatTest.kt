package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.consumerRecord
import no.nav.su.meldinger.kafka.soknad.SøknadMelding.Companion.fromConsumerRecord
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class UkjentSøknadFormatTest {

    private val ukjentFormat = UkjentFormat(key = "123",
            json = """
            {
                "sakId":"123",
                "aktørId":"54321"
            }    
        """.trimIndent(),
        headers = mapOf("X-Correlation-ID" to "noe greier")
    )

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(ukjentFormat.value())
        }
    }

    @Test
    fun `should create from consumer record`() {
        val json = """{"sakId":"222","aktørId":"54321"}"""
        val søknad = fromConsumerRecord(consumerRecord("123", json))

        when (søknad) {
            is UkjentFormat -> {
                assertEquals("123", søknad.key())
                assertEquals(json, søknad.value())
            }
        }
    }
}