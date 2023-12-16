package xzero.model;

import xzero.model.navigation.Direction;

import java.awt.*;
import java.util.ArrayList;
import xzero.model.factory.LabelFactory;
import java.util.List;

public class MagicCombinationCross extends MagicCombination{

    private final ArrayList<Player> _playerList;

    MagicCombinationCross(ArrayList<Player> playerList){
        _playerList = playerList;
        _combinationName = "КРЕСТ";
    }

    /**
     * Чтобы не было цикличного применения волшебных комбинаций, проверяем только комбинации, которые могли
     * образоваться в результате последнего хода игрока.
     * Может быть всего 5 вариантов где находится искомая комбинация:
     * - текущая метка стала в центр креста
     * - текущая метка стала правым концом
     * - текущая метка стала левым концом
     * - текущая метка стала верхним концом
     * - текущая метка стала нижним концом
     */
    public boolean findMagicCombination(GameField field, Point pos) {
        _field = field;

        return applyMagicCombination(pos)
                || applyMagicCombination(Direction.west().shift().nextPoint(pos))
                || applyMagicCombination(Direction.east().shift().nextPoint(pos))
                || applyMagicCombination(Direction.north().shift().nextPoint(pos))
                || applyMagicCombination(Direction.south().shift().nextPoint(pos));
    }

    @Override
    public boolean applyMagicCombination(Point pos) {
        return applyMagicCombination(pos, _playerList.get(0))
                || applyMagicCombination(pos, _playerList.get(1));
    }
    public boolean applyMagicCombination(Point pos, Player player) {
        List<Cell> corners = findCrossCorners(pos, player);

        if (corners.isEmpty()) {
            return false;
        }

        int changes = 0;
        for (Cell cell : corners) {
            Label label = cell.label();

            if (label != null) {
                if (label.belongsTo(player)) {
                    continue;
                }
                cell.unsetLabel();
            }

            LabelFactory _labelFactory = new LabelFactory();
            label = _labelFactory.createLabel();
            label.setPlayer(player);
            cell.setLabel(label);

            changes++;
        }

        return changes > 0;
    }

    public List<Cell> findCrossCorners(Point center, Player player) {
        List<Cell> corners = new ArrayList<>();

        boolean crossExists = _field.hasLabel(center, player)
                && _field.hasLabel(Direction.west().shift().nextPoint(center), player)
                && _field.hasLabel(Direction.east().shift().nextPoint(center), player)
                && _field.hasLabel(Direction.north().shift().nextPoint(center), player)
                && _field.hasLabel(Direction.south().shift().nextPoint(center), player);

        //Если крест существует - добавить в список оставшиеся ячейки квадрата, в котором есть крест
        if (crossExists) {
            corners.add(_field.cell(Direction.northWest().shift().nextPoint(center)));
            corners.add(_field.cell(Direction.southWest().shift().nextPoint(center)));
            corners.add(_field.cell(Direction.northEast().shift().nextPoint(center)));
            corners.add(_field.cell(Direction.southEast().shift().nextPoint(center)));
        }

        return corners;
    }
}
