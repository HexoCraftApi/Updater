package com.github.hexocraftapi.updater.updater;

/**
 * Enumeration of possible responses from the updater.
 *
 * @author Connor Spencer Harries
 */
public enum Response {

	/**
	 * An update has been found.
	 */
	SUCCESS,

	/**
	 * An error occurred whilst trying to find updates.
	 */
	FAILED,


	/**
	 * The specified update is already the latest update
	 */
	NO_UPDATE,

	/**
	 * Connection time out.
	 */
	ERROR_TIME_OUT,

	/**
	 * Denied connection.
	 */
	ERROR_403,

	/**
	 * Repository could not be found.
	 */
	ERROR_404,

	/**
	 * Used to indicate a server error such as HTTP status code 500.
	 */
	ERROR_500,

	/**
	 * The latest release isn't semver compliant.
	 */
	REPO_NOT_SEMVER,

	/**
	 * No releases have been made on the repository.
	 */
	REPO_NO_RELEASES;

	@Override
	public String toString() {
		return this.name();
	}

}
