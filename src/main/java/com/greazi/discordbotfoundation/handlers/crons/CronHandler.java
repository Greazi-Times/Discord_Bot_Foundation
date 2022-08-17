/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.crons;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import java.util.*;


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
        try{
            SchedulerFactory sf = new StdSchedulerFactory();
            this.scheduler = sf.getScheduler();
            scheduler.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * The main cron job handler
     */
    public CronHandler(boolean autoStart) {
        Debugger.debug("SlashCommand", "Slash Command main method");
        SimpleBot.getJDA().addEventListener(this);

        // Create the scheduler
        try{
            SchedulerFactory sf = new StdSchedulerFactory();
            this.scheduler = sf.getScheduler();
            if(autoStart) scheduler.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Add a cron job to the CronJobs list
     * @param module The CronJob module
     * @return this
     */
    public CronHandler addCron(SimpleCron module) {
        // Add the module to the list
        cronList.put(module.getName(), module);
        return this;
    }

    /**
     *  Schedule all cron jobs
     */
    public void scheduleJobs() {
        // Register all the commands
        for(Map.Entry<String, SimpleCron> entry : cronList.entrySet()) {
            SimpleCron cron = entry.getValue();
            try {
                scheduler.scheduleJob(cron.getJobDetail(), cron.getTrigger());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the scheduler
     */
    public void start() {
        try {
            if (!scheduler.isStarted()){
                scheduler.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Stop the scheduler
     */
    public void stop() {
        try {
            if (scheduler.isStarted()){
                scheduler.shutdown();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isShutdown() {
        try {
            return scheduler.isShutdown();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Pause all cron jobs
     */
    public void pause(){
        try {
            scheduler.pauseAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Resume all cron jobs
     */
    public void resume(){
        try {
            scheduler.resumeAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
