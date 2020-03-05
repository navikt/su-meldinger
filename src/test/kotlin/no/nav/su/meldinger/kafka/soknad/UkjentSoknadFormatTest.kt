package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.consumerRecord
import no.nav.su.meldinger.kafka.soknad.SoknadMelding.Companion.fromConsumerRecord
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class UkjentSoknadFormatTest {

    private val ukjentFormat = UkjentFormat(key = "123",
            json = """
            {
                "sakId":"123",
                "aktoerId":"54321"
            }    
        """.trimIndent()
    )

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(ukjentFormat.value())
        }
    }

    @Test
    fun `should create from consumer record`() {
        val json = """{"sakId":"222","aktoerId":"54321"}"""
        val soknad = fromConsumerRecord(
                consumerRecord("123",
                        json
                )
        )

        when (soknad) {
            is UkjentFormat -> {
                assertEquals("123", soknad.key())
                assertEquals(json, soknad.value())
            }
        }
    }
}