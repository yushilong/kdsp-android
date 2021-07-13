package com.qizhu.rili.controller;


import com.qizhu.rili.bean.AuguryCart;

import org.json.JSONObject;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by lindow on 9/1/16.
 * Api请求接口
 * 默认全部以post的方式请求，以field的方式传参，包含field则必须添加@FormUrlEncoded注解，否则报错
 * 不传参则必须不包含@FormUrlEncoded注解，否则也报错
 * 传文件以Multipart的形式传，此时所有参数以part形式传递
 */
public interface KDSPApiService {
    @FormUrlEncoded
    @POST("app/test/findTestList")
    Call<JSONObject> findTestList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/application/findApplication")
    Call<JSONObject> findApplication(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/div/addPalmMsg")
    Call<JSONObject> addPalmMsg(@Field("type") int type, @Field("startPoint") String startPoint, @Field("endPoint") String endPoint, @Field("screenSize") String screenSize);

    @POST("app/div/v2/flushFont")
    Call<JSONObject> flushFont();

    @FormUrlEncoded
    @POST("app/div/v2/addFontMsg")
    Call<JSONObject> addFontMsg(@Field("word") String word);

    @FormUrlEncoded
    @POST("app/div/findPayDivinationList")
    Call<JSONObject> findPayDivinationList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/div/changeFontStatus")
    Call<JSONObject> changeFontStatus(@Field("dtId") String dtId);

    @FormUrlEncoded
    @POST("app/palmBranch/findPalmBranchList")
    Call<JSONObject> findPalmBranchList(@Field("type") int type, @Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/wxpay/unifiedorder")
    Call<JSONObject> unifiedorder(@Field("fee") int fee, @Field("dtId") String dtId);

    @FormUrlEncoded
    @POST("app/alipay/sign")
    Call<JSONObject> sign(@Field("fee") int fee, @Field("dtId") String dtId);

    @POST("app/luckymonth/findLuckymonth")
    Call<JSONObject> findLuckymonth();

    @POST("app/luckyday/findLuckydayAlias")
    Call<JSONObject> findLuckydayAlias();

    @POST("app/sysConfig/initSysConfig")
    Call<JSONObject> initSysConfig();

    @FormUrlEncoded
    @POST("app/test/findMyTestList")
    Call<JSONObject> findMyTestList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/test/findTestListByTcId")
    Call<JSONObject> findTestListByTcId(@Field("categoryId") String categoryId, @Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/test/twoPeopleRelation")
    Call<JSONObject> twoPeopleRelation(@Field("myBirthday") String myBirthday, @Field("yourBirthday") String yourBirthday, @Field("index") int index);

    @POST("app/softUpdate/findSoftUpdate")
    Call<JSONObject> findSoftUpdate();

    @POST("app/shaking/flushShaking")
    Call<JSONObject> flushShaking();

    @FormUrlEncoded
    @POST("app/shaking/askShaking")
    Call<JSONObject> askShaking(@Field("type") int type, @Field("signCount") int signCount);

    @FormUrlEncoded
    @POST("app/shaking/findShaking")
    Call<JSONObject> findShaking(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/shaking/isLike")
    Call<JSONObject> isLike(@Field("shakId") String shakId, @Field("isLike") int isLike);

    @FormUrlEncoded
    @POST("app/item/findItemList")
    Call<JSONObject> findItemList(@Field("catId") String catId, @Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/item/searchItem")
    Call<JSONObject> searchItem(@Field("catId") String catId, @Field("itemName") String itemName, @Field("flag") int flag, @Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/preItem/addPreItem")
    Call<JSONObject> addPreItem(@Field("itemName") String itemName);

    @FormUrlEncoded
    @POST("app/order/findPreReplyOrderList")
    Call<JSONObject> findPreReplyOrderList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/order/findRepliedOrderList")
    Call<JSONObject> findRepliedOrderList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/order/updateItemStatusByOrderId")
    Call<JSONObject> updateItemStatusByOrderId(@Field("ioId") String ioId);

    @POST("app/order/getItemMsgCount")
    Call<JSONObject> getItemMsgCount();

    @FormUrlEncoded
    @POST("app/feedback/findFeedback")
    Call<JSONObject> findFeedback(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/feedback/addFeedback")
    Call<JSONObject> addFeedback(@Field("content") String content, @Field("type") int type);

    @FormUrlEncoded
    @POST("app/feedback/addFeedback")
    Call<JSONObject> addFeedback(@Field("content") String content, @Field("type") int type, @Field("width") int width, @Field("height") int height);

    @Multipart
    @POST("app/feedback/addFeedback")
    Call<JSONObject> addFeedback(@Part MultipartBody.Part content, @Part("type") RequestBody type, @Part("width") RequestBody width, @Part("height") RequestBody height);

    @FormUrlEncoded
    @POST("app/user/register")
    Call<JSONObject> register(@Field("codeVal") String code, @Field("telephoneNumber") String telephoneNumber, @Field("password") String password);

    @FormUrlEncoded
    @POST("app/user/createCode")
    Call<JSONObject> createCode(@Field("telephoneNumber") String telephoneNumber, @Field("registeWay") String registeWay);

    @FormUrlEncoded
    @POST("app/user/editPasswordCode")
    Call<JSONObject> editPasswordCode(@Field("telephoneNumber") String telephoneNumber);

    @FormUrlEncoded
    @POST("app/user/userLogin")
    Call<JSONObject> userLogin(@Field("telephoneNumber") String telephoneNumber, @Field("password") String password, @Field("registeWay") String registeWay);

    @FormUrlEncoded
    @POST("app/user/wxLogin")
    Call<JSONObject> wxLogin(@Field("code") String code);

    @FormUrlEncoded
    @POST("app/user/qqLogin")
    Call<JSONObject> qqLogin(@Field("access_token") String token, @Field("openid") String openId);

    @FormUrlEncoded
    @POST("app/user/webLogin")
    Call<JSONObject> webLogin(@Field("access_token") String token, @Field("uid") String uid);

    @Multipart
    @POST("app/user/editUserInfo")
    Call<JSONObject> editUserInfo(@Part("nickName") RequestBody nickName, @Part("birthTimes") RequestBody birthTimes, @Part("userSex") RequestBody userSex, @Part("description") RequestBody description, @Part MultipartBody.Part image, @Part("blood") RequestBody blood);

    @FormUrlEncoded
    @POST("app/user/editUserInfo")
    Call<JSONObject> editUserInfo(@Field("nickName") String nickName, @Field("birthTimes") String birthTimes, @Field("userSex") int userSex, @Field("description") String description, @Field("imageUrl") String image, @Field("blood") String blood,@Field("isLunar") int isLunar);

    @FormUrlEncoded
    @POST("app/user/editPassword")
    Call<JSONObject> editPassword(@Field("codeVal") String code, @Field("telephoneNumber") String telephoneNumber, @Field("password") String password);

    @FormUrlEncoded
    @POST("app/user/anonymityLogin")
    Call<JSONObject> anonymityLogin(@Field("openUDID") String openUDID, @Field("registeWay") String registeWay);

    @POST("app/user/getUserInfoByUserId")
    Call<JSONObject> getUserInfoByUserId();

    @Multipart
    @POST("http://upload.qiniu.com")
    Call<JSONObject> uploadImageToQiNiu(@Part("key") RequestBody key, @Part("token") RequestBody token, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("http://api.ishenpo.com:8080/DATA/app/statistics/addStatistics")
    Call<JSONObject> addStatistics(@Field("source") int source, @Field("type") int type, @Field("subType") String subType);

    @FormUrlEncoded
    @POST("http://test.ishenpo.com/DATA/app/statistics/addStatistics")
    Call<JSONObject> addTestStatistics(@Field("source") int source, @Field("type") int type, @Field("subType") String subType);

    @POST("app/goods/getCustomFortuneData")
    Call<JSONObject> getCustomFortuneData();

    @FormUrlEncoded
    @POST("app/item/getFeatureAndPointByItemId")
    Call<JSONObject> getFeatureAndPointByItemId(@Field("itemId") String itemId);

    @FormUrlEncoded
    @POST("app/virtual/item/getItemInfoByItemId")
    Call<JSONObject> getItemInfoByItemId(@Field("itemId") String itemId);

    @POST("app/item/getLookPalmData")
    Call<JSONObject> getLookPalmData();

    @POST("app/point/getPointByUserId")
    Call<JSONObject> getPointByUserId();

    @FormUrlEncoded
    @POST("app/order/kdsp/wxpay")
    Call<JSONObject> wxpay(@Field("itemId") String itemId, @Field("itemParam") String itemParam, @Field("pointSum") int pointSum);

    @FormUrlEncoded
    @POST("app/order/kdsp/alipay")
    Call<JSONObject> alipay(@Field("itemId") String itemId, @Field("itemParam") String itemParam, @Field("pointSum") int pointSum);

    @FormUrlEncoded
    @POST("app/order/kdsp/pointPay")
    Call<JSONObject> pointPay(@Field("itemId") String itemId, @Field("itemParam") String itemParam);

    @FormUrlEncoded
    @POST("app/virtual/order/wechatPay")
    Call<JSONObject> wechatPay(@Field("itemId") String itemId, @Field("itemParam") String itemParam, @Field("pointSum") int pointSum, @Field("couponId") String couponId);

    @FormUrlEncoded
    @POST("app/virtual/order/aliPay")
    Call<JSONObject> aliPay(@Field("itemId") String itemId, @Field("itemParam") String itemParam, @Field("pointSum") int pointSum, @Field("couponId") String couponId);

    @FormUrlEncoded
    @POST("app/virtual/order/membershipPay")
    Call<JSONObject> membershipPay(@Field("itemId") String itemId, @Field("itemParam") String itemParam , @Field("couponId") String couponId,@Field("pointSum") int pointSum);

    @FormUrlEncoded
    @POST("app/order/getItemAnswerByOrderId")
    Call<JSONObject> getItemAnswerByOrderId(@Field("ioId") String ioId);

    @FormUrlEncoded
    @POST("app/item/findItems")
    Call<JSONObject> findItems(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/point/activity/oper")
    Call<JSONObject> oper(@Field("bigCat") String bigCat, @Field("smallCat") String smallCat, @Field("keyCat") String keyCat);

    @FormUrlEncoded
    @POST("app/order/cancelOrder")
    Call<JSONObject> cancelOrder(@Field("ioId") String ioId);

    @FormUrlEncoded
    @POST("app/order/question/delOrder")
    Call<JSONObject> delAuguryOrder(@Field("odId") String odId);

    @POST("app/membership/v2/getMembership")
    Call<JSONObject> getMembership();

    @POST("app/membership/getMembershipCat")
    Call<JSONObject> getMembershipCat();

    @FormUrlEncoded
    @POST("app/membership/ali/payMember")
    Call<JSONObject> aliPayMember(@Field("mscId") String mscId, @Field("price") int price);

    @FormUrlEncoded
    @POST("app/membership/wx/payMember")
    Call<JSONObject> wxPayMember(@Field("mscId") String mscId, @Field("price") int price);

    @POST("app/membership/getMembershipMsg")
    Call<JSONObject> getMembershipMsg();

    @FormUrlEncoded
    @POST("app/membershipGift/exchangeMembershipGift")
    Call<JSONObject> exchangeMembershipGift(@Field("msgId") String msgId, @Field("itemId") String itemId, @Field("itemParam") String itemParam);

    @FormUrlEncoded
    @POST("app/order/kdsp/membership")
    Call<JSONObject> membership(@Field("itemId") String itemId, @Field("itemParam") String itemParam, @Field("pointSum") int pointSum);

    @FormUrlEncoded
    @POST("app/blood/getBloodData")
    Call<JSONObject> getBloodData(@Field("myBlood") String myBlood, @Field("dadBlood") String dadBlood, @Field("momBlood") String momBlood);

    @FormUrlEncoded
    @POST("app/goods/getGoodsList")
    Call<JSONObject> getGoodsList(@Field("page") int page, @Field("rows") int rows, @Field("classifyId") String classifyId);

    @FormUrlEncoded
    @POST("app/goods/getGoodsDetailsById")
    Call<JSONObject> getGoodsDetailsById(@Field("goodsId") String goodsId);

    @FormUrlEncoded
    @POST("app/shipping/addShippingAddr")
    Call<JSONObject> addShippingAddr(@Field("receiverMobile") String receiverMobile, @Field("receiverName") String receiverName, @Field("receiverAddress") String receiverAddress,
                                     @Field("receiverZip") String receiverZip, @Field("province") String province, @Field("city") String city, @Field("area") String area);

    @POST("app/shipping/findShippingAddrList")
    Call<JSONObject> findShippingAddrList();

    @FormUrlEncoded
    @POST("app/shipping/deleteShippingAddr")
    Call<JSONObject> deleteShippingAddr(@Field("shipId") String shipId);

    @FormUrlEncoded
    @POST("app/shipping/changeShippingAddrIsDefault")
    Call<JSONObject> changeShippingAddrIsDefault(@Field("shipId") String shipId);

    @FormUrlEncoded
    @POST("app/cart/addCart")
    Call<JSONObject> addCart(@Field("skuId") String skuId, @Field("count") int count);

    @FormUrlEncoded
    @POST("app/cart/deleteCartById")
    Call<JSONObject> deleteCartById(@Field("cartId") String cartId);

    @POST("app/cart/clearInvalidCart")
    Call<JSONObject> clearInvalidCart();

    @POST("app/cart/findCartsByUserId")
    Call<JSONObject> findCartsByUserId();

    @FormUrlEncoded
    @POST("app/physical/order/cartGoodsSubmitOrder")
    Call<JSONObject> submitOrderByCart(@Field("cartIds") String cartIds, @Field("shipId") String shipId, @Field("couponId") String couponId,@Field("remark") String remark);

    @POST("app/cat/findQuestionCatList")
    Call<JSONObject> findQuestionCatList();

    @FormUrlEncoded
    @POST("app/item/findQuestionThemeList")
    Call<JSONObject> findQuestionThemeList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/goods/getGoodsThemeList")
    Call<JSONObject> getGoodsThemeList(@Field("page") int page, @Field("rows") int rows, @Field("birthday") String birthday);

    @FormUrlEncoded
    @POST("app/goods/getCharmGoodsThemeList")
    Call<JSONObject> getCharmGoodsThemeList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/item/findMasterCalculationList")
    Call<JSONObject> findMasterCalculationList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/item/findMasterCalculationThemeList")
    Call<JSONObject> findMasterCalculationThemeList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/physical/order/singleGoodsSubmitOrder")
    Call<JSONObject> submitOrder(@Field("skuId") String skuId, @Field("count") int count, @Field("shipId") String shipId,@Field("couponId") String couponId,@Field("remark") String remark);

    @FormUrlEncoded
    @POST("app/physical/order/singleGoodsConfirmSubmitOrder")
    Call<JSONObject> confirmSubmitOrder(@Field("skuId") String skuId, @Field("count") int count, @Field("shipId") String shipId);

    @FormUrlEncoded
    @POST("app/physical/order/cartGoodsConfirmSubmitOrder")
    Call<JSONObject> confirmSubmitOrderByCart(@Field("cartIds") String cartIds, @Field("shipId") String shipId);

    @FormUrlEncoded
    @POST("app/order/toPay")
    Call<JSONObject> toPay(@Field("orderIds") String orderIds, @Field("payway") int payway);

    @FormUrlEncoded
    @POST("app/order/findOrderByUserIdAndStatus")
    Call<JSONObject> findOrderByUserIdAndStatus(@Field("page") int page, @Field("rows") int rows, @Field("status") int status);

    @FormUrlEncoded
    @POST("app/user/sendBindTelCode")
    Call<JSONObject> sendBindTelCode(@Field("telephoneNumber") String telephoneNumber);

    @FormUrlEncoded
    @POST("app/user/bindTelAndMergeUserMsg")
    Call<JSONObject> bindTelAndMergeUserMsg(@Field("codeVal") String code, @Field("telephoneNumber") String telephoneNumber, @Field("password") String password);

    @FormUrlEncoded
    @POST("app/user/updatePhoneNum")
    Call<JSONObject> updatePhoneNum(@Field("codeVal") String code, @Field("telephoneNumber") String telephoneNumber, @Field("password") String password);

    @FormUrlEncoded
    @POST("app/refund/toRefund")
    Call<JSONObject> toRefund(@Field("orderId") String orderId, @Field("odId") String odId);

    @FormUrlEncoded
    @POST("app/refund/submitRefundApply")
    Call<JSONObject> submitRefundApply(@Field("orderId") String orderId, @Field("odId") String odId, @Field("serverName") String serverName, @Field("reason") String reason, @Field("description") String description, @Field("images") String images);

    @FormUrlEncoded
    @POST("app/refund/cancelRefundApply")
    Call<JSONObject> cancelRefundApply(@Field("refundId") String refundId);

    @FormUrlEncoded
    @POST("app/refund/againRefundApply")
    Call<JSONObject> againRefundApply(@Field("refundId") String refundId);

    @FormUrlEncoded
    @POST("app/refund/submitRefundShippingMsg")
    Call<JSONObject> submitRefundShippingMsg(@Field("refundId") String refundId, @Field("shipCode") String shipCode, @Field("expressCompany") String expressCompany);

    @POST("app/cart/findCartCountByUserId")
    Call<JSONObject> findCartCountByUserId();

    @FormUrlEncoded
    @POST("app/order/viewShipInfo")
    Call<JSONObject> viewShipInfo(@Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("app/order/findMyOrderByUserId")
    Call<JSONObject> findMyOrderByUserId(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/physical/order/cancelOrCloseOrder")
    Call<JSONObject> cancelOrCloseOrder(@Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("app/order/delOrder")
    Call<JSONObject> delOrder(@Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("app/order/confirmReceipt")
    Call<JSONObject> confirmReceipt(@Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("app/chatMsg/addChatMsg")
    Call<JSONObject> addChatMsg(@Field("msgType") int msgType, @Field("content") String content);

    @FormUrlEncoded
    @POST("app/chatMsg/getChatMsgList")
    Call<JSONObject> getChatMsgList(@Field("page") int page, @Field("rows") int rows);

    @POST("app/shipping/getShippingCount")
    Call<JSONObject> getShippingCount();

    @FormUrlEncoded
    @POST("app/answer/changeAnswerReaded")
    Call<JSONObject> changeAnswerReaded(@Field("iaId") String iaId);

    @FormUrlEncoded
    @POST("app/replayChatMsg/findChatMsgList")
    Call<JSONObject> findChatMsgList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/replayChatMsg/findChatMsgListByUserId")
    Call<JSONObject> findChatMsgListByUserId(@Field("page") int page, @Field("rows") int rows, @Field("sendUserId") String sendUserId);

    @FormUrlEncoded
    @POST("app/replayChatMsg/replyChatMsg")
    Call<JSONObject> replyChatMsg(@Field("msgType") int msgType, @Field("content") String content, @Field("sendUserId") String sendUserId);

    @POST("app/goods/getRecommendGoodsList")
    Call<JSONObject> getRecommendGoodsList();

    @POST("app/item/getRecommendQuestionList")
    Call<JSONObject> getRecommendQuestionList();

    @POST("app/fortune/getFortuneScore")
    Call<JSONObject> getFortuneScore();

    @FormUrlEncoded
    @POST("app/fortune/getFortuneAnswer")
    Call<JSONObject> getFortuneAnswer(@Field("ioId") String ioId);

    @FormUrlEncoded
    @POST("app/name/getNameTestResult")
    Call<JSONObject> getNameTestResult(@Field("familyName") String familyName, @Field("LastName") String LastName);

    @FormUrlEncoded
    @POST("app/name/getNameTestResults")
    Call<JSONObject> getNameTestResults(@Field("ioId") String ioId);

    @POST("app/goods/getClassifyList")
    Call<JSONObject> getClassifyList();

    @FormUrlEncoded
    @POST("app/main/getMainData")
    Call<JSONObject> getMainData(@Field("birthday") String birthday, @Field("sex") int sex);

    @FormUrlEncoded
    @POST("app/article/findArticleList")
    Call<JSONObject> findArticleList(@Field("articleType") int articleType ,@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/article/findArticleList")
    Call<JSONObject> findArticleList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/article/getArticleByIdAndUserId")
    Call<JSONObject> getArticleByIdAndUserId(@Field("articleId") String articleId);

    @FormUrlEncoded
    @POST("app/article/findArticleCommentList")
    Call<JSONObject> findArticleCommentList(@Field("articleId") String articleId ,@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/article/addArticleComment")
    Call<JSONObject> addArticleComment(@Field("articleId") String articleId, @Field("commentMsg") String commentMsg);

    @FormUrlEncoded
    @POST("app/article/operArticleCollect")
    Call<JSONObject> operArticleCollect(@Field("articleId") String articleId, @Field("isCollect") int isCollect);

    @FormUrlEncoded
    @POST("app/article/findMyCollectArticleList")
    Call<JSONObject> findMyCollectArticleList(@Field("page") int page, @Field("rows") int rows);

    @FormUrlEncoded
    @POST("app/coupon/findMyCouponListByPhoneNum")
    Call<JSONObject> findMyCouponListByPhoneNum(@Field("phoneNum") String phoneNum );

    @FormUrlEncoded
    @POST("app/life/getLifeNumData")
    Call<JSONObject> getLifeNumData(@Field("birthday") String birthday );

    @FormUrlEncoded
    @POST("app/life/getEightFontLifeData")
    Call<JSONObject> getEightFontLifeData(@Field("birthday") String birthday );


    @FormUrlEncoded
    @POST("app/coupon/claimCoupon")
    Call<JSONObject> claimCoupon(@Field("couponNum") String couponNum );


    @POST("app/coupon/findNoActivationCoupon ")
    Call<JSONObject> findNoActivationCoupon( );

    @FormUrlEncoded
    @POST("app/coupon/activationOrCancelCoupon")
    Call<JSONObject> activationOrCancelCoupon(@Field("mcId") String mcId ,@Field("isUsable") int isUsable);

    @FormUrlEncoded
    @POST("app/pay/common/alipay")
    Call<JSONObject> alipay(@Field("data") String data );

    @FormUrlEncoded
    @POST("app/pay/common/wxpay")
    Call<JSONObject> wxpay(@Field("data") String data );

    @POST("app/item/findSpeItem")
    Call<JSONObject> findSpeItem( );

    @POST("app/carousel/findCarousel")
    Call<JSONObject> findCarousel( );

    @FormUrlEncoded
    @POST("app/carousel/clickCarousel")
    Call<JSONObject> clickCarousel(@Field("carouselId") String carouselId  );

    @FormUrlEncoded
    @POST("app/one2one/findOne2oneItems")
    Call<JSONObject> findOne2oneItems(@Field("classify") String classify  );

    @FormUrlEncoded
    @POST("app/one2one/confirmSubmitOrder")
    Call<JSONObject> confirmSubmitOrder(@Field("itemJson") String itemJson );

    @FormUrlEncoded
    @POST("app/one2one/toPay")
    Call<JSONObject> toPay(@Field("itemJson") String itemJson ,@Field("pointSum") int  pointSum ,@Field("couponId") String couponId ,@Field("payWay") int payWay);

    @FormUrlEncoded
    @POST("app/one2one/findOne2OneOrders")
    Call<JSONObject> findOne2OneOrders(@Field("page") int page, @Field("rows") int rows );
    @FormUrlEncoded
    @POST("app/one2one/cancelOrCloseOrder")
    Call<JSONObject> auguryCancelOrCloseOrder(@Field("orderId") String page );

    @POST("app/one2one/findOne2oneItems")
    Observable<AuguryCart> getNewsComment(
            @Query("classify") String page);
}
