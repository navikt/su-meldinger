package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.MessageBuilder.Companion.compatible
import no.nav.su.meldinger.kafka.soknad.NySoknad
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.json.JSONException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MessageResolverTest {
    @Test
    fun `should resolve class from string when compatible`() {
        assertTrue(compatible(consumerRecord("123", """
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad":{}
            }
        """.trimIndent()), NySoknad::class.java))
    }

    @Test
    fun `should not resolve when string is missing properties`() {
        assertFalse(compatible(consumerRecord("123", """
            {
                "sakId":"123",
                "soknadId":"123"
            }
        """.trimIndent()), NySoknad::class.java))
    }

    @Test
    fun `should not resolve when class is missing properties`() {
        assertFalse(compatible(consumerRecord("123", """
            {
                "sakId":"123",
                "soknadId":"123",
                "tjohei":"tjohei"
            }
        """.trimIndent()), NySoknad::class.java))
    }

    @Test
    fun `should throw exception when string is not json`() {
        assertThrows<JSONException> {
            compatible(consumerRecord("123", """
                "bogus non json"
            """.trimIndent()), NySoknad::class.java)
        }
    }

    companion object {
        fun consumerRecord(key: String, value: String): ConsumerRecord<String, String> {
            return ConsumerRecord("", 0, 0, key, value)
        }
    }
}