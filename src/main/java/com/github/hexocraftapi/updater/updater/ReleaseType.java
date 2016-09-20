package com.github.hexocraftapi.updater.updater;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public enum ReleaseType
{
	/**
	 * An "pre-release" file.
	 *
	 * Bukkit Alpha and Beta file are considered as a pre-release
	 */
	PRE_RELEASE,

	/**
	 * A "release" file.
	 *
	 * Used for Bukkit, Spigot and GitHub
	 */
	RELEASE
}
