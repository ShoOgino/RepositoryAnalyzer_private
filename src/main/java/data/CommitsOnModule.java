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
public class CommitsOnModule  implements Cloneable {
    // todo MultiMapクラスに変更して、DeserializerModificationクラスを消す。
    @JsonDeserialize(keyUsing = DeserializerModification.class)
    private final MultiKeyMap<String, CommitOnModule> commitsOnModule = new MultiKeyMap<>();
    // future-dependent metrics
    int isBuggy = 0;
    public void calcIsBuggy(int dateTarget, int dateUntilReferableCalculatingIsBuggy) {
        for (CommitOnModule commitOnModuleFixing : commitsOnModule.values()) {
            for (String idCommitInducingBugsThatThisCommitFixes : commitOnModuleFixing.IdsCommitsInducingBugsThatThisCommitFixes) {
                try {
                    CommitOnModule commitOnModuleInducing = queryByIdCommit(idCommitInducingBugsThatThisCommitFixes).get(0);
                    if (commitOnModuleInducing.date < dateTarget & dateTarget < commitOnModuleFixing.date & commitOnModuleFixing.date < dateUntilReferableCalculatingIsBuggy) {
                        this.isBuggy = 1;
                        return;
                    }
                }catch(Exception e){
                    System.out.println(commitOnModuleFixing.pathNew);
                    for(CommitOnModule commitOnModule:commitsOnModule.values()) {
                        System.out.println(commitOnModule.idCommit);
                    }
                    System.out.println();
                    System.out.println(commitOnModuleFixing.idCommit);
                    System.out.println(commitOnModuleFixing.IdsCommitsInducingBugsThatThisCommitFixes);
                    //e.printStackTrace();
                }
            }
        }
    }
    int getBuggy = 0;
    public void calcGetBuggy(int dateCommitInducingBugsReferableFrom, int dateTarget, int dateCommitFixingBugsReferableUntil) {
    }
    // future-independent metrics
    int hasBeenFixed =0;
    public int calcHasBeenFixed(){
        for(CommitOnModule commitOnModule:commitsOnModule.values()){
            commitOnModule.calcIsFix();
            if(0 < commitOnModule.IdsCommitsInducingBugsThatThisCommitFixes.size()){
                hasBeenFixed =1;
                return hasBeenFixed;
            }
        }
        return 0;
    }
    int periodExisting = 0;
    public void calcPeriodExisting(int dateFrom, int dateUntil) {
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            if (Objects.equals(commitOnModule.type, "ADD")) {
                dateFrom = commitOnModule.date;
            }
        }
        this.periodExisting = (dateUntil - dateFrom) / (60 * 60 * 24 * 7);
    }
    double periodExistingWeighted = 0;
    public void calcPeriodExistingWeighted(int dateTo){
        int periodExistingWeightedTemp = 0;
        int numOfAdditionsLineAll=0;
        for(CommitOnModule commitOnModule: commitsOnModule.values()){
            int age = (dateTo-commitOnModule.date)/(60 * 60 * 24 * 7);
            commitOnModule.calcNumOfAdditionsLine();
            periodExistingWeightedTemp += age*commitOnModule.numOfAdditionsLine;
            numOfAdditionsLineAll += commitOnModule.numOfAdditionsLine;
        }
        if(numOfAdditionsLineAll==0)return;
        periodExistingWeighted = periodExistingWeightedTemp/(double)numOfAdditionsLineAll;
    }
    int numOfCommits = 0;
    public void calcNumOfCommits() {
        this.numOfCommits = commitsOnModule.size();
    }
    double numOfCommitsNeighbor = 0;
    public void calcNumOfCommitsNeighbor(Commits commitsAll, Modules modulesAll) {
        double numOfCommitsNeighborTemp = 0;
        Map<String, Integer> path2TimesCommittedSimultaneously = calcPath2TimesCommittedSimultaneously(commitsAll);
        if(path2TimesCommittedSimultaneously.size()==0)return;
        int numOfCommitOnModulesCommittedSimltaneouslyAll = 0;
        for (String path : path2TimesCommittedSimultaneously.keySet()) {
            numOfCommitOnModulesCommittedSimltaneouslyAll += path2TimesCommittedSimultaneously.get(path);
            Module moduleCommittedSimultaneously = modulesAll.get(path);
            numOfCommitsNeighborTemp += path2TimesCommittedSimultaneously.get(path) * moduleCommittedSimultaneously.commitsOnModuleInInterval.numOfCommits;
        }
        numOfCommitsNeighborTemp /= (double)numOfCommitOnModulesCommittedSimltaneouslyAll;
        this.numOfCommitsNeighbor = numOfCommitsNeighborTemp;
    }
    int numOfCommitsFixingBugs = 0;
    public void calcNumOfCommitsFixingBugs() {
        int numOfCommitsFixingBugsTemp = 0;
        for (CommitOnModule commitOnModuleFixing : commitsOnModule.values()) {
            if (0 < commitOnModuleFixing.IdsCommitsInducingBugsThatThisCommitFixes.size()) {
                numOfCommitsFixingBugsTemp += 1;
            }
        }
        this.numOfCommitsFixingBugs = numOfCommitsFixingBugsTemp;
    }
    int numOfCommitsRefactoring = 0;
    public void calcNumOfCommitsRefactoring(Commits commitsAll) {
        int numOfCommitsRefactoringTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            Commit commit = commitsAll.get(commitOnModule.idCommit);
            //todo
            if (commit.message.contains("refactor")) {
                numOfCommitsRefactoringTemp++;
            }
        }
        this.numOfCommitsRefactoring = numOfCommitsRefactoringTemp;
    }
    int numOfBugreportsFixed = 0;
    public void calcNumOfBugreportsUnique() {
        Set<String> idsBugreports = new HashSet<String>();
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            idsBugreports.addAll(commitOnModule.IdsBugThatThisCommitFixing);
        }
        numOfBugreportsFixed = idsBugreports.size();
    }
    int numOfCommitsInducingBugs = 0;
    public void calcNumOfCommitsInducingBugs() {
        int numOfCommitsInducingBugsTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcIsInduce(commitsOnModule.values().stream().map(item -> item.idCommit).collect(Collectors.toList()));
            if (commitOnModule.isInduce) numOfCommitsInducingBugsTemp++;
        }
        numOfCommitsInducingBugs = numOfCommitsInducingBugsTemp;
    }
    int numOfCommitsOtherModulesHasBeenFixedOnTheCommit = 0;
    public void calcNumOfCommitsOtherModulesHasBeenFixedOnTheCommit(Commits commitsAll, Modules modulesAll) {
        int numOfCommitsOtherModulesHasBeenBuggyOnTheCommitTemp = 0;
        for (CommitOnModule commitOnModule : this.commitsOnModule.values()) {
            commitOnModule.calcNumOfModulesHasBeenFixedOnTheCommit(commitsAll, modulesAll);
            if (0 < commitOnModule.numOfModulesHasBeenFixedOnTheCommit) {
                numOfCommitsOtherModulesHasBeenBuggyOnTheCommitTemp++;
            }
        }
        this.numOfCommitsOtherModulesHasBeenFixedOnTheCommit = numOfCommitsOtherModulesHasBeenBuggyOnTheCommitTemp;
    }
    int numOfCommitsOtherModulesGetBuggyOnTheCommit = 0;
    public void calcNumOfCommitsOtherModulesGetBuggyOnTheCommit(Commits commitsAll) {
        int numOfCommitsOtherModulesGetBuggyOnTheCommitTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfModulesGetBuggyOnTheCommit(commitsAll);
            if (0 < commitOnModule.numOfModulesGetBuggyOnTheCommit) {
                numOfCommitsOtherModulesGetBuggyOnTheCommitTemp++;
            }
        }
        this.numOfCommitsOtherModulesGetBuggyOnTheCommit = numOfCommitsOtherModulesGetBuggyOnTheCommitTemp;
    }
    int numOfCommittersUnique = 0;
    public void calcNumOfCommittersUnique() {
        Set<String> setAuthors = new HashSet<>();
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            setAuthors.add(commitOnModule.author);
        }
        this.numOfCommittersUnique = setAuthors.size();
    }
    double numOfCommittersUniqueNeighbor = 0;
    public void calcNumOfCommittersUniqueNeighbor(Commits commitsAll, Modules modulesAll) {
        double numOfCommittersUniqueNeighborTemp = 0;
        Map<String, Integer> path2TimesCommittedSimultaneously = calcPath2TimesCommittedSimultaneously(commitsAll);
        if(path2TimesCommittedSimultaneously.size()==0)return;
        int numOfCommitOnModulesCommittedSimltaneouslyAll = 0;
        for (String path : path2TimesCommittedSimultaneously.keySet()) {
            numOfCommitOnModulesCommittedSimltaneouslyAll += path2TimesCommittedSimultaneously.get(path);
            Module moduleCommittedSimultaneously = modulesAll.get(path);
            numOfCommittersUniqueNeighborTemp += path2TimesCommittedSimultaneously.get(path) * moduleCommittedSimultaneously.commitsOnModuleInInterval.numOfCommittersUnique;
        }
        numOfCommittersUniqueNeighborTemp /= numOfCommitOnModulesCommittedSimltaneouslyAll;
        this.numOfCommittersUniqueNeighbor = numOfCommittersUniqueNeighborTemp;
    }
    int numOfCommittersUnfamiliar = 0;
    public void calcNumOfCommittersUnfamiliar() {
        Set<String> setAuthors = new HashSet<>();
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            setAuthors.add(commitOnModule.author);
        }

        int numOfCommittersMinorTemp = 0;
        for (String nameAuthor : setAuthors) {
            int numOfCommitsByTheAuthor = (int) commitsOnModule.values().stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            double ownership = numOfCommitsByTheAuthor / (float) commitsOnModule.size();
            if (ownership < 0.2) {
                numOfCommittersMinorTemp++;
            }
        }
        this.numOfCommittersUnfamiliar = numOfCommittersMinorTemp;
    }
    int numOfCommittersFamiliar = 0;
    public void calcNumOfCommittersFamiliar() {
        Set<String> setAuthors = new HashSet<>();
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            setAuthors.add(commitOnModule.author);
        }

        int numOfCommittersMajorTemp = 0;
        for (String nameAuthor : setAuthors) {
            int numOfCommitsByTheAuthor = (int) commitsOnModule.values().stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            double ownership = numOfCommitsByTheAuthor / (float) commitsOnModule.size();
            if (0.2 <= ownership) {
                numOfCommittersMajorTemp++;
            }
        }
        this.numOfCommittersFamiliar = numOfCommittersMajorTemp;
    }
    double maxOfRatio_numOfCommitsOfACommitter = 0;
    public void calcMaxOfRatio_numOfCommitsOfACommitter() {
        Set<String> setAuthors = new HashSet<>();
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            setAuthors.add(commitOnModule.author);
        }

        for (String nameAuthor : setAuthors) {
            int numOfCommitsOfTheCommitter = (int) commitsOnModule.values().stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            double numOfcommitsOfACommitterByNumOfCommitsAll = numOfCommitsOfTheCommitter / (float) commitsOnModule.size();
            if (this.maxOfRatio_numOfCommitsOfACommitter < numOfcommitsOfACommitterByNumOfCommitsAll) {
                this.maxOfRatio_numOfCommitsOfACommitter = numOfcommitsOfACommitterByNumOfCommitsAll;
            }
        }
    }
    double maxOfRatio_numOfChangesLineOfACommitter = 0;
    public void calc_maxOfRatio_numOfChangesLineOfACommitter() {
        Map<String, Integer> author2NumOfChangesLine = calcAuthor2NumOfChangesLine();
        int maxOfNumOfChangesByAnAuthor = 0;
        for (Integer numOfChangesByTheAuthor : author2NumOfChangesLine.values()) {
            if (maxOfNumOfChangesByAnAuthor < numOfChangesByTheAuthor) {
                maxOfNumOfChangesByAnAuthor = numOfChangesByTheAuthor;
            }
        }
        calcSumOfChangesLine();
        if(this.sumOfChangesLine==0) return;
        this.maxOfRatio_numOfChangesLineOfACommitter = maxOfNumOfChangesByAnAuthor / (double) this.sumOfChangesLine;
    }
    double geometricmean_sumOfChangesLineByTheCommitter = 0;
    public void calcGeometricmean_sumOfChangesLineByTheCommitter() {
        double temp = 1;
        Map<String, Integer> author2NumOfChangesLine = calcAuthor2NumOfChangesLine();
        if(author2NumOfChangesLine.size()==0)return;
        for (Integer numOfChangesLine : author2NumOfChangesLine.values()) {
            temp *= numOfChangesLine;
        }
        this.geometricmean_sumOfChangesLineByTheCommitter = Math.pow(temp, 1.0 / author2NumOfChangesLine.size());
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
        this.avgOfInterval = (sumInterval / (float) (commitOnModulesSorted.size() - 1)) / (60 * 60 * 24 * 7);
    }
    int sumOfAdditionsLine = 0;
    public void calcSumOfAdditionsLine() {
        int sumOfAdditionsLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfAdditionsLine();
            sumOfAdditionsLineTemp += commitOnModule.numOfAdditionsLine;
        }
        this.sumOfAdditionsLine = sumOfAdditionsLineTemp;
    }
    int maxOfAdditionsLine = 0;
    public void calcMaxOfAdditionsLine() {
        int maxOfAdditionsLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfAdditionsLine();
            if (maxOfAdditionsLineTemp < commitOnModule.numOfAdditionsLine) {
                maxOfAdditionsLineTemp = commitOnModule.numOfAdditionsLine;
            }
        }
        this.maxOfAdditionsLine = maxOfAdditionsLineTemp;
    }
    double avgOfAdditionsLine = 0;
    public void calcAvgOfAdditionsLine() {
        if(0==commitsOnModule.size())return;
        int sumOfAdditionsLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfAdditionsLine();
            sumOfAdditionsLineTemp += commitOnModule.numOfAdditionsLine;
        }
        this.avgOfAdditionsLine = sumOfAdditionsLineTemp / (double) commitsOnModule.size();
    }
    int sumOfDeletionsLine = 0;
    public void calcSumOfDeletionsLine() {
        int sumOfDeletionsLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfDeletionsLine();
            sumOfDeletionsLineTemp += commitOnModule.numOfDeletionsLine;
        }
        this.sumOfDeletionsLine = sumOfDeletionsLineTemp;
    }
    int maxOfDeletionsLine = 0;
    public void calcMaxOfDeletionsLine() {
        int maxOfDeletionsLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfDeletionsLine();
            if (maxOfDeletionsLineTemp < commitOnModule.numOfDeletionsLine) {
                maxOfDeletionsLineTemp = commitOnModule.numOfDeletionsLine;
            }
        }
        this.maxOfDeletionsLine = maxOfDeletionsLineTemp;
    }
    double avgOfDeletionsLine = 0;
    public void calcAvgOfDeletionsLine() {
        if(0==commitsOnModule.size())return;
        int sumOfDeletionsLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfDeletionsLine();
            sumOfDeletionsLineTemp += commitOnModule.numOfDeletionsLine;
        }
        this.avgOfDeletionsLine = sumOfDeletionsLineTemp / (double) commitsOnModule.size();
    }
    int sumOfChangesLine = 0;
    public void calcSumOfChangesLine() {
        calcSumOfAdditionsLine();
        calcSumOfDeletionsLine();
        this.sumOfChangesLine = sumOfAdditionsLine + sumOfDeletionsLine;
    }
    int sumOfChurnLine = 0;
    public void calcSumOfChurnLine() {
        int sumOfChurnLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfChurnLine();
            sumOfChurnLineTemp += commitOnModule.numOfChurnLine;
        }
        this.sumOfChurnLine = sumOfChurnLineTemp;
    }
    int maxOfChurnLine = 0;
    public void calcMaxOfChurnLine() {
        int maxOfChurnLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfChurnLine();
            if (maxOfChurnLineTemp < commitOnModule.numOfChurnLine) {
                maxOfChurnLineTemp = commitOnModule.numOfChurnLine;
            }
        }
        this.maxOfChurnLine = maxOfChurnLineTemp;
    }
    double avgOfChurnLine = 0;
    public void calcAvgOfChurnLine() {
        if(commitsOnModule.size()==0)return;
        int sumOfChurnLineTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfChurnLine();
            sumOfChurnLineTemp += commitOnModule.numOfChurnLine;
        }
        this.avgOfChurnLine = sumOfChurnLineTemp / (double) commitsOnModule.size();
    }
    int sumOfAdditionsStatement = 0;
    public void calcSumOfAdditionsStatement() {
        int sumStmtAddedTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfAdditionsStatement();
            sumStmtAddedTemp += commitOnModule.numOfAdditionsStatement;
        }
        this.sumOfAdditionsStatement = sumStmtAddedTemp;
    }
    int maxOfAdditionsStatement = 0;
    public void calcMaxOfAdditionsStatement() {
        int maxStmtAddedTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
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
            commitOnModule.calcNumOfDeletionsStatement();
            sumStmtDeletedTemp += commitOnModule.numOfDeletionsStatement;
        }
        this.sumOfDeletionsStatement = sumStmtDeletedTemp;
    }
    int maxOfDeletionsStatement = 0;
    public void calcMaxOfDeletionsStatement() {
        int maxStmtDeleted = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
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
            commitOnModule.calcNumOfChurnsStatement();
            sumChurn += commitOnModule.numOfChurnsStatement;
        }
        this.sumOfChurnsStatement = sumChurn;
    }
    int maxOfChurnsStatement = 0;
    public void calcMaxOfChurnsStatement() {
        int maxChurn = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
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
    int sumOfChangesStatement = 0;
    public void calcSumOfChangesStatement(){
        calcSumOfAdditionsStatement();
        calcSumOfDeletionsStatement();
        this.sumOfChangesStatement = this.sumOfAdditionsStatement+this.sumOfDeletionsStatement;
    }
    int maxOfChangesStatement = 0;
    public void calcMaxOfChangesStatement(){
        int temp = 0;
        for(CommitOnModule commitOnModule: commitsOnModule.values()) {
            commitOnModule.calcNumOfChangesStatement();
            if (temp < commitOnModule.numOfChangesStatement) {
                temp = commitOnModule.numOfChangesStatement;
            }
        }
        maxOfChangesStatement = temp;
    }
    double avgOfChangesStatement=0;
    public void calcAvgOfChangesStatement(){
        calcSumOfChangesStatement();
        calcNumOfCommits();
        if (this.numOfCommits == 0) this.avgOfChangesStatement = 0;
        else this.avgOfChangesStatement = sumOfChangesStatement / (float) numOfCommits;
    }
    int sumOfChangesDeclarationItself = 0;
    public void calcSumOfChangesDeclarationItself() {
        int sumDeclTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfChangesDeclarationItself();
            sumDeclTemp += commitOnModule.numOfChangesDeclarationItself;
        }
        this.sumOfChangesDeclarationItself = sumDeclTemp;
    }
    int sumOfChangesCondition = 0;
    public void calcSumOfChangesCondition() {
        int sumCondTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfChangesCondition();
            sumCondTemp += commitOnModule.numOfChangesCondition;
        }
        this.sumOfChangesCondition = sumCondTemp;
    }
    int sumOfAdditionStatementElse = 0;
    public void calcSumOfAdditionStatementElse() {
        int sumElseAdded = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfAdditionsStatementElse();
            sumElseAdded += commitOnModule.numOfAdditionsStatementElse;
        }
        this.sumOfAdditionStatementElse = sumElseAdded;
    }
    int sumOfDeletionStatementElse = 0;
    public void calcSumOfDeletionStatementElse() {
        int sumOfDeletionStatementElseTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcNumOfDeletionsStatementElse();
            sumOfDeletionStatementElseTemp += commitOnModule.numOfDeletionsStatementElse;
        }
        this.sumOfDeletionStatementElse = sumOfDeletionStatementElseTemp;
    }
    int sumOfChangesStatementElse = 0;
    public void calcSumOfChangesStatementElse() {
        calcSumOfAdditionStatementElse();
        calcSumOfDeletionStatementElse();
        sumOfChangesStatementElse = sumOfAdditionStatementElse + sumOfDeletionStatementElse;
    }
    double complexityHistory = 0;
    public void calcComplexityHistory(Commits commits) {
        List<Double> entropiesOnCommitsAndModule = new ArrayList<>();
        List<Set<Commit>> listCommitsInABlock = commits.calcListCommitsInBlock(commitsOnModule.values().stream().map(item->item.idCommit).collect(Collectors.toSet()));
        for(Set<Commit> commitsInBlock: listCommitsInABlock){
            //　コミットブロックについて、entropyOnCommitsを算出
            Map<String, Integer> path2ChangesLine = new HashMap<>();
            for (Commit commit : commitsInBlock) {
                for (CommitsOnModule commitsOnModule : commit.idParent2Modifications.values()) {
                    for (CommitOnModule commitOnModule1 : commitsOnModule.values()) {
                        String path = Objects.equals(commitOnModule1.type, "DELETE") ? commitOnModule1.pathOld : commitOnModule1.pathNew;
                        commitOnModule1.calcNumOfChangesLine();
                        if (path2ChangesLine.containsKey(path)) {
                            path2ChangesLine.replace(path, path2ChangesLine.get(path) + commitOnModule1.numOfChangesLine);
                        } else {
                            path2ChangesLine.put(path, commitOnModule1.numOfChangesLine);
                        }
                    }
                }
            }
            double entropyOnCommits = 0;
            int numOfChangesAll = path2ChangesLine.values().stream().mapToInt(val->val).sum();
            for (int numOfChangesTheModule : path2ChangesLine.values()) {
                if(numOfChangesTheModule == 0) continue; //renameだけの場合とか
                if (path2ChangesLine.size() < 2) continue;
                double p = numOfChangesTheModule / (double) numOfChangesAll;
                double log_n_p = Math.log(p) / Math.log(path2ChangesLine.size());
                double temp = Math.abs(p * log_n_p);
                entropyOnCommits += temp;
            }
            // コミットブロックについて、HCPF_3を算出
            double entropyOnCommitsAndModule = entropyOnCommits / (double) path2ChangesLine.size();
            entropiesOnCommitsAndModule.add(entropyOnCommitsAndModule);
        }
        // モジュールについてHCM_3sを算出(そのモジュールへのコミットが属するコミットブロックのHCPF_3を足し合わせる)
        this.complexityHistory = entropiesOnCommitsAndModule.stream().mapToDouble(val -> val).sum();
    }
    double complexityHistoryNeighbor = 0;
    public void calcComplexityHistoryNeighbor(Commits commitsAll, Modules modulesAll) {
        double complexityHistoryNeighborTemp = 0;
        Map<String, Integer> path2TimesCommittedSimultaneously = calcPath2TimesCommittedSimultaneously(commitsAll);
        if(path2TimesCommittedSimultaneously.size()==0)return;
        int numOfCommitsByAllCommitters = 0;
        for (String path : path2TimesCommittedSimultaneously.keySet()) {
            numOfCommitsByAllCommitters += path2TimesCommittedSimultaneously.get(path);
            Module moduleCommittedSimultaneously = modulesAll.get(path);
            complexityHistoryNeighborTemp += path2TimesCommittedSimultaneously.get(path) * moduleCommittedSimultaneously.commitsOnModuleInInterval.complexityHistory;
        }
        complexityHistoryNeighborTemp /= numOfCommitsByAllCommitters;
        this.complexityHistoryNeighbor = complexityHistoryNeighborTemp;
    }
    int maxOfModulesCommittedSimultaneously = 0;
    public void calcMaxOfModulesCommittedSimultaneously(Commits commitsAll) {
        int maxOfModulesCommittedSimultaneouslyTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            Commit commit = commitsAll.get(commitOnModule.idCommit);
            for (CommitsOnModule commitsOnModule1 : commit.idParent2Modifications.values()) {
                if (maxOfModulesCommittedSimultaneouslyTemp < commitsOnModule1.size())
                    maxOfModulesCommittedSimultaneouslyTemp = commitsOnModule1.size();
            }
        }
        this.maxOfModulesCommittedSimultaneously = maxOfModulesCommittedSimultaneouslyTemp;
    }
    double avgOfModulesCommittedSimultaneously = 0;
    public void calcAvgOfModulesCommittedSimultaneously(Commits commitsAll) {
        if(0==commitsOnModule.size())return;
        int sumOfModulesCommittedSimultaneouslyTemp = 0;
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            Commit commit = commitsAll.get(commitOnModule.idCommit);
            for (CommitsOnModule commitsOnModule1 : commit.idParent2Modifications.values()) {
                sumOfModulesCommittedSimultaneouslyTemp += commitsOnModule1.size();
            }
        }
        this.avgOfModulesCommittedSimultaneously = sumOfModulesCommittedSimultaneouslyTemp / (double) commitsOnModule.size();
    }
    public List<CommitOnModule> queryByIdCommit(String idCommit) {
        return commitsOnModule.values().stream().filter(a -> a.idCommit.equals(idCommit)).collect(Collectors.toList());
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
    public void calcVectorsOnEachNode(Commits commitsAll, Modules modulesAll, Committers authors) {
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            commitOnModule.calcVectors(commitsAll, modulesAll, authors);
        }
    }
    public CommitOnModule get(String idCommitParent, String idCommit, String pathOld, String pathNew) {
        return commitsOnModule.get(idCommitParent, idCommit, pathOld, pathNew);
    }
    public List<CommitOnModule> queryIdCommit(String idCommit) {
        return commitsOnModule.values().stream().filter(a -> a.idCommit.equals(idCommit)).collect(Collectors.toList());
    }
    public List<CommitOnModule> queryPathOld(String pathOld) {
        return commitsOnModule.values().stream().filter(a -> a.pathOld.equals(pathOld)).collect(Collectors.toList());
    }
    public List<CommitOnModule> queryPathNew(String pathNew) {
        return commitsOnModule.values().stream().filter(a -> a.pathNew.equals(pathNew)).collect(Collectors.toList());
    }
    public Map<String, Integer> calcPath2TimesCommittedSimultaneously(Commits commitsAll) {
        //同時にコミットされたファイルパスに対する、同時にコミットされた回数の辞書を取得
        Map<String, Integer> path2TimesCommittedSimultaneously = new HashMap<>();
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            for (CommitsOnModule commitsOnModuleOfModulesCommittedSimultaneously : commitsAll.get(commitOnModule.idCommit).idParent2Modifications.values()) {
                for (CommitOnModule commitOnModuleOfModuleCommittedSimultaneously : commitsOnModuleOfModulesCommittedSimultaneously.values()) {
                    String path = Objects.equals(commitOnModuleOfModuleCommittedSimultaneously.type, "DELETE") ? commitOnModuleOfModuleCommittedSimultaneously.pathOld : commitOnModuleOfModuleCommittedSimultaneously.pathNew;
                    if (commitsOnModule.values().stream().map(item -> item.pathNew).collect(Collectors.toList()).contains(path))
                        continue;
                    if (path2TimesCommittedSimultaneously.containsKey(path)) {
                        path2TimesCommittedSimultaneously.replace(path, path2TimesCommittedSimultaneously.get(path));
                    } else {
                        path2TimesCommittedSimultaneously.put(path, 1);
                    }
                }
            }
        }
        return path2TimesCommittedSimultaneously;
    }
    public Map<String, Integer> calcAuthor2NumOfChangesLine() {
        Map<String, Integer> author2NumOfChangesLine = new HashMap<>();
        for (CommitOnModule commitOnModule : commitsOnModule.values()) {
            String author = commitOnModule.author;
            int numOfChangesLine = commitOnModule.calcNumOfChangesLine();
            if (author2NumOfChangesLine.containsKey(author)) {
                author2NumOfChangesLine.replace(author, author2NumOfChangesLine.get(author) + numOfChangesLine);
            } else {
                author2NumOfChangesLine.put(author, numOfChangesLine);
            }
        }
        return author2NumOfChangesLine;
    }
    public CommitOnModule put(String idCommitParent, String idCommit, String pathOld, String pathNew, CommitOnModule commitOnModule) {
        commitsOnModule.put(idCommitParent, idCommit, pathOld, pathNew, commitOnModule);
        return commitOnModule;
    }
    public void excludeCommitsOutOfInterval(int dateBegin, int dateEnd) {
        commitsOnModule.entrySet().removeIf(entry -> entry.getValue().date < dateBegin | dateEnd < entry.getValue().date);
    }
    public void excludeCommitsMerge() {
        commitsOnModule.entrySet().removeIf(entry -> entry.getValue().isMerge);
    }
    public CommitsOnModule() {
    }
    public int size() {
        return commitsOnModule.size();
    }
    public boolean isEmpty() {
        return commitsOnModule.isEmpty();
    }
    public boolean containsKey(Object key) {
        return commitsOnModule.containsKey(key);
    }
    public boolean containsValue(Object value) {
        return commitsOnModule.containsValue(value);
    }
    public CommitOnModule get(Object key) {
        return commitsOnModule.get(key);
    }
    public CommitOnModule put(MultiKey<? extends String> key, CommitOnModule value) {
        return commitsOnModule.put(key, value);
    }
    public CommitOnModule remove(Object key) {
        return commitsOnModule.remove(key);
    }
    public void putAll(Map<? extends MultiKey<? extends String>, ? extends CommitOnModule> m) {
        commitsOnModule.putAll(m);
    }
    public void clear() {
        commitsOnModule.clear();
    }
    public Set<MultiKey<? extends String>> keySet() {
        return commitsOnModule.keySet();
    }
    public Collection<CommitOnModule> values() {
        return commitsOnModule.values();
    }
    public Set<Map.Entry<MultiKey<? extends String>, CommitOnModule>> entrySet() {
        return commitsOnModule.entrySet();
    }
    public void calcMetricsDependentOnFuture(int dateCommitInducingBugsReferableFrom, int dateTarget, int dateCommitFixingBugsReferableUntil, String selection) {
        calcIsBuggy(dateTarget, dateCommitFixingBugsReferableUntil);
        calcGetBuggy(dateCommitInducingBugsReferableFrom, dateTarget, dateCommitFixingBugsReferableUntil);
    }
    public void calcMetricsIndependentOnFuture1(Commits commitsAll, int dateFrom_ReferableToCalculateMetricsIndependentOfFuture, int dateUntil_ReferableToCalculateMetricsIndependentOfFuture, String selection) {
        if(Arrays.asList("all", "ming", "hata").contains(selection)) calcPeriodExisting(dateFrom_ReferableToCalculateMetricsIndependentOfFuture, dateUntil_ReferableToCalculateMetricsIndependentOfFuture);
        if(Arrays.asList("all", "ming").contains(selection)) calcPeriodExistingWeighted(dateUntil_ReferableToCalculateMetricsIndependentOfFuture);
        if(Arrays.asList("all", "giger", "hata", "ming").contains(selection)) calcNumOfCommits();
        if(Arrays.asList("all", "hata", "ming").contains(selection)) calcNumOfCommitsFixingBugs();
        if(Arrays.asList("all", "ming").contains(selection)) calcNumOfCommitsRefactoring(commitsAll);
        if(Arrays.asList("all", "hata").contains(selection)) calcNumOfBugreportsUnique();
        if(Arrays.asList("all").contains(selection)) calcNumOfCommitsInducingBugs();
        if(Arrays.asList("all", "hata").contains(selection)) calcNumOfCommitsOtherModulesGetBuggyOnTheCommit(commitsAll);
        if(Arrays.asList("all", "giger", "hata", "ming").contains(selection)) calcNumOfCommittersUnique();
        if(Arrays.asList("all", "hata", "ming").contains(selection)) calcNumOfCommittersUnfamiliar();
        if(Arrays.asList("all", "hata").contains(selection)) calcNumOfCommittersFamiliar();
        if(Arrays.asList("all", "hata").contains(selection)) calcMaxOfRatio_numOfCommitsOfACommitter();
        if(Arrays.asList("all", "ming").contains(selection)) calc_maxOfRatio_numOfChangesLineOfACommitter();
        if(Arrays.asList("all", "ming").contains(selection)) calcGeometricmean_sumOfChangesLineByTheCommitter();
        if(Arrays.asList("all", "hata").contains(selection)) calcMaxOfInterval();
        if(Arrays.asList("all", "hata").contains(selection)) calcMinOfInterval();
        if(Arrays.asList("all", "hata").contains(selection)) calcAvgOfInterval();
        if(Arrays.asList("all", "hata", "ming").contains(selection)) calcSumOfAdditionsLine();
        if(Arrays.asList("all", "ming").contains(selection)) calcMaxOfAdditionsLine();
        if(Arrays.asList("all", "ming").contains(selection)) calcAvgOfAdditionsLine();
        if(Arrays.asList("all", "ming").contains(selection)) calcSumOfDeletionsLine();
        if(Arrays.asList("all", "ming").contains(selection)) calcMaxOfDeletionsLine();
        if(Arrays.asList("all", "ming").contains(selection)) calcAvgOfDeletionsLine();
        if(Arrays.asList("all").contains(selection)) calcSumOfChangesLine();
        if(Arrays.asList("all", "ming").contains(selection)) calcSumOfChurnLine();
        if(Arrays.asList("all", "ming").contains(selection)) calcMaxOfChurnLine();
        if(Arrays.asList("all", "ming").contains(selection)) calcAvgOfChurnLine();
        if(Arrays.asList("all", "giger").contains(selection)) calcSumOfAdditionsStatement();
        if(Arrays.asList("all", "giger").contains(selection)) calcMaxOfAdditionsStatement();
        if(Arrays.asList("all", "giger").contains(selection)) calcAvgOfAdditionsStatement();
        if(Arrays.asList("all", "giger").contains(selection)) calcSumOfDeletionsStatement();
        if(Arrays.asList("all", "giger").contains(selection)) calcMaxOfDeletionsStatement();
        if(Arrays.asList("all", "giger").contains(selection)) calcAvgOfDeletionsStatement();
        if(Arrays.asList("all", "giger").contains(selection)) calcSumOfChurnsStatement();
        if(Arrays.asList("all", "giger").contains(selection)) calcMaxOfChurnsStatement();
        if(Arrays.asList("all", "giger").contains(selection)) calcAvgOfChurnsStatement();
        if(Arrays.asList("all", "ming").contains(selection)) calcSumOfChangesStatement();
        if(Arrays.asList("all").contains(selection)) calcMaxOfChangesStatement();
        if(Arrays.asList("all").contains(selection)) calcAvgOfChangesStatement();
        if(Arrays.asList("all", "giger", "ming").contains(selection)) calcSumOfChangesDeclarationItself();
        if(Arrays.asList("all", "giger", "ming").contains(selection)) calcSumOfChangesCondition();
        if(Arrays.asList("all", "giger").contains(selection)) calcSumOfAdditionStatementElse();
        if(Arrays.asList("all", "giger").contains(selection)) calcSumOfDeletionStatementElse();
        if(Arrays.asList("all", "ming").contains(selection)) calcSumOfChangesStatementElse();
        if(Arrays.asList("all", "ming").contains(selection)) calcComplexityHistory(commitsAll);
        if(Arrays.asList("all", "ming").contains(selection)) calcMaxOfModulesCommittedSimultaneously(commitsAll);
        if(Arrays.asList("all", "ming").contains(selection)) calcAvgOfModulesCommittedSimultaneously(commitsAll);
    }
    public void calcMetricsIndependentOnFuture2(Commits commitsAll, Modules modulesAll, String selection) {
        if(Arrays.asList("all", "hata").contains(selection)) calcNumOfCommitsOtherModulesHasBeenFixedOnTheCommit(commitsAll, modulesAll);
        if(Arrays.asList("all", "ming").contains(selection)) calcNumOfCommittersUniqueNeighbor(commitsAll, modulesAll);
        if(Arrays.asList("all", "ming").contains(selection)) calcNumOfCommitsNeighbor(commitsAll, modulesAll);
        if(Arrays.asList("all", "ming").contains(selection)) calcComplexityHistoryNeighbor(commitsAll, modulesAll);
    }
}
