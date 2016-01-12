package edu.illinois.cs.haoruo.langvision.util;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

import edu.brandeis.cs.steele.wn.FileBackedDictionary;
import edu.brandeis.cs.steele.wn.IndexWord;
import edu.brandeis.cs.steele.wn.POS;
import edu.brandeis.cs.steele.wn.PointerTarget;
import edu.brandeis.cs.steele.wn.PointerType;
import edu.brandeis.cs.steele.wn.Synset;
import edu.brandeis.cs.steele.wn.Word;
import edu.illinois.cs.haoruo.langvision.util.WNFileManager;

public class WordNetHandler {
	static boolean flag = false;
	static FileBackedDictionary m_dict;
	static WeakHashMap<String, IndexWord> wordCache;
	
	public static void load() {
		if (flag) return;
		WNFileManager fm = new WNFileManager("data/wordnet/wordnetdata");
		m_dict = new FileBackedDictionary(fm);
		wordCache = new WeakHashMap<String,IndexWord>();
		
		flag = true;
	}
	
	public static boolean existInWordNet(String str) {
		if (getIndexWord(str, "NOUN") != null || getIndexWord(str, "VERB") != null || getIndexWord(str, "ADJ") != null || getIndexWord(str, "ADV") != null) {
			return true;
		}
		return false;
	}
	
	public static IndexWord getWord(String str) {
		IndexWord word = null;
		word = getIndexWord(str, "NOUN");
		if (word != null) return word;
		word = getIndexWord(str, "VERB");
		if (word != null) return word;
		word = getIndexWord(str, "ADJ");
		if (word != null) return word;
		word = getIndexWord(str, "ADV");
		return word;
	}
	
	public static String getLemma(String str) {
		IndexWord word = getWord(str);
		if (word == null) {
			return str;
		}
		else {
			return word.getLemma();
		}
	}

