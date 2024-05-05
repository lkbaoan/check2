/** ****************************************************************
 * file: Programming.java
 * author: Thien Long Dinh (id:015792764), Thien Luat Dinh (id:015792777), An Le (id: 014128231)
 * class: CS 4450 - Computer Graphics
 *
 * assignment: final program - checkpoint 2
 * date last modified: 05/06/2024
 *
 * purpose: This class contain Axis-Aligned Bounding Box info.
 ***************************************************************** */
package finalproject;

import org.lwjgl.util.vector.Vector3f;

// Axis-Aligned Bounding Box
class AABB {

    public Vector3f min, max;

    // Constructor
    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    // method: intersects
    // purpose: check if bounding box collide with another
    public boolean intersects(AABB other) {
        return this.min.x <= other.max.x && this.max.x >= other.min.x
                && this.min.y <= other.max.y && this.max.y >= other.min.y
                && this.min.z <= other.max.z && this.max.z >= other.min.z;
    }
}
