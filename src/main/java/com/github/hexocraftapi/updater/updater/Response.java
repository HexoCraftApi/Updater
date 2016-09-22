package com.github.hexocraftapi.updater.updater;

/*
 * Copyright 2016 hexosse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
