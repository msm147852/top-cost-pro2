package com.example.core

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** Hijri date; [month] is 1-based. Approximate: calendars differ by a day depending on the method. */
data class HijriDate(val year: Int, val month: Int, val day: Int)

/** Everything derived from a birth date. Never stored: it is recomputed so it stays correct over time. */
data class BirthInfo(
    val ageYears: Int,
    val hijriAgeYears: Int,
    val hijriBirth: HijriDate,
    /** Arabic sign name without any suffix; the UI adds the "entertainment only" note. */
    val zodiac: String,
    val birthWeekday: DayOfWeek,
    val nextBirthday: LocalDate,
    val daysUntilNextBirthday: Long
)

object BirthInfoCalculator {

    /**
     * Returns null when [birth] is in the future. A 29 February birthday is observed on
     * 28 February in non-leap years. [hijri] is injectable so the rest is testable on a plain JVM.
     */
    fun compute(
        birth: LocalDate,
        today: LocalDate = LocalDate.now(),
        hijri: (LocalDate) -> HijriDate = ::icuHijri
    ): BirthInfo? {
        if (birth.isAfter(today)) return null
        val birthHijri = hijri(birth)
        val todayHijri = hijri(today)
        val hijriAge = todayHijri.year - birthHijri.year -
            if (todayHijri.month * 32 + todayHijri.day < birthHijri.month * 32 + birthHijri.day) 1 else 0
        val next = (today.year..today.year + 4)
            .map { birth.withYear(it) }
            .first { !it.isBefore(today) }
        return BirthInfo(
            ageYears = Period.between(birth, today).years,
            hijriAgeYears = hijriAge,
            hijriBirth = birthHijri,
            zodiac = zodiac(birth),
            birthWeekday = birth.dayOfWeek,
            nextBirthday = next,
            daysUntilNextBirthday = ChronoUnit.DAYS.between(today, next)
        )
    }

    fun zodiac(date: LocalDate): String = when (date.monthValue * 100 + date.dayOfMonth) {
        in 321..419 -> "الحمل"
        in 420..520 -> "الثور"
        in 521..620 -> "الجوزاء"
        in 621..722 -> "السرطان"
        in 723..822 -> "الأسد"
        in 823..922 -> "العذراء"
        in 923..1022 -> "الميزان"
        in 1023..1121 -> "العقرب"
        in 1122..1221 -> "القوس"
        in 1222..1231, in 101..119 -> "الجدي"
        in 120..218 -> "الدلو"
        else -> "الحوت" // 19 Feb - 20 Mar; every calendar day is covered by the ranges above
    }
}

/** Same conversion the screens used before (ICU Islamic calendar), now in one place. */
internal fun icuHijri(date: LocalDate): HijriDate {
    val calendar = android.icu.util.IslamicCalendar()
    calendar.timeInMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return HijriDate(
        year = calendar.get(android.icu.util.Calendar.YEAR),
        month = calendar.get(android.icu.util.Calendar.MONTH) + 1,
        day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)
    )
}
