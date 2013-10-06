/*
 * CopyRight (c) 2005-2012 GLOBE Co, Ltd. All rights reserved. Filename: ModelUtils.java Creator: qiaofeng Create-Date: 下午12:49:12
 */
package com.toolkit.persistence.ebean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: ModelUtils, v 0.1 2012-11-2 下午12:49:12 Exp $
 */
public abstract class ModelUtils {
	
	private static final Logger	logger	= LoggerFactory
												.getLogger(ModelUtils.class);
	
	public ModelUtils() {
		//
	}
	
	final static public List<Class<?>> scanModelPackage(
			Collection<String> packages) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (null != packages) {
			for (String pkgName : packages) {
				try {
					String[] clsNames = PackageUtil.findClassesInPackage(
							pkgName, null, null);
					for (String clsName : clsNames) {
						try {
							ClassLoader cl = Thread.currentThread()
									.getContextClassLoader();
							if (logger.isDebugEnabled()) {
								logger.debug("using ClassLoader {" + cl
										+ "} to load Class {" + clsName + "}");
							}
							Class<?> cls = cl.loadClass(clsName);
							Entity attr = cls.getAnnotation(Entity.class);
							if (null != attr) {
								classes.add(cls);
								logger.info("model class: add Entity Model:=>"
										+ cls);
							}
						} catch (ClassNotFoundException e) {
							logger.error("scan model class: ", e);
						}
					}
				} catch (IOException e) {
					logger.error("scan model class: ", e);
				}
			}
		}
		return classes;
	}
}
