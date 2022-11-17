package com.vanityproject.core.services.vanity;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Imported from ACS AEM Commons
 *
 */
@ConsumerType
public interface VanityUrlAdjuster {
	/**
	 * Allows for custom adjustment of the vanity path after its been parsed and
	 * resourceResolver.map(..)'d, but before it's been dispatched.
	 *
	 * @param vanityUrl the vanityUrl derived from the request's requestUri passed
	 *                  through resourceResolver.map(..)
	 * @return the vanityUrl to try to resolve
	 */
	String adjust(String vanityUrl);
}
