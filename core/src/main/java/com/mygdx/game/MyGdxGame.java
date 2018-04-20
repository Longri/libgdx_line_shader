package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    ShaderProgram shader;

    Logger log = LoggerFactory.getLogger(MyGdxGame.class);

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

//        FileHandle fragmentShader = Gdx.files.internal("dash-lines-2D.frag");
//        FileHandle vertextShader = Gdx.files.internal("dash-lines-2D.vert");

        FileHandle fragmentShader = Gdx.files.internal("batch.frag");
        FileHandle vertextShader = Gdx.files.internal("batch.vert");

        shader = new ShaderProgram(vertextShader, fragmentShader);

        if (shader.getLog().length() > 0) {
            log.error("SHADER compile error: {}", shader.getLog());
        }

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setShader(shader);

        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}
