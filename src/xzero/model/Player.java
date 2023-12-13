package xzero.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;

/**
 *  Игрок, который размещает предложенную ему метку
 */
public class Player {
    
// --------------------------------- Имя игрока -------------------------------    
    private String _name;
    private boolean abilityTodDeleteLabel = true;

    void setAbilityTodDeleteLabel(){
        abilityTodDeleteLabel = true;
    }

    public void setName(String name) {
        _name = name;
    }
    
    public String name() {
        return _name;
    }

 // ----------------------- Устанавливаем связь с полем -----------------------   
    GameField _field;
    
    public Player (GameField field, String name) {
        _field = field;
        _name = name;
    }
    
// ---------------------- Метка, которую нужно установить -----------------------    
    Label _label;
    
    public void setActiveLabel(Label l) {
        _label = l;
        _label.setPlayer(this);
        
        // Генерируем событие
        fireLabelIsReceived(_label);
    }

    public Label activeLabel() {
        return _label;
    }
    
    public void setLabelTo(Point pos){
        
        // породить исключение, если не задана активная метка
        if(activeLabel() == null){
            throw new IllegalStateException("Active label is not set");
        }


        Label current = _field.label(pos);
        if (current != null) {
            if (current.getOwner() != this && abilityTodDeleteLabel) {
                Cell cell = _field.cell(pos);
                cell.unsetLabel();
                cell.setLabel(_label);
                abilityTodDeleteLabel = false;
            } else {
                return;
            }
        }
        else
        {
            _field.setLabel(pos, _label);
        }

        // Генерируем событие
        fireLabelIsPlaced(_label);

        // Повторно использовать метку нельзя
        _label = null;
    }
    
    private ArrayList<Label> _labels = new ArrayList<>();
    
    public List<Label> labels(){
        
        _labels.clear();
        for(Label obj: _field.labels())
        {
            if(obj.player().equals(this))
            { _labels.add(obj); }
        }
        
        return Collections.unmodifiableList(_labels);
    }   
    
    // ---------------------- Порождает события -----------------------------

    private ArrayList<PlayerActionListener> _actionListeners = new ArrayList<>();
 
    // Присоединяет слушателя
    public void addPlayerActionListener(PlayerActionListener l) {
        _actionListeners.add(l);
    }
    
    // Отсоединяет слушателя
    public void removePlayerActionListener(PlayerActionListener l) {
        _actionListeners.remove(l);
    } 
    
    // Оповещает слушателей о событии
    protected void fireLabelIsPlaced(Label l) {
        PlayerActionEvent event = new PlayerActionEvent(this);
        event.setLabel(l);
        event.setPlayer(this);
        for (PlayerActionListener listener : _actionListeners) {
            listener.labelisPlaced(event);
        }
    }     

    protected void fireLabelIsReceived(Label l) {
        PlayerActionEvent event = new PlayerActionEvent(this);
        event.setLabel(l);
        event.setPlayer(this);
        for (PlayerActionListener listener : _actionListeners) {
            listener.labelIsReceived(event);
        }
    }
}
