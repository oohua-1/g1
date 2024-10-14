package h2.fw.runner.web;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class ParallelJunit {
    private final String folder;
    private final List<String> browsers;
    private final int parallel;
    private final String tag;
    public ParallelJunit(String folder, List<String> browsers, int parallel, String tag) {
        this.folder = folder;
        this.browsers = browsers;
        this.parallel = parallel;
        this.tag = tag;
    }

    public void runInParallel() {
        int poolSize = parallel;

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        for (String browser : browsers) {
            executor.submit(new JunitRunner(folder, browser, parallel, tag)); // Pass parallel as the third argument
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
