package xzero.model.navigation;
import java.awt.*;
/**
 * Смещение в декартовой системе координат
 */
public class Shift {
    
    private int _horizontal;
    private int _vertical;

    public Shift(int horiz, int vert){
        _horizontal = horiz;
        _vertical = vert;
    }    
        
    public int byHorizontal(){
        return _horizontal;
    }
    
    public int byVertical(){
        return _vertical;
    }

    public Point nextPoint(Point pos) {
        Point next = new Point(pos);
        next.translate(byHorizontal(), byVertical());

        return next;
    }
}
