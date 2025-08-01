package dev.osunolimits.main.init.engine;

public interface InitTask {
    public void execute() throws Exception;
    public String getName();
}
