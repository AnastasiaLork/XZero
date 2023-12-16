package xzero.model;

import xzero.model.navigation.Direction;
import xzero.model.factory.LabelFactory;

import java.awt.*;

public class MagicCombinationArrow extends MagicCombination{

    MagicCombinationArrow(){
        _combinationName = "СТРЕЛКА ВВЕРХ";
    }

    /**
     * По отношению к позиции в которую установлена метка на последнем ходу, может быть всего 3 варианта стрелки:
     * - текущая позиция является вершиной
     * - текущая позиция является левой стороной
     * - текущая позиция является правой стороной
     */
    public boolean findMagicCombination (GameField field, Point pos) {
        _field = field;

        return applyMagicCombination(pos)
                || applyMagicCombination(Direction.northWest().shift().nextPoint(pos))
                || applyMagicCombination(Direction.northEast().shift().nextPoint(pos));
    }

    @Override
    public boolean applyMagicCombination(Point pos) {
        MagicArrow arrow = findMagicArrow(pos);

        if (arrow == null) {
            return false;
        }

        arrow.replaceableCell().unsetLabel();

        LabelFactory _labelFactory = new LabelFactory();
        Label label = _labelFactory.createLabel();
        label.setPlayer(arrow.winner());

        arrow.replaceableCell().setLabel(label);

        // Если заменена только что установленная метка.
        //if (e.label().cell() == null) {
          //  e.setLabel(label);
        //}

        return true;
    }

    public MagicArrow findMagicArrow(Point topPos) {
        Label top = _field.label(topPos);
        Label left = _field.label(Direction.southWest().shift().nextPoint(topPos));
        Label right = _field.label(Direction.southEast().shift().nextPoint(topPos));

        if (top == null || left == null || right == null) {
            return null;
        }

        if (top.getIsNeutral() || left.getIsNeutral() || right.getIsNeutral()) {
            return null;
        }

        if (left.belongsTo(top.player()) && right.belongsTo(top.player())) {
            return null;
        }

        if (left.belongsTo(top.player())) {
            return new MagicArrow(top.player(), right.cell());
        }

        if (right.belongsTo(top.player())) {
            return new MagicArrow(top.player(), left.cell());
        }

        return new MagicArrow(left.player(), top.cell());
    }

    public static class MagicArrow {
        private final Player _winner;
        private final Cell _replaceableCell; //ячейка, метка в которой будет меняться

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
}
