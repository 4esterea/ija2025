/*
 * PowerNode.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Game node class that represents power source elements in the game grid.
 * Extends GameNode with functionality for managing active connection directions,
 * visual representation of a power node with active wire connections, and
 * custom rotation behavior. Includes lightning imagery and power-state visual effects.
 */


package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PowerNode extends GameNode {

    private Set<WireNode.Direction> activeDirections;
    private static Image lightningImage = null;
    public PowerNode(int row, int col) {
        super(row, col);
        // По умолчанию активны все направления
        activeDirections = new HashSet<>(Arrays.asList(
                WireNode.Direction.UP,
                WireNode.Direction.RIGHT,
                WireNode.Direction.DOWN,
                WireNode.Direction.LEFT
        ));
        loadLightningImage();
    }

    public void setActiveDirections(Set<WireNode.Direction> directions) {
        this.activeDirections = directions;
    }

    public Set<WireNode.Direction> getActiveDirections() {
        return activeDirections;
    }

    private void loadLightningImage() {
        if (lightningImage == null) {
            try {
                lightningImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/media/lightning.png")));
            } catch (Exception e) {}
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = gameManager.getCellSize();
        double x = col * cellSize;
        double y = row * cellSize;

        // Толщина проводов (одинаковая в обоих классах)
        double connectionWidth = cellSize * 0.1;
        // Толщина соединительного элемента (чуть толще чем провод)
        double connectionJointWidth = connectionWidth * 1.5;
        // Длина соединительного элемента
        double connectionJointLength = cellSize * 0.07;

        double overlap = 1.0; // 1 пиксель перекрытия

        // Центр ячейки
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;

        // Размер квадрата
        double squareSize = cellSize * 0.5;

        // Рисуем прозрачный фон
        gc.clearRect(x, y, cellSize, cellSize);

        // Рисуем серый квадрат в центре
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(centerX - squareSize/2, centerY - squareSize/2, squareSize, squareSize);

        // Рамка квадрата
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5);
        gc.strokeRect(centerX - squareSize/2, centerY - squareSize/2, squareSize, squareSize);

        // UP
        if (activeDirections.contains(WireNode.Direction.UP)) {
            // Темно-серый переход
            gc.setFill(Color.rgb(30, 30, 30));
            gc.fillRect(centerX - connectionJointWidth / 2, centerY - squareSize/2 - connectionJointLength,
                    connectionJointWidth, connectionJointLength);

            // Провод (с перекрытием)
            gc.setFill(Color.rgb(255, 185, 1));
            gc.fillRect(centerX - connectionWidth / 2, y - overlap,
                    connectionWidth,
                    centerY - squareSize/2 - connectionJointLength - y + overlap);
        }

        // RIGHT
        if (activeDirections.contains(WireNode.Direction.RIGHT)) {
            // Темно-серый переход
            gc.setFill(Color.rgb(30, 30, 30));
            gc.fillRect(centerX + squareSize/2, centerY - connectionJointWidth / 2,
                    connectionJointLength, connectionJointWidth);

            // Провод (с перекрытием)
            gc.setFill(Color.rgb(255, 185, 1));
            gc.fillRect(centerX + squareSize/2 + connectionJointLength,
                    centerY - connectionWidth / 2,
                    x + cellSize - (centerX + squareSize/2 + connectionJointLength) + overlap,
                    connectionWidth);
        }

        // DOWN
        if (activeDirections.contains(WireNode.Direction.DOWN)) {
            // Темно-серый переход
            gc.setFill(Color.rgb(30, 30, 30));
            gc.fillRect(centerX - connectionJointWidth / 2, centerY + squareSize/2,
                    connectionJointWidth, connectionJointLength);

            // Провод (с перекрытием)
            gc.setFill(Color.rgb(255, 185, 1));
            gc.fillRect(centerX - connectionWidth / 2,
                    centerY + squareSize/2 + connectionJointLength,
                    connectionWidth,
                    y + cellSize - (centerY + squareSize/2 + connectionJointLength) + overlap);
        }

        // LEFT
        if (activeDirections.contains(WireNode.Direction.LEFT)) {
            // Темно-серый переход
            gc.setFill(Color.rgb(30, 30, 30));
            gc.fillRect(centerX - squareSize/2 - connectionJointLength, centerY - connectionJointWidth / 2,
                    connectionJointLength, connectionJointWidth);

            // Провод (с перекрытием)
            gc.setFill(Color.rgb(255, 185, 1));
            gc.fillRect(x - overlap, centerY - connectionWidth / 2,
                    centerX - squareSize/2 - connectionJointLength - x + overlap,
                    connectionWidth);
        }

        // Рисуем молнию внутри квадрата
        if (lightningImage != null) {
            double imgSize = squareSize * 0.8;
            gc.drawImage(lightningImage,
                    centerX - imgSize/2,
                    centerY - imgSize/2,
                    imgSize, imgSize);
        }

        // Подсветка при наличии питания
        if (isPowered()) {
            gc.setGlobalAlpha(0.15);
            gc.setFill(Color.YELLOW);
            gc.fillOval(centerX - cellSize * 0.4, centerY - cellSize * 0.4,
                    cellSize * 0.8, cellSize * 0.8);
            gc.setGlobalAlpha(1.0);
        }
    }


    @Override
    public void rotate() {
        super.rotate();
        // Создаем новый набор активных направлений после поворота
        Set<WireNode.Direction> newDirections = new HashSet<>();

        // Поворачиваем каждое активное направление на 90 градусов
        for (WireNode.Direction dir : activeDirections) {
            switch (dir) {
                case UP:
                    newDirections.add(WireNode.Direction.RIGHT);
                    break;
                case RIGHT:
                    newDirections.add(WireNode.Direction.DOWN);
                    break;
                case DOWN:
                    newDirections.add(WireNode.Direction.LEFT);
                    break;
                case LEFT:
                    newDirections.add(WireNode.Direction.UP);
                    break;
            }
        }

        // Обновляем активные направления
        activeDirections = newDirections;

        // Обновляем поток энергии для переподключения элементов
        if (gameManager != null) {
            gameManager.updatePowerFlow();
        }
    }
}
