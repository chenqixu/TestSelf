package com.gnxdr.constant;

/**
 * @version V1.0.1 cqx modify by 68712-1 增加web_name解析
 * */
public class Constants {
	//常量
	public static final String SEPARATOR = "/";
	public static final String COMMA_SEPARATOR = ",";
	public static final String JOIN_KEY = "IP_ID";
	public static final String JOB_NAME_PREFIX = "GNXDR_TO_FILE_";
	public static final String JOB_DATAN = "JOB_DATAN";
	public static final String FILE_DATAN = "FILE_DATAN";
	public static final String ROWKEY_STARTTIME_FORMAT = "yyyyMMddHHmmss";
	public static final String ROWVALUE_STARTTIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String PERIOD_OF_TIME_FORMAT = "yyyyMMddHH";
	public static final String SUM_DATE_FORMAT = "yyyyMMdd";
	public static final String JOINED_OUTPUT_PREFIX = "PREFIX";
	public static final String ORACLE_TABLEFILE_IN_HDFS = "ORACLE_TABLEFILE_IN_HDFS";
	public static final String FILE_FILTER_REGEX="FILE_FILTER_REGEX";
	public static final String EXT_FILTER_REGEX="EXT_FILTER_REGEX";
	
	public static final String DEBUG = "DEBUG";
	
	//map输出错误文件前缀
	public static final String ERR_IP_FILE_FREFIX = "err_ip_";
	public static final String ERR_DNS_FILE_FREFIX = "err_dns_";
	public static final String ERR_EMAIL_FILE_FREFIX = "err_email_";
	public static final String ERR_FTP_FILE_FREFIX = "err_ftp_";
	public static final String ERR_HTTP_FILE_FREFIX = "err_http_";
	public static final String ERR_IM_FILE_FREFIX = "err_im_";
	public static final String ERR_MMS_FILE_FREFIX = "err_mms_";
	public static final String ERR_P2P_FILE_FREFIX = "err_p2p_";
	public static final String ERR_RTSP_FILE_FREFIX = "err_rtsp_";
	public static final String ERR_VOIP_FILE_FREFIX = "err_voip_";
	
	//map标识
	public static final String IP_FILE_FLAG = "IP";
	public static final String DNS_FILE_FLAG = "DNS";
	public static final String EMAIL_FILE_FLAG = "EMAIL";
	public static final String FTP_FILE_FLAG = "FTP";
	public static final String HTTP_FILE_FLAG = "HTTP";
	public static final String IM_FILE_FLAG = "IM";
	public static final String MMS_FILE_FLAG = "MMS";
	public static final String P2P_FILE_FLAG = "P2P";
	public static final String RTSP_FILE_FLAG = "RTSP";
	public static final String VOIP_FILE_FLAG = "VOIP";
	
	//程序中赋值
	public static final String TASK_HOUR = "TASK_HOUR";
	public static final String TASK_DATE = "TASK_DATE";
	
	//路径
	public static final String INPUT_IP_FILE_PATH = "INPUT_IP_FILE_PATH";
	public static final String INPUT_DNS_FILE_PATH = "INPUT_DNS_FILE_PATH";
	public static final String INPUT_EMAIL_FILE_PATH = "INPUT_EMAIL_FILE_PATH";
	public static final String INPUT_FTP_FILE_PATH = "INPUT_FTP_FILE_PATH";
	public static final String INPUT_HTTP_FILE_PATH = "INPUT_HTTP_FILE_PATH";
	public static final String INPUT_IM_FILE_PATH = "INPUT_IM_FILE_PATH";
	public static final String INPUT_MMS_FILE_PATH = "INPUT_MMS_FILE_PATH";
	public static final String INPUT_P2P_FILE_PATH = "INPUT_P2P_FILE_PATH";
	public static final String INPUT_RTSP_FILE_PATH = "INPUT_RTSP_FILE_PATH";
	public static final String INPUT_VOIP_FILE_PATH = "INPUT_VOIP_FILE_PATH";
	public static final String FILE_OUTPUT_PATH = "FILE_OUTPUT_PATH";
	public static final String FULL_OUTPUT_PATH = "FULL_OUTPUT_PATH";
	public static final String TMP_PATH = "TMP_PATH";
	public static final String FULL_TMP_PATH = "FULL_TMP_PATH";
	public static final String ERROR_OUTPUT_PATH = "ERROR_OUTPUT_PATH";
	public static final String FULL_ERROR_OUTPUT_PATH = "FULL_ERROR_OUTPUT_PATH";
	public static final String HBASE_OUTPUT_FILE = "HBASE_OUTPUT_FILE";
	public static final String FULL_HBASE_OUTPUT_FILE = "FULL_HBASE_OUTPUT_FILE";
	
