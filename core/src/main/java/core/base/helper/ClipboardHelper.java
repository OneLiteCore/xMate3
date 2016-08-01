package core.base.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

/**
 * 剪贴板辅助类
 *
 * @author DrkCore
 * @since 2015年3月21日00:25:19
 */
public class ClipboardHelper {

	private final Context context;

	public final Context getContext () {
		return context;
	}

	public ClipboardHelper (Context context) {
		this.context = context;
	}

	/* 复制到剪贴板 */
	
	/**
	 * 复制内容到剪贴板
	 *
	 * @param text
	 */
	public final void copyToClipboard (CharSequence text) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {// 高版本
			copyInHighApi(text);
		} else {// 低版本
			copyInLowApi(text);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void copyInLowApi (CharSequence text) {
		android.text.ClipboardManager clipMgr = (android.text.ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		clipMgr.setText(text);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void copyInHighApi (CharSequence text) {
		android.content.ClipboardManager clipMgr = (android.content.ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		clipMgr.setPrimaryClip(ClipData.newPlainText(null, text));
	}
	
	/* 获取剪贴板内容 */
	
	/**
	 * 获取当前剪贴板的内容
	 *
	 * @return
	 */
	public final String getClipText (Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {// 高版本
			return getClipTextInHighApi(context);
		} else {// 低版本
			return getClipTextInLowApi(context);
		}
	}
	
	@SuppressLint("NewApi")
	private String getClipTextInHighApi (Context context) {
		// 获取剪贴板服务
		android.content.ClipboardManager clipMgr = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		
		if (clipMgr.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {// 存在剪贴板中的是文本
			ClipData clipData = clipMgr.getPrimaryClip();
			int size = clipData.getItemCount();
			Item item = clipData.getItemAt(size - 1);// 获取最新的元素
			if (item != null) {
				return item.getText().toString();
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private String getClipTextInLowApi (Context context) {
		// 获取剪贴板服务
		android.text.ClipboardManager clipMgr = (android.text.ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		
		return clipMgr.getText().toString();
	}
	
}
