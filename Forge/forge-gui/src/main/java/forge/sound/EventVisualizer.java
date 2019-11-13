package forge.sound;

import java.util.Collection;

import forge.LobbyPlayer;
import forge.events.IUiEventVisitor;
import forge.events.UiEventAttackerDeclared;
import forge.events.UiEventBlockerAssigned;
import forge.events.UiEventNextGameDecision;
import forge.game.card.Card;
import forge.game.event.EventValueChangeType;
import forge.game.event.GameEvent;
import forge.game.event.GameEventBlockersDeclared;
import forge.game.event.GameEventCardAttachment;
import forge.game.event.GameEventCardChangeZone;
import forge.game.event.GameEventCardCounters;
import forge.game.event.GameEventCardDamaged;
import forge.game.event.GameEventCardDestroyed;
import forge.game.event.GameEventCardPhased;
import forge.game.event.GameEventCardRegenerated;
import forge.game.event.GameEventCardSacrificed;
import forge.game.event.GameEventCardTapped;
import forge.game.event.GameEventFlipCoin;
import forge.game.event.GameEventGameOutcome;
import forge.game.event.GameEventLandPlayed;
import forge.game.event.GameEventManaBurn;
import forge.game.event.GameEventPlayerLivesChanged;
import forge.game.event.GameEventPlayerPoisoned;
import forge.game.event.GameEventShuffle;
import forge.game.event.GameEventSpellResolved;
import forge.game.event.GameEventTokenCreated;
import forge.game.event.GameEventTurnEnded;
import forge.game.event.GameEventZone;
import forge.game.event.IGameEventVisitor;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;
import forge.util.maps.MapOfLists;

/**
 * This class is in charge of converting any forge.game.event.Event to a SoundEffectType.
 *
 */
public class EventVisualizer extends IGameEventVisitor.Base<SoundEffectType> implements IUiEventVisitor<SoundEffectType> {

    final LobbyPlayer player;
    public EventVisualizer(final LobbyPlayer lobbyPlayer) {
        this.player = lobbyPlayer;
    }

    @Override
    public SoundEffectType visit(final GameEventCardDamaged event) { return SoundEffectType.Damage; }
    @Override
    public SoundEffectType visit(final GameEventCardDestroyed event) { return SoundEffectType.Destroy; }
    @Override
    public SoundEffectType visit(final GameEventCardAttachment event) { return SoundEffectType.Equip; }
    @Override
    public SoundEffectType visit(final GameEventCardChangeZone event) {
        final ZoneType from = event.from == null ? null : event.from.getZoneType();
        final ZoneType to = event.to.getZoneType();
        if( from == ZoneType.Library && to == ZoneType.Hand) {
            return SoundEffectType.Draw;
        }
        if( from == ZoneType.Hand && (to == ZoneType.Graveyard || to == ZoneType.Library) ) {
            return SoundEffectType.Discard;
        }

        return to == ZoneType.Exile ? SoundEffectType.Exile : null;
    }
    @Override
    public SoundEffectType visit(final GameEventCardRegenerated event) { return SoundEffectType.Regen; }
    @Override
    public SoundEffectType visit(final GameEventCardSacrificed event) { return SoundEffectType.Sacrifice; }
    @Override
    public SoundEffectType visit(final GameEventCardCounters event) { return event.newValue > event.oldValue ? SoundEffectType.AddCounter : event.newValue < event.oldValue ? SoundEffectType.RemoveCounter : null; }
    @Override
    public SoundEffectType visit(final GameEventTurnEnded event) { return SoundEffectType.EndOfTurn; }
    @Override
    public SoundEffectType visit(final GameEventFlipCoin event) { return SoundEffectType.FlipCoin; }
    @Override
    public SoundEffectType visit(final GameEventPlayerLivesChanged event) { return event.newLives < event.oldLives ? SoundEffectType.LifeLoss : SoundEffectType.LifeGain; }
    @Override
    public SoundEffectType visit(final GameEventManaBurn event) { return SoundEffectType.ManaBurn; }
    @Override
    public SoundEffectType visit(final GameEventPlayerPoisoned event) { return SoundEffectType.Poison; }
    @Override
    public SoundEffectType visit(final GameEventShuffle event) { return SoundEffectType.Shuffle; }
    @Override
    public SoundEffectType visit(final GameEventTokenCreated event) { return SoundEffectType.Token; }
    @Override
    public SoundEffectType visit(final GameEventBlockersDeclared event) {
        final boolean isLocalHuman = event.defendingPlayer.getLobbyPlayer() == player;
        if (isLocalHuman) {
            return null; // already played sounds in interactive mode
        }

        for (final MapOfLists<Card, Card> ab : event.blockers.values()) {
            for(final Collection<Card> bb : ab.values()) {
                if ( !bb.isEmpty() ) {
                    // hasAnyBlocker = true;
                    return SoundEffectType.Block;
                }
            }
        }
        return null;
    }

