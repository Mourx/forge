package deckClassifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



// class to turn a deck list into a input for NN
public class DataProcesser {
	
	ModelLoader loader = new ModelLoader();
	
	ArrayList<CardData> deck;
	ArrayList<CardDataJson> deckJson;
	
	ArrayList<Integer> allValues = new ArrayList<Integer>();
	
	ListConverter conv = new ListConverter();
	HashMap<String,Double> names = new HashMap<String,Double>();
	HashMap<String,Double> costs = new HashMap<String,Double>();
	HashMap<String,Double> supers = new HashMap<String,Double>();
	HashMap<String,Double> types = new HashMap<String,Double>();
	HashMap<String,Double> subs = new HashMap<String,Double>();
	HashMap<String,Double> keywords = new HashMap<String,Double>();
	String[] colours = {"W","U","B","R","G"};
	ArrayList<Integer> typeList = new ArrayList<Integer>();
	ArrayList<Integer> colourList = new ArrayList<Integer>();
	ArrayList<Integer> keywordList = new ArrayList<Integer>();
	INDArray features;
	public DataProcesser(ArrayList<CardData> d) {
		deck = d;
		conv.setDeckList(deck);
		getCards();
		reformatCards();
		createINDarray();
		loader.LoadModel();
	}
	
	public INDArray getClasses() {
		return loader.Predict(features);
	}
	
	public void getCards() {
		conv.getCards();
		deckJson = conv.getDeckList();
	}
	
	public void createINDarray() {
		features = Nd4j.zeros(60*522);
		for(int i = 0;i<60*522;i++) {
			features.putScalar(i, allValues.get(i));
		}
	}
	
	
	public void reformatCards() {
		allValues = new ArrayList<Integer>();
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String jsonNames = "";
		String jsonCosts = "";
		String jsonSuperTypes = "";
		String jsonTypes = "";
		String jsonSubTypes = "";
		String jsonKeywords = "";
		try {
			jsonNames = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/names.json")));
			jsonCosts = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/mana_costs.json")));
			jsonSuperTypes = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/supertypes.json")));
			jsonTypes = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/types.json")));
			jsonSubTypes = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/subtypes.json")));
			jsonKeywords = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/keywords.json")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		names = gson.fromJson(jsonNames,HashMap.class);
		costs = gson.fromJson(jsonCosts, HashMap.class);
		supers = gson.fromJson(jsonSuperTypes, HashMap.class);
		types = gson.fromJson(jsonTypes, HashMap.class);
		subs = gson.fromJson(jsonSubTypes, HashMap.class);
		keywords = gson.fromJson(jsonKeywords, HashMap.class);
		for(CardDataJson data : deckJson) {
			
			// Name
			double name = 0;
			if(names.containsKey(data.name)) {
				name = names.get(data.name);
			}else {
				names.put(data.name,(double)names.size());
				name = names.get(data.name);
			}
			allValues.add((int)name);
			// ManaCost
			double cost = 0;
			if(costs.containsKey(data.mana_cost)){
				cost = costs.get(data.mana_cost);
			}else {
				costs.put(data.mana_cost, (double)costs.size());
				cost = costs.get(data.mana_cost);
			}
			allValues.add((int)cost);
			// CMC
			double cmc = data.cmc;
			String[] typings = data.type_line.replace("-","").replace("  ", " ").split("\\s+");
			allValues.add((int)cmc);
			// SuperTypes
			Iterator it  = supers.entrySet().iterator();
			while(it.hasNext()) {
				int val = 0;
				Map.Entry pair = (Map.Entry)it.next();
				String type = (String) pair.getKey();
				for(String s : typings) {
					if(s.equals(type)) {
						val = 1;
					}
				}
				typeList.add(val);
				allValues.add(val);
			}
			// Types
			it  = types.entrySet().iterator();
			while(it.hasNext()) {
				int val = 0;
				Map.Entry pair = (Map.Entry)it.next();
				String type = (String) pair.getKey();
				for(String s : typings) {			
					if(s.equals(type)) {
						val = 1;
					}
				}
				typeList.add(val);
				allValues.add(val);
			}
			// SubTypes
			it  = subs.entrySet().iterator();
			while(it.hasNext()) {
				int val = 0;
				Map.Entry pair = (Map.Entry)it.next();
				String type = (String) pair.getKey();
				for(String s : typings) {
					if(s.equals(type)) {
						val = 1;
					}
				}
				typeList.add(val);
				allValues.add(val);
			}
			// Colours
			for(String c : colours) {
				int val = 0;
				for(String co : data.colors) {
					if(co == c) {
						val = 1;
					}
				}
				colourList.add(val);
				allValues.add(val);
			}
			//Power / Toughness
			if(data.power==null) data.power = "0";
			if(data.toughness==null) data.toughness = "0";
			String tempPower = data.power.replace("*", "").replace("+", "");
			if(tempPower.isEmpty()) { 
				tempPower = "0";
			
			}
			double power = Integer.parseInt(tempPower);
			allValues.add((int)power);
			String tempTough = data.toughness.replace("*", "").replace("+", "");
			if(tempTough.isEmpty()) { 
				tempTough = "0";
			}
			double toughness = Integer.parseInt(tempTough);
			allValues.add((int)toughness);
			//Keywords
			it  = keywords.entrySet().iterator();
			Iterator keyIt = data.keywords.entrySet().iterator();
			while(it.hasNext()) {
				int val = 0;
				Map.Entry pair = (Map.Entry)it.next();
				String keyword = (String) pair.getKey();
				while(keyIt.hasNext()){
					Map.Entry keyPair = (Map.Entry)keyIt.next();
					
					String cardKeyword = (String) keyPair.getKey();
					if(cardKeyword.equals(keyword)) {
						val = 1;
					}
				}
				keywordList.add(val);
				allValues.add(val);
			}

		}
	}

}
