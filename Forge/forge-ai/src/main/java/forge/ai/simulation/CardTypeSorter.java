package forge.ai.simulation;
import forge.game.ability.*;
import forge.game.card.Card;
import forge.game.spellability.SpellAbility;
import forge.util.collect.FCollectionView;

import java.util.ArrayList;

import forge.card.*;

public class CardTypeSorter {
	
	
	public ArrayList<Integer> GetTypes(Card c){
		ArrayList<Integer> types = new ArrayList<Integer>();
		
		CardTypeView v = c.getType();
		
		//Add Type Information
		//Ignoring weird types like conspiracy, tribal, etc.
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
		
		//Add a mark to show creature has ability, 1 for mana, another 1 for 
		// non mana-ability
		FCollectionView<SpellAbility> fv = c.getAllSpellAbilities();
		int j = 0, k =0, pow = 0, tou = 0;
		i = 0;
		//if creature we evaluate if has activatable abilities
		if(v.isCreature()) {
			if(fv.size()!=0) {
				for(SpellAbility sp:fv) {
					i = (sp.isManaAbility() && i== 0) ? 1:0;
					j = (sp.isManaAbility() && j== 0) ? 0:1;
					k = (c.hasETBTrigger(false) && k==0) ? 1:0;
				}
			}
			pow = c.getBasePower();
			tou = c.getBaseToughness();
		}
		//mana ability
		types.add(i);
		//other ability
		types.add(j);
		//ETB effect;
		types.add(k);
		//power
		types.add(pow);
		//toughness
		types.add(tou);
		
		//CMC
		int cmc = c.getCMC();
		types.add(cmc);
		
		//Actual Mana Cost
		int[] wubrg = c.getManaCost().getColorShardCounts();
		//White
		types.add(wubrg[0]);
		//blUe
		types.add(wubrg[1]);
		//Black
		types.add(wubrg[2]);
		//Red
		types.add(wubrg[3]);
		//Green
		types.add(wubrg[4]);
		//Generic mana
		int generic = c.getManaCost().getGenericCost();
		types.add(generic);
				
		return types;
	}
}