    /**
     * Plays the sound corresponding to the outcome of the duel.
     */
    @Override
    public SoundEffectType visit(final GameEventGameOutcome event) {
        final boolean humanWonTheDuel = event.result.getWinningLobbyPlayer() == player;
        return humanWonTheDuel ? SoundEffectType.WinDuel : SoundEffectType.LoseDuel;
    }

    /**
     * Plays the sound corresponding to the card type/color when the card
     * ability resolves on the stack.
     */
    @Override
    public SoundEffectType visit(final GameEventSpellResolved evt) {
        if (evt.spell == null ) {
            return null;
        }

        final Card source = evt.spell.getHostCard();
        if (evt.spell.isSpell()) {
            // if there's a specific effect for this particular card, play it and
            // we're done.
            if (hasSpecificCardEffect(source)) {
                return SoundEffectType.ScriptedEffect;
            }

            if (source.isCreature() && source.isArtifact()) {
                return SoundEffectType.ArtifactCreature;
            } else if (source.isCreature()) {
                return SoundEffectType.Creature;
            } else if (source.isArtifact()) {
                return SoundEffectType.Artifact;
            } else if (source.isInstant()) {
                return SoundEffectType.Instant;
            } else if (source.isPlaneswalker()) {
                return SoundEffectType.Planeswalker;
            } else if (source.isSorcery()) {
                return SoundEffectType.Sorcery;
            } else if (source.isEnchantment()) {
                return SoundEffectType.Enchantment;
            }
        }
        return null;
    }

    /**
     * Plays the sound corresponding to the change of the card's tapped state
     * (when a card is tapped or untapped).
     *
     * @param tapped_state if true, the "tap" sound is played; otherwise, the
     * "untap" sound is played
     * @return the sound effect type
     */
    @Override
    public SoundEffectType visit(final GameEventCardTapped event) {
        return event.tapped ? SoundEffectType.Tap : SoundEffectType.Untap;
    }

    /**
     * Plays the sound corresponding to the land type when the land is played.
     *
     * @param land the land card that was played
     * @return the sound effect type
     */
    @Override
    public SoundEffectType visit(final GameEventLandPlayed event) {
        SoundEffectType resultSound = null;
        return resultSound;        
    }

    
    @Override
    public SoundEffectType visit(GameEventZone event) {
        Card card = event.card;
        ZoneType zoneTo = event.zoneType;
        EventValueChangeType zoneEventMode = event.mode;
        SoundEffectType resultSound = null;
        if(zoneEventMode == EventValueChangeType.Added && zoneTo == ZoneType.Battlefield) {
            if(card.isLand()) {
                resultSound = getLandSound(card);
            }
        }
        return resultSound;
    }

