package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.consumerRecord
import no.nav.su.meldinger.kafka.soknad.NySøknadMedSkyggesak.Companion.fromJson
import no.nav.su.meldinger.kafka.soknad.SøknadMelding.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.søknadJson
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class NySøknadMedSkyggesakTest {

    private val nySøknadMedSkyggesak = NySøknadMedSkyggesak(
            sakId = "123", aktørId = "1234567891011", søknadId = "222",
            søknad = søknadJson, fnr = "12345678910", gsakId = "333"
    )

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(nySøknadMedSkyggesak.value())
        }
    }

    @Test
    fun `should create from consumer record`() {
        val nySøknadHentGsak = fromConsumerRecord(
                consumerRecord("123", """
            {
                "sakId":"123",
                "aktørId":"54321",
                "søknadId":"123",
                "søknad":$søknadJson,
                "gsakId":"111",
                "fnr":"12345678910"
            }    
        """.trimIndent()))

        when (nySøknadHentGsak) {
            is NySøknadMedSkyggesak -> {
                assertEquals("123", nySøknadHentGsak.sakId)
                assertEquals("54321", nySøknadHentGsak.aktørId)
                assertEquals("123", nySøknadHentGsak.søknadId)
                assertEquals(parseString(søknadJson), parseString(nySøknadHentGsak.søknad))
                assertEquals("111", nySøknadHentGsak.gsakId)
            }
            else -> fail("${nySøknadHentGsak::class}")
        }
    }

    @Test
    fun `json serialization`() {
        assertEquals(
                parseString(nySøknadMedSkyggesak.value()),
                parseString(fromJson(nySøknadMedSkyggesak.value())?.value())
        )
    }

}