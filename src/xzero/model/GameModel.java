package xzero.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import xzero.model.events.*;
import xzero.model.factory.CellFactory;
import xzero.model.factory.LabelFactory;
import xzero.model.navigation.Direction;

/**
/* Aбстракция всей игры; генерирует стартовую обстановку; поочередно передает 
* ход игрокам, задавая им метку для установки на поле; следит за игроками с 
* целью определения конца игры
*/
public class GameModel {
    
// -------------------------------- Поле -------------------------------------

    private GameField _field = new GameField();
    public GameField field(){
        return _field;
    }


    
// -------------------------------- Игроки -----------------------------------
    
    private ArrayList<Player> _playerList = new ArrayList<>();
    private int _activePlayer;

    public Player activePlayer(){
        return _playerList.get(_activePlayer);
    }
    
    public GameModel(){

        // Задаем размеры поля по умолчанию
        field().setSize(5, 5);
        
        // Создаем двух игроков
        Player p;
        PlayerObserver observer = new PlayerObserver();
        MagicCrossObserver crossObserver = new MagicCrossObserver();
        MagicArrowObserver arrowObserver = new MagicArrowObserver();
        
        p = new Player(field(), "X");
        //"Следим" за игроком
        p.addPlayerActionListener(crossObserver);
        p.addPlayerActionListener(arrowObserver);
        p.addPlayerActionListener(observer);

        _playerList.add(p);
        _activePlayer = 0;
        
        p = new Player(field(), "O");
        //"Следим" за игроком
        p.addPlayerActionListener(crossObserver);
        p.addPlayerActionListener(arrowObserver);
        p.addPlayerActionListener(observer);

        _playerList.add(p);
    }
    
// ---------------------- Порождение обстановки на поле ---------------------
    
    private CellFactory _cellFactory = new CellFactory();
    
    private void generateField(){

        field().clear();
        field().setSize(5, 5);
        for(int row = 1; row <= field().height(); row++)
        {
            for(int col = 1; col <= field().width(); col++)
            {
                field().setCell(new Point(col, row), _cellFactory.createCell());           
            }
        }
    }

// ----------------------------- Игровой процесс ----------------------------
    
    public void start(){

        for (Player player : _playerList) {
            player.setAbilityTodDeleteLabel();
        }

        // Генерируем поле
        generateField();
        
        // Передаем ход первому игроку
        _activePlayer = _playerList.size()-1;
        exchangePlayer();
    }

    private LabelFactory _labelFactory = new LabelFactory();

    private void exchangePlayer(){

        // Сменить игрока
        _activePlayer++;
        if(_activePlayer >= _playerList.size())     _activePlayer = 0;
        
        // Выбрать для него метку
        Label newLabel = _labelFactory.createLabel();
        activePlayer().setActiveLabel(newLabel);
        
        // Генерируем событие
        firePlayerExchanged(activePlayer());
    }
    
    
    private static int WINNER_LINE_LENGTH = 5;
    
    private Player determineWinner(){
    
        for(int row = 1; row <= field().height(); row++)
        {
            for(int col = 1; col <= field().width(); col++)
            {
                Point pos = new Point(col, row);
                Direction direct = Direction.north();
                for(int  i = 1; i <= 8; i++)
                {
                    direct = direct.rightword();

                    List<Label> line = field().labelLine(pos, direct);

                    if(line.size() >= WINNER_LINE_LENGTH)
                    {
                        Player winner = line.get(0).getOwner();

                        return winner != null ? winner : activePlayer();
                    }
                }
            }
        }
        
        return null;
    }

    // ------------------------- Реагируем на действия игрока ------------------

    private class PlayerObserver implements PlayerActionListener{

        @Override
        public void labelisPlaced(PlayerActionEvent e) {
            
            //  Транслируем событие дальше для активного игрока
            if(e.player() == activePlayer()){
                fireLabelIsPlaced(e);
            }

            // Определяем победителя
            Player winner = determineWinner();
            
            // Меняем игрока, если игра продолжается
            if(winner == null)
            {
                exchangePlayer();
            }
            else
            { 
                // Генерируем событие
                fireGameFinished(winner);
            }
        }

        @Override
        public void labelIsReceived(PlayerActionEvent e) {
            //  Транслируем событие дальше для активного игрока
            if(e.player() == activePlayer()){
                fireLabelIsRecived(e);
            }
        }
    }

    private class MagicCrossObserver implements PlayerActionListener {

