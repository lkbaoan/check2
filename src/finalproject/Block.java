/** ****************************************************************
 * file: Programming.java
 * author: Thien Long Dinh (id:015792764), Thien Luat Dinh (id:015792777), An Le (id: 014128231)
 * class: CS 4450 - Computer Graphics
 *
 * assignment: final program - checkpoint 1
 * date last modified: 02/22/2024
 *
 * purpose: This file hold block information.
 ***************************************************************** */
package finalproject;

public class Block {

    private boolean isActive;
    private BlockType type;
    private float x, y, z;

    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);

        private int blockID;

        BlockType(int i) {
            blockID = i;
        }

        public int getId() {
            return blockID;
        }

        public void setId(int i) {
            blockID = i;
        }
    }

    public Block(BlockType type) {
        this.type = type;
    }

    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getId() {
        return type.getId();
    }
}
