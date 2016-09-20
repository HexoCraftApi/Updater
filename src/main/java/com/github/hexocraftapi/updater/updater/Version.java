package com.github.hexocraftapi.updater.updater;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Simple major.minor.patch storage system with getters.
 */
@SuppressWarnings("unused")
public class Version {

	/**
	 * Store the update major.
	 */
	private final int major;

	/**
	 * Store the update minor.
	 */
	private final int minor;

	/**
	 * Store the update patch.
	 */
	private final int patch;

	/**
	 * Pattern used to match semantic versioning compliant strings.
	 *
	 * Major: matcher.group(1) Minor: matcher.group(2) Patch: matcher.group(3)
	 */
	protected static Pattern semver = Pattern.compile("(?:.*)([0-9]+)\\.([0-9]+)\\.([0-9]+).*", Pattern.CASE_INSENSITIVE);



	/**
	 * Create a new instance of the {@link Version} class.
	 *
	 * @param major semver major
	 * @param minor semver minor
	 * @param patch semver patch
	 */
	public Version(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	/**
	 * Quick method for parsing update strings and matching them using the {@link Pattern} in {@link Version}
	 *
	 * @param version semver string to parse
	 * @return {@link Version} if valid semver string
	 */
	public static Version parse(String version) {
		Matcher matcher = Version.semver.matcher(version);

		if (matcher.matches() && matcher.groupCount()==3) {
			int x = Integer.parseInt(matcher.group(1));
			int y = Integer.parseInt(matcher.group(2));
			int z = Integer.parseInt(matcher.group(3));

			return new Version(x, y, z);
		}

		return null;
	}

	/**
	 * Quick method for parsing update strings and matching them using the {@link Pattern} in {@link Version}
	 *
	 * @param plugin Bukkit plugin
	 * @return {@link Version} if valid semver string
	 */
	public static Version parse(JavaPlugin plugin) {
		return Version.parse(plugin.getDescription().getVersion());
	}



	/**
	 * Test if the update string contains a valid semver string
	 *
	 * @param version update to test
	 * @return true if valid
	 */
	public static boolean isSemver(String version) {
		return Version.parse(version) != null;
	}



	/**
	 * @return semver major
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @return semver minor
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * @return semver patch
	 */
	public int getPatch() {
		return patch;
	}

	/**
	 * @return joined update string.
	 */
	@Override
	public String toString() {
		return major + "." + minor + "." + patch;
	}

	/**
	 * Check if this version is equal to the input version.
	 *
	 * @param version input {@link Version} object
	 * @return true if the update is greater than ours
	 */
	public boolean equals(Version version)
	{
		if(version.getMajor() != this.getMajor())   return false;
		if(version.getMinor() != this.getMinor())   return false;
		if(version.getPatch() != this.getPatch())   return false;
		return true;
	}

	/**
	 * Check if this version is lower than the input version.
	 *
	 * @param version input {@link Version} object
	 * @return true if the update is greater than ours
	 */
	public boolean isLower(Version version) {
		int result = version.getMajor() - this.getMajor();
		if (result == 0) {
			result = version.getMinor() - this.getMinor();
			if (result == 0) {
				result = version.getPatch() - this.getPatch();
			}
		}
		return result > 0;
	}

	/**
	 * Check if this version is greater than the input version.
	 *
	 * @param version input {@link Version} object
	 * @return true if the update is lower than ours
	 */
	public boolean isGreater(Version version) {
		return  version.isLower(this);
	}
}
