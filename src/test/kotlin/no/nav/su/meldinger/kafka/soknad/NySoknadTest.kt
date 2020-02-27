package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.MessageBuilder.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.MessageResolverTest.Companion.consumerRecord
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class NySoknadTest {
    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(NySoknad("sakId", "aktoerId", "soknadId", """{"key":"value"}""").value())
        }
    }

    @Test
    fun `should create from builder`() {
        val nySoknad = fromConsumerRecord(consumerRecord("123", """
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad":{"key":"value"}
            }    
        """.trimIndent()), NySoknad::class.java)
        assertEquals("123", nySoknad.sakId)
        assertEquals("54321", nySoknad.aktoerId)
        assertEquals("123", nySoknad.soknadId)
        assertEquals("""{"key":"value"}""", nySoknad.soknad)
    }
}