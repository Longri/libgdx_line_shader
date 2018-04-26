package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import org.mapsforge.core.graphics.*;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.PointF;
import org.oscim.theme.styles.MapsforgeLineStyle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Longri on 26.04.18.
 */
public class MAPSFORGE {

    final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;
    Texture texture;

    final float width;
    final float height;


    public MAPSFORGE(float width, float height) {
        this.width = width;
        this.height = height;
    }


    public void lineChanged(Array<PointF> pathPoints, MapsforgeLineStyle style) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = GRAPHIC_FACTORY.createBitmap((int) width, (int) height);
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

                org.mapsforge.core.graphics.Paint paint = GRAPHIC_FACTORY.createPaint();
                paint.setColor(style.strokeColor);
                paint.setStrokeWidth(style.stroke_width);

                Cap mapsforgeCap = Cap.ROUND;
                if (style.stroke_linecap == Paint.Cap.BUTT) mapsforgeCap = Cap.BUTT;
                else if (style.stroke_linecap == Paint.Cap.SQUARE) mapsforgeCap = Cap.SQUARE;
                paint.setStrokeCap(mapsforgeCap);


                Join mapsforgeJoin = Join.ROUND;
                if (style.stroke_linejoin == Paint.Join.BEVEL) mapsforgeJoin = Join.BEVEL;
                else if (style.stroke_linejoin == Paint.Join.MITER) mapsforgeJoin = Join.MITER;
                paint.setStrokeJoin(mapsforgeJoin);

                paint.setStyle(Style.STROKE);


                canvas.fillColor(org.mapsforge.core.graphics.Color.TRANSPARENT);
                canvas.drawPath(path, paint);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    bmp.compress(bos);
                    byte[] bytes = bos.toByteArray();
                    Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
                    texture = new Texture(pixmap, Pixmap.Format.RGBA8888, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void dispose() {
        texture.dispose();
    }

    public void draw(SpriteBatch batch, float yPos) {
        batch.draw(texture, 0, yPos, texture.getWidth(), texture.getHeight());
    }
}
