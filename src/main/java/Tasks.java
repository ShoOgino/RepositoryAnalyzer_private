import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import data.CommitsThread;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Tasks {
    @JsonProperty("isMultiprocess") Boolean isMultiprocess;
    @JsonProperty("tasks") List<Task> tasks = new ArrayList<>();

    public void execute() throws Exception {
        //タスクをプロジェクトごとにまとめる。
        Map<String, List<Task>> pathProject2Tasks = new HashMap<>();
        for(Task task: tasks){
            if(pathProject2Tasks.containsKey(task.pathProject)){
                pathProject2Tasks.get(task.pathProject).add(task);
            }else{
                List<Task> tasksOfTheProject = new ArrayList<>();
                tasksOfTheProject.add(task);
                pathProject2Tasks.put(task.pathProject, tasksOfTheProject);
            }
        }

        //同時実行プロセス数の決定
        int numOfProcesses4Tasks = 0;
        if(isMultiprocess){
            Runtime r = Runtime.getRuntime();
            int numOfCPUs = r.availableProcessors();
            numOfProcesses4Tasks = numOfCPUs - 2;
        }else{
            numOfProcesses4Tasks = 1;
        }
        //タスクを対象プロジェクトごとに実行。
        //まず対象プロジェクトを分析 → メトリクス算出タスクへ。
        for(String pathProject: pathProject2Tasks.keySet()){
            List<Task> tasksOfTheProject = pathProject2Tasks.get(pathProject);

            //プロジェクト情報(コミット情報、モジュール情報)を分析するタスクを実行
            Task taskToAnalyzeProject = new Task();
            taskToAnalyzeProject.name = "analyzeProject";
            taskToAnalyzeProject.pathProject = pathProject;
            taskToAnalyzeProject.granularity = "method";
            taskToAnalyzeProject.product = Arrays.asList("commit", "module", "bug");
            taskToAnalyzeProject.call();

            //プロジェクト情報を他のタスクに参照させる
            for(Task task: tasksOfTheProject){
                task.inheritDataOnProject(taskToAnalyzeProject);
            }

            //優先度が高いものから、タスクを実行
            Map<Integer, List<Task>> priority2Tasks = tasksOfTheProject.stream().collect(Collectors.groupingBy(e->e.priority));
            List<Future<String>> resultList = null;
            for(Integer priority: priority2Tasks.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())){
                System.out.println("priority: "+ String.valueOf(priority));
                ExecutorService pool;
                pool = Executors.newFixedThreadPool(numOfProcesses4Tasks);
                resultList = pool.invokeAll(priority2Tasks.get(priority));
                for(Future<String> result : resultList){
                    try {
                        System.out.println(result.get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                pool.shutdown();
            }
        }
    }
}
