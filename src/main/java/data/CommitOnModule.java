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
	@JsonIgnore
	public CommitsOnModule parents;
	public List<String> idsCommitParent;
	@JsonIgnore
	public CommitsOnModule children;
	public List<String> idsCommitChild;
	boolean isUsable4CalcDependentVar = false;
	boolean isUsable4CalcIndependentVar = false;
	//単体メトリクス
	int addLOC = 0;
	int delLOC = 0;
	int stmtAdded = 0;
	int stmtDeleted = 0;
	int churn = 0;
	int decl = 0;
	int cond = 0;
	int elseAdded = 0;
	int elseDeleted = 0;
	boolean isFix = false;
	boolean isInduce = false;
	boolean hasBeenBuggy =false;
	int interval=0;//前のコミットからの経過日数
	int numOfModulesHasBeenBuggyOnTheCommit = 0;
	int numOfModulesGetBuggyOnTheCommit = 0;

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
	//単体メトリクス
	public void calcMetrics(){
		calcStmtAdded();
		calcStmtDeleted();
		calcChurn();
		calcDecl();
		calcCond();
		calcElseAdded();
		calcElseDeleted();
	}
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
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			EntityType et = change.getChangedEntity().getType();
			if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_DELETE & et.toString().equals("ELSE_STATEMENT"))
				elseDeletedTemp++;
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
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			EntityType et = change.getChangedEntity().getType();
			if (ctdecl.contains(change.getChangeType())) declTemp++;
		}
		this.decl = declTemp;
	}
	public void calcCond() {
		int condTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			EntityType et = change.getChangedEntity().getType();
			if (change.getChangeType() == ChangeType.CONDITION_EXPRESSION_CHANGE) condTemp++;
		}
		this.cond = condTemp;
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
}
