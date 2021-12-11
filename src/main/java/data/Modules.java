package data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ast.RequesterFanIn;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import misc.DeserializerModification;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import util.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static util.FileUtil.findPathsFile;
import static util.FileUtil.readFile;
import static util.RepositoryUtil.checkoutRepository;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Modules implements Map<String, Module> {
    LinkedHashMap<String, Module> modules = new LinkedHashMap<>();
    public int getIdModule(String pathNew) {
        List<String> paths = new ArrayList<>(modules.keySet());
        return paths.indexOf(pathNew);
    }
    public void analyzeAllModules(Commits commits, Bugs bugsAll) {
        identifyChangesOnModule(commits);
        analyzeDevelopmentHistoryOnModule(commits);
        completeDevelopmentHistoryOnModule();
        embedInformationBugInHistoryOnModule(bugsAll);
    }
    private void identifyChangesOnModule(Commits commits) {
        for (Commit commit : ProgressBar.wrap(commits.values(), "identifyChangeOnModules")) {
            for (CommitsOnModule commitsOnModule : commit.idParent2Modifications.values()) {
                for (CommitOnModule commitOnModule : commitsOnModule.values()) {
                    if (!commitOnModule.pathOld.equals("/dev/null")) {
                        putChangeOnModule(commitOnModule, commitOnModule.pathOld);
                    }
                    if (!commitOnModule.pathNew.equals("/dev/null")) {
                        putChangeOnModule(commitOnModule, commitOnModule.pathNew);
                    }
                }
            }
        }
    }
    private void putChangeOnModule(CommitOnModule commitOnModule, String pathModule) {
        if (!modules.containsKey(pathModule)) {
            Module module = new Module(pathModule);
            modules.put(pathModule, module);
        }
        modules.get(pathModule).commitsOnModuleAll.put(commitOnModule.idCommitParent, commitOnModule.idCommit, commitOnModule.pathOld, commitOnModule.pathNew, commitOnModule);
    }
    private void analyzeDevelopmentHistoryOnModule(Commits commits) {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "identifyCommitsParent")) {
            Module moduleTarget = modules.get(pathModule);
            Queue<CommitOnModule> modificationsTarget = new ArrayDeque<>(moduleTarget.getCommitsOnModuleAll().values());

            CommitOnModule commitOnModuleTarget;
            while (0 < modificationsTarget.size()) {
                commitOnModuleTarget = modificationsTarget.poll();
                moduleTarget.commitsOnModuleAll.put(commitOnModuleTarget.idCommitParent, commitOnModuleTarget.idCommit, commitOnModuleTarget.pathOld, commitOnModuleTarget.pathNew, commitOnModuleTarget);
                if (commitOnModuleTarget.type.equals("ADD")) {//親が存在しない。
                } else {//親が存在する。
                    if (!commitOnModuleTarget.parents.isEmpty()) {//親特定済み
                        CommitsOnModule commitsOnModule = new CommitsOnModule();
                        commitOnModuleTarget.loadAncestors(commitsOnModule);
                        for (Entry<MultiKey<? extends String>, CommitOnModule> m : commitsOnModule.entrySet()) {
                            moduleTarget.commitsOnModuleAll.put(m.getKey(), m.getValue());
                        }
                        continue;
                    }
                    Set<String> idsCommitTarget = new HashSet<>();
                    Module moduleBefore = modules.get(commitOnModuleTarget.pathNewParent);
                    if (moduleBefore != null)
                        idsCommitTarget.addAll(moduleBefore.commitsOnModuleAll.values().stream().map(a -> a.idCommit).collect(Collectors.toList()));

                    Commit commitNow = commits.get(commitOnModuleTarget.idCommitParent);
                    while (true) {
                        if (idsCommitTarget.contains(commitNow.id)) {
                            boolean isOK = false;
                            for (CommitsOnModule commitsOnModule : commitNow.idParent2Modifications.values()) {
                                for (CommitOnModule commitOnModule : commitsOnModule.values()) {
                                    if (Objects.equals(commitOnModuleTarget.pathNewParent, commitOnModule.pathNew)) {
                                        commitOnModuleTarget.parents.put(commitOnModule.idCommitParent, commitOnModule.idCommit, commitOnModule.pathOld, commitOnModule.pathNew, commitOnModule);
                                        commitOnModuleTarget.idsCommitParent.add(commitOnModule.idCommit);
                                        commitOnModule.children.put(commitOnModuleTarget.idCommitParent, commitOnModuleTarget.idCommit, commitOnModuleTarget.pathOld, commitOnModuleTarget.pathNew, commitOnModuleTarget);
                                        commitOnModule.idsCommitChild.add(commitOnModuleTarget.idCommit);
                                        modificationsTarget.add(commitOnModule);
                                        isOK = true;
                                    }
                                }
                            }
                            if (isOK) break;
                        }
                        commitNow = commits.get(commitNow.idParentMaster);
                        if (commitNow == null) break;
                    }
                }
            }
        }
    }
    private void completeDevelopmentHistoryOnModule() {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "completeCommitHistory")) {
            Module moduleTarget = modules.get(pathModule);
            Queue<CommitOnModule> modificationsTarget = new ArrayDeque<>(moduleTarget.getCommitsOnModuleAll().values().stream().filter(a -> Objects.equals(a.type, "RENAME") | Objects.equals(a.type, "COPY")).collect(Collectors.toList()));
            CommitOnModule commitOnModuleTarget;
            while (0 < modificationsTarget.size()) {//過去方向
                commitOnModuleTarget = modificationsTarget.poll();
                for (CommitOnModule commitOnModule : commitOnModuleTarget.parents.values()) {
                    moduleTarget.commitsOnModuleAll.put(commitOnModule.idCommitParent, commitOnModule.idCommit, commitOnModule.pathOld, commitOnModule.pathNew, commitOnModule);
                    if (!moduleTarget.commitsOnModuleAll.containsValue(commitOnModule) & !modificationsTarget.contains(commitOnModule)) {
                        modificationsTarget.add(commitOnModule);
                    }
                }
            }
            modificationsTarget = new ArrayDeque<>(moduleTarget.getCommitsOnModuleAll().values().stream().filter(a -> Objects.equals(a.type, "RENAME") | Objects.equals(a.type, "COPY")).collect(Collectors.toList()));
            while (0 < modificationsTarget.size()) {//未来方向
                commitOnModuleTarget = modificationsTarget.poll();
                for (CommitOnModule commitOnModule : commitOnModuleTarget.children.values()) {
                    moduleTarget.commitsOnModuleAll.put(commitOnModule.idCommitParent, commitOnModule.idCommit, commitOnModule.pathOld, commitOnModule.pathNew, commitOnModule);
                    if (!moduleTarget.commitsOnModuleAll.containsValue(commitOnModule) & !modificationsTarget.contains(commitOnModule)) {
                        modificationsTarget.add(commitOnModule);
                    }
                }
            }
        }
    }
    private void embedInformationBugInHistoryOnModule(Bugs bugsAll) {
        for(String idBug: bugsAll.keySet()){
            Bug bug = bugsAll.get(idBug);
            for(BugAtomic bugAtomic: bug.bugAtomics){
                if(Objects.equals(bugAtomic.path,"/dev/null")) continue;
                Module module = modules.get(bugAtomic.path);
                for(CommitOnModule commitOnModule: module.commitsOnModuleAll.queryByIdCommit(bugAtomic.idCommitFix)){
                    commitOnModule.IdsCommitsInducingBugsThatThisCommitFixes.addAll(bugAtomic.idsCommitInduce);
                    commitOnModule.IdsBugThatThisCommitFixing.add(bug.id);
                }
                for(String idCommitInduce: bugAtomic.idsCommitInduce){
                    for(CommitOnModule commitOnModule: module.commitsOnModuleAll.queryByIdCommit(idCommitInduce)){
                        commitOnModule.IdsCommitsFixingBugThatThisCommitInduces.add(bugAtomic.idCommitFix);
                    }
                }
            }
        }
    }
    public void identifyTargetModules(Modules modulesAll, Repository repositoryMethod, String commitTarget) throws IOException, GitAPIException {
        List<String> pathSources = new ArrayList<>();
        RevCommit revCommit = repositoryMethod.parseCommit(repositoryMethod.resolve(commitTarget));
        RevTree tree = revCommit.getTree();
        try (TreeWalk treeWalk = new TreeWalk(repositoryMethod)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathSuffixFilter.create(".mjava"));
            while (treeWalk.next()) {
                pathSources.add(treeWalk.getPathString());
            }
        }
        for (String pathSource : ProgressBar.wrap(pathSources, "identifyTargetModules")) {
            Module moduleTarget = modulesAll.get(pathSource).clone();
            if (!pathSource.contains("test")){
                modules.put(pathSource, moduleTarget);
            }
        }
    }
    public void calculateAST(Repository repositoryMethod, String revisionMethodTarget)  {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "calculateAST")) {
            Module module = modules.get(pathModule);
            module.calcAST();
        }
    }
    public void calculateCommitGraph(Commits commitsAll, Modules modulesAll, Committers authors, String[] intervalRevisionMethod_referableCalculatingProcessMetrics)  {
        for (Module module : ProgressBar.wrap(modules.values(), "calculateCommitGraph")) {
            module.identifyCommitGraphTarget(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
            module.calcCommitGraph(commitsAll, modulesAll, authors);
        }
    }
    public void calculateMetricsCode(Repository repositoryFile, String revisionFileTarget, Repository repositoryMethod, String revisionMethodTarget) throws IOException, GitAPIException {
        checkoutRepository(repositoryFile, revisionFileTarget);
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "calcCodeMetrics")) {
            Module module = modules.get(pathModule);
            module.identifySourcecodeTarget(repositoryMethod, revisionMethodTarget);
            module.calcMetricsCode();
        }
        //fanInを計測
        try {
            String pathRepositoryFile = repositoryFile.getDirectory().getParentFile().getAbsolutePath();
            System.out.println("calculating FanIn...");
            final String[] sourcePathDirs = {};
            final String[] libraries = FileUtil.findPathsFile(pathRepositoryFile, ".jar").toArray(new String[0]);
            final String[] sources = FileUtil.findPathsFile(pathRepositoryFile, ".java").toArray(new String[0]);

            ASTParser parser = ASTParser.newParser(AST.JLS14);
            final Map<String, String> options = JavaCore.getOptions();
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_14);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_14);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_14);
            parser.setCompilerOptions(options);
            parser.setResolveBindings(true);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setBindingsRecovery(true);
            parser.setStatementsRecovery(true);
            parser.setEnvironment(libraries, sourcePathDirs, null, true);
            String[] keys = new String[]{""};
            RequesterFanIn requesterFanIn = new RequesterFanIn(modules);
            parser.createASTs(sources, null, keys, requesterFanIn, new NullProgressMonitor());
            for (String idMethodCalled : requesterFanIn.methodsCalled) {
                if (idMethodCalled == null) continue;
                boolean flag = false;
                for (String pathMethod : modules.keySet()) {
                    String idMethod = modules.get(pathMethod).id;
                    if (Objects.equals(idMethod, idMethodCalled)) {
                        modules.get(pathMethod).sourcecode.fanin++;
                        //flag=true;
                        break;
                    }
                }
            }
            System.out.println("FanIn caluculated");
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }
    public void calculateMetricsProcess(Commits commitsAll, Modules modulesAll, String[] intervalRevision_referableCalculatingMetricsIndependentOnFuture, String[] intervalRevision_referableCalculatingMetricsDependentOnFuture) {
        for (Module module : ProgressBar.wrap(modules.values(), "identifing commitGraph To calculate process metrics")) {
            module.identifyCommitGraphTarget(commitsAll, intervalRevision_referableCalculatingMetricsIndependentOnFuture);
        }
        for (Module module : ProgressBar.wrap(modules.values(), "calcProcessMetrics1")) {
            module.calcMetricsProcess1(commitsAll, intervalRevision_referableCalculatingMetricsIndependentOnFuture, intervalRevision_referableCalculatingMetricsDependentOnFuture);
        }
        for (Module module : ProgressBar.wrap(modules.values(), "calcProcessMetrics2")) {
            module.calcMetricsProcess2(commitsAll, modulesAll);
        }
    }
    public void saveAsJson(String pathModules) {
        int count = 0;
        for (Entry<String, Module> entry : ProgressBar.wrap(modules.entrySet(), "saveModules")) {
            String path = pathModules + "/" + entry.getKey() + ".json";
            File file = new File(path);
            path = file.getAbsolutePath();
            if (254 < path.length()) {
                path = pathModules + "/" + Integer.toString(count) + ".json";
                file = new File(path);
            }
            File dir = new File(file.getParent());
            dir.mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter writer = new BufferedWriter(osw)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(writer, entry.getValue());
                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveAsCSV(String pathOutput) throws IOException {
        File dir = new File(pathOutput);
        File dirParent = new File(dir.getParent());
        dirParent.mkdirs();

        File file = new File(pathOutput);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("");
        for(Module module: modules.values()){
            fileWriter.write(module.outputRow());
        }
        fileWriter.close();
    }
    public void loadModulesFromFile(String pathModules) {
        List<String> paths = FileUtil.findPathsFile(pathModules, ".json");
        for (String path : ProgressBar.wrap(paths, "loadModulesFromFile")) {
            try {
                String strFile = readFile(path);
                if(strFile.length()==0){
                    System.out.println(path);
                }
                ObjectMapper mapper = new ObjectMapper();
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addKeyDeserializer(MultiKey.class, new DeserializerModification());
                mapper.registerModule(simpleModule);
                Module module = mapper.readValue(strFile, new TypeReference<Module>() {});
                modules.put(module.path, module);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override public int size() {
        return modules.size();
    }
    @Override public boolean isEmpty() {
        return modules.isEmpty();
    }
    @Override public boolean containsKey(Object key) {
        return modules.containsKey(key);
    }
    @Override public boolean containsValue(Object value) {
        return modules.containsValue(value);
    }
    @Override public Module get(Object key) {
        return modules.get(key);
    }
    @Override public Module put(String key, Module value) {
        return modules.put(key, value);
    }
    @Override public Module remove(Object key) {
        return modules.remove(key);
    }
    @Override public void putAll(Map<? extends String, ? extends Module> m) {
        modules.putAll(m);
    }
    @Override public void clear() {
        modules.clear();
    }
    @Override public Set<String> keySet() {
        return modules.keySet();
    }
    @Override public Collection<Module> values() {
        return modules.values();
    }
    @Override public Set<Entry<String, Module>> entrySet() {
        return modules.entrySet();
    }
}
