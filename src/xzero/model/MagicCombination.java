package xzero.model;

import java.awt.*;

public abstract class MagicCombination {

    String _combinationName;
    GameField _field;

    /**
     * Поиск волшебной комбинации
     * return - булевое значение - найдена комбинация или нет
     */
    public abstract boolean findMagicCombination (GameField field, Point pos);

    /**
     * Преобразовать поле при волшебной комбинации
     * return - булевое значение - найдена комбинация или нет
     */
    public abstract boolean applyMagicCombination(Point pos);

}
