package com.hivetech.utils;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;


@Component
public class DateUtils {

    public static LocalDate getCurrentWeekFirstDate() {
        return LocalDate.now().with(DayOfWeek.MONDAY);
    }

    public static LocalDate getCurrentWeekLastDate() {
        return LocalDate.now().with(DayOfWeek.SUNDAY);
    }

    public static LocalDate getCurrentMonthFirstDate() {
        return LocalDate.ofEpochDay(System.currentTimeMillis() / (24 * 60 * 60 * 1000)).withDayOfMonth(1);
    }

    public static LocalDate getCurrentMonthLastDate() {
        return LocalDate.ofEpochDay(System.currentTimeMillis() / (24 * 60 * 60 * 1000)).plusMonths(1).withDayOfMonth(1).minusDays(1);
    }

    public static LocalDate getCurrentQuarterFirstDate() {
        return LocalDate.now().with(LocalDate.EPOCH.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getCurrentQuarterLastDate() {
        return getCurrentQuarterFirstDate().plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getFirstDayOfYear() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
    }

    public static LocalDate getLastDayOfYear() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
    }
    public static boolean checkExpiredCode(LocalDateTime expiredDate) {
        LocalDateTime now = LocalDateTime.now();
        if (expiredDate.isBefore(now)) return true;
        return false;
    }

    public static boolean validateDate(LocalDateTime createdAt, LocalDateTime expiredAt) {
        if (expiredAt.isBefore(createdAt) || expiredAt.isEqual(createdAt)) return true;
        return false;
    }
}