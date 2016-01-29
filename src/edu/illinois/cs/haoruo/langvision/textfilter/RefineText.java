package edu.illinois.cs.haoruo.langvision.textfilter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.illinois.cs.haoruo.langvision.util.IOManager;

public class RefineText {
	String tag_path = "/home/haoruo/TextCleanUp/tags_1.txt";
	String cap_path = "/home/haoruo/TextCleanUp/captions_1.txt";
	
	ArrayList<String> caption_lines = null;
	ArrayList<String> tag_lines = null;
	
	BufferedWriter bw1_id = null;
	BufferedWriter bw2_id = null;
	BufferedWriter bw1_text = null;
	BufferedWriter bw2_text = null;
	
	BufferedWriter bw1 = null;
	BufferedWriter bw2 = null;
	BufferedWriter bw3 = null;
	
	HashMap<String, Integer> cap_dict = new HashMap<String, Integer>();
	HashMap<String, Integer> tag_dict = new HashMap<String, Integer>();
	HashMap<String, Integer> all_dict = new HashMap<String, Integer>();
	
	int index = 0;
	
	public void splitCaption() throws IOException {
		String prev = "";
		for (int i = 0; i < caption_lines.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println(i);
			}
			
			String caption_line = caption_lines.get(i).toLowerCase();
			
			String[] cap_strs = caption_line.split("\t");
			if (cap_strs.length < 3) {
				System.out.println("Error: " + caption_line);
				System.exit(1);
			}
			String id = cap_strs[0];
			String category = cap_strs[1];
			String caption = cap_strs[2];
			
			// remove "=" and "-"
			caption = caption.replaceAll("=", " ");
			caption = caption.replaceAll("-", " ");
			caption = caption.replaceAll(",", " ");
			caption = caption.replaceAll(":", " ");
			caption = caption.replaceAll("\"", " ");
			
			// remove repeat captions
			if (caption.equals(prev)) {
				continue;
			}
			else {
				prev = caption;
			}
			
			String tag_line = tag_lines.get(i);
			String[] tag_strs = tag_line.split("\t");
			if (tag_strs.length != 2) {
				System.out.println("Error: " + tag_line);
				System.exit(1);
			}
			String tag_id = tag_strs[0];
			if (!tag_id.equals(id)) {
				System.out.println("Error: " + id + "\t" + tag_id);
				System.exit(1);
			}
			if (tag_strs[1].length() < 5) {
				continue;
			}
			
			String[] tags = tag_strs[1].substring(1, tag_strs[1].length() - 1).split(",");
			boolean flag = false;
			for (String tag : tags) {
				if (tag.length() < 3) {
					continue;
				}
				tag = tag.substring(1, tag.length() -1);
				bw2_text.write(tag + " ");
				flag = true;
			}
			if (flag == false) {
				continue;
			}
			
			bw2_text.write("\n");
			bw2_id.write(tag_id + "\n");
			
			bw1_id.write(id + "\t" + category + "\n");			
			bw1_text.write(caption + "\n"); 
			
			index++;
		}
	}

	public static void main(String[] args) throws Exception{
		RefineText rt = new RefineText();
		//rt.process();
		//rt.merge();
		rt.genDict();
	}
	
	public void process() throws IOException {
		bw1_id = IOManager.openWriter("captions_id.txt");
		bw2_id = IOManager.openWriter("tags_id.txt");
		bw1_text = IOManager.openWriter("captions_text.txt");
		bw2_text = IOManager.openWriter("tags_text.txt");
		
		caption_lines = IOManager.readLines(cap_path);
		tag_lines = IOManager.readLines(tag_path);
		if (caption_lines.size() != tag_lines.size()) {
			System.out.println("Error!");
		}
		
		splitCaption();
		
		IOManager.closeWriter(bw1_id);IOManager.closeWriter(bw1_text);
		IOManager.closeWriter(bw2_id);IOManager.closeWriter(bw2_text);
		
		System.out.println("size: " + index);
	}
	
	public void merge() throws IOException {
		bw1 = IOManager.openWriter("captions_2.txt");
		bw2 = IOManager.openWriter("tags_2.txt");
		
		ArrayList<String> caption_ids = IOManager.readLines("captions_id.txt");
		ArrayList<String> caption_lines = IOManager.readLines("captions_refine.txt");
		ArrayList<String> tag_ids = IOManager.readLines("tags_id.txt");
		ArrayList<String> tag_lines = IOManager.readLines("tags_refine.txt");
		
		if (caption_lines.size() != tag_lines.size() || caption_ids.size() != tag_lines.size()) {
			System.out.println("Error!");
			System.out.println(caption_lines.size());
			System.out.println(tag_lines.size());
			System.out.println(caption_ids.size());
			System.out.println(tag_ids.size());
			System.exit(1);
		}
		
		for (int i = 0; i < caption_ids.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println(i);
			}
			bw1.write(caption_ids.get(i) + "\t" + caption_lines.get(i) + "\n");
			
			String[] tags = tag_lines.get(i).split(" ");
			ArrayList<String> tags_new = new ArrayList<String>();
			for (String tag : tags) {
				if (!tags_new.contains(tag)) {
					tags_new.add(tag);
				}
			}
			
			String res = tag_ids.get(i) + "\t" + "[";
			for (String tag : tags_new) {
				res += "\"" + tag + "\"" + ",";
			}
			res = res.substring(0, res.length() -1) + "]";
			bw2.write(res + "\n");
		}
		
		IOManager.closeWriter(bw1);
		IOManager.closeWriter(bw2);
	}
	
	public void genDict() throws IOException {
		bw1 = IOManager.openWriter("captions_dict.txt");
		bw2 = IOManager.openWriter("tags_dict.txt");
		bw3 = IOManager.openWriter("all_dict.txt");
		
		ArrayList<String> captions = IOManager.readLines("captions_refine.txt");
		ArrayList<String> tags = IOManager.readLines("tags_refine.txt");
		
		for (int i = 0; i < captions.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println(i);
			}
			
			String[] cap_tokens = captions.get(i).split(" ");
			for (String token: cap_tokens) {
				if (cap_dict.containsKey(token)) {
					int k = cap_dict.get(token);
					cap_dict.put(token, k + 1);
				}
				else {
					cap_dict.put(token, 1);
				}
				if (all_dict.containsKey(token)) {
					int k = all_dict.get(token);
					all_dict.put(token, k + 1);
				}
				else {
					all_dict.put(token, 1);
				}
			}
			
			String[] tag_tokens = tags.get(i).split(" ");
			for (String token: tag_tokens) {
				if (tag_dict.containsKey(token)) {
					int k = tag_dict.get(token);
					tag_dict.put(token, k + 1);
				}
				else {
					tag_dict.put(token, 1);
				}
				if (all_dict.containsKey(token)) {
					int k = all_dict.get(token);
					all_dict.put(token, k + 1);
				}
				else {
					all_dict.put(token, 1);
				}
			}
		}
		
		System.out.println("Dict Building Done");
		
		outputDict(cap_dict, bw1); System.out.println("Sort 1 Done");
		outputDict(tag_dict, bw2); System.out.println("Sort 2 Done");
		outputDict(all_dict, bw3); System.out.println("Sort 3 Done");
	}

	public void outputDict(HashMap<String, Integer> dict, BufferedWriter bw) throws IOException {
		System.out.println(dict.size());
		while (dict.size() > 0) {
			int max = 0;
			String token = "";
			for (Entry<String, Integer> entry : dict.entrySet()) {
				if (max < entry.getValue()) {
					max = entry.getValue();
					token = entry.getKey();
				}
			}
			bw.write(token + "\t" + max + "\n");
			dict.remove(token);
		}
		IOManager.closeWriter(bw);
	}
}
