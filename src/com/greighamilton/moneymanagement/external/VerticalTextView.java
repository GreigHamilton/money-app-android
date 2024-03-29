package com.greighamilton.moneymanagement.external;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.view.Gravity;
import android.widget.TextView;


/**
 * @see Sample code taken from http://stackoverflow.com/a/12834417 and adapted for usage.
 * 
 * @author Greig Hamilton
 */
public class VerticalTextView extends TextView {

	final boolean topDown;

	public VerticalTextView(Context context) {
		super(context);
		final int gravity = getGravity();
		if (Gravity.isVertical(gravity)
				&& (gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
			setGravity((gravity & Gravity.HORIZONTAL_GRAVITY_MASK)
					| Gravity.TOP);
			topDown = false;
		} else
			topDown = true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		TextPaint textPaint = getPaint();
		textPaint.setColor(getCurrentTextColor());
		textPaint.drawableState = getDrawableState();

		canvas.save();

		if (topDown) {
			canvas.translate(getWidth(), 0);
			canvas.rotate(90);
		} else {
			canvas.translate(0, getHeight());
			canvas.rotate(-90);
		}

		canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

		getLayout().draw(canvas);
		canvas.restore();
	}
}