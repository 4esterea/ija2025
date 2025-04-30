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

        // Толщина проводов
        double connectionWidth = cellSize * 0.1;
        // Небольшой отступ для перекрытия между элементами
        double overlap = 1.0; // 1 пиксель перекрытия

        // Рисуем прозрачный фон
        gc.clearRect(x, y, cellSize, cellSize);

        // Рисуем провода в подключенных направлениях
        for (Direction dir : connections) {
            double wireX = 0;
            double wireY = 0;
            double wireWidth = 0;
            double wireHeight = 0;

            switch (dir) {
                case UP:
                    wireX = centerX - connectionWidth / 2;
                    wireY = y - overlap; // Выходим за пределы текущей ячейки
                    wireWidth = connectionWidth;
                    wireHeight = cellSize / 2 + overlap;
                    break;
                case RIGHT:
                    wireX = centerX;
                    wireY = centerY - connectionWidth / 2;
                    wireWidth = cellSize / 2 + overlap;
                    wireHeight = connectionWidth;
                    break;
                case DOWN:
                    wireX = centerX - connectionWidth / 2;
                    wireY = centerY;
                    wireWidth = connectionWidth;
                    wireHeight = cellSize / 2 + overlap;
                    break;
                case LEFT:
                    wireX = x - overlap; // Выходим за пределы текущей ячейки
                    wireY = centerY - connectionWidth / 2;
                    wireWidth = cellSize / 2 + overlap;
                    wireHeight = connectionWidth;
                    break;
            }

            // Определяем цвет провода
            Color innerWireColor;
            if (hasDisconnectedEnd) {
                innerWireColor = Color.RED;
            } else {
                innerWireColor = isPowered ? Color.rgb(255, 185, 1) : Color.rgb(150, 150, 150);
            }

            gc.setFill(innerWireColor);
            gc.fillRect(wireX, wireY, wireWidth, wireHeight);
        }

        // Отрисовка соединительного узла в центре, если есть больше одного соединения
        if (connections.size() > 1) {
            // Определяем цвет узла, такой же как у проводов
            Color nodeColor;
            if (hasDisconnectedEnd) {
                nodeColor = Color.RED;
            } else {
                nodeColor = isPowered ? Color.rgb(255, 185, 1) : Color.rgb(150, 150, 150);
            }

            // Размер узла соединения такой же, как толщина проводов
            double jointSize = connectionWidth;

            // Радиус скругления углов (равный размеру узла для круглой формы)
            double cornerRadius = jointSize;

            gc.setFill(nodeColor);
            gc.fillRoundRect(centerX - jointSize/2, centerY - jointSize/2,
                    jointSize, jointSize, cornerRadius, cornerRadius);
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


