import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wanandroid/data/wxArticleModel.dart';
import 'package:wanandroid/utils/routeUtil.dart';
import 'package:wanandroid/widgets/likeButtonWidget.dart';

class ItemWeChatList extends StatefulWidget {

  WXArticleBean item;

  ItemWeChatList({Key key, this.item}): super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _ItemWeChatListState();
  }

}


class _ItemWeChatListState extends State<ItemWeChatList> {
  @override
  Widget build(BuildContext context) {
    var item = widget.item;
    return InkWell(
      onTap: () => RouteUtil.toWebView(context, item.title, item.link),
      child: Column(
        children: <Widget>[
          Container(
            padding: EdgeInsets.fromLTRB(16, 10, 16, 10),
            child: Row(
              children: <Widget>[
                Expanded(
                  child: Text(item.author, style: TextStyle(color: Colors.grey[600], fontSize: 12),),
                ),
                Text(item.niceDate, style: TextStyle(color: Colors.grey[600], fontSize: 12),)
              ],
            ),
          ),
          Container(
            padding: EdgeInsets.fromLTRB(16, 0, 16, 0),
            alignment: Alignment.topLeft,
            child: Text(item.title, style: TextStyle(fontSize: 16),),
          ),
          Container(
            alignment: Alignment.topLeft,
            padding: EdgeInsets.fromLTRB(16, 10, 16, 10),
            child: Row(
              children: <Widget>[
                Expanded(
                  child: Text(
                    item.superChapterName + " / " + item.chapterName,
                    style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                    textAlign: TextAlign.left,
                  ),
                ),
                LikeButtonWidget(
                  isLike: item.collect,
                  onClick: (){
                    addOrCancelCollect(item);
                  },
                )
              ],
            ),
          ),
          Divider(height: 1),
        ],
      ),
    );
  }

  void addOrCancelCollect(item) {

  }

}