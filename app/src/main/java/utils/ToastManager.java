package utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Toast
 */
public class ToastManager {
    private static Toast toast = null;

    public static void showError(String msg) {
        Context context =SystemApplication.getContext();
        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        View view = toast.getView();
        TextView tv = (TextView) view.findViewById(android.R.id.message);
        view.setBackgroundColor(0xb0000000);
        tv.setSingleLine(true);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        // tv.setTextSize();
        toast.show();
    }

    public static void show(String msg) {
        Context context = SystemApplication.getContext();
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        View view = toast.getView();
        TextView tv = (TextView) view.findViewById(android.R.id.message);
        tv.setGravity(Gravity.CENTER);
        //view.setBackgroundColor(0xb0000000);
        tv.setSingleLine(false);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
//		tv.setTextColor(context.getResources().getColor(R.color.font_color));
        // tv.setTextSize();
        toast.show();
    }

    public void cancle() {
        if (toast != null) {
            toast.cancel();
        }

    }
}
