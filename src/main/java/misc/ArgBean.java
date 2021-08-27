package misc;

import org.kohsuke.args4j.Option;

public class ArgBean {
    @Option(name = "--pathProject", required = true)
    public  String pathProject;
    @Option(name = "--loadHistoryFromFile")
    public boolean loadHistoryFromFile = false;
    @Option(name = "--multiProcess")
    public boolean multiProcess = false;
    @Option(name = "--pathFileTask", required = true)
    public String pathFileTask;
}
