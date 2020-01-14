package com.naviworks.invt.dao;

import com.naviworks.invt.model.ApprovallineInfo;

public interface ApprovallineDAO
{
	ApprovallineInfo getInfoByLineInfo(int LineID);
	ApprovallineInfo getInfoByLineName(int LineID);
}
