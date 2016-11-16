package com.test.imageloadpick.utils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/*
 * 图片加载类
 */
public class ImageLoader {
	private static ImageLoader mInstance;
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
	private Type mType = Type.LIFO;
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

	public enum Type {
		FIFO, LIFO;
	}

	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}

	/**
	 * 
	 * @Title: init
	 * @Description: 初始化
	 */
	@SuppressLint("NewApi")
	private void init(int threadCount, Type type) {
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
		if (mType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == Type.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	public static ImageLoader getInstance(int threadCount, Type type) {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(threadCount, type);
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
	public void loadImage(final String path, final ImageView imageView) {
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
			//图片
			// 根据Path从缓存中取出Bitmap
		Bitmap  bm = getBitmapFromCache(path);
			
		if (bm != null) {
			refreashBitmap(path, imageView, bm);

		} else {
			addTask(new Runnable() {

				@Override
				public void run() {
					// 加载图片
					// 图片的压缩
					// 1,获得图片需要显示的大小
					ImageSize size = getImageViewSize(imageView);
					
					// 2,压缩图片
					Bitmap bitmap = decodeSampleBitmapFromPath(path,
							size.width, size.height);
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
	 * @Description:根据图片要显示的宽度和高度,对图片进行压缩
	 */
	protected Bitmap decodeSampleBitmapFromPath(String path, int width,
			int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 设置true是想获得图片的宽度和高度。
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = caculateInSample(options, width, height);
		// 使用等到的inSampleSize再次获得图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/*
	 * 根据需求的宽度和高度和实际的宽度和高度计算出SampleSize。
	 */
	private int caculateInSample(Options options, int reqwidth, int reqheight) {
		int width = options.outWidth;
		int height = options.outHeight;
		int SampleSize = 1;
		if (width > reqwidth || height > reqheight) {
			int widthRadio = Math.round(width * 1.0f / reqwidth * 1.0f);// 去到压缩比例因子
			int heightRadio = Math.round(height * 1.0f / reqheight * 1.0f);
			// 节省内存,去最终的最大值。这里根据业务不同,采取不同的压缩方法策略
			SampleSize = Math.max(widthRadio, heightRadio);
		}
		return SampleSize;
	}

	// 根据imageview获取适当压缩的宽和高
	private ImageSize getImageViewSize(ImageView imageView) {
		DisplayMetrics displayMetrics = imageView.getContext().getResources()
				.getDisplayMetrics();// 获取屏幕的属性
		ImageSize size = new ImageSize();
		LayoutParams lp = imageView.getLayoutParams();
		int width = imageView.getWidth();// 获取imageview实际的宽度

		if (width <= 0) {
			width = lp.width;// 获取imageview在layout的宽度
		}
		if (width <= 0) {
			width = getImageViewFieldValue(imageView, "mMaxWidth");
			// width = imageView.getMaxWidth();// 检查最大值
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;// 赋值屏幕的宽度
		}
		int height = imageView.getHeight();// 获取imageview实际的宽度
		if (height <= 0) {
			height = lp.height;// 获取imageview在layout的宽度
		}
		if (height <= 0) {
			height = getImageViewFieldValue(imageView, "mMaxHeight");
			// height = imageView.getMaxHeight();// 检查最大值
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;// 赋值屏幕的宽度
		}
		size.height = height;
		size.width = width;
		return size;
	}

	/*
	 * 通过反射获取某个属性的值
	 */
	private static int getImageViewFieldValue(Object objcet, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(objcet);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
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

	private class ImageSize {
		int width;
		int height;
	}

	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}
}
