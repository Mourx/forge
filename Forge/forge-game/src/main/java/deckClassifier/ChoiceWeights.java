package deckClassifier;

import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

import forge.deck.CardPool;
import forge.deck.Deck;
import forge.item.PaperCard;

public class ChoiceWeights {

	public float MY_CARDS = 5.0f;
	public float OP_CARDS = 4.0f;
	public float MY_LIFE = 2.0f;
	public float OP_LIFE = 2.0f;
	public float BASE_CMC = 30.0f;
	public float LAND = 100.0f;
	DataProcesser processor;
	private INDArray classes;
	
	public ChoiceWeights(Deck d) {
		ArrayList<CardData> deck = new ArrayList<CardData>();
		System.out.print(d.toString());
		CardPool pool = d.getAllCardsInASinglePool();
		pool.get(0).getName();
		for(int i = 0;i<pool.countAll();i++) {
			PaperCard c = pool.get(i);
			CardData data = new CardData(1,c.getName());
			deck.add(data);
		}
		processor = new DataProcesser(deck);
		classes = processor.getClasses();
		int maxClass = 0;
		for(int i =0;i<classes.columns();i++) {
			System.out.print(classes.getDouble(0, i) + " , ");
			if(classes.getDouble(0,maxClass) < classes.getDouble(0,i)){
				maxClass = i;
			}
		}
		float difference = ((float)(maxClass) - 1.5f);
		// Modifies weights with a minor adjustment to change behaviour
		MY_CARDS += difference * 0.5;
		OP_CARDS += difference * 0.4;
		MY_LIFE += difference * 0.2;
		OP_LIFE += difference * 0.2;
		BASE_CMC += difference * 3;
		LAND += difference * 10;
	}
}
