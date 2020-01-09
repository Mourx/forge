package forge.ai.simulation;
import forge.game.ability.*;
import forge.game.card.Card;

import java.util.ArrayList;

import forge.card.*;

public class CardTypeSorter {
	
	
	public ArrayList<Integer> GetTypes(Card c){
		ArrayList<Integer> types = new ArrayList<Integer>();
		
		CardTypeView v = c.getType();

		int i = v.isArtifact() ? 1 : 0;
		types.add(i);
		i = v.isBasicLand() ? 1:0;
		types.add(i);
		i = v.isBasicLand() ? 1:0;
		types.add(i);
		i = v.isCreature() ? 1:0;
		types.add(i);
		i = v.isEnchantment() ? 1:0;
		types.add(i);
		i = v.isInstant() ? 1:0;
		types.add(i);
		i = v.isLand() ? 1:0;
		types.add(i);
		i = v.isLegendary() ? 1:0;
		types.add(i);
		i = v.isPermanent() ? 1:0;
		types.add(i);
		i = v.isPlaneswalker() ? 1:0;
		types.add(i);
		i = v.isSorcery() ? 1:0;
		types.add(i);
		//comment
		
		
		return types;
	}
}
