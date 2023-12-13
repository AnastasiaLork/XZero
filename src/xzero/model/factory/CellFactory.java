package xzero.model.factory;

import xzero.model.Cell;

/**
 * Фабрика, порождающая возможные виды ячеек. Реализует самую простую стратегию
 */
public class CellFactory {
    
    public Cell createCell(){
       return new Cell(); 
    }
}
