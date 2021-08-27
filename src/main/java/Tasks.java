import data.Project;
import util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tasks {
    List<Task> tasks = new ArrayList<>();
    String pathFileTasks;
    int numOfProcesses4Tasks;

    public Tasks(Project project, String pathFileTasks, Boolean multiProcess){
        this.pathFileTasks = pathFileTasks;
        if(multiProcess){
            Runtime r = Runtime.getRuntime();
            int numOfCPUs = r.availableProcessors();
            numOfProcesses4Tasks = numOfCPUs - 2;
        }else{
            numOfProcesses4Tasks = 1;
        }
        try {
            parseFileTasks(project);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseFileTasks(Project project) throws IOException {
        String[] tasksStr = FileUtil.readFile(pathFileTasks).split("\\n");
        for (int numTask=0; numTask<tasksStr.length; numTask++) {
            String[] pattern = tasksStr[numTask].replace("\r", "").split(",");
            String nameTask = Arrays.copyOfRange(pattern, 0, 1)[0];
            String granularity = Arrays.copyOfRange(pattern, 1, 2)[0];
            String product = Arrays.copyOfRange(pattern, 2, 3)[0];
            String revisionFile_target   = Arrays.copyOfRange(pattern, 4, 5)[0];
            String revisionMethod_referHistoryFrom = Arrays.copyOfRange(pattern, 6, 7)[0];
            String revisionMethod_target = Arrays.copyOfRange(pattern, 7, 8)[0];
            String revisionMethod_referBugReportUntil = Arrays.copyOfRange(pattern, 8, 9)[0];
            Task task = new Task(
                    numTask,
                    nameTask,
                    granularity,
                    product,
                    project.pathProject,
                    project.pathRepositoryFile,
                    revisionFile_target,
                    project.pathRepositoryMethod,
                    revisionMethod_referHistoryFrom,
                    revisionMethod_target,
                    revisionMethod_referBugReportUntil,
                    project.commitsAll,
                    project.modulesAll,
                    project.bugsAll
            );
            tasks.add(task);
        }
    }

    public void execute(){
        ExecutorService pool;
        pool = Executors.newFixedThreadPool(numOfProcesses4Tasks);
        for(Task task: tasks) pool.submit(task);
        pool.shutdown();
    }
}
