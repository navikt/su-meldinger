package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.consumerRecord
import no.nav.su.meldinger.kafka.soknad.NySøknadMedJournalId.Companion.fromJson
import no.nav.su.meldinger.kafka.soknad.SøknadMelding.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.søknadJson
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

internal class NySøknadMedJournalIdTest {

    private val nySøknadMedJournalId = NySøknadMedJournalId(
        correlationId = "correlate",
        sakId = "123",
        aktørId = "1234567891011",
        søknadId = "222",
        søknad = søknadJson,
        fnr = "12345678910",
        gsakId = "333",
        journalId = "444"
    )

    @Test
    fun `should produce valid json`() {
        org.junit.jupiter.api.assertDoesNotThrow {
            JSONObject(nySøknadMedJournalId.value())
        }
    }

    @Test
    fun `should create from consumer record`() {
        val correlationId = "correlationId"
        val søknadFromRecord = fromConsumerRecord(
                consumerRecord("123", """
    {
                "sakId":"123",
                "aktørId":"54321",
                "søknadId":"222",
                "søknad":$søknadJson,
                "gsakId":"333",
                "journalId":"444",
                "fnr":"12345678910"
            }    
        """.trimIndent(), correlationId))

        when (søknadFromRecord) {
            is NySøknadMedJournalId -> {
                assertEquals("123", søknadFromRecord.sakId)
                assertEquals("54321", søknadFromRecord.aktørId)
                assertEquals("222", søknadFromRecord.søknadId)
                assertEquals(parseString(søknadJson), parseString(søknadFromRecord.søknad))
                assertEquals("333", søknadFromRecord.gsakId)
                assertEquals("444", søknadFromRecord.journalId)
                assertEquals("12345678910", søknadFromRecord.fnr)
                assertEquals(correlationId, søknadFromRecord.correlationId)
            }
            else -> fail()
        }
    }

    @Test
    fun `json serialization`() {
        assertEquals(
                parseString(nySøknadMedJournalId.value()),
                parseString(fromJson(nySøknadMedJournalId.value(), mapOf("X-Correlation-ID" to "some correlation id"))?.value())
        )
    }

}