package uiuc.mbr.ui;

/**
 * http://stackoverflow.com/questions/12317960/android-numberpicker-set-min-max-default-from-xml
 * Allows the use of xml elements to set a min and max value as well as how much it increments by.
 */
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)//For backward-compability
public class MyNumberPicker extends NumberPicker {

	/**Calls superclass constructor.*/
	public MyNumberPicker(Context context) {
		super(context);
	}

	/**Calls superclass constructor, and also looks for values to use in the AttributeSet.*/
	public MyNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		processAttributeSet(attrs);
	}

	/**Calls superclass constructor, and also looks for values to use in the AttributeSet.*/
	public MyNumberPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		processAttributeSet(attrs);
	}

	/**
	 * Takes the attributes passed in from the xml, and parses out the numbers that have the
	 * attributes min, max and incr.
	 * Then creates the correct values and puts into array, and sends to the numberPicker to display.
	 */
	private void processAttributeSet(AttributeSet attrs) {
		//This method reads the parameters given in the xml file and sets the properties according to it
		this.setMinValue(attrs.getAttributeIntValue(null, "min", 0));
		this.setMaxValue(attrs.getAttributeIntValue(null, "max", 0));
		float incr = attrs.getAttributeFloatValue(null, "incr", 1);
		String[] display = new String[this.getMaxValue()];
		for(int i=0; i<display.length; i++){
			float temp = (this.getMinValue()+i)*incr;
			if(temp == (long) temp)
				display[i]=String.format("%d",(long)temp);
			else
				display[i]=String.format("%.1f",temp);
		}
		this.setMaxValue(this.getMaxValue()-1);
		this.setDisplayedValues(display);
	}
}