package data;

import ast.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Sourcecode {
    public String rawdata = null;
    @JsonIgnore public CompilationUnit compilationUnit = null;
    public NodeAST4Experiment astRoot = null;
    public int countASTNode = 0;
    int numOfLines = 0;
    public void calcNumOfLines(){
        this.numOfLines = rawdata.split("\n").length;
    }
    int fanin = 0;
    int fanout = 0;
    public void calcFanOut() {
        VisitorFanout visitor = new VisitorFanout();
        compilationUnit.accept(visitor);
        this.fanout = visitor.fanout;
    }
    int numOfParameters = 0;
    public void calcParameters() {
        data.VisitorMethodDeclaration visitorMethodDeclaration = new data.VisitorMethodDeclaration();
        compilationUnit.accept(visitorMethodDeclaration);
        this.numOfParameters = visitorMethodDeclaration.parameters;
    }
    int numOfVariablesLocal = 0;
    public void calcLocalVar() {
        VisitorLocalVar visitorLocalVar = new VisitorLocalVar();
        compilationUnit.accept(visitorLocalVar);
        this.numOfVariablesLocal = visitorLocalVar.NOVariables;
    }
    double ratioOfLinesComment = 0;
    public void calcCommentRatio() {
        String regex = "\n|\r\n";
        String[] linesMethod = this.rawdata.split(regex, 0);

        int countLineCode = 0;
        int countLineComment = 0;
        boolean inComment = false;
        for (String line : linesMethod) {
            countLineCode++;
            if (line.matches(".*\\*/.*")) {
                inComment = false;
                countLineComment++;
            } else if (inComment) {
                countLineComment++;
            } else if (line.matches(".*/\\*.*")) {
                countLineComment++;
                inComment = true;
            } else if (line.matches(".*//.*")) {
                countLineComment++;
            }
        }
        this.ratioOfLinesComment =  (float) countLineComment / (float) countLineCode;
    }
    long numOfPaths = 0;
    public void calcCountPath() {
        VisitorCountPath visitorCountPath = new VisitorCountPath();
        compilationUnit.accept(visitorCountPath);
        long countPath = 1;
        for (int branch : visitorCountPath.branches) {
            countPath *= branch;
        }
        this.numOfPaths =  countPath;
    }
    int complexity = 0;
    public void calcComplexity() {
        VisitorComplexity visitorComplexity = new VisitorComplexity();
        compilationUnit.accept(visitorComplexity);
        this.complexity = visitorComplexity.complexity;
    }
    int numOfStatements = 0;
    public void calcExecStmt() {
        VisitorExecStmt visitorExecStmt = new VisitorExecStmt();
        compilationUnit.accept(visitorExecStmt);
        this.numOfStatements =  visitorExecStmt.execStmt;
    }
    int maxOfNesting = 0;
    public void calcMaxNesting() {
        VisitorMaxNesting visitorMaxNesting = new VisitorMaxNesting();
        compilationUnit.accept(visitorMaxNesting);
        this.maxOfNesting = visitorMaxNesting.maxNesting;
    }

    public Sourcecode(){}
    public Sourcecode(String rawdata) {
        this.rawdata = rawdata;
        calcCompilationUnit();
    }
    public void calcMetrics(String selection) {
        if(Objects.equals(selection, "giger")) {
            calcNumOfLines();
            calcFanOut();
            calcParameters();
            calcLocalVar();
            calcCommentRatio();
            calcCountPath();
            calcComplexity();
            calcExecStmt();
            calcMaxNesting();
        }
    }
    public void calcCompilationUnit() {
        String sourceClass = "public class Dummy{" + this.rawdata + "}";
        ASTParser parser = ASTParser.newParser(org.eclipse.jdt.core.dom.AST.JLS14);
        parser.setSource(sourceClass.toCharArray());
        this.compilationUnit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
    }
    public void calcAST() {
        NodeAST4Experiment nodeAstRoot = new NodeAST4Experiment();
        nodeAstRoot.num = countASTNode++;
        nodeAstRoot.numType = compilationUnit.getNodeType();
        String nameClass = compilationUnit.getClass().toString();
        if (0 < nameClass.split("\\.").length) {
            nodeAstRoot.nameType = nameClass.split("\\.")[nameClass.split("\\.").length - 1];
        } else {
            nodeAstRoot.nameType = nameClass;
        }
        nodeAstRoot.source = compilationUnit.toString();
        calcChildren(compilationUnit, nodeAstRoot);
        this.astRoot = nodeAstRoot;
    }
    public void calcChildren(ASTNode node, NodeAST4Experiment AST) {
        for (ASTNode nodeChild : getChildren(node)) {
            NodeAST4Experiment ASTChild = new NodeAST4Experiment();
            if (nodeChild.getNodeType() == 31) {
                countASTNode = 0;
                this.astRoot = ASTChild;
            }
            ASTChild.num = countASTNode++;
            ASTChild.numType = nodeChild.getNodeType();
            String nameClass = nodeChild.getClass().toString();
            if (0 < nameClass.split("\\.").length) {
                ASTChild.nameType = nameClass.split("\\.")[nameClass.split("\\.").length - 1];
            } else {
                ASTChild.nameType = nameClass;
            }
            ASTChild.source = nodeChild.toString();
            ASTChild.parent = AST;
            AST.children.add(ASTChild);
            calcChildren(nodeChild, ASTChild);
        }
    }
    public List<ASTNode> getChildren(ASTNode node) {
        List<ASTNode> children = new ArrayList<ASTNode>();
        List list = node.structuralPropertiesForType();
        for (int i = 0; i < list.size(); i++) {
            Object child = node.getStructuralProperty((StructuralPropertyDescriptor) list.get(i));
            if (child instanceof ASTNode) {
                children.add((ASTNode) child);
            } else if (child instanceof List) {
                for (Object object : (List) child) {
                    children.add((ASTNode) object);
                }
            }
        }
        return children;
    }

}
