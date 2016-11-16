package com.test.imageloadpick.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.imageloadpick.R;
import com.test.imageloadpick.utils.ImageLoader;
import com.test.imageloadpick.utils.ImageLoader.Type;
import com.test.imageloadpick.utils.VideoThumiaUtils;
import com.test.imageloadpick.utils.VideoThumiaUtils.Types;

public class ImageAdapter extends BaseAdapter {
	private String mDirPath;
	private List<String> mImgPaths;
	private LayoutInflater mInflater;
	public static Set<String> mSelectImg = new HashSet<String>();
	private int type;
	private Context context;
	private int MaxFileCount;

	public interface InItSelectOnlistener {
		public void initselect(Set<String> mSelectImgdate);
	}

	private InItSelectOnlistener listener;

	public ImageAdapter(Context context, List<String> mDatas, String dirPath,
			int type, InItSelectOnlistener mylistener, int maxFileCount) {
		this.mDirPath = dirPath;
		this.mImgPaths = mDatas;
		this.mInflater = LayoutInflater.from(context);
		this.type = type;
		this.context = context;
		this.listener = mylistener;
		this.MaxFileCount = maxFileCount;
	}

	@Override
	public int getCount() {
		return mImgPaths.size();
	}

	@Override
	public Object getItem(int position) {
		return mImgPaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_gridview, parent,
					false);
			holder = new ViewHolder();
			holder.mImg = (ImageView) convertView
					.findViewById(R.id.id_item_image);
			holder.mSelect = (ImageButton) convertView
					.findViewById(R.id.id_item_select);
			holder.isShowVideo = (ImageView) convertView
					.findViewById(R.id.overlay);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.mImg.setImageResource(R.drawable.pictures_no);
		holder.mSelect.setImageResource(R.drawable.picture_unselected);
		holder.mImg.setColorFilter(null);
		if (type == 1) {
			// 图片
			ImageLoader.getInstance(3, Type.LIFO).loadImage(
					mDirPath + "/" + mImgPaths.get(position), holder.mImg);
			// System.out.println("图片:"+mImgPaths.get(position));
			holder.isShowVideo.setVisibility(View.GONE);
		} else if (type == 2) {
			// 视频
			File file = new File(mDirPath + "/" + mImgPaths.get(position));
			VideoThumiaUtils.getInstance(3, Types.LIFO, context)
					.loadVideoThumail(file.getAbsolutePath(), holder.mImg);
			// System.out.println("视频:"+mImgPaths.get(position));
			holder.isShowVideo.setVisibility(View.VISIBLE);
		}
		final String filepath = mDirPath + "/" + mImgPaths.get(position);

		holder.mImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if ((mSelectImg.size() + 1) > MaxFileCount) {
					if (mSelectImg.contains(filepath)) {
						mSelectImg.remove(filepath);
						holder.mImg.setColorFilter(null);
						holder.mSelect
								.setImageResource(R.drawable.picture_unselected);
						listener.initselect(mSelectImg);
					}
					return;
				}
				// 已经被选择
				if (mSelectImg.contains(filepath)) {
					mSelectImg.remove(filepath);
					holder.mImg.setColorFilter(null);
					holder.mSelect
							.setImageResource(R.drawable.picture_unselected);
				} else {
					// 没有被选择
					mSelectImg.add(filepath);
					holder.mImg.setColorFilter(Color.parseColor("#77000000"));
					holder.mSelect
							.setImageResource(R.drawable.pictures_selected);
				}
				listener.initselect(mSelectImg);
			}
		});
		if (mSelectImg.contains(filepath)) {
			holder.mImg.setColorFilter(Color.parseColor("#77000000"));
			holder.mSelect.setImageResource(R.drawable.pictures_selected);
		}
		if (type == 2) {
			try {
				File file = new File(filepath);
				System.out.println("视频文件:" + filepath);
				FileInputStream fos = new FileInputStream(file);
				long s = fos.available();
				boolean status = FormetFileSize(s);
				if (status) {
					// 文件为空.不处理
					holder.mSelect.setVisibility(View.VISIBLE);
					holder.mImg.setClickable(true);
				} else {
					// 超过10M
					// 设置视频文件不可以点击
					holder.mSelect.setVisibility(View.GONE);
					// holder.mImg.setClickable(false);
					holder.mImg.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Toast.makeText(context, "文件太大", Toast.LENGTH_SHORT)
									.show();
						}
					});
				}
			} catch (Exception e) {
				// 视频文件不存在,异常不做处理
				System.out.println("异常");
			}

		}
		return convertView;
	}

	private static class ViewHolder {
		ImageView mImg;
		ImageButton mSelect;
		ImageView isShowVideo;
	}

	/**
	 * 获取文件大小。
	 * 
	 * @param fileS
	 * @return
	 */
	public static boolean FormetFileSize(long fileS) {

		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		// String wrongSize = "0B";
		boolean status = true;
		if (fileS == 0) {
			return false;
		}

		if (fileS < 1024) {
			// B单位
			fileSizeString = df.format((double) fileS) + "B";
			System.out.println("文件大小:" + fileSizeString);
			return status;
		} else if (fileS < 1048576) {
			// kB
			fileSizeString = df.format((double) fileS / 1024) + "KB";
			System.out.println("文件大小:" + fileSizeString);
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
			System.out.println("文件大小:" + fileSizeString);
			// MB
			if (((double) fileS / 1048576) >= 10) {
				// ==等于10M,或者大于10M。
				// 直接返回
				return false;
			}

		} else {
			// GB
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
			System.out.println("文件大小:" + fileSizeString);
			if (((double) fileS / 1073741824) >= 10240) {
				// ==等于10M,或者大于10M。
				// 直接返回
				return false;
			}
		}
		// 10M=10240KB
		return status;
	}
}
