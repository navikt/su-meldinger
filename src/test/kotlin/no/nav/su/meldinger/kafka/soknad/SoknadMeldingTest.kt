package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.soknadJson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SoknadMeldingTest {

    private val nySoknad = NySoknad(
            sakId = "123",
            aktoerId = "1234567891011",
            soknadId = "222",
            soknad = soknadJson,
            fnr = "12345678910"
    )

    private val nySoknadMedSkyggesak = NySoknadMedSkyggesak(
            sakId = "123",
            aktoerId = "1234567891011",
            soknadId = "222",
            soknad = soknadJson,
            fnr = "12345678910",
            gsakId = "333"
    )

    private val nySoknadMedJournalId = NySoknadMedJournalId(
            sakId = "123",
            aktoerId = "1234567891011",
            soknadId = "222",
            soknad = soknadJson,
            fnr = "12345678910",
            gsakId = "333",
            journalId = "444"
    )

    @Test
    fun equals() {
        assertEquals(nySoknad, NySoknad(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910"
        ))
        assertEquals(nySoknad, NySoknad(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = soknadJson.replace("\n", "").replace("\t", ""),
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "111",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "123",
                aktoerId = "1112312412515",
                soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "111",
                soknad = soknadJson,
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = """{"key":"value"}""",
                fnr = "12345678910"
        ))
        assertNotEquals(nySoknad, NySoknad(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = soknadJson,
                fnr = "01987654321"
        ))
        assertNotEquals(nySoknad, null)

        assertEquals(nySoknadMedSkyggesak, NySoknadMedSkyggesak(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910",
                gsakId = "333"
        ))

        assertNotEquals(nySoknadMedSkyggesak, NySoknadMedSkyggesak(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = soknadJson,
                fnr = "01987654321",
                gsakId = "333"
        ))
        assertNotEquals(nySoknadMedSkyggesak, NySoknadMedSkyggesak(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910",
                gsakId = "444"
        ))

        assertNotEquals(nySoknadMedSkyggesak, null)

        assertEquals(nySoknadMedJournalId, NySoknadMedJournalId(
                sakId = "123",
                aktoerId = "1234567891011",
                soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910",
                gsakId = "333",
                journalId = "444"
        ))

        assertNotEquals(nySoknadMedJournalId, null)

        assertNotEquals(nySoknad, nySoknadMedSkyggesak)
        assertNotEquals(nySoknad, nySoknadMedJournalId)
        assertNotEquals(nySoknadMedJournalId, nySoknadMedSkyggesak)
    }

    @Test
    fun hashcode() {
        val message1 = NySoknad(
                sakId = "123", aktoerId = "1234567891011", soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910")
        val message2 = NySoknad(
                sakId = "123", aktoerId = "1234567891011", soknadId = "111",
                soknad = soknadJson,
                fnr = "12345678910")
        assertNotEquals(message1.hashCode(), message2.hashCode())
        assertEquals(message1.hashCode(), message1.hashCode())
        assertTrue(hashSetOf(message1).contains(message1))
        assertFalse(hashSetOf(message1).contains(message2))

        val message3 = NySoknadMedSkyggesak(
                sakId = "123", aktoerId = "1234567891011", soknadId = "222",
                soknad = soknadJson,
                fnr = "12345678910",
                gsakId = "333"
        )
        val message4 = NySoknadMedSkyggesak(
                sakId = "123", aktoerId = "1234567891011", soknadId = "111",
                soknad = soknadJson,
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