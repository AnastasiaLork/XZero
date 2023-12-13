package xzero.model;

public class MagicArrow {
    private final Player _winner;
    private final Cell _replaceableCell;

    public MagicArrow(Player winner, Cell replaceableCell) {
        this._winner = winner;
        this._replaceableCell = replaceableCell;
    }

    public Cell replaceableCell() {
        return _replaceableCell;
    }

    public Player winner() {
        return _winner;
    }
}
