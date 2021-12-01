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
public class CommitsOnModule {
    @JsonDeserialize(keyUsing = DeserializerModification.class) private final MultiKeyMap<String, CommitOnModule> CommitsOnModule = new MultiKeyMap<>();
    //processMetrics(Re-evaluating Method-Level Bug Prediction)
    public int numOfCommits = 0;
    int authors = 0;
    int sumStmtAdded = 0;
    int maxStmtAdded = 0;
    double avgStmtAdded = 0;
    int sumStmtDeleted = 0;
    int maxStmtDeleted = 0;
    double avgStmtDeleted = 0;
    int sumChurn = 0;
    int maxChurn = 0;
    double avgChurn = 0;
    int sumDecl = 0;
    int sumCond = 0;
    int sumElseAdded = 0;
    int sumElseDeleted = 0;
    //process metrics(Bug Prediction Based on Fine-Grained Module Histories)
    int sumAddLOC = 0;
    int sumDelLOC = 0;
    int devMinor = 0;
    int devMajor = 0;
    double ownership = 0;
    int period = 0;
    double avgInterval = 0;
    int maxInterval = 0;
    int minInterval = 0;
    int numOfCommitsOtherModulesHasBeenBuggyOnTheCommit=0;
    int numOfCommitsOtherModulesGetBuggyOnTheCommit = 0;

