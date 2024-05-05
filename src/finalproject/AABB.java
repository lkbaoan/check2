package finalproject;

import org.lwjgl.util.vector.Vector3f;

// Axis-Aligned Bounding Box
class AABB {

    public Vector3f min, max;

    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    public boolean intersects(AABB other) {
        return this.min.x <= other.max.x && this.max.x >= other.min.x
                && this.min.y <= other.max.y && this.max.y >= other.min.y
                && this.min.z <= other.max.z && this.max.z >= other.min.z;
    }
}
