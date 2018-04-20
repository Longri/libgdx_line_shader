package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.mygdx.game.MyGdxGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.LibgdxLogger;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LibgdxLogger.PROPERTIES_FILE_HANDLE = new LwjglFileHandle(LibgdxLogger.CONFIGURATION_FILE_XML, Files.FileType.Local);


        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        LwjglApplication application = new LwjglApplication(new MyGdxGame(), config);

        application.setLogLevel(LwjglApplication.LOG_DEBUG);

        Logger log=LoggerFactory.getLogger("a");// initial LoggerFactory after application creation!

    }
}
