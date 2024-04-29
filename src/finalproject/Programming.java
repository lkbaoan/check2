/** ****************************************************************
 * file: Programming.java
 * author: Thien Long Dinh (id:015792764), Thien Luat Dinh (id:015792777), An Le (id: 014128231)
 * class: CS 4450 - Computer Graphics
 *
 * assignment: final program - checkpoint 1
 * date last modified: 04/29/2024
 *
 * purpose: This file start the program and launch camera controller
 ***************************************************************** */
package finalproject;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class Programming {

    private FPCameraController camera;
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;

    // method: main
    // purpose: start the program
    public static void main(String[] args) {
        Programming viewer = new Programming();
        viewer.start();
    }

    // method: start
    // purpose: start a new window and start camera render
    public void start() {
        try {
            createWindow();
            initGL();
            camera = new FPCameraController(0, 50, -40);
            camera.gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method: createWindow
    // purpose: create a new window display with set size and title
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Simple 3D Viewer");
        Display.create();
    }

    // method: initGL
    // purpose: initilize openGL task
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glMatrixMode(GL_ONE);
        initLightArrays();
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
//
//        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
//        lightPosition.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
//        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
//
//        FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4);
//        lightAmbient.put(0.2f).put(0.2f).put(0.2f).put(1.0f).flip();
//        glLight(GL_LIGHT0, GL_AMBIENT, lightAmbient);
//
//        FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4);
//        lightDiffuse.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
//        glLight(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
    }

    // method: initLightArrays
    // purpose: initalize light arrays
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }

}
