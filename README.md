# FloatingLayer 悬浮窗
本应用实现的是为了不需要权限就开启悬浮窗的功能，但是有一个限制就是只能在本应用中显示悬浮窗，当应用退到后台时，就无法在显示悬浮窗，即应用内悬浮窗。
## 目前存在的问题
开启悬浮窗需要申请SYSTEM_ALERT_WINDOW的权限，但是这个权限比较特殊，在Google 官方 Android6.0及以上，这个权限不能在通过代码申请方式获取，而且在小米等rom上，这个权限在4.x的时候就是默认关闭的（即使你申请了权限），需要用户手动在权限管理里手动开启。
## 注意
1. 预置应用应该是可以默认使用该权限的（经验说：预置应用默认开启所需要的权限，就算在apps->permission中显示的权限默认是关闭的）。
2. 通过Google Play Store(Version 6.05 or heigher is required)下载的需要该权限的应用，会被自动授予该权限
参考如下:
```
It is a new behaviour introduced in Marshmallow 6.0.1.
Every app that requests the SYSTEM_ALERT_WINDOW permission and that is installed through the Play Store (version 6.0.5 or higher is required), will have granted the permission automatically.
If instead the app is sideloaded, the permission is not automatically granted. You can try to download and install the Evernote APK from apkmirror.com. As you can see you need to manually grant the permission in Settings -> Apps -> Draw over other apps.
These are the commits [1] [2] that allow the Play Store to give the automatic grant of the SYSTEM_ALERT_WINDOW permission.
From: SYSTEM_ALERT_WINDOW - How to get this permission automatically on Android 6.0 and targetSdkVersion 23
```
## 原理
### WindowManager
我们通过windowmanager的addview方法来实现
### Android窗口机制
Android的视图都是通过窗口来实现的，Framework定义了三种窗口类型，三种类型的定义在WindowManager类中。
+ 第一种是应用窗口。所谓的应用窗口一般是指该窗口对应一个Activity，由于加载Activity是由AmS完成的，因此对于应用程序来讲，要创建一个应用类窗口，只能在Activity内部完成。
+ 第二种是子窗口。所谓的子窗口是指，该窗口必须有一个父窗口，父窗口可以是一个应用类型窗口，也可以是任何其他类型的窗口。
+ 第三类是系统窗口。系统窗口不需要对应任何Activity，也不需要父窗口。对于应用程序而言，理论上是无法创建系统窗口的，因为所有的应用程序都没有这个权限，然而系统进程却可以创建系统窗口。
	
WindowManager类对这三种类型进行了细化，把每一种类型都用一个int常量表示，这些实际上代表了窗口对应的层(Layer)。WmS在进行窗口叠加时，会按照该int常量的大小分配不同层，int值越大，代表层的位置越靠上面，即所谓的z-order。

由此我们可以通过设置不同类型的窗口，调整浮层在z轴上显示的位置，以此达到我们想要的效果。

### 为浮窗挑选合适的窗口
Android系统自带的浮层因为用到的是系统窗口，所以需要申请权限，因此我们需要找到一个不需要申请权限的窗口，而且窗口在z轴的显示层次越高越好，通过查询，我们经常使用的PopupWindow的子窗口类型为TYPE_APPLICATION_PANEL，PopupWindow是App内用到的层次最高的窗口，所以我们要选择一个在TYPE_APPLICATION_ PANEL之上且不需要申请权限的窗口，通过查阅Android系统的资料(如图1)，

TYPE_ APPLICA TION_ATTACHED_DIALOG就进入到了我们的视野，我们可以通过WindowManager创建窗口类型为TYPE_APPLICATION_ATTAC HED_DIALOG的窗口，这样就可以巧妙的绕开权限的问题，创建出浮层，而且浮层在z轴上的显示层次高于我们App内的任何窗口，由于浮层单独在一个窗口，因此一方面浮层的窗口和App的应用窗口不在同一个层，另一方面浮层可以在自己的窗口随意移动，这样浮层就不会干扰到App原来的功能
