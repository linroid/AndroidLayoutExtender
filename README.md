# AndroidLayoutExtender
Developing...

## root layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout>
    <LinearLayout ...>
        <section name="sec1">
            <TextView .../>
        </section>
        <TextView .../>
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
            <TextView .../>
            <Button .../>
        </LinearLayout>
    </section>
    <section name="sec2">
        <ReleativeLayout>
            <ScrollView .../>
        </ReleativeLayout>
    </section>
</layout>
```

## generate...
```xml
<LinearLayout ...>
    <LinearLayout>
        <TextView .../>
        <Button .../>
    </LinearLayout>
    <TextView .../>
    <ReleativeLayout>
        <ScrollView .../>
    </ReleativeLayout>
</LinearLayout>
```
