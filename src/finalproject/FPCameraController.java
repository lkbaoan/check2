/** ****************************************************************
 * file: FPCameraController.java
 * author: Thien Long Dinh (id:015792764), Thien Luat Dinh (id:015792777), An Le (id: 014128231)
 * class: CS 4450 - Computer Graphics
 *
 * assignment: final program - checkpoint 1
 * date last modified: 05/06/2024
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

    private float playerHeight = 7.01f; // Approximate height of the player
    private float playerWidth = 5.01f; // Width of the player

    private boolean dayNightCycleActive = false;
    private float cycleTimer = 0.0f;
    private float cycleDuration = 24.0f;

    // Constructor: FPCameraController
    // purpose: Constructor for FPCameraController
    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        yaw = 0.0f;
        pitch = 0.0f;
        lightPosition = new Vector3f(30.0f, 30.0f, 30.0f);
        chunk = new Chunk((int) x, (int) y, (int) z);
    }

    // method: updateLightPosition
    // purpose: change light position
    private void updateLightPosition(float deltaTime) {
        if (dayNightCycleActive) {
            cycleTimer += deltaTime;

            // Calculate position based on cycle
            float angle = (cycleTimer / cycleDuration) * 2 * (float) Math.PI;
            lightPosition.y = 20.0f + 10.0f * (float) Math.sin(angle); // Changes height
            lightPosition.x = 30.0f + 20.0f * (float) Math.cos(angle); // Orbit around the center horizontally
            lightPosition.z = 30.0f + 20.0f * (float) Math.sin(angle); // Orbit around the center depth-wise

            System.out.println("Light Position: " + lightPosition.x + ", " + lightPosition.y + ", " + lightPosition.z);

            if (cycleTimer >= cycleDuration) {
                cycleTimer = 0.0f; // Reset the cycle
            }
        }
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

    // method: canMoveTo
    // purpose: check if player can move to a location
    private boolean canMoveTo(Vector3f newPos) {
        AABB playerBox = getPlayerAABB(newPos);
        return !chunk.checkCollision(playerBox);
    }

    // method: getPlayerAABB
    // purpose: get plaer bounding box
    private AABB getPlayerAABB(Vector3f newPos) {
        Vector3f min = new Vector3f(newPos.x - playerWidth / 2, newPos.y, newPos.z - playerWidth / 2);
        Vector3f max = new Vector3f(newPos.x + playerWidth / 2, newPos.y + playerHeight, newPos.z + playerWidth / 2);
        return new AABB(min, max);
    }

    // method: move
    // purpose: move in a direction
    private void move(Vector3f direction, float distance) {
        Vector3f newPos = new Vector3f(position);
        newPos.x += direction.x * distance;
        newPos.y += direction.y * distance;
        newPos.z += direction.z * distance;

        if (canMoveTo(newPos)) {
            position.set(newPos);
        }
    }

    // method: walkForward
    // purpose: move camera forward
    private void walkForward(float distance) {
        move(new Vector3f((float) Math.sin(Math.toRadians(yaw)), 0, -(float) Math.cos(Math.toRadians(yaw))), distance);
    }

    // method: walkBackward
    // purpose: move camera backward
    private void walkBackwards(float distance) {
        move(new Vector3f(-(float) Math.sin(Math.toRadians(yaw)), 0, (float) Math.cos(Math.toRadians(yaw))), distance);
    }

    // method: strafeLeft
    // purpose: move camera to the left
    private void strafeLeft(float distance) {
        move(new Vector3f((float) Math.sin(Math.toRadians(yaw - 90)), 0, -(float) Math.cos(Math.toRadians(yaw - 90))), distance);
    }

    // method: strafeRight
    // purpose: move camera to the right
    private void strafeRight(float distance) {
        move(new Vector3f((float) Math.sin(Math.toRadians(yaw + 90)), 0, -(float) Math.cos(Math.toRadians(yaw + 90))), distance);
    }

    // method: moveUp
    // purpose: move camera upward
    private void moveUp(float distance) {
        move(new Vector3f(0, 1, 0), distance);
    }

    // method: moveUp
    // purpose: move camera upward
    private void moveDown(float distance) {
        move(new Vector3f(0, -1, 0), distance);
    }

    // method: gameLoop
    // purpose: loop to receive command to render graphics or exit
    public void gameLoop() {
        long lastTime = System.currentTimeMillis();
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - lastTime) / 1000.0f;
            lastTime = currentTime;

            processInput();
            updateLightPosition(deltaTime); // Update the light position based on time
            lookThrough();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
                chunk.changeTexture();
            }
            chunk.render();
            Display.update();
            Display.sync(60);
        }
    }

    // method: processInput
    // purpose: call for update for yaw and pitch, and call move method based on keydown
    private void processInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
            dayNightCycleActive = !dayNightCycleActive; // Toggle cycle on/off with 'P' key
        }

        float mouseDX = Mouse.getDX() * 0.2f;
        float mouseDY = Mouse.getDY() * 0.2f;
        yaw(mouseDX);
        pitch(-mouseDY);

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            walkForward(0.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            walkBackwards(0.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            strafeLeft(0.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            strafeRight(0.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            moveUp(0.35f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            moveDown(0.35f);
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
        lightPositionBuffer.put(lightPosition.x).put(lightPosition.y).put(lightPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPositionBuffer);
    }

}
