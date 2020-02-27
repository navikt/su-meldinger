package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser
import com.google.gson.JsonParser.parseString
import no.nav.su.meldinger.kafka.MessageBuilder.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.MessageResolverTest.Companion.consumerRecord
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class NySoknadTest {

    @Test
    fun `should produce valid json`() {
        assertDoesNotThrow {
            JSONObject(NySoknad("sakId", "aktoerId", "soknadId", soknad = soknadJson).value())
        }
    }

    @Test
    fun `should create from builder`() {
        val nySoknad = fromConsumerRecord(consumerRecord("123", """
            {
                "sakId":"123",
                "aktoerId":"54321",
                "soknadId":"123",
                "soknad": $soknadJson
            }    
        """.trimIndent()), NySoknad::class.java)
        assertEquals("123", nySoknad.sakId)
        assertEquals("54321", nySoknad.aktoerId)
        assertEquals("123", nySoknad.soknadId)
        assertEquals(parseString(soknadJson), parseString(nySoknad.soknad))
    }

    @Test
    fun `json serialization`(){
        val nySoknad = NySoknad("sakId", "aktoerId", "soknadId", soknadJson)
        assertEquals(parseString(nySoknad.value()), parseString(NySoknad.fromJson(nySoknad.value()).value()))
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