    //全体メトリクス
    public void calcModuleHistories() {
        for(CommitOnModule commitOnModule: CommitsOnModule.values()){
            if(commitOnModule.isMerge)continue;
            this.numOfCommits++;
        }
    }
    public void calcAuthors() {
        Set<String> setAuthors = new HashSet<>();
        for(CommitOnModule commitOnModule: CommitsOnModule.values()){
            if(commitOnModule.isMerge)continue;
            setAuthors.add(commitOnModule.author);
        }
        this.authors = setAuthors.size();
    }
    public void calcSumStmtAdded() {
        int sumStmtAddedTemp = 0;
        for (CommitOnModule commitOnModule: CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcStmtAdded();
            sumStmtAddedTemp+=commitOnModule.stmtAdded;
        }
        this.sumStmtAdded = sumStmtAddedTemp;
    }
    public void calcMaxStmtAdded() {
        int maxStmtAddedTemp = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge) continue;
            commitOnModule.calcStmtAdded();
            if (maxStmtAddedTemp < commitOnModule.stmtAdded) {
                maxStmtAddedTemp = commitOnModule.stmtAdded;
            }
        }
        this.maxStmtAdded = maxStmtAddedTemp;
    }
    public void calcAvgStmtAdded() {
        calcSumStmtAdded();
        calcModuleHistories();
        if (numOfCommits == 0) this.avgStmtAdded = 0;
        else this.avgStmtAdded = this.sumStmtAdded / (double) numOfCommits;
    }
    public void calcSumStmtDeleted() {
        int sumStmtDeletedTemp = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcStmtDeleted();
            sumStmtDeletedTemp+=commitOnModule.stmtDeleted;
        }
        this.sumStmtDeleted = sumStmtDeletedTemp;
    }
    public void calcMaxStmtDeleted() {
        int maxStmtDeleted = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcStmtDeleted();
            if (maxStmtDeleted < commitOnModule.stmtDeleted) {
                maxStmtDeleted = commitOnModule.stmtDeleted;
            }
        }
        this.maxStmtDeleted = maxStmtDeleted;
    }
    public void calcAvgStmtDeleted() {
        calcSumStmtDeleted();
        calcModuleHistories();
        if (numOfCommits == 0) this.avgStmtDeleted = 0;
        else this.avgStmtDeleted = this.sumStmtDeleted / (double) this.numOfCommits;
    }
    public void calcSumChurn() {
        int sumChurn = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcChurn();
            sumChurn+=commitOnModule.churn;
        }
        this.sumChurn = sumChurn;
    }
    public void calcMaxChurn() {
        int maxChurn = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcChurn();
            if (maxChurn < commitOnModule.churn) maxChurn = commitOnModule.churn;
        }
        this.maxChurn = maxChurn;
    }
    public void calcAvgChurn() {
        calcSumChurn();
        calcModuleHistories();
        if (this.numOfCommits == 0) this.avgChurn = 0;
        else this.avgChurn = sumChurn / (float) numOfCommits;
    }
    public void calcSumElseAdded() {
        int sumElseAdded = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcElseAdded();
            sumElseAdded+=commitOnModule.elseAdded;
        }
        this.sumElseAdded = sumElseAdded;
    }
    public void calcSumElseDeleted() {
        int sumElseDeleted = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcElseDeleted();
            sumElseDeleted+=commitOnModule.elseDeleted;
        }
        this.sumElseDeleted = sumElseDeleted;
    }
    public void calcSumDecl() {
        int sumDeclTemp = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcDecl();
            sumDeclTemp+=commitOnModule.decl;
        }
        this.sumDecl = sumDeclTemp;
    }
    public void calcSumCond() {
        int sumCondTemp = 0;
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            if(commitOnModule.isMerge)continue;
            commitOnModule.calcCond();
            sumCondTemp+=commitOnModule.cond;
        }
        this.sumCond = sumCondTemp;
    }
    /*
    public void calcAddLOC() {
        int addLOC = 0;
        for (CommitOnModule commitOnModule : modifications.values()) {
            if(commitOnModule.isMerge)continue;
            addLOC += commitOnModule.calcNOAddedLines();
        }
        this.addLOC = addLOC;
    }
    public void calcDelLOC() {
        int delLOC = 0;
        for (CommitOnModule commitOnModule : parentsFlatten) {
            if(commitOnModule.isMerge)continue;
            delLOC += commitOnModule.calcNODeletedLines();
        }
        this.delLOC = delLOC;
    }
    public void calcDevMinor() {
        Set<String> setAuthors = new HashSet<>();
        parentsFlatten.forEach(item -> setAuthors.add(item.author));

        int devMinor = 0;
        for (String nameAuthor : setAuthors) {
            int count = (int) parentsFlatten.stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            if ( ( count / (float) parentsFlatten.size() ) < 0.2) {
                devMinor++;
            }
        }
        this.devMinor = devMinor;
    }
    public void calcDevMajor() {
        Set<String> setAuthors = new HashSet<>();
        parentsFlatten.forEach(item -> setAuthors.add(item.author));

        int devMajor = 0;
        for (String nameAuthor : setAuthors) {
            int count = (int) parentsFlatten.stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            if (0.2 < count / (float) parentsFlatten.size()) {
                devMajor++;
            }
        }
        this.devMajor = devMajor;
    }
    public void calcOwnership() {
        Set<String> setAuthors = new HashSet<>();
        parentsFlatten.forEach(item -> setAuthors.add(item.author));

        for (String nameAuthor : setAuthors) {
            int count = (int) parentsFlatten.stream().filter(item -> Objects.equals(item.author, nameAuthor)).count();
            double ownership = count / (float) parentsFlatten.size();
            if (this.ownership < ownership) {
                this.ownership = ownership;
            }
        }
    }
    public void calcFixChgNum(Commits commitsAll, Bugs bugsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        Set<String> paths = new HashSet<>();
        for(CommitOnModule CommitOnModule : parentsFlatten){
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
                if (dateBegin <= dateCommitFix & dateCommitFix <= dateEnd) {
                    commitsFixingBugs.add(bugAtomic.idCommitFix);
                }
            }
        }
        this.fixChgNum = commitsFixingBugs.size();
    }
    public void calcPastBugNum(Commits commitsAll, Bugs bugsAll, String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        Set<String> paths = new HashSet<>();
        for(CommitOnModule CommitOnModule : parentsFlatten){
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
    public void calcBugIntroNum() {
		Set<String> pathsPast = parentsFlatten.stream().map(a -> a.pathNew).collect(Collectors.toSet());
		for(CommitOnModule CommitOnModule : parentsFlatten) {
            for(String pathBugIntroduced :.pathsBugIntroduced){
                if(!pathsPast.contains(pathBugIntroduced)){
                    this.bugIntroNum += 1;
                    break;
                }
            }
		}
    }
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
    public void calcPeriod(Commits commitsAll ,String[] intervalRevisionMethod_referableCalculatingProcessMetrics) {
        int periodFrom = Integer.MAX_VALUE;
        int periodTo = commitsAll.get(intervalRevisionMethod_referableCalculatingProcessMetrics[1]).date;
        for (CommitOnModule CommitOnModule : parentsFlatten) {
            if (CommitOnModule.date < periodFrom) {
                periodFrom = CommitOnModule.date;
            }
        }
        this.period = (periodTo - periodFrom) / (60 * 60 * 24);
    }
    public void calcAvgInterval() {
        int sumInterval = 0;
        List<CommitOnModule> commitOnModulesSorted = parentsFlatten.stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
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
        List<CommitOnModule> commitOnModules = parentsFlatten.stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
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
        List<CommitOnModule> commitOnModules = parentsFlatten.stream().sorted(Comparator.comparingInt(a -> a.date)).collect(Collectors.toList());
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
    */
    public void setMetricsOnEachNode() {
        for (CommitOnModule commitOnModule : CommitsOnModule.values()) {
            commitOnModule.calcMetrics();
        }
    }
	/*
	public void calcVectorSemanticType(){
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
	}
	public void calcVectorAuthor(){
		nodeCommit4Experiment.author = commitsAll.get(commitOnModule.idCommit).author;
	}
	public void calcVectorInterval() {
		int interval = 0;
		for (CommitOnModule commitOnModuleParent : commitOnModule.parents.values()) {
			interval += (commitOnModule.date - commitOnModuleParent.date) / (60 * 60 * 24);
		}
		nodeCommit4Experiment.interval = interval;
	}
	public void calcVectorCodeChurn() {
		nodeCommit4Experiment.churn[0] = commitOnModule.calcNOAddedLines();
		nodeCommit4Experiment.churn[1] = commitOnModule.calcNODeletedLines();
		nodeCommit4Experiment.churn[2] = nodeCommit4Experiment.churn[0] - nodeCommit4Experiment.churn[1];
	}
	public void calcVectorCoChange() {
		for (CommitOnModule changeOnModuleCoCommit : commitsAll.get(commitOnModule.idCommit).idParent2Modifications.get(commitOnModule.idCommitParent).values()) {
			//pathOld
			if (!Objects.equals(changeOnModuleCoCommit.pathOld, "/dev/null")) {
				nodeCommit4Experiment.coupling.add(changeOnModuleCoCommit.pathOld);
			}
			//pathNew
			if (Objects.equals(changeOnModuleCoCommit.pathNew, "/dev/null")) {
				nodeCommit4Experiment.coupling.add(changeOnModuleCoCommit.pathNew);
			}
		}
	}
	 */


    public CommitOnModule get(String idCommitParent, String idCommit, String pathOld, String pathNew){
        return CommitsOnModule.get(idCommitParent, idCommit, pathOld, pathNew);
    }
    public List<CommitOnModule> queryIdCommit(String idCommit) {
        return CommitsOnModule.values().stream().filter(a->a.idCommit.equals(idCommit)).collect(Collectors.toList());
    }
    public List<CommitOnModule> queryPathOld(String pathOld) {
        return CommitsOnModule.values().stream().filter(a->a.pathOld.equals(pathOld)).collect(Collectors.toList());
    }
    public List<CommitOnModule> queryPathNew(String pathNew) {
        return CommitsOnModule.values().stream().filter(a->a.pathNew.equals(pathNew)).collect(Collectors.toList());
    }
    public void put(String idCommitParent, String idCommit, String pathOld, String pathNew, CommitOnModule commitOnModule){
        CommitsOnModule.put(idCommitParent, idCommit, pathOld, pathNew, commitOnModule);
    }
    public void excludeCommitsOutOfInterval(int dateBegin, int dateEnd) {
        CommitsOnModule.entrySet().removeIf(entry ->  entry.getValue().date<dateBegin | dateEnd<entry.getValue().date);
    }
    public CommitsOnModule(){}
    public int size() { return CommitsOnModule.size(); }
    public boolean isEmpty() { return CommitsOnModule.isEmpty(); }
    public boolean containsKey(Object key) { return CommitsOnModule.containsKey(key); }
    public boolean containsValue(Object value) { return CommitsOnModule.containsValue(value); }
    public CommitOnModule get(Object key) { return CommitsOnModule.get(key); }
    public CommitOnModule put(MultiKey<? extends String> key, CommitOnModule value) { return CommitsOnModule.put(key, value); }
    public CommitOnModule remove(Object key) { return CommitsOnModule.remove(key); }
    public void putAll(Map<? extends MultiKey<? extends String>, ? extends CommitOnModule> m) { CommitsOnModule.putAll(m); }
    public void clear(){ CommitsOnModule.clear(); }
    public Set<MultiKey<? extends String>> keySet() { return CommitsOnModule.keySet(); }
    public Collection<CommitOnModule> values(){ return CommitsOnModule.values(); }
    public Set<Map.Entry<MultiKey<? extends String>, CommitOnModule>> entrySet() { return CommitsOnModule.entrySet(); }
}
