package core.base.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import core.base.R;
import core.base.util.LogUtil;
import core.base.common.ITaskIndicator;

/**
 * 进度条对话框
 *
 * @author DrkCore 178456643@qq.com
 * @since 2015年10月29日13:20:25s
 */
public final class ProgressDlgFrag extends CoreDlgFrag implements ITaskIndicator {
	
	/* 继承 */
	
	@Override
	public void onCreate (@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
		setCancelable(false);
	}
	
	@Nullable
	@Override
	public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container,
	                          @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dlg_frag_progress, container, false);
	}
	
	/* ITaskIndicator */
	
	private FragmentManager fragMgr;
	private boolean progressing;
	
	public ProgressDlgFrag setFragmentManager (Fragment frag) {
		return setFragmentManager(frag.getChildFragmentManager());
	}
	
	public ProgressDlgFrag setFragmentManager (FragmentActivity fragAct) {
		return setFragmentManager(fragAct.getSupportFragmentManager());
	}
	
	public ProgressDlgFrag setFragmentManager (FragmentManager fragMgr) {
		this.fragMgr = fragMgr;
		return this;
	}
	
	@Override
	public boolean isProgressing () {
		return progressing;
	}
	
	@Override
	public void showProgress () {
		progressing = true;
		//阻塞用户操作
		setCancelable(false);
		setCancelOnTouchOutSideEnable(false);
		show(fragMgr);
	}
	
	@Override
	public void hideProgress () {
		try {
			dismiss();
		} catch (Exception e) {
			LogUtil.e(e);
		}
		progressing = false;
	}
}
