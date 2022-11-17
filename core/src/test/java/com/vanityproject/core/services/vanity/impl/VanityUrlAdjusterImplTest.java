package com.vanityproject.core.services.vanity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.vanityproject.core.services.vanity.VanityUrlAdjuster;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class VanityUrlAdjusterImplTest {

	public final AemContext ctx = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

	VanityUrlAdjusterImpl vanityURLAdjuster;

	@Test
	void test() {
		ctx.registerInjectActivateService(new VanityUrlAdjusterImpl());
		vanityURLAdjuster = (VanityUrlAdjusterImpl) ctx.getService(VanityUrlAdjuster.class);
		assertEquals("vanityPath/", vanityURLAdjuster.adjust("/content/vanityproject/us/en/vanityPath.html"));
		assertEquals("vanityPath/", vanityURLAdjuster.adjust("/content/vanityproject/us/en/vanityPath/"));
		assertEquals("project/vanityPath/project/", vanityURLAdjuster.adjust("/project/vanityPath/project/"));
	}

}
