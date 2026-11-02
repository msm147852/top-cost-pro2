package com.example.core

import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BirthInfoCalculatorTest {
    private val birth = LocalDate.of(1984, 2, 11)
    private val today = LocalDate.of(2026, 10, 29)

    private fun fakeHijri(todayDay: Int): (LocalDate) -> HijriDate = { date ->
        if (date == birth) HijriDate(1404, 5, 10) else HijriDate(1448, 5, todayDay)
    }

    @Test fun regularBirthday() {
        val info = BirthInfoCalculator.compute(birth, today, fakeHijri(18))!!
        assertEquals(42, info.ageYears)
        assertEquals(44, info.hijriAgeYears)
        assertEquals("الدلو", info.zodiac)
        assertEquals(DayOfWeek.SATURDAY, info.birthWeekday)
        assertEquals(LocalDate.of(2027, 2, 11), info.nextBirthday)
        assertEquals(105L, info.daysUntilNextBirthday)
    }

    @Test fun hijriAgeIsOneLessBeforeTheHijriBirthday() {
        assertEquals(43, BirthInfoCalculator.compute(birth, today, fakeHijri(9))!!.hijriAgeYears)
    }

    @Test fun birthdayToday() {
        val b = LocalDate.of(1984, 10, 29)
        val info = BirthInfoCalculator.compute(b, today) { HijriDate(1448, 5, 18) }!!
        assertEquals(42, info.ageYears)
        assertEquals(0L, info.daysUntilNextBirthday)
        assertEquals(today, info.nextBirthday)
    }

    @Test fun leapDayBirthdayIsObservedOnFeb28InNonLeapYears() {
        val info = BirthInfoCalculator.compute(LocalDate.of(2000, 2, 29), LocalDate.of(2026, 3, 1)) { HijriDate(1447, 9, 1) }!!
        assertEquals(LocalDate.of(2027, 2, 28), info.nextBirthday)
        assertEquals(364L, info.daysUntilNextBirthday)
    }

    @Test fun futureBirthDateGivesNull() {
        assertNull(BirthInfoCalculator.compute(today.plusDays(1), today) { HijriDate(1448, 5, 18) })
    }

    @Test fun zodiacBoundaries() {
        assertEquals("الجدي", BirthInfoCalculator.zodiac(LocalDate.of(2001, 1, 19)))
        assertEquals("الدلو", BirthInfoCalculator.zodiac(LocalDate.of(2001, 1, 20)))
        assertEquals("الدلو", BirthInfoCalculator.zodiac(LocalDate.of(2001, 2, 18)))
        assertEquals("الحوت", BirthInfoCalculator.zodiac(LocalDate.of(2000, 2, 29)))
        assertEquals("الحمل", BirthInfoCalculator.zodiac(LocalDate.of(2001, 3, 21)))
        assertEquals("الجدي", BirthInfoCalculator.zodiac(LocalDate.of(2001, 12, 31)))
    }
}
