package edu.mit.media.mysnapshot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.media.mysnapshot.R;

public class SelectableIcon extends LinearLayout {

    public static class IconChoice {
        public static final String SIZE_SMALL = "small", SIZE_SUPERSMALL = "supersmall";

        public String labelText = "", size = "";
        public int iconId = 0;
        public Integer selectedColor = null;
        public String value = "";

        public IconChoice(String labelText, int iconId, String value) {
            this.labelText = labelText;
            this.iconId = iconId;
            this.value = value;
        }

        public IconChoice(String labelText, int iconId, String value, int selectedColor) {
            this(labelText, iconId, value);
            this.selectedColor = selectedColor;
        }
    }

    boolean appearsSelected;

    View rootView;

    ImageView icon;
    TextView label;

    SelectableIconGroup parent;

    int selectedColorGray;

    protected IconChoice choice = new IconChoice("", R.drawable.whitecircle, "");


    public SelectableIcon(Context context, AttributeSet attrs) {
        super(context, attrs, R.style.QuestionChoice);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SelectableIcon, 0, 0);
            try {
                choice.selectedColor = ta.getColor(R.styleable.SelectableIcon_tintColor, getResources().getColor(R.color.accent));
                choice.value = ta.getString(R.styleable.SelectableIcon_value);
                choice.iconId = ta.getResourceId(R.styleable.SelectableIcon_image, R.drawable.whitecircle);
                choice.labelText = ta.getString(R.styleable.SelectableIcon_text);
                choice.size = ta.getString(R.styleable.SelectableIcon_size);
            } finally {
                ta.recycle();
            }
        }

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());

                } else if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                    // outside bounds
                    setAppearsSelected(false);
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setAppearsSelected(true);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        setAppearsSelected(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (appearsSelected) {
                            if (! isSelected() || parent.childCanToggle(SelectableIcon.this)) {
                                setSelected(! isSelected());
                            }
                        }
                        setAppearsSelected(false);
                        break;
                }

                return false;
            }
        });

    }

    public void setChoice(IconChoice choice) {
        this.choice = choice;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        choice.size = choice.size == null ? "" : choice.size;
        switch(choice.size) {
            case "small":
                rootView = inflater.inflate(R.layout.view_selectableicon_small, this);
                break;
            case "supersmall":
                rootView = inflater.inflate(R.layout.view_selectableicon_supersmall, this);
                break;
            default:
                rootView = inflater.inflate(R.layout.view_selectableicon, this);

        }

        icon = (ImageView) findViewById(R.id.icon);
        label = (TextView) findViewById(R.id.label);


        label.setText(choice.labelText);
        icon.setImageDrawable(getResources().getDrawable(choice.iconId));

        if (choice.selectedColor == null) {
            choice.selectedColor = getContext().getResources().getColor(R.color.accent);
        }

        int grey = getResources().getColor(R.color.question_grey);
        selectedColorGray = Color.argb(255, grey, grey, grey);

        setSelected(false);


    }

    private Rect rect;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() instanceof SelectableIconGroup) {
            parent = (SelectableIconGroup) getParent();
        }
    }

    @Override
    public void setSelected(boolean selected) {

        boolean toggled = selected != isSelected();

        super.setSelected(selected);
        if (parent != null && selected) {
            parent.onChildSelected(this);
        }
        if (parent != null && toggled) {
            parent.onChildToggled(this);
        }
        setDrawState();
    }

    public boolean getAppearsSelected() {
        return appearsSelected;
    }

    public void setAppearsSelected(boolean appearsSelected) {
        this.appearsSelected = appearsSelected;
        parent.onChildAppearsSelected(appearsSelected ? this : null);
        setDrawState();
    }

    public void setDrawState() {
        invalidate();

        if (isSelected() || appearsSelected) {
            icon.setColorFilter(choice.selectedColor);
            label.setTextColor(choice.selectedColor);
        } else {
            icon.setColorFilter(selectedColorGray);
            label.setTextColor(Color.BLACK);
        }
    }


    static int getGreyscaleVal(int color) {
        int r = (color)&0xFF;
        int g = (color>>8)&0xFF;
        int b = (color>>16)&0xFF;
        int a = (color>>24)&0xFF;
        return (r + g + b) / 3;
    }

    public String getValue() {
        return choice.value;
    }


}
