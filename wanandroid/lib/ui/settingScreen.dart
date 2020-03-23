import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/common/application.dart';
import 'package:wanandroid/data/event/themeChangeEvent.dart';
import 'package:wanandroid/res/colors.dart';
import 'package:wanandroid/utils/spUtil.dart';
import 'package:wanandroid/utils/themeUtil.dart';

class SettingScreen extends StatefulWidget{
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _SettingScreenState();
  }

}

class _SettingScreenState extends State<SettingScreen> {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Scaffold(
      appBar: AppBar(
        title: Text('系统设置'),
      ),
      body: ListView(
        children: <Widget>[
          ExpansionTile(
              title: new Row(
                children: <Widget>[
                  Icon(Icons.color_lens, color: Theme.of(context).primaryColor),
                  Padding(
                    padding: EdgeInsets.only(left: 10.0),
                    child: Text('主题'),
                  )
                ],
              ),
            children: <Widget>[
              Wrap(
                children: themeColorMap.keys.map((String key) {
                  Color value = themeColorMap[key];
                  return InkWell(
                    onTap: () {
                      SPUtil.putString(Constants.THEME_COLOR_KEY, key);
                      ThemeUtils.currentThemeColor = value;
                      Application.eventBus.fire(ThemeChangeEvent());
                    },
                    child:Container(
                      margin: EdgeInsets.all(5.0),
                      width: 36.0,
                      height: 36.0,
                      color: value,
                    )
                    ,
                  );
                }).toList(),
              )
            ],
          ),

        ],
      ),
    );
  }

}