package com.monsent.common.http;

import java.util.List;
import java.util.Map;

public class HttpResult {

	public final static long RET_EXCEPTION = -1L;
	public final static long RET_SUCCESS = 0L;

	private long ret = HttpResult.RET_SUCCESS;
	private String msg;
	private Map<String, List<String>> headers;
	private Object body;

	public long getRet() {
		return ret;
	}

	public void setRet(long ret) {
		this.ret = ret;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
}
