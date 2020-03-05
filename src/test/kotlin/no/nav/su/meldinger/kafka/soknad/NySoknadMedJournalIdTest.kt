package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.consumerRecord
import no.nav.su.meldinger.kafka.soknad.NySoknadMedJournalId.Companion.fromJson
import no.nav.su.meldinger.kafka.soknad.SoknadMelding.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.soknadJson
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

internal class NySoknadMedJournalIdTest {

    private val nySoknadMedJournalId = NySoknadMedJournalId(
            sakId = "123", aktoerId = "1234567891011", soknadId = "222",
            soknad = soknadJson, fnr = "12345678910", gsakId = "333", journalId = "444"
    )

    @Test
    fun `should produce valid json`() {
        org.junit.jupiter.api.assertDoesNotThrow {
            JSONObject(nySoknadMedJournalId.value())
        }
    }

    @Test
    fun `should create from consumer record`() {
        val soknadFromRecord = fromConsumerRecord(
                consumerRecord("123", """
    {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"222",
                "soknad":$soknadJson,
                "gsakId":"333",
                "journalId":"444",
                "fnr":"12345678910"
            }    
        """.trimIndent()))

        when (soknadFromRecord) {
            is NySoknadMedJournalId -> {
                assertEquals("123", soknadFromRecord.sakId)
                assertEquals("54321", soknadFromRecord.aktoerId)
                assertEquals("222", soknadFromRecord.soknadId)
                assertEquals(parseString(soknadJson), parseString(soknadFromRecord.soknad))
                assertEquals("333", soknadFromRecord.gsakId)
                assertEquals("444", soknadFromRecord.journalId)
                assertEquals("12345678910", soknadFromRecord.fnr)
            }
            else -> fail()
        }
    }

    @Test
    fun `json serialization`() {
        assertEquals(
                parseString(nySoknadMedJournalId.value()),
                parseString(fromJson(nySoknadMedJournalId.value())?.value())
        )
    }

}