package com.ratata.nativeQueryRest.utils;

public interface Const {
	public static final String QUERYMODE_SINGLE = "S";
	public static final String QUERYMODE_LIST = "L";
	public static final String QUERYMODE_MODIFY = "M";

	public static final String PARAM_MERGEARRAY = "mergeArray";

	public static final String PARAM_QUERY = "query";
	public static final String PARAM_ISNATIVE = "isNative";
	public static final String LINK_QUERY = "linkQuery";
	public static final String PARAM_CLASSNAME = "className";
	public static final String PARAM_RESULTSET = "resultSet";
	public static final String PARAM_QUERYMODE = "queryMode";
	public static final String LOCKMODETYPE = "lockMode";
	public static final String PARAM_OFFSET = "offset";
	public static final String PARAM_LIMIT = "limit";

	public static final String PARAM_QUERYNAME = "queryName";

	public static final String PARAM_PASSPARAM = "passParam";
	public static final String PARAM_DATA = "data";
	public static final String PARAM_INSIDEOBJECT = "insideObject";

	public static final String PARAM_SINGLEREQUEST_DATA = "data";
	public static final String PARAM_SINGLEREQUEST_PARAM = "param";

	public static final String LINK_QUERY_QUERYNOTEXIST = "query not exist";
	public static final String LINK_QUERY_QUERYLISTEMPTY = "please insert query list first";
	public static final String LINK_QUERY_INITFILENAME = "initQueryList.txt";
}
