package edu.mit.media.mysnapshot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.mit.media.mysnapshot.R;

/**
 * A modified Spinner that doesn't automatically select the first entry in the list.
 *
 * Shows the prompt if nothing is selected.
 *
 * Limitations: does not display prompt if the entry list is empty.
 */
public class NoDefaultSpinner extends AppCompatSpinner {

    List<String> entryValues = new ArrayList<>();

    public NoDefaultSpinner(Context context) {
        super(context);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttrs(context, attrs);

        setSupportBackgroundTintList(getResources().getColorStateList(R.color.spinner));
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(attrs,
                R.styleable.NoDefaultSpinner);

        int n = attributeArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attributeArray.getIndex(i);
            if (attr == R.styleable.NoDefaultSpinner_android_entryValues) {
                CharSequence[] vals = attributeArray.getTextArray(attr);
                setEntryValues(vals);
            }
        }

        attributeArray.recycle();
    }

    public void setEntryValues(CharSequence[] vals) {
        entryValues.clear();
        for (CharSequence s : vals) {
            entryValues.add(s.toString());
        }
    }

    public String getSelectedItemValue() {
        return entryValues.get(getSelectedItemPosition());
    }

    public int getIndexOfValue(String value) {
        return entryValues.indexOf(value);
    }

    public void setSelectionToValue(String value) {
        setSelection(getIndexOfValue(value));
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        handleAttrs(context, attrs);
    }

    @Override
    public void setAdapter(SpinnerAdapter orig ) {
        final SpinnerAdapter adapter = newProxy(orig);

        super.setAdapter(adapter);

        try {
            final Method m = AdapterView.class.getDeclaredMethod(
                    "setNextSelectedPositionInt",int.class);
            m.setAccessible(true);
            m.invoke(this,-1);

            final Method n = AdapterView.class.getDeclaredMethod(
                    "setSelectedPositionInt",int.class);
            n.setAccessible(true);
            n.invoke(this,-1);
        }
        catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }

    protected SpinnerAdapter newProxy(SpinnerAdapter obj) {
        return (SpinnerAdapter) java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                new Class[]{SpinnerAdapter.class},
                new SpinnerAdapterProxy(obj));
    }

    @Override
    public View getSelectedView() {
        SpinnerAdapter adapter = getAdapter();
        int position = getSelectedItemPosition();
        if (position < 0) {
            return adapter.getView(position, null, this);
        }
        return super.getSelectedView();
    }



    /**
     * Intercepts getView() to display the prompt if position < 0
     */
    protected class SpinnerAdapterProxy implements InvocationHandler {

        protected SpinnerAdapter obj;
        protected Method getView;

        protected View nullView = null;

        protected SpinnerAdapterProxy(SpinnerAdapter obj) {
            this.obj = obj;
            try {
                this.getView = SpinnerAdapter.class.getMethod(
                        "getView",int.class,View.class,ViewGroup.class);
            }
            catch( Exception e ) {
                throw new RuntimeException(e);
            }
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            try {
                return m.equals(getView) &&
                        (Integer)(args[0])<0 ?
                        getView((Integer)args[0],(View)args[1],(ViewGroup)args[2]) :
                        m.invoke(obj, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected View getView(int position, View convertView, ViewGroup parent)
                throws IllegalAccessException {

            if( position<0 ) {
                if (nullView != null) {
                    return nullView;
                }
                final TextView v =
                        (TextView) ((LayoutInflater)getContext().getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE)).inflate(
                                android.R.layout.simple_spinner_item,parent,false);
                v.setText(getPrompt());

                nullView = v;

                return v;
            }
            return obj.getView(position,convertView,parent);
        }
    }
}