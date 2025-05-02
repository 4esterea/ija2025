package ija2025;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.*;

public class GameManager {

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private Difficulty difficulty;
    private int gridSize;
    private GameNode[][] grid;
    private PowerNode _powerNode;
    private List<LightBulbNode> lightBulbNodes;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Random random;
    private Map<String, Integer> originalRotations = new HashMap<>();
    private GameLogger gameLogger;
    private boolean loggingEnabled = false;

    private double cellSize;

    public GameManager(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.lightBulbNodes = new ArrayList<>();
        this.random = new Random();
        this.gameLogger = new GameLogger();

        // Установка размера сетки в зависимости от сложности
        switch (difficulty) {
            case EASY:
                gridSize = 5;
                break;
            case MEDIUM:
                gridSize = 7;
                break;
            case HARD:
                gridSize = 10;
                break;
            default:
                gridSize = 5;
        }

        // Инициализация сетки
        grid = new GameNode[gridSize][gridSize];

        // Устанавливаем базовый размер ячейки (будет изменен при инициализации игры)
        cellSize = 50;
    }

    public double getCellSize() {
        return cellSize;
    }
    public GameNode[][] getGrid() {
        return grid;
    }
    public GameLogger getGameLogger() {
        return gameLogger;
    }
    public void enableLogging() {
        if (gameLogger != null) {
            loggingEnabled = true;
            gameLogger.setLoggingEnabled(true);
        }
    }

