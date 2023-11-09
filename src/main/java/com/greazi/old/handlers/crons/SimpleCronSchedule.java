/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.old.handlers.crons;

/**
 * A cron job creator to simply create your cron jobs
 */
public abstract class SimpleCronSchedule {

    public String expression = "0 * * * * ?";

    public void everySecond() {
        expression = "* * * * * ?";
    }

    public void everyMinute() {
        expression = "0 * * * * ?";
    }

    public void everyHour() {
        expression = "0 0 * * * ?";
    }

    public void everyDay() {
        expression = "0 0 0 * * ?";
    }

    public void everyWeek() {
        expression = "0 0 0 * * ?";
    }

    public void everyMonth() {
        expression = "0 0 0 1 * ?";
    }

    public void everyYear() {
        expression = "0 0 0 1 1 ?";
    }

    public void everyXMinutes(final int minute) {
        expression = "0 " + minute + " * * * ?";
    }

    public void everyXHours(final int hour) {
        expression = "0 0 " + hour + " * * ?";
    }

    public void everyXDays(final int day) {
        expression = "0 0 0 " + day + " * ?";
    }

    public void everyXWeeks(final int week) {
        expression = "0 0 0 * " + week + " ?";
    }

    public void everyXMonths(final int month) {
        expression = "0 0 0 " + month + " * ?";
    }

    public void everyXYears(final int year) {
        expression = "0 0 0 1 " + year + " ?";
    }

    public void everyXthMinute(final int minute, final int hour) {
        expression = "0 */" + minute + " " + hour + " * * ?";
    }

    public void everyXthHour(final int hour, final int day) {
        expression = "0 0 */" + hour + " " + day + " * ?";
    }

    public void everyXthDay(final int day, final int month) {
        expression = "0 0 0 */" + day + " " + month + " ?";
    }

    public void everyXthWeek(final int week, final int month) {
        expression = "0 0 0 * */" + week + " " + month + " ?";
    }

    public void everyXthMonth(final int month, final int year) {
        expression = "0 0 0 " + month + " */" + year + " ?";
    }

    public void everyXthYear(final int year) {
        expression = "0 0 0 1 */" + year + " ?";
    }

    public void DailyAt(final int hour, final int minute) {
        expression = "0 " + minute + " " + hour + " * * ?";
    }

    public void WeeklyAt(final int hour, final int minute, final int day) {
        expression = "0 " + minute + " " + hour + " * " + day + " ?";
    }

    public void MonthlyAt(final int hour, final int minute, final int day) {
        expression = "0 " + minute + " " + hour + " " + day + " * ?";
    }

    public void YearlyAt(final int hour, final int minute, final int day, final int month) {
        expression = "0 " + minute + " " + hour + " " + day + " " + month + " ?";
    }

    public String getExpression() {
        return expression;
    }

}
