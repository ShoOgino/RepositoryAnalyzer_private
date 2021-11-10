package data;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import lombok.Data;
import misc.DoubleConverter;
import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;

@JsonIgnoreProperties(ignoreUnknown = true)
@CsvDataType()
@Data
public class Module implements Cloneable {
    @JsonIgnore public String id = "";
    @CsvField(pos = 1) public String path = null;
    @JsonIgnore public CommitsOnModule commitsOnModule = null;
    public CommitsOnModule changesOnModuleInInterval = null;
    public List<NodeCommit4Experiment> commitGraph = null;
    @JsonIgnore private Sourcecode sourcecode = null;
    @CsvField(pos = 2) int isBuggy = 0;
    @CsvField(pos = 3) int hasBeenBuggy = 0;
    //code metrics(Bug Prediction Based on Fine-Grained Module Histories)
    @CsvField(pos = 4) int fanIn = 0;
    @CsvField(pos = 5) int fanOut = 0;
    @CsvField(pos = 6) int parameters = 0;
    @CsvField(pos = 7) int localVar = 0;
    @CsvField(pos = 8, converterType = DoubleConverter.class) double commentRatio = 0;
    @CsvField(pos = 9) long countPath = 0;
    @CsvField(pos = 10) int complexity = 0;
    @CsvField(pos = 11) int execStmt = 0;
    @CsvField(pos = 12) int maxNesting = 0;
    //process metrics(Bug Prediction Based on Fine-Grained Module Histories)
    @CsvField(pos = 13) int moduleHistories = 0;
    @CsvField(pos = 14) int authors = 0;
    @CsvField(pos = 15) int stmtAdded = 0;
    @CsvField(pos = 16) int maxStmtAdded = 0;
    @CsvField(pos = 17, converterType = DoubleConverter.class) double avgStmtAdded = 0;
    @CsvField(pos = 18) int stmtDeleted = 0;
    @CsvField(pos = 19) int maxStmtDeleted = 0;
    @CsvField(pos = 20, converterType = DoubleConverter.class) double avgStmtDeleted = 0;
    @CsvField(pos = 21) int churn = 0;
    @CsvField(pos = 22) int maxChurn = 0;
    @CsvField(pos = 23, converterType = DoubleConverter.class) double avgChurn = 0;
    @CsvField(pos = 24) int decl = 0;
    @CsvField(pos = 25) int cond = 0;
    @CsvField(pos = 26) int elseAdded = 0;
    @CsvField(pos = 27) int elseDeleted = 0;
    //codeMetrics(Re-evaluating Method-Level Bug Prediction)
    @CsvField(pos = 28) int LOC = 0;
    //processMetrics(Re-evaluating Method-Level Bug Prediction)
    @CsvField(pos = 29) int addLOC = 0;
    @CsvField(pos = 30) int delLOC = 0;
    @CsvField(pos = 31) int devMinor = 0;
    @CsvField(pos = 32) int devMajor = 0;
    @CsvField(pos = 33) double ownership = 0;
    @CsvField(pos = 34) int fixChgNum = 0;
    @CsvField(pos = 35) int pastBugNum = 0;
    @CsvField(pos = 36) int bugIntroNum = 0;
    @CsvField(pos = 37) int logCoupNum = 0;
    @CsvField(pos = 38) int period = 0;
    @CsvField(pos = 39) double avgInterval = 0;
    @CsvField(pos = 40) int maxInterval = 0;
    @CsvField(pos = 41) int minInterval = 0;

