# MiTV-ASTP0：Aerial 原版桌面 + CleanYourTV 启动兼容

本分支提供一个无界面的 HOME 入口，让实测小米电视在开机和待机唤醒时直接进入
另行安装的官方 Aerial。兼容入口复用本 fork 的 CleanYourTV 启动接收器，
不替换 Aerial 的包名或签名，不增加常驻服务。

这是 **hhhwl 的非官方实验性方案**。2026-10-29 在一台已安装过 CleanYourTV、
做过精简的电视上完成当天验收，并非恢复出厂后的通用安装验证，也尚未经过多日稳定性测试。

## 实测设备与结果

| 项目 | 记录 |
|---|---|
| 型号 / 平台 | Xiaomi MiTV-ASTP0 / mulan |
| 系统 / 固件 | Android 9 / API 28 / 24.7.4.3982 |
| 架构 / 内存 | armeabi-v7a / 约 2 GB |
| Aerial | 官方 standard 1.67，versionCode 81 |
| 桥接包 | com.ts.launcher，2.4-aerial-bridge.1，versionCode 5，8,844 字节 |
| 默认 HOME | com.ts.launcher/com.gathin.tvhome.MainActivity |
| 最终可见桌面 | app.orchard.tv/app.orchard.tv.MainActivity |
| 待机唤醒 | 用户确认直接进入 Aerial，无原桌面或广告闪过 |
| 完整系统重启 | 经同意执行真实 adb reboot；启动次数增加，用户确认直入 Aerial |
| 输入 / 设置 | Aerial 控制中心打开小米设置；返回正常；HDMI1 原生输入会话和主页返回通过 |
| 语音 / 投屏 | 用户确认遥控器语音和常用手机投屏正常；全部投屏包保持启用 |
| 屏保 | 用户确认 Aerial 航拍，无广告 |

未验证拔掉电源后的冷启动、其他型号/固件、未来 OTA 和长期稳定性。
没有用重启前后的缓存差异宣称固定内存节省量。未逐一测试所有投屏协议或所有 USB 媒体格式。

## 为什么需要兼容入口

本机保留小米原桌面时，唤醒会先出现它，再由 Aerial 守护切回。
在原桌面和旧兼容桌面同时停用后，直接把 `app.orchard.tv` 设为 HOME，
命令虽然报告成功，实际解析却是 FallbackHome，再按主页仍停在那里。
恢复旧入口后电视重新可用；将该入口换成无界面的 Aerial 启动桥后，HOME 解析及实机验收通过。
这些观察说明此固件对 HOME 候选的处理有差异，不代表已逆向证明所有小米型号采用同一白名单。

桥沿用 Aerial 1.67 `bringBackIntent` 的 MAIN action、NEW_TASK 与 RESET_TASK_IF_NEEDED，
并加入 NO_ANIMATION；
启动 Aerial 后立即结束。Aerial 缺失或被禁用时尝试打开系统设置。
接收器只在自己是选定 HOME 时响应开机/STR 广播，并要求发送方具有 `DEVICE_POWER` 权限。
没有轮询、无障碍服务、网络权限或后台 Service；仅申请 `RECEIVE_BOOT_COMPLETED`。
`minSdk 26 / targetSdk 25` 是这台旧固件的兼容配置，不能据此推荐用于所有 Android TV。

