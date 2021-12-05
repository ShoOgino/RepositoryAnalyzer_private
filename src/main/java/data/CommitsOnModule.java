package data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import misc.DeserializerModification;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.*;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class CommitsOnModule extends MultiKeyMap<String, CommitOnModule> implements Cloneable{
    @JsonDeserialize(keyUsing = DeserializerModification.class) private final MultiKeyMap<String, CommitOnModule> commitsOnModule = new MultiKeyMap<>();
    // future metrics
    int isBuggy = 0;
    public void calcIsBuggy(int dateTarget, int[] intervalDate_referableCalculatingIsBuggy) {
        for(CommitOnModule commitOnModuleFixing: commitsOnModule.values()){
            for(String idCommitInducingBugsThatThisCommitFixes: commitOnModuleFixing.IdsCommitsInducingBugsThatThisCommitFixes){
                CommitOnModule commitOnModuleInducing = queryByIdCommit(idCommitInducingBugsThatThisCommitFixes).get(0);
                if(commitOnModuleInducing.date<dateTarget & dateTarget<commitOnModuleFixing.date & commitOnModuleFixing.date<intervalDate_referableCalculatingIsBuggy[1]){
                    this.isBuggy=1;
                    return;
                }
            }
        }
    }
    int getBuggy = 0;
    public void calcGetBuggy(){}
    // past metrics
    int numOfCommits = 0;
    public void calcNumOfCommits() {
        int numOfCommitsTemp = 0;
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            if(commitOnModule.isMerge)continue;
            numOfCommitsTemp++;
        }
        this.numOfCommits = numOfCommitsTemp;
    }
    int numOfCommitsFixingBugs = 0;
    public void calcNumOfCommitsFixingBugs() {
        int numOfCommitsFixingBugsTemp = 0;
        for(CommitOnModule commitOnModuleFixing: commitsOnModule.values()) {
            if(0<commitOnModuleFixing.IdsCommitsInducingBugsThatThisCommitFixes.size()) {
                numOfCommitsFixingBugsTemp+=1;
            }
        }
        this.numOfCommitsFixingBugs = numOfCommitsFixingBugsTemp;
    }
    int numOfBugreportsFixed = 0;
    public void calcNumOfBugreportsUnique() {
        Set<String> idsBugreports = new HashSet<String>();
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            idsBugreports.addAll(commitOnModule.IdsBugThatThisCommitFixing);
        }
        numOfBugreportsFixed = idsBugreports.size();
    }
    int numOfCommitsInducingBugs = 0;
    public void calcNumOfCommitsInducingBugs(){
        int numOfCommitsInducingBugsTemp = 0;
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            commitOnModule.calcIsInduce(commitsOnModule.values().stream().map(item->item.idCommit).collect(Collectors.toList()));
            if(commitOnModule.isInduce)numOfCommitsInducingBugsTemp++;
        }
        numOfCommitsInducingBugs = numOfCommitsInducingBugsTemp;
    }
    int numOfCommitsOtherModulesHasBeenBuggyOnTheCommit=0;
    public void calcNumOfCommitsOtherModulesHasBeenBuggyOnTheCommit(Commits commitsAll, Modules modulesAll) {
        int numOfCommitsOtherModulesHasBeenBuggyOnTheCommitTemp = 0;
        for(CommitOnModule commitOnModule: this.commitsOnModule.values()){
            commitOnModule.calcNumOfModulesHasBeenBuggyOnTheCommit(commitsAll, modulesAll);
            if(0<commitOnModule.numOfModulesHasBeenBuggyOnTheCommit){
                numOfCommitsOtherModulesHasBeenBuggyOnTheCommitTemp++;
            }
        }
        this.numOfCommitsOtherModulesHasBeenBuggyOnTheCommit = numOfCommitsOtherModulesHasBeenBuggyOnTheCommitTemp;
    }
    int numOfCommitsOtherModulesGetBuggyOnTheCommit = 0;
    public void calcNumOfCommitsOtherModulesGetBuggyOnTheCommit(Commits commitsAll) {
        int numOfCommitsOtherModulesGetBuggyOnTheCommitTemp = 0;
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            commitOnModule.calcNumOfModulesGetBuggyOnTheCommit(commitsAll);
            if(0<commitOnModule.numOfModulesGetBuggyOnTheCommit){
                numOfCommitsOtherModulesGetBuggyOnTheCommitTemp++;
            }
        }
        this.numOfCommitsOtherModulesGetBuggyOnTheCommit = numOfCommitsOtherModulesGetBuggyOnTheCommitTemp;
    }
    int numOfCommittersUnique = 0;
    public void calcNumOfCommittersUnique() {
        Set<String> setAuthors = new HashSet<>();
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            if(commitOnModule.isMerge)continue;
            setAuthors.add(commitOnModule.author);
        }
        this.numOfCommittersUnique = setAuthors.size();
    }
    int numOfCommittersMinor = 0;
    public void calcNumOfCommittersMinor() {
        Set<String> setAuthors = new HashSet<>();
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            if(commitOnModule.isMerge)continue;
            setAuthors.add(commitOnModule.author);
        }

        int numOfCommittersMinorTemp = 0;
        for (String nameAuthor : setAuthors) {
            int count = (int) commitsOnModule.values().stream().filter(item -> Objects.equals(item.author, nameAuthor)&!item.isMerge).count();
            double ownership =  count / (float) commitsOnModule.values().stream().filter(item -> !item.isMerge).count();
            if ( ownership < 0.2) {
                numOfCommittersMinorTemp++;
            }
        }
        this.numOfCommittersMinor = numOfCommittersMinorTemp;
    }
    int numOfCommittersMajor = 0;
    public void calcNumOfCommittersMajor() {
        Set<String> setAuthors = new HashSet<>();
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            if(commitOnModule.isMerge)continue;
            setAuthors.add(commitOnModule.author);
        }

        int numOfCommittersMajorTemp = 0;
        for (String nameAuthor : setAuthors) {
            int count = (int) commitsOnModule.values().stream().filter(item -> Objects.equals(item.author, nameAuthor)&!item.isMerge).count();
            double ownership =  count / (float) commitsOnModule.values().stream().filter(item -> !item.isMerge).count();
            if ( 0.2 <= ownership) {
                numOfCommittersMajorTemp++;
            }
        }
        this.numOfCommittersMajor = numOfCommittersMajorTemp;
    }
    double ownership = 0;
    public void calcOwnership() {
        Set<String> setAuthors = new HashSet<>();
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            if(commitOnModule.isMerge)continue;
            setAuthors.add(commitOnModule.author);
        }

        int numOfCommittersMajorTemp = 0;
        for (String nameAuthor : setAuthors) {
            int count = (int) commitsOnModule.values().stream().filter(item -> Objects.equals(item.author, nameAuthor) & !item.isMerge).count();
            double ownershipTemp = count / (float) commitsOnModule.values().stream().filter(item -> !item.isMerge).count();
            if (this.ownership < ownershipTemp) {
                this.ownership = ownershipTemp;
            }
        }
    }
    int period = 0;
    public void calcPeriod(Commits commitsAll , int dateUntil) {
        int dateFrom = Integer.MAX_VALUE;
        for (CommitOnModule CommitOnModule : commitsOnModule.values()) {
            if (CommitOnModule.date < dateFrom) {
                dateFrom = CommitOnModule.date;
            }
        }
        this.period = (dateUntil - dateFrom) / (60 * 60 * 24);
    }
    int maxOfInterval = 0;
    public void calcMaxOfInterval() {
        int maxOfIntervalTemp = 0;
        List<CommitOnModule> commitOnModules = commitsOnModule.values().stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
        if (commitOnModules.size() < 2) {
            this.maxOfInterval = 0;
            return;
        }
        for (int i = 0; i < commitOnModules.size() - 1; i++) {
            int interval = commitOnModules.get(i + 1).date - commitOnModules.get(i).date;
            if (maxOfIntervalTemp < interval) {
                maxOfIntervalTemp = interval;
            }
        }
        this.maxOfInterval = maxOfIntervalTemp / (60 * 60 * 24 * 7);
    }
    int minOfInterval = 0;
    public void calcMinOfInterval() {
        int minInterval = Integer.MAX_VALUE;
        List<CommitOnModule> commitOnModules = commitsOnModule.values().stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
        if (commitOnModules.size() < 2) {
            this.minOfInterval = 0;
            return;
        }
        for (int i = 0; i < commitOnModules.size() - 1; i++) {
            int interval = commitOnModules.get(i + 1).date - commitOnModules.get(i).date;
            if (interval < minInterval) {
                minInterval = interval;
            }
        }
        this.minOfInterval = minInterval / (60 * 60 * 24 * 7);
    }
    double avgOfInterval = 0;
    public void calcAvgOfInterval() {
        int sumInterval = 0;
        List<CommitOnModule> commitOnModulesSorted = commitsOnModule.values().stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
        if (commitOnModulesSorted.size() <= 1) {
            this.avgOfInterval = 0;
            return;
        }
        for (int i = 0; i < commitOnModulesSorted.size() - 1; i++) {
            sumInterval += commitOnModulesSorted.get(i + 1).date - commitOnModulesSorted.get(i).date;
        }
        this.avgOfInterval = (sumInterval / (float) (commitOnModulesSorted.size()-1))/(60 * 60 * 24 * 7);
    }
    int sumOfAdditionsLine = 0;
    public void calcSumOfAdditionsLine() {
        int sumOfAdditionsLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfAdditionsLine();
            sumOfAdditionsLineTemp += commitOnModule.numOfAdditionsLine;
        }
        this.sumOfAdditionsLine = sumOfAdditionsLineTemp;
    }
    int sumOfDeletionsLine = 0;
    public void calcSumOfDeletionsLine() {
        int sumOfDeletionsLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfDeletionsLine();
            sumOfDeletionsLineTemp+=commitOnModule.numOfDeletionsLine;
        }
        this.sumOfDeletionsLine = sumOfDeletionsLineTemp;
    }
    int sumOfAdditionsStatement = 0;
    public void calcSumOfAdditionsStatement() {
        int sumStmtAddedTemp = 0;
        for (CommitOnModule commitOnModule: commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfAdditionsStatement();
            sumStmtAddedTemp+=commitOnModule.numOfAdditionsStatement;
        }
        this.sumOfAdditionsStatement = sumStmtAddedTemp;
    }
    int maxOfAdditionsStatement = 0;
    public void calcMaxOfAdditionsStatement() {
        int maxStmtAddedTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge) continue;
            commitOnModule.calcNumOfAdditionsStatement();
            if (maxStmtAddedTemp < commitOnModule.numOfAdditionsStatement) {
                maxStmtAddedTemp = commitOnModule.numOfAdditionsStatement;
            }
        }
        this.maxOfAdditionsStatement = maxStmtAddedTemp;
    }
    double avgOfAdditionsStatement = 0;
    public void calcAvgOfAdditionsStatement() {
        calcSumOfAdditionsStatement();
        calcNumOfCommits();
        if (numOfCommits == 0) this.avgOfAdditionsStatement = 0;
        else this.avgOfAdditionsStatement = this.sumOfAdditionsStatement / (double) numOfCommits;
    }
    int sumOfDeletionsStatement = 0;
    public void calcSumOfDeletionsStatement() {
        int sumStmtDeletedTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfDeletionsStatement();
            sumStmtDeletedTemp+=commitOnModule.numOfDeletionsStatement;
        }
        this.sumOfDeletionsStatement = sumStmtDeletedTemp;
    }
    int maxOfDeletionsStatement = 0;
    public void calcMaxOfDeletionsStatement() {
        int maxStmtDeleted = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfDeletionsStatement();
            if (maxStmtDeleted < commitOnModule.numOfDeletionsStatement) {
                maxStmtDeleted = commitOnModule.numOfDeletionsStatement;
            }
        }
        this.maxOfDeletionsStatement = maxStmtDeleted;
    }
    double avgOfDeletionsStatement = 0;
    public void calcAvgOfDeletionsStatement() {
        calcSumOfDeletionsStatement();
        calcNumOfCommits();
        if (numOfCommits == 0) this.avgOfDeletionsStatement = 0;
        else this.avgOfDeletionsStatement = this.sumOfDeletionsStatement / (double) this.numOfCommits;
    }
    int sumOfChurnsStatement = 0;
    public void calcSumOfChurnsStatement() {
        int sumChurn = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfChurnsStatement();
            sumChurn+=commitOnModule.numOfChurnsStatement;
        }
        this.sumOfChurnsStatement = sumChurn;
    }
    int maxOfChurnsStatement = 0;
    public void calcMaxOfChurnsStatement() {
        int maxChurn = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfChurnsStatement();
            if (maxChurn < commitOnModule.numOfChurnsStatement) maxChurn = commitOnModule.numOfChurnsStatement;
        }
        this.maxOfChurnsStatement = maxChurn;
    }
    double avgOfChurnsStatement = 0;
    public void calcAvgOfChurnsStatement() {
        calcSumOfChurnsStatement();
        calcNumOfCommits();
        if (this.numOfCommits == 0) this.avgOfChurnsStatement = 0;
        else this.avgOfChurnsStatement = sumOfChurnsStatement / (float) numOfCommits;
    }
    int sumOfChangesDeclaration = 0;
    public void calcSumOfChangesDeclaration() {
        int sumDeclTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfChangesDeclaration();
            sumDeclTemp+=commitOnModule.numOfChangesDeclaration;
        }
        this.sumOfChangesDeclaration = sumDeclTemp;
    }
    int sumOfChangesCondition = 0;
    public void calcSumOfChangesCondition() {
        int sumCondTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfChangesCondition();
            sumCondTemp+=commitOnModule.numOfChangesCondition;
        }
        this.sumOfChangesCondition = sumCondTemp;
    }
    int sumOfAdditionStatementElse = 0;
    public void calcSumOfAdditionStatementElse() {
        int sumElseAdded = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfAdditionsStatementElse();
            sumElseAdded+=commitOnModule.numOfAdditionsStatementElse;
        }
        this.sumOfAdditionStatementElse = sumElseAdded;
    }
    int sumOfDeletionStatementElse = 0;
    public void calcSumOfDeletionStatementElse() {
        int sumElseDeleted = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcNumOfDeletionsStatementElse();
            sumElseDeleted+=commitOnModule.numOfDeletionsStatementElse;
        }
        this.sumOfDeletionStatementElse = sumElseDeleted;
    }



    public List<CommitOnModule> queryByIdCommit(String idCommit){
        return commitsOnModule.values().stream().filter(a->a.idCommit.equals(idCommit)).collect(Collectors.toList());
    }
    @Override public CommitsOnModule clone() {
        CommitsOnModule commitsOnModule = null;
        try {
            commitsOnModule = (CommitsOnModule) super.clone();
        } catch (Exception e) {
            commitsOnModule = null;
        }
        return commitsOnModule;
    }
    public void calcMetricsOnEachNode() {
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcMetrics();
        }
    }
    public void calcVectorsOnEachNode(Commits commitsAll, Modules modulesAll, People authors) {
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcVectors(commitsAll, modulesAll, authors);
        }
    }
    public CommitOnModule get(String idCommitParent, String idCommit, String pathOld, String pathNew){
        return commitsOnModule.get(idCommitParent, idCommit, pathOld, pathNew);
    }
    public List<CommitOnModule> queryIdCommit(String idCommit) {
        return commitsOnModule.values().stream().filter(a->a.idCommit.equals(idCommit)).collect(Collectors.toList());
    }
    public List<CommitOnModule> queryPathOld(String pathOld) {
        return commitsOnModule.values().stream().filter(a->a.pathOld.equals(pathOld)).collect(Collectors.toList());
    }
    public List<CommitOnModule> queryPathNew(String pathNew) {
        return commitsOnModule.values().stream().filter(a->a.pathNew.equals(pathNew)).collect(Collectors.toList());
    }

    public CommitOnModule put(String idCommitParent, String idCommit, String pathOld, String pathNew, CommitOnModule commitOnModule){
        commitsOnModule.put(idCommitParent, idCommit, pathOld, pathNew, commitOnModule);
        return commitOnModule;
    }
    public void excludeCommitsOutOfInterval(int dateBegin, int dateEnd) {
        commitsOnModule.entrySet().removeIf(entry ->  entry.getValue().date<dateBegin | dateEnd<entry.getValue().date);
    }
    public void excludeCommitsMerge() {
        commitsOnModule.entrySet().removeIf(entry -> entry.getValue().isMerge);
    }
    public CommitsOnModule(){}
    public int size() { return commitsOnModule.size(); }
    public boolean isEmpty() { return commitsOnModule.isEmpty(); }
    public boolean containsKey(Object key) { return commitsOnModule.containsKey(key); }
    public boolean containsValue(Object value) { return commitsOnModule.containsValue(value); }
    public CommitOnModule get(Object key) { return commitsOnModule.get(key); }
    public CommitOnModule put(MultiKey<? extends String> key, CommitOnModule value) { return commitsOnModule.put(key, value); }
    public CommitOnModule remove(Object key) { return commitsOnModule.remove(key); }
    public void putAll(Map<? extends MultiKey<? extends String>, ? extends CommitOnModule> m) { commitsOnModule.putAll(m); }
    public void clear(){ commitsOnModule.clear(); }
    public Set<MultiKey<? extends String>> keySet() { return commitsOnModule.keySet(); }
    public Collection<CommitOnModule> values(){ return commitsOnModule.values(); }
    public Set<Map.Entry<MultiKey<? extends String>, CommitOnModule>> entrySet() { return commitsOnModule.entrySet(); }
}
