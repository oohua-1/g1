package h2.fw.Report;
import java.net.URI;
import java.nio.file.Paths;


import com.epam.reportportal.utils.MemoizingSupplier;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import h2.fw.runner.web.TestRunnerUtil;
import io.reactivex.Maybe;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CucumberPlatformScenarioReporterListener
        extends ScenarioReporter {

    private static final String DUMMY_ROOT_SUITE_NAME = "End-2-End Tests";
    private static final String RP_STORY_TYPE = "SUITE";
    public static String launchUUID;
    public static final ThreadLocal<String> FEATURE_NAME_THREAD_LOCAL = new InheritableThreadLocal<>();


    @Override
    protected void startRootItem() {
        this.rootSuiteId = new MemoizingSupplier<>((

        ) -> {
            String browserName = TestRunnerUtil.getThreadBrowser();
            String featureName = FEATURE_NAME_THREAD_LOCAL.get();
            String combineFeatureName = browserName + "-" + featureName;
            if (featureName == null) {
                featureName = "Default Suite";  // Fallback if feature name is not set
            }
            StartTestItemRQ rq = new StartTestItemRQ();
            rq.setName(combineFeatureName);
            rq.setStartTime(Calendar.getInstance().getTime());
            rq.setType(RP_STORY_TYPE);
            launchUUID = this.getItemTree().getLaunchId().blockingGet();
            return this.getLaunch().startTestItem(rq);
        });
    }

}
