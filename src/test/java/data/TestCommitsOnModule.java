package data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCommitsOnModule {
    @Test public void testCalcPeriodExisting0(){
        //入力
        //CommitsOnModule:
        //    [0]: type="ADD", date=2000年1月1日00:00:00=946684800
        //dateTo: 2000年2月1日00:00:00=949363200
        //出力: 4(7days)
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.type ="ADD";
        commitOnModule.date =946684800;
        commitsOnModule.put("", "","","",commitOnModule);
        int dateTo = 949363200;
        commitsOnModule.calcPeriodExisting(0, dateTo);
        assertEquals(4, commitsOnModule.periodExisting);
    }
    @Test public void testCalcPeriodExistingWeighted0(){
        //入力
        //    CommitsOnModule:
        //        [0]: date= 2000年1月1日00:00:00=946684800 追加行=2
        //        [1]: date= 2000年2月1日00:00:00=949363200 追加行=1
        //    dateTo: 2001年3月1日00:00:00=951868800
        //過程
        //    [0]: age:8, loc_added=2
        //    [1]: age:4, loc_added=1
        //
        //出力: (8*2+4*1)/(3)=6.8......(7days)
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.date = 946684800;
        Diff diff0_0 = new Diff();
        diff0_0.linesAfter= new ArrayList<>(){{add(0);add(1);}};
        commitOnModule0.diffs.add(diff0_0);
        commitsOnModule.put("0","1","","",commitOnModule0);
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.date = 949363200;
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter= new ArrayList<>(){{add(0);}};
        commitOnModule1.diffs.add(diff1_0);
        int dateTo = 951868800;
        commitsOnModule.put("1","2","","",commitOnModule1);
        commitsOnModule.calcPeriodExistingWeighted(dateTo);
        assertEquals(String.format("%.5f",6.66666667), String.format("%.5f",commitsOnModule.periodExistingWeighted));
    }
    @Test public void testCalcNumOfCommits0(){
        //入力
        //    CommitsOnModule:
        //        [0]:
        //        [1]:
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitsOnModule.put("0","1","","",commitOnModule0);
        commitsOnModule.put("1","2","","",commitOnModule1);
        commitsOnModule.calcNumOfCommits();
        assertEquals(2, commitsOnModule.numOfCommits);
    }
    @Test public void testCalcNumOfCommitsFixingBugs0(){
        //入力
        //    CommitsOnModule:
        //        [0]: isFix=true
        //        [1]: isFix=false
        //        [2]: isFix=true
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.IdsCommitsInducingBugsThatThisCommitFixes = Arrays.asList("");
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.IdsCommitsInducingBugsThatThisCommitFixes = new ArrayList<>();
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.IdsCommitsInducingBugsThatThisCommitFixes = Arrays.asList("");
        commitsOnModule.put("0","1","","",commitOnModule0);
        commitsOnModule.put("1","2","","",commitOnModule1);
        commitsOnModule.put("2","3","","",commitOnModule2);
        commitsOnModule.calcNumOfCommitsFixingBugs();
        assertEquals(2, commitsOnModule.numOfCommitsFixingBugs);
    }
    @Test public void testCalcNumOfCommitsRefactoring0(){
        //入力
        //    CommitsOnModule:
        //        [0]: message = "refactor class A"
        //        [1]: message = "add a function"
        //        [2]: message = "refactoring"
        //出力: 2
        Commits commits = new Commits();
        Commit commit0 = new Commit();
        commit0.id = "0";
        commit0.message = "refactor class A";
        commits.put(commit0.id, commit0);
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.idCommit = "0";
        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.message = "add a function";
        commits.put(commit1.id, commit1);
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.idCommit = "1";
        Commit commit2 = new Commit();
        commit2.id = "2";
        commit2.message = "refactoring";
        commits.put(commit2.id,commit2);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.idCommit = "2";
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        commitsOnModule.put("0","1","","",commitOnModule0);
        commitsOnModule.put("1","2","","",commitOnModule1);
        commitsOnModule.put("2","3","","",commitOnModule2);
        commitsOnModule.calcNumOfCommitsRefactoring(commits);
        assertEquals(2, commitsOnModule.numOfCommitsRefactoring);
    }
    @Test public void testCalcNumOfBugreportsUnique0(){
        //入力
        //    CommitsOnModule:
        //        [0]: IdsBugThatThisCommitFixing=["123"]
        //        [1]: IdsBugThatThisCommitFixing=[]
        //        [2]: IdsBugThatThisCommitFixing=["340"]
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.IdsBugThatThisCommitFixing = Arrays.asList("123");
        commitsOnModule.put("0","1","a","a", commitOnModule0);
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.IdsBugThatThisCommitFixing = Arrays.asList();
        commitsOnModule.put("1","2","a","a", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.IdsBugThatThisCommitFixing = Arrays.asList("340");
        commitsOnModule.put("2","3","a","a", commitOnModule2);
        commitsOnModule.calcNumOfBugreportsUnique();
        assertEquals(2, commitsOnModule.numOfBugreportsFixed);
    }
    @Test public void testCalcNumOfCommitsInducingBugs0(){
        //入力
        //    CommitsOnModule:
        //        [0]: idCommit="0", IdsCommitsFixingBugThatThisCommitInduces["2"]
        //        [1]: idCommit="1", IdsCommitsFixingBugThatThisCommitInduces[]
        //        [2]: idCommit="2", IdsCommitsFixingBugThatThisCommitInduces["3"]
        //出力: 1
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.idCommit = "0";
        commitOnModule0.IdsCommitsFixingBugThatThisCommitInduces=Arrays.asList("2");
        commitsOnModule.put("0", "1", "a", "a", commitOnModule0);
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.idCommit = "1";
        commitOnModule1.IdsCommitsFixingBugThatThisCommitInduces=Arrays.asList();
        commitsOnModule.put("1", "2", "a", "a", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.idCommit = "2";
        commitOnModule2.IdsCommitsFixingBugThatThisCommitInduces=Arrays.asList("3");
        commitsOnModule.put("2", "3", "a", "a", commitOnModule2);
        commitsOnModule.calcNumOfCommitsInducingBugs();
        assertEquals(1, commitsOnModule.numOfCommitsInducingBugs);
    }
    @Test public void testCalcNumOfCommitsOtherModulesGetBuggyOnTheCommit0(){
        //入力
        //    Module "a"
        //        CommitsOnModule:
        //            [0]: idCommit="0" IdsCommitsFixingBugThatThisCommitInduces=["100"]
        //    Module "b"
        //        CommitsOnModule:
        //            [0]: idCommit="0"
        //            [1]: idCommit="1"
        //出力: 1
        Commits commits = new Commits();
        Commit commit0 = new Commit();
        commit0.id = "0";
        commit0.idParentMaster="00";
        CommitsOnModule commitsOnModule00_0 = new CommitsOnModule();
        CommitOnModule commitOnModule0_a = new CommitOnModule();
        commitOnModule0_a.idCommit="0";
        commitOnModule0_a.pathOld="a";
        commitOnModule0_a.pathNew="a";
        commitOnModule0_a.IdsCommitsFixingBugThatThisCommitInduces=Arrays.asList("100");
        commitsOnModule00_0.put("00","0","a","a", commitOnModule0_a);
        CommitOnModule commitOnModule0_b = new CommitOnModule();
        commitOnModule0_b.idCommit="0";
        commitOnModule0_b.pathOld="b";
        commitOnModule0_b.pathNew="b";
        commitsOnModule00_0.put("00","0","b","b", commitOnModule0_b);
        commit0.idParent2Modifications.put("00", commitsOnModule00_0);
        commits.put("0", commit0);

        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.idParentMaster = "0";
        CommitsOnModule commitsOnModule0_1 = new CommitsOnModule();
        CommitOnModule commitOnModule1_b = new CommitOnModule();
        commitOnModule1_b.idCommit="1";
        commitOnModule1_b.pathOld="b";
        commitOnModule1_b.pathNew="b";
        commitsOnModule0_1.put("0","1","b","b", commitOnModule1_b);
        commit1.idParent2Modifications.put("0",commitsOnModule0_1);
        commits.put("1", commit1);

        CommitsOnModule commitsOnModule_b = new CommitsOnModule();
        commitsOnModule_b.put("00","0","b","b",commitOnModule0_b);
        commitsOnModule_b.put("0","1","b","b",commitOnModule1_b);
        commitsOnModule_b.calcNumOfCommitsOtherModulesGetBuggyOnTheCommit(commits);
        assertEquals(1, commitsOnModule_b.numOfCommitsOtherModulesGetBuggyOnTheCommit);
    }
    @Test public void testCalcNumOfCommittersUnique0(){
        //入力
        //    CommitsOnModule:
        //        [0]: author="a"
        //        [1]: author="a"
        //        [2]: author="b"
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.author = "a";
        commitsOnModule.put("0","1","a","a", commitOnModule0);
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        commitsOnModule.put("1","2","a","a", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        commitsOnModule.put("2","3","a","a", commitOnModule2);
        commitsOnModule.calcNumOfCommittersUnique();
        assertEquals(2, commitsOnModule.numOfCommittersUnique);
    }
    @Test public void testCalcNumOfCommittersUnfamiliar0(){
        //入力
        //    CommitsOnModule:
        //        [0]: author="a"
        //        [1]: author="b"
        //        [2]: author="b"
        //        [3]: author="c"
        //        [4]: author="c"
        //        [5]: author="c"
        //        [6]: author="d"
        //        [7]: author="d"
        //        [8]: author="d"
        //        [9]: author="d"
        //出力: 1
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.author = "a";
        commitsOnModule.put("0","1","a","a", commitOnModule0);
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "b";
        commitsOnModule.put("1","2","b","b", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        commitsOnModule.put("2","3","b","b", commitOnModule2);
        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "c";
        commitsOnModule.put("3","4","c","c", commitOnModule3);
        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        commitsOnModule.put("5","6","c","c", commitOnModule4);
        CommitOnModule commitOnModule5 = new CommitOnModule();
        commitOnModule5.author = "c";
        commitsOnModule.put("6","7","c","c", commitOnModule5);
        CommitOnModule commitOnModule6 = new CommitOnModule();
        commitOnModule6.author = "d";
        commitsOnModule.put("7","8","d","d", commitOnModule6);
        CommitOnModule commitOnModule7 = new CommitOnModule();
        commitOnModule7.author = "d";
        commitsOnModule.put("8","9","d","d", commitOnModule7);
        CommitOnModule commitOnModule8 = new CommitOnModule();
        commitOnModule8.author = "d";
        commitsOnModule.put("9","10","d","d", commitOnModule8);
        CommitOnModule commitOnModule9 = new CommitOnModule();
        commitOnModule9.author = "d";
        commitsOnModule.put("10","11","d","d", commitOnModule9);
        commitsOnModule.calcNumOfCommittersUnfamiliar();
        assertEquals(1, commitsOnModule.numOfCommittersUnfamiliar);
    }
    @Test public void testCalcNumOfCommittersFamiliar0(){
        //入力
        //    CommitsOnModule:
        //        [0]: author="a"
        //        [1]: author="b"
        //        [2]: author="b"
        //        [3]: author="c"
        //        [4]: author="c"
        //        [5]: author="c"
        //        [6]: author="d"
        //        [7]: author="d"
        //        [8]: author="d"
        //        [9]: author="d"
        //出力: 3
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.author = "a";
        commitsOnModule.put("0","1","a","a", commitOnModule0);
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "b";
        commitsOnModule.put("1","2","b","b", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        commitsOnModule.put("2","3","b","b", commitOnModule2);
        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "c";
        commitsOnModule.put("3","4","c","c", commitOnModule3);
        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        commitsOnModule.put("5","6","c","c", commitOnModule4);
        CommitOnModule commitOnModule5 = new CommitOnModule();
        commitOnModule5.author = "c";
        commitsOnModule.put("6","7","c","c", commitOnModule5);
        CommitOnModule commitOnModule6 = new CommitOnModule();
        commitOnModule6.author = "d";
        commitsOnModule.put("7","8","d","d", commitOnModule6);
        CommitOnModule commitOnModule7 = new CommitOnModule();
        commitOnModule7.author = "d";
        commitsOnModule.put("8","9","d","d", commitOnModule7);
        CommitOnModule commitOnModule8 = new CommitOnModule();
        commitOnModule8.author = "d";
        commitsOnModule.put("9","10","d","d", commitOnModule8);
        CommitOnModule commitOnModule9 = new CommitOnModule();
        commitOnModule9.author = "d";
        commitsOnModule.put("10","11","d","d", commitOnModule9);
        commitsOnModule.calcNumOfCommittersFamiliar();
        assertEquals(3, commitsOnModule.numOfCommittersFamiliar);
    }
    @Test public void testCalcMaxOfRatio_numOfCommitsOfACommitter0(){
        //入力
        //    CommitsOnModule:
        //        [0]: author="a"
        //        [1]: author="b"
        //        [2]: author="b"
        //        [3]: author="c"
        //        [4]: author="c"
        //        [5]: author="c"
        //        [6]: author="d"
        //        [7]: author="d"
        //        [8]: author="d"
        //        [9]: author="d"
        //出力: 4/10=0.4
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule0 = new CommitOnModule();
        commitOnModule0.author = "a";
        commitsOnModule.put("0","1","a","a", commitOnModule0);
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "b";
        commitsOnModule.put("1","2","b","b", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        commitsOnModule.put("2","3","b","b", commitOnModule2);
        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "c";
        commitsOnModule.put("3","4","c","c", commitOnModule3);
        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        commitsOnModule.put("5","6","c","c", commitOnModule4);
        CommitOnModule commitOnModule5 = new CommitOnModule();
        commitOnModule5.author = "c";
        commitsOnModule.put("6","7","c","c", commitOnModule5);
        CommitOnModule commitOnModule6 = new CommitOnModule();
        commitOnModule6.author = "d";
        commitsOnModule.put("7","8","d","d", commitOnModule6);
        CommitOnModule commitOnModule7 = new CommitOnModule();
        commitOnModule7.author = "d";
        commitsOnModule.put("8","9","d","d", commitOnModule7);
        CommitOnModule commitOnModule8 = new CommitOnModule();
        commitOnModule8.author = "d";
        commitsOnModule.put("9","10","d","d", commitOnModule8);
        CommitOnModule commitOnModule9 = new CommitOnModule();
        commitOnModule9.author = "d";
        commitsOnModule.put("10","11","d","d", commitOnModule9);
        commitsOnModule.calcMaxOfRatio_numOfCommitsOfACommitter();
        assertEquals(0.4, Math.floor(commitsOnModule.maxOfRatio_numOfCommitsOfACommitter*10)/10);
    }
    @Test public void testCalc_maxOfRatio_numOfChangesLineOfACommitter0(){
        //入力
        //    CommitsOnModule:
        //        [0]: author="a" diff=+1
        //        [1]: author="b" diff=-1, +2
        //        [2]: author="b" diff=+2
        //        [3]: author="c" diff=+1
        //        [4]: author="c" diff=-1
        //        [5]: author="c" diff=+1
        //        [6]: author="d" diff=+1
        //        [7]: author="d" diff=+2
        //        [8]: author="d" diff=-2+1
        //        [9]: author="d" diff=+3
        //出力: 9/18 = 0.5
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesBefore = Arrays.asList(1);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        diff2_0.linesAfter = Arrays.asList(1,2);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesAfter = Arrays.asList(1,2);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesAfter = Arrays.asList(1);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        CommitOnModule commitOnModule5 = new CommitOnModule();
        commitOnModule5.author = "c";
        Diff diff5_0 = new Diff();
        diff5_0.linesBefore = Arrays.asList(1);
        commitOnModule5.diffs.add(diff5_0);
        commitsOnModule.put("4","5","a","a", commitOnModule5);

        CommitOnModule commitOnModule6 = new CommitOnModule();
        commitOnModule6.author = "c";
        Diff diff6_0 = new Diff();
        diff6_0.linesAfter = Arrays.asList(1);
        commitOnModule6.diffs.add(diff6_0);
        commitsOnModule.put("5","6","a","a", commitOnModule6);

        CommitOnModule commitOnModule7 = new CommitOnModule();
        commitOnModule7.author = "d";
        Diff diff7_0 = new Diff();
        diff7_0.linesAfter = Arrays.asList(1);
        commitOnModule7.diffs.add(diff7_0);
        commitsOnModule.put("6","7","a","a", commitOnModule7);

        CommitOnModule commitOnModule8 = new CommitOnModule();
        commitOnModule8.author = "d";
        Diff diff8_0 = new Diff();
        diff8_0.linesAfter = Arrays.asList(1,2);
        commitOnModule8.diffs.add(diff8_0);
        commitsOnModule.put("7","8","a","a", commitOnModule8);

        CommitOnModule commitOnModule9 = new CommitOnModule();
        commitOnModule9.author = "d";
        Diff diff9_0 = new Diff();
        diff9_0.linesBefore = Arrays.asList(1,2);
        diff9_0.linesAfter = Arrays.asList(1);
        commitOnModule9.diffs.add(diff9_0);
        commitsOnModule.put("8","9","a","a", commitOnModule9);

        CommitOnModule commitOnModule10 = new CommitOnModule();
        commitOnModule10.author = "d";
        Diff diff10_0 = new Diff();
        diff10_0.linesBefore = Arrays.asList(1,2,3);
        commitOnModule10.diffs.add(diff10_0);
        commitsOnModule.put("9","10","d","a", commitOnModule10);

        commitsOnModule.calc_maxOfRatio_numOfChangesLineOfACommitter();
        assertEquals(0.5, Math.floor(commitsOnModule.maxOfRatio_numOfChangesLineOfACommitter*10)/10);
    }
    @Test public void testCalcGeometricmean_sumOfChangesLineByTheCommitter0(){
        //入力
        //    CommitsOnModule:
        //        [0]: author="a" diff=+1
        //        [1]: author="b" diff=-1, +2
        //        [2]: author="b" diff=+2
        //        [3]: author="c" diff=+1
        //        [4]: author="c" diff=-1
        //        [5]: author="c" diff=+1
        //        [6]: author="d" diff=+1
        //        [7]: author="d" diff=+2
        //        [8]: author="d" diff=-2+1
        //        [9]: author="d" diff=+3
        //出力: (1*5*3*9)**(1/4)
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesBefore = Arrays.asList(1);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        diff2_0.linesAfter = Arrays.asList(1,2);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesAfter = Arrays.asList(1,2);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesAfter = Arrays.asList(1);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        CommitOnModule commitOnModule5 = new CommitOnModule();
        commitOnModule5.author = "c";
        Diff diff5_0 = new Diff();
        diff5_0.linesBefore = Arrays.asList(1);
        commitOnModule5.diffs.add(diff5_0);
        commitsOnModule.put("4","5","a","a", commitOnModule5);

        CommitOnModule commitOnModule6 = new CommitOnModule();
        commitOnModule6.author = "c";
        Diff diff6_0 = new Diff();
        diff6_0.linesAfter = Arrays.asList(1);
        commitOnModule6.diffs.add(diff6_0);
        commitsOnModule.put("5","6","a","a", commitOnModule6);

        CommitOnModule commitOnModule7 = new CommitOnModule();
        commitOnModule7.author = "d";
        Diff diff7_0 = new Diff();
        diff7_0.linesAfter = Arrays.asList(1);
        commitOnModule7.diffs.add(diff7_0);
        commitsOnModule.put("6","7","a","a", commitOnModule7);

        CommitOnModule commitOnModule8 = new CommitOnModule();
        commitOnModule8.author = "d";
        Diff diff8_0 = new Diff();
        diff8_0.linesAfter = Arrays.asList(1,2);
        commitOnModule8.diffs.add(diff8_0);
        commitsOnModule.put("7","8","a","a", commitOnModule8);

        CommitOnModule commitOnModule9 = new CommitOnModule();
        commitOnModule9.author = "d";
        Diff diff9_0 = new Diff();
        diff9_0.linesBefore = Arrays.asList(1,2);
        diff9_0.linesAfter = Arrays.asList(1);
        commitOnModule9.diffs.add(diff9_0);
        commitsOnModule.put("8","9","a","a", commitOnModule9);

        CommitOnModule commitOnModule10 = new CommitOnModule();
        commitOnModule10.author = "d";
        Diff diff10_0 = new Diff();
        diff10_0.linesBefore = Arrays.asList(1,2,3);
        commitOnModule10.diffs.add(diff10_0);
        commitsOnModule.put("9","10","d","a", commitOnModule10);

        commitsOnModule.calcGeometricmean_sumOfChangesLineByTheCommitter();
        assertEquals(3.4, Math.floor(commitsOnModule.geometricmean_sumOfChangesLineByTheCommitter*100)/100);

    }
    @Test public void testCalcMaxOfInterval0(){
        //入力
        //    CommitsOnModule:
        //        [0]: date=2000年1月1日00:00:00=946684800
        //        [1]: date=2000年2月1日00:00:00=949363200 , 31
        //        [2]: date=2000年6月1日00:00:00=959817600 , 120
        //出力: 120/7=17
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.date = 946684800;
        commitsOnModule.put("0", "1", "a", "a", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.date = 949363200;
        commitsOnModule.put("1", "2", "a", "a", commitOnModule2);
        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.date = 959817600;
        commitsOnModule.put("2", "3", "a", "a", commitOnModule3);
        commitsOnModule.calcMaxOfInterval();
        assertEquals(17, commitsOnModule.maxOfInterval);
    }
    @Test public void testCalcMinOfInterval0(){
        //入力
        //    CommitsOnModule:
        //        [0]: date=2000年1月1日00:00:00
        //        [1]: date=2000年2月1日00:00:00 , 31
        //        [2]: date=2000年6月1日00:00:00 , 120
        //出力: 31/7=4
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.date = 946684800;
        commitsOnModule.put("0", "1", "a", "a", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.date = 949363200;
        commitsOnModule.put("1", "2", "a", "a", commitOnModule2);
        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.date = 959817600;
        commitsOnModule.put("2", "3", "a", "a", commitOnModule3);
        commitsOnModule.calcMinOfInterval();
        assertEquals(4, commitsOnModule.minOfInterval);
    }
    @Test public void testCalcAvgOfInterval0(){
        //入力
        //    CommitsOnModule:
        //        [0]: date=2000年1月1日00:00:00
        //        [1]: date=2000年2月1日00:00:00 , 31
        //        [2]: date=2000年6月1日00:00:00 , 121
        //出力: ((31+120)/2)/7=10
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.date = 946684800;
        commitsOnModule.put("0", "1", "a", "a", commitOnModule1);
        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.date = 949363200;
        commitsOnModule.put("1", "2", "a", "a", commitOnModule2);
        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.date = 959817600;
        commitsOnModule.put("2", "3", "a", "a", commitOnModule3);
        commitsOnModule.calcAvgOfInterval();
        assertEquals(10.85,Math.floor(commitsOnModule.avgOfInterval*100)/100);
    }
    @Test public void testCalcSumOfAdditionsLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 10
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcSumOfAdditionsLine();
        assertEquals(10, commitsOnModule.sumOfAdditionsLine);
    }
    @Test public void testCalcMaxOfAdditionsLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 4
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcMaxOfAdditionsLine();
        assertEquals(4, commitsOnModule.maxOfAdditionsLine);
    }
    @Test public void testCalcAvgOfAdditionsLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 10/4 = 2.5
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcAvgOfAdditionsLine();
        assertEquals(2.5, Math.floor(commitsOnModule.avgOfAdditionsLine*10)/10);
    }
    @Test public void testCalcSumOfDeletionsLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 4
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcSumOfDeletionsLine();
        assertEquals(4, commitsOnModule.sumOfDeletionsLine);
    }
    @Test public void testCalcMaxOfDeletionsLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcMaxOfDeletionsLine();
        assertEquals(2, commitsOnModule.maxOfDeletionsLine);
    }
    @Test public void testCalcAvgOfDeletionsLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 1
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcAvgOfDeletionsLine();
        assertEquals(1, commitsOnModule.avgOfDeletionsLine);
    }
    @Test public void testCalcSumOfChangesLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 14
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcSumOfChangesLine();
        assertEquals(14, commitsOnModule.sumOfChangesLine);
    }
    @Test public void testCalcSumOfChurnLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 6
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcSumOfChurnLine();
        assertEquals(6, commitsOnModule.sumOfChurnLine);
    }
    @Test public void testCalcMaxOfChurnLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcMaxOfChurnLine();
        assertEquals(3, commitsOnModule.maxOfChurnLine);
    }
    @Test public void testCalcAvgOfChurnLine0(){
        //入力
        //    CommitsOnModule:
        //        [0]: diff=+3
        //        [1]: diff=-1
        //        [2]: diff=-2+4
        //        [3]: diff=-1+3
        //出力: 1.5
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.author = "a";
        Diff diff1_0 = new Diff();
        diff1_0.linesAfter = Arrays.asList(1, 2, 3);
        commitOnModule1.diffs.add(diff1_0);
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.author = "b";
        Diff diff2_0 = new Diff();
        diff2_0.linesBefore = Arrays.asList(1);
        commitOnModule2.diffs.add(diff2_0);
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.author = "b";
        Diff diff3_0 = new Diff();
        diff3_0.linesBefore = Arrays.asList(1,2);
        diff3_0.linesAfter = Arrays.asList(1,2,3,4);
        commitOnModule3.diffs.add(diff3_0);
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.author = "c";
        Diff diff4_0 = new Diff();
        diff4_0.linesBefore = Arrays.asList(1);
        diff4_0.linesAfter = Arrays.asList(1,2,3);
        commitOnModule4.diffs.add(diff4_0);
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcAvgOfChurnLine();
        assertEquals(1.5, Math.floor(commitsOnModule.avgOfChurnLine*10)/10);
    }
    @Test public void testCalcSumOfAdditionsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 10
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcSumOfAdditionsStatement();
        assertEquals(10, commitsOnModule.sumOfAdditionsStatement);
    }
    @Test public void testCalcMaxOfAdditionsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 4
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcMaxOfAdditionsStatement();
        assertEquals(4,commitsOnModule.maxOfAdditionsStatement);
    }
    @Test public void testCalcAvgOfAdditionsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 2.5
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcAvgOfAdditionsStatement();
        assertEquals(2.5, Math.floor(commitsOnModule.avgOfAdditionsStatement*10)/10);
    }
    @Test public void testCalcSumOfDeletionsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 4
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcSumOfDeletionsStatement();
        assertEquals(4, commitsOnModule.sumOfDeletionsStatement);
    }
    @Test public void testCalcMaxOfDeletionsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcMaxOfDeletionsStatement();
        assertEquals(2, commitsOnModule.maxOfDeletionsStatement);
    }
    @Test public void testCalcAvgOfDeletionsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 1
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcAvgOfDeletionsStatement();
        assertEquals(1, commitsOnModule.avgOfDeletionsStatement);
    }
    @Test public void testCalcSumOfChurnsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 6
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcSumOfChurnsStatement();
        assertEquals(6, commitsOnModule.sumOfChurnsStatement);
    }
    @Test public void testCalcMaxOfChurnsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcMaxOfChurnsStatement();
        assertEquals(3, commitsOnModule.maxOfChurnsStatement);
    }
    @Test public void testCalcAvgOfChurnsStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 1.5
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcAvgOfChurnsStatement();
        assertEquals(1.5, commitsOnModule.avgOfChurnsStatement);
    }
    @Test public void testCalcSumOfChangesStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 14
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcSumOfChangesStatement();
        assertEquals(14, commitsOnModule.sumOfChangesStatement);
    }
    @Test public void testCalcMaxOfChangesStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 6
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcMaxOfChangesStatement();
        assertEquals(6, commitsOnModule.maxOfChangesStatement);
    }
    @Test public void testCalcAvgOfChangesStatement0(){
        //入力
        //    CommitsOnModule:
        //        [0]: statement=+3
        //        [1]: statement=-1
        //        [2]: statement=-2+4
        //        [3]: statement=-1+3
        //出力: 3.5
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    akigno();\n" +
                        "    anikgoeae();\n" +
                        "    akingoea();\n" +
                        "    anoiekgn();\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        CommitOnModule commitOnModule4 = new CommitOnModule();
        commitOnModule4.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule4.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    sample1();\n" +
                        "    kaingoe();\n" +
                        "    sakinoge();\n" +
                        "    sakinoge();\n" +
                        "}"
        );
        commitsOnModule.put("3","4","a","a", commitOnModule4);

        commitsOnModule.calcAvgOfChangesStatement();
        assertEquals(3.5, Math.floor(commitsOnModule.avgOfChangesStatement*10)/10);
    }
    @Test public void testCalcSumOfChangesDeclarationItself0(){
        //入力
        //    CommitsOnModule:
        //        [0]: changeDecl=+1
        //        [1]: changeDecl=+1
        //        [2]: changeDecl=0
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public String example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public int example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        commitsOnModule.calcSumOfChangesDeclarationItself();
        assertEquals(2, commitsOnModule.sumOfChangesDeclarationItself);
    }
    @Test public void testCalcSumOfChangesCondition0(){
        //入力
        //    CommitsOnModule:
        //        [0]: changeCond=+1
        //        [1]: changeCond=+1
        //        [2]: changeCond=0
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijklmn){}"+
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijklm){}"+
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijklm){}"+
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijkl){}"+
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        commitsOnModule.calcSumOfChangesCondition();
        assertEquals(2, commitsOnModule.sumOfChangesCondition);

    }
    @Test public void testCalcSumOfAdditionStatementElse0(){
        //入力
        //    CommitsOnModule:
        //        [0]: additionStatementElse=1
        //        [1]: additionStatementElse=1
        //        [2]: additionStatementElse=0
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(true){}"+
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(true){}"+
                        "    else{}"+
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijklm){}"+
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijkl){}"+
                        "    else{}"+
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        commitsOnModule.calcSumOfAdditionStatementElse();
        assertEquals(2, commitsOnModule.sumOfAdditionStatementElse);
    }
    @Test public void testCalcSumOfDeletionStatementElse0(){
        //入力
        //    CommitsOnModule:
        //        [0]: DeletionStatementElse=0
        //        [1]: DeletionStatementElse=1
        //        [2]: DeletionStatementElse=1
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(true){}"+
                        "    else{}"+
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(true){}"+
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijkl){}"+
                        "    else{}"+
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijklm){}"+
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        commitsOnModule.calcSumOfDeletionStatementElse();
        assertEquals(2, commitsOnModule.sumOfDeletionStatementElse);
    }
    @Test public void testCalcSumOfChangesStatementElse0(){
        //入力
        //    CommitsOnModule:
        //        [0]: DeletionStatementElse=+1
        //        [1]: DeletionStatementElse=-1
        //        [2]: DeletionStatementElse=0
        //出力: 2
        CommitsOnModule commitsOnModule = new CommitsOnModule();

        CommitOnModule commitOnModule1 = new CommitOnModule();
        commitOnModule1.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(true){}"+
                        "}"
        );
        commitOnModule1.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(true){}"+
                        "    else{}"+
                        "}"
        );
        commitsOnModule.put("0","1","a","a", commitOnModule1);

        CommitOnModule commitOnModule2 = new CommitOnModule();
        commitOnModule2.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijkl){}"+
                        "    else{}"+
                        "}"
        );
        commitOnModule2.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "    if(abcdefghijklm){}"+
                        "}"
        );
        commitsOnModule.put("1","2","a","a", commitOnModule2);

        CommitOnModule commitOnModule3 = new CommitOnModule();
        commitOnModule3.sourceOld = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitOnModule3.sourceNew = new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                        "}"
        );
        commitsOnModule.put("2","3","a","a", commitOnModule3);

        commitsOnModule.calcSumOfChangesStatementElse();
        assertEquals(2, commitsOnModule.sumOfChangesStatementElse);
    }
    @Test public void testCalcComplexityHistory(){
        //入力
        //    Commits:
        //        [0]: date=2010/1/1 00:00:00=946684800,
        //             CommitsOnModule:
        //                 [0]: path="a" changesLine=+2
        //                 [1]: path="b" changesLine=+1
        //        [1]: date=2010/1/1 00:30:00=946686600,
        //             CommitsOnModule:
        //                 [0]: path="a" changesLine=+1
        //                 [1]: path="b" changesLine=+1
        //        [2]: date=2010/1/1 01:00:00=946688400,
        //             CommitsOnModule:
        //                 [0]: path="c" changesLine=+1
        //        [3]: date=2010/1/2 00:00:00=946771200,
        //             CommitsOnModule:
        //                 [0]: path="c" changesLine=+1
        //出力
        //  aの場合
        //  H = 3/6*log(3,3/6) + 2/6*log(3,2/6) + 1/6*log(3,1/6) = (1/2) * -0.63092975357146 + (1/3) * -1 + (1/6)* -1.63092975357146 = -0.92061983571430666666666666666667
        //  HCPF = H/numOfModules = -0.92061983571430666666666666666667/3 = -0.30687327857143555555555555555556= 0.30...
        //発見できたバグ
        //  ブロックにモジュールaに対するコミットが2件入っていた場合、それぞれに対して別個にHCPFを計算して、足し合わせてしまっている。
        Commits commits = new Commits();
        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.idParentMaster = "0";
        commit1.date = 946684800;
        CommitsOnModule commitsOnModule0_1 = new CommitsOnModule();
        CommitOnModule commitOnModule0_1_0 = new CommitOnModule();
        commitOnModule0_1_0.idCommitParent = "0";
        commitOnModule0_1_0.idCommit = "1";
        commitOnModule0_1_0.pathOld = "a";
        commitOnModule0_1_0.pathNew = "a";
        Diff diff0_1_0 = new Diff();
        diff0_1_0.linesAfter=Arrays.asList(1,2);
        commitOnModule0_1_0.diffs.add(diff0_1_0);
        commitsOnModule0_1.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        CommitOnModule commitOnModule0_1_1 = new CommitOnModule();
        commitOnModule0_1_1.idCommit = "1";
        commitOnModule0_1_1.pathOld = "b";
        commitOnModule0_1_1.pathNew = "b";
        Diff diff0_1_1 = new Diff();
        diff0_1_1.linesAfter=Arrays.asList(1);
        commitOnModule0_1_1.diffs.add(diff0_1_1);
        commitsOnModule0_1.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        commit1.idParent2Modifications.put(commit1.id, commitsOnModule0_1);
        commits.put(commit1.id, commit1);

        Commit commit2 = new Commit();
        commit2.date = 946686600;
        commit2.idParentMaster ="1";
        commit2.id ="2";
        CommitsOnModule commitsOnModule1_2 = new CommitsOnModule();
        CommitOnModule commitOnModule1_2_0 = new CommitOnModule();
        commitOnModule1_2_0.idCommitParent = "1";
        commitOnModule1_2_0.idCommit = "2";
        commitOnModule1_2_0.pathOld = "a";
        commitOnModule1_2_0.pathNew = "a";
        Diff diff1_2_0 = new Diff();
        diff1_2_0.linesAfter=Arrays.asList(1);
        commitOnModule1_2_0.diffs.add(diff1_2_0);
        commitsOnModule1_2.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        CommitOnModule commitOnModule1_2_1 = new CommitOnModule();
        commitOnModule1_2_1.idCommit = "2";
        commitOnModule1_2_1.pathOld = "b";
        commitOnModule1_2_1.pathNew = "b";
        Diff diff1_2_1 = new Diff();
        diff1_2_1.linesAfter=Arrays.asList(1);
        commitOnModule1_2_1.diffs.add(diff1_2_1);
        commitsOnModule1_2.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        commit2.idParent2Modifications.put(commit2.id, commitsOnModule1_2);
        commits.put(commit2.id, commit2);

        Commit commit3 = new Commit();
        commit3.idParentMaster ="2";
        commit3.id ="3";
        commit3.date = 946688400;
        CommitsOnModule commitsOnModule2_3 = new CommitsOnModule();
        CommitOnModule commitOnModule2_3_0 = new CommitOnModule();
        commitOnModule2_3_0.idCommitParent = "2";
        commitOnModule2_3_0.idCommit = "3";
        commitOnModule2_3_0.pathOld = "c";
        commitOnModule2_3_0.pathNew = "c";
        Diff diff2_3_0 = new Diff();
        diff2_3_0.linesAfter=Arrays.asList(1);
        commitOnModule2_3_0.diffs.add(diff2_3_0);
        commitsOnModule2_3.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        commit3.idParent2Modifications.put(commit3.id, commitsOnModule2_3);
        commits.put(commit3.id, commit3);

        Commit commit4 = new Commit();
        commit4.idParentMaster ="3";
        commit4.id ="4";
        commit4.date = 946771200;
        CommitsOnModule commitsOnModule3_4 = new CommitsOnModule();
        CommitOnModule commitOnModule3_4_0 = new CommitOnModule();
        commitOnModule3_4_0.idCommitParent = "3";
        commitOnModule3_4_0.idCommit = "4";
        commitOnModule3_4_0.pathOld = "c";
        commitOnModule3_4_0.pathNew = "c";
        Diff diff3_4_0 = new Diff();
        diff3_4_0.linesAfter=Arrays.asList(1);
        commitOnModule3_4_0.diffs.add(diff3_4_0);
        commitsOnModule3_4.put(commitOnModule3_4_0.idCommitParent, commitOnModule3_4_0.idCommit, commitOnModule3_4_0.pathOld, commitOnModule3_4_0.pathNew, commitOnModule3_4_0);
        commit4.idParent2Modifications.put(commit4.id, commitsOnModule3_4);
        commits.put(commit4.id, commit4);

        CommitsOnModule commitsOnModuleA = new CommitsOnModule();
        commitsOnModuleA.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        commitsOnModuleA.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        commitsOnModuleA.calcComplexityHistory(commits);
        assertEquals(0.3068, Math.floor(commitsOnModuleA.complexityHistory*10000)/10000);
    }
    @Test public void testCalcMaxOfModulesCommittedSimultaneously(){
        //入力
        //    Commits:
        //        [0]: date=2010/1/1 00:00:00,
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [1]: date=2010/1/1 00:30:00,
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [2]: date=2010/1/1 01:00:00,
        //             CommitsOnModule:
        //                 [0]: newpath="c" newpath="c"
        //    Modules
        //        [0]: path:"a"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [1]: path:"b"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [2]: path:"c"
        //             CommitsOnModule:
        //                 [0]:
        //出力
        // aの場合 2

        Commits commits = new Commits();
        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.idParentMaster = "0";
        commit1.date = 946684800;
        CommitsOnModule commitsOnModule0_1 = new CommitsOnModule();
        CommitOnModule commitOnModule0_1_0 = new CommitOnModule();
        commitOnModule0_1_0.idCommitParent = "0";
        commitOnModule0_1_0.idCommit = "1";
        commitOnModule0_1_0.pathOld = "a";
        commitOnModule0_1_0.pathNew = "a";
        Diff diff0_1_0 = new Diff();
        diff0_1_0.linesAfter=Arrays.asList(1,2);
        commitOnModule0_1_0.diffs.add(diff0_1_0);
        commitsOnModule0_1.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        CommitOnModule commitOnModule0_1_1 = new CommitOnModule();
        commitOnModule0_1_1.idCommit = "1";
        commitOnModule0_1_1.pathOld = "b";
        commitOnModule0_1_1.pathNew = "b";
        Diff diff0_1_1 = new Diff();
        diff0_1_1.linesAfter=Arrays.asList(1);
        commitOnModule0_1_1.diffs.add(diff0_1_1);
        commitsOnModule0_1.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        commit1.idParent2Modifications.put(commit1.id, commitsOnModule0_1);
        commits.put(commit1.id, commit1);

        Commit commit2 = new Commit();
        commit2.date = 946686600;
        commit2.idParentMaster ="1";
        commit2.id ="2";
        CommitsOnModule commitsOnModule1_2 = new CommitsOnModule();
        CommitOnModule commitOnModule1_2_0 = new CommitOnModule();
        commitOnModule1_2_0.idCommitParent = "1";
        commitOnModule1_2_0.idCommit = "2";
        commitOnModule1_2_0.pathOld = "a";
        commitOnModule1_2_0.pathNew = "a";
        Diff diff1_2_0 = new Diff();
        diff1_2_0.linesAfter=Arrays.asList(1);
        commitOnModule1_2_0.diffs.add(diff1_2_0);
        commitsOnModule1_2.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        CommitOnModule commitOnModule1_2_1 = new CommitOnModule();
        commitOnModule1_2_1.idCommit = "2";
        commitOnModule1_2_1.pathOld = "b";
        commitOnModule1_2_1.pathNew = "b";
        Diff diff1_2_1 = new Diff();
        diff1_2_1.linesAfter=Arrays.asList(1);
        commitOnModule1_2_1.diffs.add(diff1_2_1);
        commitsOnModule1_2.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        commit2.idParent2Modifications.put(commit2.id, commitsOnModule1_2);
        commits.put(commit2.id, commit2);

        Commit commit3 = new Commit();
        commit3.idParentMaster ="2";
        commit3.id ="3";
        commit3.date = 946688400;
        CommitsOnModule commitsOnModule2_3 = new CommitsOnModule();
        CommitOnModule commitOnModule2_3_0 = new CommitOnModule();
        commitOnModule2_3_0.idCommitParent = "2";
        commitOnModule2_3_0.idCommit = "3";
        commitOnModule2_3_0.pathOld = "c";
        commitOnModule2_3_0.pathNew = "c";
        Diff diff2_3_0 = new Diff();
        diff2_3_0.linesAfter=Arrays.asList(1);
        commitOnModule2_3_0.diffs.add(diff2_3_0);
        commitsOnModule2_3.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        commit3.idParent2Modifications.put(commit3.id, commitsOnModule2_3);
        commits.put(commit3.id, commit3);

        Modules modules = new Modules();
        Module moduleA = new Module();
        moduleA.commitsOnModuleAll = new CommitsOnModule();
        moduleA.commitsOnModuleAll.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        moduleA.commitsOnModuleAll.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        Module moduleB = new Module();
        moduleB.commitsOnModuleAll = new CommitsOnModule();
        moduleB.commitsOnModuleAll.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        moduleB.commitsOnModuleAll.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        Module moduleC = new Module();
        moduleC.commitsOnModuleAll = new CommitsOnModule();
        moduleC.commitsOnModuleAll.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        modules.put("a",moduleA);
        modules.put("b",moduleB);
        modules.put("c",moduleC);

        moduleA.commitsOnModuleAll.calcMaxOfModulesCommittedSimultaneously(commits);
        assertEquals(2,moduleA.commitsOnModuleAll.maxOfModulesCommittedSimultaneously);
    }
    @Test public void testCalcAvgOfModulesCommittedSimultaneously(){
        //入力
        //    Commits:
        //        [0]: date=2010/1/1 00:00:00,
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [1]: date=2010/1/1 00:30:00,
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [2]: date=2010/1/1 01:00:00,
        //             CommitsOnModule:
        //                 [0]: newpath="c" newpath="c"
        //    Modules
        //        [0]: path:"a"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [1]: path:"b"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [2]: path:"c"
        //             CommitsOnModule:
        //                 [0]:
        //出力
        // aの場合 4/2=2
        Commits commits = new Commits();
        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.idParentMaster = "0";
        commit1.date = 946684800;
        CommitsOnModule commitsOnModule0_1 = new CommitsOnModule();
        CommitOnModule commitOnModule0_1_0 = new CommitOnModule();
        commitOnModule0_1_0.idCommitParent = "0";
        commitOnModule0_1_0.idCommit = "1";
        commitOnModule0_1_0.pathOld = "a";
        commitOnModule0_1_0.pathNew = "a";
        Diff diff0_1_0 = new Diff();
        diff0_1_0.linesAfter=Arrays.asList(1,2);
        commitOnModule0_1_0.diffs.add(diff0_1_0);
        commitsOnModule0_1.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        CommitOnModule commitOnModule0_1_1 = new CommitOnModule();
        commitOnModule0_1_1.idCommit = "1";
        commitOnModule0_1_1.pathOld = "b";
        commitOnModule0_1_1.pathNew = "b";
        Diff diff0_1_1 = new Diff();
        diff0_1_1.linesAfter=Arrays.asList(1);
        commitOnModule0_1_1.diffs.add(diff0_1_1);
        commitsOnModule0_1.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        commit1.idParent2Modifications.put(commit1.id, commitsOnModule0_1);
        commits.put(commit1.id, commit1);

        Commit commit2 = new Commit();
        commit2.date = 946686600;
        commit2.idParentMaster ="1";
        commit2.id ="2";
        CommitsOnModule commitsOnModule1_2 = new CommitsOnModule();
        CommitOnModule commitOnModule1_2_0 = new CommitOnModule();
        commitOnModule1_2_0.idCommitParent = "1";
        commitOnModule1_2_0.idCommit = "2";
        commitOnModule1_2_0.pathOld = "a";
        commitOnModule1_2_0.pathNew = "a";
        Diff diff1_2_0 = new Diff();
        diff1_2_0.linesAfter=Arrays.asList(1);
        commitOnModule1_2_0.diffs.add(diff1_2_0);
        commitsOnModule1_2.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        CommitOnModule commitOnModule1_2_1 = new CommitOnModule();
        commitOnModule1_2_1.idCommit = "2";
        commitOnModule1_2_1.pathOld = "b";
        commitOnModule1_2_1.pathNew = "b";
        Diff diff1_2_1 = new Diff();
        diff1_2_1.linesAfter=Arrays.asList(1);
        commitOnModule1_2_1.diffs.add(diff1_2_1);
        commitsOnModule1_2.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        commit2.idParent2Modifications.put(commit2.id, commitsOnModule1_2);
        commits.put(commit2.id, commit2);

        Commit commit3 = new Commit();
        commit3.idParentMaster ="2";
        commit3.id ="3";
        commit3.date = 946688400;
        CommitsOnModule commitsOnModule2_3 = new CommitsOnModule();
        CommitOnModule commitOnModule2_3_0 = new CommitOnModule();
        commitOnModule2_3_0.idCommitParent = "2";
        commitOnModule2_3_0.idCommit = "3";
        commitOnModule2_3_0.pathOld = "c";
        commitOnModule2_3_0.pathNew = "c";
        Diff diff2_3_0 = new Diff();
        diff2_3_0.linesAfter=Arrays.asList(1);
        commitOnModule2_3_0.diffs.add(diff2_3_0);
        commitsOnModule2_3.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        commit3.idParent2Modifications.put(commit3.id, commitsOnModule2_3);
        commits.put(commit3.id, commit3);

        Modules modules = new Modules();
        Module moduleA = new Module();
        moduleA.commitsOnModuleAll = new CommitsOnModule();
        moduleA.commitsOnModuleAll.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        moduleA.commitsOnModuleAll.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        Module moduleB = new Module();
        moduleB.commitsOnModuleAll = new CommitsOnModule();
        moduleB.commitsOnModuleAll.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        moduleB.commitsOnModuleAll.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        Module moduleC = new Module();
        moduleC.commitsOnModuleAll = new CommitsOnModule();
        moduleC.commitsOnModuleAll.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        modules.put("a",moduleA);
        modules.put("b",moduleB);
        modules.put("c",moduleC);

        moduleA.commitsOnModuleAll.calcAvgOfModulesCommittedSimultaneously(commits);
        assertEquals(2, moduleA.commitsOnModuleAll.avgOfModulesCommittedSimultaneously);
    }
    @Test public void testCalcNumOfCommitsOtherModulesHasBeenBuggyOnTheCommit(){
        //入力
        //    Commits:
        //        [0]: date=2010/1/1 00:00:00,
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [1]: date=2010/1/1 00:30:00,
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [2]: date=2010/1/1 01:00:00,
        //             CommitsOnModule:
        //                 [0]: newpath="c" newpath="c"
        //    Modules
        //        [0]: path:"a"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [1]: path:"b"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [2]: path:"c"
        //             CommitsOnModule:
        //                 [0]:
        //出力
        // aの場合 2
        Commits commits = new Commits();
        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.idParentMaster = "0";
        commit1.date = 946684800;
        CommitsOnModule commitsOnModule0_1 = new CommitsOnModule();
        CommitOnModule commitOnModule0_1_0 = new CommitOnModule();
        commitOnModule0_1_0.idCommitParent = "0";
        commitOnModule0_1_0.idCommit = "1";
        commitOnModule0_1_0.pathOld = "a";
        commitOnModule0_1_0.pathNew = "a";
        Diff diff0_1_0 = new Diff();
        diff0_1_0.linesAfter=Arrays.asList(1,2);
        commitOnModule0_1_0.diffs.add(diff0_1_0);
        commitsOnModule0_1.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        CommitOnModule commitOnModule0_1_1 = new CommitOnModule();
        commitOnModule0_1_1.idCommit = "1";
        commitOnModule0_1_1.pathOld = "b";
        commitOnModule0_1_1.pathNew = "b";
        commitOnModule0_1_1.IdsCommitsInducingBugsThatThisCommitFixes = Arrays.asList("");
        Diff diff0_1_1 = new Diff();
        diff0_1_1.linesAfter=Arrays.asList(1);
        commitOnModule0_1_1.diffs.add(diff0_1_1);
        commitsOnModule0_1.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        commit1.idParent2Modifications.put(commit1.idParentMaster, commitsOnModule0_1);
        commits.put(commit1.id, commit1);

        Commit commit2 = new Commit();
        commit2.date = 946686600;
        commit2.idParentMaster ="1";
        commit2.id ="2";
        CommitsOnModule commitsOnModule1_2 = new CommitsOnModule();
        CommitOnModule commitOnModule1_2_0 = new CommitOnModule();
        commitOnModule1_2_0.idCommitParent = "1";
        commitOnModule1_2_0.idCommit = "2";
        commitOnModule1_2_0.pathOld = "a";
        commitOnModule1_2_0.pathNew = "a";
        Diff diff1_2_0 = new Diff();
        diff1_2_0.linesAfter=Arrays.asList(1);
        commitOnModule1_2_0.diffs.add(diff1_2_0);
        commitsOnModule1_2.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        CommitOnModule commitOnModule1_2_1 = new CommitOnModule();
        commitOnModule1_2_1.idCommit = "2";
        commitOnModule1_2_1.pathOld = "b";
        commitOnModule1_2_1.pathNew = "b";
        Diff diff1_2_1 = new Diff();
        diff1_2_1.linesAfter=Arrays.asList(1);
        commitOnModule1_2_1.diffs.add(diff1_2_1);
        commitsOnModule1_2.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        commit2.idParent2Modifications.put(commit2.idParentMaster, commitsOnModule1_2);
        commits.put(commit2.id, commit2);

        Commit commit3 = new Commit();
        commit3.idParentMaster ="2";
        commit3.id ="3";
        commit3.date = 946688400;
        CommitsOnModule commitsOnModule2_3 = new CommitsOnModule();
        CommitOnModule commitOnModule2_3_0 = new CommitOnModule();
        commitOnModule2_3_0.idCommitParent = "2";
        commitOnModule2_3_0.idCommit = "3";
        commitOnModule2_3_0.pathOld = "c";
        commitOnModule2_3_0.pathNew = "c";
        Diff diff2_3_0 = new Diff();
        diff2_3_0.linesAfter=Arrays.asList(1);
        commitOnModule2_3_0.diffs.add(diff2_3_0);
        commitsOnModule2_3.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        commit3.idParent2Modifications.put(commit3.idParentMaster, commitsOnModule2_3);
        commits.put(commit3.id, commit3);

        Modules modules = new Modules();
        Module moduleA = new Module();
        moduleA.commitsOnModuleAll = new CommitsOnModule();
        moduleA.commitsOnModuleAll.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        moduleA.commitsOnModuleAll.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        moduleA.commitsOnModuleInInterval = new CommitsOnModule();
        moduleA.commitsOnModuleInInterval.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        moduleA.commitsOnModuleInInterval.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        Module moduleB = new Module();
        moduleB.commitsOnModuleAll = new CommitsOnModule();
        moduleB.commitsOnModuleAll.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        moduleB.commitsOnModuleAll.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        moduleB.commitsOnModuleInInterval = new CommitsOnModule();
        moduleB.commitsOnModuleInInterval.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        moduleB.commitsOnModuleInInterval.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        Module moduleC = new Module();
        moduleC.commitsOnModuleAll = new CommitsOnModule();
        moduleC.commitsOnModuleAll.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        moduleC.commitsOnModuleInInterval = new CommitsOnModule();
        moduleC.commitsOnModuleInInterval.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        modules.put("a",moduleA);
        modules.put("b",moduleB);
        modules.put("c",moduleC);

        moduleA.commitsOnModuleAll.calcNumOfCommitsOtherModulesHasBeenFixedOnTheCommit(commits,modules);
        assertEquals(2, moduleA.commitsOnModuleAll.numOfCommitsOtherModulesHasBeenFixedOnTheCommit);
    }
    @Test public void testCalcNumOfCommittersUniqueNeighbor(){
        //入力
        //    Commits:
        //        [0]: date=2010/1/1 00:00:00,
        //             committer = "a"
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [1]: date=2010/1/1 00:30:00,
        //             committer = "b"
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [2]: date=2010/1/1 01:00:00,
        //             committer = "a"
        //             CommitsOnModule:
        //                 [0]: newpath="c" newpath="c"
        //    Modules
        //        [0]: path:"a"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [1]: path:"b"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //                 [2]:
        //        [2]: path:"c"
        //             CommitsOnModule:
        //                 [0]:
        //出力
        // bが2回で2人なので
        // aの場合 (2*2)/2 = 2
        Commits commits = new Commits();
        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.author = "a";
        commit1.idParentMaster = "0";
        commit1.date = 946684800;
        CommitsOnModule commitsOnModule0_1 = new CommitsOnModule();
        CommitOnModule commitOnModule0_1_0 = new CommitOnModule();
        commitOnModule0_1_0.idCommitParent = commit1.idParentMaster;
        commitOnModule0_1_0.idCommit = commit1.id;
        commitOnModule0_1_0.author = commit1.author;
        commitOnModule0_1_0.pathOld = "a";
        commitOnModule0_1_0.pathNew = "a";
        Diff diff0_1_0 = new Diff();
        diff0_1_0.linesAfter=Arrays.asList(1,2);
        commitOnModule0_1_0.diffs.add(diff0_1_0);
        commitsOnModule0_1.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        CommitOnModule commitOnModule0_1_1 = new CommitOnModule();
        commitOnModule0_1_1.idCommitParent = commit1.idParentMaster;
        commitOnModule0_1_1.idCommit = commit1.id;
        commitOnModule0_1_1.author = commit1.author;
        commitOnModule0_1_1.pathOld = "b";
        commitOnModule0_1_1.pathNew = "b";
        Diff diff0_1_1 = new Diff();
        diff0_1_1.linesAfter=Arrays.asList(1);
        commitOnModule0_1_1.diffs.add(diff0_1_1);
        commitsOnModule0_1.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        commit1.idParent2Modifications.put(commit1.id, commitsOnModule0_1);
        commits.put(commit1.id, commit1);

        Commit commit2 = new Commit();
        commit2.date = 946686600;
        commit2.idParentMaster ="1";
        commit2.id ="2";
        commit2.author = "b";
        CommitsOnModule commitsOnModule1_2 = new CommitsOnModule();
        CommitOnModule commitOnModule1_2_0 = new CommitOnModule();
        commitOnModule1_2_0.idCommitParent = commit2.idParentMaster;
        commitOnModule1_2_0.idCommit = commit2.id;
        commitOnModule1_2_0.author = commit2.author;
        commitOnModule1_2_0.pathOld = "a";
        commitOnModule1_2_0.pathNew = "a";
        Diff diff1_2_0 = new Diff();
        diff1_2_0.linesAfter=Arrays.asList(1);
        commitOnModule1_2_0.diffs.add(diff1_2_0);
        commitsOnModule1_2.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        CommitOnModule commitOnModule1_2_1 = new CommitOnModule();
        commitOnModule1_2_1.idCommitParent = commit2.idParentMaster;
        commitOnModule1_2_1.idCommit = commit2.id;
        commitOnModule1_2_1.author = commit2.author;
        commitOnModule1_2_1.pathOld = "b";
        commitOnModule1_2_1.pathNew = "b";
        Diff diff1_2_1 = new Diff();
        diff1_2_1.linesAfter=Arrays.asList(1);
        commitOnModule1_2_1.diffs.add(diff1_2_1);
        commitsOnModule1_2.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        commit2.idParent2Modifications.put(commit2.id, commitsOnModule1_2);
        commits.put(commit2.id, commit2);

        Commit commit3 = new Commit();
        commit3.idParentMaster ="2";
        commit3.id ="3";
        commit3.date = 946688400;
        commit3.author = "a";
        CommitsOnModule commitsOnModule2_3 = new CommitsOnModule();
        CommitOnModule commitOnModule2_3_0 = new CommitOnModule();
        commitOnModule2_3_0.idCommitParent = commit3.idParentMaster;
        commitOnModule2_3_0.idCommit = commit3.id;
        commitOnModule2_3_0.author = commit3.author;
        commitOnModule2_3_0.pathOld = "c";
        commitOnModule2_3_0.pathNew = "c";
        Diff diff2_3_0 = new Diff();
        diff2_3_0.linesAfter=Arrays.asList(1);
        commitOnModule2_3_0.diffs.add(diff2_3_0);
        commitsOnModule2_3.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        commit3.idParent2Modifications.put(commit3.id, commitsOnModule2_3);
        commits.put(commit3.id, commit3);

        Modules modules = new Modules();
        Module moduleA = new Module();
        moduleA.commitsOnModuleInInterval = new CommitsOnModule();
        moduleA.commitsOnModuleInInterval.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        moduleA.commitsOnModuleInInterval.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        Module moduleB = new Module();
        moduleB.commitsOnModuleInInterval = new CommitsOnModule();
        moduleB.commitsOnModuleInInterval.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        moduleB.commitsOnModuleInInterval.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        Module moduleC = new Module();
        moduleC.commitsOnModuleInInterval = new CommitsOnModule();
        moduleC.commitsOnModuleInInterval.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        modules.put("a",moduleA);
        modules.put("b",moduleB);
        modules.put("c",moduleC);

        moduleA.commitsOnModuleInInterval.calcNumOfCommittersUnique();
        moduleB.commitsOnModuleInInterval.calcNumOfCommittersUnique();
        moduleC.commitsOnModuleInInterval.calcNumOfCommittersUnique();
        moduleA.commitsOnModuleInInterval.calcNumOfCommittersUniqueNeighbor(commits, modules);
        assertEquals(2, moduleA.commitsOnModuleInInterval.numOfCommittersUniqueNeighbor);
    }
    @Test public void testCalcNumOfCommitsNeighbor(){
        //入力
        //    Commits:
        //        [0]: date=2010/1/1 00:00:00,
        //             committer = "a"
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [1]: date=2010/1/1 00:30:00,
        //             committer = "b"
        //             CommitsOnModule:
        //                 [0]: newpath="a" newpath="a"
        //                 [1]: newpath="b" newpath="b"
        //        [2]: date=2010/1/1 01:00:00,
        //             committer = "a"
        //             CommitsOnModule:
        //                 [0]: newpath="c" newpath="c"
        //    Modules
        //        [0]: path:"a"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [1]: path:"b"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [2]: path:"c"
        //             CommitsOnModule:
        //                 [0]:
        //出力
        // bについては共コミット回数が2でコミット回数が2
        // aの場合 (2*2)/2 = 2
        Commits commits = new Commits();
        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.idParentMaster = "0";
        commit1.date = 946684800;
        commit1.author= "a";
        CommitsOnModule commitsOnModule0_1 = new CommitsOnModule();
        CommitOnModule commitOnModule0_1_0 = new CommitOnModule();
        commitOnModule0_1_0.idCommitParent = "0";
        commitOnModule0_1_0.idCommit = "1";
        commitOnModule0_1_0.pathOld = "a";
        commitOnModule0_1_0.pathNew = "a";
        Diff diff0_1_0 = new Diff();
        diff0_1_0.linesAfter=Arrays.asList(1,2);
        commitOnModule0_1_0.diffs.add(diff0_1_0);
        commitsOnModule0_1.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        CommitOnModule commitOnModule0_1_1 = new CommitOnModule();
        commitOnModule0_1_1.idCommit = "1";
        commitOnModule0_1_1.pathOld = "b";
        commitOnModule0_1_1.pathNew = "b";
        Diff diff0_1_1 = new Diff();
        diff0_1_1.linesAfter=Arrays.asList(1);
        commitOnModule0_1_1.diffs.add(diff0_1_1);
        commitsOnModule0_1.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        commit1.idParent2Modifications.put(commit1.id, commitsOnModule0_1);
        commits.put(commit1.id, commit1);

        Commit commit2 = new Commit();
        commit2.date = 946686600;
        commit2.idParentMaster ="1";
        commit2.id ="2";
        commit2.author= "b";
        CommitsOnModule commitsOnModule1_2 = new CommitsOnModule();
        CommitOnModule commitOnModule1_2_0 = new CommitOnModule();
        commitOnModule1_2_0.idCommitParent = "1";
        commitOnModule1_2_0.idCommit = "2";
        commitOnModule1_2_0.pathOld = "a";
        commitOnModule1_2_0.pathNew = "a";
        Diff diff1_2_0 = new Diff();
        diff1_2_0.linesAfter=Arrays.asList(1);
        commitOnModule1_2_0.diffs.add(diff1_2_0);
        commitsOnModule1_2.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        CommitOnModule commitOnModule1_2_1 = new CommitOnModule();
        commitOnModule1_2_1.idCommit = "2";
        commitOnModule1_2_1.pathOld = "b";
        commitOnModule1_2_1.pathNew = "b";
        Diff diff1_2_1 = new Diff();
        diff1_2_1.linesAfter=Arrays.asList(1);
        commitOnModule1_2_1.diffs.add(diff1_2_1);
        commitsOnModule1_2.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        commit2.idParent2Modifications.put(commit2.id, commitsOnModule1_2);
        commits.put(commit2.id, commit2);

        Commit commit3 = new Commit();
        commit3.idParentMaster ="2";
        commit3.id ="3";
        commit3.date = 946688400;
        commit2.author= "a";
        CommitsOnModule commitsOnModule2_3 = new CommitsOnModule();
        CommitOnModule commitOnModule2_3_0 = new CommitOnModule();
        commitOnModule2_3_0.idCommitParent = "2";
        commitOnModule2_3_0.idCommit = "3";
        commitOnModule2_3_0.pathOld = "c";
        commitOnModule2_3_0.pathNew = "c";
        Diff diff2_3_0 = new Diff();
        diff2_3_0.linesAfter=Arrays.asList(1);
        commitOnModule2_3_0.diffs.add(diff2_3_0);
        commitsOnModule2_3.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        commit3.idParent2Modifications.put(commit3.id, commitsOnModule2_3);
        commits.put(commit3.id, commit3);

        Modules modules = new Modules();
        Module moduleA = new Module();
        moduleA.commitsOnModuleInInterval = new CommitsOnModule();
        moduleA.commitsOnModuleInInterval.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        moduleA.commitsOnModuleInInterval.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        Module moduleB = new Module();
        moduleB.commitsOnModuleInInterval = new CommitsOnModule();
        moduleB.commitsOnModuleInInterval.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        moduleB.commitsOnModuleInInterval.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        Module moduleC = new Module();
        moduleC.commitsOnModuleInInterval = new CommitsOnModule();
        moduleC.commitsOnModuleInInterval.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        modules.put("a",moduleA);
        modules.put("b",moduleB);
        modules.put("c",moduleC);

        moduleA.commitsOnModuleInInterval.calcNumOfCommits();
        moduleB.commitsOnModuleInInterval.calcNumOfCommits();
        moduleC.commitsOnModuleInInterval.calcNumOfCommits();
        moduleA.commitsOnModuleInInterval.calcNumOfCommitsNeighbor(commits, modules);
        assertEquals(2, moduleA.commitsOnModuleInInterval.numOfCommitsNeighbor);
    }
    @Test public void testCalcComplexityHistoryNeighbor(){
        //入力
        //    Commits:
        //        [0]: date=2010/1/1 00:00:00,
        //             CommitsOnModule:
        //                 [0]: path="a" changesLine=+2
        //                 [1]: path="b" changesLine=+1
        //        [1]: date=2010/1/1 00:30:00,
        //             CommitsOnModule:
        //                 [0]: path="a" changesLine=+1
        //                 [1]: path="b" changesLine=+1
        //        [2]: date=2010/1/1 01:00:00,
        //             CommitsOnModule:
        //                 [0]: path="c" changesLine=+1
        //    Modules
        //        [0]: path:"a"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [1]: path:"b"
        //             CommitsOnModule:
        //                 [0]:
        //                 [1]:
        //        [2]: path:"c"
        //             CommitsOnModule:
        //                 [0]:
        //出力
        // bについては共コミット回数が2でcomplexityが-0.30687
        // よってaの場合 2*-0.30687/2=-0.30687
        Commits commits = new Commits();
        Commit commit1 = new Commit();
        commit1.id = "1";
        commit1.idParentMaster = "0";
        commit1.date = 946684800;
        commit1.author= "a";
        CommitsOnModule commitsOnModule0_1 = new CommitsOnModule();
        CommitOnModule commitOnModule0_1_0 = new CommitOnModule();
        commitOnModule0_1_0.idCommitParent = "0";
        commitOnModule0_1_0.idCommit = "1";
        commitOnModule0_1_0.pathOld = "a";
        commitOnModule0_1_0.pathNew = "a";
        Diff diff0_1_0 = new Diff();
        diff0_1_0.linesAfter=Arrays.asList(1,2);
        commitOnModule0_1_0.diffs.add(diff0_1_0);
        commitsOnModule0_1.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        CommitOnModule commitOnModule0_1_1 = new CommitOnModule();
        commitOnModule0_1_1.idCommit = "1";
        commitOnModule0_1_1.pathOld = "b";
        commitOnModule0_1_1.pathNew = "b";
        Diff diff0_1_1 = new Diff();
        diff0_1_1.linesAfter=Arrays.asList(1);
        commitOnModule0_1_1.diffs.add(diff0_1_1);
        commitsOnModule0_1.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        commit1.idParent2Modifications.put(commit1.id, commitsOnModule0_1);
        commits.put(commit1.id, commit1);

        Commit commit2 = new Commit();
        commit2.date = 946686600;
        commit2.idParentMaster ="1";
        commit2.id ="2";
        commit2.author= "b";
        CommitsOnModule commitsOnModule1_2 = new CommitsOnModule();
        CommitOnModule commitOnModule1_2_0 = new CommitOnModule();
        commitOnModule1_2_0.idCommitParent = "1";
        commitOnModule1_2_0.idCommit = "2";
        commitOnModule1_2_0.pathOld = "a";
        commitOnModule1_2_0.pathNew = "a";
        Diff diff1_2_0 = new Diff();
        diff1_2_0.linesAfter=Arrays.asList(1);
        commitOnModule1_2_0.diffs.add(diff1_2_0);
        commitsOnModule1_2.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        CommitOnModule commitOnModule1_2_1 = new CommitOnModule();
        commitOnModule1_2_1.idCommit = "2";
        commitOnModule1_2_1.pathOld = "b";
        commitOnModule1_2_1.pathNew = "b";
        Diff diff1_2_1 = new Diff();
        diff1_2_1.linesAfter=Arrays.asList(1);
        commitOnModule1_2_1.diffs.add(diff1_2_1);
        commitsOnModule1_2.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        commit2.idParent2Modifications.put(commit2.id, commitsOnModule1_2);
        commits.put(commit2.id, commit2);

        Commit commit3 = new Commit();
        commit3.idParentMaster ="2";
        commit3.id ="3";
        commit3.date = 946688400;
        commit2.author= "a";
        CommitsOnModule commitsOnModule2_3 = new CommitsOnModule();
        CommitOnModule commitOnModule2_3_0 = new CommitOnModule();
        commitOnModule2_3_0.idCommitParent = "2";
        commitOnModule2_3_0.idCommit = "3";
        commitOnModule2_3_0.pathOld = "c";
        commitOnModule2_3_0.pathNew = "c";
        Diff diff2_3_0 = new Diff();
        diff2_3_0.linesAfter=Arrays.asList(1);
        commitOnModule2_3_0.diffs.add(diff2_3_0);
        commitsOnModule2_3.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        commit3.idParent2Modifications.put(commit3.id, commitsOnModule2_3);
        commits.put(commit3.id, commit3);

        Modules modules = new Modules();
        Module moduleA = new Module();
        moduleA.commitsOnModuleInInterval = new CommitsOnModule();
        moduleA.commitsOnModuleInInterval.put(commitOnModule0_1_0.idCommitParent, commitOnModule0_1_0.idCommit, commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        moduleA.commitsOnModuleInInterval.put(commitOnModule1_2_0.idCommitParent, commitOnModule1_2_0.idCommit, commitOnModule1_2_0.pathOld, commitOnModule1_2_0.pathNew, commitOnModule1_2_0);
        Module moduleB = new Module();
        moduleB.commitsOnModuleInInterval = new CommitsOnModule();
        moduleB.commitsOnModuleInInterval.put(commitOnModule0_1_1.idCommitParent, commitOnModule0_1_1.idCommit, commitOnModule0_1_1.pathOld, commitOnModule0_1_1.pathNew, commitOnModule0_1_1);
        moduleB.commitsOnModuleInInterval.put(commitOnModule1_2_1.idCommitParent, commitOnModule1_2_1.idCommit, commitOnModule1_2_1.pathOld, commitOnModule1_2_1.pathNew, commitOnModule1_2_1);
        Module moduleC = new Module();
        moduleC.commitsOnModuleInInterval = new CommitsOnModule();
        moduleC.commitsOnModuleInInterval.put(commitOnModule2_3_0.idCommitParent, commitOnModule2_3_0.idCommit, commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        modules.put("a",moduleA);
        modules.put("b",moduleB);
        modules.put("c",moduleC);

        moduleA.commitsOnModuleInInterval.calcComplexityHistory(commits);
        moduleB.commitsOnModuleInInterval.calcComplexityHistory(commits);
        moduleC.commitsOnModuleInInterval.calcComplexityHistory(commits);
        moduleA.commitsOnModuleInInterval.calcComplexityHistoryNeighbor(commits, modules);
        assertEquals(0.30687, Math.floor(moduleA.commitsOnModuleInInterval.complexityHistoryNeighbor*100000)/100000);
    }
}
