package data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ast.RequesterFanIn;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import misc.DeserializerModification;
import misc.DoubleConverter;
import me.tongfei.progressbar.ProgressBar;
import net.sf.jsefa.Serializer;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static util.FileUtil.findFiles;
import static util.FileUtil.readFile;
import static util.RepositoryUtil.checkoutRepository;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Modules implements Map<String, Module> {
    HashMap<String, Module> modules = new HashMap<>();

    public void analyzeAllModules(Commits commits) {
        identifyChangesOnModule(commits);
        analyzeDevelopmentHistoryOnModule(commits);
        completeDevelopmentHistoryOnModule();
    }
    public void identifyChangesOnModule(Commits commits) {
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
    public void putChangeOnModule(CommitOnModule commitOnModule, String pathModule) {
        if (!modules.containsKey(pathModule)) {
            Module module = new Module(pathModule);
            modules.put(pathModule, module);
        }
        modules.get(pathModule).commitsOnModule.put(commitOnModule.idCommitParent, commitOnModule.idCommit, commitOnModule.pathOld, commitOnModule.pathNew, commitOnModule);
    }
    public void analyzeDevelopmentHistoryOnModule(Commits commits) {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "identifyCommitsParent")) {
            Module moduleTarget = modules.get(pathModule);
            Queue<CommitOnModule> modificationsTarget = new ArrayDeque<>(moduleTarget.getCommitsOnModule().values());

            CommitOnModule commitOnModuleTarget;
            while (0 < modificationsTarget.size()) {
                commitOnModuleTarget = modificationsTarget.poll();
                moduleTarget.commitsOnModule.put(commitOnModuleTarget.idCommitParent, commitOnModuleTarget.idCommit, commitOnModuleTarget.pathOld, commitOnModuleTarget.pathNew, commitOnModuleTarget);
                if (commitOnModuleTarget.type.equals("ADD")) {//親が存在しない。
                } else {//親が存在する。
                    if (!commitOnModuleTarget.parents.isEmpty()) {//親特定済み
                        CommitsOnModule commitsOnModule = new CommitsOnModule();
                        commitOnModuleTarget.loadAncestors(commitsOnModule);
                        for (Entry<MultiKey<? extends String>, CommitOnModule> m : commitsOnModule.entrySet()) {
                            moduleTarget.commitsOnModule.put(m.getKey(), m.getValue());
                        }
                        continue;
                    }
                    Set<String> idsCommitTarget = new HashSet<>();
                    Module moduleBefore = modules.get(commitOnModuleTarget.pathNewParent);
                    if (moduleBefore != null)
                        idsCommitTarget.addAll(moduleBefore.commitsOnModule.values().stream().map(a -> a.idCommit).collect(Collectors.toList()));

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
    public void completeDevelopmentHistoryOnModule() {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "completeCommitHistory")) {
            Module moduleTarget = modules.get(pathModule);
            Queue<CommitOnModule> modificationsTarget = new ArrayDeque<>(moduleTarget.getCommitsOnModule().values().stream().filter(a -> Objects.equals(a.type, "RENAME") | Objects.equals(a.type, "COPY")).collect(Collectors.toList()));
            CommitOnModule commitOnModuleTarget;
            while (0 < modificationsTarget.size()) {//過去方向
                commitOnModuleTarget = modificationsTarget.poll();
                for (CommitOnModule commitOnModule : commitOnModuleTarget.parents.values()) {
                    moduleTarget.commitsOnModule.put(commitOnModule.idCommitParent, commitOnModule.idCommit, commitOnModule.pathOld, commitOnModule.pathNew, commitOnModule);
                    if (!moduleTarget.commitsOnModule.containsValue(commitOnModule) & !modificationsTarget.contains(commitOnModule)) {
                        modificationsTarget.add(commitOnModule);
                    }
                }
            }
            modificationsTarget = new ArrayDeque<>(moduleTarget.getCommitsOnModule().values().stream().filter(a -> Objects.equals(a.type, "RENAME") | Objects.equals(a.type, "COPY")).collect(Collectors.toList()));
            while (0 < modificationsTarget.size()) {//未来方向
                commitOnModuleTarget = modificationsTarget.poll();
                for (CommitOnModule commitOnModule : commitOnModuleTarget.children.values()) {
                    moduleTarget.commitsOnModule.put(commitOnModule.idCommitParent, commitOnModule.idCommit, commitOnModule.pathOld, commitOnModule.pathNew, commitOnModule);
                    if (!moduleTarget.commitsOnModule.containsValue(commitOnModule) & !modificationsTarget.contains(commitOnModule)) {
                        modificationsTarget.add(commitOnModule);
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
                //System.out.println(treeWalk.getPathString());
                pathSources.add(treeWalk.getPathString());
            }
        }
        for (String pathSource : ProgressBar.wrap(pathSources, "identifyTargetModules")) {
            //Module module = new Module(pathSource);
            Module moduleTarget = modulesAll.get(pathSource).clone();
            if (!pathSource.contains("test")
            ){
                modules.put(pathSource, moduleTarget);
            }
            //if(!pathSource.contains("test") & 0<commitsInInterval.size()) modules.put(pathSource, moduleTarget);
        }
    }
    public void calculateAST(Repository repositoryMethod, String revisionMethodTarget) throws IOException, GitAPIException {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "calculateAST")) {
            Module module = modules.get(pathModule);
            module.calcAST();
        }
    }
    public void calculateCommitGraph(Commits commitsAll, Modules modulesAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics, Bugs bugsAll) throws IOException {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "calculateCommitGraph")) {
            long startTimeOverall = System.currentTimeMillis();
            Module module = modules.get(pathModule);
            module.identifyCommitGraphTarget(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
            module.calcCommitGraph(commitsAll, modulesAll, intervalRevisionMethod_referableCalculatingProcessMetrics, bugsAll);
            long endTimeOverall = System.currentTimeMillis();
            if(60*10<(endTimeOverall - startTimeOverall)/(1000)){
                System.out.println(pathModule);
                System.out.println("処理時間：" + (endTimeOverall - startTimeOverall)/(1000) + " s");
            }
        }
        System.out.println("");
    }
    public void calculateCodeMetrics(Repository repositoryFile, String revisionFileTarget, Repository repositoryMethod, String revisionMethodTarget) throws IOException, GitAPIException {
        checkoutRepository(repositoryFile, revisionFileTarget);
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "calcCodeMetrics")) {
            Module module = modules.get(pathModule);
            //codemetrics(Re-evaluating Method-Level Bug Prediction)
            module.identifySourcecodeTarget(repositoryMethod, revisionMethodTarget);
            module.calcFanOut();
            module.calcParameters();
            module.calcLocalVar();
            module.calcCommentRatio();
            module.calcCountPath();
            module.calcComplexity();
            module.calcExecStmt();
            module.calcMaxNesting();
            //codeMetrics(Bug Prediction Based on Fine-Grained Module Histories)
            //module.calcLOC();
        }
        try {
            String pathRepositoryFile = repositoryFile.getDirectory().getParentFile().getAbsolutePath();
            System.out.println("calculating FanIn...");
            final String[] sourcePathDirs = {};
            final String[] libraries = findFiles(pathRepositoryFile, ".jar").toArray(new String[0]);
            final String[] sources = findFiles(pathRepositoryFile, ".java").toArray(new String[0]);

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
            //int countCalledMethod = 0;
            for (String idMethodCalled : ProgressBar.wrap(requesterFanIn.methodsCalled, "processMethodCalled")) {
                if (idMethodCalled == null) continue;
                //countCalledMethod++;
                boolean flag = false;
                for (String pathMethod : modules.keySet()) {
                    String idMethod = modules.get(pathMethod).id;
                    if (Objects.equals(idMethod, idMethodCalled)) {
                        modules.get(pathMethod).sourcecode.fanIn++;
                        //flag=true;
                        break;
                    }
                }
                //if(!flag)System.out.println(idMethodCalled);
            }
            System.out.println("FanIn caluculated");
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }
    public void calculateProcessMetrics(Commits commitsAll, Modules modulesAll, Bugs bugsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "calcProcessMetrics")) {
            long startTimeOverall = System.currentTimeMillis();
            Module module = modules.get(pathModule);
            //process metrics(Re-evaluating Method-Level Bug Prediction)
            module.identifyCommitGraphTarget(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
            if(module.commitGraph==null)continue;
            module.calcModuleHistories();
            module.calcAuthors();
            module.calcSumStmtAdded();
            module.calcMaxStmtAdded();
            module.calcAvgStmtAdded();
            module.calcSumStmtDeleted();
            module.calcMaxStmtDeleted();
            module.calcAvgStmtDeleted();
            module.calcSumChurn();
            module.calcMaxChurn();
            module.calcAvgChurn();
            module.calcSumDecl();
            module.calcSumCond();
            module.calcSumElseAdded();
            module.calcSumElseDeleted();
            /*
            //processMetrics(Bug Prediction Based on Fine-Grained Module Histories)
            module.calcAddLOC();
            module.calcDelLOC();
            module.calcDevMinor();
            module.calcDevMajor();
            module.calcOwnership();
            //long startTimeFixChgNum = System.currentTimeMillis();
            module.calcFixChgNum(commitsAll, bugsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
            //long endTimeFixChgNum = System.currentTimeMillis();
            //System.out.println("calcFixChgNum：" + (endTimeFixChgNum - startTimeFixChgNum)/(1000) + " s");

            //long startTimePastBugNum = System.currentTimeMillis();
            module.calcPastBugNum(commitsAll, bugsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
            //long endTimePastBugNum = System.currentTimeMillis();
            //System.out.println("pastBugNum：" + (endTimePastBugNum - startTimePastBugNum)/(1000) + " s");

            //long startTimeBugIntroNum = System.currentTimeMillis();
            module.calcBugIntroNum();
            //long endTimeBugIntroNum = System.currentTimeMillis();
            //System.out.println("bugIntroNum：" + (endTimeBugIntroNum - startTimeBugIntroNum)/(1000) + " s");

            //long startTimeLogCoupNum = System.currentTimeMillis();
            module.calcLogCoupNum();
            //long endTimeLogCoupNum = System.currentTimeMillis();
            //System.out.println("pastLogCoupNum：" + (endTimeLogCoupNum - startTimeLogCoupNum)/(1000) + " s");

            module.calcPeriod(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
            module.calcAvgInterval(commitsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
            module.calcMaxInterval();
            module.calcMinInterval();

            long endTimeOverall = System.currentTimeMillis();
            if(60*10<(endTimeOverall - startTimeOverall)/(1000)){
                System.out.println(pathModule);
                System.out.println("処理時間：" + (endTimeOverall - startTimeOverall)/(1000) + " s");
            }
             */
        }
    }
    public void calculateIsBuggy(Commits commitsAll, String revisionMethodTarget, String[] intervalRevisionMethod_referableCalculatingIsBuggy, Bugs bugsAll) {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "calculateIsBuggy")) {
            Module module = modules.get(pathModule);
            module.calcIsBuggy(commitsAll, bugsAll, revisionMethodTarget, intervalRevisionMethod_referableCalculatingIsBuggy);
        }
    }
    public void calculateHasBeenBuggy(Commits commitsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics, Bugs bugsAll) {
        for (String pathModule : ProgressBar.wrap(modules.keySet(), "calculateHasBeenBuggy")) {
            Module module = modules.get(pathModule);
            module.calcHasBeenBuggy(commitsAll, bugsAll, intervalRevisionMethod_referableCalculatingProcessMetrics);
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
        for(Module module: modules.values()){
            fileWriter.write(module.outputRow());
        }
        fileWriter.close();
    }
    public void saveAsCSV_(String pathOutput) {
        File dir = new File(pathOutput);
        File dirParent = new File(dir.getParent());
        dirParent.mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(pathOutput);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(osw);
            CsvConfiguration config = new CsvConfiguration();
            config.setFieldDelimiter(',');
            config.getSimpleTypeConverterProvider().registerConverterType(double.class, DoubleConverter.class);
            Serializer serializer = CsvIOFactory.createFactory(config, Module.class).createSerializer();

            serializer.open(writer);
            for (String key : modules.keySet()) {
                Module module = modules.get(key);
                serializer.write(module);
            }
            serializer.close(true);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadModulesFromFile(String pathModules) {
        List<String> paths = findFiles(pathModules, ".json");
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
