package com.vanityproject.core.services.vanity.impl;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.vanityproject.core.services.vanity.VanityUrlAdjuster;
import com.vanityproject.core.utils.VanityUtil;

/**
 * URL Adjuster
 *
 */
@Component(service = VanityUrlAdjuster.class)
public class VanityUrlAdjusterImpl implements VanityUrlAdjuster {

	public String adjust(String vanityUrl) {
		String vanityPath = StringUtils.substringAfter(vanityUrl, "/");
		String scope = VanityUtil.getSiteScope(vanityUrl);
		if (!scope.isEmpty()) {
			vanityPath = vanityUrl.replace(VanityUtil.getSiteScope(vanityUrl) + "/", "");
		}
		if (!vanityPath.endsWith("/")) {
			// Vanity path should always end with a "/"
			vanityPath = vanityPath.concat("/");
		}
		return vanityPath.replace(".html", "");
	}
}
