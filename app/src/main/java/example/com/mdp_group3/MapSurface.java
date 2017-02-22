package example.com.mdp_group3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

/**
 * Draw The Grid Map base on Surface View Class
 */

public class MapSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "Surface Grid";
    private Robot robot;

    private Canvas canvas;
    private Paint paint;
    private SurfaceHolder sh;

    private int mapStartX = 0, mapStartY = 0;
    private float cellWidth;

    private static Bitmap robotUp;
    private static Bitmap robotDown;
    private static Bitmap robotLeft;
    private static Bitmap robotRight;
    private final int SCREEN_PADDING = 30;
    private final int SCREEN_WIDTH = 720;
    private final int SCREEN_HEIGHT = 570;
    private static int MAP_COLS = 15;
    private static int MAP_ROWS = 20;
    private static final String HEAD_POS_UP = "U";
    private static final String HEAD_POS_DOWN = "D";
    private static final String HEAD_POS_LEFT = "L";
    private static final String HEAD_POS_RIGHT = "R";

    public static String defaultMap =                   // Example with some obstacle here
                                       "0 0 0 0 0 0 0 0 0 0 0 0 2 2 2" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 2 2 2" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 2 2 2" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
                                      " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0";

    public MapSurface(Context context){
        super(context);
        sh = getHolder();
        sh.addCallback(this);
        paint = new Paint();
        this.setKeepScreenOn(true);
    }

    public MapSurface(Context context, AttributeSet attrs){
        super(context,attrs);
        sh = getHolder();
        sh.addCallback(this);
        paint = new Paint();
        this.setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        float tmpWidth = (SCREEN_WIDTH - 2 * SCREEN_PADDING) / MAP_COLS;
        float tmpHeight = (SCREEN_HEIGHT - 2 * SCREEN_PADDING) / MAP_ROWS;
        cellWidth = tmpWidth < tmpHeight ? tmpWidth : tmpHeight;
        mapStartX = SCREEN_PADDING;
        mapStartY = SCREEN_PADDING;
        Bitmap tmpRobotUp = BitmapFactory.decodeResource(getResources(), R.drawable.botup);
        Bitmap tmpRobotDown = BitmapFactory.decodeResource(getResources(), R.drawable.bot_down);
        Bitmap tmpRobotLeft = BitmapFactory.decodeResource(getResources(), R.drawable.bot_left);
        Bitmap tmpRobotRight = BitmapFactory.decodeResource(getResources(), R.drawable.bot_right);
        robotUp = Bitmap.createScaledBitmap(tmpRobotUp, (int)cellWidth*3, (int)cellWidth*3, true);
        robotDown = Bitmap.createScaledBitmap(tmpRobotDown, (int)cellWidth*3, (int)cellWidth*3, true);
        robotLeft = Bitmap.createScaledBitmap(tmpRobotLeft, (int)cellWidth*3, (int)cellWidth*3, true);
        robotRight = Bitmap.createScaledBitmap(tmpRobotRight, (int)cellWidth*3, (int)cellWidth*3, true);

        // Default start of the map.
        //decodeAction(defaultMap);
        float defaultX = mapStartX + SCREEN_PADDING;
        float defaultY = mapStartY + SCREEN_PADDING + 17*cellWidth;
        robot = new Robot(defaultX, defaultY, "U");

        // First draw
        drawMap();
    }

    public void drawMap(){
        // Canvas draw the top-left of the paint
        //float defaultX = mapStartX + SCREEN_PADDING;
        //float defaultY = mapStartY + SCREEN_PADDING + 17*cellWidth;
        canvas = sh.lockCanvas();
        updateMap(robot.getCurrentX(), robot.getCurrentY(), robot.getHeadPos(), defaultMap);
        if(canvas != null)
            sh.unlockCanvasAndPost(canvas);
    }

    public void moveForward(){
        // Move Forward command for virtual robot
        String headPos = robot.getHeadPos();
        switch(headPos){
            case "U":
                if (robot.getCurrentY() > mapStartY + SCREEN_PADDING){
                    float currentY = robot.getCurrentY() - cellWidth;
                    robot.setCurrentY(currentY);
                }
                break;
            case "D":
                if (robot.getCurrentY() < mapStartY + SCREEN_PADDING + 17*cellWidth){
                    float currentY = robot.getCurrentY() + cellWidth;
                    robot.setCurrentY(currentY);
                }
                break;
            case "L":
                if (robot.getCurrentX() > mapStartX + SCREEN_PADDING){
                    float currentX = robot.getCurrentX() - cellWidth;
                    robot.setCurrentX(currentX);
                }
                break;
            case "R":
                if (robot.getCurrentX() < mapStartX + SCREEN_PADDING + 12*cellWidth){
                    float currentX = robot.getCurrentX() + cellWidth;
                    robot.setCurrentX(currentX);
                }
                break;
        }
        drawMap();
        /*
        if(headPos.equals("U") && robot.getCurrentY() > mapStartY + SCREEN_PADDING){
            float currentY = robot.getCurrentY() - cellWidth;
            robot.setCurrentY(currentY);
            drawMap();
        } else if (headPos.equals("L") || headPos.equals("R")){
            robot.setHeadPos("U");
            drawMap();
        }
        */
    }

    public void reverse(){
        // Move Backward
        String headPos = robot.getHeadPos();
        switch(headPos){
            case "D":
                if (robot.getCurrentY() > mapStartY + SCREEN_PADDING){
                    float currentY = robot.getCurrentY() - cellWidth;
                    robot.setCurrentY(currentY);
                }
                break;
            case "U":
                if (robot.getCurrentY() < mapStartY + SCREEN_PADDING + 17*cellWidth){
                    float currentY = robot.getCurrentY() + cellWidth;
                    robot.setCurrentY(currentY);
                }
                break;
            case "R":
                if (robot.getCurrentX() > mapStartX + SCREEN_PADDING){
                    float currentX = robot.getCurrentX() - cellWidth;
                    robot.setCurrentX(currentX);
                }
                break;
            case "L":
                if (robot.getCurrentX() < mapStartX + SCREEN_PADDING + 12*cellWidth){
                    float currentX = robot.getCurrentX() + cellWidth;
                    robot.setCurrentX(currentX);
                }
                break;
        }
        drawMap();
        /*
        if(headPos.equals("D") && robot.getCurrentY() < mapStartY + SCREEN_PADDING + 17*cellWidth){
            float currentY = robot.getCurrentY() + cellWidth;
            robot.setCurrentY(currentY);
            drawMap();
        } else if (headPos.equals("L") || headPos.equals("R")){
            robot.setHeadPos("D");
            drawMap();
        }
        */
    }

    public void turnLeft(){
        // Turn left
        String headPos = robot.getHeadPos();
        switch(headPos){
            case "U":
                robot.setHeadPos("L");
                break;
            case "L":
                robot.setHeadPos("D");
                break;
            case "D":
                robot.setHeadPos("R");
                break;
            case "R":
                robot.setHeadPos("U");
        }
        drawMap();
        /*
        if(headPos.equals("L") && robot.getCurrentX() > mapStartX + SCREEN_PADDING){
            float currentX = robot.getCurrentX() - cellWidth;
            robot.setCurrentX(currentX);
            drawMap();
        } else if (headPos.equals("U") || headPos.equals("D")){
            robot.setHeadPos("L");
            drawMap();
        }
        */
    }

    public void turnRight(){
        String headPos = robot.getHeadPos();
        switch(headPos){
            case "U":
                robot.setHeadPos("R");
                break;
            case "R":
                robot.setHeadPos("D");
                break;
            case "D":
                robot.setHeadPos("L");
                break;
            case "L":
                robot.setHeadPos("U");
        }
        drawMap();
        /*
        if(headPos.equals("R") && robot.getCurrentX() < mapStartX + SCREEN_PADDING + 12*cellWidth){
            float currentX = robot.getCurrentX() + cellWidth;
            robot.setCurrentX(currentX);
            drawMap();
        } else if (headPos.equals("U") || headPos.equals("D")){
            robot.setHeadPos("R");
            drawMap();
        }
        */
    }

    public void setCoordinate(int x, int y) {
        String headPos = "U";       // Default headPos
        float currentX = mapStartX + SCREEN_PADDING + (x - 1) * cellWidth;
        float currentY = mapStartY + SCREEN_PADDING + (18 - y) * cellWidth;
        robot.setCurrentX(currentX);
        robot.setCurrentY(currentY);
        robot.setHeadPos(headPos);
        drawMap();
    }


    public void updateMap(float posX, float posY, String headPos, String mapInfo){
        drawMapGrid(mapInfo);
        switch(headPos){
            case HEAD_POS_UP:
                canvas.drawBitmap(robotUp, posX, posY, null);
                break;
            case HEAD_POS_DOWN:
                canvas.drawBitmap(robotDown, posX, posY, null);
                break;
            case HEAD_POS_LEFT:
                canvas.drawBitmap(robotLeft, posX, posY, null);
                break;
            case HEAD_POS_RIGHT:
                canvas.drawBitmap(robotRight, posX, posY, null);
                break;
            default:
                break;
        }
    }

    public void drawMapGrid(String mapInfo){
        float currentX = mapStartX + SCREEN_PADDING, currentY = mapStartY + SCREEN_PADDING;
        float mapWidth = currentX + cellWidth * MAP_COLS;
        float mapHeight = currentY + cellWidth * MAP_ROWS;

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(mapStartX, mapStartY, mapWidth+SCREEN_PADDING, mapHeight+SCREEN_PADDING), 10, 10, paint);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        for(int i = 0; i < MAP_COLS+1; i++){
            canvas.drawLine(currentX, currentY, currentX, mapHeight, paint);
            currentX += cellWidth;
        }
        currentX = mapStartX + SCREEN_PADDING;
        for(int j = 0; j < MAP_ROWS+1; j++){
            canvas.drawLine(currentX, currentY, mapWidth, currentY, paint);
            currentY += cellWidth;
        }

        String[] info = mapInfo.split(" ");

        RectF rect = new RectF();
        for(int i = 0; i < info.length; i++){
            if(Integer.valueOf(info[i])== 1){
                paint.setColor(Color.BLUE);
                paint.setStyle(Paint.Style.FILL);
                int currentRow = i / MAP_COLS;
                int currentCol = i - (MAP_COLS * currentRow);

                float left = mapStartX + SCREEN_PADDING + currentCol * cellWidth;
                float top = mapStartY + SCREEN_PADDING + currentRow * cellWidth;
                float right = left + cellWidth;
                float bottom = top + cellWidth;

                Log.d(TAG, "paint block: " + currentRow + ", " + currentCol);
                rect.set(left, top, right, bottom);
                canvas.drawRect(rect, paint);
            }
            if(Integer.valueOf(info[i])== 2){
                paint.setColor(Color.YELLOW);
                paint.setStyle(Paint.Style.FILL);
                int currentRow = i / MAP_COLS;
                int currentCol = i - (MAP_COLS * currentRow);

                float left = mapStartX + SCREEN_PADDING + currentCol * cellWidth;
                float top = mapStartY + SCREEN_PADDING + currentRow * cellWidth;
                float right = left + cellWidth;
                float bottom = top + cellWidth;

                Log.d(TAG, "paint block: " + currentRow + ", " + currentCol);
                rect.set(left, top, right, bottom);
                canvas.drawRect(rect, paint);

            }
        }

    }

    public void decodeAction(String newMapInfo){
        String[] updatedInfo = processMapDescriptor(newMapInfo);
        float currentX = mapStartX + SCREEN_PADDING + (Integer.valueOf(updatedInfo[0]) - 1) * cellWidth;
        float currentY = mapStartY + SCREEN_PADDING + (Integer.valueOf(updatedInfo[1]) - 1) * cellWidth;
        robot.setCurrentX(currentX);
        robot.setCurrentY(currentY);
        String headPos = updatedInfo[2];
        robot.setHeadPos(headPos);
        String mapInfo = updatedInfo[3];
        drawMapGrid(mapInfo);
        Log.d(TAG, "Row: " + (updatedInfo[1]) + " Col: " + (updatedInfo[0]) + " head: " + updatedInfo[2]);
    }

    private static String[] processMapDescriptor(String mapInfo) {
        String[] tmpRobot = new String[4];
        int tmpIndex;
        for (int i = 0; i < 6; i++) {
            tmpIndex = mapInfo.indexOf(" ");
            if (i > 2)
                tmpRobot[i - 3] = mapInfo.substring(0, tmpIndex);
            mapInfo = mapInfo.substring(tmpIndex + 1);
        }
        String[] updatedInfo = new String[4];
        System.arraycopy(tmpRobot, 0, updatedInfo, 0, 2);
        updatedInfo[2] = checkHead(tmpRobot[2]);
        updatedInfo[3] = mapInfo;

        return updatedInfo;
    }

    private static String checkHead(String posX1) {
        if (Integer.valueOf(posX1) == 3)
            return HEAD_POS_LEFT;
        else if (Integer.valueOf(posX1) == 2)
            return HEAD_POS_DOWN;
        else if (Integer.valueOf(posX1) == 1)
            return HEAD_POS_RIGHT;
        else
            return HEAD_POS_UP;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
