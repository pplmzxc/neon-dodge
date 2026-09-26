package com.example.neondodge;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {
    private Game game;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        game = new Game();
        setContentView(game);
    }
    @Override protected void onPause() {
        super.onPause();
        game.pause();
    }
    final class Game extends View {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Random random = new Random();
        final ArrayList<float[]> obstacles = new ArrayList<>();
        final int cyan = Color.rgb(86, 245, 222), pink = Color.rgb(255, 79, 134);
        // Logical canvas keeps collision and drawing coordinates identical.
        float scale = 1, offsetX, offsetY, player = 180, elapsed, spawn;
        long last;
        int mode = 0, best; // 0: title, 1: playing, 2: paused, 3: result
        Game() {
            super(MainActivity.this);
            best = getPreferences(MODE_PRIVATE).getInt("best", 0);
            setContentDescription("네온 피하기. 손가락을 좌우로 움직여 장애물을 피하세요.");
        }
        void pause() { if (mode == 1) mode = 2; last = 0; invalidate(); }
        void start() {
            obstacles.clear(); player = 180; elapsed = 0; spawn = .7f;
            mode = 1; last = 0; invalidate();
        }
        void step(float dt) {
            elapsed += dt;
            spawn -= dt;
            if (spawn <= 0) {
                float width = 28 + random.nextFloat() * 34;
                obstacles.add(new float[]{18 + random.nextFloat() * (324 - width), -40, width});
                spawn = Math.max(.32f, .85f - elapsed * .006f);
            }
            float speed = Math.min(380, 155 + elapsed * 3);
            for (int i = obstacles.size() - 1; i >= 0; i--) {
                float[] b = obstacles.get(i); b[1] += speed * dt;
                if (b[1] > 660) { obstacles.remove(i); continue; }
                if (player + 11 > b[0] && player - 11 < b[0] + b[2]
                        && 565 > b[1] && 543 < b[1] + 28) {
                    mode = 3;
                    best = Math.max(best, (int)(elapsed * 10));
                    getPreferences(MODE_PRIVATE).edit().putInt("best", best).apply();
                    break;
                }
            }
        }
        void text(Canvas c, String s, float x, float y, float size, int color) {
            paint.setColor(color); paint.setTextSize(size); paint.setTextAlign(Paint.Align.CENTER);
            c.drawText(s, x, y, paint);
        }
        void rect(Canvas c, float l, float t, float r, float b, int color, float radius) {
            paint.setColor(color); c.drawRoundRect(l,t,r,b,radius,radius,paint);
        }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            long now = System.nanoTime();
            if (mode == 1 && last != 0) step(Math.min(.033f, (now-last)/1_000_000_000f));
            last = now;
            canvas.drawColor(Color.rgb(8, 12, 25));
            // Reserve top/bottom margins for system bars on edge-to-edge devices.
            scale = Math.min(getWidth()/360f, getHeight()/740f);
            offsetX = (getWidth()-360*scale)/2; offsetY=(getHeight()-640*scale)/2;
            canvas.save(); canvas.translate(offsetX, offsetY); canvas.scale(scale, scale);
            paint.setColor(Color.rgb(23, 34, 50)); paint.setStrokeWidth(1);
            for(int x=20;x<360;x+=40) canvas.drawLine(x,85,x,620,paint);
            text(canvas,"NEON DODGE",103,32,19,cyan);
            text(canvas,"최고 " + best,90,59,13,Color.LTGRAY);
            if(mode==1) text(canvas,"Ⅱ",321,40,26,Color.WHITE);
            text(canvas,String.valueOf((int)(elapsed*10)),180,115,36,Color.WHITE);
            for(float[] b:obstacles) rect(canvas,b[0],b[1],b[0]+b[2],b[1]+28,pink,7);
            rect(canvas,player-14,540,player+14,568,cyan,8);
            if(mode!=1) {
                rect(canvas,12,166,348,461,Color.rgb(17,25,43),24);
                String heading=mode==0?"네온 피하기":mode==2?"잠깐 쉬는 중":"다시 한 판?";
                text(canvas,heading,180,223,29,Color.WHITE);
                text(canvas,mode==3?"점수 " + (int)(elapsed*10):"손가락으로 좌우 이동",180,267,19,cyan);
                text(canvas,mode==0?"분홍 장애물을 피하세요":"최고 기록 " + best,180,301,16,Color.LTGRAY);
                rect(canvas,52,348,308,414,cyan,18);
                text(canvas,mode==2?"계속하기":mode==3?"다시 시작":"시작하기",180,389,23,Color.rgb(8,12,25));
            }
            text(canvas,"DRAG TO DODGE",180,615,12,Color.LTGRAY);
            canvas.restore();
            if(mode==1) postInvalidateOnAnimation();
        }
        @Override public boolean onTouchEvent(MotionEvent event) {
            float x=(event.getX()-offsetX)/scale, y=(event.getY()-offsetY)/scale;
            int action=event.getActionMasked();
            if(action==MotionEvent.ACTION_DOWN) {
                if(mode!=1) {
                    if(x>=52 && x<=308 && y>=348 && y<=414) {
                        if(mode==2) {mode=1;last=0;invalidate();} else start();
                    }
                    return true;
                }
                if(x>285 && y<75) {pause();return true;}
            }
            if(mode==1 && (action==MotionEvent.ACTION_DOWN || action==MotionEvent.ACTION_MOVE)) {
                player=Math.max(20,Math.min(340,x));invalidate();
            }
            if(action==MotionEvent.ACTION_UP) performClick();
            if(action==MotionEvent.ACTION_CANCEL) pause();
            return true;
        }
        @Override public boolean performClick() {super.performClick();return true;}
    }
}
