/*
package data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class TestCommitsOnModule {
    @Test
    public void testCalcModuleHistories0(){
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        CommitOnModule commitOnModule = new CommitOnModule();
        commitsOnModule.put("","","","",commitOnModule);
        commitsOnModule.calcModuleHistories();
        assertEquals(1, commitsOnModule.numOfCommits);
    }
    @Test
    public void testCalcModuleHistories1(){
        String pathModule="org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/dialogs/CommitDialog#okPressed().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcModuleHistories(commitsAll, commitEdgesMethod);
        assertEquals(10, module.getModuleHistories());
    }
    @Test
    public void testCalcModuleHistories2(){
        String pathModule ="org.eclipse.egit.core/src/org/eclipse/egit/core/project/RepositoryMapping#getGitDirAbsolutePath().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcModuleHistories(commitsAll, commitEdgesMethod);
        assertEquals(3, module.getModuleHistories());
    }
    @Test
    public void testCalcModuleHistories3(){
        String pathModule = "org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/actions/MergeActionHandler#execute(ExecutionEvent).mjava";
        Module module = modulesAll.get(pathModule);
        module.calcModuleHistories(commitsAll, commitEdgesMethod);
        assertEquals(8, module.getModuleHistories());
    }
    @Test
    public void testCalcModuleHistories4(){
        String pathModule = "org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/history/GitHistoryPage#initActions().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcModuleHistories(commitsAll, commitEdgesMethod);
        assertEquals(11, module.getModuleHistories());
    }
    @Test
    public void testCalcModuleHistories5(){
        String pathModule = "org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/repository/tree/RepositoryTreeNode#hashCode().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcModuleHistories(commitsAll, commitEdgesMethod);
        assertEquals(10, module.getModuleHistories());
    }


    @Test
    public void testCalcAuthors1(){
        String pathModule ="org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/dialogs/CommitDialog#okPressed().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcAuthors(commitsAll, commitEdgesMethod);
        assertEquals(6, module.getAuthors());
    }
    @Test
    public void testCalcAuthors2(){
        String pathModule ="org.eclipse.egit.core/src/org/eclipse/egit/core/project/RepositoryMapping#getGitDirAbsolutePath().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcAuthors(commitsAll, commitEdgesMethod);
        assertEquals(3, module.getAuthors());
    }
    @Test
    public void testCalcAuthors3(){
        String pathModule ="org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/actions/MergeActionHandler#execute(ExecutionEvent).mjava";
        Module module = modulesAll.get(pathModule);
        module.calcAuthors(commitsAll, commitEdgesMethod);
        assertEquals(4, module.getAuthors());
    }
    @Test
    public void testCalcAuthors4(){
        String pathModule ="org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/history/GitHistoryPage#initActions().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcAuthors(commitsAll, commitEdgesMethod);
        assertEquals(4, module.getAuthors());
    }
    @Test
    public void testCalcAuthors5(){
        String pathModule ="org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/repository/tree/RepositoryTreeNode#hashCode().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcAuthors(commitsAll, commitEdgesMethod);
        assertEquals(3, module.getAuthors());
    }


    @Test
    public void testCalcFixChgNum1(){
        String pathModule="org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/dialogs/CompareTreeView#reactOnOpen(OpenEvent).mjava";
        Module module = modulesAll.get(pathModule);
        module.calcFixChgNum(commitsAll, bugsAll, commitEdgesMethod);
        assertEquals(6 ,module.fixChgNum);
    }

    @Test
    public void testCalcMinInterval1(){
        String pathModule ="org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/repository/tree/RepositoryTreeNode#hashCode().mjava";
        Module module = modulesAll.get(pathModule);
        module.calcMinInterval(commitsAll, commitEdgesMethod);
        assertEquals(1, module.getMinInterval());
    }
}

 */
