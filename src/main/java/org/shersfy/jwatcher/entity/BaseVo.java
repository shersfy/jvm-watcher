package org.shersfy.jwatcher.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VO基类
 */
public class BaseVo {
    
    protected static Logger logger = LoggerFactory.getLogger(BaseVo.class);
	
	private  BaseVo(){}
	
	/**
	 * 创建一个继承PO对象所有属性值的VO对象;<br/>
	 * 当VO的class对象为null时，返回null;<br/>
	 * 当PO为null时，创建一个没赋PO值的VO对象.
	 * 
	 * @param vo 值对象
	 * @param po 持久对象
	 * @return vo
	 */
	public static <V, P extends BaseEntity> V newVoInstance(Class<V> voClass, P po) {

		if(voClass == null ){
			return null;
		}
		
		V vo = null;
		try {
			
			vo = voClass.newInstance();
			// PO为null，创建一个没赋PO值的VO对象
			if(po == null){
				return vo;
			}
			
			Class<? extends BaseEntity> poClazz = po.getClass();//获取PO类对象，PO的父类是BaseEntity
			Class<?> sup = po.getClass().getSuperclass();// 获取PO父类的类对象

			Field[] fields = poClazz.getDeclaredFields();// 获取PO里定义的所有字段
			Field[] supFds = sup.getDeclaredFields();// 获取PO父类里定义的所有字段

			List<Field> fdlist = new ArrayList<Field>();

			for(Field f : fields){
				if("serialVersionUID".equals(f.getName()))
				{
					continue;
				}
				fdlist.add(f);
			}

			for(Field f : supFds){
				if("serialVersionUID".equals(f.getName()))
				{
					continue;
				}
				fdlist.add(f);
			}

			if(fdlist.size()==0){
				return vo;
			}

			for(Field f : fdlist){
				String name 	= f.getName();
				Object value	= null;
				
				Method getMethod = poClazz.getMethod(("boolean".equals(f.getType().getName())?"is":"get") + StringUtils.capitalize(name));
				value = getMethod.invoke(po);

				Method setMethod = voClass.getMethod("set" + StringUtils.capitalize(name), getMethod.getReturnType());
				setMethod.invoke(vo, value);

			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return vo;
	}

}
