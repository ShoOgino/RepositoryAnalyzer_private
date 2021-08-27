package data;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class Project {
	public final String pathProject;
	public final String pathRepositoryMethod;
	public final String pathRepositoryFile;
	public final String pathModules;
	public final String pathCommits;
	public final String pathBugs;
	public Repository repositoryFile;
	public Repository repositoryMethod;
    public Commits commitsAll  = new Commits();
	public Modules modulesAll = new Modules();
	public Bugs bugsAll = new Bugs();

	public Project(String pathProject){
		this.pathProject = pathProject;
		pathRepositoryMethod = this.pathProject +"/repositoryMethod";
		pathRepositoryFile   = this.pathProject +"/repositoryFile";
		pathModules          = this.pathProject +"/modules";
		pathCommits          = this.pathProject +"/commits";
		pathBugs             = this.pathProject +"/bugs.json";

		try {
			repositoryFile = new FileRepositoryBuilder().setGitDir(new File(pathRepositoryFile + "/.git")).build();
			repositoryMethod = new FileRepositoryBuilder().setGitDir(new File(pathRepositoryMethod + "/.git")).build();
			commitsAll.loadCommitsFromRepository(repositoryMethod, pathCommits);
		    commitsAll.loadCommitsFromFile(pathCommits);
		    modulesAll.analyzeAllModules(commitsAll);
			//modulesAll.saveAsJson(setting.pathModules);
		    bugsAll.loadBugsFromFile(pathBugs);
			commitsAll.embedBugInfo(bugsAll, modulesAll);
		}catch (IOException exception){
			exception.printStackTrace();
		}
	}
}