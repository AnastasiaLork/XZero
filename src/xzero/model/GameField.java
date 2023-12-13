package xzero.model;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import xzero.model.navigation.Direction;
import xzero.model.navigation.Shift;

/**
 *  Прямоугольное поле, состоящее из ячеек
 */
public class GameField {
    
// ----------------- Контейнер ячеек, а в конечном счете, и меток ---------------
// Позиции ячеек и меток  задаются строками и столбцами в диапазоне [1, height] и
// [1, width] соответсвенно
    
// ------------------------------ Ячейки ---------------------------------------    
    private ArrayList<Cell> _cellPool = new ArrayList(); 
    
    Cell cell(Point pos){

        for(Cell obj : _cellPool)
        {
            if(obj.position().equals(pos))     
            { return obj; }
        }
            
        return null;
    }
    
    void setCell(Point pos, Cell cell){
        // Удаляем старую ячейку
        removeCell(pos);
        
        // Связываем ячейку с полем
        cell.setField(this);
        cell.setPosition(pos);
        
        // Добавляем новую ячейку
        _cellPool.add(cell);
    }

    public List<Cell> cells(){
        return Collections.unmodifiableList(_cellPool);
    }
    
    public void clear(){
        _cellPool.clear();
    }    
    
    private void removeCell(Point pos){
        
        Cell obj = cell(pos);
        if(obj != null)     _cellPool.remove(obj);
    }
    
// ------------------------------ Метки ---------------------------------------    
    
    public Label label(Point pos){
        
        Cell obj = cell(pos);
        if(obj != null)     return obj.label();
            
        return null;
    }

    public Label label(Point pos, Direction direction) {
        Point next = direction.shift().nextPoint(pos);

        return label(next);
    }
    public void setLabel(Point pos, Label label){
        Cell obj = cell(pos);
        if(obj != null) {
            obj.setLabel(label);
        }
    }

    public boolean hasLabel(Point pos, Player player) {
        Label label = label(pos);

        return label != null && label.belongsTo(player);
    }

    private ArrayList<Label> _labelPool = new ArrayList(); 
    
    public List<Label> labels(){
        _labelPool.clear();
        
        for(Cell obj : _cellPool)
        { 
            Label l = obj.label();
            if(l != null)
            {
                _labelPool.add(obj.label()); 
            }
        }
            
        return Collections.unmodifiableList(_labelPool);
    }
    
    // Возвращает ряд меток, принадлежащих одному игроку
    public List<Label> labelLine(Point start, Direction direct){
        
        ArrayList<Label> line = new ArrayList<>();
        Player startPlayer = null;
        
        // Определяем первую метку и соответствующего ей игрока
        Point pos = new Point(start);
        Label l = label(pos);

        boolean isLineFinished = (l == null);
        if(!isLineFinished)
        {
            line.add(l);
            startPlayer = line.get(0).player();
        }

        Shift shift = direct.shift();
        pos.translate(shift.byHorizontal(), shift.byVertical());
        while(!isLineFinished && containsRange(pos))
        {
            l = label(pos);
            isLineFinished = (l == null || !l.belongsTo(startPlayer));
            
            if(!isLineFinished)
            {
                line.add(l); 
            }

            pos.translate(shift.byHorizontal(), shift.byVertical());
        }
        
        return line;
    }

    public List<Cell> findCrossCorners(Point center, Player player) {
        List<Cell> corners = new ArrayList<>();

        boolean crossExists = hasLabel(center, player)
                && hasLabel(Direction.west().shift().nextPoint(center), player)
                && hasLabel(Direction.east().shift().nextPoint(center), player)
                && hasLabel(Direction.north().shift().nextPoint(center), player)
                && hasLabel(Direction.south().shift().nextPoint(center), player);

        if (crossExists) {
            corners.add(cell(Direction.northWest().shift().nextPoint(center)));
            corners.add(cell(Direction.southWest().shift().nextPoint(center)));
            corners.add(cell(Direction.northEast().shift().nextPoint(center)));
            corners.add(cell(Direction.southEast().shift().nextPoint(center)));
        }

        return corners;
    }

    public MagicArrow findMagicArrow(Point topPos) {
        Label top = label(topPos);
        Label left = label(Direction.southWest().shift().nextPoint(topPos));
        Label right = label(Direction.southEast().shift().nextPoint(topPos));

        if (top == null || left == null || right == null) {
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

    // ----------------------- Ширина и высота поля ------------------------------
    private int _width;
    private int _height;

    public void setSize(int width, int height) {
 
        _width = width;
        _height = height;
        
        // Удаляем все ячейки находящиеся вне поля
        for (Cell obj : _cellPool)
        {
            if(!containsRange(obj.position()) )  
            { 
                _cellPool.remove(obj);
            }
        }

    }

    public int width() {
        return _width;
    }

    public int height() {
        return _height;
    }
    
    public boolean containsRange(Point p){
        return p.getX() >= 1 && p.getX() <= _width &&
               p.getY() >= 1 && p.getY() <= _height ;
    }
    
// ----------------------------------------------------------------------------    
    public GameField() {
        
        setSize(10, 10);
    }
}
