package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SøknadInnholdTest {

    private val søknad = SøknadInnholdTestdataBuilder.build()

    val minimalPersonopplysninger = Personopplysninger(
            fnr = "12345678910",
            fornavn = "fornavn",
            etternavn = "etternavn",
            telefonnummer = "12345678",
            gateadresse = "gateadresse",
            postnummer = "0050",
            poststed = "Oslo",
            bokommune = "Oslo",
            statsborgerskap = "NOR"
    )

    @Test
    fun `should convert to and from json`() {
        val json = søknad.toJson()
        JSONObject(json)
        JsonParser.parseString(json) //Strict check of json syntax - JSONObject helps us along a bit too much.
        SøknadInnhold.fromJson(JSONObject(json)).shouldBe(søknad)
    }

    @Test
    fun `should put " " if appropriate around conditional values`() {
        val personopplysninger = søknad.personopplysninger.toJson()
        personopplysninger.shouldContain("\"Erik\"")
        personopplysninger.shouldContain("\"U1H20\"")

        val boforhold = søknad.boforhold.toJson()
        boforhold.shouldContain(""""ektemake-eller-samboer"""")

        val oppholdstillatelse = søknad.oppholdstillatelse.toJson()
        oppholdstillatelse.shouldContain(""""midlertidig"""")

        val utenlandsopphold = søknad.utenlandsopphold.toJson()
        utenlandsopphold.shouldNotContain("\"[\"")
        utenlandsopphold.shouldNotContain("\"]\"")

        val forNav = søknad.forNav.toJson()
        forNav.shouldContain(""""harFullmektigEllerVerge": "verge"""")

        søknad.formue.toJson()

        val inntektOgPensjon = søknad.inntektOgPensjon.toJson()
        inntektOgPensjon.shouldContain("\"sosialstønad\"")
        inntektOgPensjon.shouldNotContain("\"[\"")
        inntektOgPensjon.shouldNotContain("\"]\"")
    }

    @Test
    fun `should serialize and deserialize default values for SøknadInnhold`() {

        val expectedSøknadInnhold = SøknadInnhold(
                uførevedtak = Uførevedtak(harUførevedtak = true),
                personopplysninger = minimalPersonopplysninger,
                flyktningsstatus = Flyktningsstatus(registrertFlyktning = true),
                boforhold = Boforhold(borOgOppholderSegINorge = false, delerBolig = false),
                utenlandsopphold = Utenlandsopphold(),
                oppholdstillatelse = Oppholdstillatelse(erNorskStatsborger = true, statsborgerskapAndreLand = false),
                inntektOgPensjon = InntektOgPensjon(),
                formue = Formue(),
                forNav = ForNav()
        )
        val json = expectedSøknadInnhold.toJson()
        JsonParser.parseString(json) //JSONObject is apparently not strict enough
        SøknadInnhold.fromJson(JSONObject(json)).shouldBe(expectedSøknadInnhold)
    }

    @Test
    fun `should deserialize minimal søknadsinnsending`() {

        val expectedSøknadInnhold = SøknadInnhold(
                uførevedtak = Uførevedtak(harUførevedtak = true),
                personopplysninger = minimalPersonopplysninger.copy(
                        bruksenhet = "102"
                ),
                flyktningsstatus = Flyktningsstatus(registrertFlyktning = true),
                boforhold = Boforhold(borOgOppholderSegINorge = true, delerBolig = false),
                utenlandsopphold = Utenlandsopphold(emptyList(), emptyList()),
                oppholdstillatelse = Oppholdstillatelse(erNorskStatsborger = true, statsborgerskapAndreLand = false),
                inntektOgPensjon = InntektOgPensjon(pensjon = emptyList()),
                formue = Formue(),
                forNav = ForNav()
        )
        //language=JSON
        val json = """
            {
                "personopplysninger":{
                    "aktørid":"123",
                    "fnr":"12345678910",
                    "fornavn":"fornavn",
                    "mellomnavn":null,
                    "etternavn":"etternavn",
                    "telefonnummer":"12345678",
                    "gateadresse":"gateadresse",
                    "postnummer":"0050",
                    "poststed":"Oslo",
                    "bruksenhet":"102",
                    "bokommune":"Oslo",
                    "statsborgerskap":"NOR"
                },
                "uførevedtak":{
                    "harUførevedtak":true
                },
                "flyktningsstatus":{
                    "registrertFlyktning":true
                },
                "oppholdstillatelse":{
                    "erNorskStatsborger":true,
                    "harOppholdstillatelse":null,
                    "typeOppholdstillatelse":null,
                    "oppholdstillatelseMindreEnnTreMåneder":null,
                    "oppholdstillatelseForlengelse":null,
                    "statsborgerskapAndreLand":false,
                    "statsborgerskapAndreLandFritekst":null
                },
                "boforhold":{
                    "borOgOppholderSegINorge":true,
                    "delerBoligMedVoksne":false,
                    "delerBoligMed":null,
                    "ektemakeEllerSamboerUnder67År":null,
                    "ektemakeEllerSamboerUførFlyktning":null
                },
                "utenlandsopphold":{
                    "registrertePerioder":[],
                    "planlagtePerioder":[]
                },
                "inntektOgPensjon":{
                    "forventetInntekt":null,
                    "tjenerPengerIUtlandetBeløp":null,
                    "andreYtelserINav":null,
                    "andreYtelserINavBeløp":null,
                    "søktAndreYtelserIkkeBehandletBegrunnelse":null,
                    "sosialstønadBeløp":null,
                    "trygdeytelserIUtlandetBeløp":null,
                    "trygdeytelserIUtlandet":null,
                    "trygdeytelserIUtlandetFra":null,
                    "pensjon":[]
                },
                "formue":{
                    "borIBolig":null,
                    "verdiPåBolig":null,
                    "boligBrukesTil":null,
                    "depositumsBeløp":null,
                    "kontonummer":null,
                    "verdiPåEiendom":null,
                    "eiendomBrukesTil":null,
                    "verdiPåKjøretøy":null,
                    "kjøretøyDeEier":null,
                    "innskuddsBeløp":null,
                    "verdipapirBeløp":null,
                    "skylderNoenMegPengerBeløp":null,
                    "kontanterBeløp":null
                },
                "forNav":{
                    "harFullmektigEllerVerge":null
                }
            }
        """.trimIndent()
        JsonParser.parseString(json) //JSONObject is apparently not strict enough
        SøknadInnhold.fromJson(JSONObject(json)).shouldBe(expectedSøknadInnhold)
    }
}
