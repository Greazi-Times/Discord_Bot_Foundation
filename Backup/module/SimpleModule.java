/*
 * Copyright (c) 2022. Greazi All rights reservered
 */

package com.greazi.discordbotfoundation.module;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.objects.Requirement;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SimpleModule {

    protected SimpleBot bot;

    private boolean enabled;

    public SimpleModule(SimpleBot bot) {
        this.bot = bot;
    }

    public void enable() {
        Set<Requirement> failedRequirements = Arrays.stream(setRequirements()).filter(requirement -> !requirement.check()).collect(Collectors.toSet());
        if(failedRequirements.isEmpty()) {
            //Common.log("Enabling Module " + getName() + "..");
            onEnable();
            enabled = true;
        } else {
            Common.warning("Failed Enabling Module " + setName() + " because:");
            failedRequirements.forEach(requirement -> Common.log("- " + requirement.getUnmatchMessage()));
        }
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract String setName();

    public abstract Requirement[] setRequirements();

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }
}