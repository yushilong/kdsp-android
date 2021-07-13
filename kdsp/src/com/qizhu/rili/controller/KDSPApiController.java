package com.qizhu.rili.controller;

import android.text.TextUtils;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.BuildConfig;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.OrderDetail;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.MD5Utils;
import com.qizhu.rili.utils.SPUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

import static com.qizhu.rili.YSRLConstants.SERVICE_POLICY_AGREE;

/**
 * Created by lindow on 9/1/16.
 * 利用retrofit进行接口请求
 */
public class KDSPApiController {
    //单例模式
    private static KDSPApiController mAppInstance = null;


    public static KDSPApiController getInstance() {
        if (mAppInstance == null) {
            mAppInstance = new KDSPApiController();
        }

        return mAppInstance;
    }

    //创建retrofit对象,设置通用拦截器并设置网络延迟时间为10s
    private  Retrofit retrofit = new Retrofit.Builder()
            .client(new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
//                    .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.LOG_DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                    .addInterceptor(new KDSPInterceptor())
                    .build())
            .baseUrl(AppConfig.API_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(KDSPJsonConverterFactory.create())
            .build();





    public  KDSPApiService apiService = retrofit.create(KDSPApiService.class);

    public KDSPApiService getApi() {
       return apiService;
    }

    /**
     * 生成上传图片的key
     */
    public String generateUploadKey(String img) {
        if (TextUtils.isEmpty(img)) {
            return null;
        }
        return "/" + DateUtils.getTodayYearTime() + "/" + MD5Utils.MD5(AppContext.userId + img) + ".jpg";
    }

    /**
     * 获取测试题
     */
    public void findTestList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findTestList(page, rows).enqueue(callBack);
    }

    /**
     * 获取推荐应用
     */
    public void findApplication(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findApplication(page, rows).enqueue(callBack);
    }

    /**
     * 发测手相
     *
     * @param type       手相类型2代表一线，3为二线，以此类推
     * @param startPoint 起始点坐标（格式：200,300）x,y形式
     * @param endPoint   结束点坐标（格式：200,300）x,y形式
     * @param screenSize 整个图片的宽高
     */
    public void addPalmMsg(int type, String startPoint, String endPoint, String screenSize, KDSPHttpCallBack callBack) {
        apiService.addPalmMsg(type, startPoint, endPoint, screenSize).enqueue(callBack);
    }

    /**
     * 刷新测字信息
     */
    public void flushFont(KDSPHttpCallBack callBack) {
        apiService.flushFont().enqueue(callBack);
    }

    /**
     * 测字
     *
     * @param word 要测的字
     */
    public void addFontMsg(String word, KDSPHttpCallBack callBack) {
        apiService.addFontMsg(word).enqueue(callBack);
    }

