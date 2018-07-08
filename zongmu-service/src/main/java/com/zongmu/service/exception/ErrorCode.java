package com.zongmu.service.exception;

public class ErrorCode {

	public final static String General_Error = "00001";
	public final static String Socket_Connection_Error = "00006";
	public final static String TASK_NULL = "10000";

	public final static String TAG_ITEM_NULL = "20001";
	public final static String ASSET_NOT_FOUND = "20100";
	public final static String ASSET_TAG_EXIST = "20100";

	public final static String ASSET_FILE_EXIST = "20100";
	public final static String ASSET_FILE_NOT_FOUND = "20100";

	public final static String TASK_NOT_FOUND = "20200";
	public final static String TASK_RECORD_NOT_FOUND = "20200";
	public final static String TASK_RECORD_START_FAILED = "20200";
	public final static String TASKITEM_NOT_FOUND = "20200";
	public final static String TASKITEM_IS_IN_PROGRESS = "20200";
	public final static String REVIEW_RECORD_NOT_FOUND = "20200";

	public final static String UPLOAD_FOLDER_CREATE_FAILED = "30000";
	public final static String UPLOAD_FAILED = "30001";
	public final static String CREATE_FOLDER_FAILED = "30002";
	public final static String FTP_LOGIN_FAILED = "30003";
	public final static String FTP_CREATE_FOLDER_FAILED = "30004";
	public final static String FTP_LIST_FOLDER_FAILED = "30005";
	public final static String FTP_UPLOADED_FAILED = "30006";
	public final static String FTP_CONNECTED_FAILED = "30007";

	public final static String REJECT_REASON_EXIST = "21544";
	public final static String REJECT_REASON_NOT_EXIST = "20100";

	public final static String COLOR_TAG_NOT_EXIST = "20100";

	public final static String TRAIN_NOT_EXIST = "20100";

	public final static String Algorithm_IN_USING = "20400";

	public final static String TAG_IN_USING = "20500";
	public final static String TAGITEM_IN_USING = "20501";
	public final static String COLORTAG_IN_USING = "20502";
	public final static String ASSET_TAG_IN_USING = "20503";
	public final static String REJECT_REASON_IN_USING = "20504";
	public final static String TAG_EXIST = "20505";
	public final static String TAG_NOT_EXIST = "20506";
	public final static String COLOR_TAG_EXIST = "20507";
	public final static String Algorithm_NOT_EXIST = "20508";
	public final static String Algorithm_EXIST = "20509";
	public final static String Password_NOT_MATCH = "20510";
	public final static String USER_EXIST = "20511";
	public final static String USER_NOT_EXIST = "20512";
	public final static String USER_PASSWORD_INVALID = "20513";
	public final static String EMAIL_FORMAT_INVALID = "20514";
	public final static String ACTIVE_DATA_IS_OVERDUE = "20515";
	public final static String ACTIVE_CODE_INVALID = "20516";
	public final static String USER_IS_ACTIVE = "20517";
	public final static String USER_IS_NOT_ACTIVE = "20518";
	public final static String ACTIVE_USER_NOT_EXIST = "20519";
	public final static String COLOR_GROUP_NOT_EXIST = "20520";
	public final static String ASSET_TAG_UPDATE_FAILED = "20521";
	public final static String ASSET_CREATE_FAILED_NOT_SET_TAG = "20522";
	public final static String COLOR_GROUP_EXIST = "20523";
	public final static String RESET_DATA_IS_OVERDUE = "20524";
	public final static String RESET_ACTIVE_CODE_INVALID = "20525";
	public final static String RESET_NOT_ACTIVE = "20526";
	public final static String RESET_PASSWORD_NOT_MATCH = "20527";
	public final static String OLD_PASSWORD_EQ_NEW_PASSWORD = "20528";
	public final static String RESET_PASSWORD_INVALID = "20529";
	public final static String UPLOAD_USER_ICON_FAILED = "20530";
	public final static String SEND_MAIL_FAILED = "20531";
	public final static String ASSET_TAG_NOT_FOUND = "20532";
	public final static String POINT_REQUEST_PAY_NOT_ENOUGH = "20533";
	public final static String REJECT_TASK_COUNT_LARGER_THAN_10 = "20534";
	public final static String VIEW_TAG_NOT_FOUND = "20535";
	public final static String VIEW_TAG_NAME_EXISTS = "20536";
	public final static String VIEW_TAG_IS_USING = "20537";
	public final static String LOGIN_USER_IS_BLACK = "20538";
	public final static String VIEW_TAG_ITEM_NOT_FOUND = "20539";
	public final static String VIEW_TAG_ITEM_EXIST = "20540";
	public final static String ASSET_VIEW_TAG_EXIST = "20541";
	public final static String ASSET_VIEW_TAG_NOT_FOUND = "20542";
	public final static String File_Must_be_Four = "20543";
	public final static String Export_Failed = "20544";
	public final static String Train_Name_Dup = "30000";
	public final static String Pay_TranNO_dup = "30001";
	public final static String Export_Failed_System = "30002";
	public final static String Task_Not_Belongs_To = "30003";
	public final static String Task_Accept_By_Others = "30004";
	public final static String ASSET_NAME_DUP = "30005";
	public final static String TASK_NAME_DUP = "30006";
	public final static String LOGIN_USER_IS_LOCKEZD = "30007";
	public final static String Task_Time_Interval_Is_Negative = "30008";
	public final static String Read_Image_Height_And_Width_Failed = "30009";
}