	//Map输出管道命名(测试用)
	public static final String ERR_OUT_PIPE_NAME = "ERROUTPIPENAME";
	public static final String IP_OUT_PIPE_NAME = "IPOUTPIPENAME";
	public static final String DNS_OUT_PIPE_NAME = "DNSOUTPIPENAME";
	public static final String EMAIL_OUT_PIPE_NAME = "EMAILOUTPIPENAME";
	public static final String FTP_OUT_PIPE_NAME = "FTPOUTPIPENAME";
	public static final String HTTP_OUT_PIPE_NAME = "HTTPOUTPIPENAME";
	public static final String IM_OUT_PIPE_NAME = "IMOUTPIPENAME";
	public static final String MMS_OUT_PIPE_NAME = "MMSOUTPIPENAME";
	public static final String P2P_OUT_PIPE_NAME = "P2POUTPIPENAME";
	public static final String RTSP_OUT_PIPE_NAME = "RTSPOUTPIPENAME";
	public static final String VOIP_OUT_PIPE_NAME = "VOIPOUTPIPENAME";
	//Reduce输出管道命名
	public static final String FILE_OUT_PIPE_NAME = "FILEOUTPIPENAME";

	//输入文件字段
	public static final String GN_XDR_IP_FIELD = "GN_XDR_IP_FIELD";
	public static final String GN_XDR_DNS_FIELD = "GN_XDR_DNS_FIELD";
	public static final String GN_XDR_EMAIL_FIELD = "GN_XDR_EMAIL_FIELD";
	public static final String GN_XDR_FTP_FIELD = "GN_XDR_FTP_FIELD";
	public static final String GN_XDR_HTTP_FIELD = "GN_XDR_HTTP_FIELD";
	public static final String GN_XDR_IM_FIELD = "GN_XDR_IM_FIELD";
	public static final String GN_XDR_MMS_FIELD = "GN_XDR_MMS_FIELD";
	public static final String GN_XDR_P2P_FIELD = "GN_XDR_P2P_FIELD";
	public static final String GN_XDR_RTSP_FIELD = "GN_XDR_RTSP_FIELD";
	public static final String GN_XDR_VOIP_FIELD = "GN_XDR_VOIP_FIELD";
	
	//map输出字段
	public static final String MAP_IP_TRANSPORT = "MAP_IP_TRANSPORT";
	public static final String MAP_DNS_TRANSPORT = "MAP_DNS_TRANSPORT";
	public static final String MAP_EMAIL_TRANSPORT = "MAP_EMAIL_TRANSPORT";
	public static final String MAP_FTP_TRANSPORT = "MAP_FTP_TRANSPORT";
	public static final String MAP_HTTP_TRANSPORT = "MAP_HTTP_TRANSPORT";
	public static final String MAP_IM_TRANSPORT = "MAP_IM_TRANSPORT";
	public static final String MAP_MMS_TRANSPORT = "MAP_MMS_TRANSPORT";
	public static final String MAP_P2P_TRANSPORT = "MAP_P2P_TRANSPORT";
	public static final String MAP_RTSP_TRANSPORT = "MAP_RTSP_TRANSPORT";
	public static final String MAP_VOIP_TRANSPORT = "MAP_VOIP_TRANSPORT";
	public static final String APP_NAME_TABLE = "APP_NAME_TABLE";
	public static final String WEB_NAME_TABLE = "WEB_NAME_TABLE";//V1.0.1
	
	
	
	
	public static enum COUNTER_ENUM {
		ipMapperErrorCounterEnum,
		dnsMapperErrorCounterEnum,
		emailMapperErrorCounterEnum,
		ftpMapperErrorCounterEnum,
		httpMapperErrorCounterEnum,
		imMapperErrorCounterEnum,
		mmsMapperErrorCounterEnum,
		p2pMapperErrorCounterEnum,
		rtspMapperErrorCounterEnum,
		voipMapperErrorCounterEnum,
		
		mapTransportErrorCounterEnum,
		mapIpTransportErrorCounter,
		numFormatErrorCounterEnum,
		hbasefileSuccessCounterEnum,
		errorCounterEnum,
		joinedCounterEnum,
		unjoinedCounterEnum,
		unjoinedhttpCounterEnum,
		counter1Enum,
		counter2Enum,
		counter3Enum
	}
}
