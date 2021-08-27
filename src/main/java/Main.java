import data.*;
import misc.ArgBean;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		//引数処理
		ArgBean argBean = new ArgBean();
		CmdLineParser parser = new CmdLineParser(argBean);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.out.println("usage:");
			parser.printSingleLineUsage(System.out);
		}

		//プロジェクト全体のHistoryを分析
		Project project = new Project(argBean.pathProject);

		//複数存在するタスクについて、その内容をファイルから読み込む
		Tasks tasks = new Tasks(project, argBean.pathFileTask, argBean.multiProcess);

		//タスク実行
		tasks.execute();
	}
}