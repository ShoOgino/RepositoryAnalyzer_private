package data;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.apache.commons.collections4.keyvalue.MultiKey;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CommitOnModule {
	public String idCommit;
	public String idCommitParent;
	public int date;
	public String author = null;
	public boolean isMerge;
	public String type;
	public String pathNew;
	public String pathOld;
	public Sourcecode sourceNew;
	public Sourcecode sourceOld;
	public Diffs diffs;
	public String pathNewParent;
	@JsonIgnore public CommitsOnModule parents;
	@JsonIgnore public Set<CommitOnModule> parentsFlatten = new HashSet<>();
	public List<String> idsCommitParent;
	@JsonIgnore public CommitsOnModule children;
	@JsonIgnore public List<CommitOnModule> childrenFlatten;
	public List<String> idsCommitChild;
	//単体メトリクス
	int stmtAdded =0;
	int stmtDeleted =0;
	int churn =0;
	int decl =0;
	int cond = 0;
	int elseAdded = 0;
	int elseDeleted = 0;
	int addLOC = 0;
	int delLOC = 0;
	boolean isFixChg = false;
	boolean isPastBug = false;
	boolean isBugIntro = false;
	boolean isLogCoup = false;
	boolean isFixingBug = false;
	//全体メトリクス
	//processMetrics(Re-evaluating Method-Level Bug Prediction)
	int moduleHistories = 0;
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
	int fixChgNum = 0;
	int pastBugNum = 0;
	int bugIntroNum = 0;
	int logCoupNum = 0;
	int period = 0;
	double avgInterval = 0;
	int maxInterval = 0;
	int minInterval = 0;

	public CommitOnModule() {
		this.idCommit= "";
		this.idCommitParent = "";
		this.date=0;
		this.author="";
		this.isMerge=false;
		this.pathOld= "";
		this.pathNew= "";
		this.sourceOld= null;
		this.sourceNew= null;
		this.diffs = new Diffs();
		this.pathNewParent = "";
		this.parents = new CommitsOnModule();
		this.idsCommitParent = new ArrayList<>();
		this.children = new CommitsOnModule();
		this.idsCommitChild = new ArrayList<>();
	}
	public void loadAncestors(CommitsOnModule commitsOnModule){
		commitsOnModule.put(this.idCommitParent, this.idCommit, this.pathOld, this.pathNew, this);
		for(CommitOnModule commitOnModule : this.parents.values()) {
			if(!commitsOnModule.containsValue(commitOnModule)) commitOnModule.loadAncestors(commitsOnModule);
		}
	}
	public int calcNOAddedLines(){
		return diffs.calcNOAddedLines();
	}
	public int calcNODeletedLines(){
		return diffs.calcNODeletedLines();
	}
	public void prune(int dateBegin, int dateEnd) {
		Iterator<Map.Entry<MultiKey<? extends String>, CommitOnModule>> it = this.parents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<MultiKey<? extends String>, CommitOnModule> entry = it.next();
			if(dateBegin<=entry.getValue().date & entry.getValue().date<=dateEnd){
				entry.getValue().prune(dateBegin, dateEnd);
			} else {
				it.remove();
			}
		}
	}
	public List<SourceCodeChange> identifySourceCodeChange(CommitOnModule commitOnModule) {
		String sourcePrev = null;
		String sourceCurrent = null;
		String strPre = null;
		String strPost = null;
		if (commitOnModule.sourceOld==null || commitOnModule.sourceOld.rawdata.equals("")) {
			commitOnModule.sourceOld = new Sourcecode("");
			String regex = "\\n|\\r\\n";
			String tmp = commitOnModule.sourceNew.rawdata;
			String[] lines = tmp.split(regex, 0);

			boolean inComment = false;
			int count = 0;
			for (String line : lines) {
				if (line.matches(".*/\\*.*")) {
					inComment = true;
					count++;
				} else if (line.matches(".*\\*/.*")) {
					inComment = false;
					count++;
				} else if (inComment) {
					count++;
				} else if (line.matches(".*//.*")) {
					count++;
				} else {
					break;
				}
			}
			tmp = "";
			for (int i = count; i < lines.length; i++) {
				tmp = tmp + lines[i] + "\n";
			}

			Pattern patternPre = Pattern.compile("[\\s\\S.]*?(?=\\{)");
			Matcher matcherPre = patternPre.matcher(tmp);
			if (matcherPre.find()) {
				strPre = matcherPre.group();
			}
			Pattern patternPost = Pattern.compile("(?<=\\{)[\\s\\S.]*");
			Matcher matcherPost = patternPost.matcher(tmp);
			if (matcherPost.find()) {
				strPost = matcherPost.group();
			}
			sourcePrev = "public class Test{" + strPre +
					"{" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"}" +
					"}";
			sourceCurrent = "public class Test{" + strPre +
					"{" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					"dummy();\n" +
					strPost +
					"}";
		} else {
			sourcePrev = "public class Dummy{" + commitOnModule.sourceOld.rawdata + "}";
			sourceCurrent = "public class Dummy{" + commitOnModule.sourceNew.rawdata + "}";
		}

		FileDistiller distiller = ChangeDistiller.createFileDistiller(ChangeDistiller.Language.JAVA);
		try {
			distiller.extractClassifiedSourceCodeChanges(sourcePrev, sourceCurrent);
		} catch (Exception e) {
		}
		return distiller.getSourceCodeChanges();
	}
	public void flattenParents() {
		this.parentsFlatten.add(this);
		for(CommitOnModule parent: parents.values()){
			parent.flattenParents();
			this.parentsFlatten.addAll(parent.parentsFlatten);
		}
	}
	//単体メトリクス
	public void calcStmtAdded(){
		int stmtAddedTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			if (change.getChangeType() == ChangeType.STATEMENT_INSERT) stmtAddedTemp++;
		}
		this.stmtAdded = stmtAddedTemp;
	}
	public void calcStmtDeleted() {
		int stmtDeletedTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			if (change.getChangeType() == ChangeType.STATEMENT_DELETE) stmtDeletedTemp++;
		}
		this.stmtDeleted = stmtDeletedTemp;
	}
	public void calcChurn() {
		int churnTemp = 0;
			List<SourceCodeChange> changes = identifySourceCodeChange(this);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_INSERT) churnTemp++;
				else if (change.getChangeType() == ChangeType.STATEMENT_DELETE) churnTemp--;
			}
		this.churn = churnTemp;
	}
	public void calcElseAdded() {
		int elseAddedTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			EntityType et = change.getChangedEntity().getType();
			if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_INSERT & et.toString().equals("ELSE_STATEMENT"))
				elseAddedTemp++;
		}
		this.elseAdded = elseAddedTemp;
	}
	public void calcElseDeleted() {
		int elseDeletedTemp = 0;
		for (CommitOnModule CommitOnModule : parentsFlatten) {
			List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
			for (SourceCodeChange change : changes) {
				EntityType et = change.getChangedEntity().getType();
				if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_DELETE & et.toString().equals("ELSE_STATEMENT"))
					elseDeletedTemp++;
			}
		}
		this.elseDeleted = elseDeletedTemp;
	}
	public void calcDecl() {
		int declTemp = 0;
		List<ChangeType> ctdecl = Arrays.asList(
				ChangeType.METHOD_RENAMING,
				ChangeType.PARAMETER_DELETE,
				ChangeType.PARAMETER_INSERT,
				ChangeType.PARAMETER_ORDERING_CHANGE,
				ChangeType.PARAMETER_RENAMING,
				ChangeType.PARAMETER_TYPE_CHANGE,
				ChangeType.RETURN_TYPE_INSERT,
				ChangeType.RETURN_TYPE_DELETE,
				ChangeType.RETURN_TYPE_CHANGE,
				ChangeType.PARAMETER_TYPE_CHANGE
		);
		for (CommitOnModule CommitOnModule : parentsFlatten) {
			List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
			for (SourceCodeChange change : changes) {
				EntityType et = change.getChangedEntity().getType();
				if (ctdecl.contains(change.getChangeType())) declTemp++;
			}
		}
		this.decl = declTemp;
	}
	public void calcCond() {
		int condTemp = 0;
		for (CommitOnModule CommitOnModule : parentsFlatten) {
			List<SourceCodeChange> changes = identifySourceCodeChange(CommitOnModule);
			for (SourceCodeChange change : changes) {
				EntityType et = change.getChangedEntity().getType();
				if (change.getChangeType() == ChangeType.CONDITION_EXPRESSION_CHANGE) condTemp++;
			}
		}
		this.cond = condTemp;
	}
	//全体メトリクス
	public void calcModuleHistories() {
		int moduleHistoriesTemp = 0;
		for(CommitOnModule commitOnModule: parentsFlatten){
			if(!commitOnModule.isMerge) moduleHistoriesTemp++;
		}
		this.moduleHistories = moduleHistoriesTemp;
	}
	public void calcAuthors() {
		Set<String> setAuthors = new HashSet<>();
		for(CommitOnModule commitOnModule: parentsFlatten){
			if(commitOnModule.isMerge)continue;
			setAuthors.add(commitOnModule.author);
		}
		this.authors = setAuthors.size();
	}
	public void calcSumStmtAdded() {
		int sumStmtAddedTemp = 0;
		for (CommitOnModule commitOnModule: parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_INSERT) sumStmtAddedTemp++;
			}
		}
		this.sumStmtAdded = sumStmtAddedTemp;
	}
	public void calcMaxStmtAdded() {
		int maxStmtAddedTemp = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge) continue;
			int stmtAddedTemp = 0;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_INSERT) stmtAddedTemp++;
			}
			if (maxStmtAddedTemp < stmtAddedTemp) {
				maxStmtAddedTemp = stmtAddedTemp;
			}
		}
		this.maxStmtAdded = maxStmtAddedTemp;
	}
	public void calcAvgStmtAdded() {
		int avgStmtAdded = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_INSERT) avgStmtAdded++;
			}
		}
		calcModuleHistories();
		if (moduleHistories == 0) this.avgStmtAdded = 0;
		else this.avgStmtAdded = avgStmtAdded / (double) moduleHistories;
	}
	public void calcSumStmtDeleted() {
		int sumStmtDeletedTemp = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_DELETE) sumStmtDeletedTemp++;
			}
		}
		this.sumStmtDeleted = sumStmtDeletedTemp;
	}
	public void calcMaxStmtDeleted() {
		int maxStmtDeleted = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			int stmtDeletedOnCommit = 0;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_DELETE) stmtDeletedOnCommit++;
			}
			if (maxStmtDeleted < stmtDeletedOnCommit) {
				maxStmtDeleted = stmtDeletedOnCommit;
			}
		}
		this.maxStmtDeleted = maxStmtDeleted;
	}
	public void calcAvgStmtDeleted() {
		int avgStmtDeleted = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_DELETE) avgStmtDeleted++;
			}
		}
		calcModuleHistories();
		if (moduleHistories == 0) this.avgStmtDeleted = 0;
		else this.avgStmtDeleted = avgStmtDeleted / (double) moduleHistories;
	}
	public void calcSumChurn() {
		int sumChurn = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_INSERT) sumChurn++;
				else if (change.getChangeType() == ChangeType.STATEMENT_DELETE) sumChurn--;
			}
		}
		this.sumChurn = sumChurn;
	}
	public void calcMaxChurn() {
		int maxChurn = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			int churnTemp = 0;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				if (change.getChangeType() == ChangeType.STATEMENT_INSERT) churnTemp++;
				else if (change.getChangeType() == ChangeType.STATEMENT_DELETE) churnTemp--;
			}
			if (maxChurn < churnTemp) maxChurn = churnTemp;
		}
		this.maxChurn = maxChurn;
	}
	public void calcAvgChurn() {
		calcSumChurn();
		calcModuleHistories();
		if (this.moduleHistories == 0) this.avgChurn = 0;
		else this.avgChurn = sumChurn / (float) moduleHistories;
	}
	public void calcSumElseAdded() {
		int sumElseAdded = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				EntityType et = change.getChangedEntity().getType();
				if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_INSERT & et.toString().equals("ELSE_STATEMENT"))
					sumElseAdded++;
			}
		}
		this.sumElseAdded = sumElseAdded;
	}
	public void calcSumElseDeleted() {
		int sumElseDeleted = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				EntityType et = change.getChangedEntity().getType();
				if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_DELETE & et.toString().equals("ELSE_STATEMENT"))
					sumElseDeleted++;
			}
		}
		this.sumElseDeleted = sumElseDeleted;
	}
	public void calcSumDecl() {
		int sumDecl = 0;
		List<ChangeType> ctdecl = Arrays.asList(
				ChangeType.METHOD_RENAMING,
				ChangeType.PARAMETER_DELETE,
				ChangeType.PARAMETER_INSERT,
				ChangeType.PARAMETER_ORDERING_CHANGE,
				ChangeType.PARAMETER_RENAMING,
				ChangeType.PARAMETER_TYPE_CHANGE,
				ChangeType.RETURN_TYPE_INSERT,
				ChangeType.RETURN_TYPE_DELETE,
				ChangeType.RETURN_TYPE_CHANGE,
				ChangeType.PARAMETER_TYPE_CHANGE
		);
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				EntityType et = change.getChangedEntity().getType();
				if (ctdecl.contains(change.getChangeType())) sumDecl++;
			}
		}
		this.sumDecl = sumDecl;
	}
	public void calcSumCond() {
		int sumCond = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
			if(commitOnModule.isMerge)continue;
			List<SourceCodeChange> changes = identifySourceCodeChange(commitOnModule);
			for (SourceCodeChange change : changes) {
				EntityType et = change.getChangedEntity().getType();
				if (change.getChangeType() == ChangeType.CONDITION_EXPRESSION_CHANGE) sumCond++;
			}
		}
		this.sumCond = sumCond;
	}
	public void calcAddLOC() {
		int addLOC = 0;
		for (CommitOnModule commitOnModule : parentsFlatten) {
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
		/*todo
		Set<String> pathsPast = parentsFlatten.stream().map(a -> a.pathNew).collect(Collectors.toSet());
		for(CommitOnModule CommitOnModule : parentsFlatten) {
            for(String pathBugIntroduced :.pathsBugIntroduced){
                if(!pathsPast.contains(pathBugIntroduced)){
                    this.bugIntroNum += 1;
                    break;
                }
            }
		}
        */
	}
    public void calcLogCoupNum() {
	/*todo
        Set<String> pathsPast = changesOnModule.values().stream().map(a -> a.pathNew).collect(Collectors.toSet());
        for(ChangeOnModule changeOnModule: changesOnModuleInInterval) {
            for(String pathHasBeenBuggy :.pathsHasBeenBuggy){
                if(!pathsPast.contains(pathHasBeenBuggy)){
                    this.logCoupNum += 1;
                    break;
                }
            }
        }
     */
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
	public void setMetricsOnEachNode() {
		for(CommitOnModule commitOnModule: parentsFlatten){
			commitOnModule.calcStmtAdded();
			commitOnModule.calcStmtDeleted();
			commitOnModule.calcChurn();
			commitOnModule.calcDecl();
			commitOnModule.calcCond();
			commitOnModule.calcElseAdded();
			commitOnModule.calcElseDeleted();
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
}
