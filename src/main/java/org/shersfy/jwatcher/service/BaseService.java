package org.shersfy.jwatcher.service;

import org.shersfy.jwatcher.beans.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseService implements IBaseServic{

	protected Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
	/**处理成功**/
	protected static final int SUCESS 	= ResultCode.SUCESS;
	/**处理失败**/
	protected static final int FAIL 	= ResultCode.FAIL;
}
