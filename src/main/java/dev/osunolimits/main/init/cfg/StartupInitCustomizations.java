package dev.osunolimits.main.init.cfg;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;

import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;

public class StartupInitCustomizations extends RunableInitTask {
    @Override
    public void execute() throws Exception {
        Yaml yaml = new Yaml();
        String yamlContent = Files.readString(Paths.get(".config/customization.yml"));
        App.customization = yaml.load(yamlContent);
        logger.info("Loaded customization.yml");
    }

    @Override
    public String getName() {
        return "StartupInitCustomizations";
    }
}
