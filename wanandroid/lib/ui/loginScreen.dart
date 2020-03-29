import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wanandroid/common/application.dart';
import 'package:wanandroid/common/user.dart';
import 'package:wanandroid/data/event/loginEvent.dart';
import 'package:wanandroid/data/userModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/registerScreen.dart';
import 'package:wanandroid/utils/toastUtil.dart';
import 'package:wanandroid/widgets/loadingDialog.dart';

class LoginScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _LoginScreenState();
  }
}

class _LoginScreenState extends State<LoginScreen> {
  TextEditingController _userNameController = TextEditingController();
  TextEditingController _psdController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return GestureDetector(
      behavior: HitTestBehavior.translucent,
      onTap: () {
        // 触摸收起键盘
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Scaffold(
        resizeToAvoidBottomInset: false,
        appBar: AppBar(
          title: Text('登录'),
        ),
        body: Container(
          padding: EdgeInsets.only(top: 24),
          alignment: Alignment.center,
          child: Padding(
            padding: EdgeInsets.all(16),
            child: Column(
              children: <Widget>[
                Container(
                  padding: EdgeInsets.only(bottom: 10),
                  alignment: Alignment.centerLeft,
                  child: Text(
                    "用户登录",
                    style: TextStyle(fontSize: 20, color: Colors.black),
                  ),
                ),
                Container(
                  alignment: Alignment.centerLeft,
                  child: Text(
                    "请使用WanAndroid账号登录",
                    style: TextStyle(fontSize: 14, color: Colors.grey),
                  ),
                ),
                TextField(
                  controller: _userNameController,
                  decoration: InputDecoration(
                    labelText: "用户名",
                    hintText: "请输入用户名",
                    labelStyle: TextStyle(color: Colors.cyan),
                  ),
                  maxLines: 1,
                ),
                TextField(
                  controller: _psdController,
                  decoration: InputDecoration(
                    labelText: "密码",
                    hintText: "请输入密码",
                    labelStyle: TextStyle(color: Colors.cyan),
                  ),
                  maxLines: 1,
                  obscureText: true,
                ),
                SizedBox(
                  height: 28,
                ),
                Row(
                  children: <Widget>[
                    Expanded(
                        child: RaisedButton(
                      padding: EdgeInsets.all(16.0),
                      elevation: 0.5,
                      color: Theme.of(context).primaryColor,
                      textColor: Colors.white,
                      child: Text("登录"),
                      onPressed: () {
                        String username = _userNameController.text;
                        String password = _psdController.text;
                        print("username $username password $password");
                        _login(username, password);
                      },
                    )),
                  ],
                ),
                Container(
                    padding: EdgeInsets.only(top: 10),
                    alignment: Alignment.centerRight,
                    child: FlatButton(
                      child:
                          Text("还没有账号，注册一个？", style: TextStyle(fontSize: 14)),
                      onPressed: () {
                        registerClick();
                      },
                    )),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void registerClick() async {
    await Navigator.of(context)
        .push(CupertinoPageRoute(builder: (context) => RegisterScreen()))
        .then((value) {
      var map = jsonDecode(value);
      var username = map['username'];
      var password = map['password'];
      _userNameController.text = username;
      _psdController.text = password;
      _login(username, password);
    });
  }

  Future _login(String username, String password) async {
    if (null != username &&
        username.length != 0 &&
        null != password &&
        password.length != 0) {
      _showLoading(context);
      apiService.login((UserModel model, Response response) {
        User().saveUserInfo(model, response);
        Application.eventBus.fire(LoginEvent());
        _dismissLoading(context);
        print("model ${model.toJson()}");
        ToastUtil.show("登录成功");
        Navigator.of(context).pop();
      }, (DioError error) {
        _dismissLoading(context);
        print(error.response);
      }, username, password);
    } else {
      ToastUtil.show('用户名或密码不能为空');
    }
  }

  /// 显示Loading
  _showLoading(BuildContext context) {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (_) {
          return new LoadingDialog(
            outsideDismiss: false,
            loadingText: "正在登陆...",
          );
        });
  }

  /// 隐藏Loading
  _dismissLoading(BuildContext context) {
    Navigator.of(context).pop();
  }
}