    public void disableLogging() {
        loggingEnabled = false;
        if (gameLogger != null) {
            gameLogger.setLoggingEnabled(false);
        }
    }
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
        if (gameLogger != null) {
            gameLogger.setLoggingEnabled(enabled);
        }
    }

    private GameController gameController;

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    public void generateGameBoard() {
        disableLogging();
        placePowerNode();

        boolean hasEmptyCell = true;

        while (hasEmptyCell) {
            // Проверяем, есть ли свободные клетки на поле
            hasEmptyCell = false;
            int emptyRow = -1;
            int emptyCol = -1;

            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    if (grid[row][col] == null) {
                        hasEmptyCell = true;
                        emptyRow = row;
                        emptyCol = col;
                        break;
                    }
                }
                if (hasEmptyCell) break;
            }

            // Если нашли пустую клетку, размещаем лампочку
            if (hasEmptyCell) {
                LightBulbNode node = placeLightBulbNode();
                connectToPower(node);
            }
        }
        List<WireNode> wiresToReplace = checkDisconnectedWires();
        while (!wiresToReplace.isEmpty()) {
            replaceDisconnectedWires(wiresToReplace);
            wiresToReplace = checkDisconnectedWires();
        }
        checkAllLightBulbsConnected();
        finalizePowerNodeConnections();
        saveOriginalNodePositions();
        shuffleAllNodes();
        updatePowerFlow();
        gameLogger.logInitialState(this);
        enableLogging();
    }

    private void shuffleAllNodes(){

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null) {
                    GameNode node = grid[row][col];
                    if (node instanceof PowerNode) {
                        continue;
                    }
                    int rotations = random.nextInt(4);
                    for (int i = 0; i < rotations; i++) {
                        node.rotate();
                    }
                }
            }
        }
    }

    private void saveOriginalNodePositions() {
        originalRotations.clear();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                GameNode node = grid[row][col];
                if (node != null) {
                    String key = row + "," + col;
                    originalRotations.put(key, node.getRotation());
                }
            }
        }
    }

    public int getRotationsToOriginal(int row, int col) {
        String key = row + "," + col;
        GameNode node = grid[row][col];

        if (node == null || !originalRotations.containsKey(key)) {
            return 0;
        }

        int currentRotation = node.getRotation();
        int originalRotation = originalRotations.get(key);

        if (currentRotation == originalRotation) {
            return 0;
        }

        // Вычисляем количество поворотов на 90 градусов (0-3)
        int rotationsNeeded = (4 + (originalRotation - currentRotation) / 90) % 4;
        // Проверяем, является ли узел проводом и применяем специальные правила
        if (node instanceof WireNode) {
            WireNode wire = (WireNode) node;
            int connections = wire.getConnectedDirections().size();

            // Для провода с 4 концами не нужно вращение
            if (connections == 4) {
                return 0;
            }

            // Для провода с 2 концами достаточно проверки на поворот на 180°
            if (connections == 2) {
                // Получаем список направлений
                Set<WireNode.Direction> directions = wire.getConnectedDirections();

                // Проверяем, является ли провод прямым (I-образным)
                boolean isIType = (directions.contains(WireNode.Direction.UP) && directions.contains(WireNode.Direction.DOWN)) ||
                        (directions.contains(WireNode.Direction.LEFT) && directions.contains(WireNode.Direction.RIGHT));

                if (isIType) {
                    // Для I-образного провода важно только 2 состояния (0° и 90°)
                    return rotationsNeeded % 2;
                } else {
                    // Для L-образного провода важны все 4 состояния
                    return rotationsNeeded;
                }
            }
        }
        return rotationsNeeded;
    }

    public void finalizePowerNodeConnections() {
        int powerRow = _powerNode.getRow();
        int powerCol = _powerNode.getCol();

        // Сохраняем активные направления источника питания
        Set<WireNode.Direction> activeDirections = new HashSet<>();

        // Проверяем все четыре возможных направления
        if (isValidConnection(powerRow - 1, powerCol)) { // Вверх
            activeDirections.add(WireNode.Direction.UP);
        }
        if (isValidConnection(powerRow, powerCol + 1)) { // Вправо
            activeDirections.add(WireNode.Direction.RIGHT);
        }
        if (isValidConnection(powerRow + 1, powerCol)) { // Вниз
            activeDirections.add(WireNode.Direction.DOWN);
        }
        if (isValidConnection(powerRow, powerCol - 1)) { // Влево
            activeDirections.add(WireNode.Direction.LEFT);
        }

        // Устанавливаем активные направления в источнике питания
        _powerNode.setActiveDirections(activeDirections);

        System.out.println("Активные направления источника питания: " + activeDirections);
        drawGrid(); // Перерисовываем сетку с обновленным источником
    }

    private boolean isValidConnection(int row, int col) {
        // Проверяем границы сетки
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            return false;
        }

        GameNode node = grid[row][col];

        // Если в клетке ничего нет - нет подключения
        if (node == null) {
            return false;
        }

        // Если это провод - проверяем, принимает ли он энергию с этой стороны
        if (node instanceof WireNode) {
            WireNode wire = (WireNode) node;
            WireNode.Direction directionFromPower;

            // Определяем направление от источника к проводу
            if (row < _powerNode.getRow()) {
                directionFromPower = WireNode.Direction.UP;
            } else if (row > _powerNode.getRow()) {
                directionFromPower = WireNode.Direction.DOWN;
            } else if (col < _powerNode.getCol()) {
                directionFromPower = WireNode.Direction.LEFT;
            } else {
                directionFromPower = WireNode.Direction.RIGHT;
            }

            // Проверяем, принимает ли провод энергию от источника
            return wire.isDirectionConnected(directionFromPower.getOpposite());
        }

        // Если это лампочка - проверяем, смотрит ли она на источник
        if (node instanceof LightBulbNode) {
            LightBulbNode bulb = (LightBulbNode) node;
            WireNode.Direction bulbDirection = WireNode.Direction.fromDegrees(bulb.getRotation());

            // Направление от источника к лампочке должно совпадать с направлением лампочки
            if (row < _powerNode.getRow() && bulbDirection == WireNode.Direction.UP) return true;
            if (row > _powerNode.getRow() && bulbDirection == WireNode.Direction.DOWN) return true;
            if (col < _powerNode.getCol() && bulbDirection == WireNode.Direction.LEFT) return true;
            if (col > _powerNode.getCol() && bulbDirection == WireNode.Direction.RIGHT) return true;
        }

        return false;
    }

    public boolean checkAllLightBulbsConnected() {
        // Сначала распространяем энергию от источника питания
        updatePowerFlow();

        boolean allConnected = true;
        List<LightBulbNode> disconnectedBulbs = new ArrayList<>();

        // Проверяем, все ли лампочки получают питание
        for (LightBulbNode lightBulb : lightBulbNodes) {
            if (!lightBulb.isPowered()) {
                System.out.println("Найдена неподключенная лампочка в [" +
                        lightBulb.getRow() + "," + lightBulb.getCol() + "]");
                lightBulb.setDisconnected(true);
                disconnectedBulbs.add(lightBulb);
                allConnected = false;
            } else {
                lightBulb.setDisconnected(false);
            }
        }

        // Пытаемся подключить неподключенные лампочки
        if (!disconnectedBulbs.isEmpty()) {
            System.out.println("Пытаемся подключить " + disconnectedBulbs.size() + " неподключенных лампочек");

            // Сначала пробуем стандартное подключение
            for (LightBulbNode lightBulb : new ArrayList<>(disconnectedBulbs)) {
                connectToPower(lightBulb);

                // Проверяем, удалось ли подключить
                updatePowerFlow();
                if (lightBulb.isPowered()) {
                    lightBulb.setDisconnected(false);
                    disconnectedBulbs.remove(lightBulb);
                    System.out.println("Успешно подключена лампочка в [" +
                            lightBulb.getRow() + "," + lightBulb.getCol() + "]");
                }
            }

            // Если остались неподключенные лампочки, пробуем заменить соседние лампочки на провода
            if (!disconnectedBulbs.isEmpty()) {
                tryConnectWithNeighbors(disconnectedBulbs);
            }

            // Перепроверяем, все ли лампочки теперь подключены
            updatePowerFlow();
            allConnected = true;
            for (LightBulbNode lightBulb : lightBulbNodes) {
                if (!lightBulb.isPowered()) {
                    allConnected = false;
                    break;
                }
            }
        }

        if (allConnected) {
            System.out.println("Все лампочки подключены к источнику питания");
        }

        return allConnected;
    }

    private void tryConnectWithNeighbors(List<LightBulbNode> disconnectedBulbs) {
        for (LightBulbNode disconnectedBulb : new ArrayList<>(disconnectedBulbs)) {
            int row = disconnectedBulb.getRow();
            int col = disconnectedBulb.getCol();
            boolean connected = false;

            // Проверяем соседние ячейки
            for (WireNode.Direction dir : WireNode.Direction.values()) {
                int neighborRow = row;
                int neighborCol = col;

                switch (dir) {
                    case UP: neighborRow--; break;
                    case RIGHT: neighborCol++; break;
                    case DOWN: neighborRow++; break;
                    case LEFT: neighborCol--; break;
                }

                // Проверяем границы сетки
                if (neighborRow < 0 || neighborRow >= gridSize || neighborCol < 0 || neighborCol >= gridSize) {
                    continue;
                }

                GameNode neighbor = grid[neighborRow][neighborCol];

                // Если сосед - подключенная лампочка
                if (neighbor instanceof LightBulbNode && neighbor.isPowered()) {
                    System.out.println("Найдена подключенная лампочка-сосед в [" +
                            neighborRow + "," + neighborCol + "]");

                    // Заменяем подключенную лампочку на провод
                    LightBulbNode connectedBulb = (LightBulbNode) grid[neighborRow][neighborCol];
                    lightBulbNodes.remove(connectedBulb);

                    // Определяем направление подключенной лампочки
                    WireNode.Direction connectedBulbDir = WireNode.Direction.fromDegrees(connectedBulb.getRotation());

                    // Создаем новый провод с соединениями
                    WireNode wireNode = new WireNode(neighborRow, neighborCol);
                    wireNode.setGameManager(this);

                    // Добавляем соединение в сторону неподключенной лампочки
                    wireNode.addConnection(dir.getOpposite());

                    // Добавляем соединение в том же направлении, что и была подключенная лампочка
                    wireNode.addConnection(connectedBulbDir);

                    // Устанавливаем провод в сетке
                    grid[neighborRow][neighborCol] = wireNode;

                    // Поворачиваем неподключенную лампочку к новому проводу
                    while (WireNode.Direction.fromDegrees(disconnectedBulb.getRotation()) != dir) {
                        disconnectedBulb.rotate();
                    }

                    // Обновляем поток энергии
                    updatePowerFlow();

                    // Проверяем, подключилась ли лампочка
                    if (disconnectedBulb.isPowered()) {
                        System.out.println("Успешно подключили лампочку в [" + row + "," + col +
                                "] заменой соседней лампочки на провод");
                        disconnectedBulb.setDisconnected(false);
                        connected = true;
                        break;
                    }
                }
            }

            if (!connected) {
                System.out.println("Не удалось подключить лампочку в [" + row + "," + col +
                        "] через соседние лампочки");
            }
        }
    }

    private void connectToPower(LightBulbNode lightBulb) {
        System.out.println("\n===== НАЧИНАЕМ ПОДКЛЮЧЕНИЕ ЛАМПОЧКИ К ИСТОЧНИКУ =====");
        System.out.println("Лампочка: [" + lightBulb.getRow() + "," + lightBulb.getCol() +
                "], поворот: " + lightBulb.getRotation());
        System.out.println("Источник: [" + _powerNode.getRow() + "," + _powerNode.getCol() + "]");

        // Получаем направление выхода из лампочки
        WireNode.Direction bulbDirection = WireNode.Direction.fromDegrees(lightBulb.getRotation());
        System.out.println("Направление лампочки: " + bulbDirection);

        // Проверяем все возможные ориентации лампочки, пытаясь найти направление, в котором можно подключить к источнику
        int originalRotation = lightBulb.getRotation();
        boolean connectionSuccess = false;

        // Пробуем все 4 возможные ориентации лампочки
        for (int attempts = 0; attempts < 4; attempts++) {
            // Получаем текущее направление лампочки
            bulbDirection = WireNode.Direction.fromDegrees(lightBulb.getRotation());

            // Рассчитываем координаты первой клетки для провода
            int nextRow = lightBulb.getRow();
            int nextCol = lightBulb.getCol();

            // Смещаем координаты в направлении от лампочки
            switch (bulbDirection) {
                case UP: nextRow--; break;
                case RIGHT: nextCol++; break;
                case DOWN: nextRow++; break;
                case LEFT: nextCol--; break;
            }

            System.out.println("Пытаемся разместить первый провод в [" + nextRow + "," + nextCol + "]");

            // Проверка границ и занятости клеток
            if (nextRow < 0 || nextRow >= gridSize || nextCol < 0 || nextCol >= gridSize) {
                System.out.println("Невозможно построить путь: за пределами сетки");
                lightBulb.rotate(); // Поворачиваем и пробуем следующую ориентацию
                continue;
            }

            // Если клетка уже занята (не проводом), пробуем другую ориентацию
            if (grid[nextRow][nextCol] != null && !(grid[nextRow][nextCol] instanceof WireNode)) {
                System.out.println("Невозможно построить путь: клетка занята " + grid[nextRow][nextCol].getClass().getSimpleName());
                lightBulb.rotate(); // Поворачиваем и пробуем следующую ориентацию
                continue;
            }

            // Рекурсивно строим путь от первой клетки
            boolean pathBuilt = buildPath(nextRow, nextCol, bulbDirection.getOpposite(),
                    _powerNode.getRow(), _powerNode.getCol(), new boolean[gridSize][gridSize]);

            if (pathBuilt) {
                // Путь построен успешно
                connectionSuccess = true;
                System.out.println("Построен успешный путь от лампочки к источнику!");
                break;
            } else {
                System.out.println("Не удалось построить путь в этом направлении. Пробуем другое.");
                lightBulb.rotate(); // Поворачиваем и пробуем следующую ориентацию
            }
        }

        // Если после перебора всех ориентаций, подключение не удалось
        if (!connectionSuccess) {
            System.out.println("Не удалось подключить лампочку к источнику ни в одном из направлений.");
            // Восстанавливаем исходную ориентацию
            while (lightBulb.getRotation() != originalRotation) {
                lightBulb.rotate();
            }
        } else {
            // Обновляем поток энергии
            updatePowerFlow();
        }

        System.out.println("===== ЗАВЕРШЕНО ПОДКЛЮЧЕНИЕ ЛАМПОЧКИ К ИСТОЧНИКУ =====\n");
    }

    // Рекурсивное построение пути от текущей клетки к цели
    private boolean buildPath(int row, int col, WireNode.Direction fromDirection,
                            int targetRow, int targetCol, boolean[][] visited) {

        System.out.println("Строим путь из [" + row + "," + col + "] с направления " +
                          fromDirection + " к [" + targetRow + "," + targetCol + "]");

        // Проверка границ и посещённых клеток
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize || visited[row][col]) {
            System.out.println("  → За пределами сетки или клетка уже посещена");
            return false;
        }

        // Помечаем клетку как посещённую
        visited[row][col] = true;

        // Проверка достижения цели (источника питания)
        if (row == targetRow && col == targetCol) {
            System.out.println("  → Достигли источника питания!");
            return true;
        }

        // Проверка клетки - пустая или провод
        WireNode wireNode;
        if (grid[row][col] == null) {
            System.out.println("  → Создаём новый провод в [" + row + "," + col + "]");
            wireNode = new WireNode(row, col);
            wireNode.setGameManager(this);
            grid[row][col] = wireNode;
        } else if (grid[row][col] instanceof WireNode) {
            System.out.println("  → Используем существующий провод в [" + row + "," + col + "]");
            wireNode = (WireNode) grid[row][col];
        } else {
            System.out.println("  → Клетка занята другим объектом, не можем продолжить");
            return false;
        }

        // Добавляем соединение со стороны, откуда пришли
        wireNode.addConnection(fromDirection);
        System.out.println("  → Добавили соединение " + fromDirection);

        // Вычисляем дельты для определения направления
        int rowDelta = targetRow - row;
        int colDelta = targetCol - col;

        // Список направлений для проверки в порядке приоритета
        List<WireNode.Direction> directionsToTry = new ArrayList<>();

        // Приоритизируем направления в зависимости от относительного положения цели
        if (Math.abs(rowDelta) > Math.abs(colDelta)) {
            // Приоритет вертикального движения
            if (rowDelta < 0) directionsToTry.add(WireNode.Direction.UP);
            else if (rowDelta > 0) directionsToTry.add(WireNode.Direction.DOWN);

            if (colDelta < 0) directionsToTry.add(WireNode.Direction.LEFT);
            else if (colDelta > 0) directionsToTry.add(WireNode.Direction.RIGHT);
        } else {
            // Приоритет горизонтального движения
            if (colDelta < 0) directionsToTry.add(WireNode.Direction.LEFT);
            else if (colDelta > 0) directionsToTry.add(WireNode.Direction.RIGHT);

            if (rowDelta < 0) directionsToTry.add(WireNode.Direction.UP);
            else if (rowDelta > 0) directionsToTry.add(WireNode.Direction.DOWN);
        }

        // Добавляем оставшиеся направления, если они ещё не в списке
        for (WireNode.Direction dir : WireNode.Direction.values()) {
            if (!directionsToTry.contains(dir) && dir != fromDirection) {
                directionsToTry.add(dir);
            }
        }

        // Проходим по направлениям в порядке приоритета
        for (WireNode.Direction nextDirection : directionsToTry) {
            // Пропускаем направление, откуда пришли
            if (nextDirection == fromDirection) {
                continue;
            }

            System.out.println("  → Пробуем направление: " + nextDirection);

            // Рассчитываем следующую позицию
            int nextRow = row;
            int nextCol = col;
            switch (nextDirection) {
                case UP: nextRow--; break;
                case RIGHT: nextCol++; break;
                case DOWN: nextRow++; break;
                case LEFT: nextCol--; break;
            }

            // Рекурсивно строим путь от следующей клетки
            if (buildPath(nextRow, nextCol, nextDirection.getOpposite(), targetRow, targetCol, visited)) {
                // Путь найден, добавляем соединение с этой стороны
                wireNode.addConnection(nextDirection);
                System.out.println("  → Успешно! Добавляем соединение " + nextDirection + " к [" + row + "," + col + "]");
                return true;
            }
        }

        // Если не удалось построить путь ни в одном направлении
        System.out.println("  → Не удалось построить путь из [" + row + "," + col + "], возвращаемся");

        // Если это провод, который мы создали, но путь не нашли - удаляем провод
        if (grid[row][col] == wireNode && wireNode.getConnectedDirections().size() <= 1) {
            System.out.println("  → Удаляем бесполезный провод из [" + row + "," + col + "]");
            grid[row][col] = null;
        }

        return false;
    }
    private void placePowerNode() {
        int row = random.nextInt(gridSize);
        int col = random.nextInt(gridSize);

        // Ensure the position is empty
        while (grid[row][col] != null) {
            row = random.nextInt(gridSize);
            col = random.nextInt(gridSize);
        }

        PowerNode powerNode = new PowerNode(row, col);
        powerNode.setGameManager(this);
        grid[row][col] = powerNode;
        _powerNode = powerNode;
    }

    private LightBulbNode placeLightBulbNode() {
    int row = random.nextInt(gridSize);
    int col = random.nextInt(gridSize);

    // Ensure the position is empty
    while (grid[row][col] != null) {
        row = random.nextInt(gridSize);
        col = random.nextInt(gridSize);
    }

    LightBulbNode lightBulbNode = new LightBulbNode(row, col);
    lightBulbNode.setGameManager(this);
    grid[row][col] = lightBulbNode;
    int rotations = random.nextInt(4); // 0-3 ротаций (0, 90, 180, 270 градусов)
    for(int i = 0; i < rotations; i++) lightBulbNode.rotate();

    // Проверяем, смотрит ли лампочка за пределы поля, если да - поворачиваем
    while (isFacingBounds(lightBulbNode)) {
        lightBulbNode.rotate();
    }

    lightBulbNodes.add(lightBulbNode);

    return lightBulbNode;
}

    // Проверяет, смотрит ли лампочка за пределы игрового поля
    private boolean isFacingBounds(LightBulbNode node) {
    int rotation = node.getRotation();
    int row = node.getRow();
    int col = node.getCol();

    // Проверяем направление лампочки
    switch (rotation) {
        case 0: // Вверх
            return row == 0;
        case 90: // Вправо
            return col == gridSize - 1;
        case 180: // Вниз
            return row == gridSize - 1;
        case 270: // Влево
            return col == 0;
        default:
            return false;
    }
}

    public void initializeGame(Pane gamePane) {
        // Рассчитываем оптимальный размер ячейки
        double windowSize = Math.min(gamePane.getPrefWidth(), gamePane.getPrefHeight());
        cellSize = windowSize / gridSize;

        // Создаем canvas нужного размера
        double canvasSize = gridSize * cellSize;
        gameCanvas = new Canvas(canvasSize, canvasSize);
        gc = gameCanvas.getGraphicsContext2D();

        // Добавляем canvas на игровую панель
        gamePane.getChildren().add(gameCanvas);

        // Создаем игровую доску
        generateGameBoard();

        // Отрисовываем начальное состояние
        drawGrid();

        // Настраиваем обработчики кликов
        setupClickHandlers(gamePane);
    }

    private void drawGrid() {
        // Clear the canvas
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw each node
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null) {
                    // Метод draw класса узла должен учитывать его состояние
                    grid[row][col].draw(gc);
                }
            }
        }
    }

    public void redrawGrid() {
        drawGrid();
    }

    private void setupClickHandlers(Pane gamePane) {
        gameCanvas.setOnMouseClicked(event -> {
            double cellSize = gameCanvas.getWidth() / gridSize;
            int col = (int) (event.getX() / cellSize);
            int row = (int) (event.getY() / cellSize);

            // Check if click is within grid bounds
            if (row >= 0 && row < gridSize && col >= 0 && col < gridSize) {
                GameNode node = grid[row][col];
                if (node != null) {
                    // Rotate the node
                    node.rotate();

                    // Redraw the grid
                    drawGrid();

                    // Update power flow
                    updatePowerFlow();

                    // Вызываем метод обновления окна с решением через контроллер
                    if (gameController != null) {
                        gameController.updateSolutionIfShowing();
                    }
                }
            }
        });
    }

    // Метод проверки отсоединенных проводов, возвращает список таких проводов
    public List<WireNode> checkDisconnectedWires() {
        List<WireNode> disconnectedWires = new ArrayList<>();

        // Сбрасываем состояние проводов
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] instanceof WireNode) {
                    ((WireNode) grid[row][col]).setDisconnectedEnd(false);
                }
            }
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] instanceof WireNode) {
                    WireNode wire = (WireNode) grid[row][col];
                    Set<WireNode.Direction> validConnections = new HashSet<>();
                    Set<WireNode.Direction> invalidConnections = new HashSet<>();

                    // Проверяем каждое направление соединения
                    for (WireNode.Direction dir : wire.getConnectedDirections()) {
                        int nextRow = row;
                        int nextCol = col;

                        switch (dir) {
                            case UP: nextRow--; break;
                            case RIGHT: nextCol++; break;
                            case DOWN: nextRow++; break;
                            case LEFT: nextCol--; break;
                        }

                        boolean validConnection = false;

                        if (nextRow >= 0 && nextRow < gridSize && nextCol >= 0 && nextCol < gridSize) {
                            GameNode nextNode = grid[nextRow][nextCol];

                            if (nextNode != null) {
                                if (nextNode instanceof WireNode) {
                                    WireNode nextWire = (WireNode) nextNode;
                                    if (nextWire.isDirectionConnected(dir.getOpposite())) {
                                        validConnection = true;
                                    }
                                } else if (nextNode instanceof PowerNode) {
                                    validConnection = true;
                                } else if (nextNode instanceof LightBulbNode) {
                                    if (dir.getOpposite() == WireNode.Direction.fromDegrees(nextNode.getRotation())) {
                                        validConnection = true;
                                    }
                                }
                            }
                        }

                        if (validConnection) {
                            validConnections.add(dir);
                        } else {
                            invalidConnections.add(dir);
                        }
                    }

                    // Если у провода есть хотя бы 2 валидных соединения, удаляем только невалидные
                    if (validConnections.size() >= 2) {
                        // Удаляем невалидные соединения
                        for (WireNode.Direction dir : invalidConnections) {
                            System.out.println("Удаляем невалидное соединение " + dir +
                                    " у провода в [" + row + "," + col + "]");
                            wire.removeConnection(dir);
                        }
                    }
                    // Если меньше 2 валидных соединений, но есть хотя бы одно невалидное
                    else if (!invalidConnections.isEmpty()) {
                        wire.setDisconnectedEnd(true);
                        disconnectedWires.add(wire);
                        System.out.println("Найден отсоединенный провод в [" + row + "," + col + "] - " +
                                validConnections.size() + " валидных, " + invalidConnections.size() + " невалидных");
                    }
                }
            }
        }

        System.out.println("Найдено отсоединенных проводов: " + disconnectedWires.size());
        return disconnectedWires;
    }

    private void replaceDisconnectedWires(List<WireNode> wiresToReplace) {
        for (WireNode wire : wiresToReplace) {
            int row = wire.getRow();
            int col = wire.getCol();

            // Создаем новую лампочку
            LightBulbNode lightBulb = new LightBulbNode(row, col);
            lightBulb.setGameManager(this);
            grid[row][col] = lightBulb;

            // Ориентируем лампочку и подключаем к источнику
            orientLightBulb(lightBulb, row, col);
            lightBulbNodes.add(lightBulb);
        }

        // Перерисовываем сетку
        drawGrid();
    }

    // Вспомогательный метод для правильной ориентации лампочки
    private void orientLightBulb(LightBulbNode lightBulb, int row, int col) {
        // Проверяем соседние клетки, чтобы найти подключенный провод или источник
        for (WireNode.Direction dir : WireNode.Direction.values()) {
            int nextRow = row;
            int nextCol = col;

            switch (dir) {
                case UP: nextRow--; break;
                case RIGHT: nextCol++; break;
                case DOWN: nextRow++; break;
                case LEFT: nextCol--; break;
            }

            // Проверяем границы
            if (nextRow < 0 || nextRow >= gridSize || nextCol < 0 || nextCol >= gridSize) {
                continue;
            }

            GameNode nextNode = grid[nextRow][nextCol];
            if (nextNode != null) {
                // Если нашли провод, который подключен в нашу сторону, или источник питания
                if ((nextNode instanceof WireNode &&
                        ((WireNode)nextNode).isDirectionConnected(dir.getOpposite())) ||
                        nextNode instanceof PowerNode) {

                    // Поворачиваем лампочку так, чтобы она "смотрела" в противоположном направлении
                    while (WireNode.Direction.fromDegrees(lightBulb.getRotation()) != dir) {
                        lightBulb.rotate();
                    }
                    return;
                }
            }
        }

        // Если не нашли подходящее направление, устанавливаем случайную ориентацию
        int randomRotations = random.nextInt(4);
        for (int i = 0; i < randomRotations; i++) {
            lightBulb.rotate();
        }

        // Проверяем, не смотрит ли лампочка за пределы поля
        while (isFacingBounds(lightBulb)) {
            lightBulb.rotate();
        }

        // подключаем лампочку к источнику

        connectToPower(lightBulb);
    }

    public boolean updatePowerFlow() {
        // Сбрасываем питание для всех узлов кроме PowerNode
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null && !(grid[row][col] instanceof PowerNode)) {
                    grid[row][col].setPowered(false);
                }
            }
        }

        // Получаем координаты источника питания
        int powerRow = _powerNode.getRow();
        int powerCol = _powerNode.getCol();

        // Получаем активные направления источника питания
        Set<WireNode.Direction> activeDirections = _powerNode.getActiveDirections();

        // Распространяем энергию только в активных направлениях
        if (activeDirections.contains(WireNode.Direction.UP)) {
            propagatePower(powerRow - 1, powerCol, 180); // вверх
        }

        if (activeDirections.contains(WireNode.Direction.RIGHT)) {
            propagatePower(powerRow, powerCol + 1, 270); // вправо
        }

        if (activeDirections.contains(WireNode.Direction.DOWN)) {
            propagatePower(powerRow + 1, powerCol, 0); // вниз
        }

        if (activeDirections.contains(WireNode.Direction.LEFT)) {
            propagatePower(powerRow, powerCol - 1, 90); // влево
        }

        // Считаем количество подключенных проводов
        int poweredWires = 0;
        int totalWires = 0;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] instanceof WireNode) {
                    totalWires++;
                    if (grid[row][col].isPowered()) {
                        poweredWires++;
                    }
                }
            }
        }

        // Перерисовываем сетку
        drawGrid();

        // Возвращаем true только если все провода подключены
        return poweredWires == totalWires;
    }

    private void propagatePower(int row, int col, int fromDirection) {
        // Проверка границ сетки
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            return;
        }

        GameNode node = grid[row][col];
        if (node == null) {
            return;
        }

        // Если узел уже под напряжением, нет необходимости распространять дальше
        if (node.isPowered()) {
            return;
        }

        // Для лампочки - проверяем, что энергия приходит с правильной стороны
        if (node instanceof LightBulbNode) {
            WireNode.Direction bulbDirection = WireNode.Direction.fromDegrees(node.getRotation());
            WireNode.Direction powerDirection = WireNode.Direction.fromDegrees(fromDirection);

            // Лампочка получает питание только если она смотрит в направлении,
            // откуда приходит энергия (т.е. на провод)
            if (bulbDirection != powerDirection) {
                System.out.println("Лампочка в [" + row + "," + col + "] не запитана. " +
                        "Её направление: " + bulbDirection + ", направление энергии: " + powerDirection);
                return; // Лампочка смотрит не в ту сторону
            }
            System.out.println("Лампочка в [" + row + "," + col + "] запитана! " +
                    "Её направление: " + bulbDirection + ", направление энергии: " + powerDirection);
        }

        // Для провода - проверяем, принимает ли он энергию с текущего направления
        if (node instanceof WireNode) {
            WireNode wireNode = (WireNode) node;
            WireNode.Direction fromDir = WireNode.Direction.fromDegrees(fromDirection);

            if (!wireNode.isDirectionConnected(fromDir)) {
                return; // Провод не принимает энергию с этого направления
            }
        }

        // Применяем питание к узлу
        node.setPowered(true);

        // Распространяем питание дальше только для проводов
        if (node instanceof WireNode) {
            WireNode wireNode = (WireNode) node;

            // Распространяем энергию по всем подключенным направлениям
            for (WireNode.Direction dir : wireNode.getConnectedDirections()) {
                // Не распространяем энергию обратно в направлении, откуда пришли
                if (dir.getDegrees() == fromDirection) {
                    continue;
                }

                // Вычисляем новые координаты в зависимости от направления
                int newRow = row;
                int newCol = col;

                switch (dir) {
                    case UP:
                        newRow--;
                        break;
                    case RIGHT:
                        newCol++;
                        break;
                    case DOWN:
                        newRow++;
                        break;
                    case LEFT:
                        newCol--;
                        break;
                }

                // Распространяем энергию в новом направлении
                propagatePower(newRow, newCol, dir.getOpposite().getDegrees());
            }
        }
    }

    public boolean isGameWon() {
        // Проверяем, что все лампочки подключены
        for (LightBulbNode bulb : lightBulbNodes) {
            if (!bulb.isPowered()) {
                return false;
            }
        }

        // Проверяем, что все провода подключены
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] instanceof WireNode && !grid[row][col].isPowered()) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getGridSize() {
        return gridSize;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
    public void logNodeRotation(GameNode node, int prevRotation) {
        if (loggingEnabled && gameLogger != null) {
            gameLogger.logMove(node, prevRotation);
        }
    }

}
