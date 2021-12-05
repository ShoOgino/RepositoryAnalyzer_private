import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import util.FileUtil;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws Exception {
		//${tasks}.jsonをパースして、実行するタスクの設定を取得。
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode json = objectMapper.readTree(FileUtil.readFile(args[0]));
		Tasks tasks = objectMapper.readValue(json.toString(), Tasks.class);
		tasks.execute();
	}
}