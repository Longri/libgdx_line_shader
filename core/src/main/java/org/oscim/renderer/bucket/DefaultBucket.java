package org.oscim.renderer.bucket;

import org.oscim.renderer.GLViewport; /**
 * Created by Longri on 27.04.18.
 */
public abstract class DefaultBucket extends RenderBucket {
    protected DefaultBucket( boolean indexed, boolean quads) {
        super(RenderBucket.DEFAULT, indexed, quads);
    }

    public abstract RenderBucket getNewInstance(int level);

    public abstract int getVertexShortCnt();

    public abstract RenderBucket draw(RenderBucket b, GLViewport v, float div, RenderBuckets buckets);
}
