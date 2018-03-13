package com.rgh.limitprice;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;


/**
 * Created by RGH on 2018/1/10.
 * 限制小数点后多1位小数价格输入框
 */

public class EditTextLimitPrice extends AppCompatEditText {

    private boolean isReturnPrice;//是否返回价格
    private boolean flag = false;//避免重复settext
    private int num;//小数点个数


    public EditTextLimitPrice(Context context) {
        super(context);
    }

    public EditTextLimitPrice(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public EditTextLimitPrice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }


    private void init() {
        setHint("0");
      //  setSelection(1);
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
                if (TextUtils.isEmpty(s)) {
                    if(mClickListener!=null)
                    mClickListener.priceChanged(0, "", position);
                }

                //"-"开头不能输入
                if (s.toString().startsWith("-")) {
                    setmText("");
                    return;
                }
                //"00"开头去重
                if (s.toString().startsWith("00")) {
                    setmText("0");
                    return;
                }
                // 去掉 如：1.9.9.9   19-12.3.4.4    1....  90.0.0.9-39
                if ((s.toString().indexOf(".") == (s.toString().lastIndexOf(".") - 1)) ||
                        (s.toString().indexOf(".") == (s.toString().lastIndexOf(".") - 2))) {
                    setbeforeText2(s);
                    return;
                }
                // 去掉 如：12-22--  12--  12-2-2
                if ((s.toString().lastIndexOf("-") - (s.toString().indexOf("-")) > 0)) {
                    setbeforeText3(s);
                    return;
                }

                if (s.toString().endsWith(".-") || s.toString().endsWith("-.")) {
                    setbeforeText4(s);
                    return;
                }

                //判断以小数点的个数为判断依据
                num = (s.toString().split("[.]").length) - 1;

                //CLog.e("num", "num....: " + num);
                if (num == 1 && s.toString().endsWith("-")) {//"-"结尾不需要计算价格

                    CharSequence s1 = s.toString().subSequence(0, s.toString().indexOf(".") + 2).toString();
                    flag = true;
                    setText(getContext().getString(R.string.product_non_standard, s1));
                    flag = false;
                    setSelection(s1.length() + 1);

                    isReturnPrice = false;


                } else if ((num == 1 && s.toString().contains("-"))) {//如1.8-99，88-90.8 的情况
                    isReturnPrice = true;
                    if (s.toString().indexOf("-") < s.toString().lastIndexOf(".")) {//"."在后面,88-90.8
                        if (s.toString().endsWith("..")) {
                            setbeforeText4(s);
                            return;
                        }

                        setbeforeText(s);

                    } else {//"."在前面
                        setmText(s.toString());
                        setSelection(s.toString().length());

                    }


                } else if (num == 0) {

                    if (s.toString().endsWith("..") || s.toString().endsWith(".-") || s.toString().endsWith("-.")) {
                        setbeforeText4(s);
                    } else {
                        isReturnPrice = true;
                        setmText(s.toString());
                        setSelection(s.toString().length());
                    }

                } else if (num >= 3) {
                    setbeforeText5(s);


                } else if (num == 2 && s.toString().endsWith(".")) {
                    setbeforeText4(s);

                } else {

                    setbeforeText(s);

                }
            }
        });


    }

    private void setbeforeText(Editable s) {


        if (s.toString().endsWith("..") || s.toString().endsWith(".-") || s.toString().endsWith("-.")) {
            setbeforeText4(s);
            return;
        }

        isReturnPrice = true;

        if (s.length() - 1 - s.toString().lastIndexOf(".") > 1) {
            CharSequence ch = s.toString().substring(0, s.toString().lastIndexOf(".") + 2);
            flag = true;
            setmText(ch);
            flag = false;
            setSelection(ch.length());
            return;
        }

        if (!s.toString().endsWith(".")) {
            setPirce(getText().toString());
        }

    }

    private void setbeforeText2(Editable s) {

        CharSequence ch = s.toString().substring(0, s.toString().lastIndexOf("."));
        flag = true;
        setText(ch);
        flag = false;
        setSelection(ch.length());

    }

    private void setbeforeText3(Editable s) {

        CharSequence ch = s.toString().substring(0, s.toString().lastIndexOf("-"));
        flag = true;
        setText(ch);
        flag = false;
        setSelection(ch.length());

    }

    private void setbeforeText4(Editable s) {

        CharSequence ch = s.toString().substring(0, s.toString().length() - 1);
        flag = true;
        setText(ch);
        flag = false;
        setSelection(ch.length());

    }

    private void setbeforeText5(Editable s) {

        CharSequence ch = s.toString().substring(0, s.toString().length() - 2);
        flag = true;
        setText(ch);
        flag = false;
        setSelection(ch.length());

    }


    public void setmText(CharSequence s) {
        flag = true;
        setText(s);
        flag = false;

        if (isReturnPrice) {
            isReturnPrice = false;
            setPirce(s);
        }

    }

    //把2头规格数取中间值计算价格
    private void setPirce(CharSequence s) {
        //    mClickListener.priceChanged(s.toString());
        switch (num) {
            case 0:
                //  CLog.e("0", "setPirce: " + getText().toString());
                if (!TextUtils.isEmpty(s) && !s.toString().contains("-")) {
                    if(mClickListener!=null)   mClickListener.priceChanged(Float.valueOf(s.toString()), s.toString(), position);
                } else {
                    String[] arr = getText().toString().split("-");
                    if (arr.length == 2 && !s.toString().endsWith(".")) {
                        float f = (Float.valueOf(arr[0]) + Float.valueOf(arr[1])) / 2;
                        if(mClickListener!=null)    mClickListener.priceChanged(f, s.toString(), position);
                        //  CLog.e("1", "setPirce: " + f);
                    }
                }
                break;
            case 1:

                String[] arr = getText().toString().split("-");

                if (arr.length == 2) {
                    float f = (Float.valueOf(arr[0]) + Float.valueOf(arr[1])) / 2;
                    if(mClickListener!=null)  mClickListener.priceChanged(f, s.toString(), position);
                    //  CLog.e("1", "setPirce: " + f);
                } else {
                    if(mClickListener!=null)  mClickListener.priceChanged(Float.valueOf(s.toString()), s.toString(), position);
                }


                break;
            case 2:
                String[] arr2 = getText().toString().split("-");
                //  CLog.e("2", "setPirce: " + JsonUtils.toJson(arr2));
                float f2 = (Float.valueOf(arr2[0]) + Float.valueOf(arr2[1])) / 2;
if(mClickListener!=null)
                mClickListener.priceChanged(f2, s.toString(), position);
                break;
            default:
                break;
        }
    }

    private EditTextChanged mClickListener;

    private int position = -1;

    public void setOnEditTextChanged(EditTextChanged mClickListener, int position) {

        this.mClickListener = mClickListener;

        this.position = position;
    }

    public interface EditTextChanged {

        void priceChanged(float f, String specific, int position);

    }
}
