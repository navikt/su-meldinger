package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.consumerRecord
import no.nav.su.meldinger.kafka.soknad.NySoknadMedSkyggesak.Companion.fromJson
import no.nav.su.meldinger.kafka.soknad.SoknadMelding.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.soknadJson
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

internal class NySoknadMedSkyggesakTest {

    private val nySoknadMedSkyggesak = NySoknadMedSkyggesak(
            sakId = "123", aktoerId = "1234567891011", soknadId = "222",
            soknad = soknadJson, fnr = "12345678910", gsakId = "333"
    )

    @Test
    fun `should produce valid json`() {
        org.junit.jupiter.api.assertDoesNotThrow {
            JSONObject(nySoknadMedSkyggesak.value())
        }
    }

    @Test
    fun `should create from consumer record`() {
        val nySoknadHentGsak = fromConsumerRecord(
                consumerRecord("123", """
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad":$soknadJson,
                "gsakId":"111",
                "fnr":"12345678910"
            }    
        """.trimIndent()))

        when (nySoknadHentGsak) {
            is NySoknadMedSkyggesak -> {
                assertEquals("123", nySoknadHentGsak.sakId)
                assertEquals("54321", nySoknadHentGsak.aktoerId)
                assertEquals("123", nySoknadHentGsak.soknadId)
                assertEquals(parseString(soknadJson), parseString(nySoknadHentGsak.soknad))
                assertEquals("111", nySoknadHentGsak.gsakId)
            }
            else -> fail("${nySoknadHentGsak::class}")
        }
    }

    @Test
    fun `json serialization`() {
        assertEquals(
                parseString(nySoknadMedSkyggesak.value()),
                parseString(fromJson(nySoknadMedSkyggesak.value())?.value())
        )
    }

}