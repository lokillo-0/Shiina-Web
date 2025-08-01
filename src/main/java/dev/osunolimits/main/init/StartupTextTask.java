package dev.osunolimits.main.init;

import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;

public class StartupTextTask extends RunableInitTask {

    private static String startupAscii = "       __                                  \r\n      /\\ \\      __  __                     \r\n  ____\\ \\ \\___ /\\_\\/\\_\\    ___      __     \r\n /',__\\\\ \\  _ `\\/\\ \\/\\ \\ /' _ `\\  /'__`\\   \r\n/\\__, `\\\\ \\ \\ \\ \\ \\ \\ \\ \\/\\ \\/\\ \\/\\ \\L\\.\\_ \r\n\\/\\____/ \\ \\_\\ \\_\\ \\_\\ \\_\\ \\_\\ \\_\\ \\__/.\\_\\\r\n \\/___/   \\/_/\\/_/\\/_/\\/_/\\/_/\\/_/\\/__/\\/_/\r\n                                           \r\n                                                                                  ";

    @Override
    public void execute() throws Exception {
        System.out.println(startupAscii);
        logger.info("Shiina-Web Rewrite " + App.version);
    }

    @Override
    public String getName() {
        return "StartupTextTask";
    }
    
}
