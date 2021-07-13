package com.qizhu.rili.bean;

/**
 * @创建者 hefeng
 * @创建时间 2016/7/5 14:15
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 2016/7/5$
 * @描述 ${TODO}
 */
public class PagerBean {
    public int ivLeft;
    public int ivRight;
    public String tvLeft;
    public String tvRight;
    public String type;
    public String analysis_result;
    public String content;
    public int mode;

    public PagerBean(int ivLeft, int ivRight, String tvLeft, String tvRight, String type, String analysis_result, String content, int mode) {
        this.ivLeft = ivLeft;
        this.ivRight = ivRight;
        this.tvLeft = tvLeft;
        this.tvRight = tvRight;
        this.type = type;
        this.analysis_result = analysis_result;
        this.content = content;
        this.mode = mode;
    }
}
