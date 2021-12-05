package data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Commit {
	public String id = null;
	public Integer date = null;
	public String author = null;
	public String message = null;
	public boolean isMerge = false;
	public Map<String, CommitsOnModule> idParent2Modifications = new HashMap<>();
	public String idParentMaster = null;
	public Commit() {
	}

	public void save(String path, String option){
		File file = new File(path);
		File dirParent = new File(file.getParent());
		dirParent.mkdirs();
		try (FileOutputStream fos = new FileOutputStream(path);
			 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			 BufferedWriter writer = new BufferedWriter(osw)){
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.writeValue(writer, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
