package com.automation.mobile;

import com.automation.mobile.entities.FileLocations;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestNGGenerator {
    TestNG newTestNG = new TestNG();
    public TestNGGenerator(String testType, int threadCount) throws Exception {

        if (testType.equalsIgnoreCase("parallel")) {
            createParallelXml(threadCount);
        } else if (testType.equalsIgnoreCase("distribute")) {
            createDistributeXml(threadCount);
        } else {
            throw new Exception("select test type to be parallel or distribute");
        }
    }

    public void runTest() {
        newTestNG.run();
    }

    private void createParallelXml(int threadCount){
        XmlSuite testSuite = new XmlSuite();
        testSuite.setName("MobileAutomation");
        testSuite.setParallel(XmlSuite.ParallelMode.TESTS);

        ArrayList<String> listeners = new ArrayList<>();
        listeners.add("com.automation.mobile.listener.TestNGListener");
        testSuite.setListeners(listeners);

        List<XmlTest> tests = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            XmlTest test = new XmlTest(testSuite);
            test.setThreadCount(1);
            test.setParallel(XmlSuite.ParallelMode.NONE);
            test.setName("Device " + (i + 1));
            List<XmlClass> classes = new ArrayList<>();
            classes.add(new XmlClass("com.automation.mobile.TestRunner"));
            test.setXmlClasses(classes);
            tests.add(test);
        }
        List<XmlSuite> testSuites = new ArrayList<>();
        testSuites.add(testSuite);
        newTestNG.setXmlSuites(testSuites);
        testSuite.setFileName("TestNG.xml");
        testSuite.setThreadCount(threadCount);
        writeTestNGFile(testSuite);
        System.out.println(testSuite.toXml());
    }

    private void createDistributeXml(int threadCount){
        XmlSuite testSuite = new XmlSuite();
        testSuite.setName("MobileAutomation");
        testSuite.setParallel(XmlSuite.ParallelMode.CLASSES);

        ArrayList<String> listeners = new ArrayList<>();
        listeners.add("com.automation.mobile.listener.TestNGListener");
        testSuite.setListeners(listeners);
        XmlTest test = new XmlTest(testSuite);
        test.setName("TestNG Distribute Test");
        test.setPackages(getPackages());

        List<XmlSuite> testSuites = new ArrayList<>();
        testSuites.add(testSuite);
        newTestNG.setXmlSuites(testSuites);
        testSuite.setFileName("TestNG.xml");
        testSuite.setThreadCount(threadCount);
        writeTestNGFile(testSuite);
        System.out.println(testSuite.toXml());
    }

    private void writeTestNGFile(XmlSuite suite) {
        try {
            FileWriter writer = new FileWriter(new File(
                    System.getProperty("user.dir") + FileLocations.PARALLEL_XML_LOCATION));
            writer.write(suite.toXml());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<XmlPackage> getPackages() {
        List<XmlPackage> allPackages = new ArrayList<>();
        XmlPackage eachPackage = new XmlPackage();
        eachPackage.setName("output");
        allPackages.add(eachPackage);
        return allPackages;
    }
}
