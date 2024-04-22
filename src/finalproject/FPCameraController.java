/** ****************************************************************
 * file: FPCameraController.java
 * author: Thien Long Dinh (id:015792764), Thien Luat Dinh (id:015792777), An Le (id: 014128231)
 * class: CS 4450 - Computer Graphics
 *
 * assignment: final program - checkpoint 1
 * date last modified: 04/22/2024
 *
 * purpose: This file handle camera movement and render of the cube
 ***************************************************************** */
package finalproject;

// Inner private class that handle camera controll.
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import org.lwjgl.util.vector.Vector3f;

public class FPCameraController {

    private Vector3f position;
    private float yaw;
    private float pitch;

    private Chunk chunk;

    // method: FPCameraController
    // purpose: Constructor for FPCameraController
    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        yaw = 0.0f;
        pitch = 0.0f;

        chunk = new Chunk((int) x, (int) y, (int) z);
    }

    // method: yaw
    // purpose: Change yaw value
    private void yaw(float amount) {
        yaw += amount;
    }

    // method: pitch
    // purpose: Change pitch value
    private void pitch(float amount) {
        pitch += amount;
        if (pitch < -90) {
            pitch = -90;
        } else if (pitch > 90) {
            pitch = 90;
        }
    }

    // method: walkForward
    // purpose: move camera forward
    private void walkForward(float distance) {
        float deltaX = distance * (float) Math.sin(Math.toRadians(yaw));
        float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += deltaX;
        position.z -= deltaZ;
    }

    // method: walkBackward
    // purpose: move camera backward
    private void walkBackwards(float distance) {
        float deltaX = distance * (float) Math.sin(Math.toRadians(yaw));
        float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x -= deltaX;
        position.z += deltaZ;
    }

    // method: strafeLeft
    // purpose: move camera to the left
    private void strafeLeft(float distance) {
        float deltaX = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x += deltaX;
        position.z -= deltaZ;
    }

    // method: strafeRight
    // purpose: move camera to the right
    private void strafeRight(float distance) {
        float deltaX = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x += deltaX;
        position.z -= deltaZ;
    }

    // method: moveUp
    // purpose: move camera upward
    private void moveUp(float distance) {
        position.y += distance;
    }

    // method: moveDown
    // purpose: move camera downward
    private void moveDown(float distance) {
        position.y -= distance;
    }

    // method: gameLoop
    // purpose: loop to receive command to render graphics or exit
    public void gameLoop() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            Keyboard.poll();
            processInput();
            lookThrough();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            chunk.render();

            Display.update();
            Display.sync(60);
        }
    }

    // method: render
    // purpose: render graphics
    private void render() {
        try {
            glBegin(GL_QUADS);

            // Top face (blue)
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);

            // Bottom face (orange)
            glColor3f(1.0f, 0.5f, 0.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);

            // Front face (red)
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);

            // Back face (green)
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);

            // Left face (yellow)
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);

            // Right face (magenta)
            glColor3f(1.0f, 0.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);

            glEnd();
        } catch (Exception e) {
        }
    }

    // method: processInput
    // purpose: call for update for yaw and pitch, and call move method based on keydown
    private void processInput() {
        float mouseDX = Mouse.getDX() * 0.2f;
        float mouseDY = Mouse.getDY() * 0.2f;
        yaw(mouseDX);
        pitch(-mouseDY);

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            walkForward(.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            walkBackwards(.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            strafeLeft(.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            strafeRight(.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            moveUp(.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            moveDown(.35f);
        }
    }

    // method: lookThrough
    // purpose: ranslates and rotate the matrix so that it looks through the camera
    private void lookThrough() {
        glLoadIdentity();
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(-position.x, -position.y, -position.z);
    }

}
