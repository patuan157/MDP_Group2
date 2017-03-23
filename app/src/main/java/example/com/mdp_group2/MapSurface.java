package example.com.mdp_group2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

/**
 * Draw The Grid Map base on Surface View Class
 */

public class MapSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "Map Surface";
    public Robot robot;

    private Canvas canvas;
    private Paint paint;
    private SurfaceHolder sh;

    private int mapStartX = 0, mapStartY = 0;
    private int col = 1, row = 1;
    private float cellWidth;

    public String mdfP1 = "";
    public String mdfP2 = "";

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

    public static String defaultMap =                   // Test obstacles display here
                     "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0" +
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
        robot = new Robot(defaultX, defaultY, "U", defaultMap);

        // First draw
        drawMap();
    }

    public void drawMap(){
        // Canvas draw the top-left of the paint
        //float defaultX = mapStartX + SCREEN_PADDING;
        //float defaultY = mapStartY + SCREEN_PADDING + 17*cellWidth;
        canvas = sh.lockCanvas();
        updateMap(robot.getCurrentX(), robot.getCurrentY(), robot.getHeadPos(), robot.getArenaMap());
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
        col = x;
        row = y;
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

                //Log.d(TAG, "paint block: " + currentRow + ", " + currentCol);
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

                //Log.d(TAG, "paint block: " + currentRow + ", " + currentCol);
                rect.set(left, top, right, bottom);
                canvas.drawRect(rect, paint);

            }
        }

    }

    public void decode(String newMapInfo){
        // Receive Message from PC
        Log.d(TAG, "Completed Message: " + newMapInfo);
        try {

            String[] updateInfo;
            if (newMapInfo.contains("BOT_POS") && newMapInfo.contains("MAP")) {
                int a = newMapInfo.indexOf("MAP");
                int b = newMapInfo.indexOf("BOT_POS");
                String u ;
                String r ;
                if (a == 0) {
                    u = newMapInfo.substring(0, b);
                    r = newMapInfo.substring(b);
                } else {
                    u = newMapInfo.substring(a);
                    r = newMapInfo.substring(0, a);
                }

                // Robot Pos update

                String robotPos = r.split(" ")[1];
                Log.d(TAG, "Robot Pos :" + robotPos);

                int column = Integer.valueOf(robotPos.split(",")[1]);
                int row = Integer.valueOf(robotPos.split(",")[0]);
                String headPos = robotPos.split(",")[2];
                switch (headPos) {
                    case "N":
                        robot.setHeadPos(HEAD_POS_UP);
                        break;
                    case "S":
                        robot.setHeadPos(HEAD_POS_DOWN);
                        break;
                    case "W":
                        robot.setHeadPos(HEAD_POS_LEFT);
                        break;
                    case "E":
                        robot.setHeadPos(HEAD_POS_RIGHT);
                        break;
                }
                float currentX = mapStartX + SCREEN_PADDING + (column - 1) * cellWidth;
                float currentY = mapStartY + SCREEN_PADDING + (18 - row) * cellWidth;
                robot.setCurrentX(currentX);
                robot.setCurrentY(currentY);

                // Map Info updated

                Log.d(TAG, "Map String :" + newMapInfo);
                updateInfo = u.split(" ");
                mdfP1 = updateInfo[1];
                Log.d(TAG, "MDFp1 : " + mdfP1);
                String P1 = new String();
                mdfP2 = updateInfo[2];
                Log.d(TAG, "MDFp2 : " + mdfP2);
                String P2 = new String();
                //String robotPos = updateInfo[1].replace(" ", "");


                String mapInfo = robot.getArenaMap();
                String[] info = mapInfo.split(" ");

                for (char x : mdfP1.toCharArray()) {
                    //P1.append(hexToBin(mdfP1.toCharArray()[i]));
                    P1 += hexToBin(x);
                }
                Log.d(TAG, "P1 :" + P1.substring(2, 302));
                // After split. A "" is put at the beginning. Array length is 301
                String[] p1Info = P1.substring(2, 302).split("");

                for (char y : mdfP2.toCharArray()) {
                    //P2.append(hexToBin(mdfP2.toCharArray()[j]));
                    P2 += hexToBin(y);
                }
                Log.d(TAG, "P2 :" + P2);
                String[] p2Info = P2.split("");

                //Log.d(TAG, TextUtils.join(" ",p1Info));

                int counter1 = 1, counter2 = 1;
                for (row = 19; row >= 0; row--) {
                    for (int index = 0; index < 15; index++) {
                        int indexToSet = row * 15 + index;
                        if (p1Info[counter1].equals("0")) {
                            info[indexToSet] = "2";
                        } else {
                            if (p2Info[counter2].equals("1")) {
                                info[indexToSet] = "1";
                            } else {
                                info[indexToSet] = "0";
                            }
                            counter2++;
                        }
                        counter1++;
                    }
                }

                robot.setArenaMap(TextUtils.join(" ", info));


            } else if (newMapInfo.contains("BOT_POS")) {
                String robotPos = newMapInfo.split(" ")[1];
                Log.d(TAG, "Robot Pos :" + robotPos);

                int column = Integer.valueOf(robotPos.split(",")[1]);
                int row = Integer.valueOf(robotPos.split(",")[0]);
                String headPos = robotPos.split(",")[2];
                switch (headPos) {
                    case "N":
                        robot.setHeadPos(HEAD_POS_UP);
                        break;
                    case "S":
                        robot.setHeadPos(HEAD_POS_DOWN);
                        break;
                    case "W":
                        robot.setHeadPos(HEAD_POS_LEFT);
                        break;
                    case "E":
                        robot.setHeadPos(HEAD_POS_RIGHT);
                        break;
                }
                float currentX = mapStartX + SCREEN_PADDING + (column - 1) * cellWidth;
                float currentY = mapStartY + SCREEN_PADDING + (18 - row) * cellWidth;
                robot.setCurrentX(currentX);
                robot.setCurrentY(currentY);
            } else {
                Log.d(TAG, "Map String :" + newMapInfo);
                updateInfo = newMapInfo.split(" ");
                mdfP1 = updateInfo[1];
                Log.d(TAG, "MDFp1 : " + mdfP1);
                String P1 = new String();
                mdfP2 = updateInfo[2];
                Log.d(TAG, "MDFp2 : " + mdfP2);
                String P2 = new String();
                //String robotPos = updateInfo[1].replace(" ", "");


                String mapInfo = robot.getArenaMap();
                String[] info = mapInfo.split(" ");

                for (char x : mdfP1.toCharArray()) {
                    //P1.append(hexToBin(mdfP1.toCharArray()[i]));
                    P1 += hexToBin(x);
                }
                Log.d(TAG, "P1 :" + P1.substring(2, 302));
                // After split. A "" is put at the beginning. Array length is 301
                String[] p1Info = P1.substring(2, 302).split("");

                for (char y : mdfP2.toCharArray()) {
                    //P2.append(hexToBin(mdfP2.toCharArray()[j]));
                    P2 += hexToBin(y);
                }
                Log.d(TAG, "P2 :" + P2);
                String[] p2Info = P2.split("");

                //Log.d(TAG, TextUtils.join(" ",p1Info));

                int counter1 = 1, counter2 = 1;
                for (row = 19; row >= 0; row--) {
                    for (int index = 0; index < 15; index++) {
                        int indexToSet = row * 15 + index;
                        if (p1Info[counter1].equals("0")) {
                            info[indexToSet] = "2";
                        } else {
                            if (p2Info[counter2].equals("1")) {
                                info[indexToSet] = "1";
                            } else {
                                info[indexToSet] = "0";
                            }
                            counter2++;
                        }
                        counter1++;
                    }
                }

                robot.setArenaMap(TextUtils.join(" ", info));
            }
            drawMap();
        }
        catch (Exception e){
            Log.d(TAG, "Wrong Message Format");
        }
    }

    public void decodeMessage(String newMapInfo){
        // Decode base on Legacy PPT Format (AMDTool/scripts)
        String[] updatedInfo = processMapDescriptor(newMapInfo);
        float currentX = mapStartX + SCREEN_PADDING + (Integer.valueOf(updatedInfo[0]) - 1) * cellWidth;
        float currentY = mapStartY + SCREEN_PADDING + (Integer.valueOf(updatedInfo[1]) - 1) * cellWidth;
        robot.setCurrentX(currentX);
        robot.setCurrentY(currentY);
        String headPos = updatedInfo[2];
        robot.setHeadPos(headPos);
        String mapInfo = updatedInfo[3];
        robot.setArenaMap(mapInfo);

        drawMap();
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

        // Return Map String
        return updatedInfo;
    }

    private static String checkHead(String posX1) {
        if (Integer.valueOf(posX1) == 270)
            return HEAD_POS_LEFT;
        else if (Integer.valueOf(posX1) == 180)
            return HEAD_POS_DOWN;
        else if (Integer.valueOf(posX1) == 90)
            return HEAD_POS_RIGHT;
        else
            return HEAD_POS_UP;
    }

    public String[] getRobotPos(){
        String[] robotPos = new String[2];
        robotPos[0] = Integer.toString(col);
        robotPos[1] = Integer.toString(row);
        return robotPos;
    }

    /**
     * Helper method to convert a hex digit to a binary string of four digits.
     */
    private static String hexToBin(char hex) {
        int dec = Integer.parseInt(hex + "", 16);

        char[] buf = new char[4];
        buf[3] = (dec & 1) == 1 ? '1' : '0';
        buf[2] = (dec & 2) == 2 ? '1' : '0';
        buf[1] = (dec & 4) == 4 ? '1' : '0';
        buf[0] = (dec & 8) == 8 ? '1' : '0';

        return new String(buf);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
