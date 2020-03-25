package forge.ai.simulation;

import java.io.File;
import java.util.ArrayList;

import deckClassifier.*;

// class to turn a deck list into a input for NN
public class DataProcesser {
	
	ArrayList<CardDataJson> deck = new ArrayList<CardDataJson>();
	public DataProcesser() {
		
	}
	
	public ArrayList<CardDataJson> LoadDeck(File file) {
		ListConverter classifier = new ListConverter();
		String name = file.getName().replace(".dck", ".json");
		File json = new File("MtgJson/"+name);
		if(!json.exists()) {
			if(classifier.ReadDeck(file.getName())) {
				//classifier.matchJson();
			}
			classifier.getCards();
			classifier.saveJsonList(file.getName());
		} else {
			classifier.getCards();
			classifier.ReadFromJson(json.getName());
		}
		deck = classifier.getDeckList();
		return deck;
	}
	
	public ArrayList<CardDataJson> getDeck(){
		return deck;
	}
	
	public double[][] getAsArray(){
		double data[][];
		
		
		return data;
	}
}
