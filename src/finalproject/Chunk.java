/** ****************************************************************
 * file: Programming.java
 * author: Thien Long Dinh (id:015792764), Thien Luat Dinh (id:015792777), An Le (id: 014128231)
 * class: CS 4450 - Computer Graphics
 *
 * assignment: final program - checkpoint 2
 * date last modified: 04/22/2024
 *
 * purpose: This file hold chunk information and its render.
 ***************************************************************** */
package finalproject;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    private int vboVertexHandle;
    private int vboColorHandle;
    private int vboTextureHandle;
    private int startX, startY, startZ;
    private Random r;
    private Texture texture;
    private SimplexNoise noise;
    private boolean changeTexture;
    private double[][] noiseHolder;
    private Block.BlockType[][][] blockTypeHolder;

    // method: render
    // purpose: render blocks in chunk
    public void render() {
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }

    // method: rebuildMesh
    // purpose: build block formation that randomly generated each time the program is ran.
    public void rebuildMesh(float startX, float startY, float startZ) {
        vboColorHandle = glGenBuffers();
        vboVertexHandle = glGenBuffers();
        vboTextureHandle = glGenBuffers();
        Random rand = new Random();
        noise = new SimplexNoise(100, 0.5, rand.nextInt(10000));
        
        FloatBuffer vertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer vertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer vertexTextureData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int adjustedX = (int) startX + x;
                int adjustedZ = (int) startZ + z;
                
                double noiseValue = noise.getNoise(adjustedX, adjustedZ);
                noiseHolder[x][z] = noiseValue;
                int height = (int) (noiseValue * 10 + 10);
                
                for (int y = 0; y < height; y++) {
                    Block.BlockType blockType;
                    if (y == height - 1) {
                        float topBlockType = rand.nextFloat();
                        if (topBlockType < 0.33) {
                            blockType = Block.BlockType.BlockType_Grass;
                        } else if (topBlockType < 0.66) {
                            blockType = Block.BlockType.BlockType_Sand;
                        } else {
                            blockType = Block.BlockType.BlockType_Water;
                        }
                    } else if (y == 0) {
                        blockType = Block.BlockType.BlockType_Bedrock;
                    } else {
                        blockType = rand.nextFloat() > 0.5 ? Block.BlockType.BlockType_Dirt : Block.BlockType.BlockType_Stone;
                    }
                    blockTypeHolder[x][y][z] = blockType;
                    blocks[x][y][z] = new Block(blockType);
//                    blocks[x][y][z].setActive(true);
                    float baseHeight = (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * 0.8));
                    vertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), baseHeight, (float) (startZ + z * CUBE_LENGTH)));
                    vertexColorData.put(createCubeVertexCol(getCubeColor(blocks[x][y][z])));
                    vertexTextureData.put(createTexCube(0, 0, blocks[x][y][z], changeTexture));
                }
            }
        }
        
        vertexColorData.flip();
        vertexPositionData.flip();
        vertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    // method: rebuildTexture
    // purpose: rebuild Texture with normal/alien texture
    private void rebuildTexture() {
        FloatBuffer vertexTextureData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int height = (int) (noiseHolder[x][z] * 10 + 10);
                
                for (int y = 0; y < height; y++) {
                    blocks[x][y][z] = new Block(blockTypeHolder[x][y][z]);
                    vertexTextureData.put(createTexCube(0, 0, blocks[x][y][z], changeTexture));
                }
            }
        }
        vertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    // method: createCubeVertexCol
    // purpose: return cube colors
    private float[] createCubeVertexCol(float[] cubeColorArray) {
        float[] cubeColors = new float[cubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = cubeColorArray[i % cubeColorArray.length];
        }
        return cubeColors;
    }

    // method: createTexCube
    // purpose: return cube texture 
    private static float[] createTexCube(float x, float y, Block block, boolean terrain) {
        float offset = (1024f / 16) / 1024f;
        boolean terrainChange = terrain;
        switch (block.getId()) {
            case 0: // Grass
                int locX1G = 3,
                 locX2G = 2;
                int locX3G = 4,
                 locX4G = 3;
                if (terrainChange) {
                    locX1G = 4;
                    locX2G = 3;
                    locX3G = 7;
                    locX4G = 6;
                }
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * locX1G, y + offset * 10,
                    x + offset * locX2G, y + offset * 10,
                    x + offset * locX2G, y + offset * 9,
                    x + offset * locX1G, y + offset * 9,
                    // TOP!
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * locX4G, y + offset * 0,
                    x + offset * locX3G, y + offset * 0,
                    x + offset * locX3G, y + offset * 1,
                    x + offset * locX4G, y + offset * 1,
                    // BACK QUAD
                    x + offset * locX3G, y + offset * 1,
                    x + offset * locX4G, y + offset * 1,
                    x + offset * locX4G, y + offset * 0,
                    x + offset * locX3G, y + offset * 0,
                    // LEFT QUAD
                    x + offset * locX4G, y + offset * 0,
                    x + offset * locX3G, y + offset * 0,
                    x + offset * locX3G, y + offset * 1,
                    x + offset * locX4G, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * locX4G, y + offset * 0,
                    x + offset * locX3G, y + offset * 0,
                    x + offset * locX3G, y + offset * 1,
                    x + offset * locX4G, y + offset * 1};
            case 1: // Sand
                int locX1Sa = 3,
                 locX2Sa = 2;
                if (terrainChange) {
                    locX1Sa = 5;
                    locX2Sa = 4;
                }
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * locX1Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 1,
                    x + offset * locX1Sa, y + offset * 1,
                    // TOP!
                    x + offset * locX1Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 1,
                    x + offset * locX1Sa, y + offset * 1,
                    // FRONT QUAD
                    x + offset * locX1Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 1,
                    x + offset * locX1Sa, y + offset * 1,
                    // BACK QUAD
                    x + offset * locX1Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 1,
                    x + offset * locX1Sa, y + offset * 1,
                    // LEFT QUAD
                    x + offset * locX1Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 1,
                    x + offset * locX1Sa, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * locX1Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 2,
                    x + offset * locX2Sa, y + offset * 1,
                    x + offset * locX1Sa, y + offset * 1};
            case 2: // Water
                int locX1W = 2,
                 locX2W = 1;
                if (terrainChange) {
                    locX1W = 3;
                    locX2W = 2;
                }
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * locX1W, y + offset * 12,
                    x + offset * locX2W, y + offset * 12,
                    x + offset * locX2W, y + offset * 11,
                    x + offset * locX1W, y + offset * 11,
                    // TOP!
                    x + offset * locX1W, y + offset * 12,
                    x + offset * locX2W, y + offset * 12,
                    x + offset * locX2W, y + offset * 11,
                    x + offset * locX1W, y + offset * 11,
                    // FRONT QUAD
                    x + offset * locX1W, y + offset * 12,
                    x + offset * locX2W, y + offset * 12,
                    x + offset * locX2W, y + offset * 11,
                    x + offset * locX1W, y + offset * 11,
                    // BACK QUAD
                    x + offset * locX1W, y + offset * 12,
                    x + offset * locX2W, y + offset * 12,
                    x + offset * locX2W, y + offset * 11,
                    x + offset * locX1W, y + offset * 11,
                    // LEFT QUAD
                    x + offset * locX1W, y + offset * 12,
                    x + offset * locX2W, y + offset * 12,
                    x + offset * locX2W, y + offset * 11,
                    x + offset * locX1W, y + offset * 11,
                    // RIGHT QUAD
                    x + offset * locX1W, y + offset * 12,
                    x + offset * locX2W, y + offset * 12,
                    x + offset * locX2W, y + offset * 11,
                    x + offset * locX1W, y + offset * 11};
            case 3: // Dirt
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // TOP!
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // BACK QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 4, y + offset * 1,
                    x + offset * 4, y + offset * 0,
                    x + offset * 3, y + offset * 0};
            case 4: // Stone
                int locX1S = 2,
                 locX2S = 1;
                if (terrainChange) {
                    locX1S = 5;
                    locX2S = 4;
                }
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * locX1S, y + offset * 1,
                    x + offset * locX2S, y + offset * 1,
                    x + offset * locX2S, y + offset * 0,
                    x + offset * locX1S, y + offset * 0,
                    // TOP!
                    x + offset * locX1S, y + offset * 1,
                    x + offset * locX2S, y + offset * 1,
                    x + offset * locX2S, y + offset * 0,
                    x + offset * locX1S, y + offset * 0,
                    // FRONT QUAD
                    x + offset * locX1S, y + offset * 1,
                    x + offset * locX2S, y + offset * 1,
                    x + offset * locX2S, y + offset * 0,
                    x + offset * locX1S, y + offset * 0,
                    // BACK QUAD
                    x + offset * locX1S, y + offset * 1,
                    x + offset * locX2S, y + offset * 1,
                    x + offset * locX2S, y + offset * 0,
                    x + offset * locX1S, y + offset * 0,
                    // LEFT QUAD
                    x + offset * locX1S, y + offset * 1,
                    x + offset * locX2S, y + offset * 1,
                    x + offset * locX2S, y + offset * 0,
                    x + offset * locX1S, y + offset * 0,
                    // RIGHT QUAD
                    x + offset * locX1S, y + offset * 1,
                    x + offset * locX2S, y + offset * 1,
                    x + offset * locX2S, y + offset * 0,
                    x + offset * locX1S, y + offset * 0,};
            case 5: // Bedrock
                int locX1B = 2,
                 locX2B = 1;
                if (terrainChange) {
                    locX1B = 4;
                    locX2B = 3;
                }
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * locX1B, y + offset * 2,
                    x + offset * locX2B, y + offset * 2,
                    x + offset * locX2B, y + offset * 1,
                    x + offset * locX1B, y + offset * 1,
                    // TOP!
                    x + offset * locX1B, y + offset * 2,
                    x + offset * locX2B, y + offset * 2,
                    x + offset * locX2B, y + offset * 1,
                    x + offset * locX1B, y + offset * 1,
                    // FRONT QUAD
                    x + offset * locX1B, y + offset * 2,
                    x + offset * locX2B, y + offset * 2,
                    x + offset * locX2B, y + offset * 1,
                    x + offset * locX1B, y + offset * 1,
                    // BACK QUAD
                    x + offset * locX1B, y + offset * 2,
                    x + offset * locX2B, y + offset * 2,
                    x + offset * locX2B, y + offset * 1,
                    x + offset * locX1B, y + offset * 1,
                    // LEFT QUAD
                    x + offset * locX1B, y + offset * 2,
                    x + offset * locX2B, y + offset * 2,
                    x + offset * locX2B, y + offset * 1,
                    x + offset * locX1B, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * locX1B, y + offset * 2,
                    x + offset * locX2B, y + offset * 2,
                    x + offset * locX2B, y + offset * 1,
                    x + offset * locX1B, y + offset * 1};
        }
        return new float[]{1, 1, 1};
    }

    // method: createCube
    // purpose: create a cube at coordinate
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z};
    }

    // method: getCubeColor
    // purpose: reuturn cube color 1,1,1
    public float[] getCubeColor(Block block) {
        return new float[]{1, 1, 1};
    }

    // Constructor
    public Chunk(int startX, int startY, int startZ) {
        // try to load texture
        try {
            texture = TextureLoader.getTexture("PNG",
                    ResourceLoader.getResourceAsStream(".\\src\\finalproject\\terrain.png")); // relative path
        } catch (Exception e) {
            System.out.print("Cannot load texture!");
        }
        
        r = new Random();
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
//        for (int x = 0; x < CHUNK_SIZE; x++) {
//            for (int y = 0; y < CHUNK_SIZE; y++) {
//                for (int z = 0; z < CHUNK_SIZE; z++) {
//                    if (r.nextFloat() > 0.83f) {
//                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
//                    } else if (r.nextFloat() > 0.67f) {
//                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
//                    } else if (r.nextFloat() > 0.50f) {
//                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
//                    } else if (r.nextFloat() > 0.33f) {
//                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
//                    } else if (r.nextFloat() > 0.17f) {
//                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
//                    } else {
//                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
//                    }
//                }
//            }
//        }
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    if (r.nextFloat() > 0.83f) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
//                        blocks[x][y][z].setActive(true);
                    } else if (r.nextFloat() > 0.67f) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
