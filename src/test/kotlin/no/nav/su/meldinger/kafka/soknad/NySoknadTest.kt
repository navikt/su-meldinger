package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.MessageBuilder.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.MessageResolverTest.Companion.consumerRecord
import no.nav.su.meldinger.kafka.soknad.NySoknad.Companion.fromJson
import no.nav.su.meldinger.kafka.soknadJson
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class NySoknadTest {

    val nySoknad = NySoknad(
        sakId = "123", aktoerId = "1234567891011", soknadId = "222",
        soknad = soknadJson
    )

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(nySoknad.value())
        }
    }

    @Test
    fun `should create from builder`() {
        val nySoknad = fromConsumerRecord(
            consumerRecord(
                "123", """
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad": $soknadJson
            }    
        """.trimIndent()
            )
        )

        when (nySoknad){
            is NySoknad -> {
                assertEquals("123", nySoknad.sakId)
                assertEquals("54321", nySoknad.aktoerId)
                assertEquals("123", nySoknad.soknadId)
                assertEquals(parseString(soknadJson), parseString(nySoknad.soknad))
            }
        }
    }

    @Test
    fun `json serialization`() {
        assertEquals(parseString(nySoknad.value()), parseString(fromJson(nySoknad.value())?.value()))
    }

}