package dev.osunolimits.main.init;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import dev.osunolimits.main.init.engine.RunableInitTask;

public class StartupOkHttpTask extends RunableInitTask {
    @Override
    public void execute() throws Exception {
        try {
            Files.walk(Paths.get(".cache/"))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            // Ignore
        }
    }
    
    @Override
    public String getName() {
        return "StartupOkHttpTask";
    }
}
