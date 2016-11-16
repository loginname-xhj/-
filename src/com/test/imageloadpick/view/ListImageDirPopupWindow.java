package com.test.imageloadpick.view;

import java.util.List;

import com.test.imageloadpick.R;
import com.test.imageloadpick.bean.FolderBean;
import com.test.imageloadpick.utils.ImageLoader;
import com.test.imageloadpick.utils.VideoThumiaUtils;
import com.test.imageloadpick.utils.ImageLoader.Type;
import com.test.imageloadpick.utils.VideoThumiaUtils.Types;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ListImageDirPopupWindow extends PopupWindow {
	private int mWidth;
	private int mHeight;
	private View mContertView;
	private ListView mListView;
	private List<FolderBean> mDatas;
	private int clieck_position = 0;

	public interface OnDirSelectedListener {
		void onSelected(FolderBean folderBean);

	}

	public OnDirSelectedListener mListener;

	public OnDirSelectedListener getmListener() {
		return mListener;
	}

	public void setmListener(OnDirSelectedListener mListener) {
		this.mListener = mListener;
	}

	public ListImageDirPopupWindow(Context context, List<FolderBean> datas) {
		calWidthAndHeight(context);
		mContertView = LayoutInflater.from(context).inflate(
				R.layout.popup_main, null);
		mDatas = datas;
		setContentView(mContertView);
		setWidth(mWidth);
		setHeight(mHeight);
		setBackgroundDrawable(new BitmapDrawable());// 设置点击后消失
		setTouchable(true);
		setFocusable(true);
		setOutsideTouchable(true);// 设置外面可以点击
		// 常用的设置，点击外面，消失
		setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});
		initViews(context);
		initEvents(context);
	}

	private void initEvents(final Context context) {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mListener != null) {
					clieck_position = arg2;
					arg1.findViewById(R.id.iv_select_stats).setVisibility(
							View.VISIBLE);
					mListener.onSelected(mDatas.get(arg2));
				}
			}
		});
	}

	private void initViews(Context context) {
		mListView = (ListView) mContertView.findViewById(R.id.id_list_dir);
		mListView.setAdapter(new ListDirAdapter(context, mDatas));
	}

	/*
	 * 计算ppWindow的高度和宽度。
	 */
	private void calWidthAndHeight(Context context) {
		WindowManager vm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		vm.getDefaultDisplay().getMetrics(outMetrics);
		this.mWidth = outMetrics.widthPixels;
		this.mHeight = (int) (outMetrics.heightPixels * 0.7);// 取出屏幕的70%。
	}

	private class ListDirAdapter extends ArrayAdapter<FolderBean> {
		private LayoutInflater mInflater;
		private List<FolderBean> mDatas;
		private Context mContext;

		public ListDirAdapter(Context context, List<FolderBean> mDatas2) {
			super(context, 0, mDatas2);
			mInflater = LayoutInflater.from(context);
			this.mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_popup_main,
						parent, false);
				holder.mImg = (ImageView) convertView
						.findViewById(R.id.id_dir_item_image);
				holder.mDirName = (TextView) convertView
						.findViewById(R.id.id_dir_item_name);
				holder.mDirCount = (TextView) convertView
						.findViewById(R.id.id_dir_item_count);
				holder.iv_video_flag = (ImageView) convertView
						.findViewById(R.id.iv_video_flag);
				holder.iv_select_stats = (ImageView) convertView
						.findViewById(R.id.iv_select_stats);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			FolderBean bean = getItem(position);
			if (bean.getType() == 2) {
				// 视频

				// 重置数据
				holder.mImg.setImageResource(R.drawable.pictures_no);
				holder.iv_video_flag.setVisibility(View.VISIBLE);
				VideoThumiaUtils.getInstance(3, Types.LIFO, mContext)
						.loadVideoThumail(bean.getFirstImgPath(), holder.mImg);
				holder.mDirName.setText(bean.getName() + "我的视频");
				holder.mDirCount.setText(bean.getCount() + "");
				holder.iv_video_flag.setVisibility(View.VISIBLE);
			} else if (bean.getType() == 1) {
				// 重置数据
				// 图片
				holder.mImg.setImageResource(R.drawable.pictures_no);
				ImageLoader.getInstance(3, Type.LIFO).loadImage(
						bean.getFirstImgPath(), holder.mImg);
				holder.mDirName.setText(bean.getName());
				holder.iv_video_flag.setVisibility(View.GONE);
				holder.mDirCount.setText(bean.getCount() + "");
				holder.iv_video_flag.setVisibility(View.INVISIBLE);
			}
			if (clieck_position == position) {
				holder.iv_select_stats.setVisibility(View.VISIBLE);
			} else {
				holder.iv_select_stats.setVisibility(View.GONE);
			}
			return convertView;
		}
	}

	private static class ViewHolder {
		ImageView mImg;
		TextView mDirName;
		TextView mDirCount;
		ImageView iv_video_flag;
		ImageView iv_select_stats;
	}
}