    private SoundEffectType getLandSound(Card land) {
        SoundEffectType resultSound = null;

        // if there's a specific effect for this particular card, play it and
        // we're done.
        if (hasSpecificCardEffect(land)) {
            resultSound = SoundEffectType.ScriptedEffect;
        } else {
            // I want to get all real colors this land can produce - no interest in colorless or devoid
            String fullManaColors = "";
            for (final SpellAbility sa : land.getManaAbilities()) {
                String currManaColor = sa.getManaPartRecursive().getOrigProduced();
                if(!"C".equals(currManaColor)) {
                    fullManaColors = fullManaColors + currManaColor;
                }
            }
            // No interest if "colors together" or "alternative colors" - only interested in colors themselves
            fullManaColors = fullManaColors.replaceAll("\\s", "");

            int fullManaColorsLength = fullManaColors.length();

            if(fullManaColorsLength >= 3) {
                // three color land
                fullManaColors = fullManaColors.substring(0,3);
                if (fullManaColors.contains("W") && fullManaColors.contains("U") && fullManaColors.contains("B") && SoundSystem.instance.hasResource(SoundEffectType.WhiteBlueBlackLand)) {
                    resultSound = SoundEffectType.WhiteBlueBlackLand;
                } else if (fullManaColors.contains("W") && fullManaColors.contains("G") && fullManaColors.contains("U") && SoundSystem.instance.hasResource(SoundEffectType.WhiteGreenBlueLand)) {
                    resultSound = SoundEffectType.WhiteGreenBlueLand;
                } else if (fullManaColors.contains("W") && fullManaColors.contains("R") && fullManaColors.contains("B") && SoundSystem.instance.hasResource(SoundEffectType.WhiteRedBlackLand)) {
                    resultSound = SoundEffectType.WhiteRedBlackLand;
                } else if (fullManaColors.contains("B") && fullManaColors.contains("W") && fullManaColors.contains("G") && SoundSystem.instance.hasResource(SoundEffectType.BlackWhiteGreenLand)) {
                    resultSound = SoundEffectType.BlackWhiteGreenLand;
                } else if (fullManaColors.contains("B") && fullManaColors.contains("R") && fullManaColors.contains("G") && SoundSystem.instance.hasResource(SoundEffectType.BlackRedGreenLand)) {
                    resultSound = SoundEffectType.BlackRedGreenLand;
                } else if (fullManaColors.contains("U") && fullManaColors.contains("B") && fullManaColors.contains("R") && SoundSystem.instance.hasResource(SoundEffectType.BlueBlackRedLand)) {
                    resultSound = SoundEffectType.BlueBlackRedLand;
                } else if (fullManaColors.contains("G") && fullManaColors.contains("U") && fullManaColors.contains("R") && SoundSystem.instance.hasResource(SoundEffectType.GreenBlueRedLand)) {
                    resultSound = SoundEffectType.GreenBlueRedLand;
                } else if (fullManaColors.contains("G") && fullManaColors.contains("B") && fullManaColors.contains("U") && SoundSystem.instance.hasResource(SoundEffectType.GreenBlackBlueLand)) {
                    resultSound = SoundEffectType.GreenBlackBlueLand;
                } else if (fullManaColors.contains("G") && fullManaColors.contains("R") && fullManaColors.contains("W")  && SoundSystem.instance.hasResource(SoundEffectType.GreenRedWhiteLand)) {
                    resultSound = SoundEffectType.GreenRedWhiteLand;
                } else if (fullManaColors.contains("R") && fullManaColors.contains("U") && fullManaColors.contains("W")  && SoundSystem.instance.hasResource(SoundEffectType.RedBlueWhiteLand)) {
                    resultSound = SoundEffectType.RedBlueWhiteLand;
                }
            }

            if(resultSound == null && fullManaColorsLength >= 2) {
                // three color land without sounds installed, or two color land
                // lets try
                fullManaColors = fullManaColors.substring(0,2);
                if (fullManaColors.contains("W") && (fullManaColors.contains("U")) && SoundSystem.instance.hasResource(SoundEffectType.WhiteBlueLand)) {
                    resultSound = SoundEffectType.WhiteBlueLand;
                } else if (fullManaColors.contains("W") && (fullManaColors.contains("G")) && SoundSystem.instance.hasResource(SoundEffectType.WhiteGreenLand)) {
                    resultSound = SoundEffectType.WhiteGreenLand;
                } else if (fullManaColors.contains("W") && (fullManaColors.contains("R")) && SoundSystem.instance.hasResource(SoundEffectType.WhiteRedLand)) {
                    resultSound = SoundEffectType.WhiteRedLand;
                } else if (fullManaColors.contains("B") && (fullManaColors.contains("W")) && SoundSystem.instance.hasResource(SoundEffectType.BlackWhiteLand)) {
                    resultSound = SoundEffectType.BlackWhiteLand;
                } else if (fullManaColors.contains("B") && (fullManaColors.contains("R")) && SoundSystem.instance.hasResource(SoundEffectType.BlackRedLand)) {
                    resultSound = SoundEffectType.BlackRedLand;
                } else if (fullManaColors.contains("U") && (fullManaColors.contains("B")) && SoundSystem.instance.hasResource(SoundEffectType.BlueBlackLand)) {
                    resultSound = SoundEffectType.BlueBlackLand;
                } else if (fullManaColors.contains("G") && (fullManaColors.contains("U")) && SoundSystem.instance.hasResource(SoundEffectType.GreenBlueLand)) {
                    resultSound = SoundEffectType.GreenBlueLand;
                } else if (fullManaColors.contains("G") && (fullManaColors.contains("B")) && SoundSystem.instance.hasResource(SoundEffectType.GreenBlackLand)) {
                    resultSound = SoundEffectType.GreenBlackLand;
                } else if (fullManaColors.contains("G") && (fullManaColors.contains("R")) && SoundSystem.instance.hasResource(SoundEffectType.GreenRedLand)) {
                    resultSound = SoundEffectType.GreenRedLand;
                } else if (fullManaColors.contains("R") && (fullManaColors.contains("U")) && SoundSystem.instance.hasResource(SoundEffectType.RedBlueLand)) {
                    resultSound = SoundEffectType.RedBlueLand;
                }
            }

            if(resultSound == null) {
                // multicolor land without sounds installed, or single mana land, or colorless/devoid land
                // in case of multicolor, lets take only the 1st color of the list, it sure has sound
                if(fullManaColorsLength >= 2) {
                    fullManaColors = fullManaColors.substring(0,1);                
                }
                if (fullManaColors.contains("B")) {
                    resultSound = SoundEffectType.BlackLand;
                } else if (fullManaColors.contains("U")) {
                    resultSound = SoundEffectType.BlueLand;
                } else if (fullManaColors.contains("G")) {
                    resultSound = SoundEffectType.GreenLand;
                } else if (fullManaColors.contains("R")) {
                    resultSound = SoundEffectType.RedLand;
                } else if (fullManaColors.contains("W")) {
                    resultSound = SoundEffectType.WhiteLand;
                } else {
                    resultSound = SoundEffectType.OtherLand;
                }
            }
        }

        return resultSound;
    }

