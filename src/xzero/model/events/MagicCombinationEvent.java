package xzero.model.events;

import xzero.model.Player;

public class MagicCombinationEvent extends GameEvent {
    private final String _combination;

    public String combination() {
        return _combination;
    }

    public MagicCombinationEvent(Object source, Player player, String combination) {
        super(source);
        setPlayer(player);

        this._combination = combination;
    }
}
