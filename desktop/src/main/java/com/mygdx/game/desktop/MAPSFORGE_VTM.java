package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MapPosition;
import org.oscim.core.PointF;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.*;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.MapsforgeLineStyle;
import org.oscim.utils.FastMath;

import static org.oscim.renderer.bucket.RenderBucket.*;
import static org.oscim.renderer.bucket.RenderBucket.CIRCLE;
import static org.oscim.renderer.bucket.RenderBucket.SYMBOL;

/**
 * Created by Longri on 26.04.18.
 */
public class MAPSFORGE_VTM extends BucketRenderer {


    GeometryBuffer mLine = new GeometryBuffer(2, 1);

    public MAPSFORGE_VTM() {
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

    /**
     * Render all 'buckets'
     */
    @Override
    public synchronized void render(GLViewport v) {
        MapPosition layerPos = mMapPosition;

        GLState.test(false, false);
        GLState.blend(true);

        float div = (float) (v.pos.scale / layerPos.scale);

        boolean project = true;

        setMatrix(v, project);

        for (RenderBucket b = buckets.get(); b != null; ) {

            buckets.bind();

            if (!project && b.type != SYMBOL) {
                project = true;
                setMatrix(v, project);
            }

            switch (b.type) {
                case POLYGON:
                    b = PolygonBucket.Renderer.draw(b, v, 1, true);
                    break;
                case LINE:
                    b = LineBucket.Renderer.draw(b, v, div, buckets);
                    break;
                case TEXLINE:
                    b = LineTexBucket.Renderer.draw(b,
                            v,
                            FastMath.pow(layerPos.zoomLevel - v.pos.zoomLevel) * (float) layerPos.getZoomScale(),
                            buckets);
                    break;
                case MESH:
                    b = MeshBucket.Renderer.draw(b, v);
                    break;
                case HAIRLINE:
                    b = HairLineBucket.Renderer.draw(b, v);
                    break;
                case BITMAP:
                    b = BitmapBucket.Renderer.draw(b, v, 1, 1);
                    break;
                case SYMBOL:
                    if (project) {
                        project = false;
                        setMatrix(v, project);
                    }
                    b = TextureBucket.Renderer.draw(b, v, div);
                    break;
                case CIRCLE:
                    b = CircleBucket.Renderer.draw(b, v);
                    break;
                default:
                    log.error("invalid bucket {}", b.type);
                    b = b.next;
                    break;
            }
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

            //translate to position inside boundingBox

            ShaderLineBucket slb=new ShaderLineBucket(11);
            slb= (ShaderLineBucket) this.buckets.addDefaultBucket(11,slb);

//            HairLineBucket ll = this.buckets.addHairLineBucket(11, line1);

            slb.line=line1;

            float dx = (Gdx.graphics.getWidth() / 2) - ((Gdx.graphics.getWidth() / 3) * 2);
            float dy = Gdx.graphics.getHeight() / 2;


            slb.addLine(mLine.translate(-dx, -dy));
        }

    }
}