    /**
     * Play a specific sound effect based on card's name.
     *
     * @param c the card to play the sound effect for.
     * @return the sound effect type
     */
    private static boolean hasSpecificCardEffect(final Card c) {
        // Implement sound effects for specific cards here, if necessary.
        String effect = "";
        if (null != c) {
            effect = c.getSVar("SoundEffect");
        }
        return effect.isEmpty() ? false : true;
    }


    /**
     * Returns the value of the SoundEffect SVar of the card that triggered
     * the event, otherwise returns an empty string.
     *
     * @param evt the event which is the source of the sound effect
     * @return a string containing the SoundEffect SVar, or empty string if
     * SVar:SoundEffect does not exist.
     */
    public String getScriptedSoundEffectName(final GameEvent evt) {
        Card c = null;

        if (evt instanceof GameEventSpellResolved) {
            c = ((GameEventSpellResolved) evt).spell.getHostCard();
        } else if (evt instanceof GameEventLandPlayed) {
            c = ((GameEventLandPlayed) evt).land;
        }

        return c != null ? c.getSVar("SoundEffect") : "";
    }


    @Override
    public SoundEffectType visit(final UiEventBlockerAssigned event) {
        return event.attackerBeingBlocked == null ? null : SoundEffectType.Block;
    }
    @Override
    public SoundEffectType visit(final UiEventAttackerDeclared event) {
        return null;
    }
    @Override
    public SoundEffectType visit(final UiEventNextGameDecision event) {
        return null;
    }
    @Override
    public SoundEffectType visit(final GameEventCardPhased event) {
        return SoundEffectType.Phasing;
    }
}