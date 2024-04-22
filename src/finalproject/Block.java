/** ****************************************************************
 * file: Programming.java
 * author: Thien Long Dinh (id:015792764), Thien Luat Dinh (id:015792777), An Le (id: 014128231)
 * class: CS 4450 - Computer Graphics
 *
 * assignment: final program - checkpoint 2
 * date last modified: 04/22/2024
 *
 * purpose: This file hold block information.
 ***************************************************************** */
package finalproject;

public class Block {

    private boolean isActive;
    private BlockType type;
    private float x, y, z;

    // Inner class that hold block type information
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

        // method: getId
        // purpose: return block ID
        public int getId() {
            return blockID;
        }

        // method: setId
        // purpose: set block ID
        public void setId(int i) {
            blockID = i;
        }
    }

    // Constructor
    public Block(BlockType type) {
        this.type = type;
    }

    // method: setCoords
    // purpose: set block coordiante
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // method: isActive
    // purpose: return if block is active
    public boolean isActive() {
        return isActive;
    }

    // method: setActive
    // purpose: set active status 
    public void setActive(boolean active) {
        isActive = active;
    }

    // method: getId
    // purpose: return type Id
    public int getId() {
        return type.getId();
    }
}
