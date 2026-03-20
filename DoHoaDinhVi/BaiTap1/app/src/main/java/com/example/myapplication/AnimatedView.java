package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class AnimatedView extends View {
    private Paint paintBall, paintPaddle, paintText;


    private int x = 100, y = 100;
    private int dx = 10, dy = 10;
    private int radius = 40;

    // thanh ngang
    private float paddleX;
    private int paddleWidth = 250;
    private int paddleHeight = 40;
    private int paddleBottomMargin = 150;

    //game
    private boolean isGameOver = false;
    private Random random = new Random();

    public AnimatedView(Context context) {
        super(context);

        paintBall = new Paint();
        paintBall.setColor(Color.RED);
        paintBall.setAntiAlias(true);

        paintPaddle = new Paint();
        paintPaddle.setColor(Color.BLUE);

        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(80);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setFakeBoldText(true);
    }

    // Hàm đổi màu ngẫu nhiên
    private void changeBallColor() {
        int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        paintBall.setColor(color);
    }

    private void resetGame() {
        x = getWidth() / 2;
        y = 200;
        dx = 10;
        dy = 10;
        isGameOver = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        if (isGameOver) {
            // Hiện chữ Game Over
            canvas.drawText("GAME OVER", screenWidth / 2f, screenHeight / 2f, paintText);
            paintText.setTextSize(40);
            canvas.drawText("Chạm để chơi lại", screenWidth / 2f, screenHeight / 2f + 100, paintText);
            paintText.setTextSize(80);
            return;
        }

        // Thanh ngang
        float paddleY = screenHeight - paddleBottomMargin;
        canvas.drawRect(paddleX, paddleY, paddleX + paddleWidth, paddleY + paddleHeight, paintPaddle);

        // Bóng
        canvas.drawCircle(x, y, radius, paintBall);

        // Cập nhật vị trí bóng
        x += dx;
        y += dy;

        // Va chạm tường trái & phải → chỉ đổi hướng, KHÔNG đổi màu
        if (x + radius > screenWidth || x - radius < 0) {
            dx = -dx;
            // Bỏ changeBallColor() ở đây
        }

        // Va chạm tường trên → chỉ đổi hướng, KHÔNG đổi màu
        if (y - radius < 0) {
            dy = -dy;
            // Bỏ changeBallColor() ở đây
        }

        // Va chạm thanh paddle → ĐỔI HƯỚNG + ĐỔI MÀU
        if (y + radius >= paddleY && y + radius <= paddleY + Math.abs(dy) + 5) {  // +5 để dễ bắt hơn
            if (x >= paddleX && x <= paddleX + paddleWidth) {
                dy = -dy;
                // Đưa bóng lên ngay trên thanh một chút để tránh kẹt
                y = (int) (paddleY - radius - 1);
                changeBallColor();           // <--- Chỉ đổi màu ở đây
            }
        }

        // Thua game
        if (y - radius > screenHeight) {
            isGameOver = true;
        }

        postInvalidateDelayed(16);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                resetGame();
            }
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                paddleX = event.getX() - (paddleWidth / 2f);
                if (paddleX < 0) paddleX = 0;
                if (paddleX + paddleWidth > getWidth()) paddleX = getWidth() - paddleWidth;
                invalidate();
                break;
        }
        return true;
    }
}