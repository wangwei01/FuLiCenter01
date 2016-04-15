package cn.ucai.fulicenter.bean;

import java.io.Serializable;

/**
 * 获取服务端返回的响应实体类
 * @author chen
 *
 */
public class MessageBean implements Serializable {
	/** 响应是否成功,true:成功，false：失败*/
	private boolean success;
	/** 返回的字符串*/
	private String msg;
	public MessageBean(){}
	public MessageBean(boolean success, String msg){
		super();
		this.success = success;
		this.msg = msg;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "MessageBean [success=" + success + ", msg=" + msg + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MessageBean that = (MessageBean) o;

		if (success != that.success) return false;
		return msg != null ? msg.equals(that.msg) : that.msg == null;

	}

	@Override
	public int hashCode() {
		int result = (success ? 1 : 0);
		result = 31 * result + (msg != null ? msg.hashCode() : 0);
		return result;
	}
}
