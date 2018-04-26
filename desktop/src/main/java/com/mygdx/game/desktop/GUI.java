package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import org.mapsforge.core.graphics.*;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.PointF;
import org.oscim.map.Map;
import org.oscim.theme.styles.MapsforgeLineStyle;
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
    CheckBox btnLineJoinMitter;
    CheckBox btnLineJoinRound;
    CheckBox btnLineJoinBevel;
    TextArea pathTextField;
    TextArea colorField;
    TextArea widthField;

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

                    org.mapsforge.core.graphics.Paint pointPaint = GRAPHIC_FACTORY.createPaint();
                    pointPaint.setColor(Color.BLUE);

                    for (PointF poi : pathPoints) {
                        canvas.drawCircle((int) poi.x, (int) poi.y, 3, pointPaint);
                    }

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

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter para = new FreeTypeFontGenerator.FreeTypeFontParameter();
        para.size = 20;
        para.color = com.badlogic.gdx.graphics.Color.RED;
        font = generator.generateFont(para);

        FileHandle skinFile = Gdx.files.internal("clean-crispy-ui.json");
        FileHandle atlas = Gdx.files.internal("clean-crispy-ui.atlas");
        Skin skin = new Skin();

        skin.add("font", font, BitmapFont.class);
        skin.addRegions(new TextureAtlas(atlas));
        skin.load(skinFile);

        String pathString = "20,60,100,60,100,20,250,250,20,380";
        pathTextField = new TextArea(pathString, skin);
        colorField = new TextArea("#ff0000", skin);
        widthField = new TextArea("15", skin);
        Label pathLabel = new Label("Path:", skin);
        Label capLabel = new Label("Line Cap:", skin);
        Label joinLabel = new Label("Line Join:", skin);
        Label colorLabel = new Label("Line Color:", skin);
        Label widthLabel = new Label("Line Width:", skin);


        Gdx.input.setInputProcessor(this);
        contextTable = new Table();
        contextTable.setDebug(true, true);
        this.addActor(contextTable);


        btnCapButt = new CheckBox("butt", skin);
        btnCapRound = new CheckBox("round", skin);
        btnCapSquare = new CheckBox("square", skin);
        btnLineJoinMitter = new CheckBox("miter", skin);
        btnLineJoinRound = new CheckBox("round", skin);
        btnLineJoinBevel = new CheckBox("bevel", skin);
        TextButton btnUpdate = new TextButton("update", skin);

        btnCapButt.addListener(listener);
        btnCapRound.addListener(listener);
        btnCapSquare.addListener(listener);
        btnLineJoinMitter.addListener(listener);
        btnLineJoinRound.addListener(listener);
        btnLineJoinBevel.addListener(listener);
        btnUpdate.addListener(listener);


        ButtonGroup buttonGroup = new ButtonGroup(btnCapButt, btnCapRound, btnCapSquare);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);
        buttonGroup.setUncheckLast(true); //If true, when the maximum number of buttons are checked and an additional button is checked, the last button to be checked is unchecked so that the maximum is not exceeded.
        btnCapRound.setChecked(true);


        ButtonGroup buttonGroup2 = new ButtonGroup(btnLineJoinMitter, btnLineJoinRound, btnLineJoinBevel);
        buttonGroup2.setMaxCheckCount(1);
        buttonGroup2.setMinCheckCount(0);
        buttonGroup2.setUncheckLast(true); //If true, when the maximum number of buttons are checked and an additional button is checked, the last button to be checked is unchecked so that the maximum is not exceeded.
        btnLineJoinRound.setChecked(true);

        contextTable.add(pathLabel);
        contextTable.add(pathTextField).colspan(2).expandX().fillX();
        contextTable.add(btnUpdate).row();

        contextTable.add(colorLabel);
        contextTable.add(colorField).colspan(2).expandX().fillX();
        contextTable.add((Actor) null).row();

        contextTable.add(widthLabel);
        contextTable.add(widthField).colspan(2).expandX().fillX();
        contextTable.add((Actor) null).row();

        contextTable.add(capLabel);
        contextTable.add(btnCapButt).left();
        contextTable.add(joinLabel);
        contextTable.add(btnLineJoinMitter).left().row();


        contextTable.add((Actor) null);
        contextTable.add(btnCapRound).left();
        contextTable.add((Actor) null);
        contextTable.add(btnLineJoinRound).left().row();

        contextTable.add((Actor) null);
        contextTable.add(btnCapSquare).left();
        contextTable.add((Actor) null);
        contextTable.add(btnLineJoinBevel).left().row();

        listener.clicked(null, 0, 0);
    }

    ClickListener listener = new ClickListener() {
        public void clicked(InputEvent event, float x, float y) {
            log.debug("Actor Clicked");

            //extract Path
            pathPoints.clear();
            String pathString = pathTextField.getText();
            String[] values = pathString.split(",");
            float valueX = 0;
            boolean xSet = false;
            for (String value : values) {
                float f = Float.parseFloat(value);
                if (xSet) {
                    pathPoints.add(new PointF(valueX, f));
                    xSet = false;
                } else {
                    valueX = f;
                    xSet = true;
                }
            }

            MapsforgeLineStyle style = getStyle();


            //Update VTM
            vtm.lineChanged(pathPoints, style);
            mMap.updateMap(true);

            //update mapsforge
            mapsforge.lineChanged(pathPoints, style);

            boundigTexture = null;// create new with path points

        }
    };

    private MapsforgeLineStyle getStyle() {

        Paint.Cap cap = Paint.Cap.ROUND;
        if (btnCapButt.isChecked()) cap = Paint.Cap.BUTT;
        else if (btnCapSquare.isChecked()) cap = Paint.Cap.SQUARE;

        Paint.Join join = Paint.Join.ROUND;
        if (btnLineJoinMitter.isChecked()) join = Paint.Join.MITER;
        else if (btnLineJoinBevel.isChecked()) join = Paint.Join.BEVEL;

        int strokeColor = org.oscim.backend.canvas.Color.parseColor(colorField.getText());
        float strokeWidth = Float.parseFloat(widthField.getText());

        return new MapsforgeLineStyle(strokeColor, strokeWidth, cap, join);
    }

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
