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

package org.openecomp.sdc.ci.tests.execute.sanity;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openecomp.sdc.ci.tests.dataProvider.OnbordingDataProviders;
import org.openecomp.sdc.ci.tests.datatypes.AmdocsLicenseMembers;
import org.openecomp.sdc.ci.tests.datatypes.CanvasElement;
import org.openecomp.sdc.ci.tests.datatypes.CanvasManager;
import org.openecomp.sdc.ci.tests.datatypes.ServiceReqDetails;
import org.openecomp.sdc.ci.tests.datatypes.enums.UserRoleEnum;
import org.openecomp.sdc.ci.tests.execute.setup.ArtifactsCorrelationManager;
import org.openecomp.sdc.ci.tests.execute.setup.ExtentTestActions;
import org.openecomp.sdc.ci.tests.execute.setup.SetupCDTest;
import org.openecomp.sdc.ci.tests.pages.CompositionPage;
import org.openecomp.sdc.ci.tests.pages.DeploymentArtifactPage;
import org.openecomp.sdc.ci.tests.pages.GovernorOperationPage;
import org.openecomp.sdc.ci.tests.pages.HomePage;
import org.openecomp.sdc.ci.tests.pages.OpsOperationPage;
import org.openecomp.sdc.ci.tests.pages.ResourceGeneralPage;
import org.openecomp.sdc.ci.tests.pages.ServiceGeneralPage;
import org.openecomp.sdc.ci.tests.pages.TesterOperationPage;
import org.openecomp.sdc.ci.tests.utilities.FileHandling;
import org.openecomp.sdc.ci.tests.utilities.GeneralUIUtils;
import org.openecomp.sdc.ci.tests.utilities.OnboardingUtils;
import org.openecomp.sdc.ci.tests.utilities.ServiceUIUtils;
import org.openecomp.sdc.ci.tests.utils.general.ElementFactory;
import org.openecomp.sdc.ci.tests.verificator.ServiceVerificator;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.clearspring.analytics.util.Pair;

public class Onboard extends SetupCDTest {
	
	protected static String filepath = FileHandling.getVnfRepositoryPath();
	protected String makeDistributionValue;
	
	@Parameters({ "makeDistribution" })
	@BeforeMethod
	public void beforeTestReadParams(@Optional("true") String makeDistributionReadValue) {
		makeDistributionValue = makeDistributionReadValue;                             
	}
	
	@Test
	public void onboardVNFTestSanity() throws Exception, Throwable {
		List<String> fileNamesFromFolder = OnboardingUtils.getVnfNamesFileList();
		String vnfFile = fileNamesFromFolder.get(0).toString();
		runOnboardToDistributionFlow(filepath, vnfFile);
	}

	
	public void runOnboardToDistributionFlow(String filepath, String vnfFile) throws Exception, AWTException {
		String vspName = onboardAndCertify(filepath, vnfFile);

		reloginWithNewRole(UserRoleEnum.DESIGNER);
		// create service
		ServiceReqDetails serviceMetadata = ElementFactory.getDefaultService();
		ServiceUIUtils.createService(serviceMetadata, getUser());

		ServiceGeneralPage.getLeftMenu().moveToCompositionScreen();
		CompositionPage.searchForElement(vspName);
		CanvasManager serviceCanvasManager = CanvasManager.getCanvasManager();
		CanvasElement vfElement = serviceCanvasManager.createElementOnCanvas(vspName);
		ArtifactsCorrelationManager.addVNFtoServiceArtifactCorrelation(serviceMetadata.getName(), vspName);
		
		assertNotNull(vfElement);
		ServiceVerificator.verifyNumOfComponentInstances(serviceMetadata, "0.1", 1, getUser());
		ExtentTestActions.addScreenshot(Status.INFO, "ServiceComposition_" + vnfFile ,"The service topology is as follows: ");

		ServiceGeneralPage.clickSubmitForTestingButton(serviceMetadata.getName());

		reloginWithNewRole(UserRoleEnum.TESTER);
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		TesterOperationPage.certifyComponent(serviceMetadata.getName());

		reloginWithNewRole(UserRoleEnum.GOVERNOR);
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		GovernorOperationPage.approveSerivce(serviceMetadata.getName());

		if (makeDistributionValue.equals("true")){
			
		
		reloginWithNewRole(UserRoleEnum.OPS);
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		OpsOperationPage.distributeService();
		OpsOperationPage.displayMonitor();

		List<WebElement> rowsFromMonitorTable = OpsOperationPage.getRowsFromMonitorTable();
		AssertJUnit.assertEquals(1, rowsFromMonitorTable.size());

		OpsOperationPage.waitUntilArtifactsDistributed(0);
		
//		validateInputArtsVSouput(serviceMetadata.getName());

		}
		
		getExtendTest().log(Status.INFO, String.format("The onboarding %s test is passed ! ", vnfFile));
	}

