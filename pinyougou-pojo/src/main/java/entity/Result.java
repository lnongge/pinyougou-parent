package entity;

import java.io.Serializable;

/**
 * 添加方法返回值封装实体类
 */
public class Result implements Serializable{
	private Boolean success;
	private String message;

	public Result(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
