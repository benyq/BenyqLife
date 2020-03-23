import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_swiper/flutter_swiper.dart';
import 'package:wanandroid/data/bannerModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/utils/routeUtil.dart';
import 'package:wanandroid/widgets/customCachedImage.dart';

class HomeBannerScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _HomeBannerScreenState();
  }
}

class _HomeBannerScreenState extends State<HomeBannerScreen> {
  List<BannerBean> _bannerList = new List();

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    _bannerList.add(null);
    _getBannerList();
  }

  Future _getBannerList() async {
    apiService.getBannerList((BannerModel model) {
      if (model.data.length > 0) {
        setState(() {
          _bannerList = model.data;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Offstage(
      offstage: _bannerList.length == 0,
      child: Swiper(
        itemBuilder: (BuildContext context, int index) {
          if (index >= _bannerList.length ||
              _bannerList[index] == null ||
              _bannerList[index].imagePath == null) {
            return Container(height: 0);
          } else {
            return InkWell(
              onTap: () {
                RouteUtil.toWebView(context, _bannerList[index].title, _bannerList[index].url);
              },
              child:
              CustomCachedImage(
                fit: BoxFit.fill,
                imageUrl: _bannerList[index].imagePath,)
            );
          }
        },
        itemCount: _bannerList.length,
        autoplay: true,
        pagination: SwiperPagination(),
      ),
    );
  }


}
