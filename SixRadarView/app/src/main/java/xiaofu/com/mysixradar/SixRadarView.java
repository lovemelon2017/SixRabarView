package xiaofu.com.mysixradar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 正六边形雷达图
 */

public class SixRadarView extends View {
    private static final String LINE_COLOR_GRAY = "#19ffffff"; //六边形 边线颜色
    private static final String CONTENT_COLOR = "#b9ff1e1e";  //六边形内部填充颜色  b9ff1e1e
    private static final String LINE_EMIT = "#8e8e8e"; // 与中心点连线颜色
    private static final int TOP_DISTANCE = 24;
    private static final int RIGHT_DISTANCE = 20;
    private static final int LEFT_DISTANCE = 20;
    private static final int BOM_DISTANCE = 30;
    private static final int CENTER_DISTANCE = 10;
    private static final int NORMAL_RADIUS = 100;
    private static final int NORMAL_SKINCOLOR = 67; //肤色六档
    private int dataCount = 6;//六边形
    private float radian = (float) (Math.PI * 2 / dataCount);//每个维度的角度
    private float radius = 100;//一条星射线的长度
    private float maxRadius = 400; //最大半径
    private int centerX;//中心坐标 Y
    private int centerY;//中心坐标 X
    private String[] dimensionsTitles = {"击杀", "生存", "助攻", "物理", "魔法", "防御"};//标题
    private String[] dimensions = {"rng牛逼>", "苟且偷生>", "混世魔>", "8043>", "一般>", "漫天肥羽>"};
    private int times[] = {2, 3, 1, 3, 1, 2}; //维度倍数  4档


    private Paint mPaintText1, mPaintText2;//绘制文字的画笔
    private int radarMargin = 0;
    private Path mPentagonPath;//记录白色六边形的路径
    private Paint mPentagonPaint;//绘制白色六边形的画笔

    private RadarClickInterface radarClickInterface;

    public void setRadarClickInterface(RadarClickInterface radarClickInterface) {
        this.radarClickInterface = radarClickInterface;
    }

    public SixRadarView(Context context) {
        super(context);
        init();
    }

    public SixRadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SixRadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPentagonPaint = new Paint();//初始化白色五边形的画笔
        mPentagonPaint.setAntiAlias(true);//
        mPentagonPaint.setStyle(Paint.Style.FILL_AND_STROKE);//
        mPaintText1 = new Paint();//初始化文字画笔
        mPaintText1.setAntiAlias(true);//
        mPaintText1.setStyle(Paint.Style.FILL);//

        mPaintText2 = new Paint();//初始化文字画笔
        mPaintText2.setAntiAlias(true);//
        mPaintText2.setStyle(Paint.Style.FILL);//

