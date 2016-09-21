package com.github.hexocraftapi.updater.updater;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
@SuppressWarnings("unused")
public class Update
{
	/**
	 * The plugin being updated.
	 */
	private final JavaPlugin plugin;

	/**
	 * Name.
	 */
	private final String name;

	/**
	 * Version available for download.
	 */
	private final Version version;

	/**
	 * Type of release.
	 */
	private final ReleaseType releaseType;

	/**
	 * Download link.
	 */
	private URL downloadUrl;

	/*
	 * update's game
	 */
	private String versionGame;

	/*
	 * Changes
	 */
	private String changes;



	public Update(JavaPlugin plugin, String name, Version version, ReleaseType releaseType)
	{
		this.plugin = plugin;
		this.name = name;
		this.version = version;
		this.releaseType = releaseType;
	}



	public JavaPlugin getPlugin()
	{
		return plugin;
	}

	public String getName()
	{
		return name;
	}

	public Version getVersion()
	{
		return version;
	}

	public ReleaseType getReleaseType()
	{
		return releaseType;
	}

	public void setDownloadUrl(URL downloadUrl)
	{
		this.downloadUrl = downloadUrl;
	}

	public URL getDownloadUrl()
	{
		return downloadUrl;
	}

	public void setVersionGame(String versionGame)
	{
		this.versionGame = versionGame;
	}

	public String getVersionGame()
	{
		return versionGame;
	}

	public void setChanges(String changes)
	{
		this.changes = changes;
	}

	public String getChanges()
	{
		return changes;
	}

	/*public String getNewFileName()
	{
		File file = ReflectionUtils.getPluginFile(this.plugin);
		String name = FilenameUtils.getBaseName(file.getPath());
		String ext = FilenameUtils.getExtension(file.getPath());
		return name + " " + this.version.toString() + "." + ext;
	}*/
}
