/*
 * Minimal compatibility implementation for legacy android.preference.SeekBarPreference.
 * It is sufficient for Browser's custom preference subclass compilation.
 */
package android.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
