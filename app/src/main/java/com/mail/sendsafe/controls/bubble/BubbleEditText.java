package com.mail.sendsafe.controls.bubble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mail.sendsafe.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author rafal
 * 
 */
public class BubbleEditText extends EditText {
	/**
	 * 
	 */
	private static final String TAG = BubbleEditText.class.getSimpleName();

	/**
	 * text that is going to be wrapped in bubble image
	 */
	public ArrayList<String> mBubbleTexts;

	/**
	 * callback
	 */
	private OnBubbleClickListener mListener;

	/**
	 * context
	 */
	private Context mContext;

	/**
	 * hyperlink remembers when span begins and where it ends
	 */
	public ArrayList<Hyperlink> mHyperLinks;

	private int mDrawableRight;

	/**
	 * constructor
	 * 
	 * @param context
	 * @param attrs
	 */
	public BubbleEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mBubbleTexts = new ArrayList<String>();
		setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * add text thet is going to be wrapped in bubble image view
	 *
	 */
	public void addBubbleText(String text) {
		mBubbleTexts.add(text);
	}

	public void removeBubble(Hyperlink linkSpec) {

		String before = getText().toString().substring(0, linkSpec.start);
		String after = getText().toString().substring(linkSpec.end,
				getText().length());

		setText(before + after);
		refreshBubbles();

	}

	public void setText(String text) {
		super.setText(text, BufferType.SPANNABLE);
		refreshBubbles();
		setCoursorAtTheEndOfEditText();
	}

	/**
	 * places coursor at the end rather than at the beginning
	 */
	private void setCoursorAtTheEndOfEditText() {
		int textLength = getText().length();
		setSelection(textLength, textLength);
	}

	public void refreshBubbles() {

		// remove patterns that were deleted by user from edit text view
		removeDeletedPatterns(getText().toString(), false);
		SpannableString spannable = new SpannableString(getText().toString());

		mHyperLinks = new ArrayList<Hyperlink>();

		for (String pattern : mBubbleTexts) {
			Log.v(TAG, "gathering links for pattern: " + pattern);
			ArrayList<Hyperlink> hyperLinks = gatherLinks(spannable, pattern);
			mHyperLinks.addAll(hyperLinks);
			// create image span bubble for each created hyperling (in case
			// there are reapeating petterns)
			for (Hyperlink hyperLinkSpecs : hyperLinks) {
				ImageSpan imageSpan = getImageSpan(hyperLinkSpecs);
				spannable.setSpan(imageSpan, hyperLinkSpecs.start,
						hyperLinkSpecs.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// create clickable span for callbacks
				ClickableSpan clickSpan = new BubbleClickableSpan(
						hyperLinkSpecs);

				spannable.setSpan(clickSpan, hyperLinkSpecs.start,
						hyperLinkSpecs.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		// add avarything to edit text view
		setText(spannable);
	}

	private ImageSpan getImageSpan(Hyperlink hyperLinkSpecs) {
		View textView = createContactTextView(hyperLinkSpecs.textSpan
				.toString());
		BitmapDrawable bitmapDrawable = (BitmapDrawable) convertViewToDrawable(textView);
		bitmapDrawable.setBounds(10, 10, bitmapDrawable.getIntrinsicWidth(),
				bitmapDrawable.getIntrinsicHeight());
		return new ImageSpan(bitmapDrawable);
	}

	private class BubbleClickableSpan extends ClickableSpan {

		private Hyperlink linkSpec;

		private BubbleClickableSpan(Hyperlink linkSpec) {
			Log.v(TAG, "clickable span set");
			this.linkSpec = linkSpec;
		}

		@Override
		public void onClick(View widget) {
			mListener.onBubbleClick(widget, linkSpec);
		}

	}

	public void setBubbleDrawableRight(int drawable) {
		mDrawableRight = drawable; 
	}

	public View createContactTextView(String text) {

		LinearLayout l = new LinearLayout(getContext());
		l.setOrientation(LinearLayout.HORIZONTAL);
		l.setBackgroundResource(R.drawable.bordered_rectangle_rounded_corners);

		TextView tv = new TextView(getContext());
		l.addView(tv);
		tv.setText(text);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

		ImageView im = new ImageView(getContext());
		l.addView(im);
		im.setImageResource(mDrawableRight);
		im.setScaleType(ImageView.ScaleType.FIT_CENTER);

		return l;
	}

	/*public TextView createContactTextView(String text) {

		TextView tv = new TextView(mContext);
		tv.setText(text);
		tv.setTextSize(16);
		tv.setPadding(10, 10, 10, 10);
		tv.setBackgroundResource(R.color.bubble);
		if (mDrawableRight > 0) {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, mDrawableRight, 0);
			tv.setCompoundDrawablePadding(15);
		}

		return tv;
	}*/

	public Object convertViewToDrawable(View view) {

		int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(spec, spec);
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth()+10,
				view.getMeasuredHeight()+10, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		c.drawColor(Color.DKGRAY);
		c.translate(-view.getScrollX(), -view.getScrollY());
		view.draw(c);
		view.setDrawingCacheEnabled(true);
		Bitmap cacheBmp = view.getDrawingCache();
		Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
		view.destroyDrawingCache();
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), viewBmp);

		return bd;

	}

	public void removeDeletedPatterns(String text, boolean delateExisting) {
		Log.v(TAG, "pattern: " + text + ". delete existing: " + delateExisting);
		for (int j = 0; j < mBubbleTexts.size(); j++) {

			if (text.contains(mBubbleTexts.get(j)) == delateExisting) {
				Log.v(TAG, "pattern deleted: " + text);
				mBubbleTexts.remove(j);
			}
		}

	}

	public void setOnBubbleClickListener(OnBubbleClickListener newListener) {
		Log.v(TAG, "setOnTextLinkClickListener");
		mListener = newListener;
	}

	private final ArrayList<Hyperlink> gatherLinks(Spannable s, String st) {
		ArrayList<Hyperlink> hyperlinks = new ArrayList<Hyperlink>();
		Pattern pattern = Pattern.compile(Pattern.quote(st));
		Matcher m = pattern.matcher(s);

		while (m.find()) {
			Hyperlink spec = new Hyperlink();

			int start = m.start();
			int end = m.end();

			spec.textSpan = s.subSequence(start, end);

			spec.start = start;
			spec.end = end;
			hyperlinks.add(spec);

		}
		return hyperlinks;
	}

	public void clearPatterns() {
		mBubbleTexts.clear();
		// listOfLinks.clear();
	}

}
