package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser
import no.nav.su.meldinger.kafka.MessageBuilder.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.MessageResolverTest.Companion.consumerRecord
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NySoknadMedSkyggesakTest {
    @Test
    fun `should produce valid json`() {
        org.junit.jupiter.api.assertDoesNotThrow {
            JSONObject(NySoknadMedSkyggesak("sakId", "aktoerId", "soknadId", """{"key":"value"}""", "gsakId").value())
        }
    }

    @Test
    fun `should create from builder`() {
        val nySoknadHentGsak = fromConsumerRecord(consumerRecord("123", """
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad":{"key":"value"},
                "gsakId":"111"
            }    
        """.trimIndent()), NySoknadMedSkyggesak::class.java)
        assertEquals("123", nySoknadHentGsak.sakId)
        assertEquals("54321", nySoknadHentGsak.aktoerId)
        assertEquals("123", nySoknadHentGsak.soknadId)
        assertEquals("""{"key":"value"}""", nySoknadHentGsak.soknad)
        assertEquals("111", nySoknadHentGsak.gsakId)
    }

    @Test
    fun `json serialization`(){
        val nySoknadMedSkyggesak = NySoknadMedSkyggesak("sakId", "aktoerId", "soknadId", soknadJson, "111")
        assertEquals(
            JsonParser.parseString(nySoknadMedSkyggesak.value()),
            JsonParser.parseString(NySoknadMedSkyggesak.fromJson(nySoknadMedSkyggesak.value()).value())
        )
    }

    private val soknadJson =
        """
    {
      "personopplysninger": {
        "fnr": "12345678910",
        "fornavn": "fornavn",
        "mellomnavn": "ØÆÅ",
        "etternavn": "etternavn",
        "telefonnummer": "90011900",
        "gateadresse": "storgata 1",
        "bruksenhet": "20e",
        "postnummer": "0909",
        "poststed": "Oslo",
        "bokommune": "Oslo",
        "statsborgerskap": "Tunisisk",
        "flyktning": "true",
        "bofastnorge": "true"
      },
      "boforhold": {
        "borSammenMed": [
          "over18"
        ],
        "delerBoligMed": [
          {
            "navn": "Turid Schønberg",
            "fødselsnummer": "12312312312312"
          }
        ],
        "delerDuBolig": "true"
      },
      "utenlandsopphold": {
        "utenlandsoppholdArray": [
          {
            "utreisedato": "31122019",
            "innreisedato": "04012020"
          }
        ],
        "PlanlagtUtenlandsoppholdArray": [
          {
            "planlagtUtreisedato": "01032020",
            "planlagtInnreisedato": "05032020"
          },
          {
            "planlagtUtreisedato": "01042020",
            "planlagtInnreisedato": "05042020"
          }
        ],
        "utenlandsopphold": "true",
        "planlagtUtenlandsopphold": "true"
      },
      "oppholdstillatelse": {
        "varigopphold": "false",
        "oppholdstillatelseUtløpsdato": "30/10/2024",
        "soektforlengelse": "false"
      },
      "inntektPensjonFormue": {
        "pensjonsOrdning": [
          {
            "ordning": "KLP",
            "beløp": "99"
          },
          {
            "ordning": "SPK",
            "beløp": "98"
          }
        ],
        "kravannenytelse": "true",
        "kravannenytelseBegrunnelse": "Hundepensjon",
        "arbeidselleranneninntekt": "true",
        "arbeidselleranneninntektBegrunnelse": "2500",
        "hardupensjon": "true",
        "sumPersoninntekt": "30000",
        "harduformueeiendom": "true",
        "formueBeløp": "2323",
        "hardufinansformue": "false",
        "harduannenformueeiendom": "true",
        "typeFormue": "hytte i afrika",
        "samletSkattetakst": "3500",
        "sosialstonad": "true"
      },
      "forNAV": {
        "maalform": "nynorsk",
        "personligmote": "ja",
        "fullmektigmote": "ja",
        "passsjekk": "ja",
        "forNAVmerknader": "Trivelig type"
      }
    }
""".trimIndent()

}