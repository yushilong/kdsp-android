package com.qizhu.rili.data;

import org.json.JSONObject;

import java.util.List;

/**
 * 获取数据后的数据消息
 */
public class DataMessage<T> {

    public static final int SUCCESS_FROM_DB = 0, SUCCESS_FROM_SERVER = 1, SUCCESS_FROM_CACHE = 2, FAIL = 3;
    public int status = SUCCESS_FROM_DB;            //状态字段
    public int appendNum;                           //新增加的数据数目
    public int totalNum;                            //数据总数目
    public String msg = "";                         //附加内容，则获取数据失败时，可以将原因写入
    public List<T> data;                            //数据列表
    public JSONObject jsonInfo;                     //从服务器返回的数据前台需要分析json时，从这里获取，根据具体需求来设定，一般留空即可
    public int returnNum;                           //从服务器返回的数据个数,用来判断是否显示“木有了”的用户交互

    public boolean isList = true;                   //判断是否为列表数据（默认为列表）
    public T dataDetail;                            //单条数据详情（当前不为列表时）

    public boolean isDataFromServer(){
        return status == SUCCESS_FROM_SERVER;
    }

    public boolean isDataFromDb(){
        return status == SUCCESS_FROM_DB;
    }

    public boolean isDataFromCache(){
        return status == SUCCESS_FROM_CACHE;
    }

    public boolean isFail(){
        return status == FAIL;
    }

    public boolean isDataSuccess() {
        return isDataFromDb() || isDataFromServer();
    }

    /**
     * 判断当前列表数据是不否为空
     */
    public boolean isDataEmpty() {
        return data == null || data.isEmpty();
    }

    /**
     * 没有更多数据
     */
    public boolean hasNoNextData() {
        return !isFail() && returnNum <= 0;
    }

    /**
     * 从数据库中获取数据后默认返回的DataMessage
     * @param returnNum     这一页返回的数据量
     * @param appendNum     新增的数量
     * @param totalNum      总量
     * @param data          数据
     */
    public static <T> DataMessage<T> buildDbDataMessage(int returnNum, int appendNum, int totalNum, List<T> data) {
        DataMessage<T> msg = new DataMessage<T>();
        msg.status = DataMessage.SUCCESS_FROM_DB;
        msg.returnNum = returnNum;
        msg.appendNum = appendNum;
        msg.totalNum = totalNum;
        msg.data = data;
        return msg;
    }

    /**
     * 从服务器中获取数据成功后默认返回的DataMessage
     * @param returnNum     这一页返回的数据量
     * @param appendNum     新增的数量
     * @param totalNum      总量
     * @param data          数据
     */
    public static <T> DataMessage<T> buildServerSuccessDataMessage(int returnNum, int appendNum, int totalNum, List<T> data) {
        return buildServerSuccessDataMessage(returnNum, appendNum, totalNum, null, data);
    }

    /**
     * 从服务器中获取数据成功后默认返回的DataMessage
     * @param returnNum     这一页返回的数据量,最好写在appendNum前，因为appendNum会对返回的列表做清除处理
     * @param appendNum     新增的数量
     * @param totalNum      总量
     * @param jsonInfo      对应json
     * @param data          数据
     */
    public static <T> DataMessage<T> buildServerSuccessDataMessage(int returnNum, int appendNum, int totalNum, JSONObject jsonInfo, List<T> data) {
        DataMessage<T> msg = new DataMessage<T>();
        msg.status = DataMessage.SUCCESS_FROM_SERVER;
        msg.returnNum = returnNum;
        msg.appendNum = appendNum;
        msg.totalNum = totalNum;
        msg.jsonInfo = jsonInfo;
        msg.data = data;
        return msg;
    }

    /**
     * 从内存中获取到单条数据详情后返回的DataMessage
     * @param data      数据
     * @param <T>       数据类型
     */
    public static <T> DataMessage<T> buildCacheDataDetailMessage(T data) {
        DataMessage<T> msg = new DataMessage<T>();
        msg.status = DataMessage.SUCCESS_FROM_CACHE;
        msg.isList = false;
        msg.dataDetail = data;
        return msg;
    }

    /**
     * 从服务器（数据库中）获取到单条数据详情后返回的DataMessage
     * @param data      数据
     * @param status    状态
     * @param <T>       数据类型
     */
    public static <T> DataMessage<T> buildSuccessDataDetailMessage(T data, int status) {
        DataMessage<T> msg = new DataMessage<T>();
        msg.status = status;
        msg.isList = false;
        msg.dataDetail = data;
        return msg;
    }

    /**
     * 获取数据失败后默认返回的DataMessage
     * @param msgStr    消息
     */
    public static <T> DataMessage<T> buildFailDataMessage(String msgStr) {
        DataMessage<T> msg = new DataMessage<T>();
        msg.status = DataMessage.FAIL;
        msg.msg = msgStr;
        return msg;
    }

    @Override
    public String toString() {
        return "DataMessage{" +
                "status=" + status +
                ", appendNum=" + appendNum +
                ", totalNum=" + totalNum +
                ", returnNum=" + returnNum +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", jsonInfo=" + jsonInfo +
                ", isList=" + isList +
                ", dataDetail=" + dataDetail +
                '}';
    }
}
