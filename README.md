Android BottomSheet example
===========================

![AppLover](https://raw.github.com/tutti-ch/android-bottomsheet/master/preview.png)

This is more or less the BottomSheet implementation in Lollipop used for the ResolverActivity (as well used for sharing). Just with some changes to make it work on API 15+ instead of 21 only.

Copied (and slightly modified) files from AOSP:

- [ResolverDrawerLayout.java](https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/widget/ResolverDrawerLayout.java) - Where all the magic lies ;-)
- [resolver_list_with_default.xml](https://github.com/android/platform_frameworks_base/blob/master/core/res/res/layout/resolver_list_with_default.xml) - Where ResolverDrawerLayout gets used
- [themes_devices_default.xml](https://github.com/android/platform_frameworks_base/blob/master/core/res/res/values/themes_device_defaults.xml) - just Theme.DeviceDefault.Resolver
- [attrs.xml](https://github.com/android/platform_frameworks_base/blob/master/core/res/res/values/attrs.xml) - just ResolverDrawerLayout and ResolverDrawerLayout_LayoutParams
- [ResolverActivity.java](https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/app/ResolverActivity.java) - Where everything gets used

## Features
- BottomSheet as an Activity that overlays the current activity
 - Can be fully customized
- ChooserActivity based on BottomSheet for easy chooser customization (for example for sharing)
 - Set items to be moved to the top (setPriorityItems(...))
 - Used items move to the top

## Dependencies

```
compile 'com.android.support:appcompat-v7:21.0.3' // or higher

// only needed if you want to use the BottomSheetChooserActivity
compile 'com.android.support:recyclerview-v7:21.0.3'
```

## Usage
- build.gradle
```
compile 'ch.tutti.android.bottomsheet:library:<latest-version>'
```

- Extend ch.tutti.android.bottomsheet.BottomSheetActivity
```java
public class BottomSheetExampleBaseActivity extends BottomSheetActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_bottom_sheet);
        setBottomSheetTitle(R.string.app_name);
        setBottomSheetIcon(R.mipmap.ic_launcher);
        setBottomSheetTitleVisible(true);

        // TODO setup own views
    }
}
```
- Add BottomSheetActivity to the AndroidManifest.xml
```
<activity
    android:name=".CustomBottomSheetActivity"
    android:theme="@style/BottomSheet.Light"
    android:finishOnCloseSystemDialogs="true"
    android:excludeFromRecents="true"
    android:documentLaunchMode="never"
    android:relinquishTaskIdentity="true" />
```
- ```java
Intent bottomSheetIntent = new Intent(this, CustomBottomSheetActivity.class);
BottomSheetActivity.startActivity(this, bottomSheetIntent); // Used so that bottom sheet animates upwards on start.
```

For an example see the example directory.

### BottomSheetShareActivity

- Add it to the AndroidManifest.xml

```
<activity
    android:name="ch.tutti.android.bottomsheet.BottomSheetChooserActivity"
    android:documentLaunchMode="never"
    android:excludeFromRecents="true"
    android:finishOnCloseSystemDialogs="true"
    android:relinquishTaskIdentity="true"
    android:theme="@style/BottomSheet.Light" />
```

- Launch the ShareActivity with:

```java
Intent bottomSheetIntent = BottomSheetChooserActivity.create(this)
    .forIntent(yourShareIntent)
    .title("Share")
    .icon(R.mipmap.ic_launcher)
    // .history(false)
    // .priority("com.whatsapp", "com.facebook.katana", "com.facebook.orca",
    //        "com.google.android.gm", "com.google.android.talk",
    //        "com.google.android.apps.plus")
    .getIntent();
BottomSheetActivity.startActivity(this, bottomSheetIntent); // Used so that bottom sheet animates upwards on start.
```

## License

    Copyright (c) 2015 tutti.ch AG

    Permission to use, copy, modify, and distribute this software for any
    purpose with or without fee is hereby granted, provided that the above
    copyright notice and this permission notice appear in all copies.

    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
