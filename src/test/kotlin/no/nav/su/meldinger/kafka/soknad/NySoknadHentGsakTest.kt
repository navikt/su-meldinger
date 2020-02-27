package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.MessageBuilder.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.MessageResolverTest.Companion.consumerRecord
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NySoknadHentGsakTest {
    @Test
    fun `should produce valid json`() {
        org.junit.jupiter.api.assertDoesNotThrow {
            JSONObject(NySoknadHentGsak("sakId", "aktoerId", "soknadId", """{"key":"value"}""", "gsakId").value())
        }
    }

    @Test
    fun `should create from builder`() {
        val nySoknadHentGsak = fromConsumerRecord(consumerRecord("123", """
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad":{"key":"value"},
                "gsakId":"111"
            }    
        """.trimIndent()), NySoknadHentGsak::class.java)
        assertEquals("123", nySoknadHentGsak.sakId)
        assertEquals("54321", nySoknadHentGsak.aktoerId)
        assertEquals("123", nySoknadHentGsak.soknadId)
        assertEquals("""{"key":"value"}""", nySoknadHentGsak.soknad)
        assertEquals("111", nySoknadHentGsak.gsakId)
    }
}