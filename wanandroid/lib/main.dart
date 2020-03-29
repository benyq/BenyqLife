import 'dart:io';

import 'package:event_bus/event_bus.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:wanandroid/common/application.dart';
import 'package:wanandroid/res/colors.dart';
import 'package:wanandroid/ui/splashScreen.dart';
import 'package:wanandroid/utils/spUtil.dart';
import 'package:wanandroid/utils/themeUtil.dart';

import 'common/Common.dart';
import 'common/user.dart';
import 'data/event/themeChangeEvent.dart';
import 'net/dioManager.dart';

void main() async {

  WidgetsFlutterBinding.ensureInitialized();

  await SPUtil.getInstance();

  await getTheme();

  runApp(MyApp());

  if (Platform.isAndroid) {
    // 以下两行 设置android状态栏为透明的沉浸。写在组件渲染之后，
    // 是为了在渲染后进行set赋值，覆盖状态栏，写在渲染之前MaterialApp组件会覆盖掉这个值。
    SystemUiOverlayStyle systemUiOverlayStyle =
    SystemUiOverlayStyle(statusBarColor: Colors.transparent);
    SystemChrome.setSystemUIOverlayStyle(systemUiOverlayStyle);
  }
}

Future<Null> getTheme() async {
// 是否是夜间模式
  bool dark = SPUtil.getBool(Constants.DARK_KEY, defValue: false);
  ThemeUtils.dark = dark;

  // 如果不是夜间模式，设置的其他主题颜色才起作用
  if (!dark) {
    String themeColorKey = SPUtil.getString(
        Constants.THEME_COLOR_KEY, defValue: 'redAccent');
    if (themeColorMap.containsKey(themeColorKey)) {
      ThemeUtils.currentThemeColor = themeColorMap[themeColorKey];
    }
  }
}

class MyApp extends StatefulWidget {
  // This widget is the root of your application.
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _MyAppState();
  }
}

class _MyAppState extends State<MyApp> {

  /// 主题模式
  ThemeData themeData;

  void _initAsync() async {
    await User().getUserInfo();
    await DioManager.init();
  }


  @override
  void initState() {
    super.initState();
    _initAsync();
    themeData = ThemeUtils.getThemeData();
    Application.eventBus = EventBus();
    registerThemeEvent();
  }

  /// 注册主题改变事件
  void registerThemeEvent() {
    Application.eventBus
        .on<ThemeChangeEvent>()
        .listen((ThemeChangeEvent onData) => this.changeTheme(onData));
  }

  void changeTheme(ThemeChangeEvent onData) {
    setState(() {
      themeData = ThemeUtils.getThemeData();
    });
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return MaterialApp(
      title: 'Flutter Demo',
      theme: themeData,
      home: SplashScreen(),
    );
  }

}
