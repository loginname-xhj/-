package com.test.imageloadpick;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.imageloadpick.adapter.ImageAdapter;
import com.test.imageloadpick.adapter.ImageAdapter.InItSelectOnlistener;
import com.test.imageloadpick.bean.FolderBean;
import com.test.imageloadpick.view.ListImageDirPopupWindow;
import com.test.imageloadpick.view.ListImageDirPopupWindow.OnDirSelectedListener;


public class MainActivity extends Activity
{
	private GridView mGridView;
	private List<String> mImgs;
	private ImageAdapter mImageAdapter;
	private RelativeLayout mBootomly;
	private TextView mDirName;
	private TextView mDirCount;
	private File mCurrentDir;
	private int mMaxCount;
	private int mMaxVideoCount;
	private List<FolderBean> mFBeans;
	private ProgressDialog mProgressDialog;
	private ListImageDirPopupWindow mDirPopupWindow;
	private ImageView btn_back;
	private TextView tv_select_modes_type;
	private Button commit;
	// ------------------第三方传进来的参数。用于确定一些参数配置
	private boolean showVideo;// 是否展示系统的视频信息
	private boolean showImage;// 是否展示系统的图库信息
	private int select_file_mount = 5;// 控制选择文件的总和。不能超过这个数目默认是5个

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mProgressDialog.dismiss();
			// 绑定数据到view中。
			data2View();
			initDirPopupWindow();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_main_select);
		Intent intent = getIntent();// 传递过来的取出配置参数。
		showVideo = intent.getBooleanExtra("showVideo", false);// 默认视频不展示
		showImage = intent.getBooleanExtra("showImage", true);// 默认图片展示。
		select_file_mount = intent.getIntExtra("select_file_mount", 5);
		initView();
		initDatas();
		initEvent();
	}

	protected void initDirPopupWindow()
	{
		mDirPopupWindow = new ListImageDirPopupWindow(MainActivity.this,
				mFBeans);

		// ppWindows点击其他区域的事件。
		mDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				lightOn();
			}
		});
		mDirPopupWindow.setmListener(new OnDirSelectedListener()
		{

			@Override
			public void onSelected(FolderBean folderBean)
			{
				if (folderBean.getType() == 1)
				{
					// 图片
					tv_select_modes_type.setText("图片");
					mCurrentDir = new File(folderBean.getDir());
					mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter()
					{

						@Override
						public boolean accept(File dir, String filename)
						{
							// 图片过滤器.符合jpg,jpeg,png
							if (filename.endsWith("jpg")
									|| filename.endsWith("png"))
							{
								return true;
							}
							return false;
						}
					}));
					mImageAdapter = new ImageAdapter(MainActivity.this, ListSort(mImgs),
							mCurrentDir.getAbsolutePath(), 1,
							new MyInItSelectOnlistener(), select_file_mount);
					mGridView.setAdapter(mImageAdapter);
					mDirName.setText(folderBean.getName());
					mDirCount.setText(mImgs.size() + "");
					mDirPopupWindow.dismiss();
				} else if (folderBean.getType() == 2)
				{
					tv_select_modes_type.setText("视频");
					// 视频
					mCurrentDir = new File(folderBean.getDir());
					mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter()
					{

						@Override
						public boolean accept(File dir, String filename)
						{
							// 视频过滤器.符合mp3,3gp,mp4
							if (filename.endsWith("mp4"))
							{
								return true;
							}
							return false;
						}
					}));

					mImageAdapter = new ImageAdapter(MainActivity.this, ListSort(mImgs),
							mCurrentDir.getAbsolutePath(), 2,
							new MyInItSelectOnlistener(), select_file_mount);
					mGridView.setAdapter(mImageAdapter);
					mDirName.setText(folderBean.getName());
					mDirCount.setText(mImgs.size() + "");
					mDirPopupWindow.dismiss();
				}

			}
		});
	}

	/*
	 * 内容区域变亮
	 */
	protected void lightOn()
	{
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}

	protected void data2View()
	{
		if (mCurrentDir == null)
		{
			Toast.makeText(MainActivity.this, "没有扫描到任何图片", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter()
		{

			@Override
			public boolean accept(File dir, String filename)
			{
				// 图片过滤器.符合jpg,jpeg,png
				if (filename.endsWith("jpg") || filename.endsWith("png"))
				{
					return true;
				}
				return false;
			}
		}));
		mImageAdapter = new ImageAdapter(MainActivity.this, ListSort(mImgs),
				mCurrentDir.getAbsolutePath(), 1, new MyInItSelectOnlistener(),
				select_file_mount);
		mGridView.setAdapter(mImageAdapter);
		mDirName.setText(mCurrentDir.getName());
		mDirCount.setText(mMaxCount + "");
	}

	private void initEvent()
	{
		mBootomly.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
				mDirPopupWindow.showAsDropDown(mBootomly, 0, 0);
				lightOff();
			}
		});
		btn_back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				ImageAdapter.mSelectImg.clear();
				finish();
			}
		});
		commit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (mImageAdapter.mSelectImg.size() > 0)
				{
					Iterator<String> list = mImageAdapter.mSelectImg.iterator();
					ArrayList<String> list_path = new ArrayList<String>();
					while (list.hasNext())
					{
						list_path.add(list.next());
					}
					mImageAdapter.mSelectImg.clear();
					Intent intent = new Intent();
					intent.putStringArrayListExtra("file_path", list_path);
					setResult(Activity.RESULT_OK, intent);
					finish();
				} else
				{
					Toast.makeText(MainActivity.this, "请选择图片",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	protected void lightOff()
	{

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.3f;
		getWindow().setAttributes(lp);
	}

	// 利用ContentProvider扫描手机里面的所有图片
	private void initDatas()
	{
		if (!(Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)))
		{
			Toast.makeText(MainActivity.this, "当前存储卡不能用", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mProgressDialog = ProgressDialog.show(MainActivity.this, null,
				"正在加载...");
		new Thread()
		{

			@Override
			public void run()
			{
				ContentResolver cr = MainActivity.this.getContentResolver();
				Set<String> mDirPaths = new HashSet<String>();
				if (showImage)
				{
					Uri mImaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					Cursor cursor = cr.query(mImaUri, null,
							MediaStore.Images.Media.MIME_TYPE + "=? or "
									+ MediaStore.Images.Media.MIME_TYPE + "=?",
							new String[]
							{ "image/jpeg", "image/png" },
							MediaStore.Images.Media.DATE_MODIFIED + " desc");
					while (cursor.moveToNext())
					{
						String path = cursor.getString(cursor
								.getColumnIndex(MediaStore.Images.Media.DATA));
						File parentFile = new File(path).getParentFile();
						if (parentFile == null)
							continue;
						String dirpath = parentFile.getAbsolutePath();
						if (Constant.CAMER_ROOT_PATH.equals(dirpath)
								|| Constant.CAMER_ROOT_ERROR_PATH
										.equals(dirpath))
						{

							continue;
						}

						if (dirpath.contains("/DCIM/Camera"))
						{
							// 系统相册
						} else
						{
							// 其他目录
							continue;
						}
						FolderBean folderBean = null;
						if (mDirPaths.contains(dirpath))
						{
							continue;
						} else
						{
							mDirPaths.add(dirpath);
							folderBean = new FolderBean();
							folderBean.setDir(dirpath);
							folderBean.setFirstImgPath(path);
						}
						if (parentFile.list() == null)
							continue;
						int picSize = parentFile.list(new FilenameFilter()
						{

							@Override
							public boolean accept(File dir, String filename)
							{
								// 图片过滤器.符合jpg,jpeg,png
								if (filename.endsWith("jpg")
										|| filename.endsWith("jpeg")
										|| filename.endsWith("png"))
								{
									return true;
								}
								return false;
							}
						}).length;
						if (picSize > 0)
						{
							// 有图片
							folderBean.setCount(picSize);
							folderBean.setType(1);
							mFBeans.add(folderBean);
							if (picSize > mMaxCount)
							{
								mMaxCount = picSize;
								mCurrentDir = parentFile;
							}
						}

					}
					cursor.close();
				}
				// 扫描视频
				 if (showVideo) {
				String[] pp =
				{ "_id", "_data" };
				Uri media_uri = Uri
						.parse("content://media/external/video/media");
				Cursor cc = cr.query(media_uri, pp, null, null, null);
				Set<String> mVideoDirPaths = new HashSet<String>();
				while (cc.moveToNext())
				{
					String path = cc.getString(1);
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirpath = parentFile.getAbsolutePath();
					FolderBean folderBean = null;
					if (mVideoDirPaths.contains(dirpath))
					{
						continue;
					} else
					{
						mVideoDirPaths.add(dirpath);
						folderBean = new FolderBean();
						folderBean.setDir(dirpath);
						folderBean.setFirstImgPath(path);
					}
					if (parentFile.list() == null)
						continue;
					int picSize = parentFile.list(new FilenameFilter()
					{

						@Override
						public boolean accept(File dir, String filename)
						{
							// 视频过滤器
							if (filename.endsWith("mp4"))
							{
								return true;
							}
							return false;
						}
					}).length;
					if (picSize > 0)
					{
						// 扫描目录为空
						folderBean.setCount(picSize);
						folderBean.setType(2);
						mFBeans.add(folderBean);
						if (picSize > mMaxCount)
						{
							mMaxCount = picSize;
							// mCurrentDir = parentFile;
						}
					}

				}
				cc.close();
				 }

				// 扫描完成,释放变量中的内存
				mDirPaths = null;
				// 通知handler,图片扫描完成
				mHandler.sendEmptyMessage(0x0110);
			}

		}.start();

	}

	private void initView()
	{
		btn_back = (ImageView) findViewById(R.id.btn_back);
		tv_select_modes_type = (TextView) findViewById(R.id.tv_select_modes_type);
		commit = (Button) findViewById(R.id.commit);

		mGridView = (GridView) findViewById(R.id.id_gridview);
		mBootomly = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		mDirName = (TextView) findViewById(R.id.id_dir_name);
		mDirCount = (TextView) findViewById(R.id.id_dir_count);
		mFBeans = new ArrayList<FolderBean>();
	}

	private class MyInItSelectOnlistener implements InItSelectOnlistener
	{

		@Override
		public void initselect(Set<String> mSelectImgdate)
		{
			if (mSelectImgdate.size() == 0)
			{
				commit.setText("完成");
			} else
			{
				commit.setText("(" + mSelectImgdate.size() + "/"
						+ select_file_mount + ")" + "完成");
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mImageAdapter.mSelectImg.clear();

	}

	/**
	 * 进行排序
	 * 
	 * @param list
	 * @return
	 */
	public List<String> ListSort(List<String> list)
	{
		List<String> lists = new ArrayList<String>();
		if (list == null || list.size() == 0)
		{
			return lists;
		}
		for (int i = list.size() - 1; i >= 0; i--)
		{
			lists.add(list.get(i));
		}
		return lists;
	}

}
