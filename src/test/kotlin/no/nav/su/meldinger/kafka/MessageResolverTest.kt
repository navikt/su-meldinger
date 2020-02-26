package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.MessageResolver.Companion.compatible
import no.nav.su.meldinger.kafka.soknad.NySoknad
import org.json.JSONException
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MessageResolverTest {
    @Test
    fun `should resolve class from string when compatible`() {
        assertTrue(compatible(NySoknad::class.java, JSONObject("""
            {
                "sakId":"123",
                "soknadId":"123",
                "soknad":{}
            }
        """.trimIndent())))
    }

    @Test
    fun `should not resolve when string is missing properties`() {
        assertFalse(compatible(NySoknad::class.java, JSONObject("""
            {
                "sakId":"123",
                "soknadId":"123"
            }
        """.trimIndent())))
    }

    @Test
    fun `should not resolve when class is missing properties`() {
        assertFalse(compatible(NySoknad::class.java, JSONObject("""
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
            compatible(NySoknad::class.java, JSONObject("""
                "bogus non json"
            """.trimIndent()))
        }
    }
}