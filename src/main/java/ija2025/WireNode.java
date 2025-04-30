package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.Set;

public class WireNode extends GameNode {
    // Множество направлений, в которых провод имеет соединения
    private Set<Direction> connections = new HashSet<>();

    public enum Direction {
        UP(0),
        RIGHT(90),
        DOWN(180),
        LEFT(270);

        private final int degrees;

        Direction(int degrees) {
            this.degrees = degrees;
        }

        public int getDegrees() {
            return degrees;
        }

        public Direction getOpposite() {
            switch (this) {
                case UP: return DOWN;
                case RIGHT: return LEFT;
                case DOWN: return UP;
                case LEFT: return RIGHT;
                default: return UP;
            }
        }

        public static Direction fromDegrees(int degrees) {
            degrees = (degrees % 360 + 360) % 360; // Нормализация угла
            switch (degrees) {
                case 0: return UP;
                case 90: return RIGHT;
                case 180: return DOWN;
                case 270: return LEFT;
                default: return UP;
            }
        }
    }

    public WireNode(int row, int col) {
        super(row, col);
    }

    // Добавление соединения в определённом направлении
    public void addConnection(Direction direction) {
        connections.add(direction);
    }

    // Получение всех соединений
    public Set<Direction> getConnectedDirections() {
        return new HashSet<>(connections);
    }

    // Проверка наличия соединения
    public boolean isDirectionConnected(Direction direction) {
        return connections.contains(direction);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = gameManager.getCellSize();
        double x = col * cellSize;
        double y = row * cellSize;
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;
        double wireWidth = 10; // Ширина провода
        double lineLength = cellSize / 2; // Длина линии от центра до края

        // Рисуем фон
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, y, cellSize, cellSize);

        // Рисуем границу
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, cellSize, cellSize);

        // Устанавливаем цвет провода в зависимости от статуса
        if (hasDisconnectedEnd) {
            gc.setFill(Color.RED); // Красный для проводов с отключенными концами
        } else {
            gc.setFill(isPowered ? Color.GREEN : Color.DARKGRAY);
        }

        // Рисуем соединения из центра в каждое подключенное направление
        for (Direction dir : connections) {
            // Сохраняем состояние графического контекста
            gc.save();

            // Перемещаемся в центр ячейки
            gc.translate(centerX, centerY);

            // Вращаем в соответствии с направлением
            gc.rotate(dir.getDegrees());

            // Рисуем линию от центра до края в нужном направлении
            gc.fillRect(-wireWidth/2, -lineLength, wireWidth, lineLength);

            // Восстанавливаем графический контекст
            gc.restore();
        }
    }

    @Override
    public void rotate() {
        super.rotate();

        // Обновляем соединения при вращении
        Set<Direction> newConnections = new HashSet<>();
        for (Direction dir : connections) {
            // Вращаем каждое направление на 90 градусов
            int newDegrees = (dir.getDegrees() + 90) % 360;
            newConnections.add(Direction.fromDegrees(newDegrees));
        }

        connections = newConnections;
    }

    private boolean hasDisconnectedEnd = false;

    // Добавьте геттер и сеттер
    public boolean hasDisconnectedEnd() {
        return hasDisconnectedEnd;
    }

    public void setDisconnectedEnd(boolean disconnected) {
        this.hasDisconnectedEnd = disconnected;
    }

    public boolean removeConnection(Direction direction) {
        if (connections.contains(direction)) {
            connections.remove(direction);
            return true;
        }
        return false;
    }
}


