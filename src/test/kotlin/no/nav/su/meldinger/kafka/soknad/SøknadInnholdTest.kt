package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow

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
        assertDoesNotThrow() {
            JSONObject(søknad.toJson())
            JsonParser.parseString(søknad.toJson()) //Strict check of json syntax - JSONObject helps us along a bit too much.
        }
        assertEquals(søknad.toJson(), SøknadInnhold.fromJson(JSONObject(søknad.toJson())).toJson())
    }

    @Test
    fun `should put " " if appropriate around conditional values`() {
        val personopplysninger = søknad.personopplysninger.toJson()
        assertTrue(personopplysninger.contains("\"Erik\""))
        assertTrue(personopplysninger.contains("\"U1H20\""))

        val boforhold = søknad.boforhold.toJson()
        boforhold.shouldContain(""""ektemake-eller-samboer"""")

        val oppholdstillatelse = søknad.oppholdstillatelse.toJson()
        oppholdstillatelse.shouldContain(""""midlertidig"""")

        val utenlandsopphold = søknad.utenlandsopphold.toJson()
        assertFalse(utenlandsopphold.contains("\"[\""))
        assertFalse(utenlandsopphold.contains("\"]\""))

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
}
