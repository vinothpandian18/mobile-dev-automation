# Automation Mobile Framework
## Execution Instruction

The Mobile Automation scripts can be executed for local real devices, emulator through Command Line Interface(CLI)
Also, the Script can be customized by the following parameters ("," separated for each device)

	1. Mobile Device: Each mobile device’s parameters are saved in a properties file in /src/test/resources/MobileDevices directory. The file name is the same as the parameter value in mobileDevice Property. For each device, set devicetype property to be either local or cloud. Example: -DmobileDevice=ipad,nexus,iphonex

	2. Environment: mobileEnv property is used to select the mobile app and test environment. This property can be set to qa or production environment. Below is an example for setting the test app environment to qa. Exmaple: -DmobileEnv=qa
	
	3. Test groups: Test cases can be executed in groups by using tags and features. If not select any tag or feature, all test cases in the whole test suite will be executed.

**Example for running test using tags: -Dcucumber.filter.tags="@smoke,@P1"**




**Example for running test using features: -Dcucumber.features="{resources path}/features/sign_in.feature"**

_**`"-Dtest=MobileRunner
-DmobileDevice=emulator
-DmobileEnv=qa
-Dcucumber.features=src/test/resources/Mobile/amway_features/swaglab_homepage.feature"`**_




## Command Line Examples
1. Run smoke tests on one device – ipad, and in qa environment
		`mvn test -Dtest=MobileRunner -DmobileDevice=ipad -DmobileEnv=qa2 -Dcucumber.filter.tags="@smoke"`
2. Run P1 tests on 5 devices – ipad, nexus, emulator, iphoneXR, samsungs20 in production environment
    `mvn test -Dtest=CustomizeRunner -DmobileDevice= ipad, nexus, emulator, iphonex, samsungs8 
		-DmobileEnv=prod Dcucumber.filter.tags="@P1"`
