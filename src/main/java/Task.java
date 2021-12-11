import com.fasterxml.jackson.annotation.JsonProperty;
import data.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.util.List;
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
        this.modulesAll = task.modulesAll;
        this.bugsAll = task.bugsAll;
    }

    @Override
    public String  call()  {
        System.out.println(name);
        // copy repositoryFile to process tasks in parallel. (we have to checkout and keep a revision in repositoryFile while processing a task.)
        pathRepositoryFileOriginal = pathProject+"/repositoryFile";
        File fileRepositoryFileOriginal = new File(pathRepositoryFileOriginal);
        pathRepositoryFileCopy = pathProject+"/repositoryFile_" + name;
        File fileRepositoryFileCopy = new File(pathRepositoryFileCopy);
        try {
            FileUtils.copyDirectory(fileRepositoryFileOriginal, fileRepositoryFileCopy);
            repositoryFile = new FileRepositoryBuilder().setGitDir(new File(pathRepositoryFileCopy + "/.git")).build();
            pathRepositoryMethod = pathProject+"/repositoryMethod";
            repositoryMethod = new FileRepositoryBuilder().setGitDir(new File(pathRepositoryMethod + "/.git")).build();

            if(product.contains("commit")){
                String pathCommits = pathProject + "/commits";
                commitsAll.loadCommitsFromRepository(repositoryMethod, pathCommits);
                commitsAll.loadCommitsFromFile(pathCommits);
            }
            //childrenを捕捉
            for(Commit commit: commitsAll.values()){
                for(String idParent: commit.idParent2Modifications.keySet()){
                    if(commitsAll.containsKey(idParent)) commitsAll.get(idParent).children.add(commit.id);
                }
            }
            committers.analyzeAuthors(commitsAll);

            if(product.contains("bug")) {
                String pathBugs = pathProject + "/bugs.json";
                bugsAll.loadBugsFromFile(pathBugs);
            }

            if(product.contains("module")){
                String pathModules = pathProject + "/modules";
                modulesAll.analyzeAllModules(commitsAll, bugsAll);
                modulesAll.saveAsJson(pathModules);
            }

            if(product.contains("AST")
                    | product.contains("tokens")
                    | product.contains("commitGraph")
                    | product.contains("metricsProcess")
                    | product.contains("metricsCode")) {
                // identify target modules
                Modules modulesTarget = new Modules();
                modulesTarget.identifyTargetModules(modulesAll, repositoryMethod, revisionMethodTarget);

                //calculate past metrics
                if (product.contains("AST")) {
                    modulesTarget.calculateAST(repositoryMethod, revisionMethodTarget);
                }
                if (product.contains("tokens")){
                }
                if (product.contains("metricsCode")) {
                    modulesTarget.calculateMetricsCode(repositoryFile, revisionFileTarget, repositoryMethod, revisionMethodTarget);
                }
                if (product.contains("commitGraph")){
                    modulesTarget.calculateCommitGraph(commitsAll, modulesAll, committers, intervalRevisionMethod_referableCalculatingMetricsIndependentOnFuture);
                }
                if(product.contains("metricsProcess")){
                    modulesTarget.calculateMetricsProcess(
                            commitsAll,
                            modulesAll,
                            intervalRevisionMethod_referableCalculatingMetricsIndependentOnFuture,
                            intervalRevisionMethod_referableCalculatingMetricsDependentOnFuture
                    );
                }

                //save calculated metrics
                pathOutput = pathProject + "/output/" + name;
                modulesTarget.saveAsJson(pathOutput);
                modulesTarget.saveAsCSV(pathOutput + ".csv");
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }finally {
            repositoryFile.close();
            try {
                FileUtils.deleteDirectory(fileRepositoryFileCopy);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "";
    }
}
