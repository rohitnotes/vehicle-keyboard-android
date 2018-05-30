package com.parkingwang.vehiclekeyboard.view;

import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.parkingwang.vehiclekeyboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 */
abstract class ButtonGroup {

    private static final String TAG = "InputView.ButtonGroup";

    private final Button[] mFieldViews = new Button[9];

    private Button[] mFieldCaches;
    private int mFieldsCacheVersion = -1;
    private int mFieldsCurrtVersion = 0;

    public ButtonGroup() {
        final int[] resIds = new int[]{
                R.id.number_0,
                R.id.number_1,
                R.id.number_2,
                R.id.number_3,
                R.id.number_4,
                R.id.number_5,
                R.id.number_6,
                R.id.number_6_of_end,
                R.id.number_7_of_end,
        };
        for (int i = 0; i < resIds.length; i++) {
            mFieldViews[i] = findViewById(resIds[i]);
            mFieldViews[i].setTag("[RAW.idx:" + i + "]");
        }
        // 默认时，显示8位
        changeTo8FieldViews();
    }

    protected abstract Button findViewById(int id);

    public void setTextToFields(String text) {
        // cleanup
        for (Button f : mFieldViews) {
            f.setText(null);
        }

        final char[] chars = text.toCharArray();
        if (chars.length >= 8) {
            changeTo8FieldViews();
        } else {
            changeTo7FieldViews();
        }
        // 显示到对应键位
        final Button[] fields = getAvailableFieldViews();
        for (int i = 0; i < fields.length; i++) {
            final String txt;
            if (i < chars.length) {
                txt = String.valueOf(chars[i]);
            } else {
                txt = null;
            }
            fields[i].setText(txt);
        }
    }

    public Button[] getAvailableFieldViews() {
        if (mFieldsCurrtVersion == mFieldsCacheVersion) {
            return mFieldCaches;
        }
        Log.d(TAG, "[## ReCache ###] Rebuild field views cache");
        final List<Button> output = new ArrayList<>(8);
        for (int i = 0; i < mFieldViews.length; i++) {
            if (i < 6) {
                output.add(mFieldViews[i]);
            } else {
                if (mFieldViews[i].isShown()) {
                    output.add(mFieldViews[i]);
                }
            }
        }
        mFieldsCacheVersion = mFieldsCurrtVersion;
        mFieldCaches = output.toArray(new Button[output.size()]);
        return mFieldCaches;
    }

    public Button getFieldViewAt(int index) {
        if (index < 6) {
            return mFieldViews[index];
        } else {
            if (index == 6) {
                if (mFieldViews[6].isShown()) {
                    return mFieldViews[6];
                } else {
                    return mFieldViews[7];
                }
            } else {
                return mFieldViews[index + 1];
            }
        }
    }

    public boolean changeTo7FieldViews() {
        if (mFieldViews[7].isShown()) {
            return false;
        }
        mFieldsCurrtVersion++;
        mFieldViews[6].setVisibility(View.GONE);
        mFieldViews[7].setVisibility(View.VISIBLE);
        mFieldViews[8].setVisibility(View.GONE);
        // cleanup gone
        mFieldViews[6].setText(null);
        mFieldViews[8].setText(null);
        return true;
    }

    public boolean changeTo8FieldViews() {
        if (mFieldViews[8].isShown()) {
            return false;
        }
        mFieldsCurrtVersion++;
        mFieldViews[6].setVisibility(View.VISIBLE);
        mFieldViews[7].setVisibility(View.GONE);
        mFieldViews[8].setVisibility(View.VISIBLE);
        // cleanup gone
        mFieldViews[7].setText(null);
        return true;
    }

    public Button getLastFieldView() {
        if (mFieldViews[8].isShown()) {
            return mFieldViews[8];
        } else {
            return mFieldViews[7];
        }
    }

    public Button getFirstSelectedOrNull() {
        for (Button field : getAvailableFieldViews()) {
            if (field.isSelected()) {
                return field;
            }
        }
        return null;
    }

    public Button getLastFilledOrNull() {
        final Button[] fields = getAvailableFieldViews();
        for (int i = fields.length - 1; i >= 0; i--) {
            if (!TextUtils.isEmpty(fields[i].getText())) {
                return fields[i];
            }
        }
        return null;
    }

    public Button getFirstEmpty() {
        final Button[] fields = getAvailableFieldViews();
        Button out = fields[0];
        for (Button field : fields) {
            out = field;
            final CharSequence keyTxt = field.getText();
            if (TextUtils.isEmpty(keyTxt)) {
                break;
            }
        }
        Log.d(TAG, "[-- CheckEmpty --]: Btn.idx: " + out.getTag() + ", Btn.text: " + out.getText() + ", Btn.addr: " + out);
        return out;
    }

    public int getNextIndexOf(Button target) {
        final Button[] fields = getAvailableFieldViews();
        for (int i = 0; i < fields.length; i++) {
            if (target == fields[i]) {
                return Math.min(fields.length - 1, i + 1);
            }
        }
        return 0;
    }

    public boolean isAllFilled() {
        for (Button field : getAvailableFieldViews()) {
            if (TextUtils.isEmpty(field.getText())) {
                return false;
            }
        }
        return true;
    }

    public String getText() {
        final StringBuilder sb = new StringBuilder();
        for (Button field : getAvailableFieldViews()) {
            sb.append(field.getText());
        }
        return sb.toString();
    }

    public void setAllFieldViewsTextSize(float size) {
        for (Button field : mFieldViews) {
            field.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public void setAllFieldViewsOnClickListener(View.OnClickListener listener) {
        for (Button field : mFieldViews) {
            field.setOnClickListener(listener);
        }
    }
}
