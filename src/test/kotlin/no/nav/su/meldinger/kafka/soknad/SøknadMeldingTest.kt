package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.søknadJson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SøknadMeldingTest {

    private val nySøknad = NySøknad(
            correlationId = "5",
            sakId = "123",
            aktørId = "1234567891011",
            søknadId = "222",
            søknad = søknadJson,
            fnr = "12345678910"
    )
    private val nySøknadMedJournalId = nySøknad.medJournalId("444")

    @Test
    fun equals() {
        assertEquals(nySøknad, NySøknad(
                correlationId = "5",
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910"
        ))
        assertEquals(nySøknad, NySøknad(
                correlationId = "5",
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson.replace("\n", "").replace("\t", ""),
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                correlationId = "5",
                sakId = "111",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                correlationId = "5",
                sakId = "123",
                aktørId = "1112312412515",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                correlationId = "5",
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "111",
                søknad = søknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                correlationId = "5",
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = """{"key":"value"}""",
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                correlationId = "5",
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "01987654321"
        ))
        assertNotEquals(nySøknad, null)

        assertEquals(nySøknadMedJournalId, NySøknadMedJournalId(
                correlationId = "5",
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910",
                journalId = "444"
        ))

        assertNotEquals(nySøknadMedJournalId, null)

        assertNotEquals(nySøknad, nySøknadMedJournalId)
    }

    @Test
    fun hashcode() {
        val message1 = NySøknad(
                correlationId = "5", sakId = "123", aktørId = "1234567891011", søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910")
        val message2 = NySøknad(
                correlationId = "5", sakId = "123", aktørId = "1234567891011", søknadId = "111",
                søknad = søknadJson,
                fnr = "12345678910")
        assertNotEquals(message1.hashCode(), message2.hashCode())
        assertEquals(message1.hashCode(), message1.hashCode())
        assertTrue(hashSetOf(message1).contains(message1))
        assertFalse(hashSetOf(message1).contains(message2))

        val message3 = NySøknadMedJournalId(
                correlationId = "5", sakId = "123", aktørId = "1234567891011", søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910",
                journalId = "jid"
        )
        val message4 = NySøknadMedJournalId(
                correlationId = "5", sakId = "123", aktørId = "1234567891011", søknadId = "111",
                søknad = søknadJson,
                fnr = "12345678910",
                journalId = "jid"
        )
        assertNotEquals(message3.hashCode(), message4.hashCode())
        assertEquals(message3.hashCode(), message3.hashCode())
        assertTrue(hashSetOf(message3).contains(message3))
        assertFalse(hashSetOf(message3).contains(message4))
    }

    @Test
    fun `test toString`() {
        val ukjent = UkjentFormat("key", "value", mapOf("X-Correlation-ID" to "hei"))
        assertEquals("class: UkjentFormat, key: key, value: value", ukjent.toString())
    }
}