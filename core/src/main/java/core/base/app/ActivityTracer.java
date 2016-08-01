package core.base.app;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * 用于管理Activity栈的工具类。
 * 由于使用{@link WeakReference}所以无需担心内存泄露的问题。
 *
 * @author DrkCore
 * @since 2015年11月18日23:20:46
 */
public final class ActivityTracer {
	
	/* 单例模式 */
	
	private static final ActivityTracer INSTANCE = new ActivityTracer();
	
	public static ActivityTracer getInstance () {
		return INSTANCE;
	}
	
	private ActivityTracer () {}
	
	/* Activity的引用队列 */
	
	private final List<WeakReference<Activity>> activityTrace = new LinkedList<>();
	
	public void addActivity (Activity activity) {
		if (activity != null && !activity.isFinishing()) {// 只在Activity可用时添加
			activityTrace.add(new WeakReference<>(activity));
		}
	}
	
	public int getActivityTraceSize () {
		return activityTrace.size();
	}
	
	public boolean isActivityExits (Activity activity) {
		if (activity != null) {
			for (WeakReference<Activity> actRef : activityTrace) {
				if (actRef.get() == activity) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isActivityExits (Class<? extends Activity> clazz) {
		if (clazz != null) {
			Activity act;
			for (WeakReference<Activity> actRef : activityTrace) {
				act = actRef.get();
				if (act != null && act.getClass() == clazz) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 具体实现请参阅{@link #removeActivity(Activity, boolean)}，默认会结束activity。
	 *
	 * @param activity
	 * @return
	 */
	public boolean removeActivity (Activity activity) {
		return removeActivity(activity, true);
	}
	
	/**
	 * 移除指向activity的引用。
	 *
	 * @param activity Activity的实例。为null时直接返回false
	 * @param finish   移除时是否finish还未结束的activity。
	 * @return 是否成功移除。
	 */
	public boolean removeActivity (Activity activity, boolean finish) {
		if (activity != null) {
			Activity act;
			boolean removeFlag = false;
			for (WeakReference<Activity> actRef : activityTrace) {
				act = actRef.get();
				if (act == activity) {
					if (!activity.isFinishing() && finish) {
						activity.finish();
					}
					removeFlag = true;
					break;
				}
			}
			if (removeFlag) {
				clearDeadReference();
			}
		}
		return false;
	}
	
	/**
	 * 具体实现请参阅{@link #removeActivity(Class, boolean, boolean)}，
	 * 默认移除并结束所有对应的实例。
	 *
	 * @param clazz
	 * @return
	 */
	public boolean removeActivity (Class<? extends Activity> clazz) {
		return removeActivity(clazz, true, true);
	}
	
	/**
	 * 移除指定Class指定的Activity的引用。
	 *
	 * @param clazz     Activity的Class
	 * @param finish    如果为true，当找到clazz对应的实例且其还未结束时，终结之。
	 * @param removeAll 当clazz的实例不唯一时，是否对所有的实例执行操作。
	 * @return 是否成功移除。只要移除一个实例便算是成功。
	 */
	public boolean removeActivity (Class<? extends Activity> clazz, boolean finish, boolean removeAll) {
		if (clazz == null) {
			return false;
		}
		Activity act;
		boolean removeOnce = false;
		for (WeakReference<Activity> actRef : activityTrace) {
			act = actRef.get();
			if (act != null && (act.getClass() == clazz)) {
				if (!act.isFinishing() && finish) {
					act.finish();
				}
				actRef.clear();
				
				removeOnce = true;
				if (!removeAll) {// 无需继续移除，深藏功与名
					break;
				}
			}
		}
		if (removeOnce) {
			clearDeadReference();// 打扫卫生
		}
		return removeOnce;// 只要成功至少一次就算是成功了
	}
	
	/**
	 * 结束所有的Activity并且清空引用。
	 */
	public void finishAllActivity () {
		WeakReference<Activity> actRef;
		Activity act;
		for (int i = activityTrace.size() - 1; i >= 0; i--) {
			actRef = activityTrace.get(i);
			act = actRef.get();
			if (act != null && !act.isFinishing()) {
				act.finish();
			}
			actRef.clear();
		}
		clearDeadReference();
		activityTrace.clear();
	}
	
	/**
	 * 清空已经被释放的或者已经finish掉的Activity的引用。
	 */
	public void clearDeadReference () {
		WeakReference<Activity> actRef;
		Activity act;
		for (int i = 0, size = activityTrace.size(); i < size; i++) {
			actRef = activityTrace.get(i);
			act = actRef.get();
			if (act == null || act.isFinishing()) {
				if (activityTrace.remove(actRef)) {
					size--;
					i--;
				}
			}
		}
	}
	
}
