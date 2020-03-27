package deckClassifier;

import java.util.ArrayList;
import java.util.Map.Entry;

import forge.deck.CardPool;
import forge.deck.Deck;
import forge.deck.DeckSection;
import forge.game.card.Card;
import forge.game.card.CardCollectionView;
import forge.item.PaperCard;

public class ChoiceWeights {

	public float weight = 5.0f;
	DataProcesser processor;
	
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
	}
}
