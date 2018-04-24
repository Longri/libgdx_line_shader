/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import org.mapsforge.core.graphics.*;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GL;
import org.oscim.backend.GLAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.PointF;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.theme.styles.LineStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LineRenderTest extends GdxMapApp {

    GeometryBuffer mGeom = new GeometryBuffer(2, 1);
    GeometryBuffer mLine = new GeometryBuffer(2, 1);
    SpriteBatch batch;
    Texture texture;
    ShaderProgram defaultShader, solidShader, dashShader;
    BitmapFont font;
    GL20 gl;
    Logger log = LoggerFactory.getLogger(LineRenderTest.class);
    float fpsX, fpsY;
    Stage mainStage;
    Table contextTable;
    CheckBox btnCapButt;
    CheckBox btnCapRound;
    CheckBox btnCapSquare;
    CheckBox btnLineJoinButt;
    CheckBox btnLineJoinRound;
    CheckBox btnLineJoinSquare;
    final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;

    Array<PointF> pathPoints = new Array<>();


    static boolean fixedLineWidth = true;
    LineTest l = new LineTest();

    @Override
    public void createLayers() {
        MapRenderer.setBackgroundColor(0xff000000);

        mMap.setMapPosition(0, 0, 1 << 4);

        GeometryBuffer g = mLine;
        g.startLine();
        g.addPoint(-100, 0);
        g.addPoint(100, 0);

        addLines(l, 0, true, false, Cap.ROUND);

        mMap.layers().add(new GenericLayer(mMap, l));
    }

    void addLines(LineTest l, int layer, boolean addOutline, boolean fixed, Cap cap) {

        GeometryBuffer g = mLine;

        LineStyle line1;

        if (fixed) {
            line1 = new LineStyle(Color.RED, 5f);
        } else {
            line1 = new LineStyle(0, null, Color.RED, 10.0f, cap, false, 0, 0, 0, 0, 0f, false, null, true, null, LineStyle.REPEAT_START_DEFAULT, LineStyle.REPEAT_GAP_DEFAULT);
        }

        TextureItem tex = null;
        try {
            tex = new TextureItem(CanvasAdapter.getBitmapAsset("", "patterns/dot.png"));
            tex.mipmap = true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        LineBucket ll = l.buckets.addLineBucket(10, line1);
        ll.addLine(g.translate(-40, -100));


    }


    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        texture = new Texture("badlogic.jpg");

        gl = Gdx.gl;

        defaultShader = new ShaderProgram(
                Gdx.files.internal("batch.vert"),
                Gdx.files.internal("batch.frag"));
        if (defaultShader.getLog().length() > 0) {
            log.error("DEFAULT SHADER compile error: {}", defaultShader.getLog());
        }

        solidShader = new ShaderProgram(
                Gdx.files.internal("solid-lines-2D.vert"),
                Gdx.files.internal("solid-lines-2D.frag"));
        if (solidShader.getLog().length() > 0) {
            log.error("SOLID SHADER compile error: {}", solidShader.getLog());
        }

        dashShader = new ShaderProgram(
                Gdx.files.internal("dash-lines-2D.vert"),
                Gdx.files.internal("dash-lines-2D.frag"));
        if (dashShader.getLog().length() > 0) {
            log.error("DASH SHADER compile error: {}", dashShader.getLog());
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter para = new FreeTypeFontGenerator.FreeTypeFontParameter();
        para.size = 20;
        para.color = com.badlogic.gdx.graphics.Color.RED;

        font = generator.generateFont(para);

        mMapRenderer = new MapRenderer(this.mMap);
        mMapRenderer.onSurfaceCreated();

        pathPoints.add(new PointF(20, 20), new PointF(100, 20), new PointF(50, 50));

        createUi();
    }


    private void createUi() {
        mainStage = new Stage();
        Gdx.input.setInputProcessor(mainStage);
        contextTable = new Table();
        mainStage.addActor(contextTable);


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
    }

    EventListener listener = new ClickListener() {
        public void clicked(InputEvent event, float x, float y) {
            log.debug("Actor Clicked");


            {//Update VTM
                synchronized (l) {
                    l.clear();
                    GeometryBuffer g = mLine;
                    g.clear();
                    g.startLine();
                    for (PointF poi : pathPoints) {
                        g.addPoint(poi.x, poi.y);
                    }

                    Cap cap = Cap.ROUND;
                    if (btnCapButt.isChecked()) cap = Cap.BUTT;
                    else if (btnCapSquare.isChecked()) cap = Cap.SQUARE;

                    addLines(l, 0, true, false, cap);

                }
                mMap.updateMap(true);
            }

            {//update mapsforge
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bmp = GRAPHIC_FACTORY.createBitmap(200, 100);
                        Canvas canvas = GRAPHIC_FACTORY.createCanvas();
                        canvas.setBitmap(bmp);

                        Path path = GRAPHIC_FACTORY.createPath();
                        for (PointF poi : pathPoints) {
                            if (path.isEmpty()) {
                                path.moveTo(poi.x, poi.y);
                            } else {
                                path.lineTo(poi.x, poi.y);
                            }
                        }

                        Paint paint = GRAPHIC_FACTORY.createPaint();
                        paint.setColor(org.mapsforge.core.graphics.Color.RED);
                        paint.setStrokeWidth(20f);

                        org.mapsforge.core.graphics.Cap cap = org.mapsforge.core.graphics.Cap.ROUND;
                        if (btnCapButt.isChecked()) cap = org.mapsforge.core.graphics.Cap.BUTT;
                        else if (btnCapSquare.isChecked()) cap = org.mapsforge.core.graphics.Cap.SQUARE;
                        paint.setStrokeCap(cap);

                        paint.setStyle(Style.STROKE);

                        canvas.fillColor(org.mapsforge.core.graphics.Color.BLUE);
                        canvas.drawPath(path, paint);
//                        canvas.drawLine(10, 10, 50, 50, paint);


                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        try {
                            bmp.compress(bos);
                            byte[] bytes = bos.toByteArray();
                            Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
                            texture = new Texture(pixmap, Pixmap.Format.RGB565, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

        }
    };

    @Override
    public void render() {
        super.render();
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderVtm();

        batch.setShader(defaultShader);
        batch.begin();
        font.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()) + " fps", fpsX, fpsY);
        for (int i = 0; i < 1; i++) {
            batch.draw(texture, 0, 200, 200, 100);
        }
        batch.end();

        mainStage.act();
        mainStage.draw();

        renderSolid();


        Gdx.graphics.requestRendering();
    }


    private void renderSolid() {


    }

    private void renderVtm() {
        GLState.enableVertexArrays(-1, -1);
        GLAdapter.gl.frontFace(GL.CW);

        try {
            mMapRenderer.onDrawFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GLState.bindVertexBuffer(0);
        GLState.bindElementBuffer(0);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        fpsX = 10;
        fpsY = height - 20;
        mainStage.getViewport().update(width, height, true);
        contextTable.setBounds(0, 0, width, height / 2);
    }


    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
        font.dispose();
        dashShader.dispose();
        solidShader.dispose();
        dashShader.dispose();
        gl = null;
        mainStage.dispose();
        super.dispose();
    }


    class LineTest extends BucketRenderer {

        public LineTest() {
            mMapPosition.scale = 0;
        }

        public synchronized void clear() {
            buckets.clear();
            setReady(false);
        }

        @Override
        public synchronized void update(GLViewport v) {

            if (mMapPosition.scale == 0)
                mMapPosition.copy(v.pos);

            if (!isReady()) {
                compile();
            }
        }
    }

}
