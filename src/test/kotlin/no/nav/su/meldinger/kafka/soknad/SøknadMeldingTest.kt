package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.søknadJson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SøknadMeldingTest {

    private val nySøknad = NySøknad(
            sakId = "123",
            aktørId = "1234567891011",
            søknadId = "222",
            søknad = søknadJson,
            fnr = "12345678910"
    )

    private val nySøknadMedSkyggesak = NySøknadMedSkyggesak(
            sakId = "123",
            aktørId = "1234567891011",
            søknadId = "222",
            søknad = søknadJson,
            fnr = "12345678910",
            gsakId = "333"
    )

    private val nySøknadMedJournalId = NySøknadMedJournalId(
            sakId = "123",
            aktørId = "1234567891011",
            søknadId = "222",
            søknad = søknadJson,
            fnr = "12345678910",
            gsakId = "333",
            journalId = "444"
    )

    @Test
    fun equals() {
        assertEquals(nySøknad, NySøknad(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910"
        ))
        assertEquals(nySøknad, NySøknad(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson.replace("\n", "").replace("\t", ""),
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                sakId = "111",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                sakId = "123",
                aktørId = "1112312412515",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "111",
                søknad = søknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = """{"key":"value"}""",
                fnr = "12345678910"
        ))
        assertNotEquals(nySøknad, NySøknad(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "01987654321"
        ))
        assertNotEquals(nySøknad, null)

        assertEquals(nySøknadMedSkyggesak, NySøknadMedSkyggesak(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910",
                gsakId = "333"
        ))

        assertNotEquals(nySøknadMedSkyggesak, NySøknadMedSkyggesak(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "01987654321",
                gsakId = "333"
        ))
        assertNotEquals(nySøknadMedSkyggesak, NySøknadMedSkyggesak(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910",
                gsakId = "444"
        ))

        assertNotEquals(nySøknadMedSkyggesak, null)

        assertEquals(nySøknadMedJournalId, NySøknadMedJournalId(
                sakId = "123",
                aktørId = "1234567891011",
                søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910",
                gsakId = "333",
                journalId = "444"
        ))

        assertNotEquals(nySøknadMedJournalId, null)

        assertNotEquals(nySøknad, nySøknadMedSkyggesak)
        assertNotEquals(nySøknad, nySøknadMedJournalId)
        assertNotEquals(nySøknadMedJournalId, nySøknadMedSkyggesak)
    }

    @Test
    fun hashcode() {
        val message1 = NySøknad(
                sakId = "123", aktørId = "1234567891011", søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910")
        val message2 = NySøknad(
                sakId = "123", aktørId = "1234567891011", søknadId = "111",
                søknad = søknadJson,
                fnr = "12345678910")
        assertNotEquals(message1.hashCode(), message2.hashCode())
        assertEquals(message1.hashCode(), message1.hashCode())
        assertTrue(hashSetOf(message1).contains(message1))
        assertFalse(hashSetOf(message1).contains(message2))

        val message3 = NySøknadMedSkyggesak(
                sakId = "123", aktørId = "1234567891011", søknadId = "222",
                søknad = søknadJson,
                fnr = "12345678910",
                gsakId = "333"
        )
        val message4 = NySøknadMedSkyggesak(
                sakId = "123", aktørId = "1234567891011", søknadId = "111",
                søknad = søknadJson,
                fnr = "12345678910",
                gsakId = "444"
        )
        assertNotEquals(message3.hashCode(), message4.hashCode())
        assertEquals(message3.hashCode(), message3.hashCode())
        assertTrue(hashSetOf(message3).contains(message3))
        assertFalse(hashSetOf(message3).contains(message4))
    }

    @Test
    fun `test toString`() {
        val ukjent = UkjentFormat("key", "value")
        assertEquals("class: UkjentFormat, key: key, value: value", ukjent.toString())
    }
}