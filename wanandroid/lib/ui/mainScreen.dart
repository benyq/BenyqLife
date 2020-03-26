import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wanandroid/ui/projectScreen.dart';
import 'package:wanandroid/ui/squareScreen.dart';
import 'package:wanandroid/ui/systemScreen.dart';
import 'package:wanandroid/ui/weChatScreen.dart';
import 'package:wanandroid/utils/utils.dart';
import 'drawerScreen.dart';
import 'homeScreen.dart';

class MainScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _MainScreenState();
  }
}

class _MainScreenState extends State<MainScreen> {
  /// 当前选中的索引
  int _selectedIndex = 0; // 当前选中的索引

  /// tabs的名字
  final bottomBarTitles = ["首页", "广场", "公众号", "体系", "项目"];

  PageController _pageController = PageController();

  /// 五个Tabs的内容
  var pages = <Widget>[
    HomeScreen(),
    SquareScreen(),
    WeChatScreen(),
    SystemScreen(),
    ProjectScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return WillPopScope(
      onWillPop: _onWillPop,
      child: Scaffold(
          appBar: AppBar(
            title: Text(bottomBarTitles[_selectedIndex]),
            elevation: 0,
          ),
          drawer: DrawerScreen(),
          //这个不需要PageView， 在这里使用只是为了熟悉 Widget
          body: PageView.builder(
            itemBuilder: (context, index) => pages[index],
            itemCount: pages.length,
            controller: _pageController,
            physics: NeverScrollableScrollPhysics(), //禁止滑动
            onPageChanged: (index) {
              setState(() {
                _selectedIndex = index;
              });
            },
          ),
          bottomNavigationBar: BottomNavigationBar(
            items: <BottomNavigationBarItem>[
              BottomNavigationBarItem(
                icon: buildImage(0, "ic_home"),
                title: Text(bottomBarTitles[0]),
              ),
              BottomNavigationBarItem(
                icon: buildImage(1, "ic_square_line"),
                title: Text(bottomBarTitles[1]),
              ),
              BottomNavigationBarItem(
                icon: buildImage(2, "ic_wechat"),
                title: Text(bottomBarTitles[2]),
              ),
              BottomNavigationBarItem(
                icon: buildImage(3, "ic_system"),
                title: Text(bottomBarTitles[3]),
              ),
              BottomNavigationBarItem(
                icon: buildImage(4, "ic_project"),
                title: Text(bottomBarTitles[4]),
              )
            ],
            type: BottomNavigationBarType.fixed, // 设置显示模式,
            currentIndex: _selectedIndex, // 当前选中项的索引
            onTap: _onItemTapped, // 选择的处理事件 选中变化回调函数
          )),
    );
  }

  /// tabs 底总的图片
  Widget buildImage(index, iconPath) {
    return Image.asset(
      Utils.getImgPath(iconPath),
      width: 22,
      height: 22,
      color: _selectedIndex == index
          ? Theme.of(context).primaryColor
          : Colors.grey[600],
    );
  }

  void _onItemTapped(int index) {
    _pageController.jumpToPage(index);
  }

  Future<bool> _onWillPop() {
    return showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: Text("提示"),
            content: Text("确定退出应用吗？"),
            actions: <Widget>[
              FlatButton(
                onPressed: () => Navigator.of(context).pop(false),
                child: Text(
                  '再看一会',
                  style: TextStyle(color: Colors.cyan),
                ),
              ),
              FlatButton(
                onPressed: () => Navigator.of(context).pop(true),
                child: Text(
                  '退出',
                  style: TextStyle(color: Colors.cyan),
                ),
              )
            ],
          ),
        ) ??
        false;
  }
}
