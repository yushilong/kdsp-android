package com.qizhu.rili;

import android.app.Activity;
import android.content.Context;

import com.qizhu.rili.ui.activity.MainActivity;
import com.qizhu.rili.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class AppManager {

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private MainActivity mMainActivity;

    private AppManager() {
        activityStack = new Stack<Activity>();
    }

    /**
     * 栈中是否为空
     */
    public boolean isEmpty() {
        return activityStack.isEmpty();
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    public boolean isMainFinished() {
        return this.mMainActivity == null;
    }

    public void setMainActivity(MainActivity activity) {
        this.mMainActivity = activity;
    }

    public MainActivity getMainActivity() {
        return this.mMainActivity;
    }

    public void clearMainActivity() {
        this.mMainActivity = null;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
        LogUtils.d("ACTIVITY STACK size = " + activityStack.size());
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.lastElement();
    }

    /**
     * 获取前一个activity，便于返回
     */
    public Activity lastActivity() {
        if (activityStack.size() < 2) {
            return null;
        }
        return activityStack.get(activityStack.size() - 1);
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack.empty()) {
            return;
        }
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 从栈中移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            if (activityStack.remove(activity)) {
                activity.finish();
            }
        }
        //隐藏上一个页面的键盘
/*        if(activityStack.size() > 0){
            UIUtils.alwaysHideSoftKeyboard(activityStack.peek());
        }*/
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        ArrayList<Activity> activityList = new ArrayList<Activity>(activityStack);
        for (Activity anActivityList : activityList) {
            if (null != anActivityList) {
                anActivityList.finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束所有Activity保留主界面
     */
    public void finishAllActivityExcludeMain() {
        int stackSize = activityStack.size();
        if (stackSize >= 1) {
            ArrayList<Activity> activityList = new ArrayList<Activity>(activityStack.subList(1, stackSize));
            for (Activity activity : activityList) {
                if (activity != null) {
                    activity.finish();
                    activityStack.remove(activity);
                }
            }
        }
    }

    /**
     * 返回堆栈中的activity列表
     */
    public static ArrayList<Activity> getActivityArrayList() {
        ArrayList<Activity> activityList = new ArrayList<Activity>(activityStack);
        Collections.reverse(activityList);
        return activityList;
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
/*			ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());*/
//			System.exit(0);
        } catch (Exception e) {
        }
    }

    /**
     * 程序是否该返回主界面
     * 仅当主activity并未启动时并且当前堆栈只有一个activity，则后退至mainActivity
     */
    public boolean backToMain() {
        return isMainFinished() && activityStack.size() < 2;
    }
}