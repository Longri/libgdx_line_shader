package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.PointF;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.MapsforgeLineStyle;

import java.io.IOException;

/**
 * Created by Longri on 26.04.18.
 */
public class VTM extends BucketRenderer {

    GeometryBuffer mLine = new GeometryBuffer(2, 1);

    public VTM() {
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

    public void lineChanged(Array<PointF> pathPoints, MapsforgeLineStyle style) {
        synchronized (this) {
            this.clear();
            mLine.clear();
            mLine.startLine();
            for (PointF poi : pathPoints) {
                mLine.addPoint(poi.x, poi.y);
            }
            LineStyle line1 = new LineStyle(0, null, style.strokeColor, style.stroke_width, style.stroke_linecap,
                    true, 0, 0, 0, 0, 0f, false,
                    null, true, null, LineStyle.REPEAT_START_DEFAULT, LineStyle.REPEAT_GAP_DEFAULT);


            TextureItem tex = null;
            try {
                tex = new TextureItem(CanvasAdapter.getBitmapAsset("", "patterns/dot.png"));
                tex.mipmap = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            //translate to position inside boundingBox
            LineBucket ll = this.buckets.addLineBucket(10, line1);
            float dx = (Gdx.graphics.getWidth() / 2) - (Gdx.graphics.getWidth() / 3);
            float dy = Gdx.graphics.getHeight() / 2;
            ll.addLine(mLine.translate(-dx, -dy));
        }

    }
}
