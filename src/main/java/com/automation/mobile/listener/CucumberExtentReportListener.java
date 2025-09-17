package com.automation.mobile.listener;

import com.automation.mobile.appium.AppiumDeviceManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.gherkin.model.Asterisk;
import com.aventstack.extentreports.gherkin.model.ScenarioOutline;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Test;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.messages.Messages;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import com.aventstack.extentreports.service.ExtentService;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CucumberExtentReportListener implements ConcurrentEventListener {

    private Map<String, ExtentTest> featureMap = new ConcurrentHashMap<>();
    private ThreadLocal<ExtentTest> featureTestThreadLocal = new InheritableThreadLocal<>();
    private Map<String, ExtentTest> scenarioOutlineMap = new ConcurrentHashMap<>();
    private ThreadLocal<ExtentTest> scenarioOutlineThreadLocal = new InheritableThreadLocal<>();
    private ThreadLocal<ExtentTest> scenarioThreadLocal = new InheritableThreadLocal<>();
    private ThreadLocal<Boolean> isHookThreadLocal = new InheritableThreadLocal<>();
    private ThreadLocal<ExtentTest> stepTestThreadLocal = new InheritableThreadLocal<>();
    private ThreadLocal<Set<String>> featureTagsThreadLocal = new InheritableThreadLocal<>();
    private ThreadLocal<Set<String>> scenarioOutlineTagsThreadLocal = new InheritableThreadLocal<>();

    @SuppressWarnings("serial")
    private static final Map<String, String> MIME_TYPES_EXTENSIONS = new HashMap<String, String>() {
        {
            put("image/bmp", "bmp");
            put("image/gif", "gif");
            put("image/jpeg", "jpeg");
            put("image/jpg", "jpg");
            put("image/png", "png");
            put("image/svg+xml", "svg");
            // TODO Video, txt, html, pdf etc.
            // put("video/ogg", "ogg");
            // put("video/mp4", "mp4");
        }
    };

    private static final AtomicInteger EMBEDDED_INT = new AtomicInteger(0);

    private final TestSourcesModel testSources = new TestSourcesModel();

    private ThreadLocal<URI> currentFeatureFile = new ThreadLocal<>();
    private ThreadLocal<Messages.GherkinDocument.Feature.Scenario> currentScenarioOutline = new InheritableThreadLocal<>();
    private ThreadLocal<Messages.GherkinDocument.Feature.Scenario.Examples> currentExamples = new InheritableThreadLocal<>();

    private EventHandler<TestSourceRead> testSourceReadHandler = new EventHandler<TestSourceRead>() {
        @Override
        public void receive(TestSourceRead event) {
            handleTestSourceRead(event);
        }
    };
    private EventHandler<TestCaseStarted> caseStartedHandler = new EventHandler<TestCaseStarted>() {
        @Override
        public void receive(TestCaseStarted event) {
            handleTestCaseStarted(event);
        }
    };
    private EventHandler<TestStepStarted> stepStartedHandler = new EventHandler<TestStepStarted>() {
        @Override
        public void receive(TestStepStarted event) {
            handleTestStepStarted(event);
        }
    };
    private EventHandler<TestStepFinished> stepFinishedHandler = new EventHandler<TestStepFinished>() {
        @Override
        public void receive(TestStepFinished event) {
            handleTestStepFinished(event);
        }
    };
    private EventHandler<EmbedEvent> embedEventhandler = new EventHandler<EmbedEvent>() {
        @Override
        public void receive(EmbedEvent event) {
            handleEmbed(event);
        }
    };
    private EventHandler<WriteEvent> writeEventhandler = new EventHandler<WriteEvent>() {
        @Override
        public void receive(WriteEvent event) {
            handleWrite(event);
        }
    };
    private EventHandler<TestRunFinished> runFinishedHandler = new EventHandler<TestRunFinished>() {
        @Override
        public void receive(TestRunFinished event) {
            finishReport();
        }
    };

    public CucumberExtentReportListener(String arg) {
        ExtentService.getInstance();
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);
        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestStepStarted.class, stepStartedHandler);
        publisher.registerHandlerFor(TestStepFinished.class, stepFinishedHandler);
        publisher.registerHandlerFor(EmbedEvent.class, embedEventhandler);
        publisher.registerHandlerFor(WriteEvent.class, writeEventhandler);
        publisher.registerHandlerFor(TestRunFinished.class, runFinishedHandler);
    }

    private void handleTestSourceRead(TestSourceRead event) {
        testSources.addTestSourceReadEvent(event.getUri(), event);
    }

    private synchronized void handleTestCaseStarted(TestCaseStarted event) {
        handleStartOfFeature(event.getTestCase());
        handleScenarioOutline(event.getTestCase());
        createTestCase(event.getTestCase());
    }

    private synchronized void handleTestStepStarted(TestStepStarted event) {
        isHookThreadLocal.set(false);

        if (event.getTestStep() instanceof HookTestStep) {

            ExtentTest t = scenarioThreadLocal.get().createNode(Asterisk.class, event.getTestStep().getCodeLocation(),
                    (((HookTestStep) event.getTestStep()).getHookType()).toString().toUpperCase());
            stepTestThreadLocal.set(t);
            isHookThreadLocal.set(true);
        }

        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep testStep = (PickleStepTestStep) event.getTestStep();
            createTestStep(testStep);
        }
    }

    private synchronized void handleTestStepFinished(TestStepFinished event) {
        updateResult(event.getResult());
    }

    private synchronized void updateResult(Result result) {
        scenarioThreadLocal.get().assignCategory(String.format("Device_%s", AppiumDeviceManager.getDevice().getPlatform().toUpperCase()+"_"+AppiumDeviceManager.getDevice().getDeviceName()+"_"+AppiumDeviceManager.getDevice().getPlatformVersion().toUpperCase()));

        Test test = stepTestThreadLocal.get().getModel();
        switch (result.getStatus().name().toLowerCase()) {
            case "failed":
                stepTestThreadLocal.get().fail(result.getError());
                break;
            case "undefined":
                stepTestThreadLocal.get().fail("Step undefined");
                break;
            case "pending":
                stepTestThreadLocal.get().fail(result.getError());
                break;
            case "skipped":
                if (isHookThreadLocal.get()) {
                    ExtentService.getInstance().removeTest(stepTestThreadLocal.get());
                    break;
                }
                boolean currentEndingEventSkipped = test.hasLog()
                        ? test.getLogs().get(test.getLogs().size() - 1).getStatus() == Status.SKIP
                        : false;
                if (result.getError() != null) {
                    stepTestThreadLocal.get().skip(result.getError());
                } else if (!currentEndingEventSkipped) {
                    String details = result.getError() == null ? "Step skipped" : result.getError().getMessage();
                    stepTestThreadLocal.get().skip(details);
                }
                break;
            case "passed":
                if (stepTestThreadLocal.get() != null) {
                    if (isHookThreadLocal.get()) {
                        boolean mediaLogs = !test.getLogs().stream().filter(l -> l.getMedia() != null)
                                .collect(Collectors.toList()).isEmpty();
                        if (!test.hasLog() && !mediaLogs)
                            ExtentService.getInstance().removeTest(stepTestThreadLocal.get());
                    }
                    stepTestThreadLocal.get().pass("");
                }
                break;
            default:
                break;
        }
    }

    private synchronized void handleEmbed(EmbedEvent event) {

        String mimeType = event.getMediaType();
        String extension = MIME_TYPES_EXTENSIONS.get(mimeType);
        if (extension != null) {
            if (stepTestThreadLocal.get() == null) {
                ExtentTest t = scenarioThreadLocal.get().createNode(Asterisk.class, "Embed");
                stepTestThreadLocal.set(t);
            }

            String title = event.getName() == null ? "" : event.getName();
            if (ExtentService.isBase64ImageSrcEnabled() && mimeType.startsWith("image/")) {
                stepTestThreadLocal.get().info(title, MediaEntityBuilder
                        .createScreenCaptureFromBase64String(Base64.getEncoder().encodeToString(event.getData()))
                        .build());
            } else {
                StringBuilder fileName = new StringBuilder("embedded").append(EMBEDDED_INT.incrementAndGet())
                        .append(".").append(extension);
                try {
                    URL url = toUrl(fileName.toString());
                    writeBytesToURL(event.getData(), url);
                    try {
                        File file = new File(url.toURI());
                        stepTestThreadLocal.get().info(title,
                                MediaEntityBuilder
                                        .createScreenCaptureFromPath(
                                                ExtentService.getScreenshotReportRelatvePath() + file.getName())
                                        .build());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeBytesToURL(byte[] buf, URL url) throws IOException {
        OutputStream out = createReportFileOutputStream(url);
        try {
            out.write(buf);
        } catch (IOException e) {
            throw new IOException("Unable to write to report file item: ", e);
        }
    }

    private static OutputStream createReportFileOutputStream(URL url) {
        try {
            return new URLOutputStream(url);
        } catch (IOException | URISyntaxException e) {
            throw new CucumberException(e);
        }
    }

    private URL toUrl(String fileName) {
        try {
            URL url = Paths.get(ExtentService.getScreenshotFolderName(), fileName).toUri().toURL();
            return url;
        } catch (IOException e) {
            throw new CucumberException(e);
        }
    }

    private void handleWrite(WriteEvent event) {
        String text = event.getText();
        if (text != null && !text.isEmpty()) {
            stepTestThreadLocal.get().info(text);
        }
    }

    private void finishReport() {
        ExtentService.getInstance().flush();
    }

    private synchronized void handleStartOfFeature(TestCase testCase) {
        if (currentFeatureFile == null || !currentFeatureFile.equals(testCase.getUri())) {
            currentFeatureFile.set(testCase.getUri());
            createFeature(testCase);
        }
    }

    private synchronized void createFeature(TestCase testCase) {
        Messages.GherkinDocument.Feature feature = testSources.getFeature(testCase.getUri());
        try {
            ExtentService.getInstance().setGherkinDialect(feature.getLanguage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (feature != null) {
            if (featureMap.containsKey(feature.getName())) {
                featureTestThreadLocal.set(featureMap.get(feature.getName()));
                return;
            }
            if (featureTestThreadLocal.get() != null
                    && featureTestThreadLocal.get().getModel().getName().equals(feature.getName())) {
                return;
            }
            ExtentTest t = ExtentService.getInstance().createTest(
                    com.aventstack.extentreports.gherkin.model.Feature.class, feature.getName(),
                    feature.getDescription());
            featureTestThreadLocal.set(t);
            featureMap.put(feature.getName(), t);

            Set<String> tagList = feature.getTagsList().stream().map(tag -> tag.getName()).collect(Collectors.toSet());
            featureTagsThreadLocal.set(tagList);
        }
    }

    private synchronized void handleScenarioOutline(TestCase testCase) {
        TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile.get(), testCase.getLine());
        Messages.GherkinDocument.Feature.Scenario scenarioDefinition = TestSourcesModel.getScenarioDefinition(astNode);

        if (scenarioDefinition.getKeyword().equals("Scenario Outline")) {
            if (currentScenarioOutline.get() == null
                    || !currentScenarioOutline.get().getName().equals(scenarioDefinition.getName())) {
                scenarioOutlineThreadLocal.set(null);
                createScenarioOutline(scenarioDefinition);
                currentScenarioOutline.set(scenarioDefinition);
            }
            Messages.GherkinDocument.Feature.Scenario.Examples examples = (Messages.GherkinDocument.Feature.Scenario.Examples) astNode.parent.node;
            if (currentExamples.get() == null || !currentExamples.get().equals(examples)) {
                currentExamples.set(examples);
                createExamples(examples);
            }
        } else {
            scenarioOutlineThreadLocal.set(null);
            currentScenarioOutline.set(null);
            currentExamples.set(null);
        }
    }

    private synchronized void createScenarioOutline(Messages.GherkinDocument.Feature.Scenario scenarioOutline) {
        if (scenarioOutlineMap.containsKey(scenarioOutline.getName())) {
            scenarioOutlineThreadLocal.set(scenarioOutlineMap.get(scenarioOutline.getName()));
            return;
        }
        if (scenarioOutlineThreadLocal.get() == null) {
            ExtentTest t = featureTestThreadLocal.get().createNode(
                    com.aventstack.extentreports.gherkin.model.ScenarioOutline.class, scenarioOutline.getName(),
                    scenarioOutline.getDescription());
            scenarioOutlineThreadLocal.set(t);
            scenarioOutlineMap.put(scenarioOutline.getName(), t);

            Set<String> tagList = scenarioOutline.getTagsList().stream().map(tag -> tag.getName())
                    .collect(Collectors.toSet());
            scenarioOutlineTagsThreadLocal.set(tagList);
        }
    }

    private void createExamples(Messages.GherkinDocument.Feature.Scenario.Examples examples) {
        List<Messages.GherkinDocument.Feature.TableRow> rows = new ArrayList<>();
        rows.add(examples.getTableHeader());
        rows.addAll(examples.getTableBodyList());
        String[][] data = getTable(rows);
        String markup = MarkupHelper.createTable(data).getMarkup();
        if (examples.getName() != null && !examples.getName().isEmpty()) {
            markup = examples.getName() + markup;
        }
        markup = scenarioOutlineThreadLocal.get().getModel().getDescription() + markup;
        scenarioOutlineThreadLocal.get().getModel().setDescription(markup);
    }

    private String[][] getTable(List<Messages.GherkinDocument.Feature.TableRow> rows) {
        String data[][] = null;
        int rowSize = rows.size();
        for (int i = 0; i < rowSize; i++) {
            Messages.GherkinDocument.Feature.TableRow row = rows.get(i);
            List<Messages.GherkinDocument.Feature.TableRow.TableCell> cells = row.getCellsList();
            int cellSize = cells.size();
            if (data == null) {
                data = new String[rowSize][cellSize];
            }
            for (int j = 0; j < cellSize; j++) {
                data[i][j] = cells.get(j).getValue();
            }
        }
        return data;
    }

    private synchronized void createTestCase(TestCase testCase) {
        TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile.get(), testCase.getLine());
        if (astNode != null) {
            Messages.GherkinDocument.Feature.Scenario scenarioDefinition = TestSourcesModel.getScenarioDefinition(astNode);
            ExtentTest parent = scenarioOutlineThreadLocal.get() != null ? scenarioOutlineThreadLocal.get()
                    : featureTestThreadLocal.get();
            ExtentTest t = parent.createNode(com.aventstack.extentreports.gherkin.model.Scenario.class,
                    testCase.getName(), scenarioDefinition.getDescription());
            scenarioThreadLocal.set(t);
        }
        if (!testCase.getTags().isEmpty()) {
            testCase.getTags().forEach(x -> scenarioThreadLocal.get().assignCategory(x));
        }

        if (featureTagsThreadLocal.get() != null) {
            featureTagsThreadLocal.get().forEach(x -> scenarioThreadLocal.get().assignCategory(x));
        }

        Test parent = scenarioThreadLocal.get().getModel().getParent();
        if (parent.getBddType() == ScenarioOutline.class && scenarioOutlineTagsThreadLocal.get() != null) {
            scenarioOutlineTagsThreadLocal.get().forEach(x -> scenarioThreadLocal.get().assignCategory(x));
        }
    }

    private synchronized void createTestStep(PickleStepTestStep testStep) {
        String stepName = testStep.getStep().getText();
        TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile.get(),
                testStep.getStep().getLine());
        if (astNode != null) {
            Messages.GherkinDocument.Feature.Step step = (Messages.GherkinDocument.Feature.Step) astNode.node;
            try {
                String name = stepName == null || stepName.isEmpty()
                        ? step.getText().replace("<", "&lt;").replace(">", "&gt;")
                        : stepName;
                ExtentTest t = scenarioThreadLocal.get().createNode(new GherkinKeyword(step.getKeyword().trim()),
                        step.getKeyword() + name, testStep.getCodeLocation());
                stepTestThreadLocal.set(t);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        StepArgument argument = testStep.getStep().getArgument();
        if (argument != null) {
            if (argument instanceof DocStringArgument) {
                stepTestThreadLocal.get()
                        .pass(MarkupHelper.createCodeBlock(((DocStringArgument) argument).getContent()));
            } else if (argument instanceof DataTableArgument) {
                stepTestThreadLocal.get()
                        .pass(MarkupHelper.createTable(createDataTableList((DataTableArgument) argument)));
            }
        }
    }

    private String[][] createDataTableList(DataTableArgument dataTable) {
        List<List<String>> cells = dataTable.cells();
        int rowSize = cells.size();
        int cellSize = cells.get(0).size();
        String[][] data = new String[rowSize][cellSize];

        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < cellSize; j++)
                data[i][j] = cells.get(i).get(j);
        }
        return data;
    }

    // the below additions are from PR #33
    // https://github.com/extent-framework/extentreports-cucumber4-adapter/pull/33
    public synchronized void addTestStepLog(String message) {
        stepTestThreadLocal.get().info(message);
    }

    public synchronized void addTestStepScreenCaptureFromPath(String imagePath) throws IOException {
        stepTestThreadLocal.get().addScreenCaptureFromPath(imagePath);
    }

    public synchronized void addTestStepScreenCaptureFromPath(String imagePath, String title)
            throws IOException {
        stepTestThreadLocal.get().addScreenCaptureFromPath(imagePath, title);
    }

    public ExtentTest getCurrentStep() {
        return stepTestThreadLocal.get();
    }
}