	public String onboardAndCertify(String filepath, String vnfFile) throws Exception, IOException {
		Pair<String,Map<String,String>> onboardAndValidate = OnboardingUtils.onboardAndValidate(filepath, vnfFile, getUser());
		String vspName = onboardAndValidate.left;
		
		DeploymentArtifactPage.getLeftPanel().moveToCompositionScreen();
		ExtentTestActions.addScreenshot(Status.INFO, "TopologyTemplate_" + vnfFile ,"The topology template for " + vnfFile + " is as follows : ");
		
		DeploymentArtifactPage.clickSubmitForTestingButton(vspName);

		reloginWithNewRole(UserRoleEnum.TESTER);
		GeneralUIUtils.findComponentAndClick(vspName);
		TesterOperationPage.certifyComponent(vspName);
		return vspName;
	}
	
	
	@Test(dataProviderClass = OnbordingDataProviders.class, dataProvider = "VNF_List")
	public void onboardVNFTest(String filepath, String vnfFile) throws Exception, Throwable {
		setLog(vnfFile);
		System.out.println("printttttttttttttt - >" + makeDistributionValue);
		runOnboardToDistributionFlow(filepath, vnfFile);
	}
	
	@Test(dataProviderClass = OnbordingDataProviders.class, dataProvider = "VNF_List")
	public void onboardVNFShotFlow(String filepath, String vnfFile) throws Exception, Throwable {
		setLog(vnfFile);
		System.out.println("printttttttttttttt - >" + makeDistributionValue);
		onboardAndCertify(filepath, vnfFile);
	}

	@Test(dataProviderClass = OnbordingDataProviders.class, dataProvider = "randomVNF_List")
	public void onboardRandomVNFsTest(String filepath, String vnfFile) throws Exception, Throwable {
		setLog(vnfFile);
		System.out.println("printttttttttttttt - >" + makeDistributionValue);
		System.out.println("Vnf File name is: " + vnfFile);
		runOnboardToDistributionFlow(filepath, vnfFile);
	}
	
	
	@Test
	public void onboardUpdateVNFTest() throws Exception, Throwable {
//		Object[] fileNamesFromFolder = FileHandling.getZipFileNamesFromFolder(filepath);
		List<String> fileNamesFromFolder = FileHandling.getZipFileNamesFromFolder(filepath);
//		String vnfFile = fileNamesFromFolder[0].toString();
		String vnfFile = fileNamesFromFolder.get(0);
		
		Pair<String,Map<String,String>> vsp = OnboardingUtils.onboardAndValidate(filepath, vnfFile, getUser());
		String vspName = vsp.left;
		ResourceGeneralPage.clickSubmitForTestingButton(vspName);

		reloginWithNewRole(UserRoleEnum.TESTER);
		GeneralUIUtils.findComponentAndClick(vspName);
		TesterOperationPage.certifyComponent(vspName);

		reloginWithNewRole(UserRoleEnum.DESIGNER);
		// create service
		ServiceReqDetails serviceMetadata = ElementFactory.getDefaultService();
		ServiceUIUtils.createService(serviceMetadata, getUser());

		ServiceGeneralPage.getLeftMenu().moveToCompositionScreen();
		CompositionPage.searchForElement(vspName);
		CanvasManager serviceCanvasManager = CanvasManager.getCanvasManager();
		CanvasElement vfElement = serviceCanvasManager.createElementOnCanvas(vspName);
		assertNotNull(vfElement);
		ServiceVerificator.verifyNumOfComponentInstances(serviceMetadata, "0.1", 1, getUser());

		HomePage.navigateToHomePage();
		
		///update flow
//		String updatedVnfFile = fileNamesFromFolder[1].toString();
		String updatedVnfFile = fileNamesFromFolder.get(1);

		getExtendTest().log(Status.INFO, String.format("Going to update the VNF with %s......", updatedVnfFile));
		// update VendorSoftwareProduct
		OnboardingUtils.updateVnfAndValidate(filepath, vsp, updatedVnfFile, getUser());
		
		ResourceGeneralPage.clickSubmitForTestingButton(vspName);

		reloginWithNewRole(UserRoleEnum.TESTER);
		GeneralUIUtils.findComponentAndClick(vspName);
		TesterOperationPage.certifyComponent(vspName);

		reloginWithNewRole(UserRoleEnum.DESIGNER);
		
		// replace exiting VFI in service with new updated
		
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		ServiceGeneralPage.getLeftMenu().moveToCompositionScreen();
		serviceCanvasManager = CanvasManager.getCanvasManager();
		CompositionPage.changeComponentVersion(serviceCanvasManager, vfElement, "2.0");
		ServiceVerificator.verifyNumOfComponentInstances(serviceMetadata, "0.1", 1, getUser());

		ServiceGeneralPage.clickSubmitForTestingButton(serviceMetadata.getName());

		reloginWithNewRole(UserRoleEnum.TESTER);
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		TesterOperationPage.certifyComponent(serviceMetadata.getName());

		reloginWithNewRole(UserRoleEnum.GOVERNOR);
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		GovernorOperationPage.approveSerivce(serviceMetadata.getName());
		

			
				reloginWithNewRole(UserRoleEnum.OPS);
				GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
				OpsOperationPage.distributeService();
				OpsOperationPage.displayMonitor();
		
				List<WebElement> rowsFromMonitorTable = OpsOperationPage.getRowsFromMonitorTable();
				AssertJUnit.assertEquals(1, rowsFromMonitorTable.size());
		
				OpsOperationPage.waitUntilArtifactsDistributed(0);
		
		
		getExtendTest().log(Status.INFO, String.format("Onboarding %s test is passed ! ", vnfFile));
		
		
	}

