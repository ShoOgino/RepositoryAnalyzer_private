import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import data.*;
import data.Module;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

public class Task implements Callable<String> {
    @JsonProperty("name") public String name;
    @JsonProperty("priority") public Integer priority = 0;
    @JsonProperty("pathProject") public String pathProject;
    @JsonProperty("granularity") public String granularity;
    @JsonProperty("product") public List<String> product;
    private String pathRepositoryFileOriginal;
    private String pathRepositoryFileCopy;
    private Repository repositoryFile;
    @JsonProperty("revisionTargetFile") private  String revisionFileTarget;
    @JsonProperty("intervalRevisionFile_referableCalculatingMetricsIndependentOnFuture") private String[] intervalRevisionFile_referableCalculatingMetricsIndependentOnFuture = new String[2];
    @JsonProperty("intervalRevisionFile_referableCalculatingMetricsDependentOnFuture") private String[] intervalRevisionFile_referableCalculatingMetricsDependentOnFuture = new String[2];
    private String pathRepositoryMethod;
    private Repository repositoryMethod;
    @JsonProperty("revisionTargetMethod") private  String revisionMethodTarget;
    @JsonProperty("intervalRevisionMethod_referableCalculatingMetricsIndependentOnFuture") private String[] intervalRevisionMethod_referableCalculatingMetricsIndependentOnFuture = new String[2];
    @JsonProperty("intervalRevisionMethod_referableCalculatingMetricsDependentOnFuture") private String[] intervalRevisionMethod_referableCalculatingMetricsDependentOnFuture = new String[2];
    private String pathOutput;
    private Commits commitsAll = new Commits();
    private Committers committers = new Committers();
    private Modules modulesAll = new Modules();
    private Bugs bugsAll = new Bugs();

    public void inheritDataOnProject(Task task){
        this.commitsAll = task.commitsAll;
        this.committers = task.committers;
        this.modulesAll = task.modulesAll;
        this.bugsAll = task.bugsAll;
    }

    @Override
    public String call()  {
        System.out.println(name);
        // copy repositoryFile to process tasks in parallel. (we have to checkout and keep a revision in repositoryFile while processing a task.)
        pathRepositoryFileOriginal = pathProject+"/repositoryFile";
        pathRepositoryFileCopy = pathProject+"/repositoryFile_" + name;
        FileUtil.copyDirectory(pathRepositoryFileOriginal,pathRepositoryFileCopy);
        // create repository object
        pathRepositoryMethod = pathProject+"/repositoryMethod";
        try {
            repositoryFile = new FileRepositoryBuilder().setGitDir(new File(pathRepositoryFileCopy + "/.git")).build();
            repositoryMethod = new FileRepositoryBuilder().setGitDir(new File(pathRepositoryMethod + "/.git")).build();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if(product.contains("commit")){
            String pathCommits = pathProject + "/commits";
            commitsAll.loadCommitsFromRepository(repositoryMethod, pathCommits);
            commitsAll.loadCommitsFromFile(pathCommits);
        }
        if(product.contains("committers")) committers.analyzeAuthors(commitsAll);
        if(product.contains("bug")) {
            String pathBugs = pathProject + "/bugs.json";
            bugsAll.loadBugsFromFile(pathBugs);
        }
        if(product.contains("module")){
            String pathModules = pathProject + "/modules";
            modulesAll.analyzeAllModules(commitsAll, bugsAll);
            modulesAll.saveAsJson(pathModules);
        }

        if(Objects.equals(granularity, "method")){
            if(revisionMethodTarget==null) return "";
            Modules modulesTarget = new Modules();
            modulesTarget.identifyTargetModules(modulesAll, repositoryMethod, revisionMethodTarget);
            modulesTarget.identifyCommitsOnModuleInInterval(commitsAll, intervalRevisionMethod_referableCalculatingMetricsIndependentOnFuture);
            //calculate past metrics
            if (product.contains("AST")) {
                modulesTarget.calculateAST(repositoryMethod, revisionMethodTarget);
            }
            if (product.contains("metricsCode")) {
                modulesTarget.calculateMetricsCode(repositoryFile, revisionFileTarget, repositoryMethod, revisionMethodTarget, "giger");
            }
            if (product.contains("commitGraph")){
                Runtime r = Runtime.getRuntime();
                int NOfCPU = r.availableProcessors()-2;

                List<Modules> listModules = new ArrayList<>();
                List<Module> listModule = new ArrayList<>(modulesTarget.values());
                Collections.shuffle(listModules);
                List<List<Module>> modulesSplitted = Lists.partition(listModule, listModule.size()/NOfCPU);
                for (int i = 0; i < NOfCPU; i++) {
                    Modules modules = new Modules(modulesSplitted.get(i), commitsAll, modulesAll, committers, intervalRevisionMethod_referableCalculatingMetricsIndependentOnFuture, intervalRevisionMethod_referableCalculatingMetricsDependentOnFuture);
                    listModules.add(modules);
                    modules.start();
                }
                for (int i = 0; i < NOfCPU; i++) {
                    try {
                        listModules.get(i).join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for(Module module: modulesTarget.values()){
                    module.calcMetricsProcess2(commitsAll, modulesAll, "giger");
                }
            }
            if(product.contains("metricsProcess")){
                modulesTarget.calculateMetricsProcess(
                        commitsAll,
                        modulesAll,
                        intervalRevisionMethod_referableCalculatingMetricsIndependentOnFuture,
                        intervalRevisionMethod_referableCalculatingMetricsDependentOnFuture,
                        "giger"
                );
            }
            //save calculated metrics
            pathOutput = pathProject + "/output/" + name;
            modulesTarget.saveAsJson(pathOutput);
            modulesTarget.saveAsCSV(pathOutput + ".csv", "giger");
            repositoryFile.close();
            FileUtil.deleteDirectory(pathRepositoryFileCopy);
        }else if(Objects.equals(granularity, "file")){
            System.out.println();
        }else if(Objects.equals(granularity,"commit")){
            System.out.println();
        }
        return "";
    }
}
