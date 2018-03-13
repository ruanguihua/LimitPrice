package com.rgh.limitprice;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

/**
 * Created by RGH on 2018/1/10.
 * 2位小数价格输入框
 */

public class EditTextNonPrice extends AppCompatEditText {
    private int mMaxNum;
    private boolean flag;

    public EditTextNonPrice(Context context) {
        super(context);
    }

    public EditTextNonPrice(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public EditTextNonPrice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }


    private void init(AttributeSet attrs) {
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.EditTextNonPrice);
        mMaxNum = typedArray.getInt(R.styleable.EditTextNonPrice_decimalNum, 2);
        setHint("0.00");
       // setSelection(4);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (flag) return;

                //"00"开头去重
                if (s.toString().startsWith("00")) {
                    setmText("0");
                    return;
                }
                if (s.toString().contains(".") && s.length() - 1 - s.toString().lastIndexOf(".") > mMaxNum) {
                    CharSequence ch = s.toString().substring(0, s.toString().lastIndexOf(".") + (mMaxNum+1));

                    setmText(ch);
                    setSelection(ch.length());
                    return;
                }
                setmText(s);
            }
        });


    }


    public void setmText(CharSequence s) {
        flag = true;
        setText(s);
        setSelection(s.length());
        flag = false;

        if (TextUtils.isEmpty(s.toString())) {
            if (mClickListener != null)
                mClickListener.priceChanged(0);
        } else if (!s.toString().endsWith(".")) {
            if (mClickListener != null)
                mClickListener.priceChanged(Float.valueOf(s.toString()));
        }


    }


    private EditTextChanged mClickListener;

    public void setOnEditTextChanged(EditTextChanged mClickListener) {

        this.mClickListener = mClickListener;
    }

    public interface EditTextChanged {

        void priceChanged(float f);

    }
}
