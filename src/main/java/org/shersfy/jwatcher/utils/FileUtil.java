package org.shersfy.jwatcher.utils;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;


/**
 * 文件工具类
 * @author shersfy
 * @date 2018-02-27
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class FileUtil {

	public static final int BUFSIZE = 1024 * 8;
	
	private FileUtil(){}

	public static enum FileSizeUnit{
		Byte, KB, MB, GB, TB, PB, Auto;
		
		/**
		 * 计算单位
		 * @param len 文件大小, 单位byte(B)
		 * @return 返回单位
		 */
		public static FileSizeUnit countUnit(long len){
			
			if(len >= Math.pow(1024, FileSizeUnit.PB.ordinal())){
				return PB;
			}
			
			if(len >= Math.pow(1024, TB.ordinal())){
				return TB;
			}
			
			if(len >= Math.pow(1024, GB.ordinal())){
				return GB;
			}
			
			if(len >= Math.pow(1024, MB.ordinal())){
				return MB;
			}
			
			if(len >= Math.pow(1024, KB.ordinal())){
				return KB;
			}
			
			return Byte;
		}
	}
	
	
	
	/**
	 * 计算字节数
	 * 
	 * @param len 文件大小
	 * @param unit 单位
	 * @return 返回字节数(单位byte)
	 */
	public static long countBytes(long len, FileSizeUnit unit){
		if(unit == null || unit == FileSizeUnit.Auto){
			return len;
		}
		
		long blen = (long) Math.pow(1024, unit.ordinal());
		blen = len * blen;
		return blen;
	}
	
	/**
	 * 文件大小单位换算
	 * 
	 * @param file 文件
	 * @param unit 换算单位
	 * @return 返回指定单位换算后大小
	 */
	public static String getLength(File file, FileSizeUnit unit){
		if(file == null){
			return "0";
		}
		
		long len = file.length();
		return getLength(len, unit);
	}
	
	/**
	 * 文件大小单位换算
	 * 
	 * @param len 文件大小
	 * @param unit 单位
	 * @return 返回指定单位换算后大小
	 */
	public static String getLength(long len, FileSizeUnit unit){
		
		if(unit == null){
			return String.valueOf(len);
		}
		
		String lenStr = "0";
		if(FileSizeUnit.Auto == unit){
			unit = FileSizeUnit.countUnit(len);
		}
		lenStr = String.format("%.2f", len/Math.pow(1024, unit.ordinal()));
		
		return lenStr;
	}
	
	/**
	 * 文件大小单位换算
	 * 
	 * @param file 文件
	 * @param unit 换算单位
	 * @return 返回换算后带单位的大小
	 */
	public static String getLengthWithUnit(File file){
		if(file == null){
			return "0";
		}
		
		long len = file.length();
		
		FileSizeUnit unit = FileSizeUnit.countUnit(len);
		String lenStr = getLength(file, unit);
		lenStr = String.format("%s %s", lenStr, unit.name());
		return lenStr;
	}
	/**
	 * 文件大小单位换算
	 * 
	 * @param len 文件大小byte
	 * @return 返回换算后带单位的大小
	 */
	public static String getLengthWithUnit(long len){
		FileSizeUnit unit = FileSizeUnit.countUnit(len);
		String lenStr = getLength(len, unit);
		lenStr = String.format("%s %s", lenStr, unit.name());
		return lenStr;
	}
	/**
	 * Concatenates a filename to a base path using normal command line style rules.
	 * 
	 * @param basePath the base path to attach to, always treated as a path
	 * @param fullFilenameToAdd the filename (or path) to attach to the base
	 * @return the concatenated path, or null if invalid
	 */
	public static String concat(String basePath, String fullFilenameToAdd){
		String path = null;
		if(basePath!=null && fullFilenameToAdd!=null && 
				(fullFilenameToAdd.contains("~")
				|| fullFilenameToAdd.equals(".")
				|| fullFilenameToAdd.equals("..")
				)){
			if(basePath.endsWith("\\") || basePath.endsWith("/")){
				path = basePath + fullFilenameToAdd;
			} else {
				path = basePath + "/"+ fullFilenameToAdd;
			}
			return path;
		}
		if(StringUtils.startsWith(fullFilenameToAdd, "/") 
				|| StringUtils.startsWith(fullFilenameToAdd, "\\")){
			fullFilenameToAdd = fullFilenameToAdd.substring(1);
		}
		path = FilenameUtils.concat(basePath, fullFilenameToAdd);
		return replaceLinuxPath(path);
	}
	/***
	 * 替换为linux路径分隔符
	 * 
	 * @author PengYang
	 * @date 2017-09-28
	 * 
	 * @param path 文件路径
	 * @return 
	 */
	public static String replaceLinuxPath(String path){
		if(path == null){
			return null;
		}
		return path.replace(File.separatorChar, '/');
	}
	
	
}
