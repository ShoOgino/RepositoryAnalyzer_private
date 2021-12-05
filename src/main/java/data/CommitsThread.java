package data;

import me.tongfei.progressbar.ProgressBar;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CommitsThread  extends Thread{
    Repository repository;
    List<RevCommit> revcommits;
    String pathCommits;

    public CommitsThread(Repository repository, List<RevCommit> revcommits, String pathCommits){
        this.repository = repository;
        this.revcommits = revcommits;
        this.pathCommits = pathCommits;
    }

    public void run() {
        try {
            System.out.println("thread started");
            //それぞれのコミットについて、変更内容を取得する。
            for (RevCommit revCommit : ProgressBar.wrap(revcommits, "loadCommitsFromRepository")) {
                Commit commit = new Commit();
                commit.id = revCommit.getName();
                //PersonIdent authorIdent = revCommit.getAuthorIdent();
                //commit.date = (int)(authorIdent.getWhen().getTime()/1000);
                commit.date = revCommit.getCommitTime();
                commit.author = revCommit.getAuthorIdent().getName();
                commit.message = revCommit.getFullMessage();
                commit.isMerge = revCommit.getParentCount() > 1;
                commit.idParentMaster = revCommit.getParentCount() == 0 ? "0000000000000000000000000000000000000000" : revCommit.getParent(0).getName();
                if (revCommit.getParentCount() == 0) {
                    CommitsOnModule commitsOnModule = calcChangeOnModulesBetweenCommits(repository, revCommit, null);
                    commit.idParent2Modifications.put("0000000000000000000000000000000000000000", commitsOnModule);
                } else {
                    for (RevCommit revCommitParent : revCommit.getParents()) {
                        CommitsOnModule commitsOnModule = calcChangeOnModulesBetweenCommits(repository, revCommit, revCommitParent);
                        commit.idParent2Modifications.put(revCommitParent.getName(), commitsOnModule);
                    }
                }
                for (RevCommit revCommitParentSub : revCommit.getParents()) {
                    if (Objects.equals(revCommitParentSub.getName(), commit.idParentMaster)) continue;
                    CommitsOnModule commitsOnModuleSub = commit.idParent2Modifications.get(revCommitParentSub.getName());
                    CommitsOnModule commitsOnModuleMain = commit.idParent2Modifications.get(commit.idParentMaster);
                    List<String> pathsMain = commitsOnModuleMain.values().stream().filter(a -> !Objects.equals(a.type, "DELETE")).map(a -> a.pathNew).collect(Collectors.toList());
                    commitsOnModuleSub.entrySet().removeIf(
                            entry -> !pathsMain.contains(entry.getKey().getKey(3))
                    );
                    for (CommitOnModule commitOnModule : commitsOnModuleMain.values()) {
                        if (0 == commitsOnModuleSub.queryPathNew(commitOnModule.pathNew).size()) {
                            if (!commitOnModule.type.equals("DELETE")) {
                                CommitOnModule m = new CommitOnModule();
                                m.idCommit = revCommit.getName();
                                m.idCommitParent = revCommitParentSub.getName();
                                //PersonIdent authorIdent_ = revCommit.getAuthorIdent();
                                //m.date = (int)(authorIdent.getWhen().getTime()/1000);
                                m.date = revCommit.getCommitTime();
                                m.author = revCommit.getAuthorIdent().getName();
                                m.isMerge = revCommit.getParentCount() > 1;
                                m.message = revCommit.getFullMessage();
                                m.type = "UNCHANGE";
                                m.pathOld = commitOnModule.pathNew;
                                m.pathNew = commitOnModule.pathNew;
                                m.sourceOld = commitOnModule.sourceNew;
                                m.sourceNew = commitOnModule.sourceNew;
                                m.pathNewParent = commitOnModule.pathNew;
                                commitsOnModuleSub.put(m.idCommitParent, m.idCommit, m.pathOld, m.pathNew, m);
                            }
                        }
                    }
                }
                commit.save(pathCommits + "/" + commit.id + ".json", "CommitsAll");
            }
            System.out.println("thread ends");
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public CommitsOnModule calcChangeOnModulesBetweenCommits(Repository repository, RevCommit revCommit, RevCommit revCommitParent) throws IOException {
        CommitsOnModule commitsOnModule = new CommitsOnModule();
        try(DiffFormatter diffFormatter = new DiffFormatter(System.out)) {
            diffFormatter.setRepository(repository);
            diffFormatter.setDetectRenames(true);
            diffFormatter.setDiffAlgorithm(new HistogramDiff());
            ObjectReader reader = repository.newObjectReader();
            //diffEntryを取得
            AbstractTreeIterator oldTreeIter = revCommitParent ==null ? new EmptyTreeIterator() :new CanonicalTreeParser(null, reader, revCommitParent.getTree());
            AbstractTreeIterator newTreeIter = new CanonicalTreeParser(null, reader, revCommit.getTree());
            //renameを検出。明示的。
            RenameDetector rd = new RenameDetector(repository);
            rd.setRenameScore(30);
            rd.addAll(diffFormatter.scan(oldTreeIter, newTreeIter));
            List<DiffEntry> diffEntries = rd.compute();
            for (DiffEntry diffEntry : diffEntries) {
                CommitOnModule commitOnModule = new CommitOnModule();
                commitOnModule.idCommit = revCommit.getName();
                commitOnModule.idCommitParent = revCommitParent == null ? "0000000000000000000000000000000000000000" : revCommitParent.getName();
                //PersonIdent authorIdent = revCommit.getAuthorIdent();
                //commitOnModule.date = (int)(authorIdent.getWhen().getTime()/1000);
                commitOnModule.date = revCommit.getCommitTime();
                commitOnModule.author = revCommit.getAuthorIdent().getName();
                commitOnModule.isMerge = revCommit.getParentCount() > 1;
                commitOnModule.type = diffEntry.getChangeType().toString();
                commitOnModule.message = revCommit.getFullMessage();
                commitOnModule.pathOld = diffEntry.getOldPath();
                commitOnModule.pathNew = diffEntry.getNewPath();
                if (commitOnModule.type.equals("ADD") & commitOnModule.isMerge) commitOnModule.pathNewParent = diffEntry.getNewPath();
                else commitOnModule.pathNewParent = diffEntry.getOldPath();

                //コミット直前のソースコードを取得
                if (diffEntry.getOldId().name().equals("0000000000000000000000000000000000000000")) {
                    commitOnModule.sourceOld = null;
                } else {
                    ObjectLoader loader = repository.open(diffEntry.getOldId().toObjectId());
                    String sourceOldRaw = new String(loader.getBytes());
                    commitOnModule.sourceOld = new Sourcecode(sourceOldRaw);
                }
                //コミット直後のソースコードを取得
                if (diffEntry.getNewId().name().equals("0000000000000000000000000000000000000000")) {
                    commitOnModule.sourceNew = null;
                } else {
                    ObjectLoader loader = repository.open(diffEntry.getNewId().toObjectId());
                    String sourceNewRaw = new String(loader.getBytes());
                    commitOnModule.sourceNew = new Sourcecode(sourceNewRaw);
                }

                for (Edit changeOriginal : diffFormatter.toFileHeader(diffEntry).toEditList()) {
                    Diff diff = new Diff();
                    for (int i = changeOriginal.getBeginA(); i < changeOriginal.getEndA(); i++) {
                        diff.linesBefore.add(i);
                    }
                    for (int i = changeOriginal.getBeginB(); i < changeOriginal.getEndB(); i++) {
                        diff.linesAfter.add(i);
                    }
                    commitOnModule.diffs.add(diff);
                }
                commitsOnModule.put(commitOnModule.idCommitParent, commitOnModule.idCommit, commitOnModule.pathOld, commitOnModule.pathNew, commitOnModule);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return commitsOnModule;
    }
}
