package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser
import no.nav.su.meldinger.kafka.soknad.Boforhold.Companion.borSammenMedKey
import no.nav.su.meldinger.kafka.soknad.Boforhold.Companion.delerBoligMedKey
import no.nav.su.meldinger.kafka.soknad.ForNav.Companion.merknaderKey
import no.nav.su.meldinger.kafka.soknad.InntektPensjonFormue.Companion.annenFormueKey
import no.nav.su.meldinger.kafka.soknad.InntektPensjonFormue.Companion.depositumBeløpKey
import no.nav.su.meldinger.kafka.soknad.InntektPensjonFormue.Companion.framsattKravAnnenYtelseBegrunnelseKey
import no.nav.su.meldinger.kafka.soknad.InntektPensjonFormue.Companion.pensjonsOrdningKey
import no.nav.su.meldinger.kafka.soknad.Oppholdstillatelse.Companion.søktOmForlengelseKey
import no.nav.su.meldinger.kafka.soknad.Oppholdstillatelse.Companion.utløpsDatoKey
import no.nav.su.meldinger.kafka.soknad.Personopplysninger.Companion.bruksenhetKey
import no.nav.su.meldinger.kafka.soknad.Personopplysninger.Companion.mellomnavnKey
import no.nav.su.meldinger.kafka.soknad.Utenlandsopphold.Companion.planlagtUtenlandsoppholdKey
import no.nav.su.meldinger.kafka.soknad.Utenlandsopphold.Companion.registrertePerioderKey
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDate
import java.time.Month

internal class SøknadInnholdTest {

    private val aDate = LocalDate.of(2020, Month.MARCH, 10)
    private val personopplysninger = Personopplysninger(
            fnr = "12345678910",
            fornavn = "kake",
            mellomnavn = "kjeks",
            etternavn = "mannen",
            telefonnummer = "12345678",
            gateadresse = "gaten",
            postnummer = "0050",
            poststed = "Oslo",
            bruksenhet = "50",
            bokommune = "Oslo",
            flyktning = true,
            borFastINorge = true,
            statsborgerskap = "NOR"
    )

    private val boforhold = Boforhold(
            delerBolig = true,
            borSammenMed = listOf("voksen", "barn"),
            delerBoligMed = listOf(
                    Boforhold.DelerBoligMedPerson("voksen1", "voksen jensen"),
                    Boforhold.DelerBoligMedPerson("voksen2", "voksen hansen"))
    )

    private val utenlandsopphold = Utenlandsopphold(
            utenlandsopphold = true,
            registrertePerioder = listOf(UtenlandsoppholdPeriode(aDate, aDate)),
            planlagtUtenlandsopphold = true,
            planlagtePerioder = listOf(UtenlandsoppholdPeriode(aDate, aDate))
    )

    private val oppholdstillatelse = Oppholdstillatelse(
            harVarigOpphold = false,
            utløpsDato = aDate,
            søktOmForlengelse = true
    )

    private val inntektPensjonFormue = InntektPensjonFormue(
            framsattKravAnnenYtelse = true,
            framsattKravAnnenYtelseBegrunnelse = "annen ytelse begrunnelse",
            harInntekt = true,
            inntektBeløp = 2500.0,
            harPensjon = true,
            pensjonsOrdning = listOf(
                    PensjonsOrdningBeløp("KLP", 2000.0),
                    PensjonsOrdningBeløp("SPK", 5000.0)),
            sumInntektOgPensjon = 7000.0,
            harFormueEiendom = true,
            harFinansFormue = true,
            formueBeløp = 1000.0,
            harAnnenFormue = true,
            annenFormue = listOf(AnnenFormue("juveler", 2000.0)),
            harDepositumskonto = true,
            depositumBeløp = 25000
    )

    private val forNav = ForNav(
            målform = "norsk",
            søkerMøttPersonlig = true,
            harFullmektigMøtt = false,
            erPassSjekket = true,
            merknader = "intet å bemerke"
    )

    private val søknad = SøknadInnhold(
            personopplysninger = personopplysninger,
            boforhold = boforhold,
            utenlandsopphold = utenlandsopphold,
            oppholdstillatelse = oppholdstillatelse,
            inntektPensjonFormue = inntektPensjonFormue,
            forNav = forNav
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
        val personopplysninger = personopplysninger.toJson()
        assertTrue(personopplysninger.contains("\"kjeks\""))
        assertTrue(personopplysninger.contains("\"50\""))

        val boforhold = boforhold.toJson()
        assertTrue(boforhold.contains("\"voksen\""))
        assertTrue(boforhold.contains("\"barn\""))
        assertTrue(boforhold.contains("\"voksen1\""))
        assertTrue(boforhold.contains("\"voksen jensen\""))

        val oppholdstillatelse = oppholdstillatelse.toJson()
        assertTrue(oppholdstillatelse.contains("\"2020-03-10\""))
        assertFalse(oppholdstillatelse.contains("\"true\""))

        val utenlandsopphold = utenlandsopphold.toJson()
        assertFalse(utenlandsopphold.contains("\"[\""))
        assertFalse(utenlandsopphold.contains("\"]\""))

        val forNav = forNav.toJson()
        assertTrue(forNav.contains("\"intet å bemerke\""))

        val inntektPensjonFormue = inntektPensjonFormue.toJson()
        assertTrue(inntektPensjonFormue.contains("\"annen ytelse begrunnelse\""))
        assertFalse(inntektPensjonFormue.contains("\"[\""))
        assertFalse(inntektPensjonFormue.contains("\"]\""))
    }

