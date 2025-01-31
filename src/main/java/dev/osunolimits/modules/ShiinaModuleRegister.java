package dev.osunolimits.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.osunolimits.main.App;
import dev.osunolimits.routes.get.modular.Module;

public class ShiinaModuleRegister {

    private Map<String, Module> modules = new HashMap<>();
    private Map<String, Module> defaultModules = new HashMap<>();

    public void registerDefaultModule(String location, Module module) {
        try {
            App.log.info("Registering default module: for [" + location + "] " + module.getClass().getName());
            modules.put(location, module);
            defaultModules.put(location, module);
            
        } catch (Exception e) {
            App.log.error("Failed to register module", e);
        }
    }

    public void registerModule(String location, Module module) {
        try {
            App.log.info("Registering plugin module: for [" + location + "] " + module.getClass().getName());
            modules.put(location, module);
        } catch (Exception e) {
            App.log.error("Failed to register module", e);
        }
    }

    public List<Module> getModules(String location) {
        List<Module> moduleList = new ArrayList<>();

        for(int i = 0; i < modules.size(); i++) {
            Module module = modules.values().toArray()[i] instanceof Module ? (Module) modules.values().toArray()[i] : null;
            String localLocation = modules.keySet().toArray()[i].toString();
            if (localLocation.equals(location)) {
                moduleList.add(module);
            }
        }

        return moduleList;  
    }
    
}
