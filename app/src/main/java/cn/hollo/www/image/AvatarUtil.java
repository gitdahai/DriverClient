package cn.hollo.www.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by orson on 14-10-11.
 * 处理头像的工具类
 */
public class AvatarUtil {
    /**
     * 生成原型
     * @param bitmap
     * @param revise  : 修正值
     * @return
     */
    public static Bitmap circular(Bitmap bitmap, int revise){
        int width = bitmap.getWidth() + revise;
        int height = bitmap.getHeight() + revise;

        //创建一个新的Bitmap对象
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        /*canvas.drawRoundRect(rectF, bitmap.getWidth()/ratio,
                bitmap.getHeight()/ratio, paint);*/

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 生成原型的头像
     * @param rsBm
     * @return
     */
    public static Bitmap createHeaderBm(Bitmap rsBm){
        Bitmap ovlBm = null;
        //缩小原Bitmap对象
        int width = (int)(rsBm.getWidth() * 0.8f);
        Bitmap scaleBm = AvatarUtil.scaleBitmap(rsBm, width, width);
        ovlBm = AvatarUtil.circular(scaleBm, 0);
        return ovlBm;
    }

    /**
     * 缩放Bitmap对象
     * @param bm
     * @param w
     * @param h
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bm, int w, int h){
        Bitmap result = null;
        //获取这个图片的宽和高
        int width = bm.getWidth();
        int height = bm.getHeight();

        //定义预转换成的图片的宽度和高度
        int newWidth = w;
        int newHeight = h;

        //计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();

        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        // 创建新的图片
        result = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        return result;
    }

    /**
     * 合成２个Bitmap对象
     * @param bg
     * @param fg
     * @param left
     * @param top
     * @return
     */
    public static Bitmap compoundBitmap(Bitmap bg, Bitmap fg, int left, int top){
        Bitmap result = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bg, 0, 0, null);
        canvas.drawBitmap(fg, left, top, null);

        return result;
    }

    public static Bitmap compoundBitmapCenterHorizontal(Bitmap bg, Bitmap fg, int top){
        Bitmap result = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bg, 0, 0, null);

        int widthBg = bg.getWidth();
        int widthFg = fg.getWidth();

        canvas.drawBitmap(fg, (widthBg - widthFg) / 2, top, null);

        return result;
    }

    public static Bitmap bitmapBlur( Bitmap sentBitmap, float radius){
        Bitmap overlay = Bitmap.createBitmap(sentBitmap.getWidth(), sentBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        //canvas.translate(-10, -10);

        canvas.drawBitmap(sentBitmap, 0, 0, null);
        overlay = FastBlur.doBlur(overlay, (int)radius, true);

        return overlay;
    }
}

