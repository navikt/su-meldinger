package no.nav.su.meldinger.kafka.soknad

import org.json.JSONException
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class NySoknadTest {
    @Test
    fun `should resolve class from string when compatible`() {
        assertTrue(NySoknad.compatible(JSONObject("""
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad":{}
            }
        """.trimIndent())))
    }

    @Test
    fun `should not resolve when string is missing properties`() {
        assertFalse(NySoknad.compatible(JSONObject("""
            {
                "sakId":"123",
                "soknadId":"123"
            }
        """.trimIndent())))
    }

    @Test
    fun `should not resolve when class is missing properties`() {
        assertFalse(NySoknad.compatible(JSONObject("""
            {
                "sakId":"123",
                "soknadId":"123",
                "tjohei":"tjohei"
            }
        """.trimIndent())))
    }

    @Test
    fun `should throw exception when string is not json`() {
        assertThrows<JSONException> {
            NySoknad.compatible(JSONObject("""
                "bogus non json"
            """.trimIndent()))
        }
    }

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(NySoknad("sakId", "aktoerId", "soknadId", """{"key":"value"}""").value())
        }
    }

    @Test
    fun `should create from JSONObject`() {
        val nySoknad = NySoknad(JSONObject()
                .put("sakId", "123")
                .put("aktoerId", "54321")
                .put("soknadId", "123")
                .put("soknad", JSONObject("""{"key":"value"}"""))
        )
        assertEquals("123", nySoknad.sakId)
        assertEquals("123", nySoknad.soknadId)
        assertEquals("""{"key":"value"}""", nySoknad.soknad)
        assertEquals("54321", nySoknad.aktoerId)
    }
}