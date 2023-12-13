package xzero.model;

import java.awt.Point;
import java.util.ArrayList;

/*
 * Ячейка, являющаяся составной частью поля и содержащая в себе метку
 */
public class Cell {
    
// --------------------- Позиция метки -----------------------
    private Point _position;
    
    void setPosition(Point pos){
        _position = pos;
    }

    public Point position(){
        return (Point)_position.clone();
    }
    
// --------- Метка, принадлежит полю. Принадлежность задает само поле ------
    private GameField _field;
    
    void setField(GameField f){
        _field = f;
    }
    
// --------------------- Метка, принадлежащая ячейке -----------------------
    private Label _label;
    public void setLabel(Label label) {
       if(isEmpty())
       {
           _label = label;
           if (!isEmpty()) {
               _label.setCell(this);
           }

       }
    }
    void unsetLabel(){
        if(!isEmpty()){
            _label.unsetCell();
            _label = null;
        }
    }
    public Label label() {
        return _label;
    }

    public boolean isEmpty(){
        return _label == null;
    }

}
