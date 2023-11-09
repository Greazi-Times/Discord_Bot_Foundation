/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.old.handlers.crons;

import com.greazi.old.SimpleBot;
import org.quartz.*;

import java.util.Date;

/**
 * A cron job creator to simply create your cron jobs
 */
public abstract class SimpleCron implements Job {

    // ----------------------------------------------------------------------------------------
    // Main options
    // ----------------------------------------------------------------------------------------

    /**
     * The name of the cron job
     */
    private String name = "example";

    /**
     * The group of the cron job
     */
    private String group = "example";

    /**
     * The cron expression of the cron job
     */
    private String schedule = "0 * * * * ?";

    /**
     * Should the cron job re-execute if it fails
     */
    private boolean shouldRecover = false;

    /**
     * Should the cron job be stored if no trigger is assigned
     */
    private boolean durability = false;

    /**
     * Is it priority
     */
    private int priority = 0;

    private Date startTime = new Date();

    private Date endTime = null;

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * Run the cron job logic
     *
     * @param context JobExecutionContext
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {

    }

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Set the name
     */
    public SimpleCron(final String name) {
        this.name = name;
    }

    /**
     * Set the group
     */
    public void group(final String group) {
        this.group = group;
    }

    /**
     * Set the schedule
     */
    public void schedule(final String schedule) {
        this.schedule = schedule;
    }

    /**
     * Set the schedule
     */
    public void schedule(final SimpleCronSchedule schedule) {
        this.schedule = schedule.getExpression();
    }

    /**
     * Should the cron job re-execute if it fails (default: false)
     */
    public void recover(final boolean recover) {
        this.shouldRecover = recover;
    }

    /**
     * Should the cron job be stored if no trigger is assigned (default: false)
     */
    public void alwaysStore(final boolean alwaysStore) {
        this.durability = alwaysStore;
    }

    /**
     * Set its priority (default: 0)
     */
    public void priority(final int priority) {
        this.priority = priority;
    }

    /**
     * Set the time the Trigger should start at
     */
    public void startTime(final Date triggerStartTime) {
        this.startTime = triggerStartTime;
    }

    /**
     * Set the time at which the Trigger will no longer fire - even if it's
     */
    public void endTime(final Date triggerEndTime) {
        this.endTime = triggerEndTime;
    }

    // ----------------------------------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------------------------------

    /**
     * Returns the name of the cron job
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the group of the cron job
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Return the schedule of the cron job
     *
     * @return the schedule
     */
    public String getSchedule() {
        return schedule;
    }

    /**
     * Whether the cron job should re-execute if it fails (default: false)
     *
     * @return the shouldRecover
     */
    public boolean getShouldRecover() {
        return shouldRecover;
    }

    /**
     * Whether the cron job should be stored if no trigger is assigned (default: false)
     *
     * @return the always store
     */
    public boolean getAlwaysStore() {
        return durability;
    }

    /**
     * The priority (default: 0)
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * The time the Trigger should start at
     *
     * @return the start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * The time at which the Trigger will no longer fire - even if it's
     *
     * @return the end time
     */
    public Date getEndTime() {
        return endTime;
    }

    public JobDetail getJobDetail() {
        return JobBuilder
                .newJob(this.getClass())
                .withIdentity(this.getName(), this.getGroup())
                .requestRecovery(this.getShouldRecover())
                .storeDurably(this.getAlwaysStore())
                .build();
    }

    public CronTrigger getTrigger() {
        return TriggerBuilder
                .newTrigger()
                .withIdentity("PatreonFetcherTrigger", "PatreonFetcher")
                .withSchedule(CronScheduleBuilder.cronSchedule(this.getSchedule()))
                .startAt(startTime)
                .endAt(endTime)
                .build();
    }

    public void build() {
        SimpleBot.getCronHandler().addCron(this);
    }

}
