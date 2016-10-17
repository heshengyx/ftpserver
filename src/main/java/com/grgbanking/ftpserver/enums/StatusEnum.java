package com.grgbanking.ftpserver.enums;

public enum StatusEnum {
	
	SUCCESS(1, "成功"),
	FAIL(0, "失败"),
	ERROR(-1, "异常/错误");
	
	private int value;
	private String text;
	
	private StatusEnum(int value, String text){
		this.value = value;
		this.text = text;
	}
	
	public int getValue(){
		return this.value;
	}
	public String getText(){
		return this.text;
	}
	
	public static String getTextByValue(int value) {
		String text = "";
		for(StatusEnum e : StatusEnum.values()){
			if (e.getValue() == value) {
				text = e.getText();
				break;
			}
        }
		return text;
	}
}
