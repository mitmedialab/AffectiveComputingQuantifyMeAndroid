package edu.mit.media.mysnapshot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.media.mysnapshot.R;

public class FontTextView extends TextView {

    public static String RALEWAY = "Raleway-Medium.ttf";

    static HashMap<String, Typeface> TYPEFACES = new HashMap<>();
    static List<String> TYPEFACE_NAMES = new ArrayList<String>() {{
        add("Montserrat-Regular.ttf");
        add("Montserrat-Bold.ttf");
        add(RALEWAY);
        add("Raleway-SemiBold.ttf");
        add("Raleway-Light.ttf");
    }};

    static int DEFAULT_FONT = 2;

    String typefaceName;
    Typeface typeface;

    boolean useHTML = false;

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FontTextView, 0, 0);
        try {
            typefaceName = TYPEFACE_NAMES.get(ta.getInt(R.styleable.FontTextView_customtypeface, DEFAULT_FONT));
            useHTML = ta.getBoolean(R.styleable.FontTextView_usehtml, false);
        } finally {
            ta.recycle();
        }

        if (typefaceName == null) {
            typefaceName = TYPEFACE_NAMES.get(DEFAULT_FONT);
        }
        setTypeFaceName(typefaceName);

        if (useHTML) {
            setText(Html.fromHtml(getText().toString()));
        }

    }

    public void setTypeFaceName(String name) {
        typefaceName = name;
        if (! TYPEFACES.containsKey(typefaceName) || TYPEFACES.get(typefaceName) == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + typefaceName);
            TYPEFACES.put(typefaceName, typeface);
        }
        typeface = TYPEFACES.get(typefaceName);
        setTypeface(typeface);
    }

    @Override
    public void setText(CharSequence text, BufferType type)
    {
        if (useHTML) {
            super.setText(Html.fromHtml(text.toString()), type);
        } else {
            super.setText(text, type);
        }
    }
}
