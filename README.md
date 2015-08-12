# ProgressCircle [![Download](https://api.bintray.com/packages/alokvnair/maven/ProgressCircle/images/download.svg)](https://bintray.com/alokvnair/maven/ProgressCircle/_latestVersion)
A Material Design Progress Loader based on [Spinner Loader - Gooey light Effect](https://goo.gl/2L7wDk) by Christophe Kerebel

![ProgressCircle](https://raw.githubusercontent.com/alokvnair/ProgressCircle/master/screens/progressCircle.gif)

###Usage

You can add to your project by adding the following dependency to your `build.gradle`

```
dependencies {
    compile 'com.alokvnair:progresscircle:1.0.0'
}
```

only one attribute `point_color`

```xml
<com.alokvnair.progresscircle.ProgressCircle
        android:id="@+id/progressCircle"
        app:point_color="@color/accent_material_dark"
        android:layout_width="40dp"
        android:layout_height="40dp"/>
```

and one method `public void setPointColor(int color)`

```java
setPointColor(color);
```

### License

```
The MIT License (MIT)

Copyright (c) 2015 Alok Nair

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
