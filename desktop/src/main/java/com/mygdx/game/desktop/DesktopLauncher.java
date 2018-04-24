package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.oscim.awt.AwtGraphics;
import org.oscim.backend.GLAdapter;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMapApp;
import org.oscim.gdx.LwjglGL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.LibgdxLogger;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LibgdxLogger.PROPERTIES_FILE_HANDLE = new LwjglFileHandle(LibgdxLogger.CONFIGURATION_FILE_XML, Files.FileType.Local);

        new SharedLibraryLoader().load("vtm-jni");
        AwtGraphics.init();
        GdxAssets.init("assets/");
        GLAdapter.init(new LwjglGL20());

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.useHDPI = true;
        config.samples = 1;
        config.x = 100;
        config.y = 0;
        config.width = 800;
        config.height = 600;
        config.title = "Line Shader Test";
        config.stencil = 8;
        config.foregroundFPS = 0;
        config.backgroundFPS = 0;


        GdxMapApp mapAdapter = new LineRenderTest();

        LwjglApplication application = new LwjglApplication(mapAdapter, config);
        application.setLogLevel(LwjglApplication.LOG_DEBUG);
        Logger log = LoggerFactory.getLogger("a");// initial LoggerFactory after application creation!

    }
}
