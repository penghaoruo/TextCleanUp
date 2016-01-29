package edu.illinois.cs.haoruo.langvision.textfilter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.illinois.cs.haoruo.langvision.util.IOManager;
import edu.illinois.cs.haoruo.langvision.util.WordNetHandler;

public class CleanUp {
	String tag_path = "/home/liwei/jdownload/fotolia/tag.txt";
	String cap_path = "/home/liwei/jdownload/fotolia/captioning.txt";
	String lang_path = "/home/haoruo/TextCleanUp/langid/langid/lang.txt";
	
	ArrayList<String> caption_lines = null;
	ArrayList<String> tag_lines = null;
	ArrayList<String> lang_lines = null;
	
	ArrayList<String> captionsTokens = new ArrayList<String>();
	ArrayList<String> tagsTokens = new ArrayList<String>();
	
	BufferedWriter bw1 = IOManager.openWriter("captions.txt");
	BufferedWriter bw2 = IOManager.openWriter("tags.txt");
	
	int index = 0;
	
	public void removeCapWhole() throws IOException {
		for (int i = 0; i < caption_lines.size(); i++) {
//			if (i != 261219) {
//				continue;
//			}
			System.out.println(i);
			
			String caption_line = caption_lines.get(i).toLowerCase();
//			System.out.println(caption_line);
			
			String[] strs = caption_line.split("\t");
			if (strs.length < 3) {
				System.out.println("Error: " + caption_line);
				System.exit(1);
			}
			String id = strs[0];
			String category = strs[1];
			String caption = strs[2];
			
			// remove ??
			if (caption.contains("??")) {
				//System.out.println("Removing ??: " + caption);
				continue;
			}
			
			// remove non-English
			String lang = lang_lines.get(i).substring(2,4);
			if (!lang.equals("en")) {
				//System.out.println("Removing Non-English: " + caption + "\t" + lang);
				continue;
			}
			
			String caption_new = removeCapPart(caption);
//			System.out.println(caption_new);
			if (caption_new == null) {
				continue;
			}
			strs = caption_new.split(" ");
			for (String str: strs) {
				if (!captionsTokens.contains(str)) {
					captionsTokens.add(str);
				}
			}
			
//			System.out.println(tag_lines.get(i));
			String tag_new = removeTag(tag_lines.get(i));
//			System.out.println(tag_new);
			
			bw1.write(id + "\t" + category + "\t" + caption_new + "\n");
			bw2.write(tag_new + "\n");
			index++;
		}
		
	}
	
	public String removeCapPart(String str) {
		String res = str;
		
		// remove ! and .
		res = removeSymbol(res, "!");
		res = removeSymbol(res, ".");
		
		// replace &amp;  and &quot;
		res = replaceSymbol(res);
		
		// remove ( ... )
		res = removeBracket(res);
		
		// remove tail non-character symbols
		res = removeTail(res);
	
		return res;
	}

	public String removeTag(String line) {
		line = line.toLowerCase();
		
		String[] strs = line.split("\t");
		if (strs.length != 2) {
			System.out.println("Error: " + line);
			System.exit(1);
		}
		String id = strs[0];
		String[] tags = strs[1].substring(1, strs[1].length() - 1).split(",");
		
		ArrayList<String> tags_new = new ArrayList<String>();
		
		for (String tag : tags) {
//			System.out.println(tag);
			if (tag.length() < 3) {
				continue;
			}
			tag = tag.substring(1, tag.length() -1);
			
			tag = WordNetHandler.getLemma(tag);
			if (tags_new.contains(tag)) {
				continue;
			}
			
			// check synset
			for (String tag_tmp : tags_new) {
				if (WordNetHandler.isSynonym(tag_tmp, tag)) {
					continue;
				}
			}
			
			tags_new.add(tag);
		}
				
		String res = id + "\t" + "[";
		for (String tag_tmp : tags_new) {
			res += "\"" + tag_tmp + "\"" + ",";
			if (!tagsTokens.contains(tag_tmp)) {
				tagsTokens.add(tag_tmp);
			}
		}
		res = res.substring(0, res.length() -1) + "]";
		return res;
	}
	
	public void genStat() {
		System.out.println("size: " + index);
		System.out.println("caption unique token num: " + captionsTokens.size());
		System.out.println("tag unique num: " + tagsTokens.size());
	}
	
	public void output() throws IOException {
		IOManager.closeWriter(bw1);
		IOManager.closeWriter(bw2);
	}
	
	public void process() throws IOException {
		WordNetHandler.load();
		caption_lines = IOManager.readLines(cap_path);
		tag_lines = IOManager.readLines(tag_path);
		lang_lines = IOManager.readLines(lang_path);
		if (caption_lines.size() != tag_lines.size() || tag_lines.size() != lang_lines.size()) {
			System.out.println("Error!");
		}
		
		removeCapWhole();
		
		genStat();
		output();
	}
	
	public static void main(String[] args) throws Exception{
		CleanUp cl = new CleanUp();
		cl.process();
	}
	
	public boolean isAllEngChar(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < 'a' || str.charAt(i) > 'z') {
				return false;
			}
		}
		return true;
	}
	
	public String removeSymbol(String str, String s) {
		while (str.contains(s)) {
			str = str.replace(s, "");
		}
		return str;
	}
	
	public String replaceSymbol(String str) {
		while (str.contains("&amp;")) {
			str = str.replace("&amp;", "&");
		}
		while (str.contains("&quot;")) {
			str = str.replace("&quot;", "\"");
		}
		return str;
	}
	
	public String removeBracket(String str) {
		while (str.contains("(")) {
			int p = str.indexOf('(');
			int q = str.indexOf(')', p);
			if (q != -1) {
				str = str.substring(0, p) + str.substring(q + 1);
			} else {
				break;
			}
		}
		return str;
	}
	
	public String removeTail(String str) {
		int p = str.length() - 1;
		while (str.charAt(p) < 'a' || str.charAt(p) > 'z') {
			p--;
			if (p == -1) break;
		}
		if (p == -1) {
			return null;
		}
		return str.substring(0, p + 1);
	}
}
