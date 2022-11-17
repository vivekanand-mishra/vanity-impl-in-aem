package com.vanityproject.core.utils;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class VanityUtil.
 */
public class VanityUtil {

	private VanityUtil() {
		// No code
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(VanityUtil.class);

	/**
	 * Get site scope
	 * 
	 * @param requestPath - full page path
	 * @return String
	 */
	public static String getSiteScope(String requestPath) {
		String scope = "";
		String[] pathArray = requestPath.split("/", 6);
		if (pathArray.length > 5) {
			for (int i = 1; i < 5; i++) {
				scope += "/" + pathArray[i];
			}
		}
		return scope;
	}

	/**
	 * Checks if the provided vanity path is a valid redirect
	 *
	 * @param vanityPath Vanity path that needs to be validated.
	 * @param request    SlingHttpServletRequest object used for performing
	 *                   query/lookup
	 * @return return true if the vanityPath is a registered sling:vanityPath under
	 *         /content
	 */
	public static String getVanityPath(String vanityPath, SlingHttpServletRequest request) throws RepositoryException {
		final Resource vanityResource = request.getResourceResolver().resolve(vanityPath);
		LOGGER.debug("vanityPath:: -{}, Reosurce:: {}", vanityPath, vanityResource);
		if (vanityResource != null) {
			String targetPath = null;
			if (vanityResource.isResourceType("sling:redirect")) {
				targetPath = vanityResource.getValueMap().get("sling:target", String.class);
			} else if (!StringUtils.equals(vanityPath, vanityResource.getPath())) {
				targetPath = vanityResource.getPath();
			}
			final String pathScope = VanityUtil.getSiteScope(request.getRequestURI());
			LOGGER.debug("targetPath: {}, pathScope: {}", targetPath, pathScope);
			if (targetPath != null
					&& StringUtils.startsWith(targetPath, StringUtils.defaultIfEmpty(pathScope, "/content"))) {
				LOGGER.debug("Found vanity resource at [ {} ] for sling:vanityPath [ {} ]", targetPath, vanityPath);
				return targetPath;
			}
		}
		return StringUtils.EMPTY;
	}

}
