/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.old.utils;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

public class Random {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @NotNull
    public static String string(final int length) {
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    @NotNull
    public static String customString(final int length, final String characters) {
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        return sb.toString();
    }

    /**
     * Return a random value from a String array
     *
     * @return A random value from the array
     */
    public static String fromArray(@NotNull final String[] strings) {
        final java.util.Random random = new java.util.Random();
        final int index = random.nextInt(strings.length);
        return strings[index];
    }
}
