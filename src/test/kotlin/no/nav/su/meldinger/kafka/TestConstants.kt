package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.soknad.SøknadMelding
import org.apache.kafka.clients.consumer.ConsumerRecord

fun consumerRecord(key: String, value: String, correlationId: String = "defaultCorrelation"): ConsumerRecord<String, String> {
    return ConsumerRecord("", 0, 0, key, value).apply {
        this.headers().add(SøknadMelding.correlationKey, correlationId.toByteArray())
    }
}

val søknadJson =
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