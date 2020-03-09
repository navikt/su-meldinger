package no.nav.su.meldinger.kafka.soknad

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ChainingTest {
    @Test
    fun `skal være mulig å bygge og sammenlikne en rekkefølge av meldinger`() {
        val original = NySøknad("correlationId","sakId", "aktørId", "søknadId", """{"json":"objekt}""", "fnr")
        val medSkyggesak = original.medSkyggesak("skyggesakId")
        assertTrue(medSkyggesak.følger(original))
        val medJournalId = medSkyggesak.medJournalId("journalId")
        assertTrue(medJournalId.følger(medSkyggesak))
    }
}