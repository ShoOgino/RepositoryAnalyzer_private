package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.*;

@Data
public class CommitOnModule {
	public String idCommit;
	public String idCommitParent;
	public int date;
	public String author = null;
	public boolean isMerge;
	public String type;
	public String pathNew;
	public String pathOld;
	public String sourceNew;
	public String sourceOld;
	public Diffs diffs;
	public String pathNewParent;
	@JsonIgnore public CommitsOnModule parents;
	public List<String> idsCommitParent;
	@JsonIgnore public CommitsOnModule children;
	public List<String> idsCommitChild;
	public CommitOnModule() {
		this.idCommit= "";
		this.idCommitParent = "";
		this.date=0;
		this.author="";
		this.isMerge=false;
		this.pathOld= "";
		this.pathNew= "";
		this.sourceOld= "";
		this.sourceNew= "";
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
}
