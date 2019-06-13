/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.quest;


import forge.deck.DeckgenUtil;
import forge.deck.io.Archetype;
import forge.model.FModel;

/**
 * <p>
 * QuestDuel class.
 * </p>
 * MODEL - A single duel event data instance, including meta and deck.
 * 
 */
public class QuestEventLDADuel extends QuestEventDuel {

    private Archetype archetype;

    /**
     * Instantiates a new quest duel.
     */
    public QuestEventLDADuel(Archetype archetype) {
        super();
        this.archetype = archetype;
        this.eventDeck = DeckgenUtil.buildLDACArchetypeDeck(archetype, FModel.getFormats().getStandard(),true);
    }

}
