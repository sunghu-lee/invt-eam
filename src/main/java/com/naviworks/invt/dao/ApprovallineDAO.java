package com.naviworks.invt.dao;

import com.naviworks.invt.model.ApprovallineInfo;

public interface ApprovallineDAO
{
	ApprovallineInfo getInfoByLineInfo(String LineID);
	ApprovallineInfo getInfoByLineName(String LineID);
}
