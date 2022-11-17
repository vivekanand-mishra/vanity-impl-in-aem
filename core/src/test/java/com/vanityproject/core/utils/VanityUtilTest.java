package com.vanityproject.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.resource.details.AssetDetails;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * VanityUtilTest Class
 */
@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class VanityUtilTest {

	AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

	public static final String NAVIGATION_TITLE = "Navigation Title";

	public static final String PAGE_TITLE = "Page Title";

	public static final String TITLE = "Title";

	/**
	 * path for project page
	 */
	public static final String PAGE_PATH = "/content/project/us/en/home.html";

	/** The Constant RESOURCE_CONTENT. */
	private static final String RESOURCE_CONTENT = "/pagemodel/test-page.json";

	/** The Constant TEST_CONTENT_ROOT. */
	private static final String TEST_CONTENT_ROOT = "/content/page";

	/** The Constant RESOURCE. */
	private static final String CURRENT_RESOURCE = TEST_CONTENT_ROOT;

	public static final String CHILD_RESOURCE = "/content/project/us/en/home/jcr:content/image";
	public static final String CHILD_RESOURCE_PATH = "/content/project/us/en/home/jcr:content/image/file.sftmp/jcr:content";
	public static final String PUBLISH_PAGE_PATH = "http://localhost:4503/content/project/us/en/home.html";
	public static final String FILE_REFERENCE = "/content/dam/project/2022/media/icons/agenda.png";
	public static final String IMAGE_NODE = "/content/project/us/en/home/jcr:content/image";
	public static final Long IMAGE_HEIGHT = 1650L;
	public static final Long IMAGE_WIDTH = 450L;
	public static final String PUBLISH_FILE_REFERENCE = "http://localhost:4503/content/dam/project/2022/media/cards/why-attend-best-practices.png";
	public static final String IMAGE_PATH = "/content/page/dam/agenda.png";
	public static final String PUBLISH_TWITTER_THUMBNAIL = "http://localhost:4503/content/page/dam/agenda.png";
	public static final String FACEBOOK_IMAGE = "/content/dam/project/2022/media/cards/connect-party.png";
	public static final String PUBLISH_FACEBOOK_IMAGE = "http://localhost:4503/content/dam/project/2022/media/cards/connect-party.png";
	public static final String FILE_PATH = "/content/project/us/en/home/jcr:content/image";

	private static final String PRIMARY_TYPE = "jcr:primaryType";

	private static final String SLING_REDIRECT = "sling:redirect";

	private static final String SLING_TARGET = "sling:target";

	private static final String RESOURCE_PATH = "/content/bar";

	@Mock
	Page page;

	@Mock
	protected ResourceResolver resourceResolver;
	@Mock
	Externalizer externalizer;

	@Mock
	private Resource resource;

	Resource childResource;

	@Mock
	ModifiableValueMap map;

	@Mock
	ValueMap valuemap;

	@Mock
	AssetDetails assetDetails;

	@Mock
	AssetDetails assetRes;

	@BeforeEach
	public void setup() throws Throwable {
		MockSlingHttpServletRequest request = aemContext.request();
		aemContext.load().json(RESOURCE_CONTENT, TEST_CONTENT_ROOT);
		aemContext.request().setPathInfo(TEST_CONTENT_ROOT);
		request.setResource(aemContext.resourceResolver().getResource(CURRENT_RESOURCE));
		resource = aemContext.currentResource(CURRENT_RESOURCE);
		childResource = resource.getChild("jcr:content/image");
	}

	@Test
	void testGetSiteScope() {
		assertEquals("/content/project/us/en", VanityUtil.getSiteScope(CHILD_RESOURCE));
	}

	@Test
	void testGetSiteScopeWhenDefault() {
		assertEquals("", VanityUtil.getSiteScope("/content/project"));
	}

	@Test
	void isVanityPath() throws RepositoryException {
		aemContext.build().resource("/foo", PRIMARY_TYPE, SLING_REDIRECT, SLING_TARGET, RESOURCE_PATH);
		assertEquals(RESOURCE_PATH, VanityUtil.getVanityPath("/foo", aemContext.request()));
	}

	@Test
	void htmlVanityPath() throws RepositoryException {
		aemContext.build().resource(PAGE_PATH, PRIMARY_TYPE, SLING_REDIRECT, SLING_TARGET, RESOURCE_PATH);
		assertEquals(RESOURCE_PATH, VanityUtil.getVanityPath(PAGE_PATH, aemContext.request()));
	}

	@Test
	void isVanityPath_OutsideOfPathScope() throws RepositoryException {
		aemContext.build().resource("/foo", PRIMARY_TYPE, SLING_REDIRECT, SLING_TARGET, "/bar");
		assertEquals("", VanityUtil.getVanityPath("/foo", aemContext.request()));
	}

	@Test
	void isVanityPath_NotRedirectResource() throws RepositoryException {
		// Redirect resources
		aemContext.build().resource("/foo", PRIMARY_TYPE, "nt:unstructured");
		assertEquals("", VanityUtil.getVanityPath("/foo", aemContext.request()));
	}

}
