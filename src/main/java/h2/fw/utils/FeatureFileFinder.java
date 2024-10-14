package h2.fw.utils;

import h2.fw.runner.TestNGXmlGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;

public class FeatureFileFinder {
    private static final String FEATURES_DIRECTORY = "src/test/resources/features/";
    private static final Logger LOGGER = LogManager.getLogger(FeatureFileFinder.class.getName());

    private static final FeatureFileFinder INSTANCE = new FeatureFileFinder();

    private FeatureFileFinder() {}

    public static FeatureFileFinder getInstance() {
        return INSTANCE;
    }

    public String getFeatureFilePath(String featureFileName) {
        String fullName = featureFileName + ".feature";
        try (Stream<Path> paths = Files.walk(Paths.get(FEATURES_DIRECTORY))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(fullName))
                    .findFirst()
                    .map(Path::toString)
                    .orElseThrow( ()-> new RuntimeException("Feature file not found: " + fullName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}