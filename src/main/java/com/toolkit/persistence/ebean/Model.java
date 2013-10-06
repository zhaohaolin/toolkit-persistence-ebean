/*
 * CopyRight (c) 2005-2012 GLOBE Co, Ltd. All rights reserved. 
 * Filename: Model.java 
 * Creator: qiaofeng Create-Date: 下午06:49:50
 */
package com.toolkit.persistence.ebean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * the base of model class
 * 
 * @author qiaofeng
 * @version $Id: Model, v 0.1 2012-11-2 下午06:49:50 Exp $
 */
@MappedSuperclass
public class Model extends BaseModel {
	
	@Id
	@GeneratedValue
	private Long	id;
	
	@Column(name = "create_time", insertable = true)
	private Date	createTime	= new Date();
	
	@Column(name = "modify_time", insertable = true)
	private Date	modifyTime	= new Date();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Object _key() {
		return getId();
	}
	
	public Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Date getModifyTime() {
		return this.modifyTime;
	}
	
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
}
