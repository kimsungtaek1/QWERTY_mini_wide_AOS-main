# Android Speech Bubble Design

This document contains the XML drawable resources for creating an iOS-style speech bubble with shadow effects for Android keyboard key previews.

## Features
- Gradient background (white to light gray)
- Variable border thickness (thinner at top, thicker at bottom)
- Sharp triangular tail
- Soft shadow effects
- Rounded corners

## Files

### bubble_background.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 본체 하단 그림자 -->
    <item android:top="32dp">
        <shape android:shape="rectangle">
            <gradient
                android:startColor="#25000000"
                android:centerColor="#15000000"
                android:endColor="#00000000"
                android:angle="270" />
            <size android:height="8dp" />
            <corners
                android:bottomLeftRadius="8dp"
                android:bottomRightRadius="8dp" />
        </shape>
    </item>
    
    <!-- 꼬리 왼쪽 그림자 -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="-3dp"
        android:left="-1dp">
        <rotate
            android:fromDegrees="45"
            android:pivotX="50%"
            android:pivotY="50%">
            <shape android:shape="rectangle">
                <size
                    android:width="6dp"
                    android:height="6dp" />
                <gradient
                    android:startColor="#40000000"
                    android:centerColor="#20000000"
                    android:endColor="#00000000"
                    android:angle="225" />
            </shape>
        </rotate>
    </item>
    
    <!-- 꼬리 오른쪽 그림자 -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="-3dp"
        android:right="-1dp">
        <rotate
            android:fromDegrees="45"
            android:pivotX="50%"
            android:pivotY="50%">
            <shape android:shape="rectangle">
                <size
                    android:width="6dp"
                    android:height="6dp" />
                <gradient
                    android:startColor="#40000000"
                    android:centerColor="#20000000"
                    android:endColor="#00000000"
                    android:angle="315" />
            </shape>
        </rotate>
    </item>
    
    <!-- 말풍선 본체 외곽선 (하단이 더 두꺼운 효과) -->
    <item android:bottom="1dp">
        <shape android:shape="rectangle">
            <gradient
                android:startColor="#E8E8E8"
                android:endColor="#D0D0D0"
                android:angle="270" />
            <corners android:radius="8dp" />
        </shape>
    </item>
    
    <!-- 말풍선 본체 내부 (그라데이션 효과) -->
    <item android:bottom="1.3dp" android:left="0.3dp" android:right="0.3dp" android:top="0.3dp">
        <shape android:shape="rectangle">
            <gradient
                android:startColor="#FFFFFF"
                android:endColor="#F8F8F8"
                android:angle="270" />
            <corners android:radius="7.7dp" />
            <padding
                android:left="12dp"
                android:top="4dp"
                android:right="12dp"
                android:bottom="4dp" />
        </shape>
    </item>
    
    <!-- 꼬리 본체 (더 뾰족한 삼각형) -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="-5dp">
        <rotate
            android:fromDegrees="45"
            android:pivotX="50%"
            android:pivotY="50%">
            <shape android:shape="rectangle">
                <size
                    android:width="7dp"
                    android:height="7dp" />
                <solid android:color="#FAFAFA" />
            </shape>
        </rotate>
    </item>
    
    <!-- 꼬리 양쪽에 그림자 효과 -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="-5dp"
        android:left="0.5dp">
        <rotate
            android:fromDegrees="45"
            android:pivotX="50%"
            android:pivotY="50%">
            <shape android:shape="rectangle">
                <size
                    android:width="7.5dp"
                    android:height="7.5dp" />
                <solid android:color="#00000000" />
                <stroke
                    android:width="0.3dp"
                    android:color="#F0F0F0" />
            </shape>
        </rotate>
    </item>
    
    <!-- 꼬리의 윗부분을 가리는 사각형 (그라데이션) -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="0dp">
        <shape android:shape="rectangle">
            <size
                android:width="10dp"
                android:height="4dp" />
            <gradient
                android:startColor="#FFFFFF"
                android:endColor="#F8F8F8"
                android:angle="270" />
        </shape>
    </item>
