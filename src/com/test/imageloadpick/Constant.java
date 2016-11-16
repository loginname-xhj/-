package com.test.imageloadpick;


import android.os.Environment;

/**
 * 常用字符串文件类
 * 
 * @author dou
 * 
 */
public class Constant {

	/**
	 * 内网数据url 需要8080端口的数据 //服务器IP //更新包 //servlet上存储数据 //上传头像地址
	 */
	public static String SERVER_HOST = "172.28.1.5";
	// public static final String SERVER_UPDATE_URL = "http://" + SERVER_HOST
	// + "/UploadFiles/upload/update.xml";
	// public static String SERVER_LOAD_POST = "http://" + SERVER_HOST
	// + "/UploadFiles/UploadFileServlet";
	// public static final String SERVER_FILES_URL = "http://" + SERVER_HOST
	// + "/UploadFiles/upload/";
	/**
	 * 外网数据URl //服务器IP //更新包 //servlet上存储数据 //上传头像地址
	 */
	// public static String SERVER_HOST =
	// "172.28.1.5";http://118.26.167.246:9090/plugins/sample/getuser?userId=
	// public static String SERVER_HOST = "118.26.167.246";// 应急通外网地址
	// public static String SERVER_HOST = "210.51.163.234";
	// public static String SERVER_HOST = 192.168.3.134
	// public static String SERVER_HOST = "192.168.3.134";

	// 新应急通讯录获取地址
	public static final String GET_MECHANISM = "http://" + SERVER_HOST
			+ ":9090/plugins/txl/showtxl?method=mec";

	public static final String GET_OFFICE = "http://" + SERVER_HOST
			+ ":9090/plugins/txl/showtxl?method=off";

	public static final String GET_PERSON = "http://" + SERVER_HOST
			+ ":9090/plugins/txl/showtxl?method=per";

	public static final String SERVER_UPDATE_URL = "http://" + SERVER_HOST
			+ ":9090/plugins/upload/getnewapk";
	public static String SERVER_LOAD_POST = "http://" + SERVER_HOST
			+ ":80/UploadFiles/UploadFileServlet";
	public static final String SERVER_FILES_URL = "http://" + SERVER_HOST
			+ "/UploadFiles/upload/";
	// 有网查看个人信息
	public static final String URL_PERSIONINFO = "http://" + SERVER_HOST
			+ ":9090/plugins/phonebook/getphonebook?method=vcard&username=";
	// 上传头像路径
	public static final String URL_UPLOADHEADPHOTO = "http://" + SERVER_HOST
			+ ":9090/plugins/phonebook/loadphoto";
	// 获取联系人组织结构的地址
	public static String url_org = "http://" + SERVER_HOST
			+ ":9090/plugins/deptChart/ofgroup?method=limitedlist&username=";
	// 获取联系人的地址
	public static String url_straff = "http://"
			+ SERVER_HOST
			+ ":9090/plugins/deptChart/findusersthruuser?method=jsonlist&username=";
	// 获取应急通人员人员的地址
	public static final String URL_GETORG = "http://" + SERVER_HOST
			+ ":9090/plugins/upload/getownuser";
	// 获取vcard的信息
	public static final String URL_GETVCARDS = "http://" + SERVER_HOST
			+ ":9090/plugins/phonebook/getphonebook?method=allvcards";

