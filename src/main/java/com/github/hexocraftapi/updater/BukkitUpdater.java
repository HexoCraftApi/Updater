package com.github.hexocraftapi.updater;

import com.github.hexocraftapi.updater.updater.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import static com.github.hexocraftapi.updater.updater.utils.LoggerUtils.log;
import static com.github.hexocraftapi.updater.updater.utils.WebUtils.initConnection;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
@SuppressWarnings("unused")
public class BukkitUpdater extends Updater
{
	/**
	 * Host to contact
	 */
	protected static String host = "https://api.curseforge.com";

	/**
	 * Repository query
	 */
	protected static String query = "/servermods/files?projectIds={{ PROJECT_ID }}";

	/**
	 * URL to query.
	 */
	protected URL queryUrl;

	/**
	 * API Key.
	 */
	protected String apiKey = null;

	/**
	 * Ressource ID.
	 */
	protected String projectId = null;



	public BukkitUpdater(JavaPlugin plugin, String projectId)
	{
		this(plugin, null, projectId, false);
	}

	public BukkitUpdater(JavaPlugin plugin, String apiKey, String projectId)
	{
		this(plugin, apiKey, projectId, false);
	}

	public BukkitUpdater(JavaPlugin plugin, String apiKey, String projectId, boolean verbose)
	{
		super(plugin, verbose);

		try
		{
			this.apiKey = apiKey;
			this.projectId = projectId;
			this.queryUrl = new URL((host + query).replace("{{ PROJECT_ID }}", this.projectId));
			log(Level.INFO, "Set the URL to " + this.queryUrl);
		}
		catch(NumberFormatException ex)
		{
			log(Level.SEVERE, "Unable to parse semver string!");
		}
		catch(MalformedURLException ex)
		{
			log(Level.SEVERE, "Invalid URL, return failed response.");
			result = Response.FAILED;
		}

		if (this.result != Response.FAILED) {
			this.thread = new Thread(new UpdaterRunnable(this));
			this.thread.start();
		}
	}


	/**
	 * Check the repository for neweest file.
	 *
	 * @return true if successful.
	 */
	protected boolean read()
	{
		try {
			final URLConnection connection = initConnection(queryUrl);
			if(this.apiKey != null) connection.addRequestProperty("X-API-Key", apiKey);
			log(Level.INFO, "Opening connection to API");

			final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			final StringBuilder buffer = new StringBuilder();

			String line;
			while((line = reader.readLine()) != null)
				buffer.append(line);

			JSONArray releases = (JSONArray) JSONValue.parse(buffer.toString());
			log(Level.INFO, "Parsing the returned JSON");

			if(releases.isEmpty())
			{
				log(Level.WARNING, "The updater could not find any files for the project id " + this.projectId);
				this.result = Response.REPO_NO_RELEASES;
				return false;
			}

			JSONObject release = (JSONObject) releases.get(releases.size() - 1);
			String name = release.get("name").toString();
			String downloadUrl = release.get("downloadUrl").toString();
			String fileName = release.get("fileName").toString();
			String gameVersion = release.get("gameVersion").toString();
			ReleaseType type = ((Boolean)release.get("releaseType").equals("release")) ? ReleaseType.RELEASE : ReleaseType.PRE_RELEASE;

			if(Version.isSemver(fileName))
			{
				this.update = new Update(this.plugin ,name, Version.parse(fileName), type);
				this.update.setDownloadUrl(new URL(downloadUrl));
				this.update.setVersionGame(gameVersion);

				if(current.isLower(this.update.getVersion()))
				{
					log(Level.INFO, "Hooray, we found a semver compliant update!");
					this.result = Response.SUCCESS;
				}
				else
				{
					log(Level.INFO, "The update you specified is the latest update available!");
					this.result = Response.NO_UPDATE;
					this.update = null;
				}
			}
			else
			{
				log(Level.WARNING, "Version string is not semver compliant!");
				this.result = Response.REPO_NOT_SEMVER;
				this.update = null;
			}
		}
		catch(Exception e)
		{
			if(e.getMessage().contains("connect timed out"))
			{
				log(Level.WARNING, "HTTP request time out!");
				this.result = Response.ERROR_TIME_OUT;
				this.update = null;
			}
			else if(e.getMessage().contains("HTTP response code: 403"))
			{
				log(Level.WARNING, "GitHub denied our HTTP request!");
				this.result = Response.ERROR_403;
				this.update = null;
			}
			else if(e.getMessage().contains("HTTP response code: 404"))
			{
				log(Level.WARNING, "The specified repository could not be found!");
				this.result = Response.ERROR_404;
				this.update = null;
			}
			else if(e.getMessage().contains("HTTP response code: 500"))
			{
				log(Level.WARNING, "Internal server error");
				this.result = Response.ERROR_500;
				this.update = null;
			}
			else
			{
				log(Level.SEVERE, "Failed to check for updates!");
				this.result = Response.FAILED;
				this.update = null;
			}
			return false;
		}
		return true;
	}
}
