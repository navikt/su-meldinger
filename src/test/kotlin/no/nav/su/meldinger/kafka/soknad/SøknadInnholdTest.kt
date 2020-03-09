package no.nav.su.meldinger.kafka.soknad

import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDate

internal class SøknadInnholdTest {

    private val personopplysninger = Personopplysninger(
            fnr = "12345678910",
            fornavn = "fornavn",
            mellomnavn = "mellomnavn",
            etternavn = "etternavn",
            telefonnummer = "12345678",
            gateadresse = "gateadresse",
            postnummer = "0050",
            poststed = "Oslo",
            bruksenhet = "50",
            bokommune = "Oslo",
            flyktning = true,
            borFastINorge = true,
            statsborgerskap = "NOR"
    ).also {
        assertDoesNotThrow() {
            JSONObject(it.toJson())
        }
    }

    private val boforhold = Boforhold(
            delerBolig = true,
            borSammenMed = listOf("borSammenMed"),
            delerBoligMed = listOf(
                    Boforhold.DelerBoligMedPerson("fnr", "navn"),
                    Boforhold.DelerBoligMedPerson("fnr", "navn"))
    ).also {
        assertDoesNotThrow() {
            JSONObject(it.toJson())
        }
    }

    private val utenlandsopphold = Utenlandsopphold(
            utenlandsopphold = true,
            registrertePerioder = listOf(UtenlandsoppholdPeriode(LocalDate.now(), LocalDate.now())),
            planlagtUtenlandsopphold = true,
            planlagtePerioder = listOf(UtenlandsoppholdPeriode(LocalDate.now(), LocalDate.now()))
    ).also {
        assertDoesNotThrow() {
            JSONObject(it.toJson())
        }
    }

    private val oppholdstillatelse = Oppholdstillatelse(
            harVarigOpphold = false,
            utløpsDato = LocalDate.now(),
            søktOmForlengelse = true
    ).also {
        assertDoesNotThrow() {
            JSONObject(it.toJson())
        }
    }

    private val inntektPensjonFormue = InntektPensjonFormue(
            framsattKravAnnenYtelse = true,
            framsattKravAnnenYtelseBegrunnelse = "framsattKravAnnenYtelseBegrunnelse",
            harInntekt = true,
            inntektBeløp = 2500.0,
            harPensjon = true,
            pensjonsOrdning = listOf(
                    PensjonsOrdningBeløp("ordning", 2000.0),
                    PensjonsOrdningBeløp("ordning", 5000.0)),
            sumInntektOgPensjon = 7000.0,
            harFormueEiendom = true,
            harFinansFormue = true,
            formueBeløp = 1000.0,
            harAnnenFormue = true,
            annenFormue = listOf(AnnenFormue("type", 2000.0))
    ).also {
        assertDoesNotThrow() {
            JSONObject(it.toJson())
        }
    }

    private val søknad = SøknadInnhold(
            personopplysninger = personopplysninger,
            boforhold = boforhold,
            utenlandsopphold = utenlandsopphold,
            oppholdstillatelse = oppholdstillatelse,
            inntektPensjonFormue = inntektPensjonFormue
    ).also {
        assertDoesNotThrow() {
            JSONObject(it.toJson())
        }
    }

    @Test
    fun `should create object from json`() {
        println(søknad)
        assertEquals(søknad.toJson(), SøknadInnhold.fromJson(JSONObject(søknad.toJson())).toJson())
    }
}