import com.fasterxml.jackson.annotation.JsonProperty;
import data.Bugs;
import data.Commits;
import data.Modules;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class Task implements Callable<String> {
    private int numTask;
    @JsonProperty("name") public String name;
    @JsonProperty("priority") public Integer priority = 0;
    @JsonProperty("pathProject") public String pathProject;
    @JsonProperty("granularity") public String granularity;
    @JsonProperty("product") public List<String> product;
    private String pathRepositoryFileOriginal;
    private String pathRepositoryFileCopy;
    private Repository repositoryFile;
    @JsonProperty("revisionTargetFile") private  String revisionFileTarget;
    @JsonProperty("intervalRevisionFile_referableCalculatingProcessMetrics") private String[] intervalRevisionFile_referableCalculatingProcessMetrics = new String[2];
    @JsonProperty("intervalRevisionFile_referableCalculatingIsBuggy") private String[] intervalRevisionFile_referableCalculatingIsBuggy = new String[2];
    private String pathRepositoryMethod;
    private Repository repositoryMethod;
    @JsonProperty("revisionTargetMethod") private  String revisionMethodTarget;
    @JsonProperty("intervalRevisionMethod_referableCalculatingProcessMetrics") private String[] intervalRevisionMethod_referableCalculatingProcessMetrics = new String[2];
    @JsonProperty("intervalRevisionMethod_referableCalculatingIsBuggy") private String[] intervalRevisionMethod_referableCalculatingIsBuggy = new String[2];
    private String pathOutput;
    private Commits commitsAll = new Commits();
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

            if(product.contains("module")){
                String pathModules = pathProject + "/modules";
                modulesAll.analyzeAllModules(commitsAll);
                modulesAll.saveAsJson(pathModules);
            }

            if(product.contains("bug")) {
                String pathBugs = pathProject + "/bugs.json";
                bugsAll.loadBugsFromFile(pathBugs);
                commitsAll.embedBugInfo(bugsAll, modulesAll);
            }

            // identify target modules
            if(product.contains("AST")
                    | product.contains("tokens")
                    | product.contains("commitGraph")
                    | product.contains("metrics")
                    | product.contains("hasBeenBuggy")
                    | product.contains("isBuggy")) {
                Modules modulesTarget = new Modules();
                modulesTarget.identifyTargetModules(modulesAll, repositoryMethod, revisionMethodTarget);

                // calculate products on the target modules
                if (product.contains("commitGraph")){
                    modulesTarget.calculateCommitGraph(commitsAll, modulesAll, intervalRevisionMethod_referableCalculatingProcessMetrics, bugsAll);
                }
                if (product.contains("metrics")) {
                    modulesTarget.calculateProcessMetrics(commitsAll, modulesAll, bugsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
                    modulesTarget.calculateCodeMetrics(repositoryFile, revisionFileTarget, repositoryMethod, revisionMethodTarget);
                }
                if (product.contains("tokens")){
                }
                if (product.contains("AST")) {
                    modulesTarget.calculateAST(repositoryMethod, revisionMethodTarget);
                }
                if(product.contains("isBuggy")){
                    modulesTarget.calculateIsBuggy(commitsAll, revisionMethodTarget, intervalRevisionMethod_referableCalculatingIsBuggy, bugsAll);
                }
                if(product.contains("hasBeenBuggy")){
                    modulesTarget.calculateHasBeenBuggy(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics, bugsAll);
                }

                // save data
                pathOutput = pathProject + "/output/" + name;
                if (
                        product.contains("AST")
                                | product.contains("commitGraph")
                                | product.contains("tokens")
                ) {
                    modulesTarget.saveAsJson(pathOutput);
                }
                if (product.contains("metrics")) {
                    modulesTarget.saveAsCSV(pathOutput + ".csv");
                }
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