    public Module() { }
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
    public void calcIsBuggy(Commits commitsAll, String revisionMethodTarget, String[] intervalRevisionMethod_referableCalculatingIsBuggy, Bugs bugsAll) {
        Set<String> pathsPast = new HashSet<>();
        for (CommitOnModule CommitOnModule : this.commitsOnModule.values()) {
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
    //code metrics(Bug Prediction Based on Fine-Grained Module Histories)
    public void calcFanOut(){
        this.fanOut = sourcecode.calcFanOut();
    }
    public void calcParameters(){
        this.parameters = sourcecode.calcParameters();
    }
    public void calcLocalVar(){
        this.localVar = sourcecode.calcLocalVar();
    }
    public void calcCommentRatio(){ this.commentRatio = sourcecode.calcCommentRatio();}
    public void calcCountPath(){ this.countPath = sourcecode.calcCountPath();}
    public void calcComplexity(){ this.complexity = sourcecode.calcComplexity();}
    public void calcExecStmt(){ this.execStmt = sourcecode.calcExecStmt();}
    public void calcMaxNesting(){ this.maxNesting = sourcecode.calcMaxNesting();}
    //process metrics(Bug Prediction Based on Fine-Grained Module Histories)
    public void calcModuleHistories() {
        int moduleHistories = changesOnModuleInInterval.size();
        this.moduleHistories = moduleHistories;
    }
    public void calcAuthors() {
        Set<String> setAuthors = new HashSet<>();
        changesOnModuleInInterval.values().forEach(item -> setAuthors.add(item.author));
        this.authors = setAuthors.size();
    }
    public void calcStmtAdded() {
        int stmtAdded = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                if (change.getChangeType() == ChangeType.STATEMENT_INSERT) stmtAdded++;
            }
        }
        this.stmtAdded = stmtAdded;
    }
    public void calcMaxStmtAdded() {
        int maxStmtAdded = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            int stmtAddedTemp = 0;
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                if (change.getChangeType() == ChangeType.STATEMENT_INSERT) stmtAddedTemp++;
            }
            if (maxStmtAdded < stmtAddedTemp) {
                maxStmtAdded = stmtAddedTemp;
            }
        }
        this.maxStmtAdded = maxStmtAdded;
    }
    public void calcAvgStmtAdded() {
        int avgStmtAdded = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                if (change.getChangeType() == ChangeType.STATEMENT_INSERT) avgStmtAdded++;
            }
        }
        calcModuleHistories();
        if (moduleHistories == 0) this.avgStmtAdded = 0;
        else this.avgStmtAdded = avgStmtAdded / (double) moduleHistories;
    }
    public void calcStmtDeleted() {
        int stmtDeleted = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                if (change.getChangeType() == ChangeType.STATEMENT_DELETE) stmtDeleted++;
            }
        }
        this.stmtDeleted = stmtDeleted;
    }
    public void calcMaxStmtDeleted() {
        int maxStmtDeleted = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            int stmtDeletedOnCommit = 0;
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                if (change.getChangeType() == ChangeType.STATEMENT_DELETE) stmtDeletedOnCommit++;
            }
            if (maxStmtDeleted < stmtDeletedOnCommit) {
                maxStmtDeleted = stmtDeletedOnCommit;
            }
        }
        this.maxStmtDeleted = maxStmtDeleted;
    }
    public void calcAvgStmtDeleted() {
        int avgStmtDeleted = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                if (change.getChangeType() == ChangeType.STATEMENT_DELETE) avgStmtDeleted++;
            }
        }
        calcModuleHistories();
        if (moduleHistories == 0) this.avgStmtDeleted = 0;
        else this.avgStmtDeleted = avgStmtDeleted / (double) moduleHistories;
    }
    public void calcChurn() {
        int churn = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                if (change.getChangeType() == ChangeType.STATEMENT_INSERT) churn++;
                else if (change.getChangeType() == ChangeType.STATEMENT_DELETE) churn--;
            }
        }
        this.churn = churn;
    }
    public void calcMaxChurn() {
        int maxChurn = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            int churnTemp = 0;
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                if (change.getChangeType() == ChangeType.STATEMENT_INSERT) churnTemp++;
                else if (change.getChangeType() == ChangeType.STATEMENT_DELETE) churnTemp--;
            }
            if (maxChurn < churnTemp) maxChurn = churnTemp;
        }
        this.maxChurn = maxChurn;
    }
    public void calcAvgChurn() {
        calcChurn();
        calcModuleHistories();
        if (moduleHistories == 0) this.avgChurn = 0;
        else this.avgChurn = churn / (float) moduleHistories;
    }
    public void calcElseAdded() {
        int elseAdded = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                EntityType et = change.getChangedEntity().getType();
                if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_INSERT & et.toString().equals("ELSE_STATEMENT"))
                    elseAdded++;
            }
        }
        this.elseAdded = elseAdded;
    }
    public void calcElseDeleted() {
        int elseDeleted = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                EntityType et = change.getChangedEntity().getType();
                if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_DELETE & et.toString().equals("ELSE_STATEMENT"))
                    elseDeleted++;
            }
        }
        this.elseDeleted = elseDeleted;
    }
    public void calcDecl() {
        int decl = 0;
        List<ChangeType> ctdecl = Arrays.asList(
                ChangeType.METHOD_RENAMING,
                ChangeType.PARAMETER_DELETE,
                ChangeType.PARAMETER_INSERT,
                ChangeType.PARAMETER_ORDERING_CHANGE,
                ChangeType.PARAMETER_RENAMING,
                ChangeType.PARAMETER_TYPE_CHANGE,
                ChangeType.RETURN_TYPE_INSERT,
                ChangeType.RETURN_TYPE_DELETE,
                ChangeType.RETURN_TYPE_CHANGE,
                ChangeType.PARAMETER_TYPE_CHANGE
        );
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                EntityType et = change.getChangedEntity().getType();
                if (ctdecl.contains(change.getChangeType())) decl++;
            }
        }
        this.decl = decl;
    }
    public void calcCond() {
        int cond = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
            for (SourceCodeChange change : changes) {
                EntityType et = change.getChangedEntity().getType();
                if (change.getChangeType() == ChangeType.CONDITION_EXPRESSION_CHANGE) cond++;
            }
        }
        this.cond = cond;
    }
    //codeMetrics(Re-evaluating Method-Level Bug Prediction)
    public void calcLOC() {
        this.LOC = sourcecode.calcLOC();
    }
    //processMetrics(Re-evaluating Method-Level Bug Prediction)
    public void calcAddLOC() {
        int addLOC = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            addLOC += CommitOnModule.calcNOAddedLines();
        }
        this.addLOC = addLOC;
    }
    public void calcDelLOC() {
        int delLOC = 0;
        for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            delLOC += CommitOnModule.calcNODeletedLines();
        }
        this.delLOC = delLOC;
    }
    public void calcDevMinor() {
        Set<String> setAuthors = new HashSet<>();
        changesOnModuleInInterval.values().forEach(item -> setAuthors.add(item.author));

        int devMinor = 0;
        for (String nameAuthor : setAuthors) {
            int count = (int) changesOnModuleInInterval.values().stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            if ( ( count / (float) changesOnModuleInInterval.size() ) < 0.2) {
                devMinor++;
            }
        }
        this.devMinor = devMinor;
    }
    public void calcDevMajor() {
        Set<String> setAuthors = new HashSet<>();
        changesOnModuleInInterval.values().forEach(item -> setAuthors.add(item.author));

        int devMajor = 0;
        for (String nameAuthor : setAuthors) {
            int count = (int) changesOnModuleInInterval.values().stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            if (0.2 < count / (float) changesOnModuleInInterval.size()) {
                devMajor++;
            }
        }
        this.devMajor = devMajor;
    }
    public void calcOwnership() {
        Set<String> setAuthors = new HashSet<>();
        changesOnModuleInInterval.values().forEach(item -> setAuthors.add(item.author));

        for (String nameAuthor : setAuthors) {
            int count = (int) changesOnModuleInInterval.values().stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            double ownership = count / (float) changesOnModuleInInterval.size();
            if (this.ownership < ownership) {
                this.ownership = ownership;
            }
        }
    }
    public void calcFixChgNum(Commits commitsAll, Bugs bugsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        Set<String> paths = new HashSet<>();
        for(CommitOnModule CommitOnModule : changesOnModuleInInterval.values()){
            if(!Objects.equals(CommitOnModule.pathNew, "/dev/null"))paths.add(CommitOnModule.pathNew);
            if(!Objects.equals(CommitOnModule.pathOld, "/dev/null"))paths.add(CommitOnModule.pathOld);
        }
        Set<String> commitsFixingBugs = new HashSet<>();
        for(String path: paths) {
            List<BugAtomic> bugAtomics = bugsAll.identifyAtomicBugs(path);
            for (BugAtomic bugAtomic : bugAtomics) {
                int dateBegin = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[0]).date;
                int dateCommitFix = commitsAll.get(bugAtomic.idCommitFix).date;
                int dateEnd = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]).date;
                if (dateBegin < dateCommitFix & dateCommitFix < dateEnd) {
                    commitsFixingBugs.add(bugAtomic.idCommitFix);
                }
            }
        }
        this.fixChgNum = commitsFixingBugs.size();
    }
    public void calcPastBugNum(Commits commitsAll, Bugs bugsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        Set<String> paths = new HashSet<>();
        for(CommitOnModule CommitOnModule : changesOnModuleInInterval.values()){
            if(!Objects.equals(CommitOnModule.pathNew, "/dev/null"))paths.add(CommitOnModule.pathNew);
            if(!Objects.equals(CommitOnModule.pathOld, "/dev/null"))paths.add(CommitOnModule.pathOld);
        }
        for(String path: paths) {
            List<Bug> bugs = bugsAll.identifyBug(path);
            for (Bug bug : bugs) {
                for (BugAtomic bugAtomic : bug.bugAtomics) {
                    if(Objects.equals(bugAtomic.path, path)){
                        int dateCommitFix = commitsAll.get(bugAtomic.idCommitFix).date;
                        int dateTarget = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]).date;
                        if (dateCommitFix < dateTarget) {
                            this.pastBugNum++;
                            break;
                        }
                    }
                }
            }
        }
    }
    //todo
    public void calcBugIntroNum() {
        Set<String> pathsPast = commitsOnModule.values().stream().map(a -> a.pathNew).collect(Collectors.toSet());
        for(CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
            /*
            for(String pathBugIntroduced :.pathsBugIntroduced){
                if(!pathsPast.contains(pathBugIntroduced)){
                    this.bugIntroNum += 1;
                    break;
                }
            }
             */
        }
    }
    /*todo
    public void calcLogCoupNum() {
        Set<String> pathsPast = changesOnModule.values().stream().map(a -> a.pathNew).collect(Collectors.toSet());
        for(ChangeOnModule changeOnModule: changesOnModuleInInterval) {
            for(String pathHasBeenBuggy :.pathsHasBeenBuggy){
                if(!pathsPast.contains(pathHasBeenBuggy)){
                    this.logCoupNum += 1;
                    break;
                }
            }
        }
    }
     */
    public void calcPeriod(Commits commitsAll ,String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        int periodFrom = Integer.MAX_VALUE;
        int periodTo = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]).date;
        for (CommitOnModule CommitOnModule : commitsOnModule.values()) {
            if (CommitOnModule.date < periodFrom) {
                periodFrom = CommitOnModule.date;
            }
        }
        this.period = (periodTo - periodFrom) / (60 * 60 * 24);
    }
    public void calcAvgInterval(Commits commitsAll ,String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        int sumInterval = 0;
        List<CommitOnModule> commitOnModulesSorted = changesOnModuleInInterval.values().stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
        if (commitOnModulesSorted.size() <= 1) {
            this.avgInterval = 0;
            return;
        }
        for (int i = 0; i < commitOnModulesSorted.size() - 1; i++) {
            sumInterval += commitOnModulesSorted.get(i + 1).date - commitOnModulesSorted.get(i).date;
        }
        this.avgInterval = (sumInterval / (float) (commitOnModulesSorted.size()-1))/(60 * 60 * 24 * 7);
    }
    public void calcMaxInterval() {
        int maxInterval = 0;
        List<CommitOnModule> commitOnModules = changesOnModuleInInterval.values().stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
        if (commitOnModules.size() < 2) {
            this.maxInterval = 0;
            return;
        }
        for (int i = 0; i < commitOnModules.size() - 1; i++) {
            int interval = commitOnModules.get(i + 1).date - commitOnModules.get(i).date;
            if (maxInterval < interval) {
                maxInterval = interval;
            }
        }
        this.maxInterval = maxInterval / (60 * 60 * 24 * 7);
    }
    public void calcMinInterval() {
        int minInterval = Integer.MAX_VALUE;
        List<CommitOnModule> commitOnModules = changesOnModuleInInterval.values().stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
        if (commitOnModules.size() < 2) {
            this.minInterval = 0;
            return;
        }
        for (int i = 0; i < commitOnModules.size() - 1; i++) {
            int interval = commitOnModules.get(i + 1).date - commitOnModules.get(i).date;
            if (interval < minInterval) {
                minInterval = interval;
            }
        }
        this.minInterval = minInterval / (60 * 60 * 24 * 7);
    }
    //others
    //この中で、HEADの特定もする。
    public void identifyCommitsOnModuleTarget(Commits commitsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        String sourcecodeNow = "";

        CommitsOnModule commitsOnModuleResult = new CommitsOnModule();

        int dateBegin = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[0]).date;
        int dateEnd = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]).date;
        for (Map.Entry<MultiKey<? extends String>, CommitOnModule> entry : commitsOnModule.entrySet()) {
            Commit commit = commitsAll.get(entry.getValue().idCommit);
            if (dateBegin <= commit.date & commit.date <= dateEnd) {
                commitsOnModuleResult.put(entry.getKey(), entry.getValue());
            }
        }

        this.changesOnModuleInInterval = commitsOnModuleResult;
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
    public List<SourceCodeChange> identifySourceCodeChange(CommitOnModule CommitOnModule) {
        String sourcePrev = null;
        String sourceCurrent = null;
        String strPre = null;
        String strPost = null;
        if (CommitOnModule.sourceOld.equals("")) {
            String regex = "\\n|\\r\\n";
            String tmp = CommitOnModule.sourceNew;
            String[] lines = tmp.split(regex, 0);

            boolean inComment = false;
            int count = 0;
            for (String line : lines) {
                if (line.matches(".*/\\*.*")) {
                    inComment = true;
                    count++;
                } else if (line.matches(".*\\*/.*")) {
                    inComment = false;
                    count++;
                } else if (inComment) {
                    count++;
                } else if (line.matches(".*//.*")) {
                    count++;
                } else {
                    break;
                }
            }
            tmp = "";
            for (int i = count; i < lines.length; i++) {
                tmp = tmp + lines[i] + "\n";
            }

            Pattern patternPre = Pattern.compile("[\\s\\S.]*?(?=\\{)");
            Matcher matcherPre = patternPre.matcher(tmp);
            if (matcherPre.find()) {
                strPre = matcherPre.group();
            }
            Pattern patternPost = Pattern.compile("(?<=\\{)[\\s\\S.]*");
            Matcher matcherPost = patternPost.matcher(tmp);
            if (matcherPost.find()) {
                strPost = matcherPost.group();
            }
            sourcePrev = "public class Test{" + strPre +
                    "{" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "}" +
                    "}";
            sourceCurrent = "public class Test{" + strPre +
                    "{" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    "dummy();\n" +
                    strPost +
                    "}";
        } else {
            sourcePrev = "public class Dummy{" + CommitOnModule.sourceOld + "}";
            sourceCurrent = "public class Dummy{" + CommitOnModule.sourceNew + "}";
        }

        FileDistiller distiller = ChangeDistiller.createFileDistiller(ChangeDistiller.Language.JAVA);
        try {
            distiller.extractClassifiedSourceCodeChanges(sourcePrev, sourceCurrent);
        } catch (Exception e) {
        }
        return distiller.getSourceCodeChanges();
    }
    public void calcCommitGraph(Commits commitsAll, Modules modulesAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics, Bugs bugs) throws IOException {
        Set<String> types = new HashSet<>();
        Map<String, Integer> id2Num = new HashMap<>();
        this.commitGraph = new ArrayList<>();
        //this.changesOnModuleInInterval = this.changesOnModuleInInterval.stream().sorted(Comparator.comparingInt(CommitOnModule::getDate).reversed()).collect(Collectors.toList());
        List<CommitOnModule> commitOnModulesHead = identifyChangeOnModuleHead(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics[1]);
        List<CommitOnModule> commitOnModulesTarget = identifyChangeOnModuleHead(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics[1]);
        for (CommitOnModule commitOnModule : this.changesOnModuleInInterval.values()) {
            if (checkIfTheChangeIs(commitOnModulesTarget, commitOnModule)) {
                commitOnModulesTarget.add(commitOnModule);
            }
        }
        for (int i = 0; i < commitOnModulesTarget.size(); i++) {
            CommitOnModule commitOnModule = commitOnModulesTarget.get(i);
            id2Num.put(commitOnModule.idCommit + commitOnModule.idCommitParent + commitOnModule.pathOld + commitOnModule.pathNew, i + 1);
        }
        for (CommitOnModule commitOnModule : commitOnModulesTarget) {
            NodeCommit4Experiment nodeCommit4Experiment = new NodeCommit4Experiment();
            //id
            nodeCommit4Experiment.idCommit = commitOnModule.idCommit;
            nodeCommit4Experiment.idCommitParent = commitOnModule.idCommitParent;
            //node and edge
            nodeCommit4Experiment.num = id2Num.get(commitOnModule.idCommit + commitOnModule.idCommitParent + commitOnModule.pathOld + commitOnModule.pathNew);
            for (CommitOnModule commitOnModuleParent : commitOnModule.parents.values()) {
                nodeCommit4Experiment.parents.add(id2Num.get(commitOnModule.idCommit + commitOnModule.idCommitParent + commitOnModule.pathOld + commitOnModule.pathNew));
            }
            //content
            //1. semantic type
            JdtTreeGenerator jdtTreeGenerator = new JdtTreeGenerator();
            String sourcePrev = "public class Test{" + commitOnModule.sourceOld + "}";
            String sourceCurrent = "public class Test{" + commitOnModule.sourceNew + "}";
            ITree iTreePrev = jdtTreeGenerator.generateFrom().string(sourcePrev).getRoot();
            ITree iTreeCurrent = jdtTreeGenerator.generateFrom().string(sourceCurrent).getRoot();
            com.github.gumtreediff.matchers.Matcher defaultMatcher = Matchers.getInstance().getMatcher();
            MappingStore mappings = defaultMatcher.match(iTreePrev, iTreeCurrent);
            EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator();
            EditScript actions = editScriptGenerator.computeActions(mappings);
            for(Action action: actions) {
                int index = 0;
                switch (action.getNode().getType().name) {
                    case "AnonymousClassDeclaration":
                        index = 0;
                        break;
                    case "ArrayAccess":
                        index = 1;
                        break;
                    case "ArrayCreation":
                        index = 2;
                        break;
                    case "ArrayInitializer":
                        index = 3;
                        break;
                    case "ArrayType":
                        index = 4;
                        break;
                    case "AssertStatement":
                        index = 5;
                        break;
                    case "Assignment":
                        index = 6;
                        break;
                    case "Block":
                        index = 7;
                        break;
                    case "BooleanLiteral":
                        index = 8;
                        break;
                    case "BreakStatement":
                        index = 9;
                        break;
                    case "CastExpression":
                        index = 10;
                        break;
                    case "CatchClause":
                        index = 11;
                        break;
                    case "CharacterLiteral":
                        index = 12;
                        break;
                    case "ClassInstanceCreation":
                        index = 13;
                        break;
                    case "CompilationUnit":
                        index = 14;
                        break;
                    case "ConditionalExpression":
                        index = 15;
                        break;
                    case "ConstructorInvocation":
                        index = 16;
                        break;
                    case "ContinueStatement":
                        index = 17;
                        break;
                    case "DoStatement":
                        index = 18;
                        break;
                    case "EmptyStatement":
                        index = 19;
                        break;
                    case "ExpressionStatement":
                        index = 20;
                        break;
                    case "FieldAccess":
                        index = 21;
                        break;
                    case "FieldDeclaration":
                        index = 22;
                        break;
                    case "ForStatement":
                        index = 23;
                        break;
                    case "IfStatement":
                        index = 24;
                        break;
                    case "ImportDeclaration":
                        index = 25;
                        break;
                    case "InfixExpression":
                        index = 26;
                        break;
                    case "Initializer":
                        index = 27;
                        break;
                    case "Javadoc":
                        index = 28;
                        break;
                    case "LabeledStatement":
                        index = 29;
                        break;
                    case "MethodDeclaration":
                        index = 30;
                        break;
                    case "MethodInvocation":
                        index = 31;
                        break;
                    case "NullLiteral":
                        index = 32;
                        break;
                    case "NumberLiteral":
                        index = 33;
                        break;
                    case "PackageDeclaration":
                        index = 34;
                        break;
                    case "ParenthesizedExpression":
                        index = 35;
                        break;
                    case "PostfixExpression":
                        index = 36;
                        break;
                    case "PrefixExpression":
                        index = 37;
                        break;
                    case "PrimitiveType":
                        index = 38;
                        break;
                    case "QualifiedName":
                        index = 39;
                        break;
                    case "ReturnStatement":
                        index = 40;
                        break;
                    case "SimpleName":
                        index = 41;
                        break;
                    case "SimpleType":
                        index = 42;
                        break;
                    case "SingleVariableDeclaration":
                        index = 43;
                        break;
                    case "StringLiteral":
                        index = 44;
                        break;
                    case "SuperConstructorInvocation":
                        index = 45;
                        break;
                    case "SuperFieldAccess":
                        index = 46;
                        break;
                    case "SuperMethodInvocation":
                        index = 47;
                        break;
                    case "SwitchCase":
                        index = 48;
                        break;
                    case "SwitchStatement":
                        index = 49;
                        break;
                    case "SynchronizedStatement":
                        index = 50;
                        break;
                    case "ThisExpression":
                        index = 51;
                        break;
                    case "ThrowStatement":
                        index = 52;
                        break;
                    case "TryStatement":
                        index = 53;
                        break;
                    case "TypeDeclaration":
                        index = 54;
                        break;
                    case "TypeDeclarationStatement":
                        index = 55;
                        break;
                    case "TypeLiteral":
                        index = 56;
                        break;
                    case "VariableDeclarationExpression":
                        index = 57;
                        break;
                    case "VariableDeclarationFragment":
                        index = 58;
                        break;
                    case "VariableDeclarationStatement":
                        index = 59;
                        break;
                    case "WhileStatement":
                        index = 60;
                        break;
                    case "InstanceofExpression":
                        index = 61;
                        break;
                    case "LineComment":
                        index = 62;
                        break;
                    case "BlockComment":
                        index = 63;
                        break;
                    case "TagElement":
                        index = 64;
                        break;
                    case "TextElement":
                        index = 65;
                        break;
                    case "MemberRef":
                        index = 66;
                        break;
                    case "MethodRef":
                        index = 67;
                        break;
                    case "MethodRefParameter":
                        index = 68;
                        break;
                    case "EnhancedForStatement":
                        index = 69;
                        break;
                    case "EnumDeclaration":
                        index = 70;
                        break;
                    case "EnumConstantDeclaration":
                        index = 71;
                        break;
                    case "TypeParameter":
                        index = 72;
                        break;
                    case "ParameterizedType":
                        index = 73;
                        break;
                    case "QualifiedType":
                        index = 74;
                        break;
                    case "WildcardType":
                        index = 75;
                        break;
                    case "NormalAnnotation":
                        index = 76;
                        break;
                    case "MarkerAnnotation":
                        index = 77;
                        break;
                    case "SingleMemberAnnotation":
                        index = 78;
                        break;
                    case "MemberValuePair":
                        index = 79;
                        break;
                    case "AnnotationTypeDeclaration":
                        index = 80;
                        break;
                    case "AnnotationTypeMemberDeclaration":
                        index = 81;
                        break;
                    case "Modifier":
                        index = 82;
                        break;
                    case "UnionType":
                        index = 83;
                        break;
                    case "Dimension":
                        index = 84;
                        break;
                    case "LambdaExpression":
                        index = 85;
                        break;
                    case "IntersectionType":
                        index = 86;
                        break;
                    case "NameQualifiedType":
                        index = 87;
                        break;
                    case "CreationReference":
                        index = 88;
                        break;
                    case "ExpressionMethodReference":
                        index = 89;
                        break;
                    case "SuperMethhodReference":
                        index = 90;
                        break;
                    case "TypeMethodReference":
                        index = 91;
                        break;
                    case "INFIX_EXPRESSION_OPERATOR":
                        index = 92;
                        break;
                    case "METHOD_INVOCATION_RECEIVER":
                        index = 93;
                        break;
                    case "METHOD_INVOCATION_ARGUMENTS":
                        index = 94;
                        break;
                    case "TYPE_DECLARATION_KIND":
                        index = 95;
                        break;
                    case "ASSIGNEMENT_OPERATOR":
                        index = 96;
                        break;
                    case "PREFIX_EXPRESSION_OPERATOR":
                        index = 97;
                        break;
                    case "POSTFIX_EXPRESSION_OPERATOR":
                        index = 98;
                        break;
                    default:
                        System.out.println(action.getNode().getType().name);
                }
                if (action.getName().contains("insert")) {
                } else if (action.getName().contains("update")) {
                    index += 99 * 1;
                } else if (action.getName().contains("move")) {
                    index += 99 * 2;
                } else if (action.getName().contains("delete")) {
                    index += 99 * 3;
                }
                nodeCommit4Experiment.semantics[index]++;
            }
            //2. author
            nodeCommit4Experiment.author = commitsAll.get(commitOnModule.idCommit).author;

            //4. interval
            int interval = 0;
            for (CommitOnModule commitOnModuleParent : commitOnModule.parents.values()) {
                interval += (commitOnModule.date - commitOnModuleParent.date) / (60 * 60 * 24);
            }
            nodeCommit4Experiment.interval = interval;
            //5. code churn
            nodeCommit4Experiment.churn[0] = commitOnModule.calcNOAddedLines();
            nodeCommit4Experiment.churn[1] = commitOnModule.calcNODeletedLines();
            nodeCommit4Experiment.churn[2] = nodeCommit4Experiment.churn[0] - nodeCommit4Experiment.churn[1];
            //6. co-change
            for (CommitOnModule changeOnModuleCoCommit : commitsAll.get(commitOnModule.idCommit).idParent2Modifications.get(commitOnModule.idCommitParent).values()) {
                //pathOld
                if (!Objects.equals(changeOnModuleCoCommit.pathOld, "/dev/null")){
                    nodeCommit4Experiment.coupling.add(changeOnModuleCoCommit.pathOld);
                }
                //pathNew
                if (Objects.equals(changeOnModuleCoCommit.pathNew, "/dev/null")) {
                    nodeCommit4Experiment.coupling.add(changeOnModuleCoCommit.pathNew);
                }
            }
            //others
            nodeCommit4Experiment.isMerge = commitOnModule.isMerge;
            nodeCommit4Experiment.isFixingBug = bugs.calculateIsFix(commitOnModule.idCommit);

            commitGraph.add(nodeCommit4Experiment);
        }
        Commit commitHead = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]);
        NodeCommit4Experiment nodeCommit4ExperimentHead = new NodeCommit4Experiment();
        nodeCommit4ExperimentHead.num = 0;
        nodeCommit4ExperimentHead.interval = (commitHead.date - commitOnModulesTarget.get(0).date) / (60 * 60 * 24);
        nodeCommit4ExperimentHead.author = "dummy";
        for (CommitOnModule commitOnModuleParent : commitOnModulesHead) {
            nodeCommit4ExperimentHead.parents.add(id2Num.get(commitOnModuleParent.idCommit + commitOnModuleParent.idCommitParent));
        }
        this.commitGraph.add(nodeCommit4ExperimentHead);
    }
    private List<CommitOnModule> identifyChangeOnModuleHead(Commits commitsAll, String revisionMethod_target) {

        List<CommitOnModule> commitOnModulesHead = new ArrayList<>();
        Commit commit = commitsAll.get(revisionMethod_target);
        List<String> idsCommit = changesOnModuleInInterval.values().stream().map(a -> a.idCommit).collect(Collectors.toList());
        while (true) {
            if (Objects.equals(commit, null)) {
                break;
            } else if (idsCommit.contains(commit.id)) {
                for (CommitOnModule CommitOnModule : changesOnModuleInInterval.values()) {
                    if (Objects.equals(CommitOnModule.idCommit, commit.id)) {
                        commitOnModulesHead.add(CommitOnModule);
                    }
                }
                break;
            }
            commit = commitsAll.get(commit.idParentMaster);
        }
        //違うAddから始まってるなら、それらのグラフは別々。片方を消す。
        return commitOnModulesHead;
    }
    public boolean checkIfTheChangeIs(List<CommitOnModule> commitOnModulesTarget, CommitOnModule CommitOnModule) {
        for (CommitOnModule commitOnModuleChild : CommitOnModule.children.values()) {
            for (CommitOnModule commitOnModuleTarget : commitOnModulesTarget) {
                if (Objects.equals(commitOnModuleChild.idCommit, commitOnModuleTarget.idCommit)) {
                    return true;
                }
            }
        }
        return false;
    }
    public void calcAST() { sourcecode.calcAST(); }
}