        mPentagonPath = new Path();//初始化白色五边形路径
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        drawPentagon(canvas);//绘制实心六边形
        drawSix(canvas);//绘制六条星射线
        drawSixBian(canvas);   //绘制六边形边
        drawPentagonBian(canvas); //内层白边
        drawDemensions(canvas);//绘制六个维度
        drawDemensionsTitle(canvas);//绘制最外层六个
    }


    /**
     * 六个维度具体等级
     *
     * @param canvas
     */
    private void drawDemensionsTitle(Canvas canvas) {

        mPaintText2.setTextSize(48);//第一层title 大小
        mPaintText2.setColor(Color.WHITE);
        for (int i = 0; i < dataCount; i++) {
            int x = getSixPoint(i, radarMargin).x;
            int y = getSixPoint(i, radarMargin).y;
            int hTop = getTextHeight(dimensionsTitles[i]);
            int hBom = getTextHeight1(dimensions[i]);

            int wTop = (int) mPaintText2.measureText(dimensionsTitles[i]);
            int wBom = (int) mPaintText1.measureText(dimensions[i]);
            switch (i) {
                case 0://第一个点
                    x = x - wTop / 2 + wBom / 4;
                    y = y - hTop - CENTER_DISTANCE;
                    break;
                case 1:  //x轴正方向 文字
                    x = x + RIGHT_DISTANCE;
                    y = y;
                    break;
                case 2:
                    x = x - wTop / 2 + wTop / 4;
                    y = y + hTop / 2 + BOM_DISTANCE;
                    break;
                case 3:
                    x = x - wTop / 2 - wTop / 4;
                    y = y + hTop / 2 + BOM_DISTANCE;
                    break;
                case 4:
                    x = x - wTop - LEFT_DISTANCE;
                    y = y;
                    break;
                case 5:
                    x = x - wTop / 2 - wBom / 4;
                    y = y - hTop - CENTER_DISTANCE;
                    break;
            }
            canvas.drawText(dimensionsTitles[i], x, y, mPaintText2);
        }
    }

    /**
     * 绘制 六个维度
     *
     * @param canvas
     */

    private void drawDemensions(Canvas canvas) {
        mPaintText1.setTextSize(24);//第一层title 大小
        mPaintText2.setTextSize(48);//第一层title 大小
        mPaintText1.setColor(Color.parseColor("#7effffff"));
        for (int i = 0; i < dataCount; i++) {
            int x = getSixPoint(i, radarMargin).x;
            int y = getSixPoint(i, radarMargin).y;
            int hBom = getTextHeight1(dimensions[i]); //下高
            int hTop = getTextHeight(dimensionsTitles[i]);//上高
            int tBom = (int) mPaintText1.measureText(dimensions[i]);  //下边宽
            int tTop = (int) mPaintText2.measureText(dimensionsTitles[i]);  //上边宽
            switch (i) {
                case 0:
                    x = x - tBom / 2 + tBom / 4;
                    y = y - TOP_DISTANCE;
                    break;
                case 1:
                    x = x + (tTop - tBom) / 2 + RIGHT_DISTANCE;
                    y = y + hBom + CENTER_DISTANCE;
                    break;
                case 2:
                    x = x - tBom / 2 + tTop / 4;
                    y = y + hTop + hBom + CENTER_DISTANCE;
                    break;
                case 3:
                    x = x - tBom / 2 - tTop / 4;
                    y = y + hTop + hBom + CENTER_DISTANCE;
                    break;
                case 4:
                    x = x - tBom - LEFT_DISTANCE - (tTop - tBom) / 2;
                    y = y + hBom + CENTER_DISTANCE;
                    break;
                case 5:
                    x = x - tBom / 2 - tBom / 4;
                    y = y - TOP_DISTANCE;

                    break;
            }
            canvas.drawText(dimensions[i], x, y, mPaintText1);
        }
    }

    private int getTextHeight(String text) {
        Paint.FontMetrics fm = mPaintText2.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    private int getTextHeight1(String text) {
        Paint.FontMetrics fm = mPaintText1.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 绘制六条边
     *
     * @param canvas
     */
    private void drawSixBian(Canvas canvas) {
        mPentagonPaint.setColor(Color.parseColor(LINE_EMIT));
        mPentagonPaint.setShader(null);
        mPentagonPaint.setStrokeWidth(3);//设置宽度为1
        canvas.drawLine(getSixPoint(0).x, getSixPoint(0).y, getSixPoint(1).x, getSixPoint(1).y, mPentagonPaint);
        canvas.drawLine(getSixPoint(1).x, getSixPoint(1).y, getSixPoint(2).x, getSixPoint(2).y, mPentagonPaint);
        canvas.drawLine(getSixPoint(2).x, getSixPoint(2).y, getSixPoint(3).x, getSixPoint(3).y, mPentagonPaint);
        canvas.drawLine(getSixPoint(3).x, getSixPoint(3).y, getSixPoint(4).x, getSixPoint(4).y, mPentagonPaint);
        canvas.drawLine(getSixPoint(4).x, getSixPoint(4).y, getSixPoint(5).x, getSixPoint(5).y, mPentagonPaint);
        canvas.drawLine(getSixPoint(5).x, getSixPoint(5).y, getSixPoint(0).x, getSixPoint(0).y, mPentagonPaint);
    }

    /**
     * 五条放射线
     *
     * @param canvas
     */
    private void drawSix(Canvas canvas) {
        mPentagonPaint.setColor(Color.parseColor(LINE_EMIT));
        mPentagonPaint.setStrokeWidth(2);//设置宽度为1
        for (int i = 0; i < dataCount; i++) {
            LinearGradient gradient = new LinearGradient(centerX, centerY, getSixPoint(i).x, getSixPoint(i).y,
                    Color.parseColor("#00000000"), Color.parseColor("#8e8e8e"), Shader.TileMode.MIRROR);
            mPentagonPaint.setShader(gradient);
            canvas.drawLine(centerX, centerY, getSixPoint(i).x, getSixPoint(i).y, mPentagonPaint);//绘制
        }
    }

    /**
     * 内层白边
     *
     * @param canvas
     */
    private void drawPentagonBian(Canvas canvas) {
        for (int j = 0; j < 2; j++) {
            if (j == 0) {
                mPentagonPaint.setStrokeWidth(2);
                mPentagonPaint.setStyle(Paint.Style.STROKE);
                mPentagonPaint.setColor(Color.WHITE);
            } else if (j == 1) {
                radius = maxRadius;
                mPentagonPaint.setColor(Color.parseColor(LINE_COLOR_GRAY));
            }
            mPentagonPath.reset();
            for (int i = 0; i < dataCount; i++) {//绘制一层
                if (j == 0) {
                    if (i == 2) {
                        radius = times[i] * NORMAL_SKINCOLOR;
                    } else {
                        radius = times[i] * NORMAL_RADIUS;
                    }
                }
                if (i == 0) {
                    mPentagonPath.moveTo(getSixPoint(i).x, getSixPoint(i).y);

                } else {
                    mPentagonPath.lineTo(getSixPoint(i).x, getSixPoint(i).y);
                }
            }
            mPentagonPath.close();
            canvas.drawPath(mPentagonPath, mPentagonPaint);
        }
    }

    /**
     * 绘制五角形状
     *
     * @param canvas
     */
    private void drawPentagon(Canvas canvas) {

        for (int j = 0; j < 2; j++) {
            if (j == 0) {
                mPentagonPaint.setStrokeWidth(10);
                mPentagonPaint.setStyle(Paint.Style.FILL);
                mPentagonPaint.setColor(Color.parseColor(CONTENT_COLOR));
            } else if (j == 1) {
                radius = maxRadius;
                mPentagonPaint.setShader(null);
                mPentagonPaint.setColor(Color.parseColor(LINE_COLOR_GRAY));
            }
            mPentagonPath.reset();
            for (int i = 0; i < dataCount; i++) {//绘制一层
                if (j == 0) {
                    if (i == 2) {
                        radius = times[i] * NORMAL_SKINCOLOR;
                    } else {
                        radius = times[i] * NORMAL_RADIUS;
                    }
                }
                if (i == 0) {
                    mPentagonPath.moveTo(getSixPoint(i).x, getSixPoint(i).y);

                } else {
                    mPentagonPath.lineTo(getSixPoint(i).x, getSixPoint(i).y);
                }

            }
            mPentagonPath.close();
            canvas.drawPath(mPentagonPath, mPentagonPaint);
        }
    }


    public Point getSixPoint(int position) {
        return getSixPoint(position, 0);
    }
    // 右上角的顶点为第一个点，顺时针计算，position 依次是 0，1，2，3，4
    public Point getSixPoint(int position, int radarMargin) {
        int x = 0;
        int y = 0;
        switch (position) {
            case 0://第一象限，
                x = (int) (centerX + (radius + radarMargin) * Math.cos(radian));
                y = (int) (centerY - (radius + radarMargin) * Math.sin(radian));
                break;
            case 1://x正坐标
                x = (int) (centerX + (radius + radarMargin));
                y = centerY;
                break;
            case 2://第三象限，
                x = (int) (centerX + (radius + radarMargin) * Math.cos(radian));
                y = (int) (centerY + (radius + radarMargin) * Math.sin(radian));
                break;
            case 3://第四象限，
                x = (int) (centerX - (radius + radarMargin) * Math.cos(radian));
                y = (int) (centerY + (radius + radarMargin) * Math.sin(radian));
                break;
            case 4://x轴负方向
                x = (int) (centerX - (radius + radarMargin));
                y = centerY;
                break;
            case 5://第二象限
                x = (int) (centerX - (radius + radarMargin) * Math.cos(radian));
                y = (int) (centerY - (radius + radarMargin) * Math.sin(radian));
                break;

        }
        return new Point(x, y);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        Point point;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                point = getSixPoint(0);
                if (point.x > x - 100 && point.x < x + 100 && y > point.y - 100 && y < point.y + 100) {
                    if (radarClickInterface != null) {
                        radarClickInterface.onRightTop();
                    }
                    return true;
                }
                point = getSixPoint(1);
                if (point.x > x - 100 && point.x < x + 100 && y > point.y - 100 && y < point.y + 100) {
                    if (radarClickInterface != null) {
                        radarClickInterface.onRightCenter();
                    }
                    return true;
                }
                point = getSixPoint(2);
                if (point.x > x - 100 && point.x < x + 100 && y > point.y - 100 && y < point.y + 100) {
                    if (radarClickInterface != null) {
                        radarClickInterface.onRightBottom();
                    }
                    return true;
                }

                point = getSixPoint(3);
                if (point.x > x - 100 && point.x < x + 100 && y > point.y - 100 && y < point.y + 100) {
                    if (radarClickInterface != null) {
                        radarClickInterface.onLeftBottom();
                    }
                    return true;
                }
                point = getSixPoint(4);
                if (point.x > x - 100 && point.x < x + 100 && y > point.y - 100 && y < point.y + 100) {
                    if (radarClickInterface != null) {
                        radarClickInterface.onLeftCenter();
                    }
                    return true;
                }
                point = getSixPoint(5);
                if (point.x > x - 100 && point.x < x + 100 && y > point.y - 100 && y < point.y + 100) {
                    if (radarClickInterface != null) {
                        radarClickInterface.onLeftTop();
                    }
                    return true;
                }
                break;
        }


        return super.onTouchEvent(event);
    }


    public void setDimensionsTitles(String[] dimensionsTitles) {
        this.dimensionsTitles = dimensionsTitles;
    }

    public void setDimensions(String[] dimensions) {
        this.dimensions = dimensions;
    }

    public void setTimes(int[] times) {
        this.times = times;
    }

}
