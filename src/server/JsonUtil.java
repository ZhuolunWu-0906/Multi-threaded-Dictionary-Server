package server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.lang.String;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonUtil {
	
	
	//	Create a dictionary (JSON) file if it does not exist
	public static void createJsonFile() {
		if (!(new File("server-dict.json").isFile())) {
			
			JSONArray dict = new JSONArray();
			
			try (FileWriter file = new FileWriter("server-dict.json")) {
	            //We can write any JSONArray or JSONObject instance to the file
	            file.write(dict.toJSONString()); 
	            file.flush();
	 
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	
	//	Read dictionary (JSON) file
	public static JSONArray readJsonFile() {
		
		JSONParser jsonParser = new JSONParser();
		JSONArray dict = new JSONArray();
		
		try (FileReader reader = new FileReader("server-dict.json"))
        {
            Object obj = jsonParser.parse(reader);
            dict = (JSONArray) obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		return dict;
	}
	
	
	//	Get meaning of the word
	public static String getMeaning(JSONArray dict, String word) {
		
		int size = dict.size();
		String meaning = new String();
		
		for (int i = 0; i < size; i++) {
			String extracted = dict.get(i).toString();
			int index = extracted.indexOf(":");
			if (extracted.substring(2,index-1).equals(word)) {
				meaning = extracted.substring(index+2, extracted.length()-2);
				break;
			}
		}
		
		return meaning;
	}
	
	
	//	Add a new word in dictionary
	@SuppressWarnings("unchecked")
	public static JSONArray addWord(JSONArray dict, String word, String meaning) {
		JSONObject newWord = new JSONObject();
		newWord.put(word,meaning);
		dict.add(newWord);
		return dict;
	}
	
	
	//	Delete a word in dictionary
	public static JSONArray deleteWord(JSONArray dict, String toBeDeleted) {
		
		int index = getIndex(dict, toBeDeleted);
		
		dict.remove(index);
		
		return dict;
	}
	
	
	//	Modify the meaning of one word
	public static JSONArray modifyWord(JSONArray dict, String toBeModified, String modifiedMeaning) {
		
		dict = deleteWord(dict, toBeModified);
		dict = addWord(dict, toBeModified, modifiedMeaning);
		
		return dict;
	}
	
	
	//	Get index of the word in the dictionary
	public static int getIndex(JSONArray dict, String word) {
		
		int count;
		int size = dict.size();
		
		for (count = 0; count < size; count++) {
			String extracted = dict.get(count).toString();
			int index = extracted.indexOf(":");
			if (extracted.substring(2,index-1).equals(word)) {
				break;
			}
		}
		
		if (count == size) {
			return -1;
		}
		
		return count;
	}
	
	
	//	Find if the word is in the dictionary
	public static Boolean inObject(JSONArray dict, String word) {
		
		int index = getIndex(dict, word);
		
		if (index == -1) {
			return false;
		}
		
		return true;
	}
	
	
}