    /**
     * 已打赏测字列表
     */
    public void findPayDivinationList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findPayDivinationList(page, rows).enqueue(callBack);
    }

    /**
     * 改变测字信息为已读
     */
    public void changeFontStatus(String dtId, KDSPHttpCallBack callBack) {
        apiService.changeFontStatus(dtId).enqueue(callBack);
    }

    /**
     * 查询当前用户的支线手相列表
     *
     * @param type 手相类型2代表一线，3为二线，以此类推
     */
    public void findPalmBranchList(int type, int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findPalmBranchList(type, page, rows).enqueue(callBack);
    }

    /**
     * 微信支付
     *
     * @param fee  支付的钱，以分为单位
     * @param dtId 测字的id
     */
    public void unifiedorder(int fee, String dtId, KDSPHttpCallBack callBack) {
        apiService.unifiedorder(fee, dtId).enqueue(callBack);
    }

    /**
     * 支付宝支付
     */
    public void sign(int fee, String dtId, KDSPHttpCallBack callBack) {
        apiService.sign(fee, dtId).enqueue(callBack);
    }

    /**
     * 获取每月运势
     */
    public void findLuckymonth(KDSPHttpCallBack callBack) {
        apiService.findLuckymonth().enqueue(callBack);
    }

    /**
     * 获取别名列表
     */
    public void findLuckydayAlias(KDSPHttpCallBack callBack) {
        apiService.findLuckydayAlias().enqueue(callBack);
    }

    /**
     * 获取配置
     */
    public void initSysConfig(KDSPHttpCallBack callBack) {
        apiService.initSysConfig().enqueue(callBack);
    }

    /**
     * 获取我测过的测试题
     */
    public void findMyTestList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findMyTestList(page, rows).enqueue(callBack);
    }

    /**
     * 根据测试id获取测试题
     */
    public void findTestListByTcId(String categoryId, int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findTestListByTcId(categoryId, page, rows).enqueue(callBack);
    }

    /**
     * 获取星宿感情及关系配对
     *
     * @param myBirthday   我的生日（格式：1989-01-01）
     * @param yourBirthday 他的生日（格式：1989-01-01）
     * @param index        两者关系（只能传1，2，3 传其他的算法就不对了，1如果是恋人的结果，2如果是朋友的结果，3如果是共事者的结果）
     */
    public void twoPeopleRelation(String myBirthday, String yourBirthday, int index, KDSPHttpCallBack callBack) {
        apiService.twoPeopleRelation(myBirthday, yourBirthday, index).enqueue(callBack);
    }

    /**
     * 检查软件更新
     */
    public void findSoftUpdate(KDSPHttpCallBack callBack) {
        apiService.findSoftUpdate().enqueue(callBack);
    }

    /**
     * 刷新求签
     */
    public void flushShaking(KDSPHttpCallBack callBack) {
        apiService.flushShaking().enqueue(callBack);
    }

    /**
     * 求签
     *
     * @param type      为求签类型，1为月老签，2为事业签,3为财运
     * @param signCount 签文index，月老1-101，事业，财运1-100随机
     */
    public void askShaking(int type, int signCount, KDSPHttpCallBack callBack) {
        apiService.askShaking(type, signCount).enqueue(callBack);
    }

    /**
     * 获取求签列表
     */
    public void findShaking(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findShaking(page, rows).enqueue(callBack);
    }

    /**
     * 点赞
     *
     * @param shakId 点赞id
     * @param isLike 是否点赞，1为赞，2为不赞
     */
    public void isLike(String shakId, int isLike, KDSPHttpCallBack callBack) {
        apiService.isLike(shakId, isLike).enqueue(callBack);
    }

    /**
     * 根据分类获取商品（问题）列表
     */
    public void findItemList(String catId, int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findItemList(catId, page, rows).enqueue(callBack);
    }

    /**
     * 微信支付接口
     */
    public void wxpay(String itemId, String itemParam, int pointSum, KDSPHttpCallBack callBack) {
        apiService.wxpay(itemId, itemParam, pointSum).enqueue(callBack);
    }

    /**
     * 支付宝支付优惠券接口
     */
    public void alipay(String itemId, String itemParam, int pointSum, KDSPHttpCallBack callBack) {
        apiService.alipay(itemId, itemParam, pointSum).enqueue(callBack);
    }

    /**
     * 福豆支付接口
     */
    public void pointPay(String itemId, String itemParam, KDSPHttpCallBack callBack) {
        apiService.pointPay(itemId, itemParam).enqueue(callBack);
    }

    /**
     * 微信支付优惠券接口
     */
    public void wechatPay(String itemId, String itemParam, int pointSum, String couponId, KDSPHttpCallBack callBack) {
        apiService.wechatPay(itemId, itemParam, pointSum, couponId).enqueue(callBack);
    }

    /**
     * 支付宝支付接口
     */
    public void aliPay(String itemId, String itemParam, int pointSum, String couponId, KDSPHttpCallBack callBack) {
        apiService.aliPay(itemId, itemParam, pointSum, couponId).enqueue(callBack);
    }

    /**
     * 福豆支付优惠券接口
     */
    public void membershipPay(String itemId, String itemParam, String couponId, int pointSum, KDSPHttpCallBack callBack) {
        apiService.membershipPay(itemId, itemParam, couponId, pointSum).enqueue(callBack);
    }


    /**
     * 根据关键字搜索商品（问题）
     */
    public void searchItem(String catId, String itemName, int flag, int page, int rows, KDSPHttpCallBack callBack) {
        apiService.searchItem(catId, itemName, flag, page, rows).enqueue(callBack);
    }

    /**
     * 用户添加问题
     */
    public void addPreItem(String itemName, KDSPHttpCallBack callBack) {
        apiService.addPreItem(itemName).enqueue(callBack);
    }

    /**
     * 待回复订单
     */
    public void findPreReplyOrderList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findPreReplyOrderList(page, rows).enqueue(callBack);
    }

    /**
     * 已回复订单
     */
    public void findRepliedOrderList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findRepliedOrderList(page, rows).enqueue(callBack);
    }

    /**
     * 已回复订单
     */
    public void updateItemStatusByOrderId(String ioId, KDSPHttpCallBack callBack) {
        apiService.updateItemStatusByOrderId(ioId).enqueue(callBack);
    }

    /**
     * 获取用户未读数
     */
    public void getItemMsgCount(KDSPHttpCallBack callBack) {
        if(SPUtils.getBoolleanValue(SERVICE_POLICY_AGREE)) {
            apiService.getItemMsgCount().enqueue(callBack);
        }
    }
    /**
     * 反馈列表
     */
    public void findFeedback(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findFeedback(page, rows).enqueue(callBack);
    }

    /**
     * 增加反馈,文字
     */
    public void addFeedback(String content, KDSPHttpCallBack callBack) {
        apiService.addFeedback(content, 0).enqueue(callBack);
    }

    /**
     * 增加反馈,图片
     */
    public void addFeedback(String content, int width, int height, KDSPHttpCallBack callBack) {
        apiService.addFeedback(content, 1, width, height).enqueue(callBack);
    }

    /**
     * 增加反馈
     */
    public void addFeedback(File content, int width, int height, KDSPHttpCallBack callBack) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), content);
        MultipartBody.Part image = MultipartBody.Part.createFormData("file", content.getName(), requestFile);
        apiService.addFeedback(image, RequestBody.create(MediaType.parse("multipart/form-data"), "1"), RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(width)), RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(height))).enqueue(callBack);
    }

    /**
     * 注册验证码
     *
     * @param code            验证码
     * @param telephoneNumber 用户名
     * @param password        密码
     */
    public void register(String code, String telephoneNumber, String password, KDSPHttpCallBack callBack) {
        apiService.register(code, telephoneNumber, password).enqueue(callBack);
    }

    /**
     * 注册验证码
     *
     * @param telephoneNumber 电话号码
     */
    public void createCode(String telephoneNumber, KDSPHttpCallBack callBack) {
        apiService.createCode(telephoneNumber, AppContext.channel).enqueue(callBack);
    }

    /**
     * 更改密码
     *
     * @param mobile 电话号码
     */
    public void editPasswordCode(String mobile, KDSPHttpCallBack callBack) {
        apiService.editPasswordCode(mobile).enqueue(callBack);
    }

    /**
     * 用户登录
     *
     * @param telephoneNumber 电话号码
     * @param password        密码
     */
    public void userLogin(String telephoneNumber, String password, KDSPHttpCallBack callBack) {
        apiService.userLogin(telephoneNumber, password, AppContext.channel).enqueue(callBack);
    }

    /**
     * 用户微信登录
     */
    public void wxLogin(String code, KDSPHttpCallBack callBack) {
        apiService.wxLogin(code).enqueue(callBack);
    }

    /**
     * 用户qq登录
     */
    public void qqLogin(String token, String openId, KDSPHttpCallBack callBack) {
        apiService.qqLogin(token, openId).enqueue(callBack);
    }

    /**
     * 用户weibo登录
     */
    public void webLogin(String token, String uid, KDSPHttpCallBack callBack) {
        apiService.webLogin(token, uid).enqueue(callBack);
    }

    /**
     * 编辑用户信息，以文件的形式传入图片
     */
    public void editUserInfo(String nickName, String birthTimes, int gender, String description, File image, String blood, KDSPHttpCallBack callBack) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("file", image.getName(), requestFile);
        apiService.editUserInfo(RequestBody.create(null, nickName), RequestBody.create(null, birthTimes), RequestBody.create(null, String.valueOf(gender)), RequestBody.create(null, description), photo, RequestBody.create(null, blood)).enqueue(callBack);
    }

    /**
     * 编辑用户信息,以地址的形式传入图片
     */
    public void editUserInfo(String nickName, String birthTimes, int gender, String description, String image, String blood, int isLunar, KDSPHttpCallBack callBack) {
        apiService.editUserInfo(nickName, birthTimes, gender, description, image, blood, isLunar).enqueue(callBack);
    }

    /**
     * 更改密码
     */
    public void editPassword(String code, String telephoneNumber, String password, KDSPHttpCallBack callBack) {
        apiService.editPassword(code, telephoneNumber, password).enqueue(callBack);
    }

    /**
     * 匿名用户注册
     */
    public void anonymityLogin(String openUDID, KDSPHttpCallBack callBack) {
        apiService.anonymityLogin(openUDID, AppContext.channel).enqueue(callBack);
    }

    /**
     * 获取用户信息
     */
    public void getUserInfoByUserId(KDSPHttpCallBack callBack) {
        apiService.getUserInfoByUserId().enqueue(callBack);
    }

    /**
     * 向七牛上传图片
     *
     * @param key  拼接的图片地址
     * @param file 图片文件
     */
    public void uploadImageToQiNiu(String key, File file, QiNiuUploadCallBack callBack) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        //调用七牛接口时，需传入七牛的token
        String qiniuToken = SPUtils.getStringValue(YSRLConstants.QINIU_TOKEN);
        apiService.uploadImageToQiNiu(RequestBody.create(null, key), RequestBody.create(null, qiniuToken), image).enqueue(callBack);
    }

    /**
     * 添加统计
     */
    public void addStatistics(int source, int type, String subType, KDSPHttpCallBack callBack) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            apiService.addTestStatistics(source, type, subType).enqueue(callBack);
        } else {
            apiService.addStatistics(source, type, subType).enqueue(callBack);
        }
    }

    /**
     * 获取定制运势页面数据
     */
    public void getCustomFortuneData(KDSPHttpCallBack callBack) {
        apiService.getCustomFortuneData().enqueue(callBack);
    }

    /**
     * 根据问题ID查询问题及属性以及福豆数
     */
    public void getFeatureAndPointByItemId(String ItemId, KDSPHttpCallBack callBack) {
        apiService.getFeatureAndPointByItemId(ItemId).enqueue(callBack);
    }

    /**
     * 根据问题ID和用户ID查询问题及属性以及福豆数会员，优惠券等信息
     */
    public void getItemInfoByItemId(String ItemId, KDSPHttpCallBack callBack) {
        apiService.getItemInfoByItemId(ItemId).enqueue(callBack);
    }

    /**
     * 获取手相面相的数据
     */
    public void getLookPalmData(KDSPHttpCallBack callBack) {
        apiService.getLookPalmData().enqueue(callBack);
    }

    /**
     * 根据用户ID，获取福豆数
     */
    public void getPointByUserId(KDSPHttpCallBack callBack) {
        apiService.getPointByUserId().enqueue(callBack);
    }

    /**
     * 根据订单ID，获取回复数据
     */
    public void getItemAnswerByOrderId(String ioId, KDSPHttpCallBack callBack) {
        apiService.getItemAnswerByOrderId(ioId).enqueue(callBack);
    }

    /**
     * 查询问题列表(福豆兑换列表)
     */
    public void findItems(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findItems(page, rows).enqueue(callBack);
    }

    /**
     * 获取福豆
     */
    public void oper(String bigCat, String smallCat, String keyCat, KDSPHttpCallBack callBack) {
        apiService.oper(bigCat, smallCat, keyCat).enqueue(callBack);
    }

    /**
     * 取消占卜订单
     */
    public void cancelOrder(String ioId, KDSPHttpCallBack callBack) {
        apiService.cancelOrder(ioId).enqueue(callBack);
    }

    /**
     * 删除占卜订单
     */
    public void delAuguryOrder(String ioId, KDSPHttpCallBack callBack) {
        apiService.delAuguryOrder(ioId).enqueue(callBack);
    }

    /**
     * 获取会员状态
     */
    public void getMembership(KDSPHttpCallBack callBack) {
        apiService.getMembership().enqueue(callBack);
    }

    /**
     * 获取会员卡种类
     */
    public void getMembershipCat(KDSPHttpCallBack callBack) {
        apiService.getMembershipCat().enqueue(callBack);
    }

    /**
     * 支付宝购买和充值
     */
    public void aliPayMember(String mscId, int price, KDSPHttpCallBack callBack) {
        apiService.aliPayMember(mscId, price).enqueue(callBack);
    }

    /**
     * 微信购买和充值
     */
    public void wxPayMember(String mscId, int price, KDSPHttpCallBack callBack) {
        apiService.wxPayMember(mscId, price).enqueue(callBack);
    }

    /**
     * 获取会员卡信息及会员福利
     */
    public void getMembershipMsg(KDSPHttpCallBack callBack) {
        apiService.getMembershipMsg().enqueue(callBack);
    }

    /**
     * 根据礼品ID获取礼品对应的表单属性
     */
    public void exchangeMembershipGift(String msgId, String itemId, String itemParam, KDSPHttpCallBack callBack) {
        apiService.exchangeMembershipGift(msgId, itemId, itemParam).enqueue(callBack);
    }

    /**
     * 福豆+会员卡支付
     */
    public void membership(String itemId, String itemParam, int pointSum, KDSPHttpCallBack callBack) {
        apiService.membership(itemId, itemParam, pointSum).enqueue(callBack);
    }

    /**
     * 血型测算
     */
    public void getBloodData(String myBlood, String dadBlood, String momBlood, KDSPHttpCallBack callBack) {
        apiService.getBloodData(myBlood, dadBlood, momBlood).enqueue(callBack);
    }

    /**
     * 商品列表
     */
    public void getGoodsList(int type, boolean isTheme, int page, int rows, String birthday, String classifyId, KDSPHttpCallBack callBack) {
        if (isTheme) {
            if (type == 0) {
                apiService.getCharmGoodsThemeList(page, rows).enqueue(callBack);
            } else {
                apiService.getGoodsThemeList(page, rows, birthday).enqueue(callBack);
            }

        } else {
            apiService.getGoodsList(page, rows, classifyId).enqueue(callBack);
        }
    }

    /**
     * 根据商品ID，查询商品详情
     */
    public void getGoodsDetailsById(String goodsId, KDSPHttpCallBack callBack) {
        apiService.getGoodsDetailsById(goodsId).enqueue(callBack);
    }

    /**
     * 增加收货地址
     */
    public void addShippingAddr(String receiverMobile, String receiverName, String receiverAddress, String receiverZip,
                                String province, String city, String area, KDSPHttpCallBack callBack) {
        apiService.addShippingAddr(receiverMobile, receiverName, receiverAddress, receiverZip, province, city, area).enqueue(callBack);
    }

    /**
     * 根据用户ID查询收货地址
     */
    public void findShippingAddrList(KDSPHttpCallBack callBack) {
        apiService.findShippingAddrList().enqueue(callBack);
    }

    /**
     * 根据用户收货地址ID删除收货地址
     */
    public void deleteShippingAddr(String shipId, KDSPHttpCallBack callBack) {
        apiService.deleteShippingAddr(shipId).enqueue(callBack);
    }

    /**
     * 设置为默认收货地址
     */
    public void changeShippingAddrIsDefault(String shipId, KDSPHttpCallBack callBack) {
        apiService.changeShippingAddrIsDefault(shipId).enqueue(callBack);
    }

    /**
     * 添加或修改购物车
     */
    public void addCart(String skuId, int count, KDSPHttpCallBack callBack) {
        apiService.addCart(skuId, count).enqueue(callBack);
    }

    /**
     * 根据ID删除购物车
     */
    public void deleteCartById(String cartId, KDSPHttpCallBack callBack) {
        apiService.deleteCartById(cartId).enqueue(callBack);
    }

    /**
     * 清空无效购物车
     */
    public void clearInvalidCart(KDSPHttpCallBack callBack) {
        apiService.clearInvalidCart().enqueue(callBack);
    }

    /**
     * 根据用户ID查询购物车
     */
    public void findCartsByUserId(KDSPHttpCallBack callBack) {
        apiService.findCartsByUserId().enqueue(callBack);
    }

    /**
     * 通过购物车提交订单
     */
    public void submitOrderByCart(String cartIds, String shipId, String couponId, String remark, KDSPHttpCallBack callBack) {
        apiService.submitOrderByCart(cartIds, shipId, couponId, remark).enqueue(callBack);
    }

    /**
     * 查询问题分类列表
     */
    public void findQuestionCatList(KDSPHttpCallBack callBack) {
        apiService.findQuestionCatList().enqueue(callBack);
    }

    /**
     * 查询主题问题分类列表
     */
    public void findQuestionThemeList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findQuestionThemeList(page, rows).enqueue(callBack);
    }

    /**
     * 获取大师亲算问题的列表
     */
    public void findMasterCalculationList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findMasterCalculationList(page, rows).enqueue(callBack);
    }

    /**
     * 获取大师亲算问题的主题列表
     */
    public void findMasterCalculationThemeList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findMasterCalculationThemeList(page, rows).enqueue(callBack);
    }

    /**
     * 通过商品详情提交订单
     */
    public void submitOrder(String skuId, int count, String shipId, String couponId, String remark, KDSPHttpCallBack callBack) {
        apiService.submitOrder(skuId, count, shipId, couponId, remark).enqueue(callBack);
    }

    /**
     * 立刻购买确定下单获得订单详情
     */
    public void confirmSubmitOrder(String skuId, int count, String shipId, KDSPHttpCallBack callBack) {
        apiService.confirmSubmitOrder(skuId, count, shipId).enqueue(callBack);
    }

    /**
     * 购物车确定下单获得订单详情
     */
    public void confirmSubmitOrderByCart(String cartIds, String shipId, KDSPHttpCallBack callBack) {
        apiService.confirmSubmitOrderByCart(cartIds, shipId).enqueue(callBack);
    }

    /**
     * 去支付,支付方式（1是微信，2是支付宝）
     */
    public void toPay(String orderIds, int payway, KDSPHttpCallBack callBack) {
        apiService.toPay(orderIds, payway).enqueue(callBack);
    }

    /**
     * 查询某个状态的订单列表
     */
    public void findOrderByUserIdAndStatus(int page, int rows, int status, KDSPHttpCallBack callBack) {
        if (status != OrderDetail.COMPLETED) {
            //查询其他状态的订单
            apiService.findOrderByUserIdAndStatus(page, rows, status).enqueue(callBack);
        } else {
            //查询我的订单（包括订单关闭，交易成功的单子）
            apiService.findMyOrderByUserId(page, rows).enqueue(callBack);
        }
    }

    /**
     * 绑定手机发送验证码
     *
     * @param telephoneNumber 电话号码
     */
    public void sendBindTelCode(String telephoneNumber, KDSPHttpCallBack callBack) {
        apiService.sendBindTelCode(telephoneNumber).enqueue(callBack);
    }

    /**
     * 绑定手机
     *
     * @param code            验证码
     * @param telephoneNumber 用户名
     * @param password        密码
     */
    public void bindTelAndMergeUserMsg(String code, String telephoneNumber, String password, KDSPHttpCallBack callBack) {
        apiService.bindTelAndMergeUserMsg(code, telephoneNumber, password).enqueue(callBack);
    }

    /**
     * 修改手机号
     *
     * @param code            验证码
     * @param telephoneNumber 用户名
     * @param password        密码
     */
    public void updatePhoneNum(String code, String telephoneNumber, String password, KDSPHttpCallBack callBack) {
        apiService.updatePhoneNum(code, telephoneNumber, password).enqueue(callBack);
    }

    /**
     * 点击退款
     *
     * @param orderId 整个订单ID
     * @param odId    订单明细ID
     */
    public void toRefund(String orderId, String odId, KDSPHttpCallBack callBack) {
        apiService.toRefund(orderId, odId).enqueue(callBack);
    }

    /**
     * 提交退款申请
     *
     * @param orderId     整个订单ID
     * @param odId        订单明细ID
     * @param serverName  退款类型(退款退货、仅退款)
     * @param reason      退款原因
     * @param description 退款原因详细描述
     * @param images      图片地址（多张图片，客户端拼接以逗号“,”隔开）
     */
    public void submitRefundApply(String orderId, String odId, String serverName, String reason, String description, String images, KDSPHttpCallBack callBack) {
        apiService.submitRefundApply(orderId, odId, serverName, reason, description, images).enqueue(callBack);
    }

    /**
     * 取消退款申请
     *
     * @param refundId 退货退款ID
     */
    public void cancelRefundApply(String refundId, KDSPHttpCallBack callBack) {
        apiService.cancelRefundApply(refundId).enqueue(callBack);
    }

    /**
     * 重新申请退款
     *
     * @param refundId 退货退款ID
     */
    public void againRefundApply(String refundId, KDSPHttpCallBack callBack) {
        apiService.againRefundApply(refundId).enqueue(callBack);
    }

    /**
     * 提交物流信息
     */
    public void submitRefundShippingMsg(String refundId, String shipCode, String expressCompany, KDSPHttpCallBack callBack) {
        apiService.submitRefundShippingMsg(refundId, shipCode, expressCompany).enqueue(callBack);
    }

    /**
     * 查询购物车数量
     */
    public void findCartCountByUserId(KDSPHttpCallBack callBack) {
        apiService.findCartCountByUserId().enqueue(callBack);
    }

    /**
     * 根据订单ID获取物流信息
     *
     * @param orderId 订单id
     */
    public void viewShipInfo(String orderId, KDSPHttpCallBack callBack) {
        apiService.viewShipInfo(orderId).enqueue(callBack);
    }

    /**
     * 取消（关闭）订单
     *
     * @param orderId 订单id
     */
    public void cancelOrCloseOrder(String orderId, KDSPHttpCallBack callBack) {
        apiService.cancelOrCloseOrder(orderId).enqueue(callBack);
    }

    /**
     * 删除订单
     *
     * @param orderId 订单id
     */
    public void delOrder(String orderId, KDSPHttpCallBack callBack) {
        apiService.delOrder(orderId).enqueue(callBack);
    }

    /**
     * 确认收货
     *
     * @param orderId 订单id
     */
    public void confirmReceipt(String orderId, KDSPHttpCallBack callBack) {
        apiService.confirmReceipt(orderId).enqueue(callBack);
    }

    /**
     * 添加留言
     *
     * @param msgType 消息类型(默认为0：文本，1：图片，2：json数据)
     * @param content 消息内容（包含图片地址和json数据都放到此字段中）
     */
    public void addChatMsg(int msgType, String content, KDSPHttpCallBack callBack) {
        apiService.addChatMsg(msgType, content).enqueue(callBack);
    }

    /**
     * 获取留言列表
     */
    public void getChatMsgList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.getChatMsgList(page, rows).enqueue(callBack);
    }

    /**
     * 获取地址列表数
     */
    public void getShippingCount(KDSPHttpCallBack callBack) {
        apiService.getShippingCount().enqueue(callBack);
    }

    /**
     * 改变语音的状态为已读
     *
     * @param iaId 语音id
     */
    public void changeAnswerReaded(String iaId, KDSPHttpCallBack callBack) {
        apiService.changeAnswerReaded(iaId).enqueue(callBack);
    }

    /**
     * 获取留言用户列表以及最后一条留言
     */
    public void findChatMsgList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findChatMsgList(page, rows).enqueue(callBack);
    }

    /**
     * 根据用户ID查询留言列表
     */
    public void findChatMsgListByUserId(int page, int rows, String sendUserId, KDSPHttpCallBack callBack) {
        apiService.findChatMsgListByUserId(page, rows, sendUserId).enqueue(callBack);
    }

    /**
     * 添加留言
     *
     * @param sendUserId 回复的用户
     */
    public void replyChatMsg(int msgType, String content, String sendUserId, KDSPHttpCallBack callBack) {
        apiService.replyChatMsg(msgType, content, sendUserId).enqueue(callBack);
    }

    /**
     * 推荐实物商品
     */
    public void getRecommendGoodsList(KDSPHttpCallBack callBack) {
        apiService.getRecommendGoodsList().enqueue(callBack);
    }

    /**
     * 推荐虚拟商品
     */
    public void getRecommendQuestionList(KDSPHttpCallBack callBack) {
        apiService.getRecommendQuestionList().enqueue(callBack);
    }

    /**
     * 获取十年大运趋势图数据
     */
    public void getFortuneScore(KDSPHttpCallBack callBack) {
        apiService.getFortuneScore().enqueue(callBack);
    }

    /**
     * 获取十年大运结果
     *
     * @param ioId 订单ID
     */
    public void getFortuneAnswer(String ioId, KDSPHttpCallBack callBack) {
        apiService.getFortuneAnswer(ioId).enqueue(callBack);
    }

    /**
     * 免费测名
     *
     * @param familyName 姓（最多两个汉字）
     * @param LastName   名（最多三个汉字）
     */
    public void getNameTestResult(String familyName, String LastName, KDSPHttpCallBack callBack) {
        apiService.getNameTestResult(familyName, LastName).enqueue(callBack);
    }

    /**
     * 起名拿答案
     *
     * @param ioId 订单ID
     */
    public void getNameTestResults(String ioId, KDSPHttpCallBack callBack) {
        apiService.getNameTestResults(ioId).enqueue(callBack);
    }

    /**
     * 商品分类
     */
    public void getClassifyList(KDSPHttpCallBack callBack) {
        apiService.getClassifyList().enqueue(callBack);
    }

    /**
     * 获取主页数据
     *
     * @param birthday 生日（格式：yyyy-MM-dd-HH-mm）
     */
    public void getMainData(String birthday, int sex, KDSPHttpCallBack callBack) {
        apiService.getMainData(birthday, sex).enqueue(callBack);
    }

    /**
     * 获取文章列表
     */
    public void findArticleList(int articleType, int page, int rows, KDSPHttpCallBack callBack) {
        if (articleType == 0) {
            apiService.findArticleList(page, rows).enqueue(callBack);
        } else {
            apiService.findArticleList(articleType, page, rows).enqueue(callBack);
        }

    }

    /**
     * 根据id获取文章
     */
    public void getArticleByIdAndUserId(String articleId, KDSPHttpCallBack callBack) {
        apiService.getArticleByIdAndUserId(articleId).enqueue(callBack);
    }

    /**
     * 获取文章评论列表
     */
    public void findArticleCommentList(String articleId, int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findArticleCommentList(articleId, page, rows).enqueue(callBack);
    }

    /**
     * 添加文章评论
     */
    public void addArticleComment(String articleId, String content, KDSPHttpCallBack callBack) {
        apiService.addArticleComment(articleId, content).enqueue(callBack);
    }

    /**
     * 文章收藏或取消收藏
     */
    public void operArticleCollect(String articleId, int isCollect, KDSPHttpCallBack callBack) {
        apiService.operArticleCollect(articleId, isCollect).enqueue(callBack);
    }

    /**
     * 查询我收藏的文章列表
     */
    public void findMyCollectArticleList(int page, int rows, KDSPHttpCallBack callBack) {
        apiService.findMyCollectArticleList(page, rows).enqueue(callBack);
    }

    /**
     * 查询我收藏的文章列表
     */
    public void findMyCouponListByPhoneNum(String phoneNum, KDSPHttpCallBack callBack) {
        apiService.findMyCouponListByPhoneNum(phoneNum).enqueue(callBack);
    }

    /**
     * 获取生命数
     */
    public void getLifeNumData(String birthday, KDSPHttpCallBack callBack) {
        apiService.getLifeNumData(birthday).enqueue(callBack);
    }

    /**
     * 获取八字
     */
    public void getEightFontLifeData(String birthday, KDSPHttpCallBack callBack) {
        apiService.getEightFontLifeData(birthday).enqueue(callBack);
    }

    /**
     * 获取优惠券
     */
    public void claimCoupon(String couponNum, KDSPHttpCallBack callBack) {
        apiService.claimCoupon(couponNum).enqueue(callBack);
    }

  /**
     * 获取活动优惠券
     */
    public void findNoActivationCoupon ( KDSPHttpCallBack callBack) {
        apiService.findNoActivationCoupon().enqueue(callBack);
    }

    /**
     * 激活取消优惠券
     */
    public void activationOrCancelCoupon (String mcId,int isUsable, KDSPHttpCallBack callBack) {
        apiService.activationOrCancelCoupon(mcId,isUsable).enqueue(callBack);
    }

    /**
     * h5支付宝支付
     */
    public void alipay(String data, KDSPHttpCallBack callBack) {
        apiService.alipay(data).enqueue(callBack);
    }

    /**
     * h5微信支付
     */
    public void wxpay(String data, KDSPHttpCallBack callBack) {
        apiService.wxpay(data).enqueue(callBack);
    }

    /**
     * h5微信支付
     */
    public void findSpeItem(KDSPHttpCallBack callBack) {
        apiService.findSpeItem().enqueue(callBack);
    }

    /**
     * 广告轮播图
     */
    public void findCarousel(KDSPHttpCallBack callBack) {
        apiService.findCarousel().enqueue(callBack);
    }

    /**
     * 广告轮播图统计
     */
    public void clickCarousel(String id, KDSPHttpCallBack callBack) {
        apiService.clickCarousel(id).enqueue(callBack);
    }

    /**
     * 大师一对一商品
     */
    public void findOne2oneItems(String classify, KDSPHttpCallBack callBack) {
        apiService.findOne2oneItems(classify).enqueue(callBack);
    }

    /**
     * 大师一对一确定下单
     */
    public void confirmSubmitOrder(String itemJson, KDSPHttpCallBack callBack) {
        apiService.confirmSubmitOrder(itemJson).enqueue(callBack);
    }

    /**
     * 大师一对一订单列表
     */
    public void findOne2OneOrders(int page,int rows,KDSPHttpCallBack callBack) {
        apiService.findOne2OneOrders(page,rows).enqueue(callBack);
    }

    /**
     * 大师一对一订单列表
     */
    public void toPay(String itemJson,int pointSum,String couponId,int payWay, KDSPHttpCallBack callBack) {
        apiService.toPay(itemJson,pointSum,couponId,payWay).enqueue(callBack);
    }

    /**
     * 大师一对一订单列表
     */
    public void auguryCancelOrCloseOrder(String itemJson, KDSPHttpCallBack callBack) {
        apiService.auguryCancelOrCloseOrder(itemJson).enqueue(callBack);
    }


}