package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LightBulbNode extends GameNode {
    private double cellSize = 50; // Значение по умолчанию

    public LightBulbNode(int row, int col) {
        super(row, col);
    }

    // Конструктор с дополнительным параметром cellSize
    public LightBulbNode(int row, int col, double cellSize) {
        super(row, col);
        this.cellSize = cellSize;
    }

    // Сеттер для установки размера ячейки после создания
    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }

    // Геттер для получения размера ячейки
    public double getCellSize() {
        return cellSize;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = gameManager.getCellSize();

        // Используем переданный размер ячейки вместо фиксированного
        double x = col * cellSize;
        double y = row * cellSize;
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;
        double bulbRadius = cellSize / 3;
        double wireWidth = cellSize / 6.25; // Масштабируем ширину провода (50/8 = 6.25)

        // Рисуем фон ячейки
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, y, cellSize, cellSize);

        // Рисуем границу ячейки
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, cellSize, cellSize);

        // Сохраняем текущее состояние графического контекста
        gc.save();

        // Перемещаемся в центр ячейки и вращаем в соответствии с поворотом
        gc.translate(centerX, centerY);
        gc.rotate(rotation);

        // Рисуем провод, идущий к лампочке
        if (isDisconnected()) {
            gc.setFill(Color.RED); // Красный для отключенных лампочек
        } else {
            gc.setFill(isPowered() ? Color.GREEN : Color.GRAY);
        }
        gc.fillRect(-wireWidth/2, -cellSize/2, wireWidth, cellSize/4);

        // Рисуем лампочку
        if (isPowered()) {
            // Светящаяся лампочка, когда под напряжением
            gc.setFill(Color.YELLOW);
            gc.fillOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

            // Добавляем эффект свечения
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(2);
            gc.strokeOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);
        } else if (isDisconnected()) {
            // Красная лампочка для отключенных
            gc.setFill(Color.RED);
            gc.fillOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

            gc.setStroke(Color.DARKRED);
            gc.setLineWidth(1);
            gc.strokeOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);
        } else {
            // Серая лампочка, когда не под напряжением
            gc.setFill(Color.DARKGRAY);
            gc.fillOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);
        }

        // Восстанавливаем графический контекст
        gc.restore();
    }

    @Override
    public void rotate() {
        super.rotate();
        // Additional logic specific to LightBulbNode rotation if needed
    }

    // Добавьте это поле в класс LightBulbNode
    private boolean isDisconnected = false;

    // И соответствующие геттер и сеттер
    public boolean isDisconnected() {
        return isDisconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.isDisconnected = disconnected;
    }
}
