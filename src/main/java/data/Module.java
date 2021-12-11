package data;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Module implements Cloneable {
    @JsonIgnore public String id = "";
    public String path = null;
    public CommitsOnModule commitsOnModuleAll = null;
    public CommitsOnModule commitsOnModuleInInterval = new CommitsOnModule();
    public Sourcecode sourcecode = null;

    public Module() {}
    public Module(String path) {
        this.path = path;
        this.commitsOnModuleAll = new CommitsOnModule();
    }
    @Override public Module clone() {
        Module module = null;
        try {
            module = (Module) super.clone();
            module.commitsOnModuleAll = this.commitsOnModuleAll.clone();
        } catch (Exception e) {
            module = null;
        }
        return module;
    }
    public void calcMetricsCode(){
        sourcecode.calcMetrics();
    }
    public void calcMetricsProcess1(Commits commitsAll, String[] intervalRevision_referableCalculatingMetricsIndependentOnFuture, String[] intervalRevision_referableCalculatingMetricsDependentOnFuture) {
        int dateFrom_ReferableToCalculateMetricsIndependentOfFuture = commitsAll.get(intervalRevision_referableCalculatingMetricsIndependentOnFuture[0]).date;
        int dateUntil_ReferableToCalculateMetricsIndependentOfFuture = commitsAll.get(intervalRevision_referableCalculatingMetricsIndependentOnFuture[1]).date;
        int dateUntil_ReferableToCalculateMetricsDependentOfFuture =commitsAll.get(intervalRevision_referableCalculatingMetricsDependentOnFuture[1]).date;
        commitsOnModuleAll.calcMetricsDependentOnFuture(dateFrom_ReferableToCalculateMetricsIndependentOfFuture, dateUntil_ReferableToCalculateMetricsIndependentOfFuture, dateUntil_ReferableToCalculateMetricsDependentOfFuture);
        commitsOnModuleInInterval.calcMetricsIndependentOnFuture1(commitsAll, dateFrom_ReferableToCalculateMetricsIndependentOfFuture, dateUntil_ReferableToCalculateMetricsIndependentOfFuture);
    }
    public void calcMetricsProcess2(Commits commitsAll, Modules modulesAll){
        commitsOnModuleInInterval.calcMetricsIndependentOnFuture2(commitsAll, modulesAll);
    }
    public void identifyCommitGraphTarget(Commits commitsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        if(commitsOnModuleInInterval.size()!=0) return;
        Commit commit = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]);

        //コミットグラフのheadを特定+その祖先をコミットグラフの要素として特定
        List<String> idsCommit = this.commitsOnModuleAll.values().stream().map(a -> a.idCommit).collect(Collectors.toList());
        while (true) {
            if (idsCommit.contains(commit.id)) {
                List<CommitOnModule> commitOnModules = commitsOnModuleAll.queryIdCommit(commit.id);
                for(CommitOnModule commitOnModule: commitOnModules){//ある時点から見て最新の「コミット」は2つ存在することがある。例えば、最新のコミットがマージコミットの場合。
                    if(Objects.equals(commitOnModule.type, "DELETE")) continue;
                    commitOnModule.loadAncestors(this.commitsOnModuleInInterval);
                }
                break;
            }
            if(!commitsAll.containsKey(commit.idParentMaster)) break;
            else commit = commitsAll.get(commit.idParentMaster);
        }

        //コミットグラフの要素がきちんと期間内にあるかどうかを確認
        int dateBegin = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[0]).date;
        int dateEnd = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]).date;
        this.commitsOnModuleInInterval.excludeCommitsOutOfInterval(dateBegin, dateEnd);
        this.commitsOnModuleInInterval.excludeCommitsMerge();
    }
    public void identifySourcecodeTarget(Repository repositoryMethod, String idCommit) throws IOException {
        RevCommit revCommit = repositoryMethod.parseCommit(repositoryMethod.resolve(idCommit));
        RevTree tree = revCommit.getTree();
        try (TreeWalk treeWalk = new TreeWalk(repositoryMethod)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathSuffixFilter.create(this.path));
            while (treeWalk.next()) {
                ObjectLoader loader = repositoryMethod.open(treeWalk.getObjectId(0));
                this.sourcecode = new Sourcecode(new String(loader.getBytes()));
            }
        }
    }
    public void calcAST() { sourcecode.calcAST(); }
    public void calcCommitGraph(Commits commitsAll, Modules modulesAll, Committers authors) {
        commitsOnModuleInInterval.calcMetricsOnEachNode();
        commitsOnModuleInInterval.calcVectorsOnEachNode(commitsAll, modulesAll, authors);
    }
    public String outputRow(){
        String row =
                "\"" + path + "\"" + ", " +
                commitsOnModuleAll.isBuggy + ", " +
                sourcecode.fanin + ", " +
                sourcecode.fanout + ", " +
                sourcecode.numOfVariablesLocal + ", " +
                sourcecode.numOfParameters + ", "+
                sourcecode.ratioOfLinesComment + ", "+
                sourcecode.numOfPaths + ", "+
                sourcecode.complexity + ", "+
                sourcecode.numOfStatements + ", "+
                sourcecode.maxOfNesting + ", "+
                commitsOnModuleInInterval.numOfCommits + ", "+
                commitsOnModuleInInterval.numOfCommittersUnique + ", "+
                commitsOnModuleInInterval.sumOfAdditionsStatement + ", "+
                commitsOnModuleInInterval.maxOfAdditionsStatement + ", "+
                commitsOnModuleInInterval.avgOfAdditionsStatement + ", "+
                commitsOnModuleInInterval.sumOfDeletionsStatement + ", "+
                commitsOnModuleInInterval.maxOfDeletionsStatement + ", "+
                commitsOnModuleInInterval.avgOfDeletionsStatement + ", "+
                commitsOnModuleInInterval.sumOfChurnsStatement + ", "+
                commitsOnModuleInInterval.maxOfChurnsStatement + ", "+
                commitsOnModuleInInterval.avgOfChurnsStatement + ", "+
                commitsOnModuleInInterval.sumOfChangesDeclarationItself + ", "+
                commitsOnModuleInInterval.sumOfChangesCondition + ", "+
                commitsOnModuleInInterval.sumOfAdditionStatementElse + ", "+
                commitsOnModuleInInterval.sumOfDeletionStatementElse + ", " + "\n";
        return row;
    }

}