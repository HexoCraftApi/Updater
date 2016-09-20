package com.github.hexocraftapi.updater.updater;

import org.apache.commons.lang.Validate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.github.hexocraftapi.updater.updater.utils.LoggerUtils.log;
import static com.github.hexocraftapi.updater.updater.utils.WebUtils.getProxy;
import static com.github.hexocraftapi.updater.updater.utils.WebUtils.initConnection;
import static com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler.BUFFER_SIZE;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public class Downloader
{
	/**
	 * Used for downloading files.
	 */
	private static final int BUFFER_SIZE = 1024;

	/**
	 * The update.
	 */
	final Update update;

	/**
	 * The plugin folder.
	 */
	final File pluginFolder;

	/**
	 * The update folder.
	 */
	final File updateFolder;


	public Downloader(Update update)
	{
		Validate.notNull(update, "update cannot be null");
		Validate.notNull(update.getPlugin(), "plugin cannot be null");
		Validate.notNull(update.getDownloadUrl(), "plugin cannot be null");

		this.update = update;
		this.pluginFolder = update.getPlugin().getDataFolder().getParentFile();
		this.updateFolder = new File(pluginFolder, "Updater");

		if(!this.updateFolder.exists())
			this.fileIOOrError(updateFolder, updateFolder.mkdir(), true);
	}

	public boolean download()
	{
		// Remove possibly leftover files from the updater folder
		deleteOldFiles();

		// Download the file and save it to the updater folder
		if(!downloadFile())
			return false;

		return true;
	}

	/**
	 * Remove possibly leftover files from the updater folder.
	 */
	private void deleteOldFiles()
	{
		//Just a quick check to make sure we didn't leave any files from last time...
		File[] files = listFilesOrError(this.updateFolder);
		for(final File file : files)
		{
			if(file.getName().endsWith(".zip"))
				this.fileIOOrError(file, file.mkdir(), true);
		}
	}

	/**
	 * Download the file and save it to the updater folder.
	 */
	private boolean downloadFile()
	{
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try
		{
			// Init connection
			HttpURLConnection connection = (HttpURLConnection)initConnection(this.update.getDownloadUrl());

			// always check HTTP response code first
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER)
			{
				String newUrl = connection.getHeaderField("Location");
				connection = (HttpURLConnection)initConnection(new URL(newUrl));
				responseCode = connection.getResponseCode();
			}

			// always check HTTP response code first
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				String fileName = "";
				String fileURL = this.update.getDownloadUrl().toString();
				String disposition = connection.getHeaderField("Content-Disposition");
				String contentType = connection.getContentType();
				int fileLength = connection.getContentLength();

				// extracts file name from header field
				if(disposition != null)
				{
					int index = disposition.indexOf("filename=");
					if(index > 0)
						fileName = disposition.substring(index + 10, disposition.length() - 1);
				}
				// extracts file name from URL
				else
					fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());

				// opens input stream from the HTTP connection
				in = new BufferedInputStream(connection.getInputStream());

				// opens an output stream to save into file
				fout = new FileOutputStream(new File(this.updateFolder, fileName));

				log(Level.INFO, "About to download a new update: " + this.update.getVersion().toString());
				final byte[] buffer = new byte[BUFFER_SIZE]; int bytesRead;
				while((bytesRead = in.read(buffer, 0, BUFFER_SIZE)) != -1)
					fout.write(buffer, 0, bytesRead);
				log(Level.INFO, "File downloaded: " + fileName);
			}
		}
		catch(Exception ex) {
			log(Level.WARNING, "The auto-updater tried to download a new update, but was unsuccessful.");
			return false;
		}
		finally
		{
			try { if(in != null) in.close(); }
			catch(final IOException ex) {
				log(Level.SEVERE, null);
				return false;
			}
			try { if(fout != null) fout.close(); }
			catch(final IOException ex)  {
				log(Level.SEVERE, null);
				return false;
			}
			return true;
		}
	}

	/**
	 * Perform a file operation and log any errors if it fails.
	 * @param file file operation is performed on.
	 * @param result result of file operation.
	 * @param create true if a file is being created, false if deleted.
	 */
	private void fileIOOrError(File file, boolean result, boolean create)
	{
		if(!result)
			log(Level.SEVERE, "The updater could not " + (create ? "create" : "delete") + " file at: " + file.getAbsolutePath());
	}

	private File[] listFilesOrError(File folder)
	{
		File[] files = folder.listFiles();
		if(files == null)
		{
			log(Level.SEVERE, "The updater could not access files at: " + this.updateFolder.getAbsolutePath());
			return new File[0];
		}
		else
		{
			return files;
		}
	}
}
