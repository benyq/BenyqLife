import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/data/projectArticleModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/utils/routeUtil.dart';
import 'package:wanandroid/utils/toastUtil.dart';
import 'package:wanandroid/widgets/customCachedImage.dart';
import 'package:wanandroid/widgets/likeButtonWidget.dart';
import 'package:wanandroid/widgets/refreshFooter.dart';

class ProjectArticleScreen extends StatefulWidget {
  final int id;

  ProjectArticleScreen(this.id);

  @override
  State<StatefulWidget> createState() {
    return _ProjectArticleScreenState();
  }
}

class _ProjectArticleScreenState extends State<ProjectArticleScreen> with AutomaticKeepAliveClientMixin{
  int _page = 1;
  ScrollController _scrollController = ScrollController();
  RefreshController _refreshController = RefreshController();

  /// 是否显示悬浮按钮
  bool _isShowFAB = false;

  List<ProjectArticleBean> _projectArticleList = List();

  @override
  void initState() {
    super.initState();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    getProjectArticles();
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Scaffold(
      body: SmartRefresher(
        enablePullDown: true,
        enablePullUp: true,
        header: WaterDropHeader(),
        footer: RefreshFooter(),
        controller: _refreshController,
        onLoading: getMoreProjectArticles,
        onRefresh: getProjectArticles,
        child: ListView.builder(
            controller: _scrollController,
            itemCount: _projectArticleList.length,
            itemBuilder: (BuildContext context, int index) {
              ProjectArticleBean bean = _projectArticleList[index];
              return ProjectArticleView(item: bean,);
            }),
      ),
      floatingActionButton: !_isShowFAB
          ? null
          : FloatingActionButton(
              onPressed: () {
                _scrollController.animateTo(0,
                    duration: Duration(milliseconds: 2000), curve: Curves.ease);
              },
              heroTag: "home",
              child: Icon(Icons.arrow_upward),
            ),
    );
  }

  Future getProjectArticles() async {
    _page = 1;
    apiService.getProjectArticleList((ProjectArticleListModel model) {
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          _refreshController.refreshCompleted(resetFooterState: true);
          setState(() {
            _projectArticleList.clear();
            _projectArticleList.addAll(model.data.datas);
          });
        }else {
          ToastUtil.show(model.errorMsg);
        }
      }
    }, (DioError error) {

    }, widget.id, _page);
  }

  Future getMoreProjectArticles() async {
    _page++;
    apiService.getProjectArticleList((ProjectArticleListModel model) {
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          _refreshController.loadComplete();
          setState(() {
            _projectArticleList.addAll(model.data.datas);
          });
        }else {
          ToastUtil.show(model.errorMsg);
          _refreshController.loadNoData();
        }
      }else {
        _refreshController.loadFailed();
      }
    }, (DioError error) {
      _refreshController.loadFailed();
    }, widget.id, _page);
  }

  @override
  bool get wantKeepAlive => true;
}


class ProjectArticleView extends StatefulWidget {

  ProjectArticleBean item;

  ProjectArticleView({this.item});

  @override
  State<StatefulWidget> createState() {
    return _ProjectArticleViewState();
  }

}

class _ProjectArticleViewState extends State<ProjectArticleView> {

  @override
  Widget build(BuildContext context) {
    var bean = widget.item;
    return InkWell(
      onTap: () => RouteUtil.toWebView(context, bean.title, bean.link),
      child: Column(
        children: <Widget>[
          Row(
            children: <Widget>[
              Container(
                padding: EdgeInsets.fromLTRB(16, 8, 8, 8),
                child: Container(
                  width: 80,
                  height: 130,
                  child: CustomCachedImage(imageUrl: bean.envelopePic,),
                ),
              ),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: <Widget>[
                    Container(
                      alignment: Alignment.topLeft,
                      padding: EdgeInsets.fromLTRB(0, 8, 8, 8),
                      child: Text(bean.title, style: TextStyle(fontSize: 16),maxLines: 2,textAlign: TextAlign.start,),
                    ),
                    Container(
                      alignment: Alignment.topLeft,
                      padding: EdgeInsets.fromLTRB(0, 0, 8, 8),
                      child: Text(
                        bean.desc,
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey[600],
                        ),
                        maxLines: 2,
                        textAlign: TextAlign.left,
                        softWrap: true,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                    Container(
                      alignment: Alignment.topLeft,
                      padding: EdgeInsets.fromLTRB(0, 0, 8, 8),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: <Widget>[
                            Text(
                              bean.author.isNotEmpty
                                  ? bean.author
                                  : bean.shareUser,
                              style: TextStyle(
                                  fontSize: 12, color: Colors.grey[600]),
                              textAlign: TextAlign.left,
                            ),
                          Text(
                            bean.niceDate,
                            style:
                            TextStyle(color: Colors.grey[600], fontSize: 12),
                          )
                        ],
                      ),
                    ),
                    Container(
                      alignment: Alignment.topRight,
                      padding: EdgeInsets.fromLTRB(0, 0, 8, 8),
                      child:  LikeButtonWidget(isLike: bean.collect,),
                    )
                  ],
                ),
              ),
            ],
          ),
          Divider(height: 1,)
        ],
      ),
    );
  }

}