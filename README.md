# AndroidLayoutExtender
Developing...

## root layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
                  >

    <LinearLayout android:layout_width="match_parent"
                  android:layout_height="match_parent"
                  android:orientation="vertical">

        <section name="sec1">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>
        </section>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

        <section name="sec2"/>
    </LinearLayout>
</layout>
```

## child layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout extends="@layout/parent"/>

    <section name="sec1">

        <LinearLayout>

            <TextView/>

            <Button/>
        </LinearLayout>
    </section>

    <section name="sec2">

        <ReleativeLayout>

            <ScrollView/>
        </ReleativeLayout>
    </section>

</layout>
```