        /**
         * Чтобы не было цикличного применения волшебных комбинаций, проверяем только комбинации, которые могли
         * образоваться в результате последнего хода игрока.
         * Может быть всего 5 вариантов где находится искомая комбинация:
         * - текущая метка стала в центр креста
         * - текущая метка стала правым концом
         * - текущая метка стала левым концом
         * - текущая метка стала верхним концом
         * - текущая метка стала нижним концом
         * @param e событие
         */
        @Override
        public void labelisPlaced(PlayerActionEvent e) {
            if (e.player() != activePlayer()) {
                return;
            }

            Point current = e.label().cell().position();

            boolean applied = applyCombination(current)
                    || applyCombination(Direction.west().shift().nextPoint(current))
                    || applyCombination(Direction.east().shift().nextPoint(current))
                    || applyCombination(Direction.north().shift().nextPoint(current))
                    || applyCombination(Direction.south().shift().nextPoint(current));

            if (applied) {
                fireMagicCombination(e.player(), "КРЕСТ");
            }
        }

        @Override
        public void labelIsReceived(PlayerActionEvent e) {
        }

        private boolean applyCombination(Point pos) {
            List<Cell> corners = field().findCrossCorners(pos, activePlayer());

            if (corners.isEmpty()) {
                return false;
            }

            int changes = 0;
            for (Cell cell : corners) {
                Label label = cell.label();

                if (label != null) {
                    if (label.belongsTo(activePlayer())) {
                        continue;
                    }
                    cell.unsetLabel();
                }

                label = _labelFactory.createLabel();
                label.setPlayer(activePlayer());
                cell.setLabel(label);

                changes++;
            }

            return changes > 0;
        }
    }

    private class MagicArrowObserver implements PlayerActionListener {
        /**
         * По отношению к позиции в которую установлена метка на последнем ходу, может быть всего 3 варианта стрелки:
         * - текущая позиция является вершиной
         * - текущая позиция является левой стороной
         * - текущая позиция является правой стороной
         *
         * @param e событие
         */
        @Override
        public void labelisPlaced(PlayerActionEvent e) {
            if (e.player() != activePlayer()) {
                return;
            }

            Point current = e.label().cell().position();

            boolean applied = applyCombination(e, current)
                    || applyCombination(e, Direction.northWest().shift().nextPoint(current))
                    || applyCombination(e, Direction.northEast().shift().nextPoint(current));

            if (applied) {
                fireMagicCombination(e.player(), "СТРЕЛКА ВВЕРХ");
            }
        }

        @Override
        public void labelIsReceived(PlayerActionEvent e) {
        }

        private boolean applyCombination(PlayerActionEvent e, Point pos) {
            MagicArrow arrow = field().findMagicArrow(pos);

            if (arrow == null) {
                return false;
            }

            arrow.replaceableCell().unsetLabel();

            Label label = _labelFactory.createLabel();
            label.setPlayer(arrow.winner());

            arrow.replaceableCell().setLabel(label);

            // Если заменена только что установленная метка.
            if (e.label().cell() == null) {
                e.setLabel(label);
            }

            return true;
        }
    }

// ------------------------ Порождает события игры ----------------------------
    
    // Список слушателей
    private ArrayList _listenerList = new ArrayList(); 
 
    // Присоединяет слушателя
    public void addGameListener(GameListener l) { 
        _listenerList.add(l); 
    }
    
    // Отсоединяет слушателя
    public void removeGameListener(GameListener l) { 
        _listenerList.remove(l); 
    } 
    
    // Оповещает слушателей о событии
    protected void fireGameFinished(Player winner) {
        
        GameEvent event = new GameEvent(this);
        event.setPlayer(winner);
        for (Object listner : _listenerList)
        {
            ((GameListener)listner).gameFinished(event);
        }
    }     

    protected void firePlayerExchanged(Player p) {
        
        GameEvent event = new GameEvent(this);
        event.setPlayer(p);
        for (Object listner : _listenerList)
        {
            ((GameListener)listner).playerExchanged(event);
        }
    }

    protected void fireMagicCombination(Player p, String combination) {
        MagicCombinationEvent event = new MagicCombinationEvent(this, p, combination);

        for (Object listener : _listenerList)
        {
            ((GameListener)listener).magicCombinationProcessed(event);
        }
    }

// --------------------- Порождает события, связанные с игроками -------------
    
    // Список слушателей
    private ArrayList _playerListenerList = new ArrayList(); 
 
    // Присоединяет слушателя
    public void addPlayerActionListener(PlayerActionListener l) { 
        _playerListenerList.add(l); 
    }
    
    // Отсоединяет слушателя
    public void removePlayerActionListener(PlayerActionListener l) { 
        _playerListenerList.remove(l); 
    } 
    
    // Оповещает слушателей о событии
    protected void fireLabelIsPlaced(PlayerActionEvent e) {
        
        for (Object listner : _playerListenerList)
        {
            ((PlayerActionListener)listner).labelisPlaced(e);
        }
    }     
    
    protected void fireLabelIsRecived(PlayerActionEvent e) {
        
        for (Object listner : _playerListenerList)
        {
            ((PlayerActionListener)listner).labelIsReceived(e);
        }
    }         
}
