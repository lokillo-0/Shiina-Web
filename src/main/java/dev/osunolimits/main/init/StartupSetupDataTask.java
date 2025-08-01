package dev.osunolimits.main.init;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;

import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;
import dev.osunolimits.models.DbVersion;

public class StartupSetupDataTask extends RunableInitTask {
    @Override
    public void execute() throws Exception {
        if (!Files.exists(Paths.get("data"))) {
            Files.createDirectories(Paths.get("data"));
        }

        if (!Files.exists(Paths.get("data/dbversion.json"))) {

            Gson gson = new Gson();
            DbVersion dbVersion = new DbVersion();
            dbVersion.setVersion(App.version);
            dbVersion.setDbVersion(App.dbVersion);

            Files.writeString(Paths.get("data/dbversion.json"), gson.toJson(dbVersion));
        }
    }

    @Override
    public String getName() {
        return "StartupSetupDataTask";
    }
}
