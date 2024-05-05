/** ****************************************************************
 * file: FPCameraController.java
 * author: Thien Long Dinh (id:015792764), Thien Luat Dinh (id:015792777), An Le (id: 014128231)
 * class: CS 4450 - Computer Graphics
 *
 * assignment: final program - checkpoint 1
 * date last modified: 04/29/2024
 *
 * purpose: This file handle camera movement and render of the cube
 ***************************************************************** */
package finalproject;

// Inner private class that handle camera controll.
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public class FPCameraController {

    private Vector3f position;
    private Vector3f lightPosition;
    private float yaw;
    private float pitch;
    private Chunk chunk;

    private float playerHeight = 0.1f; // Approximate height of the player
    private float playerWidth = 0.1f; // Width of the player

    // method: FPCameraController
    // purpose: Constructor for FPCameraController
    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        yaw = 40.0f;
        pitch = 45.0f;
        lightPosition = new Vector3f(20.0f, 60.0f, 0.0f);
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

    private boolean canMoveTo(Vector3f newPos) {
        AABB playerBox = getPlayerAABB(newPos);
        return !chunk.checkCollision(playerBox);
    }

    private AABB getPlayerAABB(Vector3f newPos) {
        Vector3f min = new Vector3f(newPos.x - playerWidth / 2, newPos.y, newPos.z - playerWidth / 2);
        Vector3f max = new Vector3f(newPos.x + playerWidth / 2, newPos.y + playerHeight, newPos.z + playerWidth / 2);
        return new AABB(min, max);
    }

    private void move(Vector3f direction, float distance) {
        Vector3f newPos = new Vector3f(position);
        newPos.x += direction.x * distance;
        newPos.y += direction.y * distance;
        newPos.z += direction.z * distance;

        if (canMoveTo(newPos)) {
            position.set(newPos);
        }
    }

    private void walkForward(float distance) {
        move(new Vector3f((float) Math.sin(Math.toRadians(yaw)), 0, -(float) Math.cos(Math.toRadians(yaw))), distance);
    }

    private void walkBackwards(float distance) {
        move(new Vector3f(-(float) Math.sin(Math.toRadians(yaw)), 0, (float) Math.cos(Math.toRadians(yaw))), distance);
    }

    private void strafeLeft(float distance) {
        move(new Vector3f((float) Math.sin(Math.toRadians(yaw - 90)), 0, -(float) Math.cos(Math.toRadians(yaw - 90))), distance);
    }

    private void strafeRight(float distance) {
        move(new Vector3f((float) Math.sin(Math.toRadians(yaw + 90)), 0, -(float) Math.cos(Math.toRadians(yaw + 90))), distance);
    }

    private void moveUp(float distance) {
        move(new Vector3f(0, 1, 0), distance);
    }

    private void moveDown(float distance) {
        move(new Vector3f(0, -1, 0), distance);
    }

    // method: walkForward
    // purpose: move camera forward
//    private void walkForward(float distance) {
//        float deltaX = distance * (float) Math.sin(Math.toRadians(yaw));
//        float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw));
//        position.x += deltaX;
//        position.z -= deltaZ;
//    }
//
//    // method: walkBackward
//    // purpose: move camera backward
//    private void walkBackwards(float distance) {
//        float deltaX = distance * (float) Math.sin(Math.toRadians(yaw));
//        float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw));
//        position.x -= deltaX;
//        position.z += deltaZ;
//    }
//
//    // method: strafeLeft
//    // purpose: move camera to the left
//    private void strafeLeft(float distance) {
//        float deltaX = distance * (float) Math.sin(Math.toRadians(yaw - 90));
//        float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw - 90));
//        position.x += deltaX;
//        position.z -= deltaZ;
//
//    }
//
//    // method: strafeRight
//    // purpose: move camera to the right
//    private void strafeRight(float distance) {
//        float deltaX = distance * (float) Math.sin(Math.toRadians(yaw + 90));
//        float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw + 90));
//        position.x += deltaX;
//        position.z -= deltaZ;
//
//    }
//
//    // method: moveUp
//    // purpose: move camera upward
//    private void moveUp(float distance) {
//        position.y += distance;
//    }
//
//    // method: moveDown
//    // purpose: move camera downward
//    private void moveDown(float distance) {
//        position.y -= distance;
//    }
    // method: gameLoop
    // purpose: loop to receive command to render graphics or exit
    public void gameLoop() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
//            Keyboard.poll();
            processInput();
            lookThrough();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            chunk.render();
            if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
                chunk.changeTexture();
            }
            Display.update();
            Display.sync(60);
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

        FloatBuffer lightPositionBuffer = BufferUtils.createFloatBuffer(4);
        lightPositionBuffer.put(lightPosition.x).put(
                lightPosition.y).put(lightPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPositionBuffer);
    }

}
