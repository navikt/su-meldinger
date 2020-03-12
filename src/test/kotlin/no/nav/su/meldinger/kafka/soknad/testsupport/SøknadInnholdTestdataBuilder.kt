package no.nav.su.meldinger.kafka.soknad.testsupport

import no.nav.su.meldinger.kafka.soknad.*
import java.time.LocalDate
import java.time.Month

class SøknadInnholdTestdataBuilder {
    companion object {
        fun personopplysninger(
                fnr: String = "12345678910",
                fornavn: String = "Ola",
                mellomnavn: String = "Erik",
                etternavn: String = "Nordmann",
                telefonnummer: String = "12345678",
                gateadresse: String = "Oslogata 12",
                postnummer: String = "0050",
                poststed: String = "Oslo",
                bruksenhet: String = "U1H20",
                bokommune: String = "Oslo",
                flyktning: Boolean = true,
                borFastINorge: Boolean = true,
                statsborgerskap: String = "NOR"
        ) = Personopplysninger(
                fnr, fornavn, mellomnavn, etternavn, telefonnummer, gateadresse, postnummer, poststed, bruksenhet, bokommune, flyktning, borFastINorge, statsborgerskap
        )

        fun build(
                personopplysninger: Personopplysninger = personopplysninger(),

                boforhold: Boforhold = Boforhold(
                        delerBolig = true,
                        borSammenMed = listOf(
                                "Ektefelle/Partner/Samboer",
                                "Andre personer over 18 år"
                        ),
                        delerBoligMed = listOf(
                                Boforhold.DelerBoligMedPerson("12345678911", "Kari Nordmann"),
                                Boforhold.DelerBoligMedPerson("12345678912", "Per Nordmann"))
                ),

                utenlandsopphold: Utenlandsopphold = Utenlandsopphold(
                        utenlandsopphold = true,
                        registrertePerioder = listOf(
                                UtenlandsoppholdPeriode(LocalDate.of(2020, Month.JANUARY, 1), LocalDate.of(2020, Month.JANUARY, 31)),
                                UtenlandsoppholdPeriode(LocalDate.of(2020, Month.FEBRUARY, 1), LocalDate.of(2020, Month.FEBRUARY, 5))),
                        planlagtUtenlandsopphold = true,
                        planlagtePerioder = listOf(
                                UtenlandsoppholdPeriode(LocalDate.of(2020, Month.JULY, 1), LocalDate.of(2020, Month.JULY, 31))
                        )
                ),

                oppholdstillatelse: Oppholdstillatelse = Oppholdstillatelse(
                        harVarigOpphold = false,
                        utløpsDato = LocalDate.of(2020, Month.DECEMBER, 31),
                        søktOmForlengelse = true
                ),

                inntektPensjonFormue: InntektPensjonFormue = InntektPensjonFormue(
                        framsattKravAnnenYtelse = true,
                        framsattKravAnnenYtelseBegrunnelse = "Har søkt om foreldrepenger",
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
                        annenFormue = listOf(
                                AnnenFormue("Juveler", 2000.0),
                                AnnenFormue("Speedbåt", 200000.0)
                        ),
                        harDepositumskonto = true,
                        depositumBeløp = 25000,
                        harSosialStønad = true
                ),

                forNav: ForNav = ForNav(
                        målform = "Norsk",
                        søkerMøttPersonlig = true,
                        harFullmektigMøtt = false,
                        erPassSjekket = true,
                        merknader = "Intet å bemerke"
                )
        ) = SøknadInnhold(personopplysninger, boforhold, utenlandsopphold, oppholdstillatelse, inntektPensjonFormue, forNav)
    }
}