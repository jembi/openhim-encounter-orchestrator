/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea.transformers.exceptions;

/**
 * Indicates that a setting in the application properties file is invalid.
 */
public class InvalidPropertyException extends Exception {

	private static final long serialVersionUID = 6783918512590940491L;

	public InvalidPropertyException(String property, String value) {
		super("Invalid value '" + value + "' for property '" + property + "'");
	}
}
