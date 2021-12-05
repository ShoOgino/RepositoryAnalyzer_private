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

    // future metrics
    public void calcIsBuggy(int dateTarget, int[] intervalDate_referableCalculatingIsBuggy){
        commitsOnModuleAll.calcIsBuggy(dateTarget, intervalDate_referableCalculatingIsBuggy);
    }
    // past metrics
    //// code metrics
    public void calcLOC() {
        sourcecode.calcNumOfLines();
    }
    public void calcFanOut(){
        sourcecode.calcFanOut();
    }
    public void calcParameters(){
        sourcecode.calcParameters();
    }
    public void calcLocalVar(){
        sourcecode.calcLocalVar();
    }
    public void calcCommentRatio(){ sourcecode.calcCommentRatio();}
    public void calcCountPath(){ sourcecode.calcCountPath();}
    public void calcComplexity(){ sourcecode.calcComplexity();}
    public void calcExecStmt(){ sourcecode.calcExecStmt();}
    public void calcMaxNesting(){ sourcecode.calcMaxNesting();}
    //// process metrics
    public boolean calcHasBeenBuggy(int dateTarget){
        for(CommitOnModule commitOnModule: commitsOnModuleAll.values()){
            if(commitOnModule.date<dateTarget&&0<commitOnModule.IdsCommitsFixingBugThatThisCommitInduces.size()){
                return true;
            }
        }
        return false;
    }
    public void calcNumOfCommits() {
        commitsOnModuleInInterval.calcNumOfCommits();
    }
    public void calcNumOfCommitsFixingBugs() {
        commitsOnModuleInInterval.calcNumOfCommitsFixingBugs();
    }
    public void calcNumOfBugreportsUnique() {
        commitsOnModuleInInterval.calcNumOfBugreportsUnique();
    }
    public void calcNumOfCommitsInducingBugs(){
        commitsOnModuleInInterval.calcNumOfCommitsInducingBugs();
    }
    public void calcNumOfCommitsOtherModulesHasBeenBuggyOnTheCommit(Commits commitsAll, Modules modulesAll) {
        commitsOnModuleInInterval.calcNumOfCommitsOtherModulesHasBeenBuggyOnTheCommit(commitsAll, modulesAll);
    }
    public void calcNumOfCommitsOtherModulesGetBuggyOnTheCommit(Commits commitsAll) {
        commitsOnModuleInInterval.calcNumOfCommitsOtherModulesGetBuggyOnTheCommit(commitsAll);
    }
    public void calcNumOfCommittersUnique() {
        commitsOnModuleInInterval.calcNumOfCommittersUnique();
    }
    public void calcNumOfCommittersMinor() {
        commitsOnModuleInInterval.calcNumOfCommittersMinor();
    }
    public void calcNumOfCommittersMajor() {
        commitsOnModuleInInterval.calcNumOfCommittersMajor();
    }
    public void calcOwnership() {
        commitsOnModuleInInterval.calcOwnership();
    }
    public void calcPeriod(Commits commitsAll ,String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        Commit commit = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]);
        commitsOnModuleInInterval.calcPeriod(commitsAll, commit.date);
    }
    public void calcMaxOfInterval() {
        commitsOnModuleInInterval.calcMaxOfInterval();
    }
    public void calcMinOfInterval() {
        commitsOnModuleInInterval.calcMinOfInterval();
    }
    public void calcAvgOfIntervalCommit() {
        commitsOnModuleInInterval.calcAvgOfInterval();
    }
    public void calcSumOfLinesAdded() {
        commitsOnModuleInInterval.calcSumOfAdditionsLine();
    }
    public void calcSumOfLinesDeleted() {
        commitsOnModuleInInterval.calcSumOfDeletionsLine();
    }
    public void calcSumOfAdditionsStatement() {
        commitsOnModuleInInterval.calcSumOfAdditionsStatement();
    }
    public void calcMaxOfAdditionsStatement() {
        commitsOnModuleInInterval.calcMaxOfAdditionsStatement();
    }
    public void calcAvgOfAdditionsStatement() {
        commitsOnModuleInInterval.calcAvgOfAdditionsStatement();
    }
    public void calcSumOfDeletionsStatement() {
        commitsOnModuleInInterval.calcSumOfDeletionsStatement();
    }
    public void calcMaxOfDeletionsStatement() {
        commitsOnModuleInInterval.calcMaxOfDeletionsStatement();
    }
    public void calcAvgOfDeletionsStatement() {
        commitsOnModuleInInterval.calcAvgOfDeletionsStatement();
    }
    public void calcSumOfChurnsStatement() {
        commitsOnModuleInInterval.calcSumOfChurnsStatement();
    }
    public void calcMaxOfChurnsStatement() {
        commitsOnModuleInInterval.calcMaxOfChurnsStatement();
    }
    public void calcAvgOfChurnsStatement() {
        commitsOnModuleInInterval.calcAvgOfChurnsStatement();
    }
    public void calcSumOfChangesDeclaration() {
        commitsOnModuleInInterval.calcSumOfChangesDeclaration();
    }
    public void calcSumOfChangesCondition() {
        commitsOnModuleInInterval.calcSumOfChangesCondition();
    }
    public void calcSumOfAdditionStatementElse() {
        commitsOnModuleInInterval.calcSumOfAdditionStatementElse();
    }
    public void calcSumOfDeletionStatementElse() {
        commitsOnModuleInInterval.calcSumOfDeletionStatementElse();
    }

    //others
    public void identifyCommitGraphTarget(Commits commitsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
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
    public void calcCommitGraph(Commits commitsAll, Modules modulesAll, People authors) {
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
                commitsOnModuleInInterval.sumOfChangesDeclaration + ", "+
                commitsOnModuleInInterval.sumOfChangesCondition + ", "+
                commitsOnModuleInInterval.sumOfAdditionStatementElse + ", "+
                commitsOnModuleInInterval.sumOfDeletionStatementElse + ", " + "\n";
        return row;
    }
}