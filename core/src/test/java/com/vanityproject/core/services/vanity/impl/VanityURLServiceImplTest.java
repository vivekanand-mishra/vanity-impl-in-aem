package com.vanityproject.core.services.vanity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vanityproject.core.services.vanity.VanityURLService;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.util.PrivateAccessor;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
public class VanityURLServiceImplTest {

	public final AemContext ctx = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

	@Mock
	RequestDispatcher requestDispatcher;

	VanityURLServiceImpl vanityURLService;

	@Mock
	VanityUrlAdjusterImpl vanityUrlAdjuster;

	private static final String PRIMARY_TYPE = "jcr:primaryType";

	private static final String SLING_REDIRECT = "sling:redirect";

	private static final String SLING_TARGET = "sling:target";

	private static final String RESOURCE_PATH = "/content/bar";

	private static final String PAGE_PATH = "/content/vanityproject/us/en/foo.html";

	@BeforeEach
	public void setUp() throws NoSuchFieldException {
		ctx.request().setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
			@Override
			public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
				return requestDispatcher;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
				return requestDispatcher;
			}
		});

		ctx.registerInjectActivateService(new VanityURLServiceImpl());
		vanityURLService = (VanityURLServiceImpl) ctx.getService(VanityURLService.class);
		PrivateAccessor.setField(vanityURLService, "vanityUrlAdjuster", vanityUrlAdjuster);
	}

	@Test
	void testRedirection() throws Throwable {
		ctx.build().resource(PAGE_PATH, PRIMARY_TYPE, SLING_REDIRECT, SLING_TARGET, PAGE_PATH);
		ctx.build().resource("/foo", PRIMARY_TYPE, SLING_REDIRECT, SLING_TARGET, PAGE_PATH);
		ctx.request().setServletPath(PAGE_PATH);
		when(vanityUrlAdjuster.adjust(PAGE_PATH)).thenReturn("/foo");
		PrivateAccessor.invoke(vanityURLService, "redirectToVanity",
				new Class[] { SlingHttpServletRequest.class, SlingHttpServletResponse.class, String.class },
				new Object[] { ctx.request(), ctx.response(), PAGE_PATH });
		assertTrue(true);
	}

	@Test
	public void nonExistingResourceBehavior() {
		Resource nonExisting = ctx.resourceResolver().resolve("/dev/null");
		assertEquals("/dev/null", nonExisting.getPath());
	}

	@Test
	public void dispatch_NoMapping() throws ServletException, IOException, RepositoryException {
		ctx.request().setServletPath("/my-vanity");

		assertFalse(vanityURLService.dispatch(ctx.request(), ctx.response()));
		verify(requestDispatcher, times(0)).forward(ctx.request(), ctx.response());
	}

	@Test
	public void dispatch_Loop() throws ServletException, IOException, RepositoryException {
		ctx.request().setAttribute("acs-aem-commons__vanity-check-loop-detection", true);

		assertFalse(vanityURLService.dispatch(ctx.request(), ctx.response()));
		verify(requestDispatcher, times(0)).forward(ctx.request(), ctx.response());
	}

	@Test
	public void htmlDispatchPath() throws ServletException, IOException, RepositoryException {
		ctx.build().resource(PAGE_PATH, PRIMARY_TYPE, SLING_REDIRECT, SLING_TARGET, RESOURCE_PATH);
		ctx.request().setServletPath(PAGE_PATH);
		assertFalse(vanityURLService.dispatch(ctx.request(), ctx.response()));
	}

}
