package com.monsent.common.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

	public final static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public final static MediaType MEDIA_TYPE_TEXT_HTML = MediaType.parse("text/html;charset=utf-8");
    public final static MediaType MEDIA_TYPE_TEXT_PLAIN = MediaType.parse("text/plain;charset=utf-8");

	private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000 * 10, TimeUnit.MILLISECONDS)
			.build();

	static {
	}

	/**
	 * 设置证书
	 * @param certificates 证书文件流
	 */
	private static void setCertificates(InputStream[] certificates) {
		OkHttpClient.Builder builder = client.newBuilder();
		if (certificates != null) {
			try {
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
				KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				keyStore.load(null);
				int index = 0;
				for (InputStream certificate : certificates) {
					String certificateAlias = Integer.toString(index++);
					keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
					if (certificate != null) {
						certificate.close();
					}
				}
				TrustManagerFactory trustManagerFactory = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init(keyStore);
				TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
				if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager)) {
					X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
					SSLContext sslContext = SSLContext.getInstance("TLS");
					sslContext.init(null, new TrustManager[] { trustManager }, null);
					SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
					builder.sslSocketFactory(sslSocketFactory, trustManager);
				}
			} catch (Exception e) {

			} finally {
				for (InputStream certificate : certificates) {
					if (certificate != null) {
						try {
							certificate.close();
						} catch(Exception e) {
						}
					}
				}
			}
		}
		client = builder.build();
	}

	/**
	 * 创建请求体
	 * @param mediaType 媒体类型
	 * @param data 传递的数据
	 * @return 请求体
	 */
	public static RequestBody createRequestBody(MediaType mediaType, String data){
		return RequestBody.create(mediaType, data);
	}

	/**
	 * 同步Post请求
	 * @param url 请求URL
	 * @param headers 添加请求头
	 * @param body 请求体
	 * @param certificates 证书
	 * @return 请求结果
	 */
	public static HttpResult postSync(String url, Map<String, String> headers, RequestBody body,
			InputStream[] certificates) {
		HttpResult result = new HttpResult();
		Builder builder = new Builder().url(url);
		if (headers != null) {
			builder.headers(Headers.of(headers));
		}
		Request request = builder.post(body).build();
		Response response = null;
		try {
			if (certificates != null) {
				setCertificates(certificates);
			}
			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				result.setRet(HttpResult.RET_SUCCESS);
				result.setHeaders(response.headers().toMultimap());
				result.setBody(response.body().string());
			} else {
				result.setRet(response.code());
				result.setMsg(response.message());
			}
		} catch (Exception e) {
			result.setRet(HttpResult.RET_EXCEPTION);
			result.setMsg(e.getMessage());
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (Exception e) {

				}
			}
		}
		return result;
	}

	/**
	 * 同步Post请求
	 * @param url 请求URL
	 * @param headers 添加请求头
	 * @param body 请求体
	 * @return 请求结果
	 */
	public static HttpResult postSync(String url, Map<String, String> headers, RequestBody body){
		return postSync(url, headers, body, null);
	}

	/**
	 * 同步Post请求
	 * @param url 请求URL
	 * @param body 请求体
	 * @return 请求结果
	 */
	public static HttpResult postSync(String url, RequestBody body){
		return postSync(url, null, body);
	}

	/**
	 * 同步Post请求
	 * @param url url
	 * @param headers 请求头
	 * @param json json数据
	 * @param certificates 证书
	 * @return 请求结果
	 */
	public static HttpResult postJsonSync(String url, Map<String, String> headers, String json,
			InputStream[] certificates) {
		RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
		return postSync(url, headers, body, certificates);
	}

	/**
	 * 同步Post请求
	 * @param url url
	 * @param headers 请求头
	 * @param json json数据
	 * @return 请求结果
	 */
	public static HttpResult postJsonSync(String url, Map<String, String> headers, String json) {
		return postJsonSync(url, headers, json, null);
	}

	/**
	 * 同步Post请求
	 * @param url url
	 * @param json json数据
	 * @return 请求结果
	 */
	public static HttpResult postJsonSync(String url, String json) {
		return postJsonSync(url, null, json);
	}

	/**
	 * 同步提交表单请求
	 * @param url url
	 * @param headers 请求头
	 * @param form 表单
	 * @param certificates 证书
	 * @return 请求结果
	 */
	public static HttpResult postFormSync(String url, Map<String, String> headers, Map<String, String> form,
			InputStream[] certificates) {
		FormBody.Builder builder = new FormBody.Builder();
		if (form != null) {
			for (String key : form.keySet()) {
				builder.add(key, form.get(key));
			}
		}
		RequestBody body = builder.build();
		return postSync(url, headers, body, certificates);
	}

	/**
	 * 同步提交表单请求
	 * @param url url
	 * @param headers 请求头
	 * @param form 表单
	 * @return 请求结果
	 */
	public static HttpResult postFormSync(String url, Map<String, String> headers, Map<String, String> form) {
		return postFormSync(url, headers, form, null);
	}

	/**
	 * 同步提交表单请求
	 * @param url url
	 * @param form 表单
	 * @return 请求结果
	 */
	public static HttpResult postFormSync(String url, Map<String, String> form) {
		return postFormSync(url, null, form);
	}

	/************************** Post Async ***************************/
	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param headers 请求头
	 * @param body 请求体
	 * @param callback 回调
	 * @param certificates 证书
	 */
	public static void postAsync(String url, Map<String, String> headers, RequestBody body, final OkHttpCallback callback,
			InputStream[] certificates) {
		Builder builder = new Builder().url(url);
		if (headers != null) {
			builder.headers(Headers.of(headers));
		}
		Request request = builder.post(body).build();
		if (certificates != null) {
			setCertificates(certificates);
		}
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			public void onResponse(Call call, Response response) throws IOException {
				HttpResult result = new HttpResult();
				if (response.isSuccessful()) {
					result.setRet(HttpResult.RET_SUCCESS);
					result.setBody(response.body().string());
					result.setHeaders(response.headers().toMultimap());
				} else {
					result.setRet(response.code());
					result.setMsg(response.message());
				}
				if (callback != null) {
					callback.onSuccess(result);
				}
			}

			public void onFailure(Call call, IOException e) {
				if (callback != null) {
					callback.onFailure(e.getMessage());
				}
			}
		});
	}

	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param headers 请求头
	 * @param body 请求体
	 * @param callback 回调
	 */
	public static void postAsync(String url, Map<String, String> headers, RequestBody body, final OkHttpCallback callback){
		postAsync(url, headers, body, callback, null);
	}

	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param body 请求体
	 * @param callback 回调
	 */
	public static void postAsync(String url, RequestBody body, final OkHttpCallback callback){
		postAsync(url, null, body, callback);
	}

	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param headers 请求体
	 * @param json json数据
	 * @param callback 回调
	 * @param certificates 证书
	 */
	public static void postJsonAsync(String url, Map<String, String> headers, String json, final OkHttpCallback callback,
			InputStream[] certificates) {
		RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
		postAsync(url, headers, body, callback, certificates);
	}

	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param headers 请求体
	 * @param json json数据
	 * @param callback 回调
	 */
	public static void postJsonAsync(String url, Map<String, String> headers, String json, final OkHttpCallback callback) {
		postJsonAsync(url, headers, json, callback, null);
	}

	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param json json数据
	 * @param callback 回调
	 */
	public static void postJsonAsync(String url, String json, final OkHttpCallback callback) {
		postJsonAsync(url, null, json, callback);
	}

	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param headers 请求头
	 * @param form 表单
	 * @param callback 回调
	 * @param certificates 证书
	 */
	public static void postFormAsync(String url, Map<String, String> headers, Map<String, String> form,
									 final OkHttpCallback callback, InputStream[] certificates) {
		FormBody.Builder builder = new FormBody.Builder();
		if (form != null) {
			for (String key : form.keySet()) {
				builder.add(key, form.get(key));
			}
		}
		RequestBody body = builder.build();
		postAsync(url, headers, body, callback, certificates);
	}

	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param headers 请求头
	 * @param form 表单
	 * @param callback 回调
	 */
	public static void postFormAsync(String url, Map<String, String> headers, Map<String, String> form,
									 final OkHttpCallback callback) {
		postFormAsync(url, headers, form, callback, null);
	}

	/**
	 * 异步提交POST请求
	 * @param url url
	 * @param form 表单
	 * @param callback 回调
	 */
	public static void postFormAsync(String url, Map<String, String> form, final OkHttpCallback callback) {
		postFormAsync(url, null, form, callback);
	}

	/************************** Get Sync ***************************/
	/**
	 * 同步GET请求
	 * @param url url
	 * @param headers 请求头
	 * @param parameters 参数
	 * @param certificates 证书
	 * @return 请求结果
	 */
	public static HttpResult getSync(String url, Map<String, String> headers, Map<String, String> parameters,
			InputStream[] certificates) {
		HttpResult result = new HttpResult();
		if (parameters != null) {
			StringBuffer sb = new StringBuffer(url + "?");
			for (String key : parameters.keySet()) {
				sb.append(String.format("%s=%s&", key, parameters.get(key)));
			}
			url = sb.toString();
		}
		Builder builder = new Builder().url(url);
		if (headers != null) {
			builder.headers(Headers.of(headers));
		}
		Request request = builder.get().build();
		Response response = null;
		try {
			if (certificates != null) {
				setCertificates(certificates);
			}
			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				result.setRet(HttpResult.RET_SUCCESS);
				result.setBody(response.body().string());
				result.setHeaders(response.headers().toMultimap());
			} else {
				result.setRet(response.code());
				result.setMsg(response.message());
			}
		} catch (Exception e) {
			result.setRet(HttpResult.RET_EXCEPTION);
			result.setMsg(e.getMessage());
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (Exception e) {

				}
			}
		}
		return result;
	}

	/**
	 * 同步GET请求
	 * @param url url
	 * @param headers 请求头
	 * @param parameters 参数
	 * @return 请求结果
	 */
	public static HttpResult getSync(String url, Map<String, String> headers, Map<String, String> parameters) {
		return getSync(url, headers, parameters, null);
	}

	/**
	 * 同步GET请求
	 * @param url url
	 * @param parameters 参数
	 * @return 请求结果
	 */
	public static HttpResult getSync(String url, Map<String, String> parameters) {
		return getSync(url, null, parameters);
	}

	/************************** Get Async ***************************/
	/**
	 * 异步GET请求
	 * @param url url
	 * @param headers 请求头
	 * @param parameters 参数
	 * @param callback 回调
	 * @param certificates 证书
	 */
	public static void getAsync(String url, Map<String, String> headers, Map<String, String> parameters,
			final OkHttpCallback callback, InputStream[] certificates) {
		if (parameters != null) {
			StringBuffer sb = new StringBuffer(url + "?");
			for (String key : parameters.keySet()) {
				sb.append(String.format("%s=%s&", key, parameters.get(key)));
			}
			url = sb.toString();
		}
		Builder builder = new Builder().url(url);
		if (headers != null) {
			builder.headers(Headers.of(headers));
		}
		Request request = builder.get().build();
		if (certificates != null) {
			setCertificates(certificates);
		}
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			public void onResponse(Call call, Response response) throws IOException {
				HttpResult result = new HttpResult();
				if (response.isSuccessful()) {
					result.setRet(HttpResult.RET_SUCCESS);
					result.setBody(response.body().string());
					result.setHeaders(response.headers().toMultimap());
				} else {
					result.setRet(response.code());
					result.setMsg(response.message());
				}
				if (callback != null) {
					callback.onSuccess(result);
				}
			}

			public void onFailure(Call call, IOException e) {
				if (callback != null) {
					callback.onFailure(e.getMessage());
				}
			}
		});
	}

	/**
	 * 异步GET请求
	 * @param url url
	 * @param headers 请求头
	 * @param parameters 参数
	 * @param callback 回调
	 */
	public static void getAsync(String url, Map<String, String> headers, Map<String, String> parameters,
			final OkHttpCallback callback) {
		getAsync(url, headers, parameters, callback, null);
	}

	/**
	 * 异步GET请求
	 * @param url url
	 * @param parameters 参数
	 * @param callback 回调
	 */
	public static void getAsync(String url, Map<String, String> parameters, final OkHttpCallback callback) {
		getAsync(url, null, parameters, callback);
	}

	public static void main(String[] args) throws FileNotFoundException {
		String json = "{\"username\":\"admin\",\"password:\"123456\"}";
		HttpResult result = OkHttpUtils.postJsonSync("http://10.10.6.23:8080/router/user/login.do", json);
		System.out.println(result.getRet());


		OkHttpUtils.postJsonAsync("http://10.10.6.23:8080/router/user/login.do", json,
				new OkHttpCallback() {

					public void onSuccess(HttpResult result) {
						System.out.println(result);
					}

					public void onFailure(String msg) {
						System.out.println(msg);
					}
				});

		InputStream is = new FileInputStream(new File("F:\\srca.cer"));
		OkHttpUtils.getAsync("https://kyfw.12306.cn/otn/", null, null, new OkHttpCallback() {

			public void onSuccess(HttpResult result) {
				System.out.println(result);
			}

			public void onFailure(String msg) {
				System.out.println(msg);
			}
		}, new InputStream[] { is });
	}

}
