package xzero.model;

/**
 * Метка, которую можно поместить на поле
 */
public class Label {
    private final boolean isNeutral; //показатель нейтральной метки

    public boolean getIsNeutral()
    {
        return isNeutral;
    }
    public Label(boolean isNeutral) {
        this.isNeutral = isNeutral;
    }

    public Label() {
        this(false);
    }
// --------------- Ячейка, которой прнадлежит метка. Задает сама ячейка -------
    private Cell _cell;
    private boolean cellIsReceived = false;

    void setCell(Cell cell) {
        if(_cell == null) {
            _cell = cell;
            cellIsReceived = true;
            if (_cell != null) {
                _cell.setLabel(this);
            }
        }
    }
    void unsetCell() {
        if (cellIsReceived) {
            cellIsReceived = false;
            _cell.unsetLabel();
            _cell = null;
        }
    }
    public Cell cell() {
        return _cell;
    }
    
// - Игрок, которому прнадлежит метка. Метка может быть нейтральной (не принадлежать никому) -
    
    private Player _player = null;

    /** Получить владельца метки: если она нейтрально - владельца нет, иначе получить игрока */
    public Player getOwner() {
        return isNeutral ? null : _player;
    }
    void setPlayer(Player p){
        _player = p;
    }

    void unsetPlayer(){
        _player = null;
    }
    
    public Player player(){
        return _player;
    }


    /** Принадлежит ли метка заданному игроку (также может быть общей) */
    public boolean belongsTo(Player player) {
        return isNeutral || (_player != null && _player.equals(player));
    }
}
