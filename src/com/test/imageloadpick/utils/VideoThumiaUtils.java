package com.test.imageloadpick.utils;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

/*
 * 视频缩略图加载类
 */
public class VideoThumiaUtils {
	private static VideoThumiaUtils mInstance;
	/**
	 * 图片缓存的核心对象
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	private static final int DEFAULT_THREAD_COUNT = 1;
	// 队列的调度方式
	private Types mType = Types.LIFO;
	// 任务队列
	private LinkedList<Runnable> mTaskQueue;
	// 后台轮询线程
	private Thread mPoolThread;
	private Handler mPoolThreadHandler;
	/*
	 * UI线程的Handler。
	 */
	private Handler mUIHandler;
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);// 信号的释放和获取,
	private Semaphore mSemaphoreThreadPool;
	private Context mContext;

	public enum Types {
		FIFO, LIFO;
	}

	private VideoThumiaUtils(int threadCount, Types type, Context context) {
		init(threadCount, type, context);
	}

	/**
	 * 
	 * @Title: init
	 * @Description: 初始化
	 */
	@SuppressLint("NewApi")
	private void init(int threadCount, Types type, Context context) {
		this.mContext = context;
		// 后台轮询线程
		mPoolThread = new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// 线程池取出一个任务去执行
						mThreadPool.execute(getTask());
						try {
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				};
				mSemaphorePoolThreadHandler.release();// 释放一个信号量
				Looper.loop();
			}

		};
		mPoolThread.start();
		// 获取我们应用的最大内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();// 返回每一行占用的内存大小
			}
		};
		// 创建线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount);
	}

	/**
	 * 
	 * @Title: getTask
	 * @Description: 从任务队列取出一个方法
	 */
	private Runnable getTask() {
		if (mType == Types.FIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == Types.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	public static VideoThumiaUtils getInstance(int threadCount, Types type,
			Context context) {
		if (mInstance == null) {
			synchronized (VideoThumiaUtils.class) {
				if (mInstance == null) {
					mInstance = new VideoThumiaUtils(threadCount, type, context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 
	 * @Title: loadImage
	 * @Description: 依据path为imageview设置图片
	 */
	public void loadVideoThumail(final String path, final ImageView imageView) {
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// 获取图片,为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bitmap = holder.bitmap;
					ImageView imageView = holder.imageView;
					String path = holder.path;
					// 将path和getTag()存储路径进行对比
					if (imageView.getTag().toString().equals(path)) {
						imageView.setImageBitmap(bitmap);
					}

				}
			};
		}
		// 图片
		// 根据Path从缓存中取出Bitmap
		Bitmap bm = getBitmapFromCache(path);

		if (bm != null) {
			refreashBitmap(path, imageView, bm);

		} else {
			// 缓存没有图片。
			addTask(new Runnable() {

				@Override
				public void run() {
					// 加载图片
					// 图片的压缩
					// 1,获得图片需要显示的大小

					// 2,压缩图片
					Bitmap bitmap = decodeSampleBitmapFromPath(path);
					// 把图片加载到缓存中
					addBitmapToLruCache(path, bitmap);
					refreashBitmap(path, imageView, bitmap);
					mSemaphoreThreadPool.release();
				}

			});
		}
	}

	private void refreashBitmap(final String path, final ImageView imageView,
			Bitmap bm) {
		Message message = Message.obtain();
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bm;
		holder.imageView = imageView;
		holder.path = path;
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}

	/*
	 * 将图片加入到缓存
	 */
	@SuppressLint("NewApi")
	protected void addBitmapToLruCache(String path, Bitmap bitmap) {
		if (getBitmapFromCache(path) == null) {
			if (bitmap != null) {
				mLruCache.put(path, bitmap);
			}
		}
	}

	/**
	 * 
	 * @Title: decodeSampleBitmapFromPath
	 * @Description:根据图片要显示的宽度和高度,对视频的缩略图图片进行压缩
	 */
	protected Bitmap decodeSampleBitmapFromPath(String path) {
		Bitmap bitmap = VideoThumailLoader.getVideoThumbnail(mContext,
				mContext.getContentResolver(), path);
		return bitmap;
	}

	private synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);
		try {
			if (mPoolThreadHandler == null) {
				mSemaphorePoolThreadHandler.acquire();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 
	 * @Title: getBitmapFromCache
	 * @Description: 根据Path从缓存中取出Bitmap
	 */
	@SuppressLint("NewApi")
	private Bitmap getBitmapFromCache(String key) {
		return mLruCache.get(key);
	}


	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}
}
