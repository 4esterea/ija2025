package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class LightBulbNode extends GameNode {

    public LightBulbNode(int row, int col) {
        super(row, col);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = gameManager.getCellSize();
        double x = col * cellSize;
        double y = row * cellSize;
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;

        // Небольшой отступ для перекрытия между элементами
        double overlap = 1.0; // 1 пиксель перекрытия

        // Используем одинаковую толщину провода, как в других нодах
        double connectionWidth = cellSize * 0.1;
        // Толщина соединительного элемента (чуть толще чем провод)
        double connectionJointWidth = connectionWidth * 1.5;
        // Длина соединительного элемента
        double connectionJointLength = cellSize * 0.07;
        // Размер лампочки
        double bulbRadius = cellSize * 0.25;

        // Очищаем фон ячейки
        gc.clearRect(x, y, cellSize, cellSize);

        // Сохраняем текущее состояние графического контекста
        gc.save();

        // Перемещаемся в центр ячейки и вращаем в соответствии с поворотом
        gc.translate(centerX, centerY);
        gc.rotate(rotation);

        // Рисуем провод, идущий к лампочке (до края ячейки)
        if (isDisconnected()) {
            gc.setFill(Color.RED); // Красный для отключенных лампочек
        } else {
            gc.setFill(isPowered() ? Color.rgb(255, 185, 1) : Color.rgb(150, 150, 150));
        }

        // Рисуем провод с учетом перекрытия
        gc.fillRect(-connectionWidth/2, -cellSize/2 - overlap, connectionWidth,
                cellSize/2 - bulbRadius - connectionJointLength + overlap);

        // Рисуем темно-серый соединительный элемент между проводом и лампочкой
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(-connectionJointWidth/2, -bulbRadius - connectionJointLength,
                connectionJointWidth, connectionJointLength);

        // Рисуем корпус лампочки по центру ячейки
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

        // Рамка корпуса лампочки
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5);
        gc.strokeOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

        // Внутренняя часть лампочки
        double innerRadius = bulbRadius * 0.7;

        // Если лампочка под напряжением - добавляем свечение
        if (isPowered()) {
            // Внутренняя часть лампочки
            gc.setFill(Color.YELLOW);
            gc.fillOval(-innerRadius, -innerRadius, innerRadius*2, innerRadius*2);

            // Добавляем эффект свечения
            gc.setGlobalAlpha(0.15);
            gc.setFill(Color.YELLOW);
            gc.fillOval(-bulbRadius*1.5, -bulbRadius*1.5, bulbRadius*3, bulbRadius*3);
            gc.setGlobalAlpha(1.0);
        } else if (isDisconnected()) {
            // Красная лампочка для отключенных
            gc.setFill(Color.RED);
            gc.fillOval(-innerRadius, -innerRadius, innerRadius*2, innerRadius*2);
        } else {
            // Прозрачная внутренняя часть для невключенных лампочек
            gc.setFill(Color.rgb(43, 45, 48));
            gc.fillOval(-innerRadius, -innerRadius, innerRadius*2, innerRadius*2);
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
