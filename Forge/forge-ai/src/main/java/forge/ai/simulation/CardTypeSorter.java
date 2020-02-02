package forge.ai.simulation;
import forge.game.ability.*;
import forge.game.card.Card;
import forge.game.spellability.SpellAbility;
import forge.util.collect.FCollectionView;

import java.util.ArrayList;

import forge.card.*;
import com.google.gson.*;

public class CardTypeSorter {
	
	public CardTypeSorter() {
		
	}
	
	public void GetTypes(Card c){
		ArrayList<CardPackage> types = new ArrayList<CardPackage>();
		
		CardTypeView v = c.getType();
		
		//Add Type Information
		//Ignoring weird types like conspiracy, tribal, etc.
		int i = v.isArtifact() ? 1 : 0;
		types.add(new CardPackage("Artifact",i));
		i = v.isBasicLand() ? 1:0;
		types.add(new CardPackage("BasicLand",i));
		i = v.isCreature() ? 1:0;
		types.add(new CardPackage("Creature",i));
		i = v.isEnchantment() ? 1:0;
		types.add(new CardPackage("Enchantment",i));
		i = v.isInstant() ? 1:0;
		types.add(new CardPackage("Instant",i));
		i = v.isLand() ? 1:0;
		types.add(new CardPackage("Land",i));
		i = v.isLegendary() ? 1:0;
		types.add(new CardPackage("Legendary",i));
		i = v.isPermanent() ? 1:0;
		types.add(new CardPackage("Permanent",i));
		i = v.isPlaneswalker() ? 1:0;
		types.add(new CardPackage("Planeswalker",i));
		i = v.isSorcery() ? 1:0;
		types.add(new CardPackage("Sorcery",i));
		
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
		types.add(new CardPackage("ManaAbility",i));
		//other ability
		types.add(new CardPackage("ActivatedAbility",j));
		//ETB effect;
		types.add(new CardPackage("ETBEffect",k));
		//power
		types.add(new CardPackage("Power",pow));
		//toughness
		types.add(new CardPackage("Toughness",tou));
		
		//CMC
		int cmc = c.getCMC();
		types.add(new CardPackage("CMC",cmc));
		
		//Actual Mana Cost
		int[] wubrg = c.getManaCost().getColorShardCounts();
		//White
		types.add(new CardPackage("White",wubrg[0]));
		//blUe
		types.add(new CardPackage("Blue",wubrg[1]));
		//Black
		types.add(new CardPackage("Black",wubrg[2]));
		//Red
		types.add(new CardPackage("Red",wubrg[3]));
		//Green
		types.add(new CardPackage("Green",wubrg[4]));
		//Generic mana
		int generic = c.getManaCost().getGenericCost();
		types.add(new CardPackage("Generic",generic));
				
		GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
		Gson gson = builder.create();
		String json = gson.toJson(types);
		System.out.print(json);
		
	}
}
