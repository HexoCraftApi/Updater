package com.github.hexocraftapi.updater.updater.utils;

import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public class LoggerUtils
{
	public static boolean verbose = false;

	/**
	 * This class should not normally be instantiated.
	 */
	private LoggerUtils() {}


	public static void log(Level level, String message)
	{
		if(verbose)
		{
			Bukkit.getServer().getLogger().log(level, "[Updater] " + message);
		}
	}

	public static void log(Level level, String message, Throwable error)
	{
		if(verbose)
			Bukkit.getServer().getLogger().log(level, "[Updater] " + message, error);
	}

}
