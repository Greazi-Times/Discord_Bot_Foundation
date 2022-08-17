/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple class to format the time
 */
public class DateFormatter {

	/**
	 * Format a milSec to a DateTime stamp yyyy-MM-dd HH:mm:ss
	 *
	 * @param milSec long - Time in milliseconds
	 * @return DateTime
	 */
	public static String formatSqlDate(long milSec) {

		// Get the date from milliseconds
		Date date = new Date(milSec);
		// Format the time
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Return the formatted time
		return formatter.format(date);
	}
}
