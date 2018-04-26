package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import org.mapsforge.core.graphics.*;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.PointF;
import org.oscim.map.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Created by Longri on 26.04.18.
 */
public class GUI extends Stage {

    Logger log = LoggerFactory.getLogger(GUI.class);

    float YsplitPos;

    Table contextTable;
    CheckBox btnCapButt;
    CheckBox btnCapRound;
    CheckBox btnCapSquare;
    CheckBox btnLineJoinButt;
    CheckBox btnLineJoinRound;
    CheckBox btnLineJoinSquare;

    Array<PointF> pathPoints = new Array<>();
    BitmapFont font;
    Texture boundigTexture;

    private final Map mMap;
    private final VTM vtm;
    private final MAPSFORGE mapsforge;
    final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;

    public GUI(Map map, VTM l, MAPSFORGE mapsforge) {
        this.vtm = l;
        this.mMap = map;
        this.mapsforge = mapsforge;
    }

    public void drawBoundingBoxes(SpriteBatch batch) {
        if (boundigTexture == null) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    float width = Gdx.graphics.getWidth() / 3;
                    float height = (Gdx.graphics.getHeight() - YsplitPos);
                    Bitmap bmp = GRAPHIC_FACTORY.createBitmap((int) width, (int) height);
                    Canvas canvas = GRAPHIC_FACTORY.createCanvas();
                    canvas.setBitmap(bmp);

                    Path path = GRAPHIC_FACTORY.createPath();
                    path.moveTo(0, 0);
                    path.lineTo(0, height);
                    path.lineTo(width, height);
                    path.lineTo(width, 0);
                    path.lineTo(0, 0);

                    org.mapsforge.core.graphics.Paint paint = GRAPHIC_FACTORY.createPaint();
                    paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(5);
                    paint.setStyle(Style.STROKE);

                    canvas.fillColor(org.mapsforge.core.graphics.Color.TRANSPARENT);
                    canvas.drawPath(path, paint);


                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        bmp.compress(bos);
                        byte[] bytes = bos.toByteArray();
                        Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
                        boundigTexture = new Texture(pixmap, Pixmap.Format.RGBA8888, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            batch.draw(boundigTexture, 0, YsplitPos, boundigTexture.getWidth(), boundigTexture.getHeight());
            batch.draw(boundigTexture, boundigTexture.getWidth(), YsplitPos, boundigTexture.getWidth(), boundigTexture.getHeight());
            batch.draw(boundigTexture, boundigTexture.getWidth() * 2, YsplitPos, boundigTexture.getWidth(), boundigTexture.getHeight());
        }
    }

    public void create() {
        pathPoints.add(new PointF(20, 20));
        pathPoints.add(new PointF(100, 20));
        pathPoints.add(new PointF(50, 50));
        pathPoints.add(new PointF(250, 250));
        pathPoints.add(new PointF(20, 380));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter para = new FreeTypeFontGenerator.FreeTypeFontParameter();
        para.size = 20;
        para.color = com.badlogic.gdx.graphics.Color.RED;
        font = generator.generateFont(para);


        Gdx.input.setInputProcessor(this);
        contextTable = new Table();
        contextTable.setDebug(true, true);
        this.addActor(contextTable);

        FileHandle skinFile = Gdx.files.internal("clean-crispy-ui.json");
        FileHandle atlas = Gdx.files.internal("clean-crispy-ui.atlas");
        Skin skin = new Skin();

        skin.add("font", font, BitmapFont.class);
        skin.addRegions(new TextureAtlas(atlas));
        skin.load(skinFile);

        btnCapButt = new CheckBox("butt", skin);
        btnCapRound = new CheckBox("round", skin);
        btnCapSquare = new CheckBox("square", skin);
        btnLineJoinButt = new CheckBox("miter", skin);
        btnLineJoinRound = new CheckBox("round", skin);
        btnLineJoinSquare = new CheckBox("bevel", skin);

        btnCapButt.addListener(listener);
        btnCapRound.addListener(listener);
        btnCapSquare.addListener(listener);
        btnLineJoinButt.addListener(listener);
        btnLineJoinRound.addListener(listener);
        btnLineJoinSquare.addListener(listener);

        ButtonGroup buttonGroup = new ButtonGroup(btnCapButt, btnCapRound, btnCapSquare);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);
        buttonGroup.setUncheckLast(true); //If true, when the maximum number of buttons are checked and an additional button is checked, the last button to be checked is unchecked so that the maximum is not exceeded.
        btnCapRound.setChecked(true);


        ButtonGroup buttonGroup2 = new ButtonGroup(btnLineJoinButt, btnLineJoinRound, btnLineJoinSquare);
        buttonGroup2.setMaxCheckCount(1);
        buttonGroup2.setMinCheckCount(0);
        buttonGroup2.setUncheckLast(true); //If true, when the maximum number of buttons are checked and an additional button is checked, the last button to be checked is unchecked so that the maximum is not exceeded.
        btnLineJoinRound.setChecked(true);

        contextTable.add(btnCapButt).left();
        contextTable.add(btnLineJoinButt).left().row();
        contextTable.add(btnCapRound).left();
        contextTable.add(btnLineJoinRound).left().row();
        contextTable.add(btnCapSquare).left();
        contextTable.add(btnLineJoinSquare).left().row();

        listener.clicked(null, 0, 0);
    }

    ClickListener listener = new ClickListener() {
        public void clicked(InputEvent event, float x, float y) {
            log.debug("Actor Clicked");

            Paint.Cap cap = Paint.Cap.ROUND;
            if (btnCapButt.isChecked()) cap = Paint.Cap.BUTT;
            else if (btnCapSquare.isChecked()) cap = Paint.Cap.SQUARE;

            //Update VTM
            vtm.lineChanged(pathPoints, cap);
            mMap.updateMap(true);

            //update mapsforge
            mapsforge.lineChanged(pathPoints, cap);

        }
    };

    public void resize(int width, int height) {
        this.getViewport().update(width, height, true);
        YsplitPos = height / 3;
        contextTable.setBounds(0, 0, width, YsplitPos);
    }

    public void drawMapsforge(SpriteBatch batch) {
        mapsforge.draw(batch, YsplitPos);
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        mapsforge.dispose();
    }
}
