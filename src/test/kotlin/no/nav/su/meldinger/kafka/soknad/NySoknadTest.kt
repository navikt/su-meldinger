package no.nav.su.meldinger.kafka.soknad

import org.json.JSONException
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class NySoknadTest {
    @Test
    fun `should resolve class from string when compatible`() {
        assertTrue(NySoknad.compatible("""
            {
                "sakId":"123",
                "soknadId":"123",
                "soknad":{}
            }
        """.trimIndent()))
    }

    @Test
    fun `should not resolve when string is missing properties`() {
        assertFalse(NySoknad.compatible("""
            {
                "sakId":"123",
                "soknadId":"123"
            }
        """.trimIndent()))
    }

    @Test
    fun `should not resolve when class is missing properties`() {
        assertFalse(NySoknad.compatible("""
            {
                "sakId":"123",
                "soknadId":"123",
                "tjohei":"tjohei"
            }
        """.trimIndent()))
    }

    @Test
    fun `should throw exception when string is not json`() {
        assertThrows<JSONException> {
            NySoknad.compatible("""
                "bogus non json"
            """.trimIndent())
        }
    }

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(NySoknad("sakId", "soknadId", """{"key":"value"}""").value())
        }
    }
}