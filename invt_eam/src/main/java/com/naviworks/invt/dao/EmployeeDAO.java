package com.naviworks.invt.dao;

import com.naviworks.invt.model.EmployeeInfo;

public interface EmployeeDAO
{
	EmployeeInfo getInfoByName(String name);
	EmployeeInfo getInfoByCID(String cid);
	EmployeeInfo getInfoByFlag(String flag);
}
