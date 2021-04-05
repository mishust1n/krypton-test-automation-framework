package io.mishustin.krypton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;

public class TestInitialization {

    private static final Logger LOG = LogManager.getLogger(TestInitialization.class);

    private static boolean isInit = false;

    public static synchronized void init() throws IOException {
        if (!isInit) {
            LOG.info("Test initialization");
            TestConfiguration.readRunProperties(Paths.get(System.getProperty("user.dir"), "run.properties"));
            TestConfiguration.readConfig(Paths.get(System.getProperty("user.dir"), "env_config", TestConfiguration.env + ".properties"));
            TestReporter.createReportFile();
            isInit = true;
        }
    }
}
