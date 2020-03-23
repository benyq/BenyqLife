import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_webview_plugin/flutter_webview_plugin.dart';
import 'package:wanandroid/utils/routeUtil.dart';

class WebViewScreen extends StatefulWidget {
  /// 标题
  String title;

  /// 链接
  String url;

  WebViewScreen({Key key, @required this.title, @required this.url})
      : super(key: key);


  @override
  State<StatefulWidget> createState() {
    return _WebViewScreenState();
  }
}

class _WebViewScreenState extends State<WebViewScreen> {

  bool isLoad = true;

  final flutterWebViewPlugin = new FlutterWebviewPlugin();

  @override
  void initState() {
    super.initState();
    flutterWebViewPlugin.onStateChanged.listen((state) {
      if (state.type == WebViewState.finishLoad) {
        setState(() {
          isLoad = false;
        });
      } else if (state.type == WebViewState.startLoad) {
        setState(() {
          isLoad = true;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    ///套WillPopScope的作用是消除点击返回的时候，Appbar先返回，然后界面才返回的问题
    return WillPopScope(
      child:   WebviewScaffold(
        url: widget.url,
        appBar: AppBar(
          elevation: 0.4,
          title: Text(widget.title),
          bottom: new PreferredSize(
            child: SizedBox(
              height: 2,
              child: isLoad ? new LinearProgressIndicator() : Container(),
            ),
            preferredSize: Size.fromHeight(2),
          ),
          actions: <Widget>[
            IconButton(
              icon: Icon(Icons.language, size: 25.0,),
              onPressed: () => RouteUtil.launchInBrowser(widget.url),
            ),
            IconButton(
              icon: Icon(Icons.share, size: 25.0,),
              onPressed: () => RouteUtil.launchInBrowser(widget.url),
            )
          ],
        ),
        withZoom: false,
        withLocalStorage: true,
        withJavascript: true,
        hidden: true,
      ),
      onWillPop: () {
        flutterWebViewPlugin.close();
        return Future.value(true);
      },
    );
  }





}
