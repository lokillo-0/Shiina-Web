package dev.osunolimits.main.init.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunableInitTask implements InitTask {

    protected Logger logger = LoggerFactory.getLogger("StartupTask [" + getName() + "]");

    @Override
    public void execute() throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    public Logger getLogger() {
        return logger;
    }
    
}
