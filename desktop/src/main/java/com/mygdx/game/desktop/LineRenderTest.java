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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.oscim.backend.GL;
import org.oscim.backend.GLAdapter;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.GLState;
import org.oscim.renderer.MapRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineRenderTest extends GdxMapApp {

    Logger log = LoggerFactory.getLogger(LineRenderTest.class);

    SpriteBatch batch;
    ShaderProgram defaultShader, solidShader, dashShader;
    GL20 gl;
    float fpsX, fpsY;
    GUI mainStage;

    float segmentWidth;
    float segmentHeight;

    VTM vtm = new VTM();
    MAPSFORGE_VTM mapsforge_vtm = new MAPSFORGE_VTM();
    MAPSFORGE mapsforge;

    @Override
    public void createLayers() {
        MapRenderer.setBackgroundColor(0xff000000);
        mMap.setMapPosition(0, 0, 1 << 4);
        mMap.layers().add(new GenericLayer(mMap, mapsforge_vtm));
        mMap.layers().add(new GenericLayer(mMap, vtm));
    }


    @Override
    public void create() {
        super.create();

        float segmentWidth = Gdx.graphics.getWidth() / 3;
        float segmentHeight = Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 3);
        mapsforge = new MAPSFORGE(segmentWidth, segmentHeight);


        mainStage = new GUI(mMap, vtm, mapsforge_vtm, mapsforge);
        batch = new SpriteBatch();

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

        mMapRenderer = new MapRenderer(this.mMap);
        mMapRenderer.onSurfaceCreated();

        mainStage.create();
    }


    @Override
    public void render() {
        super.render();
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderVtm();

        batch.setShader(defaultShader);
        batch.begin();
        mainStage.font.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()) + " fps", fpsX, fpsY);

        mainStage.drawMapsforge(batch);

        batch.end();

        mainStage.act();
        mainStage.draw();

        renderSolid();

        batch.begin();
        mainStage.drawBoundingBoxes(batch);
        batch.end();
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
        mainStage.resize(width, height);
    }


    @Override
    public void dispose() {
        batch.dispose();
        dashShader.dispose();
        solidShader.dispose();
        dashShader.dispose();
        gl = null;
        mainStage.dispose();
        super.dispose();
    }
}
