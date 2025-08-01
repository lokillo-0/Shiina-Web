package dev.osunolimits.main.init.engine;

public class StartupTaskRunner {
    
    public static void register(InitTask task) {
        try {
            task.execute();
        } catch (Exception e) {
            if(task instanceof RunableInitTask runableInitTask) {
                runableInitTask.getLogger().error("Error executing task: " + task.getName(), e);   
                System.exit(1);
            }
        }
        
    }

}
