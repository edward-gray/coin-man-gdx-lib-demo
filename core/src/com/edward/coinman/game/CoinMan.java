package com.edward.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture man[];
    Texture dizzyMan;
    BitmapFont bitmapFont;

    int score = 0;
    int gameState = 0;

    int manState = 0;
    int pause = 0;
    float gravity = 2f;
    int velocity = 0;
    int manY = 0;
    Rectangle manRectangle;

    Random random;

    ArrayList<Integer> coinXs = new ArrayList<>();
    ArrayList<Integer> coinYs = new ArrayList<>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<>();
    Texture coin;
    int coinCount;

    ArrayList<Integer> bombXs = new ArrayList<>();
    ArrayList<Integer> bombYs = new ArrayList<>();
    ArrayList<Rectangle> bombRectangles = new ArrayList<>();
    Texture bomb;
    int bombCount;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        dizzyMan = new Texture("dizzy-1.png");

        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(10);

        manY = Gdx.graphics.getHeight() / 2;

        random = new Random();
        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
    }

    private void makeCoin() {
        float height = random.nextFloat() * Gdx.graphics.getHeight() / 2;
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    private void makeBomb() {
        float height = random.nextFloat() * Gdx.graphics.getHeight() / 2;
        bombYs.add((int) height);
        bombXs.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();

        // showing background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // checking game state
        if (gameState == 0) {
            // waiting for start
            if (Gdx.input.justTouched()) {
                // start game
                gameState = 1;
            }
        } else if (gameState == 1) {
            // game is live
            coinRectangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                // coin
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 7);

                coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }

            // showing bombs
            if (bombCount < 300) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }

            bombRectangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {
                // bomb
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 10);
                bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }


            // jump in touch
            if (Gdx.input.justTouched()) {
                velocity = -50;
            }

            // speed of frames
            if (pause < 8) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }

            // speed of gravity
            velocity += gravity;
            manY -= velocity;

            // keep him on the ground
            if (manY <= 0) {
                manY = 0;
            }
        } else if (gameState == 2) {
            // game over
            if (Gdx.input.justTouched()) {
                manY = Gdx.graphics.getHeight() / 2;

                score = 0;
                velocity = 0;

                coinCount = 0;
                coinYs.clear();
                coinXs.clear();
                coinRectangles.clear();

                bombCount = 0;
                bombXs.clear();
                bombYs.clear();
                bombRectangles.clear();

                gameState = 1;
            }
        }

        // showing coins
        if (coinCount < 100) {
            coinCount++;
        } else {
            coinCount = 0;
            makeCoin();
        }

        // showing dizzy face in game over
        int manX = (Gdx.graphics.getWidth() / 2) - (man[manState].getWidth() / 2);
        if (gameState == 2) {
            batch.draw(dizzyMan, manX, manY);
        } else {
            batch.draw(man[manState], manX, manY);
        }

        manRectangle = new Rectangle(manX, manY, man[manState].getWidth(), man[manState].getHeight());

        // checking coin collision
        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
                score++;
                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

        // checking bomb collision
        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
                // game over
                gameState = 2;
            }
        }

        bitmapFont.draw(batch, String.valueOf(score), 100, 200);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
