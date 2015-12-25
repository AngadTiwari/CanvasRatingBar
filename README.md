# CanvasRatingBar
A simple canvas based rating bar. Simple and ALLEGIANT in look.
### How to add
CanvasRatingBar is published with JitPack.io. To add this library to your project, add these lines to your build.gradle
```
repositories {
  maven { url "https://jitpack.io" }
}
dependencies {
  compile 'com.github.sembozdemir:ViewPagerArrowIndicator:1.0.0'
}
```
### How to use
#### It is so simple. Just wrap your CanvasRatingBar with any ViewGroup
```
<com.angtwr31.library.CanvasRatingBar
        android:id="@+id/ratingbar"
        android:layout_width="wrap_content"
        android:layout_height="30dp"
        app:maxStars="5"
        app:ratingValue="0.0"
        app:halfStarEnable="true"
        app:fillColor="@android:color/holo_red_light"/>
```
