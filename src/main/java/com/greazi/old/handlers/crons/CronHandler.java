/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.old.handlers.crons;

import com.greazi.old.Common;
import com.greazi.old.SimpleBot;
import com.greazi.old.debug.Debugger;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * The slash command handler that handles the whole slash command event.
 * Uses the information of {@link SimpleCron}
 */
public class CronHandler extends ListenerAdapter {

    // The hashmap and list of tall the cron jobs
    private final HashMap<String, SimpleCron> cronList = new HashMap<>();
    private Scheduler scheduler;

    /**
     * The main cron job handler
     */
    public CronHandler() {
        Debugger.debug("SlashCommand", "Slash Command main method");
        SimpleBot.getJDA().addEventListener(this);

        // Create the scheduler
        try {
            final SchedulerFactory sf = new StdSchedulerFactory();
            this.scheduler = sf.getScheduler();
            scheduler.start();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The main cron job handler
     */
    public CronHandler(final boolean autoStart) {
        Debugger.debug("SlashCommand", "Slash Command main method");
        SimpleBot.getJDA().addEventListener(this);

        // Create the scheduler
        try {
            final SchedulerFactory sf = new StdSchedulerFactory();
            this.scheduler = sf.getScheduler();
            if (autoStart) scheduler.start();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a cron job to the CronJobs list
     *
     * @param module The CronJob module
     * @return this
     */
    public CronHandler addCron(final SimpleCron module) {
        // Add the module to the list
        cronList.put(module.getName(), module);
        return this;
    }

    /**
     * Schedule all cron jobs
     */
    public void scheduleJobs() {
        // Register all the commands
        for (final Map.Entry<String, SimpleCron> entry : cronList.entrySet()) {
            final SimpleCron cron = entry.getValue();
            try {
                scheduler.scheduleJob(cron.getJobDetail(), cron.getTrigger());
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the scheduler
     */
    public void start() {
        try {
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the scheduler
     */
    public void stop() {
        Common.log("Stopping Cron Jobs");
        try {
            if (scheduler.isStarted()) {
                scheduler.shutdown();
                Common.log("CronJobs stopped");
                return;
            } else {
                Common.error("CronJobs already stopped");
                return;
            }
        } catch (final Exception e) {
            Common.throwError(e, "Error while stopping Cron Jobs");
        }
        return;
    }

    public boolean isShutdown() {
        try {
            return scheduler.isShutdown();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Pause all cron jobs
     */
    public void pause() {
        try {
            scheduler.pauseAll();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resume all cron jobs
     */
    public void resume() {
        try {
            scheduler.resumeAll();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


}
