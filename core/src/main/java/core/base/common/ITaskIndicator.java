package core.base.common;

/**
 * 进度指示的接口
 * 
 * @author DrkCore 178456643@qq.com
 * @since 2015年11月2日13:38:18
 */
public interface ITaskIndicator {
	
	boolean isProgressing ();
	
	void showProgress ();
	
	void hideProgress ();
	
}
