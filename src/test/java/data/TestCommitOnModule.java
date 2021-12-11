package data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCommitOnModule {
    @Test public void testsCalcStmtAdded0(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode("");
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcStmtAdded0_after(){\n" +
                "    sample();\n" +
                "}"
        );
        commitOnModule.calcNumOfAdditionsStatement();
        assertEquals(1, commitOnModule.numOfAdditionsStatement);
    }
    @Test public void testsCalcStmtAdded1(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcStmtAdded1(){\n" +
                        "    sample();\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcStmtAdded1(){\n" +
                        "    sample();\n" +
                        "    sample();\n" +
                        "}"
        );
        commitOnModule.calcNumOfAdditionsStatement();
        assertEquals(1, commitOnModule.numOfAdditionsStatement);
    }
    @Test public void testsCalcStmtAdded2(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcStmtAdded2(){\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcStmtAdded2(){\n" +
                        "    sample();\n" +
                        "    sample();\n" +
                        "}"
        );
        commitOnModule.calcNumOfAdditionsStatement();
        assertEquals(2, commitOnModule.numOfAdditionsStatement);
    }
    @Test public void testsCalcStmtDeleted1(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcStmtDeleted1(){\n" +
                        "    sample();\n" +
                        "    sample();\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcStmtDeleted1(){\n" +
                        "    sample();\n" +
                        "}"
        );
        commitOnModule.calcNumOfDeletionsStatement();
        assertEquals(1, commitOnModule.numOfDeletionsStatement);
    }
    @Test public void testsCalcStmtDeleted2(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcStmtDeleted2(){\n" +
                        "    sample();\n" +
                        "    sample();\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcStmtDeleted2(){\n" +
                        "}"
        );
        commitOnModule.calcNumOfDeletionsStatement();
        assertEquals(2, commitOnModule.numOfDeletionsStatement);
    }
    @Test public void testCalcChurn0(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcStmtDeleted1(){\n" +
                        "    sample0();\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcStmtDeleted1(){\n" +
                        "    sample0();\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitOnModule.calcNumOfChurnsStatement();
        assertEquals(1, commitOnModule.numOfChurnsStatement);
    }
    @Test public void testCalcChurn1(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcChurn1(){\n" +
                        "    sample0();\n" +
                        "    sample1();\n" +
                        "    sample2();\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcChurn1(){\n" +
                        "    sample0();\n" +
                        "    sample2();\n" +
                        "    sample3();\n" +
                        "}"
        );
        commitOnModule.calcNumOfChurnsStatement();
        assertEquals(0, commitOnModule.numOfChurnsStatement);
    }
    @Test public void testCalcElseAdded0(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcElseAdded0(){\n" +
                        "    if(true){}\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcElseAdded0(){\n" +
                        "    if(true){}\n" +
                        "    else{}\n" +
                        "}"
        );
        commitOnModule.calcNumOfAdditionsStatementElse();
        assertEquals(1, commitOnModule.numOfAdditionsStatementElse);
    }
    @Test public void testCalcElseAdded1(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcElseAdded1(){\n" +
                        "    if(true){}\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcElseAdded1(){\n" +
                        "    if(true){}\n" +
                        "    else if(true){}\n" +
                        "    else{}\n" +
                        "}"
        );
        commitOnModule.calcNumOfAdditionsStatementElse();
        assertEquals(2, commitOnModule.numOfAdditionsStatementElse);
    }
    @Test public void testCalcElseDeleted0(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcElseAdded0(){\n" +
                        "    if(true){}\n" +
                        "    else{}\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcElseAdded0(){\n" +
                        "    if(true){}\n" +
                        "}"
        );
        commitOnModule.calcNumOfDeletionsStatementElse();
        assertEquals(1, commitOnModule.numOfDeletionsStatementElse);
    }
    @Test public void testCalcDecl0(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcElseAdded0(){\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcElseAdded0(String example){\n" +
                        "}"
        );
        commitOnModule.calcNumOfChangesDeclarationItself();
        assertEquals(1, commitOnModule.numOfChangesDeclarationItself);
    }
    @Test public void testCalcCond0(){
        CommitOnModule commitOnModule = new CommitOnModule();
        commitOnModule.sourceOld=new Sourcecode(
                "public void example4TestCalcCond0(){\n" +
                        "    if(which.equals(gsd.getRepository())){}\n" +
                        "}"
        );
        commitOnModule.sourceNew=new Sourcecode(
                "public void example4TestCalcCond0(){\n" +
                        "    if(which.getRepository().equals(gsd.getRepository())){}\n" +
                        "}"
        );
        commitOnModule.calcNumOfChangesCondition();
        assertEquals(1, commitOnModule.numOfChangesCondition);
    }
}
