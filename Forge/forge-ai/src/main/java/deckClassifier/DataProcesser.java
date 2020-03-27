package deckClassifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

import deckClassifier.*;

// class to turn a deck list into a input for NN
public class DataProcesser {
	
	ArrayList<CardData> deck;
	ArrayList<CardDataJson> deckJson;
	ListConverter conv = new ListConverter();
	HashMap<String,Integer> names = new HashMap<String,Integer>();
	HashMap<String,Integer> costs = new HashMap<String,Integer>();
	HashMap<String,Integer> supers = new HashMap<String,Integer>();
	HashMap<String,Integer> types = new HashMap<String,Integer>();
	HashMap<String,Integer> subs = new HashMap<String,Integer>();
	
	
	
	public DataProcesser(ArrayList<CardData> d) {
		deck = d;
		conv.setDeckList(deck);
		getCards();
		reformatCards();
	}
	
	public void getCards() {
		conv.getCards();
		deckJson = conv.getDeckList();
	}
	
	public void reformatCards() {
		for(CardDataJson data : deckJson) {
			System.out.print("inner");
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String jsonNames = "";
			String jsonCosts = "";
			String jsonSuperTypes = "";
			String jsonTypes = "";
			String jsonSubTypes = "";
			try {
				jsonNames = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/names.json")));
				jsonCosts = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/mana_costs.json")));
				jsonSuperTypes = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/supertypes.json")));
				jsonTypes = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/types.json")));
				jsonSubTypes = new String(Files.readAllBytes(Paths.get("../../PythonNN/Tensorflow testing/pyScripts/subtypes.json")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			names = gson.fromJson(jsonNames,HashMap.class);
			costs = gson.fromJson(jsonCosts, HashMap.class);
			supers = gson.fromJson(jsonSuperTypes, HashMap.class);
			types = gson.fromJson(jsonTypes, HashMap.class);
			subs = gson.fromJson(jsonSubTypes, HashMap.class);
			
			System.out.print(names.toString());
			int name = names.get(data.name);
			int cost = costs.get(data.mana_cost);
			float cmc = data.cmc;
			String[] typings = data.type_line.replace("-","").replace("  ", " ").split("\\s+");
			System.out.print(typings);
			
			
		}
	}

}