    @Test
    fun `should handle optional values as null`() {
        val jsonPersonopplysninger = JSONObject(Personopplysninger(
                fnr = "12345678910",
                fornavn = "fornavn",
                etternavn = "etternavn",
                telefonnummer = "12345678",
                gateadresse = "gateadresse",
                postnummer = "0050",
                poststed = "Oslo",
                bokommune = "Oslo",
                flyktning = true,
                borFastINorge = true,
                statsborgerskap = "NOR"
        ).toJson())
        assertNull(jsonPersonopplysninger.optString(mellomnavnKey, null))
        assertNull(jsonPersonopplysninger.optString(bruksenhetKey, null))

        val personopplysninger = Personopplysninger.fromJson(jsonPersonopplysninger)
        assertNull(personopplysninger.mellomnavn)
        assertNull(personopplysninger.bruksenhet)

        val jsonBoforhold = JSONObject(Boforhold(
                delerBolig = false
        ).toJson())
        assertNull(jsonBoforhold.optString(borSammenMedKey, null))
        assertNull(jsonBoforhold.optString(delerBoligMedKey, null))

        val boforhold = Boforhold.fromJson(jsonBoforhold)
        assertNull(boforhold.borSammenMed)
        assertNull(boforhold.delerBoligMed)

        val jsonOppholdstillatelse = JSONObject(Oppholdstillatelse(
                harVarigOpphold = false
        ).toJson())

        assertNull(jsonOppholdstillatelse.optString(utløpsDatoKey, null))
        assertNull(jsonOppholdstillatelse.optNullableBoolean(søktOmForlengelseKey))

        val oppholdstillatelse = Oppholdstillatelse.fromJson(jsonOppholdstillatelse)
        assertNull(oppholdstillatelse.utløpsDato)
        assertNull(oppholdstillatelse.søktOmForlengelse)

        val jsonUtenlandsopphold = JSONObject(Utenlandsopphold(
                utenlandsopphold = false,
                planlagtUtenlandsopphold = false
        ).toJson())
        assertNull(jsonUtenlandsopphold.optJSONArray(registrertePerioderKey))
        assertNull(jsonUtenlandsopphold.optJSONArray(planlagtUtenlandsoppholdKey))

        val utenlandsopphold = Utenlandsopphold.fromJson(jsonUtenlandsopphold)
        assertNull(utenlandsopphold.registrertePerioder)
        assertNull(utenlandsopphold.planlagtePerioder)

        val jsonForNav = JSONObject(ForNav(
                målform = "målform",
                søkerMøttPersonlig = true,
                harFullmektigMøtt = false,
                erPassSjekket = true
        ).toJson())
        assertNull(jsonUtenlandsopphold.optString(merknaderKey, null))

        val forNav = ForNav.fromJson(jsonForNav)
        assertNull(forNav.merknader)

        val jsonInntektPensjonFormue = JSONObject(InntektPensjonFormue(
                framsattKravAnnenYtelse = false,
                harInntekt = true,
                inntektBeløp = 2500.0,
                harPensjon = false,
                sumInntektOgPensjon = 7000.0,
                harFormueEiendom = false,
                harFinansFormue = true,
                formueBeløp = 1000.0,
                harAnnenFormue = true,
                harDepositumskonto = false
        ).toJson())
        assertNull(jsonInntektPensjonFormue.optString(framsattKravAnnenYtelseBegrunnelseKey, null))
        assertNull(jsonInntektPensjonFormue.optJSONArray(pensjonsOrdningKey))
        assertNull(jsonInntektPensjonFormue.optJSONArray(annenFormueKey))
        assertNull(jsonInntektPensjonFormue.optNullableNumber(depositumBeløpKey))

        val inntektPensjonFormue = InntektPensjonFormue.fromJson(jsonInntektPensjonFormue)
        assertNull(inntektPensjonFormue.framsattKravAnnenYtelseBegrunnelse)
        assertNull(inntektPensjonFormue.pensjonsOrdning)
        assertNull(inntektPensjonFormue.annenFormue)
        assertNull(inntektPensjonFormue.depositumBeløp)

        val søknad = SøknadInnhold(personopplysninger, boforhold, utenlandsopphold, oppholdstillatelse, inntektPensjonFormue, forNav)

        assertDoesNotThrow {
            JSONObject(søknad.toJson())
            JsonParser.parseString(søknad.toJson()) //Strict check of json syntax - JSONObject helps us along a bit too much.
        }
        assertEquals(søknad.toJson(), SøknadInnhold.fromJson(JSONObject(søknad.toJson())).toJson())
    }
}