	public static boolean isSynonym(String tag_tmp, String tag) {
		ArrayList<String> synonyms = new ArrayList<String>();
		synonyms.addAll(getSynonyms(tag, "NOUN"));
		synonyms.addAll(getSynonyms(tag, "VERB"));
		synonyms.addAll(getSynonyms(tag, "ADJ"));
		synonyms.addAll(getSynonyms(tag, "ADV"));
		if (synonyms.contains(tag_tmp)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static ArrayList<String> getSynonyms(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			try {
				int count = word.getTaggedSenseCount();
				if (count > 0) {
					Synset[] synsets = word.getSenses();
					for (Synset s: synsets) {
						try {
							Word[] words = s.getWords();
							for (Word w: words) {
								if (!word.getLemma().equals(w.getLemma())) {
									res.add(w.getLemma());
								}
							}
						} catch (Exception e) {
							System.out.println("[Fail to get synonyms] " + str + "\t" + pos);
//							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				System.out.print("[Fail to get synonyms] " + str + "\t" + pos);
//				e.printStackTrace();
			}
		}
		return res;
	}

	public static ArrayList<String> getHypernyms(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.HYPERNYM);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get hypernyms] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getHyponyms(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.HYPONYM);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get hyponyms] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getEntailmentWords(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.ENTAILMENT);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get entialment words] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getDerivedWords(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.DERIVED);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get derived words] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getMemberHolonyms(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.MEMBER_HOLONYM);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get member_holonym words] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getMemberMeronym(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.MEMBER_MERONYM);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get member_meronym words] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getAntomyms(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.ANTONYM);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get anotnym words] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getAttributes(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.ATTRIBUTE);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get attribute words] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getSubstanceHolonyms(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.SUBSTANCE_HOLONYM);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get substance_holonym words] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static ArrayList<String> getSubstanceMeronyms(String str, String pos) {
		ArrayList<String> res = new ArrayList<String>();
		if (isFilteredWord(str, pos)) 
			return res;
		IndexWord word = getIndexWord(str, pos);
		if (word != null) {
			int count = word.getTaggedSenseCount();
			if (count > 0) {
				Synset[] synsets = word.getSenses();
				for (Synset s: synsets) {
					try {
						PointerTarget[] sets = s.getTargets(PointerType.SUBSTANCE_MERONYM);
						for (PointerTarget target: sets) {
							Synset ss = (Synset) target;
							Word[] words = ss.getWords();
							for (Word w: words) {
								res.add(w.getLemma());
							}
						}
					} catch (Exception e) {
						System.out.println("[Fail to get substance_meronym words] " + str + "\t" + pos);
//						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public static boolean isFilteredWord (String str, String pos) {
		if (pos.equals("VERB")) {
			if (str.toLowerCase().equals("have") || 
					str.toLowerCase().equals("know") || 
					str.toLowerCase().equals("take") ||
					str.toLowerCase().equals("has") || 
					str.toLowerCase().equals("knows") || 
					str.toLowerCase().equals("takes") ||
					str.toLowerCase().equals("check") || 
					str.toLowerCase().equals("checks") ||
					str.toLowerCase().equals("love") || 
					str.toLowerCase().equals("loves") ||
					str.toLowerCase().equals("bang") || 
					str.toLowerCase().equals("bangs") ||
					str.toLowerCase().equals("screw") || 
					str.toLowerCase().equals("screws") ||
					str.toLowerCase().equals("denounce") || 
					str.toLowerCase().equals("denounces")  ||
					str.toLowerCase().equals("inform") || 
					str.toLowerCase().equals("informs")  ||
					str.toLowerCase().equals("criticize") || 
					str.toLowerCase().equals("criticizes") ||
					str.toLowerCase().equals("correct") || 
					str.toLowerCase().equals("corrects") ||
					str.toLowerCase().equals("make") || 
					str.toLowerCase().equals("makes") || 
					str.toLowerCase().equals("made") || 
					str.toLowerCase().equals("score") || 
					str.toLowerCase().equals("scores") ||
					str.toLowerCase().equals("sbetray") ||
					str.toLowerCase().equals("fuck") ||
					str.toLowerCase().equals("snitch") ||
					str.toLowerCase().equals("scores") || 
					str.toLowerCase().equals("berate") || 
					str.toLowerCase().equals("lambaste")  || 
					str.toLowerCase().equals("reprimand")  || 
					str.toLowerCase().equals("betray")  || 
					str.toLowerCase().equals("lambaste")  || 
					str.toLowerCase().equals("check")  || 
					str.toLowerCase().equals("checked")  || 
					str.toLowerCase().equals("lecture")  || 
					str.toLowerCase().equals("lectured")  || 
					str.toLowerCase().equals("shop")  || 
					str.toLowerCase().equals("shopped")  || 
					str.toLowerCase().equals("shopping") 
					) {
				
				return true;
			}
		}
		if (pos.equals("NOUN")) {
			if (str.toLowerCase().equals("love") || 
					str.toLowerCase().equals("loves")) {
				return true;
			}
		}
		return false;
	}
	
	private static IndexWord getIndexWord(String word, String pos) {
		if (wordCache.containsKey(word))
			return wordCache.get(word);   

		IndexWord result = lookupIndexWordSafe(word, pos);
		if (result != null) {
			wordCache.put(word, result);
			return result;
		}
		if (result == null && word.endsWith("s")) {
			String lemma = word.substring(0, word.length()-1);
			result = lookupIndexWordSafe(lemma, pos);
		}
		if (result == null && word.endsWith("es") ) {
			String lemma = word.substring(0, word.length()-2);
			result = lookupIndexWordSafe(lemma, pos);
		}
		if (result != null) {
			wordCache.put(word, result);
			return result;
		}

		//System.out.println(word + "-s and -es not found, trying irregulars");
		String baseForm = null;
		if (pos.equals("NOUN")) {
		    baseForm = m_dict.lookupBaseForm(POS.NOUN, word);
		} else if (pos.equals("ADJ")) {
		    baseForm = m_dict.lookupBaseForm(POS.ADJ, word);
		}
		if (baseForm != null) {
		    if (baseForm.length() >= word.length() - 2) {
		    	result = lookupIndexWordSafe(baseForm, pos);
		    	if (!baseForm.equalsIgnoreCase(word)) {
		    		//System.out.println("Found baseform: "
		    		// + baseForm + " of " + word);
		    	}
		    }
		} 

		wordCache.put(word, result);
		return result;
	}
	 
	protected static IndexWord lookupIndexWordSafe(String word, String pos) {
		if (word.length() == 0)
			return null;
		if (word.startsWith("\'") && word.length() <= 2)
		    return null;

		IndexWord result = null;
		try {
		    if (pos.equals("NOUN")) {
		    	result = m_dict.lookupIndexWord(POS.NOUN, word);
		    } else if (pos.equals("VERB")) {
		    	result = m_dict.lookupIndexWord(POS.VERB, word);
		    } else if (pos.equals("ADJ")) {
		    	result = m_dict.lookupIndexWord(POS.ADJ, word);
		    } else if (pos.equals("ADV")) {
			    result = m_dict.lookupIndexWord(POS.ADV, word);
		    } else {
		    	System.err.println("Unrecognized POS when looking up IndexWord");
		    }
		} catch (ClassCastException e) { //Known problem.
		    //Ignore.
		    //e.printStackTrace();
		} catch (NoSuchElementException e) { //POS IMPLIED problem.
		    //Ignore (probably punctuation or number)
		    //NOTE: The underlying JWordNet may generate a text
		    //message (to standard out or err) indicating
		    //that there is a problem parsing 18 IMPLIED
		    //(Part of the copyright notice.
		    //System.err.println(word + " (" + pos + ")");
		}
		//It is a known problem (for us) that lookupIndexWord will backoff
		//to a prefix if the whole string isn't found.
		if (result != null && !result.getLemma().equalsIgnoreCase(word)) {
		    //System.err.println("Warning: found " + result.getLemma()
		    //+ " instead of " + word + ".  Ignoring.");
		    result = null;
		}
		return result;
	}
}