	@Test
	public void threeVMMSCsInServiceTest() throws Exception{
		
		List<String> vmmscList = new ArrayList<String>();
		vmmscList = Arrays.asList(new File(filepath).list()).stream().filter(e -> e.contains("vmmsc") && e.endsWith(".zip")).collect(Collectors.toList());
		assertTrue("Did not find vMMSCs", vmmscList.size() > 0);
		
		Map<String, String> vspNames = new HashMap<String, String>(); 
		for (String vnfFile : vmmscList){
			getExtendTest().log(Status.INFO, String.format("Going to onboard the VNF %s......", vnfFile));
			System.out.println(String.format("Going to onboard the VNF %s......", vnfFile));

			AmdocsLicenseMembers amdocsLicenseMembers = OnboardingUtils.createVendorLicense(getUser());
			Pair<String,Map<String,String>> createVendorSoftwareProduct = OnboardingUtils.createVendorSoftwareProduct(vnfFile, filepath, getUser(), amdocsLicenseMembers);

			getExtendTest().log(Status.INFO, String.format("Searching for onboarded %s", vnfFile));
			HomePage.showVspRepository();
			getExtendTest().log(Status.INFO,String.format("Going to import %s......", vnfFile.substring(0, vnfFile.indexOf("."))));
			OnboardingUtils.importVSP(createVendorSoftwareProduct);
			
			ResourceGeneralPage.getLeftMenu().moveToDeploymentArtifactScreen();
			DeploymentArtifactPage.verifyArtifactsExistInTable(filepath, vnfFile);
			
			String vspName = createVendorSoftwareProduct.left;
			DeploymentArtifactPage.clickSubmitForTestingButton(vspName);
			
			vspNames.put(vnfFile, vspName);
		}
		
		reloginWithNewRole(UserRoleEnum.TESTER);
		for (String vsp : vspNames.values()){
			GeneralUIUtils.findComponentAndClick(vsp);
			TesterOperationPage.certifyComponent(vsp);
		}
		
		reloginWithNewRole(UserRoleEnum.DESIGNER);
		// create service
		ServiceReqDetails serviceMetadata = ElementFactory.getDefaultService();
		ServiceUIUtils.createService(serviceMetadata, getUser());
		ServiceGeneralPage.getLeftMenu().moveToCompositionScreen();
		CanvasManager serviceCanvasManager = CanvasManager.getCanvasManager();

		for (String vsp : vspNames.values()){
			CompositionPage.searchForElement(vsp);
			CanvasElement vfElement = serviceCanvasManager.createElementOnCanvas(vsp);
			assertNotNull(vfElement);
		}
		ServiceVerificator.verifyNumOfComponentInstances(serviceMetadata, "0.1", vspNames.values().size(), getUser());
		File imageFilePath = GeneralUIUtils.takeScreenshot(null, SetupCDTest.getScreenshotFolder(), "Info_" + getExtendTest().getModel().getName());
		final String absolutePath = new File(SetupCDTest.getReportFolder()).toURI().relativize(imageFilePath.toURI()).getPath();
		SetupCDTest.getExtendTest().log(Status.INFO, "Three kinds of vMMSC are in canvas now." + getExtendTest().addScreenCaptureFromPath(absolutePath));
		
		ServiceGeneralPage.clickSubmitForTestingButton(serviceMetadata.getName());

		reloginWithNewRole(UserRoleEnum.TESTER);
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		TesterOperationPage.certifyComponent(serviceMetadata.getName());

		reloginWithNewRole(UserRoleEnum.GOVERNOR);
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		GovernorOperationPage.approveSerivce(serviceMetadata.getName());

		reloginWithNewRole(UserRoleEnum.OPS);
		GeneralUIUtils.findComponentAndClick(serviceMetadata.getName());
		OpsOperationPage.distributeService();
		OpsOperationPage.displayMonitor();

		List<WebElement> rowsFromMonitorTable = OpsOperationPage.getRowsFromMonitorTable();
		AssertJUnit.assertEquals(1, rowsFromMonitorTable.size());

		OpsOperationPage.waitUntilArtifactsDistributed(0);

	}
	
	
	@Override
	protected UserRoleEnum getRole() {
		return UserRoleEnum.DESIGNER;
	}

}
