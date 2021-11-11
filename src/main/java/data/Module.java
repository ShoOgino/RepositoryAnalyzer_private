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
    @JsonIgnore public CommitsOnModule commitsOnModule = null;
    public CommitOnModule commitGraph = new CommitOnModule();
    public Sourcecode sourcecode = null;
    @JsonIgnore public Bugs bugs = null;
    int fanIn = 0;
    int hasBeenBuggy = 0;
    int isBuggy = 0;
    public void calcIsBuggy(Commits commitsAll, String revisionMethodTarget, String[] intervalRevisionMethod_referableCalculatingIsBuggy, Bugs bugsAll) {
        Set<String> pathsPast = new HashSet<>();
        for (CommitOnModule CommitOnModule : commitsOnModule.values()) {
            if (!Objects.equals(CommitOnModule.type, "DELETE")) pathsPast.add(CommitOnModule.pathNew);
        }
        for (String oneOfPath : pathsPast) {
            List<BugAtomic> bugAtomics = bugsAll.identifyAtomicBugs(oneOfPath);
            for (BugAtomic bugAtomic : bugAtomics) {
                Commit commitFix = commitsAll.get(bugAtomic.idCommitFix);
                Commit commitTimePoint = commitsAll.get(revisionMethodTarget);
                Commit commitLastBugFix = commitsAll.get(intervalRevisionMethod_referableCalculatingIsBuggy[1]);
                for (String idCommit : bugAtomic.idsCommitInduce) {
                    Commit commitInduce = commitsAll.get(idCommit);
                    if (commitInduce.date < commitTimePoint.date & commitTimePoint.date < commitFix.date & commitFix.date < commitLastBugFix.date)
                        isBuggy = 1;
                }
            }
        }
    }
    public void calcHasBeenBuggy(Commits commitsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics, Bugs bugsAll) {
        List<BugAtomic> bugAtomics = bugsAll.identifyAtomicBugs(path);
        if (bugAtomics == null) return;
        for (BugAtomic bugAtomic : bugAtomics) {
            Commit commitFrom =  commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[0]);
            Commit commitFix = commitsAll.get(bugAtomic.idCommitFix);
            Commit commitTimePoint = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]);
            if (commitFrom.date<commitFix.date &  commitFix.date < commitTimePoint.date) {
                this.hasBeenBuggy = 1;
            }
        }
    }

    public Module() {}
    public Module(String path) {
        this.path = path;
        this.commitsOnModule = new CommitsOnModule();
    }
    public Module clone() {
        Module module = null;
        try {
            module = (Module) super.clone();
            module.id = this.id;
            module.path = this.path;
            module.commitsOnModule = this.commitsOnModule;
        } catch (Exception e) {
            module = null;
        }
        return module;
    }
    //metrics
    //code metrics(Bug Prediction Based on Fine-Grained Module Histories)
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
    //process metrics(Bug Prediction Based on Fine-Grained Module Histories)
    public void calcModuleHistories() { commitGraph.calcModuleHistories();}
    public void calcAuthors() {
        commitGraph.calcAuthors();
    }
    public void calcSumStmtAdded() {
        commitGraph.calcSumStmtAdded();
    }
    public void calcMaxStmtAdded() {
        commitGraph.calcMaxStmtAdded();
    }
    public void calcAvgStmtAdded() {
        commitGraph.calcAvgStmtAdded();
    }
    public void calcSumStmtDeleted() {
        commitGraph.calcSumStmtDeleted();
    }
    public void calcMaxStmtDeleted() {
        commitGraph.calcMaxStmtDeleted();
    }
    public void calcAvgStmtDeleted() {
        commitGraph.calcAvgStmtDeleted();
    }
    public void calcSumChurn() {
        commitGraph.calcSumChurn();
    }
    public void calcMaxChurn() {
        commitGraph.calcMaxChurn();
    }
    public void calcAvgChurn() {
        commitGraph.calcAvgChurn();
    }
    public void calcSumElseAdded() {
        commitGraph.calcSumElseAdded();
    }
    public void calcSumElseDeleted() {
        commitGraph.calcSumElseDeleted();
    }
    public void calcSumDecl() {
        commitGraph.calcSumDecl();
    }
    public void calcSumCond() {
        commitGraph.calcSumCond();
    }
    //codeMetrics(Re-evaluating Method-Level Bug Prediction)
    public void calcLOC() {
        sourcecode.calcLOC();
    }
    //processMetrics(Re-evaluating Method-Level Bug Prediction)
    public void calcAddLOC() {
        commitGraph.calcAddLOC();
    }
    public void calcDelLOC() {
        commitGraph.calcDelLOC();
    }
    public void calcDevMinor() {
        commitGraph.calcDevMinor();
    }
    public void calcDevMajor() {
        commitGraph.calcDevMajor();
    }
    public void calcOwnership() {
        commitGraph.calcOwnership();
    }
    public void calcFixChgNum(Commits commitsAll, Bugs bugsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        commitGraph.calcFixChgNum(commitsAll, bugsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
    }
    public void calcPastBugNum(Commits commitsAll, Bugs bugsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        commitGraph.calcPastBugNum(commitsAll, bugsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
    }
    public void calcBugIntroNum() {
        commitGraph.calcBugIntroNum();
    }
    public void calcLogCoupNum() {
        commitGraph.calcLogCoupNum();
    }
    public void calcPeriod(Commits commitsAll ,String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        commitGraph.calcPeriod(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
    }
    public void calcAvgInterval() {
        commitGraph.calcAvgInterval();
    }
    public void calcMaxInterval() {
        commitGraph.calcMaxInterval();
    }
    public void calcMinInterval() {
        commitGraph.calcMinInterval();
    }
    //others
    //この中で、HEADの特定もする。
    public void identifyCommitGraphTarget(Commits commitsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        Commit commit = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]);

        //コミットグラフのheadを特定
        List<String> idsCommit = this.commitsOnModule.values().stream().map(a -> a.idCommit).collect(Collectors.toList());
        while (true) {
            if (idsCommit.contains(commit.id)) {
                this.commitGraph = commitsOnModule.queryIdCommit(commit.id).get(0);
                break;
            }
            if(!commitsAll.containsKey(commit.idParentMaster)){
                break;
            } else{
                commit = commitsAll.get(commit.idParentMaster);
            }
        }

        //コミットグラフのHeadが参照期間内にあるかを判定
        int dateBegin = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[0]).date;
        int dateEnd = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]).date;
        if(dateBegin <= this.commitGraph.date & this.commitGraph.date <= dateEnd){
            this.commitGraph.prune(dateBegin, dateEnd);
            this.commitGraph.flattenParents();
        }
    }
    public void identifySourcecodeTarget(Repository repositoryMethod, String idCommit) throws IOException {
        RevCommit revCommit = repositoryMethod.parseCommit(repositoryMethod.resolve(idCommit));
        RevTree tree = revCommit.getTree();
        try (TreeWalk treeWalk = new TreeWalk(repositoryMethod)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathSuffixFilter.create(path));
            while (treeWalk.next()) {
                ObjectLoader loader = repositoryMethod.open(treeWalk.getObjectId(0));
                this.sourcecode = new Sourcecode(new String(loader.getBytes()));
            }
        }
    }
    public void calcAST() { commitGraph.sourceNew.calcAST(); }
    public void calcCommitGraph(Commits commitsAll, Modules modulesAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics, Bugs bugsAll) {
        commitGraph.setMetricsOnEachNode();
    }
    public String outputRow(){
        String row =
                "\"" + path + "\"" + ", " +
                isBuggy + ", " +
                fanIn + ", " +
                sourcecode.fanOut + ", " +
                sourcecode.localVar + ", " +
                sourcecode.parameters + ", "+
                sourcecode.commentRatio + ", "+
                sourcecode.countPath + ", "+
                sourcecode.complexity + ", "+
                sourcecode.execStmt + ", "+
                sourcecode.maxNesting + ", "+
                commitGraph.moduleHistories + ", "+
                commitGraph.authors + ", "+
                commitGraph.sumStmtAdded + ", "+
                commitGraph.maxStmtAdded + ", "+
                commitGraph.avgStmtAdded + ", "+
                commitGraph.sumStmtDeleted + ", "+
                commitGraph.maxStmtDeleted + ", "+
                commitGraph.avgStmtDeleted + ", "+
                commitGraph.sumChurn + ", "+
                commitGraph.maxChurn + ", "+
                commitGraph.avgChurn + ", "+
                commitGraph.sumDecl + ", "+
                commitGraph.sumCond + ", "+
                commitGraph.sumElseAdded + ", "+
                commitGraph.sumElseDeleted + ", " + "\n";
        return row;
    }
}