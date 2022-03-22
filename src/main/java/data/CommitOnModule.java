package data;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import com.fasterxml.jackson.annotation.*;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;

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
	public String message;
	public Diffs diffs;
	public String pathNewParent;
	@JsonIgnore
	public CommitsOnModule parents;
	public List<String> idsCommitParent;
	@JsonIgnore
	public CommitsOnModule children;
	public List<String> idsCommitChild;
	//バグ関係のデータ
	//todo ここ、StringじゃなくてBugオブジェクトをもたせればいいのでは
	public List<String> IdsCommitsInducingBugsThatThisCommitFixes = new ArrayList<>();
	public List<String> IdsCommitsFixingBugThatThisCommitInduces = new ArrayList<>();
	public List<String> IdsBugThatThisCommitFixing = new ArrayList<>();
	public int numOfChangesStatement = 0;
	public void calcNumOfChangesStatement() {
		calcNumOfAdditionsStatement();
		calcNumOfDeletionsStatement();
		this.numOfChangesStatement=numOfAdditionsStatement+numOfDeletionsStatement;
	}
	//単体メトリクス
	int numOfAdditionsLine = 0;
	public void calcNumOfAdditionsLine(){
		this.numOfAdditionsLine = diffs.calcNOAddedLines();
	}
	int numOfDeletionsLine = 0;
	public void calcNumOfDeletionsLine(){
		this.numOfDeletionsLine = diffs.calcNODeletedLines();
	}
	int numOfChangesLine = 0;
	public int calcNumOfChangesLine(){
		calcNumOfAdditionsLine();
		calcNumOfDeletionsLine();
		numOfChangesLine = numOfAdditionsLine+numOfDeletionsLine;
		return numOfChangesLine;
	}
	int numOfChurnLine = 0;
	public void calcNumOfChurnLine(){
		calcNumOfAdditionsLine();
		calcNumOfDeletionsLine();
		this.numOfChurnLine = this.numOfAdditionsLine - this.numOfDeletionsLine;
	}
	int numOfAdditionsStatement = 0;
	public void calcNumOfAdditionsStatement(){
		int stmtAddedTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			if (change.getChangeType() == ChangeType.STATEMENT_INSERT) stmtAddedTemp++;
		}
		this.numOfAdditionsStatement = stmtAddedTemp;
	}
	int numOfDeletionsStatement = 0;
	public void calcNumOfDeletionsStatement() {
		int stmtDeletedTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			if (change.getChangeType() == ChangeType.STATEMENT_DELETE) stmtDeletedTemp++;
		}
		this.numOfDeletionsStatement = stmtDeletedTemp;
	}
	int numOfChurnsStatement = 0;
	public void calcNumOfChurnsStatement() {
		calcNumOfAdditionsStatement();
		calcNumOfDeletionsStatement();
		this.numOfChurnsStatement = this.numOfAdditionsStatement -this.numOfDeletionsStatement;
	}
	int numOfChangesDeclarationItself = 0;
	public void calcNumOfChangesDeclarationItself() {
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
		this.numOfChangesDeclarationItself = declTemp;
	}
	int numOfChangesCondition = 0;
	public void calcNumOfChangesCondition() {
		int condTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			EntityType et = change.getChangedEntity().getType();
			if (change.getChangeType() == ChangeType.CONDITION_EXPRESSION_CHANGE) condTemp++;
		}
		this.numOfChangesCondition = condTemp;
	}
	int numOfAdditionsStatementElse = 0;
	public void calcNumOfAdditionsStatementElse() {
		int elseAddedTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			EntityType et = change.getChangedEntity().getType();
			if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_INSERT & et.toString().equals("ELSE_STATEMENT"))
				elseAddedTemp++;
		}
		this.numOfAdditionsStatementElse = elseAddedTemp;
	}
	int numOfDeletionsStatementElse = 0;
	public void calcNumOfDeletionsStatementElse() {
		int elseDeletedTemp = 0;
		List<SourceCodeChange> changes = identifySourceCodeChange(this);
		for (SourceCodeChange change : changes) {
			EntityType et = change.getChangedEntity().getType();
			if (change.getChangeType() == ChangeType.ALTERNATIVE_PART_DELETE & et.toString().equals("ELSE_STATEMENT"))
				elseDeletedTemp++;
		}
		this.numOfDeletionsStatementElse = elseDeletedTemp;
	}
	boolean isFix = false;
	public void calcIsFix(){
		this.isFix = (0<this.IdsCommitsInducingBugsThatThisCommitFixes.size());
	}
	boolean isInduce = false;
	boolean isRefactoring = false;
	public void calcIsInduce(List<String> idsCommitReferenced){
		for(String idCommit:this.IdsCommitsFixingBugThatThisCommitInduces){
			if (idsCommitReferenced.contains(idCommit)) {
				isInduce = true;
				break;
			}
		}
	}
	int numOfModulesHasBeenFixedOnTheCommit = 0;
	public void calcNumOfModulesHasBeenFixedOnTheCommit(Commits commitsAll, Modules modulesAll){
		int numOfModulesHasBeenBuggyOnTheCommitTemp=0;
		Commit commit = commitsAll.get(this.idCommit);
		CommitsOnModule commitsOnModuleInTheCommit = commit.idParent2Modifications.get(commit.idParentMaster);
		for(CommitOnModule commitOnModuleInTheCommit: commitsOnModuleInTheCommit.values()){
			String path = Objects.equals(commitOnModuleInTheCommit.type, "DELETE") ? commitOnModuleInTheCommit.pathOld : commitOnModuleInTheCommit.pathNew;
			if(modulesAll.get(path).commitsOnModuleInInterval.calcHasBeenFixed()==1){
				numOfModulesHasBeenBuggyOnTheCommitTemp++;
			}
		}
		this.numOfModulesHasBeenFixedOnTheCommit = numOfModulesHasBeenBuggyOnTheCommitTemp;
	}
	int numOfModulesGetBuggyOnTheCommit = 0;
	public void calcNumOfModulesGetBuggyOnTheCommit(Commits commitsAll){
		int numOfModulesGetBuggyOnTheCommitTemp = 0;
		Commit commit = commitsAll.get(this.idCommit);
		CommitsOnModule commitsOnModuleInTheCommit = commit.idParent2Modifications.get(commit.idParentMaster);
		for(CommitOnModule commitOnModuleInTheCommit: commitsOnModuleInTheCommit.values()){
			if(0<commitOnModuleInTheCommit.IdsCommitsFixingBugThatThisCommitInduces.size()){
				numOfModulesGetBuggyOnTheCommitTemp++;
			}
		}
		this.numOfModulesGetBuggyOnTheCommit = numOfModulesGetBuggyOnTheCommitTemp;
	}
	int[] vectorSemanticType = null;
	public void calcVectorSemanticType() {
		vectorSemanticType = new int[400];
		try {
			JdtTreeGenerator jdtTreeGenerator = new JdtTreeGenerator();
			String sourcePrev = "public class Test{" + this.sourceOld.rawdata + "}";
			String sourceCurrent = "public class Test{" + this.sourceNew.rawdata + "}";
			ITree iTreePrev = jdtTreeGenerator.generateFrom().string(sourcePrev).getRoot();
			ITree iTreeCurrent = jdtTreeGenerator.generateFrom().string(sourceCurrent).getRoot();
			com.github.gumtreediff.matchers.Matcher defaultMatcher = Matchers.getInstance().getMatcher();
			MappingStore mappings = defaultMatcher.match(iTreePrev, iTreeCurrent);
			EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator();
			EditScript actions = editScriptGenerator.computeActions(mappings);
			for (Action action : actions) {
				int index = 0;
				switch (action.getNode().getType().name) {
					case "AnonymousClassDeclaration"        : index = 0;  break;
					case "ArrayAccess"                      : index = 1;  break;
					case "ArrayCreation"                    : index = 2;  break;
					case "ArrayInitializer"                 : index = 3;  break;
					case "ArrayType"                        : index = 4;  break;
					case "AssertStatement"                  : index = 5;  break;
					case "Assignment"                       : index = 6;  break;
					case "Block"                            : index = 7;  break;
					case "BooleanLiteral"                   : index = 8;  break;
					case "BreakStatement"                   : index = 9;  break;
					case "CastExpression"                   : index = 10; break;
					case "CatchClause"                      : index = 11; break;
					case "CharacterLiteral"                 : index = 12; break;
					case "ClassInstanceCreation"            : index = 13; break;
					case "CompilationUnit"                  : index = 14; break;
					case "ConditionalExpression"            : index = 15; break;
					case "ConstructorInvocation"            : index = 16; break;
					case "ContinueStatement"                : index = 17; break;
					case "DoStatement"                      : index = 18; break;
					case "EmptyStatement"                   : index = 19; break;
					case "ExpressionStatement"              : index = 20; break;
					case "FieldAccess"                      : index = 21; break;
					case "FieldDeclaration"                 : index = 22; break;
					case "ForStatement"                     : index = 23; break;
					case "IfStatement"                      : index = 24; break;
					case "ImportDeclaration"                : index = 25; break;
					case "InfixExpression"                  : index = 26; break;
					case "Initializer"                      : index = 27; break;
					case "Javadoc"                          : index = 28; break;
					case "LabeledStatement"                 : index = 29; break;
					case "MethodDeclaration"                : index = 30; break;
					case "MethodInvocation"                 : index = 31; break;
					case "NullLiteral"                      : index = 32; break;
					case "NumberLiteral"                    : index = 33; break;
					case "PackageDeclaration"               : index = 34; break;
					case "ParenthesizedExpression"          : index = 35; break;
					case "PostfixExpression"                : index = 36; break;
					case "PrefixExpression"                 : index = 37; break;
					case "PrimitiveType"                    : index = 38; break;
					case "QualifiedName"                    : index = 39; break;
					case "ReturnStatement"                  : index = 40; break;
					case "SimpleName"                       : index = 41; break;
					case "SimpleType"                       : index = 42; break;
					case "SingleVariableDeclaration"        : index = 43; break;
					case "StringLiteral"                    : index = 44; break;
					case "SuperConstructorInvocation"       : index = 45; break;
					case "SuperFieldAccess"                 : index = 46; break;
					case "SuperMethodInvocation"            : index = 47; break;
					case "SwitchCase"                       : index = 48; break;
					case "SwitchStatement"                  : index = 49; break;
					case "SynchronizedStatement"            : index = 50; break;
					case "ThisExpression"                   : index = 51; break;
					case "ThrowStatement"                   : index = 52; break;
					case "TryStatement"                     : index = 53; break;
					case "TypeDeclaration"                  : index = 54; break;
					case "TypeDeclarationStatement"         : index = 55; break;
					case "TypeLiteral"                      : index = 56; break;
					case "VariableDeclarationExpression"    : index = 57; break;
					case "VariableDeclarationFragment"      : index = 58; break;
					case "VariableDeclarationStatement"     : index = 59; break;
					case "WhileStatement"                   : index = 60; break;
					case "InstanceofExpression"             : index = 61; break;
					case "LineComment"                      : index = 62; break;
					case "BlockComment"                     : index = 63; break;
					case "TagElement"                       : index = 64; break;
					case "TextElement"                      : index = 65; break;
					case "MemberRef"                        : index = 66; break;
					case "MethodRef"                        : index = 67; break;
					case "MethodRefParameter"               : index = 68; break;
					case "EnhancedForStatement"             : index = 69; break;
					case "EnumDeclaration"                  : index = 70; break;
					case "EnumConstantDeclaration"          : index = 71; break;
					case "TypeParameter"                    : index = 72; break;
					case "ParameterizedType"                : index = 73; break;
					case "QualifiedType"                    : index = 74; break;
					case "WildcardType"                     : index = 75; break;
					case "NormalAnnotation"                 : index = 76; break;
					case "MarkerAnnotation"                 : index = 77; break;
					case "SingleMemberAnnotation"           : index = 78; break;
					case "MemberValuePair"                  : index = 79; break;
					case "AnnotationTypeDeclaration"        : index = 80; break;
					case "AnnotationTypeMemberDeclaration"  : index = 81; break;
					case "Modifier"                         : index = 82; break;
					case "UnionType"                        : index = 83; break;
					case "Dimension"                        : index = 84; break;
					case "LambdaExpression"                 : index = 85; break;
					case "IntersectionType"                 : index = 86; break;
					case "NameQualifiedType"                : index = 87; break;
					case "CreationReference"                : index = 88; break;
					case "ExpressionMethodReference"        : index = 89; break;
					case "SuperMethhodReference"            : index = 90; break;
					case "TypeMethodReference"              : index = 91; break;
					case "INFIX_EXPRESSION_OPERATOR"        : index = 92; break;
					case "METHOD_INVOCATION_RECEIVER"       : index = 93; break;
					case "METHOD_INVOCATION_ARGUMENTS"      : index = 94; break;
					case "TYPE_DECLARATION_KIND"            : index = 95; break;
					case "ASSIGNEMENT_OPERATOR"             : index = 96; break;
					case "PREFIX_EXPRESSION_OPERATOR"       : index = 97; break;
					case "POSTFIX_EXPRESSION_OPERATOR"      : index = 98; break;
					default                                 : index = 99; break;
				}
				if (action.getName().contains("insert")) {
					index += 100 * 0;
				} else if (action.getName().contains("update")) {
					index += 100 * 1;
				} else if (action.getName().contains("move")) {
					index += 100 * 2;
				} else if (action.getName().contains("delete")) {
					index += 100 * 3;
				}
				vectorSemanticType[index]++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	int[] vectorAuthor = null;
	public void calcVectorAuthor(Committers authors){
		vectorAuthor =new int[authors.size()];
		vectorAuthor[authors.getIdOfAuthor(this.author)]++;
	}
	int[] vectorInterval = null;
	public void calcVectorInterval() {
		vectorInterval = new int[1];
		int minOfInterval = Integer.MAX_VALUE;
		if(0<this.parents.values().size()) {
			for (CommitOnModule commitOnModuleParent : this.parents.values()) {
				int intervalTemp = (this.date - commitOnModuleParent.date) / (60 * 60 * 24);
				if (intervalTemp < minOfInterval) {
					minOfInterval = intervalTemp;
				}
			}
		}else{
			minOfInterval = 0;
		}
		vectorInterval[0]=minOfInterval;
	}
	int[] vectorType = null;
	public void calcVectorType(){
		vectorType = new int[]{0,0,0,0};
		Set<String> content = Arrays.stream(message.split("\\s")).collect(Collectors.toSet());
		int type = 0;
		calcIsFix();
		if(isFix) type = 1;
		if(
				content.contains("fix") ||
				content.contains("error") ||
				content.contains("fail") ||
				content.contains("failur")
		) type = 1;
		if(type == 0) {
			for (String word : content) {
				if (word.contains("bug") && !word.contains("debug")) {
					type = 1;
					break;
				}
			}
		}
		if(type == 0) {
			if(content.contains("remov")) type = 3;
			if(type == 0) {
				for (String word : content) {
					if (word.contains("format") && !word.contains("informat")) {
						type = 3;
						break;
					}
					if (word.contains("refactor") || word.contains("enhanc")) {
						type = 3;
						break;
					}
				}
			}
		}
		if(type == 0 && (content.contains("enhanc") || content.contains("featur") || content.contains("updat") || content.contains("add") || content.contains("ad"))) type = 2;
		if(type == 0) {
			for (String word : content) {
				if (word.contains("debug") || word.contains("test")) {
					type = 4;
					break;
				}
			}
		}
		if(type==0) type=2;
		if(type==1)vectorType[0]=1;
		else if(type==2)vectorType[1]=1;
		else if(type==3)vectorType[2]=1;
		else if(type==4)vectorType[3]=1;
	}
	int[] vectorCodeChurn=null;
	public void calcVectorCodeChurn() {
		vectorCodeChurn = new int[3];
		calcNumOfAdditionsLine();
		vectorCodeChurn[0] = this.numOfAdditionsLine;
		calcNumOfDeletionsLine();
		vectorCodeChurn[1] = this.numOfDeletionsLine;
		calcNumOfChurnLine();
		vectorCodeChurn[2] = this.numOfChurnLine;
	}
	int[] vectorCochange = null;
	public void calcVectorCoChange(Commits commitsAll, Modules modulesAll) {
		vectorCochange = new int[modulesAll.size()];
		for (CommitOnModule changeOnModuleCoCommit : commitsAll.get(this.idCommit).idParent2Modifications.get(this.idCommitParent).values()) {
			if (Objects.equals(changeOnModuleCoCommit.type, "RENAME") | Objects.equals(changeOnModuleCoCommit.type, "COPY")){
				vectorCochange[modulesAll.getIdModule(changeOnModuleCoCommit.pathOld)]++;
				vectorCochange[modulesAll.getIdModule(changeOnModuleCoCommit.pathNew)]++;
			}else if (Objects.equals(changeOnModuleCoCommit.type, "DELETE")) {
				vectorCochange[modulesAll.getIdModule(changeOnModuleCoCommit.pathOld)]++;
			}else {
				vectorCochange[modulesAll.getIdModule(changeOnModuleCoCommit.pathNew)]++;
			}
		}
	}

	public void calcMetrics(){
		calcNumOfAdditionsStatement();
		calcNumOfDeletionsStatement();
		calcNumOfChurnsStatement();
		calcNumOfChangesDeclarationItself();
		calcNumOfChangesCondition();
		calcNumOfAdditionsStatementElse();
		calcNumOfDeletionsStatementElse();
	}
	public void calcVectors(Commits commitsAll, Modules modulesAll, Committers authors) {
		calcVectorSemanticType();
		calcVectorAuthor(authors);
		calcVectorCoChange(commitsAll, modulesAll);
		calcVectorInterval();
		calcVectorCodeChurn();
		calcVectorType();
	}

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
	public void delete(){
		this.vectorType = null;
		this.vectorCochange = null;
		this.vectorCodeChurn = null;
		this.vectorInterval = null;
		this.vectorAuthor = null;
		this.vectorSemanticType = null;
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
			//e.printStackTrace();
			//System.out.println("distiller error");
		}
		return distiller.getSourceCodeChanges();
	}
}
