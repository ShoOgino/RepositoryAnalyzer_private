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
	@JsonIgnore
	public CommitsOnModule parentsModification;
	public List<String> parents;
	@JsonIgnore
	public CommitsOnModule childrenModification;
	public List<String> children;

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
		this.parentsModification = new CommitsOnModule();
		this.parents = new ArrayList<>();
		this.childrenModification = new CommitsOnModule();
		this.children = new ArrayList<>();
	}

	public void loadAncestors(CommitsOnModule commitsOnModule){
		commitsOnModule.put(this.idCommitParent, this.idCommit, this.pathOld, this.pathNew, this);
		for(CommitOnModule commitOnModule : this.parentsModification.values()) {
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
