package example.com.mdp_group2;

/**
 * Robot Class to track the coordinate of the robot on the map and the direction of movement
 */

public class Robot {
    private float currentX;
    private float currentY;
    private String headPos;

    public Robot(float currentX, float currentY, String headPos){
        this.currentX = currentX;
        this.currentY = currentY;
        this.headPos = headPos;
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public String getHeadPos() {
        return headPos;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    public void setHeadPos(String headPos) {
        this.headPos = headPos;
    }

}
