import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/common/application.dart';
import 'package:wanandroid/common/user.dart';
import 'package:wanandroid/data/event/loginEvent.dart';
import 'package:wanandroid/data/event/themeChangeEvent.dart';
import 'package:wanandroid/data/userInfoModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/res/style.dart';
import 'package:wanandroid/ui/settingScreen.dart';
import 'package:wanandroid/utils/routeUtil.dart';
import 'package:wanandroid/utils/spUtil.dart';
import 'package:wanandroid/utils/themeUtil.dart';
import 'package:wanandroid/utils/utils.dart';

import 'collectScreen.dart';
import 'loginScreen.dart';

class DrawerScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _DrawerScreen();
  }
}

class _DrawerScreen extends State<DrawerScreen> with AutomaticKeepAliveClientMixin {

  bool isLogin = false;
  String username = "去登录";
  String level = "--"; // 等级
  String rank = "--"; // 排名
  String myScore = ''; // 我的积分

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    this.registerLoginEvent();
    if (null != User.singleton.userName && User.singleton.userName.isNotEmpty) {
      isLogin = true;
      username = User.singleton.userName;
      getUserInfo();
    }
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
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
                Gaps.vGap5,
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    Text('等级:',
                        style:
                        TextStyle(fontSize: 11, color: Colors.grey[100]),
                        textAlign: TextAlign.center),
                    Text(level,
                        style:
                        TextStyle(fontSize: 11, color: Colors.grey[100]),
                        textAlign: TextAlign.center),
                    Gaps.hGap5,
                    Text('排名:',
                        style:
                        TextStyle(fontSize: 11, color: Colors.grey[100]),
                        textAlign: TextAlign.center),
                    Text(rank,
                        style:
                        TextStyle(fontSize: 11, color: Colors.grey[100]),
                        textAlign: TextAlign.center),
                  ],
                )
              ],
            ),
          ),
          ListTile(
            title: Text(
              '我的积分',
              style: TextStyle(fontSize: 16),
            ),
            leading: Image.asset(
              Utils.getImgPath('ic_score'),
              width: 24,
              height: 24,
              color: Theme.of(context).primaryColor,
            ),
            trailing: Text(myScore, style: TextStyle(color: Colors.grey[500])),
            onTap: () {},
          ),
          ListTile(
            title: Text(
              '我的收藏',
              style: TextStyle(fontSize: 16),
            ),
            leading: Icon(Icons.favorite_border,
                size: 24, color: Theme.of(context).primaryColor),
            onTap: () {
              if (isLogin) {
                RouteUtil.push(context, CollectScreen());
              }
            },
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

  void registerLoginEvent() {
    Application.eventBus.on<LoginEvent>().listen((event){
      setState(() {
        isLogin = true;
        username = User.singleton.userName;
        getUserInfo();
      });
    });
  }

  @override
  // TODO: implement wantKeepAlive
  bool get wantKeepAlive => true;

  Future getUserInfo() async {
    apiService.getUserInfo((UserInfoModel model) {
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        setState(() {
          level = (model.data.coinCount ~/ 100 + 1).toString();
          rank = model.data.rank.toString();
          myScore = model.data.coinCount.toString();
        });
      }
    }, (DioError error) {});
  }
}
