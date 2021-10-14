import com.fasterxml.jackson.annotation.JsonProperty;
import data.Bugs;
import data.Commits;
import data.Modules;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
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
    @JsonProperty("revisionFile_referHistoryFromTheRevision") private String revisionFile_referHistoryFromTheRevision;
    @JsonProperty("revisionFile_referHistoryUntilTheRevision") private String revisionFile_referHistoryUntilTheRevision;
    @JsonProperty("revisionFile_referBugReportUntilTheRevision") private String revisionFile_referBugReportUntilTheRevision;
    private String pathRepositoryMethod;
    private Repository repositoryMethod;
    @JsonProperty("revisionMethod_referHistoryFromTheRevision") private String revisionMethod_referHistoryFromTheRevision;
    @JsonProperty("revisionMethod_referHistoryUntilTheRevision") private String revisionMethod_referHistoryUntilTheRevision;
    @JsonProperty("revisionMethod_referBugReportUntilTheRevision") private String revisionMethod_referBugReportUntilTheRevision;
    private String pathOutput;
    private Commits commitsAll = new Commits();
    private Modules modulesAll = new Modules();
    private Bugs bugsAll = new Bugs();

    public void inherit(Task task){
        this.commitsAll = task.commitsAll;
        this.modulesAll = task.modulesAll;
        this.bugsAll = task.bugsAll;
    }

    @Override
    public String  call() throws Exception {
        System.out.println(name);
        try {
            // copy repositoryFile to process tasks in parallel. (we have to checkout a revision in repositoryFile and keep it while processing a task.)
            pathRepositoryFileOriginal = pathProject+"/repositoryFile";
            File fileRepositoryFileOriginal = new File(pathRepositoryFileOriginal);
            pathRepositoryFileCopy = pathProject+"/repositoryFile_" + name;
            File fileRepositoryFileCopy = new File(pathRepositoryFileCopy);
            FileUtils.copyDirectory(fileRepositoryFileOriginal, fileRepositoryFileCopy);
            repositoryFile = new FileRepositoryBuilder().setGitDir(new File(pathRepositoryFileCopy + "/.git")).build();
            pathRepositoryMethod = pathProject+"/repositoryMethod";
            repositoryMethod = new FileRepositoryBuilder().setGitDir(new File(pathRepositoryMethod + "/.git")).build();

            String pathCommits = pathProject + "/commits";
            if(product.contains("commit")){
                commitsAll.loadCommitsFromRepository(repositoryMethod, pathCommits);
                commitsAll.loadCommitsFromFile(pathCommits);
            }

            String pathModules = pathProject + "/modules";
            if(product.contains("module")){
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
                modulesTarget.identifyTargetModules(modulesAll, repositoryMethod, revisionMethod_referHistoryUntilTheRevision);

                // calculate products on the target modules
                if (product.contains("commitGraph")){
                    modulesTarget.calculateCommitGraph(commitsAll, modulesAll, revisionMethod_referHistoryFromTheRevision, revisionMethod_referHistoryUntilTheRevision, bugsAll);
                }
                if (product.contains("metrics")) {
                    modulesTarget.calculateProcessMetrics(commitsAll, modulesAll, bugsAll, revisionMethod_referHistoryFromTheRevision, revisionMethod_referHistoryUntilTheRevision, revisionMethod_referBugReportUntilTheRevision);
                    modulesTarget.calculateCodeMetrics(repositoryFile, revisionFile_referHistoryUntilTheRevision, repositoryMethod, revisionMethod_referHistoryUntilTheRevision);
                }
                if (product.contains("tokens")){
                }
                if (product.contains("AST")) {
                    modulesTarget.calculateAST(repositoryMethod, revisionMethod_referHistoryUntilTheRevision);
                }
                if(product.contains("isBuggy")){
                    modulesTarget.calculateIsBuggy(commitsAll, revisionMethod_referHistoryUntilTheRevision, revisionMethod_referBugReportUntilTheRevision, bugsAll);
                }
                if(product.contains("hasBeenBuggy")){
                    modulesTarget.calculateHasBeenBuggy(commitsAll, revisionMethod_referHistoryFromTheRevision, revisionMethod_referHistoryUntilTheRevision, bugsAll);
                }

                // save data
                pathOutput = pathProject + "/output/" + name;
                if (
                        product.contains("AST")
                                | product.contains("commitGraph")
                                | product.contains("tokens")
                                | product.contains("hasBeenBuggy")
                                | product.contains("isBuggy")
                ) {
                    modulesTarget.saveAsJson(pathOutput);
                }
                if (product.contains("metrics")) {
                    modulesTarget.saveAsCSV(pathOutput + ".csv");
                }
            }

            // delete copied repositoryFile.
            repositoryFile.close();
            FileUtils.deleteDirectory(fileRepositoryFileCopy);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return "";
    }
}
