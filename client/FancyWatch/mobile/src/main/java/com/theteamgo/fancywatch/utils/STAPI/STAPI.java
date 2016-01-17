package com.theteamgo.fancywatch.utils.STAPI;

import android.graphics.Bitmap;
import android.util.Log;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by houfang on 16/1/17.
 */
public class STAPI {
    private static final String TAG = "STAPI";
    private static final boolean DEBUG = true;

    private static final String WEBSITE_CN = "https://v1-api.visioncloudapi.com/";
    private static final String RESPONSECODE_KEY = "responseCode";

    static final private int BUFFERSIZE = 1048576;
    static final private int TIMEOUT = 30000;

    private String webSite;
    private String apiId, apiSecret;
    private STAPIParameters4Post params;
    private STAPIParameters4Get paramsGet;
    private int httpTimeOut = TIMEOUT;

    /**
     *  客户端初始化函数 （建议在程序入口出调用，或者在调用其他STAPI之前调用）
     *
     *  @param apiid     SenseTime 的API ID
     *  @param apisecret SenseTime 的API SECRET
     */
    public STAPI(String apiId, String apiSecret) {
        super();
        this.apiId = apiId;
        this.apiSecret = apiSecret;
        this.webSite = WEBSITE_CN;
    }

    public STAPI() {
        super();
    }

    // =======================信息获取===============================
    /**
     *  获得当前账户的使用信息，包括频率限制以及各种数量限制，建议在程序入口调用，一方面测试调用方法是否正确，一方面也可以验证自己的api_id 和 api_secret
     * @return 当前账户的使用信息
     * @throws STAPIException
     */
    public JSONObject infoApi() throws STAPIException{
        return requestGet("info", "api");
    }


