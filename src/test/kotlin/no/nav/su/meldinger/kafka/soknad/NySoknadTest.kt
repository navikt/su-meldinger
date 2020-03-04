package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.MessageBuilder.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.MessageResolverTest.Companion.consumerRecord
import no.nav.su.meldinger.kafka.soknad.NySoknad.Companion.fromJson
import no.nav.su.meldinger.kafka.soknadJson
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class NySoknadTest {

    val nySoknad = NySoknad(
            sakId = "123", aktoerId = "1234567891011", soknadId = "222",
            soknad = soknadJson,
            fnr = "12345678910"
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
                "soknad": $soknadJson,
                "fnr":"12345678910"
            }    
        """.trimIndent()
                )
        )

        when (nySoknad) {
            is NySoknad -> {
                assertEquals("123", nySoknad.sakId)
                assertEquals("54321", nySoknad.aktoerId)
                assertEquals("123", nySoknad.soknadId)
                assertEquals(parseString(soknadJson), parseString(nySoknad.soknad))
                assertEquals("12345678910", nySoknad.fnr)
            }
        }
    }

    @Test
    fun `should accept its own json`(){
        assertTrue(NySoknad.accept(nySoknad.value()))
    }

    @Test
    fun `equals`() {
        assertEquals(nySoknad, NySoknad(
                sakId = "123", aktoerId = "1234567891011", soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910"
        ))
        assertEquals(nySoknad, NySoknad(
                sakId = "123", aktoerId = "1234567891011", soknadId = "222",
                soknad = soknadJson.replace("\n", "").replace("\t",""),
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "111", aktoerId = "1234567891011", soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910")
        )
        assertNotEquals(nySoknad, NySoknad(
                sakId = "123", aktoerId = "1112312412515", soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "123", aktoerId = "1234567891011", soknadId = "111",
                soknad = soknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "123", aktoerId = "1234567891011", soknadId = "222",
                soknad = """{"key":"value"}""",
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "123", aktoerId = "1234567891011", soknadId = "222",
                soknad = soknadJson,
                fnr = "01987654321"
        ))
        assertNotEquals(nySoknad, null)

    }

    @Test
    fun `json serialization`() {
        assertEquals(parseString(nySoknad.value()), parseString(fromJson(nySoknad.value())?.value()))
    }

}