</layer-list>
```

### bubble_background_dark.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 본체 하단 그림자 -->
    <item android:top="32dp">
        <shape android:shape="rectangle">
            <gradient
                android:startColor="#25000000"
                android:centerColor="#15000000"
                android:endColor="#00000000"
                android:angle="270" />
            <size android:height="8dp" />
            <corners
                android:bottomLeftRadius="8dp"
                android:bottomRightRadius="8dp" />
        </shape>
    </item>
    
    <!-- 꼬리 그림자 -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="-3dp">
        <rotate
            android:fromDegrees="45"
            android:pivotX="50%"
            android:pivotY="50%">
            <shape android:shape="rectangle">
                <size
                    android:width="8dp"
                    android:height="8dp" />
                <gradient
                    android:type="radial"
                    android:gradientRadius="6dp"
                    android:centerX="0.5"
                    android:centerY="0.5"
                    android:startColor="#20000000"
                    android:endColor="#00000000" />
            </shape>
        </rotate>
    </item>
    
    <!-- 말풍선 본체 외곽선 (하단이 더 두꺼운 효과) -->
    <item android:bottom="1dp">
        <shape android:shape="rectangle">
            <gradient
                android:startColor="#E8E8E8"
                android:endColor="#D0D0D0"
                android:angle="270" />
            <corners android:radius="8dp" />
        </shape>
    </item>
    
    <!-- 말풍선 본체 내부 (그라데이션 효과) -->
    <item android:bottom="1.3dp" android:left="0.3dp" android:right="0.3dp" android:top="0.3dp">
        <shape android:shape="rectangle">
            <gradient
                android:startColor="#FFFFFF"
                android:endColor="#F8F8F8"
                android:angle="270" />
            <corners android:radius="7.7dp" />
            <padding
                android:left="12dp"
                android:top="4dp"
                android:right="12dp"
                android:bottom="4dp" />
        </shape>
    </item>
    
    <!-- 꼬리 본체 (더 뾰족한 삼각형) -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="-5dp">
        <rotate
            android:fromDegrees="45"
            android:pivotX="50%"
            android:pivotY="50%">
            <shape android:shape="rectangle">
                <size
                    android:width="7dp"
                    android:height="7dp" />
                <solid android:color="#FAFAFA" />
            </shape>
        </rotate>
    </item>
    
    <!-- 꼬리 외곽선 -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="-5dp"
        android:left="0.5dp">
        <rotate
            android:fromDegrees="45"
            android:pivotX="50%"
            android:pivotY="50%">
            <shape android:shape="rectangle">
                <size
                    android:width="6.5dp"
                    android:height="6.5dp" />
                <solid android:color="#00000000" />
                <stroke
                    android:width="0.3dp"
                    android:color="#F0F0F0" />
            </shape>
        </rotate>
    </item>
    
    <!-- 꼬리의 윗부분을 가리는 사각형 (그라데이션) -->
    <item
        android:gravity="bottom|center_horizontal"
        android:bottom="0dp">
        <shape android:shape="rectangle">
            <size
                android:width="10dp"
                android:height="6dp" />
            <gradient
                android:startColor="#FFFFFF"
                android:endColor="#F8F8F8"
                android:angle="270" />
        </shape>
    </item>
</layer-list>
```

## Usage in Layout

### key_popup_layout.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/popup_container"
    android:layout_width="48dp"
    android:layout_height="35dp"
    android:background="@drawable/bubble_background"
    android:elevation="8dp"
    android:gravity="center"
    android:orientation="vertical">

    <TextView
        android:id="@+id/popup_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="@android:color/black"
        android:textSize="18sp"
        android:textStyle="bold"
        android:gravity="center" />
</LinearLayout>
```

## PopupWindow Configuration (Kotlin)

```kotlin
class KeyPopupWindow(private val context: Context) {
    private var popupWindow: PopupWindow? = null
    private var popupView: View? = null
    
    init {
        setupPopupView()
    }
    
    private fun setupPopupView() {
        popupView = LayoutInflater.from(context).inflate(R.layout.key_popup_layout, null)
        
        popupWindow = PopupWindow(popupView).apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isFocusable = false
            isTouchable = false
            isClippingEnabled = false
            setBackgroundDrawable(null)
            elevation = 12f
        }
    }
    
    fun show(anchor: View, text: String) {
        updateContent(text)
        updateStyle()
        
        // Measure the popup view to get its dimensions
        popupView?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = popupView?.measuredWidth ?: 0
        val popupHeight = popupView?.measuredHeight ?: 0
        
        // Get anchor location on screen
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        
        val anchorX = location[0]
        val anchorY = location[1]
        val anchorWidth = anchor.width
        val anchorHeight = anchor.height
        
        // Calculate popup position (centered above the key)
        val xOffset = (anchorWidth - popupWidth) / 2
        val yOffset = -(popupHeight + anchorHeight - 40) // Position even lower (closer to the key)
        
        try {
            popupWindow?.showAsDropDown(anchor, xOffset, yOffset, Gravity.NO_GRAVITY)
            showAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateContent(text: String) {
        popupView?.findViewById<TextView>(R.id.popup_text)?.apply {
            this.text = text
            textSize = when {
                text.length > 1 -> 12f
                else -> 14f
            }
        }
    }
    
    private fun updateStyle() {
        val isLightMode = KeyLetter.isLightMode
        popupView?.findViewById<View>(R.id.popup_container)?.apply {
            setBackgroundResource(R.drawable.bubble_background)
        }
        
        popupView?.findViewById<TextView>(R.id.popup_text)?.apply {
            setTextColor(context.getColor(android.R.color.black))
        }
    }
}
```

## Design Specifications

- **Container Size**: 48dp x 35dp
- **Corner Radius**: 8dp
- **Border**: 0.3dp, gradient from #E8E8E8 to #D0D0D0
- **Background**: Gradient from #FFFFFF to #F8F8F8
- **Tail Size**: 7dp x 7dp (45° rotated square)
- **Shadow**: Bottom shadow with gradient
- **Text Size**: 14f for single character, 12f for multiple
- **Elevation**: 8dp for container, 12f for PopupWindow