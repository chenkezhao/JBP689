# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\0Develop\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

###--------------xUtils3代码混淆配置-----------
-keepattributes Signature,*Annotation*
-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers @org.xutils.db.annotation.* class * {*;}
-keepclassmembers @org.xutils.http.annotation.* class * {*;}
-keepclassmembers class * {
    @org.xutils.view.annotation.Event <methods>;
}

###--------------umeng 实时统计混淆配置-----------
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

#由于SDK需要引用导入工程的资源文件，通过了反射机制得到资源引用文件R.java，
#但是在开发者通过proguard等混淆/优化工具处理apk时，proguard可能会将R.java删除，
#如果遇到这个问题，请在proguard配置文件中添加keep命令如：
#-keep public class com.ckz.thought.R$*{
#public static final int *;
#}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
###--------------EventBus 3.0 混淆配置-----------
# http://greenrobot.org/eventbus/documentation/proguard/
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


###--------------volley混淆配置-----------
-keep class com.android.volley.** { *; }
-keep interface com.android.volley.** { *; }

###--------------jsoup混淆配置-----------
 -keep class org.jsoup.**