Aerial 的桌面守护、通知使用权、联网壁纸/天气/航拍及局域网配置页属于
[Aerial 官方程序](https://github.com/pengchujin/aerial-tv)，与此桥的权限分开。
航拍影片不包含在本发布中。官方航拍版权及字体/第三方许可见 Aerial 项目。

## 下载与构建

- [本次桥接 APK、此指南、许可证和校验文件](https://github.com/hhhwl/cleanyourtv/releases/tag/v2.4-mitv-astp0-aerial-bridge.1)
- [实测官方 Aerial 1.67](https://github.com/pengchujin/aerial-tv/releases/tag/v1.67)，下载 `aerial-standard.apk`
- [此前实测 CleanYourTV 原包，可供原安装用户恢复](https://github.com/hhhwl/cleanyourtv/releases/tag/v2.4-mitv-astp0-controls.1)

| 文件 | SHA-256 |
|---|---|
| Aerial-Xiaomi-Home-Bridge-1.apk | `69f6de68708871bc2ab3dcf941a4d64eddc2db649eb4265407b40ac7a0abbf0d` |
| 官方 aerial-standard.apk 1.67 | `29e5bda2350305d7d63c9dae0339ac885722fa42f239f51bbbb3bd133cf606ad` |
| 此前 CleanYourTV controls APK | `f441c595df746c00d6520af9fb2646f3ccad231aa663a5c9d522859b559ec259` |

桥和此前原包的签名证书 SHA-256 均为
`5e0b770f3c1a6d4fd2818b196a23152f08ec6d4a329ba83ed067aba256bf3659`。
本发布沿用这台电视先前安装所用的本地 Android debug 签名证书；私钥不发布。
别人重新构建产生的 debug key 通常不同，不能覆盖本发布的安装。
源码和资源可以复现；不承诺签名 APK 文件逐字节一致。

完整检出此分支或 Release 标签，不能只复制桥模块：它通过 Gradle Sync 复用
`launcher/app/src/main/java/com/gathin/tvhome/Startup{Policy,Receiver}.java`
及 `StartupPolicyTest.java`。需要完整 JDK 17、Android SDK 34 / Build Tools 34.0.0：

```sh
git clone --branch codex/aerial-startup-bridge https://github.com/hhhwl/cleanyourtv.git
cd cleanyourtv/launcher
# 先将 JAVA_HOME、ANDROID_HOME 指向自己的 JDK 17 和 SDK。
./gradlew --no-daemon :aerial-bridge:testReleaseUnitTest :aerial-bridge:lintRelease :aerial-bridge:assembleRelease
# 输出：aerial-bridge/build/outputs/apk/release/aerial-bridge-release.apk
```

实测源码通过 142 项 JVM 测试（原应用 135 + 桥复用 7）、lint 和发布构建；
原项目 Python 测试 57 项通过、1 项因环境跳过。APK 与电视上安装文件的 SHA-256 一致。

## 安装流程（逐项核对本机，不批量照搬到其他型号）

1. 电脑和电视连同一普通局域网，开启 ADB，电视上允许授权。设置 `TV` 为自己的设备地址，
   下文 `TV_IP` 是占位符。先确认型号、Android 版本及电视设置中显示的固件
   **24.7.4.3982** 与本报告相符。

   ```sh
   TV='TV_IP:5555'
   adb connect "$TV"
   adb -s "$TV" shell getprop ro.product.model
   adb -s "$TV" shell getprop ro.build.version.release
   ```

2. 备份包列表、停用状态和原 HOME。以下文件只保存在自己电脑，不发到公共 issue。
   对要调整的每个包另存 `dumpsys package`，保留 User 0 的 enabled 状态用于恢复。

   ```sh
   mkdir -p backup
   adb -s "$TV" shell pm list packages > backup/packages.txt
   adb -s "$TV" shell pm list packages -d > backup/disabled.txt
   adb -s "$TV" shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME > backup/home.txt
   adb -s "$TV" shell dumpsys package com.ts.launcher > backup/previous-home-package.txt
   adb -s "$TV" shell pm path com.ts.launcher
   # 如果已安装该包：将上条返回的 APK 路径（去掉 package:）用 adb pull 备份。
   ```

3. 从官方 Release 安装 Aerial 1.67，先按[该版本安装说明](https://github.com/pengchujin/aerial-tv/blob/v1.67/site/agent.md)
   完成授权和守护配置，验证界面、设置和 HDMI 可用。尊重已有无障碍服务，不能直接覆盖整串设置。
   本次选择保留了 Aerial 的守护和用户要求的 ADB 维护选项；后者不是桥运行所需。
   其他用户完成维护后可在 Aerial 中关闭“保持 ADB”，再关闭电视调试。

4. 若已存在 `com.ts.launcher`，必须先确认它是自己要替换的旧兼容桌面、APK 已备份，
   并核对证书和版本。它也可能被其他桌面使用。遇到签名或版本冲突应停止覆盖操作，
   保留原来的可用桌面，不能为了安装而先卸载唯一 HOME。

   ```sh
   apksigner verify --print-certs Aerial-Xiaomi-Home-Bridge-1.apk
   adb -s "$TV" install -r Aerial-Xiaomi-Home-Bridge-1.apk
   adb -s "$TV" shell pm enable --user 0 com.ts.launcher
   adb -s "$TV" shell cmd package set-home-activity --user 0 com.ts.launcher/com.gathin.tvhome.MainActivity
   adb -s "$TV" shell input keyevent 3
   adb -s "$TV" shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
   adb -s "$TV" shell dumpsys window windows
   ```

   必须确认解析到桥且可见焦点是 `app.orchard.tv/.MainActivity`，并用遥控器确认界面可操作。
   失败时恢复原 HOME，保持原桌面启用。

5. 只有上一步成功，才在这个已验证固件上停用原桌面。停用后再次固定 HOME，
   因为包候选集合变化可能使旧的“始终使用”选择失效。

   ```sh
   adb -s "$TV" shell pm disable-user --user 0 com.mitv.tvhome
   adb -s "$TV" shell cmd package set-home-activity --user 0 com.ts.launcher/com.gathin.tvhome.MainActivity
   adb -s "$TV" shell input keyevent 3
   ```

6. 按下面的记录决定是否逐项精简。每次只变更一个包，检查结果，小批完成后验证界面；
   用当前包信息确定用途。每项可用 `pm disable-user --user 0 PACKAGE`，把原状态记下来。
   保留系统核心包、电视输入服务、遥控器/蓝牙、输入法、升级、账号和家人使用的应用。

7. 验收主页、设置返回、常用 HDMI、遥控器语音及手机投屏；回到 Aerial 后用遥控器关开机，
   不按其他键观察。经使用者同意再执行 `adb -s "$TV" reboot`；等待系统及桌面就绪，
   检查 HOME 解析、可见界面、停用列表和开机日志，并请人确认有无闪过原桌面/广告。
   完整系统重启与拔电测试须分别记录。不要用伪造的全局 STR 广播代替真实唤醒。

## 本次精简记录：当天验证，非通用推荐清单

以下是本次相对原状态的变更，不代表设备出厂时的全部后台状态。
遥控器语音保留，用户选择关闭直接喊“小爱”的远场唤醒；所有投屏保留。

| 停用包 | 本次用途判断 / 选择 |
|---|---|
| com.miui.analytics | 统计采集 |
| com.miui.tv.analytics | 电视使用统计 |
| com.mitv.cloudcontrol | 厂商云控策略 |
| com.xiaomi.mitv.tvpush.tvpushservice | 厂商推送 |
| com.android.smspush | WAP 短信推送 |
| com.xm.webcontent | 推荐网页容器 |
| com.mitv.shoplugin | 购物插件 |
| com.xiaomi.mitv.tvmanager | 电视管家常驻服务 |
| com.xiaomi.wakeupservice | 用户不用的远场语音唤醒 |
| com.mitv.tvhome | 桥验收后停用的原桌面 |
| com.xiaomi.tv.gallery | 此前已停用的“时尚画报”广告屏保，本次继续保持 |

保留了 `com.xiaomi.voicecontrol`、`com.xiaomi.mitv.smartshare`、
`com.duokan.airkan.tvbox`、`com.xiaomi.miplay`、`com.xiaomi.mitv.settings`、
`com.xiaomi.mitv.tvplayer`、`com.mitv.mivideoplayer`、`com.xiaomi.mitv.upgrade`，
以及原有视频应用和账号。不要把这些保留项和上表一起做成批量停用命令。
这份记录尚不满足上游“停用后使用数天”的正式机型 debloat 清单要求。

## 恢复

先重新启用小米桌面，再按 [Aerial 官方还原说明](https://github.com/pengchujin/aerial-tv/blob/v1.67/site/agent.md)
关闭其桌面守护并恢复系统配置，然后将小米桌面设为 HOME，验证界面正常：

```sh
adb -s "$TV" shell pm enable --user 0 com.mitv.tvhome
# 此处先完成上文链接中的 Aerial 还原步骤。
adb -s "$TV" shell cmd package set-home-activity --user 0 com.mitv.tvhome/.MainActivityUserMode
adb -s "$TV" shell input keyevent 3
```

确认原桌面可用后才停用桥；随后再固定一次 HOME：

```sh
adb -s "$TV" shell pm disable-user --user 0 com.ts.launcher
adb -s "$TV" shell cmd package set-home-activity --user 0 com.mitv.tvhome/.MainActivityUserMode
```

恢复自己此次停用的后台，按备份的原状态使用 `pm default-state --user 0 PACKAGE`
（原 enabled=0）或 `pm enable --user 0 PACKAGE`（原 enabled=1）。不恢复原本就停用的包。
如要恢复此前的 CleanYourTV 界面，先保持另一可用 HOME，再覆盖安装自己的原 APK；
本次备份与桥同证书、同 versionCode 5，满足覆盖恢复条件。
再启用 `com.ts.launcher`、选择该 HOME，并实测。这些恢复步骤不会清除账号或卸载系统包。

## 来源与许可

- CleanYourTV：[Gathin23/cleanyourtv](https://github.com/Gathin23/cleanyourtv)，MIT；
  本 fork 保留其版权和完整 [LICENSE](../LICENSE)。
- Aerial：[pengchujin/aerial-tv](https://github.com/pengchujin/aerial-tv)，独立安装的官方 MIT 项目。
- 桥的实机源码提交：`39b804d91cc3c0747122502b36b114d9b275ca50`；
  共享启动策略来自本分支祖先 `6392b6d`。发布标签保留全部依赖和历史。
- 本方案的问题请提交至 [hhhwl/cleanyourtv issues](https://github.com/hhhwl/cleanyourtv/issues)。
