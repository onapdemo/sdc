/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.sdc.externalApis;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.rules.TestName;
import org.openecomp.sdc.be.dao.api.ActionStatus;
import org.openecomp.sdc.be.datatypes.enums.AssetTypeEnum;
import org.openecomp.sdc.be.datatypes.enums.ResourceTypeEnum;
import org.openecomp.sdc.be.model.DistributionStatusEnum;
import org.openecomp.sdc.be.model.Resource;
import org.openecomp.sdc.be.model.Service;
import org.openecomp.sdc.be.resources.data.auditing.AuditingActionEnum;
import org.openecomp.sdc.ci.tests.api.ComponentBaseTest;
import org.openecomp.sdc.ci.tests.datatypes.ArtifactReqDetails;
import org.openecomp.sdc.ci.tests.datatypes.ResourceAssetStructure;
import org.openecomp.sdc.ci.tests.datatypes.ServiceAssetStructure;
import org.openecomp.sdc.ci.tests.datatypes.enums.ArtifactTypeEnum;
import org.openecomp.sdc.ci.tests.datatypes.enums.ErrorInfo;
import org.openecomp.sdc.ci.tests.datatypes.enums.LifeCycleStatesEnum;
import org.openecomp.sdc.ci.tests.datatypes.enums.NormativeTypesEnum;
import org.openecomp.sdc.ci.tests.datatypes.enums.ResourceCategoryEnum;
import org.openecomp.sdc.ci.tests.datatypes.enums.ServiceCategoriesEnum;
import org.openecomp.sdc.ci.tests.datatypes.enums.UserRoleEnum;
import org.openecomp.sdc.ci.tests.datatypes.expected.ExpectedExternalAudit;
import org.openecomp.sdc.ci.tests.datatypes.http.RestResponse;
import org.openecomp.sdc.ci.tests.utils.Utils;
import org.openecomp.sdc.ci.tests.utils.general.AtomicOperationUtils;
import org.openecomp.sdc.ci.tests.utils.general.ElementFactory;
import org.openecomp.sdc.ci.tests.utils.rest.ArtifactRestUtils;
import org.openecomp.sdc.ci.tests.utils.rest.AssetRestUtils;
import org.openecomp.sdc.ci.tests.utils.rest.BaseRestUtils;
import org.openecomp.sdc.ci.tests.utils.rest.ServiceRestUtils;
import org.openecomp.sdc.ci.tests.utils.validation.AuditValidationUtils;
import org.openecomp.sdc.ci.tests.utils.validation.ErrorValidationUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GetFilteredAssetServlet extends ComponentBaseTest {

	protected final static String categoryFilterKey = "category";
	protected final static String subCategoryFilterKey = "subCategory";
	protected final static String distributionStatusFilterKey = "distributionStatus";
	protected final static String serviceKey = "service";
	protected final static String resourceKey = "resource";

	@Rule
	public static TestName name = new TestName();

	public GetFilteredAssetServlet() {
		super(name, GetFilteredAssetServlet.class.getName());
	}

	@BeforeMethod
	public void setup() throws Exception {
		AtomicOperationUtils.createDefaultConsumer(true);
	}

	// RESOURCE
	// Success

	@Test // (enabled = false)
	public void getResourceAssetBySpecifiedCategory() throws Exception {
		String[] filter = { categoryFilterKey + "=" + ResourceCategoryEnum.GENERIC_ABSTRACT.getCategory() };
		List<String> expectedAssetNamesList = new ArrayList<>();

		Resource resource1 = AtomicOperationUtils.createResourcesByTypeNormTypeAndCatregory(ResourceTypeEnum.VF, NormativeTypesEnum.COMPUTE, ResourceCategoryEnum.GENERIC_ABSTRACT, UserRoleEnum.DESIGNER, true).left().value();
		expectedAssetNamesList.add(resource1.getName());
		Resource resource2 = AtomicOperationUtils.createResourcesByTypeNormTypeAndCatregory(ResourceTypeEnum.VF, NormativeTypesEnum.DATABASE, ResourceCategoryEnum.GENERIC_NETWORK_ELEMENTS, UserRoleEnum.DESIGNER, true).left().value();
		expectedAssetNamesList.add(resource2.getName());
		Resource resource3 = AtomicOperationUtils.createResourcesByTypeNormTypeAndCatregory(ResourceTypeEnum.VF, NormativeTypesEnum.COMPUTE, ResourceCategoryEnum.NETWORK_CONNECTIVITY_CON_POINT, UserRoleEnum.DESIGNER, true).left().value();

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.RESOURCES, filter);
		BaseRestUtils.checkSuccess(assetResponse);

		List<ResourceAssetStructure> resourceAssetList = AssetRestUtils.getResourceAssetList(assetResponse);
		List<String> getActualAssetNamesList = AssetRestUtils.getResourceNamesList(resourceAssetList);
		Utils.compareArrayLists(getActualAssetNamesList, expectedAssetNamesList, "Element");

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.RESOURCES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	// 2 resources has the same category and different subcategory
	@Test // (enabled = false)
	public void getResourceAssetBySpecifiedCategoryAndSubCategory() throws Exception {
		String[] filter = { categoryFilterKey + "=" + ResourceCategoryEnum.GENERIC_ABSTRACT.getCategory(), subCategoryFilterKey + "=" + ResourceCategoryEnum.GENERIC_ABSTRACT.getSubCategory() };
		// String[] filter2 = {categoryFilterKey + "=" +
		// ResourceCategoryEnum.GENERIC_NETWORK_ELEMENTS.getCategory(),
		// subCategoryFilterKey + "=" +
		// ResourceCategoryEnum.GENERIC_NETWORK_ELEMENTS.getSubCategory()};
		List<String> expectedAssetNamesList = new ArrayList<>();

		Resource resource1 = AtomicOperationUtils.createResourcesByTypeNormTypeAndCatregory(ResourceTypeEnum.VF, NormativeTypesEnum.COMPUTE, ResourceCategoryEnum.GENERIC_ABSTRACT, UserRoleEnum.DESIGNER, true).left().value();
		expectedAssetNamesList.add(resource1.getName());
		Resource resource2 = AtomicOperationUtils.createResourcesByTypeNormTypeAndCatregory(ResourceTypeEnum.VF, NormativeTypesEnum.DATABASE, ResourceCategoryEnum.GENERIC_NETWORK_ELEMENTS, UserRoleEnum.DESIGNER, true).left().value();

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.RESOURCES, filter);
		BaseRestUtils.checkSuccess(assetResponse);

		List<ServiceAssetStructure> resourceAssetList = AssetRestUtils.getServiceAssetList(assetResponse);
		List<String> getActualAssetNamesList = AssetRestUtils.getServiceNamesList(resourceAssetList);
		Utils.compareArrayLists(getActualAssetNamesList, expectedAssetNamesList, "Element");

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.RESOURCES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getResourceAssetBySpecifiedSubCategoryAndCategory() throws Exception {
		String[] filter = { subCategoryFilterKey + "=" + ResourceCategoryEnum.GENERIC_DATABASE.getSubCategory(), categoryFilterKey + "=" + ResourceCategoryEnum.GENERIC_DATABASE.getCategory(), };
		List<String> expectedAssetNamesList = new ArrayList<>();

		Resource resource1 = AtomicOperationUtils.createResourcesByTypeNormTypeAndCatregory(ResourceTypeEnum.VF, NormativeTypesEnum.COMPUTE, ResourceCategoryEnum.GENERIC_ABSTRACT, UserRoleEnum.DESIGNER, true).left().value();
		Resource resource2 = AtomicOperationUtils.createResourcesByTypeNormTypeAndCatregory(ResourceTypeEnum.VF, NormativeTypesEnum.DATABASE, ResourceCategoryEnum.GENERIC_DATABASE, UserRoleEnum.DESIGNER, true).left().value();
		expectedAssetNamesList.add(resource2.getName());

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.RESOURCES, filter);
		BaseRestUtils.checkSuccess(assetResponse);

		List<ServiceAssetStructure> resourceAssetList = AssetRestUtils.getServiceAssetList(assetResponse);
		List<String> getActualAssetNamesList = AssetRestUtils.getServiceNamesList(resourceAssetList);
		Utils.compareArrayLists(getActualAssetNamesList, expectedAssetNamesList, "Element");

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.RESOURCES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	// Failure
	@Test // (enabled = false)
	public void getResourceAssetCategoryNotExists() throws Exception {
		String[] filter = { categoryFilterKey + "=" + "NotExistingCategory" };

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.RESOURCES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList(resourceKey, categoryFilterKey, "NotExistingCategory");
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.RESOURCES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getResourceAssetSubCategoryNotExists() throws Exception {
		String[] filter = { categoryFilterKey + "=" + ResourceCategoryEnum.GENERIC_ABSTRACT.getCategory(), subCategoryFilterKey + "=" + "NotExistingSubCategory" };

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.RESOURCES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.COMPONENT_SUB_CATEGORY_NOT_FOUND_FOR_CATEGORY.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList("Resource", "NotExistingSubCategory", ResourceCategoryEnum.GENERIC_ABSTRACT.getCategory());
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.COMPONENT_SUB_CATEGORY_NOT_FOUND_FOR_CATEGORY.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.RESOURCES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getResourceAssetCategoryNotExistsSubCategoryExists() throws Exception {
		String[] filter = { subCategoryFilterKey + "=" + ResourceCategoryEnum.NETWORK_L2_3_GETEWAY.getSubCategory(), categoryFilterKey + "=" + "NotExistingCategory" };

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.RESOURCES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList(resourceKey, categoryFilterKey, "NotExistingCategory");
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.RESOURCES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getResourceAssetInvalidFilterKey() throws Exception {
		String[] filter = { subCategoryFilterKey + "1=" + ResourceCategoryEnum.NETWORK_L2_3_GETEWAY.getSubCategory(), categoryFilterKey + "=" + "NotExistingCategory" };

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.RESOURCES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.INVALID_FILTER_KEY.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList(subCategoryFilterKey + "1", "[" + subCategoryFilterKey + ", " + categoryFilterKey + "]");
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.INVALID_FILTER_KEY.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.RESOURCES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	// ---------------------------------------------------------------------------------------------------------
	// SERVICE
	// Success
	@Test // (enabled = false)
	public void getServiceAssetBySpecifiedCategory() throws Exception {
		String[] filter = { categoryFilterKey + "=" + ServiceCategoriesEnum.MOBILITY.getValue() };
		List<String> expectedAssetNamesList = new ArrayList<>();

		Service service1 = AtomicOperationUtils.createServiceByCategory(ServiceCategoriesEnum.MOBILITY, UserRoleEnum.DESIGNER, true).left().value();
		expectedAssetNamesList.add(service1.getName());

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.SERVICES, filter);
		BaseRestUtils.checkSuccess(assetResponse);

		List<ServiceAssetStructure> serviceAssetList = AssetRestUtils.getServiceAssetList(assetResponse);
		List<String> getActualAssetNamesList = AssetRestUtils.getServiceNamesList(serviceAssetList);
		Utils.compareArrayLists(getActualAssetNamesList, expectedAssetNamesList, "Element");

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.SERVICES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);

	}

	@Test // (enabled = false)
	public void getServiceAssetBySpecifiedCategoryAndDistributionStatus() throws Exception {
		String[] filter = { categoryFilterKey + "=" + ServiceCategoriesEnum.MOBILITY.getValue(), distributionStatusFilterKey + "=" + DistributionStatusEnum.DISTRIBUTION_NOT_APPROVED };
		List<String> expectedAssetNamesList = new ArrayList<>();
		ArtifactReqDetails artifactDetails = ElementFactory.getArtifactByType(ArtifactTypeEnum.OTHER, ArtifactTypeEnum.OTHER, true);

		Service service1 = AtomicOperationUtils.createServiceByCategory(ServiceCategoriesEnum.MOBILITY, UserRoleEnum.DESIGNER, true).left().value();
		expectedAssetNamesList.add(service1.getName());
		Service service2 = AtomicOperationUtils.createServiceByCategory(ServiceCategoriesEnum.MOBILITY, UserRoleEnum.DESIGNER, true).left().value();
		RestResponse addInformationalArtifactToService = ArtifactRestUtils.addInformationalArtifactToService(artifactDetails, ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER), service2.getUniqueId());
		BaseRestUtils.checkSuccess(addInformationalArtifactToService);
		service2 = (Service) AtomicOperationUtils.changeComponentState(service2, UserRoleEnum.DESIGNER, LifeCycleStatesEnum.CERTIFY, true).getLeft();
		expectedAssetNamesList.add(service2.getName());

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.SERVICES, filter);
		BaseRestUtils.checkSuccess(assetResponse);

		List<ServiceAssetStructure> serviceAssetList = AssetRestUtils.getServiceAssetList(assetResponse);
		List<String> getActualAssetNamesList = AssetRestUtils.getServiceNamesList(serviceAssetList);
		Utils.compareArrayLists(getActualAssetNamesList, expectedAssetNamesList, "Element");

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.SERVICES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getServiceAssetByDistributionStatus() throws Exception {
		String[] filter = { distributionStatusFilterKey + "=" + DistributionStatusEnum.DISTRIBUTION_NOT_APPROVED };
		List<String> expectedAssetNamesList = new ArrayList<>();
		ArtifactReqDetails artifactDetails = ElementFactory.getArtifactByType(ArtifactTypeEnum.OTHER, ArtifactTypeEnum.OTHER, true);

		Service service1 = AtomicOperationUtils.createServiceByCategory(ServiceCategoriesEnum.MOBILITY, UserRoleEnum.DESIGNER, true).left().value();
		expectedAssetNamesList.add(service1.getName());
		Service service2 = AtomicOperationUtils.createServiceByCategory(ServiceCategoriesEnum.MOBILITY, UserRoleEnum.DESIGNER, true).left().value();
		RestResponse addInformationalArtifactToService = ArtifactRestUtils.addInformationalArtifactToService(artifactDetails, ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER), service2.getUniqueId());
		BaseRestUtils.checkSuccess(addInformationalArtifactToService);
		service2 = (Service) AtomicOperationUtils.changeComponentState(service2, UserRoleEnum.DESIGNER, LifeCycleStatesEnum.CERTIFY, true).getLeft();
		ServiceRestUtils.approveServiceDistribution(service2.getUniqueId(), UserRoleEnum.GOVERNOR.getUserId());

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.SERVICES, filter);
		BaseRestUtils.checkSuccess(assetResponse);

		List<ServiceAssetStructure> serviceAssetList = AssetRestUtils.getServiceAssetList(assetResponse);
		List<String> getActualAssetNamesList = AssetRestUtils.getServiceNamesList(serviceAssetList);
		Utils.compareArrayLists(getActualAssetNamesList, expectedAssetNamesList, "Element");

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.SERVICES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	// Failure
	@Test // (enabled = false)
	public void getServiceAssetCategoryNotExists() throws Exception {
		String[] filter = { categoryFilterKey + "=" + "NotExistingCategory" };

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.SERVICES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList(serviceKey, categoryFilterKey, "NotExistingCategory");
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.SERVICES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getServiceAssetInvalidDistributionStatus() throws Exception {
		String[] filter = { distributionStatusFilterKey + "=" + "NotExistingDistributionStatus" };
		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.SERVICES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList(serviceKey, distributionStatusFilterKey, "NotExistingDistributionStatus");
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.SERVICES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getServiceAssetCategoryExitsDistributionStatusInvalid() throws Exception {
		String[] filter = { categoryFilterKey + "=" + ServiceCategoriesEnum.MOBILITY.getValue(), distributionStatusFilterKey + "=" + "NotExistingDistributionStatus" };

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.SERVICES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList(serviceKey, distributionStatusFilterKey, "NotExistingDistributionStatus");
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.SERVICES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getServiceAssetCategoryNotExistsDistributionStatus() throws Exception {
		String[] filter = { distributionStatusFilterKey + "=" + DistributionStatusEnum.DISTRIBUTION_NOT_APPROVED, categoryFilterKey + "=" + "NotExistingCategory" };

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.SERVICES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList(serviceKey, categoryFilterKey, "NotExistingCategory");
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.COMPONENT_CATEGORY_NOT_FOUND.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.SERVICES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);
	}

	@Test // (enabled = false)
	public void getServiceAssetInvalidFilterKey() throws Exception {
		String[] filter = { distributionStatusFilterKey + "1=" + DistributionStatusEnum.DISTRIBUTION_NOT_APPROVED, categoryFilterKey + "=" + ServiceCategoriesEnum.MOBILITY.getValue() };

		RestResponse assetResponse = AssetRestUtils.getComponentListByAssetType(true, AssetTypeEnum.SERVICES, filter);
		ErrorInfo errorInfo = ErrorValidationUtils.parseErrorConfigYaml(ActionStatus.INVALID_FILTER_KEY.name());

		assertNotNull("check response object is not null after create resouce", assetResponse);
		assertNotNull("check error code exists in response after create resource", assetResponse.getErrorCode());
		assertEquals("Check response code after create resource", errorInfo.getCode(), assetResponse.getErrorCode());

		List<String> variables = Arrays.asList(distributionStatusFilterKey + "1", "[" + categoryFilterKey + ", " + distributionStatusFilterKey + "]");
		ErrorValidationUtils.checkBodyResponseOnError(ActionStatus.INVALID_FILTER_KEY.name(), variables, assetResponse.getResponse());

		// Validate audit message
		ExpectedExternalAudit expectedAssetListAudit = ElementFactory.getDefaultAssetListAudit(AssetTypeEnum.SERVICES, AuditingActionEnum.GET_FILTERED_ASSET_LIST);
		expectedAssetListAudit.setRESOURCE_URL(AssetRestUtils.buildUrlWithFilter(expectedAssetListAudit.getRESOURCE_URL(), filter));
		expectedAssetListAudit.setSTATUS(errorInfo.getCode().toString());
		expectedAssetListAudit.setDESC(AuditValidationUtils.buildAuditDescription(errorInfo, variables));
		AuditValidationUtils.validateExternalAudit(expectedAssetListAudit, AuditingActionEnum.GET_FILTERED_ASSET_LIST.getName(), null);

	}
}