	// 获取应急通组织结构的地址
	public static final String URL_GETORGSTAFF = "http://" + SERVER_HOST
			+ ":9090/plugins/upload/getownuser?requestId=1";
	// 一键上报
	public static final String URL_ONEUP = "http://" + SERVER_HOST
			+ ":9090/plugins/report/newreportservlet?method=new";
	// 一键上报获取当前用户属于哪个单位的标识符
	public static final String URL_USERUNIT = "http://" + SERVER_HOST
			+ ":9090/plugins/report/newreportservlet?method=gettype&user=";;
	// 一键上报获取上报日志
	public static final String URL_ONEUP_LOG = "http://" + SERVER_HOST
			+ ":9090/plugins/report/getreport?userid=";
	// 一键上报保存转存储的URL
	public static final String URL_ONEUP_STATUS = "http://" + SERVER_HOST
			+ ":9090/plugins/report/reportservlet?method=submit&id=";
	// servlet上获取通讯录的角色数据
	public static String SERVER_ADDRESS = "http://" + SERVER_HOST
			+ ":9090/plugins/sample/getuser?userId=";
	// 获取已加入过的群列表////或者群成员url
	public static String SERVER_QUNINFOGET = "http://" + SERVER_HOST
			+ ":9090/plugins/sample/getroom";
	public static String ZSYJ = "http://" + SERVER_HOST
			+ ":9090/plugins/sample/emservlet";
	// 舆情获取标题
	public static String SERVER_OPINION = "http://" + SERVER_HOST
			+ ":9090/plugins/publicoptions/pubopserverlet?method=list&type=";
	// 舆情详情获取
	public static String SERVER_OPINION_DETAILS = "http://"
			+ SERVER_HOST
			+ ":9090/plugins/publicoptions/pubopserverlet?method=getContent&id=";

	public static String SERVER_NAME = "www.taiji.com.cn";
	// 存储已经加入过群名称的数据库名
	public static final String groupdatabase = "QunDataBase";
	// 存储组织机构和机构成员的库
	public static final String Organizationdatabase = "OrgDataBase";
	public static int SERVER_PORT = 5222;
	public static final int GET_VERSION = 8;
	public static final int NOTGET_VERSION = 8888;

	public static final String ROSTER_DELETED = "roster.deleted";
	public static final String ROSTER_DELETED_KEY = "roster.deleted.key";

	/**
	 * 花名册有删除的ACTION和KEY
	 */
	public static final String ROSTER_UPDATED = "roster.updated";
	public static final String ROSTER_UPDATED_KEY = "roster.updated.key";
	/**
	 * 花名册有更新的ACTION和KEY
	 */
	public static final String ROSTER_ADDED = "roster.added";
	public static final String ROSTER_ADDED_KEY = "roster.added.key";

	/**
	 * 收到好友邀请请求
	 */
	public static final String ROSTER_PRESENCE_CHANGED = "roster.presence.changed";
	public static final String ROSTER_PRESENCE_CHANGED_KEY = "roster.presence.changed.key";

	public static final String ROSTER_SUBSCRIPTION = "roster.subscribe";
	public static final String ROSTER_SUB_FROM = "roster.subscribe.from";
	public static final String NOTICE_ID = "notice.id";

	public static final String NEW_MESSAGE_ACTION = "roster.newmessage";

	/**
	 * 我的消息
	 */
	public static final String MY_NEWS = "my.news";
	public static final String MY_NEWS_DATE = "my.news.date";
	/**
	 * 服务器的配置
	 */
	public static final String LOGIN_SET = "login_set"; // 登录设置
	public static final String USERNAME = "username"; // 账户
	public static final String PASSWORD = "password"; // 密码
	public static final String XMPP_HOST = "xmpp_host"; // 地址
	public static final String XMPP_PORT = "xmpp_port"; // 端口
	public static final String XMPP_SEIVICE_NAME = "xmpp_service_name"; // 服务名
	public static final String IS_AUTOLOGIN = "isAutoLogin"; // 是否自动登录
	public static final String IS_NOVISIBLE = "isNovisible"; // 是否隐身
	public static final String IS_REMEMBER = "isRemember"; // 是否记住账户密码
	public static final String IS_FIRSTSTART = "isFirstStart"; // 是否首次启动
	/**
	 * 登录提示
	 */
	public static final int LOGIN_SECCESS = 0;
	public static final int HAS_NEW_VERSION = 1;
	public static final int IS_NEW_VERSION = 2;
	public static final int LOGIN_ERROR_ACCOUNT_PASS = 3;
	public static final int SERVER_UNAVAILABLE = 4;
	public static final int LOGIN_ERROR = 5;

	public static final String XMPP_CONNECTION_CLOSED = "xmpp_connection_closed";

	public static final String LOGIN = "login";
	public static final String RELOGIN = "relogin";