    // =======================人脸检测与分析===============================
    /**
     *  提供图片，进行人脸检测以及人脸分析
     *
     *  @param imageBitmap        必须，格式必须为 JPG（JPEG），BMP，PNG，GIF，TIFF 之一,
    宽和高必须大于 8px，小于等于 4000px
    文件尺寸小于等于 5 MB
    上传本地图片需上传的图片文件
     *   注意：对于其他可选参数（如isAutoRotate，withLandmarks106和 withAttributes），如果没有需求，请不要开启，这样会减少系统计算时间
     *  @param withLandmarks106	 非必须，值为 true 时，计算 106 个关键点
     *  @param withAttributes   非必须，值为 true 时，提取人脸属性
     *  @param isAutoRotate  非必须，值为true时，对图片进行自动旋转
     *  @param userData    非必须，用户自定义信息
     *  @return
     *  @throws STAPIException
     */
    public JSONObject faceDetection(Bitmap imageBitmap,Boolean withLandmarks106,Boolean withAttributes,Boolean isAutoRotate,String userData) throws STAPIException{
        STAPIParameters4Post params = new STAPIParameters4Post();
        params.setFile(imageBitmap);
        if(withLandmarks106!=null){
            params.setLandmarks106(withLandmarks106);
        }
        if(withAttributes!=null){
            params.setAttributes(withAttributes);
        }
        if(isAutoRotate!=null){
            params.setAutoRotate(isAutoRotate);
        }
        if(!isNull(userData)){
            params.setUserData(userData);
        }
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(STAPIParameters4Post params) throws STAPIException{
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(Bitmap imageBitmap) throws STAPIException{
        STAPIParameters4Post params = new STAPIParameters4Post();
        params.setFile(imageBitmap);
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(File imageFile) throws STAPIException{
        STAPIParameters4Post params = new STAPIParameters4Post();
        params.setFile(imageFile);
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(byte[] imageBytes) throws STAPIException{
        STAPIParameters4Post params = new STAPIParameters4Post();
        params.setFile(imageBytes);
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(String url) throws STAPIException{
        STAPIParameters4Post params = new STAPIParameters4Post();
        params.setUrl(url);
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(Bitmap imageBitmap,STAPIParameters4Post params) throws STAPIException{
        if (params == null) params = new STAPIParameters4Post();
        params.setFile(imageBitmap);
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(File imageFile,STAPIParameters4Post params) throws STAPIException{
        if (params == null) params = new STAPIParameters4Post();
        params.setFile(imageFile);
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(byte[] imageBytes,STAPIParameters4Post params) throws STAPIException{
        if (params == null) params = new STAPIParameters4Post();
        params.setFile(imageBytes);
        return requestPost("face", "detection", params);
    }

    public JSONObject faceDetection(String imageUrl,STAPIParameters4Post params) throws STAPIException{
        if (params == null) params = new STAPIParameters4Post();
        params.setUrl(imageUrl);
        return requestPost("face", "detection", params);
    }



    private JSONObject requestPost(String control, String action, STAPIParameters4Post params) throws STAPIException {
        URL url;
        HttpsURLConnection urlConn = null;
        int responseCode = -1;
//		STAPIException apiException = null;
        try {
            if(DEBUG){
                Log.d(TAG, "post url:"+webSite+control+"/"+action);
            }
            url = new URL(webSite+control+"/"+action);
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(httpTimeOut);
            urlConn.setReadTimeout(httpTimeOut);
            urlConn.setDoOutput(true);

            urlConn.setRequestProperty("connection", "keep-alive");
            urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + params.boundaryString());

            MultipartEntity reqEntity = params.getMultiPart();

            reqEntity.addPart("api_id", new StringBody(apiId));
            reqEntity.addPart("api_secret", new StringBody(apiSecret));
            reqEntity.writeTo(urlConn.getOutputStream());

            String resultString = null;
            InputStream inputStream = null;
            responseCode = urlConn.getResponseCode();
            if(DEBUG){
                Log.d(TAG, "request getResponseCode:"+responseCode);
            }
            if (HttpsURLConnection.HTTP_OK == responseCode)
                inputStream = urlConn.getInputStream();
            else {
                inputStream = urlConn.getErrorStream();
            }
            resultString = readString(inputStream);
            if(DEBUG){
                Log.d(TAG, "request resultString:"+resultString);
            }
            JSONObject result = new JSONObject(resultString);
//		    if(result.has(JsonStatus.STATUS_KEY)){
//		    	String status = result.get(JsonStatus.STATUS_KEY).toString();
//	            if(!JsonStatus.STATUS_OK.equalsIgnoreCase(status)){
//	            	String reason = null;
//	            	if(result.has(JsonStatus.REASON_KEY)){
//	            		reason = result.get(JsonStatus.REASON_KEY).toString();
//	            	}
//	            	apiException = new STAPIException(
//	            			responseCode,status,reason);
//	            	throw apiException;
//	            }
//	        }else{
//	        	apiException = new STAPIException(responseCode,JsonStatus.NO_RESPONSE,null);
//	            throw apiException;
//	        }
            if(inputStream!=null){
                inputStream.close();
            }
            return result;
        } catch (Exception e) {
            throw new STAPIException(responseCode,e.toString());
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
    }

    public JSONObject requestGet(String control, String action) throws STAPIException {
        return requestGet(control, action, getParamsGet());
    }

    public STAPIParameters4Get getParamsGet() {
        if (paramsGet == null) paramsGet = new STAPIParameters4Get();
        return paramsGet;
    }

    private JSONObject requestGet(String control, String action, STAPIParameters4Get params) throws STAPIException {
        URL url;
        HttpsURLConnection urlConn = null;
        int responseCode = -1;
        try {
            if(DEBUG){
                Log.d(TAG, "get url:" + webSite + control + "/" + action + "?api_id=" + apiId + "&api_secret=" + apiSecret + params.getParamsMap() + "&date=" + System.currentTimeMillis());
            }
            url = new URL(webSite+control+"/"+action+"?api_id="+apiId+"&api_secret="+apiSecret+params.getParamsMap()+"&date="+System.currentTimeMillis());
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(httpTimeOut);
            urlConn.setReadTimeout(httpTimeOut);
            urlConn.setRequestProperty("connection", "keep-alive");
            String resultString = null;
            InputStream inputStream = null;
            responseCode = urlConn.getResponseCode();
            if(DEBUG){
                Log.d(TAG, "request getResponseCode:"+responseCode);
            }
            if (HttpsURLConnection.HTTP_OK == responseCode)
                inputStream = urlConn.getInputStream();
            else {
                inputStream = urlConn.getErrorStream();
            }
            resultString = readString(inputStream);
            if(DEBUG){
                Log.d(TAG, "request resultString:"+resultString);
            }
            JSONObject result = new JSONObject(resultString);
//		    if(result.has(JsonStatus.STATUS_KEY)){
//		    	String status = result.get(JsonStatus.STATUS_KEY).toString();
//	            if(!JsonStatus.STATUS_OK.equalsIgnoreCase(status)){
//	            	String reason = null;
//	            	if(result.has(JsonStatus.REASON_KEY)){
//	            		reason = result.get(JsonStatus.REASON_KEY).toString();
//	            	}
//	            	apiException = new STAPIException(
//	            			responseCode,status,reason);
//	            	throw apiException;
//	            }
//	        }else{
//	        	apiException = new STAPIException(responseCode,JsonStatus.NO_RESPONSE,null);
//	            throw apiException;
//	        }
            if(inputStream!=null){
                inputStream.close();
            }
            return result;
        } catch (Exception e) {
            throw new STAPIException(responseCode,e.toString());
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
    }



    private JSONObject requestGetMap(String control, String action, Map<String,Object> paramsMap) throws STAPIException {
        URL url;
        HttpsURLConnection urlConn = null;
        int responseCode = -1;
        try {
            StringBuffer params = new StringBuffer();
            if(paramsMap!=null){
                String flag = "0";
                for(Map.Entry entry:paramsMap.entrySet()){
                    if("true".equals(entry.getValue().toString())){
                        flag = "1";
                    }else if("false".equals(entry.getValue().toString())){
                        flag = "0";
                    }else{
                        flag = entry.getValue().toString();
                    }
                    params.append("&"+entry.getKey()+"="+flag);
                }
            }
            url = new URL(webSite+control+"/"+action+"?api_id="+apiId+"&api_secret="+apiSecret+params.toString());
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(httpTimeOut);
            urlConn.setReadTimeout(httpTimeOut);
            urlConn.setRequestProperty("connection", "keep-alive");
            String resultString = null;
            InputStream inputStream = null;
            responseCode = urlConn.getResponseCode();
            if(DEBUG){
                Log.d(TAG, "request getResponseCode:"+responseCode);
            }
            if (HttpsURLConnection.HTTP_OK == responseCode)
                inputStream = urlConn.getInputStream();
            else {
                inputStream = urlConn.getErrorStream();
            }
            resultString = readString(inputStream);
            if(DEBUG){
                Log.d(TAG, "request resultString:"+resultString);
            }
            JSONObject result = new JSONObject(resultString);
//		    if(result.has(JsonStatus.STATUS_KEY)){
//		    	String status = result.get(JsonStatus.STATUS_KEY).toString();
//	            if(!JsonStatus.STATUS_OK.equalsIgnoreCase(status)){
//	            	String reason = null;
//	            	if(result.has(JsonStatus.REASON_KEY)){
//	            		reason = result.get(JsonStatus.REASON_KEY).toString();
//	            	}
//	            	apiException = new STAPIException(
//	            			responseCode,status,reason);
//	            	throw apiException;
//	            }
//	        }else{
//	        	apiException = new STAPIException(responseCode,JsonStatus.NO_RESPONSE,null);
//	            throw apiException;
//	        }
            if(inputStream!=null){
                inputStream.close();
            }
            return result;
        } catch (Exception e) {
            throw new STAPIException(responseCode,e.toString());
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
    }

    private static String readString(InputStream is) {
        if(null == is){
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int   i=-1;
        try {
            while((i=is.read())!=-1){
                baos.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toString();
    }

    private boolean isNull(String s){
        return (null == s || "" == s);
    }
}