//                        blocks[x][y][z].setActive(true);
                    } else if (r.nextFloat() > 0.50f) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
//                        blocks[x][y][z].setActive(true);
                    } else if (r.nextFloat() > 0.33f) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
//                        blocks[x][y][z].setActive(true);
                    } else if (r.nextFloat() > 0.17f) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
//                        blocks[x][y][z].setActive(true);
                    } else {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
//                        blocks[x][y][z].setActive(true);
                    }
//                    blocks[x][y][z].setActive(true);
                }
            }
        }
        
        vboColorHandle = glGenBuffers();
        vboVertexHandle = glGenBuffers();
        vboTextureHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        changeTexture = false;
        noiseHolder = new double[CHUNK_SIZE][CHUNK_SIZE];
        blockTypeHolder = new Block.BlockType[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        rebuildMesh(startX, startY, startZ);
        
    }

    // method: changeTexture
    // purpose: change texture status and rebuild the texture vertex
    public void changeTexture() {
        changeTexture = !changeTexture;
        rebuildTexture();
    }
    
    public boolean checkCollision(AABB playerBox) {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    Block block = blocks[x][y][z];
                    if (block != null && block.isActive()) {
                        float blockX = startX + x * CUBE_LENGTH;
                        float blockY = startY + y * CUBE_LENGTH;
                        float blockZ = startZ + z * CUBE_LENGTH;
                        AABB blockBox = new AABB(
                                new Vector3f(blockX, blockY, blockZ),
                                new Vector3f(blockX + CUBE_LENGTH, blockY + CUBE_LENGTH, blockZ + CUBE_LENGTH)
                        );
                        if (playerBox.intersects(blockBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
