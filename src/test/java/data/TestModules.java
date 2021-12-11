package data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestModules {
    @Test
    public void testIdentifyChangesOnModule(){
        Commits commits = new Commits();

        Commit commit1 = new Commit();
        commit1.id="1";
        commit1.idParentMaster = "0";
        CommitsOnModule commitsOnModule0_1=new CommitsOnModule();
        CommitOnModule commitOnModule0_1_0 = new CommitOnModule();
        commitOnModule0_1_0.type="ADD";
        commitOnModule0_1_0.idCommitParent = "0";
        commitOnModule0_1_0.idCommit = "1";
        commitOnModule0_1_0.pathOld="/dev/null";
        commitOnModule0_1_0.pathNew="a";
        commitOnModule0_1_0.pathNewParent = "/dev/null";
        commitsOnModule0_1.put("0","1",commitOnModule0_1_0.pathOld, commitOnModule0_1_0.pathNew, commitOnModule0_1_0);
        commit1.idParent2Modifications.put("0", commitsOnModule0_1);
        commits.put("1",commit1);

        Commit commit2 = new Commit();
        commit2.id="2";
        commit2.idParentMaster="1";
        commits.put("2",commit2);

        Commit commit3 = new Commit();
        commit3.id = "3";
        commit3.idParentMaster = "2";
        CommitsOnModule commitsOnModule2_3=new CommitsOnModule();
        CommitOnModule commitOnModule2_3_0 = new CommitOnModule();
        commitOnModule2_3_0.type="MODIFY";
        commitOnModule2_3_0.idCommitParent = "2";
        commitOnModule2_3_0.idCommit= "3";
        commitOnModule2_3_0.pathOld="a";
        commitOnModule2_3_0.pathNew="a";
        commitOnModule2_3_0.pathNewParent="a";
        commitsOnModule2_3.put("2","3",commitOnModule2_3_0.pathOld, commitOnModule2_3_0.pathNew, commitOnModule2_3_0);
        commit2.idParent2Modifications.put("2", commitsOnModule2_3);
        commits.put("3",commit3);

        Modules modules = new Modules();
        Bugs bugs = new Bugs();
        modules.analyzeAllModules(commits, bugs);
        assertEquals(commitOnModule0_1_0, commitOnModule2_3_0.parents.get("0", "1", "/dev/null", commitOnModule2_3_0.pathNew));
    }
    /*
    private void check() {
        for(Module module: ProgressBar.wrap(modules.values(),"check")) {
            boolean hasAddOrRenameOrCopy = false;
            for(ChangeOnModule changeOnModule : module.changesOnModule.values()) {
                if(java.util.Objects.equals(changeOnModule.type, "ADD")
                        | java.util.Objects.equals(changeOnModule.type, "RENAME")
                        | java.util.Objects.equals(changeOnModule.type, "COPY")){
                    hasAddOrRenameOrCopy=true;
                }
            }
            if (hasAddOrRenameOrCopy) continue;
            else System.out.println(module.path);
        }
    }

    public void checkParent(){
        int countAll = 0;
        int countYabai =0;
        int count = 0;
        for(Module module: ProgressBar.wrap(modules.values(), "testIdentifyParents")){
            for(ChangeOnModule changeOnModule : module.changesOnModule.values()){
                if(changeOnModule.type.equals("ADD"))continue;;
                countAll++;
                if(changeOnModule.parents.size()==0){
                    System.out.println(module.path);
                    System.out.println(changeOnModule.idCommit);
                    if(java.util.Objects.equals(changeOnModule.type, "RENAME") | java.util.Objects.equals(changeOnModule.type, "COPY")) countYabai++;
                    count++;
                    continue;
                }
                boolean isParentOk = true;
                for(ChangeOnModule changeOnModuleParent : changeOnModule.parentsModification.values()){
                    if(!java.util.Objects.equals(changeOnModule.sourceOld, changeOnModuleParent.sourceNew)){
                        isParentOk=false;
                        break;
                    }
                }
                if(isParentOk)continue;
                count++;
                if(java.util.Objects.equals(changeOnModule.type, "RENAME") | java.util.Objects.equals(changeOnModule.type, "COPY")) countYabai++;
                System.out.println(module.path);
                System.out.println(changeOnModule.idCommit);
                System.out.println(changeOnModule.sourceOld);
                for(ChangeOnModule changeOnModuleBefore : changeOnModule.parentsModification.values()) {
                    System.out.println(changeOnModuleBefore.idCommit);
                    System.out.println(changeOnModuleBefore.sourceNew);
                }
            }
        }
        System.out.println(countAll);
        System.out.println(countYabai);
        System.out.println(count);
    }


    public void checkChildren(){
        int countAll = 0;
        int countYabai =0;
        int count = 0;
        for(Module module: ProgressBar.wrap(modules.values(), "testIdentifyChildlen")){
            for(ChangeOnModule changeOnModule : module.changesOnModule.values()){
                if(changeOnModule.type.equals("DELETE"))continue;;
                countAll++;
                if(modification.children.size()==0){
                    System.out.println(module.path);
                    System.out.println(modification.idCommit);
                    if(Objects.equals(modification.type, "RENAME") | Objects.equals(modification.type, "COPY")) countYabai++;
                    count++;
                    continue;
                }
                boolean isChildOK = true;
                for(ChangeOnModule child: changeOnModule.childrenModification.values()){
                    if(!java.util.Objects.equals(changeOnModule.sourceNew, child.sourceOld)){
                        isChildOK=false;
                        break;
                    }
                }
                if(isChildOK)continue;
                count++;
                if(java.util.Objects.equals(changeOnModule.type, "RENAME") | Objects.equals(changeOnModule.type, "COPY")) countYabai++;
                System.out.println(module.path);
                System.out.println(changeOnModule.idCommit);
                System.out.println(changeOnModule.sourceNew);
                for(ChangeOnModule changeOnModuleAfter : changeOnModule.childrenModification.values()) {
                    System.out.println(changeOnModuleAfter.idCommit);
                    System.out.println(changeOnModuleAfter.sourceOld);
                }
            }
        }
        System.out.println(countAll);
        System.out.println(countYabai);
        System.out.println(count);
    }

    @Test
    public void testIdentifyCommitsParent() throws Exception {
        util.RepositoryUtil.checkoutRepository(pathRepositoryFile, commitEdgesFile[1]);
        util.RepositoryUtil.checkoutRepository(pathRepositoryMethod, commitEdgesMethod[1]);
        commitsAll.loadCommitsFromRepository(pathRepositoryMethod, idCommitHead);
        modulesAll.analyzeModules(commitsAll);
        for(Module module: ProgressBar.wrap(modulesAll.values(), "testidentifyparents")){
            for(Modification modification: module.modifications.values()){
                if(modification.type.equals("ADD"))continue;;
                boolean isParentOk = false;
                List<Modification> modificationsBefore = module.modifications.findFromIdCommit(modification.parent);
                for(Modification modificationBefore: modificationsBefore){
                    if(Objects.equal(modificationBefore.sourceNew, modification.sourceOld)){
                        isParentOk=true;
                        break;
                    }
                }
                if(isParentOk)continue;
                System.out.println(module.path);
                System.out.println(modification.idCommit);
                System.out.println(modification.sourceOld);
                for(Modification modificationBefore: modificationsBefore) {
                    System.out.println(modificationBefore.idCommit);
                    System.out.println(modificationBefore.sourceNew);
                }
            }
        }
    }
     */
}
