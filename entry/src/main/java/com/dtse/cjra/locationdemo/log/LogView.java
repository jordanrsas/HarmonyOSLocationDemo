package com.dtse.cjra.locationdemo.log;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Text;
import ohos.app.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogView extends Text {
    private static char SPACE = 32;
    private static char LINE_FEED = 10;
    private static char RETURN = 13;

    public LogView(Context context) {
        super(context);
    }

    public LogView(Context context, AttrSet attrSet) {
        super(context, attrSet);
    }

    public LogView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
    }

    public void println(String tag, String message) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String dateStr = format.format(date);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateStr);
        stringBuilder.append(SPACE);
        stringBuilder.append(tag);
        stringBuilder.append(": ");
        stringBuilder.append(message);
        stringBuilder.append(RETURN).append(LINE_FEED);

        getContext().getUITaskDispatcher().asyncDispatch(() -> appendToLog(stringBuilder.toString()));
    }

    public void appendToLog(String s) {
        append("\n" + s);
    }
}