	public static final String ALL_FRIEND = "所有好友";
	public static final String NO_GROUP_FRIEND = "未分组好友";
	/**
	 * 系统消息
	 */
	public static final String ADMIN_CMESSAGE_UPDATEAPP = "update";
	public static final String ACTION_SYS_MSG = "action_sys_msg";
	public static final String MSG_TYPE = "broadcast";
	public static final String SYS_MSG = "sysMsg";
	public static final String SYS_MSG_DIS = "系统消息";
	public static final String ADD_FRIEND_QEQUEST = "好友请求";
	/**
	 * 请求某个操作返回的状态值
	 */
	public static final int SUCCESS = 0; // 存在
	public static final int FAIL = 1; // 不存在
	public static final int UNKNOWERROR = 2; // 出现莫名的错误.
	public static final int NETWORKERROR = 3; // 网络错误
	/***
	 * 通讯录根据用户ｉｄ和用户名去查找人员中的请求ｘｍｌ是否包含自组织
	 */
	public static final int containsZz = 0;
	/***
	 * 创建请求分组联系人列表xml分页参数
	 */
	public static final String currentpage = "1"; // //
													// 当前第几页
	public static final String pagesize = "1000"; // 当前页的条数

	/***
	 * 创建请求xml操作类型
	 */
	public static final String add = "00";
	public static final String rename = "01";
	public static final String remove = "02";

	/**
	 * 重连接
	 */
	/**
	 * 重连接状态acttion
	 * 
	 */
	public static final String ACTION_RECONNECT_STATE = "action_reconnect_state";
	/**
	 * 描述冲连接状态的关机子，寄放的intent的关键字
	 */
	public static final String RECONNECT_STATE = "reconnect_state";
	/**
	 * 描述冲连接，
	 */
	public static final boolean RECONNECT_STATE_SUCCESS = true;
	public static final boolean RECONNECT_STATE_FAIL = false;
	/**
	 * 是否在线的SharedPreferences名称
	 */
	public static final String PREFENCE_USER_STATE = "prefence_user_state";
	public static final String IS_ONLINE = "is_online";
	/**
	 * 精确到毫秒
	 */
	public static final String MS_FORMART = "yyyy-MM-dd HH:mm:ss SSS";

	public static boolean isYS = false;

	// 定位服务
	public static final String LOCATION = "location";
	public static final String LOCATION_ACTION = "locationAction";
	// 一键上报获取上报日志
	// 头像文件路径
	// 存放所有图片的路径，头像缩略图，原图
	public static String CAMER_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/yjchat/headPhoto";
	// 之前遗留下来的文件夹存储图片
	public static String CAMER_ROOT_ERROR_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/chat/headPhoto";
	// 获取通讯录变化时间
	public static String SERVER_ADRESS_CHAGE = "http://" + SERVER_HOST
			+ ":9090/plugins/phonebook/getphonebook?method=lasttime";
	// 消息下达 更新后台是否查看数据
	public static String SERVER_ANNEX_ISREAD = "http://" + SERVER_HOST
			+ ":9090/plugins/sendmessage/sendnewservlet?method=setreader";
	// 執行更新指令之後反饋給後臺
	public static String UPDATEAFTER_APP = "http://" + SERVER_HOST
			+ ":9090/plugins/sendmessage/promptservlet?message=";
	// 用于标识应急管理通讯录插入是否已经数据库的Action
	public static final String ACTION_INSERT_CONRACT_YJ_FLAG = "action_insert_contact_yj";
	// 成功与否的标示
	public static boolean INSERT_SUCCESS = false;
	// 转发联系人人数上限
	public static int totle = 4;
	public static String exten_name = "_thu.jpg";
	// 转发附件的上限
	public static int select_count = 5;
	// 刷新常用联系人Action
	public final static String action_notifity_commoncotract = "action.commoncotract.changed";

	// 将程序异常信息捕获，上传异常信息到服务器上。服务器的地址
	public static String SERVER_EXCEPTION_URL = "http://" + SERVER_HOST
			+ ":80/SystemCrashesLog/logservlet";
}
