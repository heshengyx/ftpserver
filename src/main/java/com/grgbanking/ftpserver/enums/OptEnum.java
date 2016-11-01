package com.grgbanking.ftpserver.enums;

public enum OptEnum {
	
	//传送消息
	Ftping,
	
	//确认消息
	FtpEnd, 
	
	//升级消息
	UpgradeFile,
	
	//升级开始
	UpgradeStart,
	
	//升级结束
	UpgradeOver,
	
	//文件开始
	UpgradeFileStart,
	
	//文件结束
	UpgradeFileEnd;
}
