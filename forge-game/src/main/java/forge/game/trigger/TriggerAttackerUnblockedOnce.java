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
package forge.game.trigger;

import forge.game.GameEntity;
import forge.game.card.Card;
import forge.game.spellability.SpellAbility;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Trigger_AttackerUnblockedOnce class.
 * </p>
 * 
 * @author Forge
 * @version $Id$
 */
public class TriggerAttackerUnblockedOnce extends Trigger {

    /**
     * <p>
     * Constructor for Trigger_AttackerUnblocked.
     * </p>
     * 
     * @param params
     *            a {@link java.util.HashMap} object.
     * @param host
     *            a {@link forge.game.card.Card} object.
     * @param intrinsic
     *            the intrinsic
     */
    public TriggerAttackerUnblockedOnce(final java.util.Map<String, String> params, final Card host, final boolean intrinsic) {
        super(params, host, intrinsic);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public final boolean performTest(final Map<String, Object> runParams2) {
        if (hasParam("ValidDefenders")) {
            boolean valid = false;

            final List<GameEntity> srcs = (List<GameEntity>) runParams2.get("Defenders");
            for (GameEntity c : srcs) {
                if (c.isValid(this.mapParams.get("ValidDefenders").split(","), this.getHostCard().getController(), this.getHostCard(), null)) {
                    valid = true;
                }
            }
            if (!valid) {
                return false;
            }
            /*
            if (hasParam("ValidAttackers")) {
                // should be updated if a creature of a specific type attackes a defender
            }
            */
        }
        if (hasParam("ValidAttackingPlayer")) {
            if (!matchesValid(runParams2.get("AttackingPlayer"),
                    this.mapParams.get("ValidAttackingPlayer").split(","), this.getHostCard())) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public final void setTriggeringObjects(final SpellAbility sa) {
        sa.setTriggeringObject("AttackingPlayer", this.getRunParams().get("AttackingPlayer"));
        sa.setTriggeringObject("Defenders", this.getRunParams().get("Defenders"));
    }

    @Override
    public String getImportantStackObjects(SpellAbility sa) {
        StringBuilder sb = new StringBuilder();
        sb.append("AttackingPlayer: ").append(sa.getTriggeringObject("AttackingPlayer"));
        sb.append("Defenders: ").append(sa.getTriggeringObject("Defenders"));
        return sb.toString();
    }
}
