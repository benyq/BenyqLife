import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/common/application.dart';
import 'package:wanandroid/data/event/themeChangeEvent.dart';
import 'package:wanandroid/res/style.dart';
import 'package:wanandroid/ui/settingScreen.dart';
import 'package:wanandroid/utils/routeUtil.dart';
import 'package:wanandroid/utils/spUtil.dart';
import 'package:wanandroid/utils/themeUtil.dart';
import 'package:wanandroid/utils/utils.dart';

import 'loginScreen.dart';

class DrawerScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _DrawerScreen();
  }
}

class _DrawerScreen extends State<DrawerScreen> {
  String username = "去登录";

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: <Widget>[
          Container(
            padding: EdgeInsets.fromLTRB(16, 40, 16, 10),
            color: Theme.of(context).primaryColor,
            child: Column(
              children: <Widget>[
                Container(
                  alignment: Alignment.centerRight,
                  child: InkWell(
                    child: Image.asset(
                      Utils.getImgPath('ic_rank'),
                      color: Colors.white,
                      width: 20,
                      height: 20,
                    ),
                    onTap: () => print('object'),
                  ),
                ),
                CircleAvatar(
                  backgroundImage:
                      AssetImage(Utils.getImgPath('ic_default_avatar')),
                  radius: 40.0,
                ),
                Gaps.vGap10,
                InkWell(
                  child: Text(
                    username,
                    style: TextStyle(fontSize: 20, color: Colors.white),
                  ),
                  onTap: () => RouteUtil.push(context, LoginScreen()),
                ),
              ],
            ),
          ),
          ListTile(
            title: Text(
              '我的收藏',
              style: TextStyle(fontSize: 16),
            ),
            leading: Image.asset(
              Utils.getImgPath('ic_score'),
              width: 24,
              height: 24,
              color: Theme.of(context).primaryColor,
            ),
            trailing: Text('250', style: TextStyle(color: Colors.grey[500])),
            onTap: () {},
          ),
          ListTile(
            title: Text(
              'TODO',
              style: TextStyle(fontSize: 16),
            ),
            leading: Image.asset(
              Utils.getImgPath('ic_todo'),
              width: 24,
              height: 24,
              color: Theme.of(context).primaryColor,
            ),
            onTap: () {},
          ),
          ListTile(
            title: Text(
              "夜间模式",
              textAlign: TextAlign.left,
              style: TextStyle(fontSize: 16),
            ),
            leading: Icon(Icons.brightness_2,
                size: 24, color: Theme.of(context).primaryColor),
            onTap: () {
              ThemeUtils.dark = !ThemeUtils.dark;
              SPUtil.putBool(Constants.DARK_KEY, ThemeUtils.dark);
              Application.eventBus.fire(new ThemeChangeEvent());
            },
          ),
          ListTile(
            title: Text(
              "系统设置",
              textAlign: TextAlign.left,
              style: TextStyle(fontSize: 16),
            ),
            leading: Icon(Icons.settings,
                size: 24, color: Theme.of(context).primaryColor),
            onTap: () => RouteUtil.push(context, SettingScreen()),
          ),
        ],
      ),
    );
  }
}
