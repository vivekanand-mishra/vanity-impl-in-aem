package com.vanityproject.core.services.vanity.impl;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vanityproject.core.services.vanity.VanityURLService;
import com.vanityproject.core.services.vanity.VanityUrlAdjuster;
import com.vanityproject.core.utils.VanityUtil;
import com.day.cq.commons.PathInfo;

/**
 * Imported from ACS AEM Commons and customized as per application need
 *
 */
@Component(service = VanityURLService.class)
public class VanityURLServiceImpl implements VanityURLService {

	private static final Logger log = LoggerFactory.getLogger(VanityURLServiceImpl.class);

	private static final String VANITY_DISPATCH_CHECK_ATTR = "acs-aem-commons__vanity-check-loop-detection";

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	private volatile VanityUrlAdjuster vanityUrlAdjuster;

	public boolean dispatch(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException, RepositoryException {
		if (request.getAttribute(VANITY_DISPATCH_CHECK_ATTR) != null) {
			log.trace("Processing a previously vanity dispatched request. Skipping...");
			return false;
		}

		request.setAttribute(VANITY_DISPATCH_CHECK_ATTR, true);

		// new PathInfo(..) will try to perform a rr.map(..) on requestUri as long as it
		// can be rr.resolved(..)
		final PathInfo pathInfo = new PathInfo(request.getResourceResolver(), request.getRequestURI());
		if (!("html".equals(pathInfo.getExtension()) && request.getRequestURI().startsWith("/content/"))) {
			// Handle only HTML calls
			return false;
		}
		return redirectToVanity(request, response, pathInfo.getResourcePath());
	}

	private Boolean redirectToVanity(SlingHttpServletRequest request, SlingHttpServletResponse response,
			String candidateVanity) throws RepositoryException, ServletException, IOException {
		// This check mirrors the check in new PathInfo(..); if resolving the requestUri
		candidateVanity = request.getResourceResolver().map(request, candidateVanity);
		String requestUri = request.getRequestURI();
		log.debug("Generated Candidate Vanity URL by mapping of [ {} -> {} ]", requestUri, candidateVanity);
		if (vanityUrlAdjuster != null) {
			candidateVanity = vanityUrlAdjuster.adjust(candidateVanity);
			log.debug("Custom adjustment of candidate vanity [ {} ]", candidateVanity);
		}
		String redirectPath = VanityUtil.getVanityPath(candidateVanity, request);
		if (!StringUtils.equals(candidateVanity, requestUri) && !redirectPath.isEmpty()) {
			log.debug("Forwarding request to vanity resource [ {} ]", redirectPath);
			final RequestDispatcher requestDispatcher = request.getRequestDispatcher(redirectPath);
			requestDispatcher.forward(request, response);
			return true;
		}
		return false;
	}

}
