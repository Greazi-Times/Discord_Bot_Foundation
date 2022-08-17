/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.crons;

import org.quartz.CronScheduleBuilder;

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

    public void everyXMinutes(int minute) {
        expression = "0 " + minute + " * * * ?";
    }

    public void everyXHours(int hour) {
        expression = "0 0 " + hour + " * * ?";
    }

    public void everyXDays(int day) {
        expression = "0 0 0 " + day + " * ?";
    }

    public void everyXWeeks(int week) {
        expression = "0 0 0 * " + week + " ?";
    }

    public void everyXMonths(int month) {
        expression = "0 0 0 " + month + " * ?";
    }

    public void everyXYears(int year) {
        expression = "0 0 0 1 " + year + " ?";
    }

    public void everyXthMinute(int minute, int hour) {
        expression = "0 */" + minute + " " + hour + " * * ?";
    }

    public void everyXthHour(int hour, int day) {
        expression = "0 0 */" + hour + " " + day + " * ?";
    }

    public void everyXthDay(int day, int month) {
        expression = "0 0 0 */" + day + " " + month + " ?";
    }

    public void everyXthWeek(int week, int month) {
        expression = "0 0 0 * */" + week + " " + month + " ?";
    }

    public void everyXthMonth(int month, int year) {
        expression = "0 0 0 " + month + " */" + year + " ?";
    }

    public void everyXthYear(int year) {
        expression = "0 0 0 1 */" + year + " ?";
    }

    public void DailyAt(int hour, int minute) {
        expression = "0 " + minute + " " + hour + " * * ?";
    }

    public void WeeklyAt(int hour, int minute, int day) {
        expression = "0 " + minute + " " + hour + " * " + day + " ?";
    }

    public void MonthlyAt(int hour, int minute, int day) {
        expression = "0 " + minute + " " + hour + " " + day + " * ?";
    }

    public void YearlyAt(int hour, int minute, int day, int month) {
        expression = "0 " + minute + " " + hour + " " + day + " " + month + " ?";
    }

    public String getExpression(){
        return expression;
    }

}
