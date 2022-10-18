package cfca.trustsign.demo.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import cfca.trustsign.demo.constant.MIMEType;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.constant.SystemConst;
import cfca.trustsign.demo.util.CommonUtil;

public class HttpConnector2 {
    // public static String JKS_PATH = "E:\\svnWork\\svn.47.11.dev\\R13\\P1085\\dev\\69-Demo\\CSServer\\JavaDemo/jks/admin.jks", JKS_PWD = "11111111",
    // ALIAS = "051@administrator@z01@1 (cfca test oca1)";
/*    public static String JKS_PATH = "E:\\svnWork\\svn.47.11.dev\\R13\\P1085\\dev\\69-Demo\\CSServer\\JavaDemo/jks/openPlat.jks", JKS_PWD = "11111111",
            ALIAS = "plat";
    public static String JKS_PATH_OP_USER = "E:\\svnWork\\svn.47.11.dev\\R13\\P1085\\dev\\69-Demo\\CSServer\\JavaDemo/jks/openPlatUser.jks",
            JKS_PWD_OP_USER = "11111111", ALIAS_OP_USER = "plat";*/

/////////////////////////////////////////////修改用户通信证书//////////////////////////////////////////////////////
/*   public static String JKS_PATH = "./jks/server0126.jks", JKS_PWD = "cfca1234",
	ALIAS = "pufa0127";*/
/*   public static String JKS_PATH = "./jks/openPlat.jks", JKS_PWD = "11111111",
				ALIAS = "plat";*/
/*	public static String JKS_PATH = "./jks/server.jks", JKS_PWD = "cfca1234",
			ALIAS = "plat";*/
	public static String JKS_PATH = "./jks/server0126.jks", JKS_PWD = "cfca1234",
	ALIAS = "pufa0127";
/////////////////////////////////////////////修改用户签名证书//////////////////////////////////////////////////////
   public static String JKS_PATH_OP_USER = "./jks/server0126.jks", JKS_PWD_OP_USER = "cfca1234",
		   ALIAS_OP_USER = "pufa0127";
/*    public static String JKS_PATH_OP_USER = "./jks/openPlatUser.jks",
            JKS_PWD_OP_USER = "11111111", ALIAS_OP_USER = "plat";*/
	/*public static String JKS_PATH_OP_USER = "./jks/openPlatUser.jks",
            JKS_PWD_OP_USER = "11111111", ALIAS_OP_USER = "plat";*/
/*	public static String JKS_PATH_OP_USER = "./jks/server.jks",
    JKS_PWD_OP_USER = "cfca1234", ALIAS_OP_USER = "server";*/
	
//    public String url = "https://localhost:8443/FEP/", channel = "Test";
//////////////////////////////////////////////修改url//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //public String url = "https://101.52.126.115:8444/FEP/", channel = "Test";
    public String url = "https://opentest.cfca.com.cn:9444/updopapi/cfca/anxinSign/V1/ContractDownload/V1", channel = "Test";

    public int connectTimeout = 3000, readTimeout = 10000;
    public boolean isSSL = true;
    public String keyStorePath = JKS_PATH, keyStorePassword = JKS_PWD;
    public String trustStorePath = JKS_PATH, trustStorePassword = JKS_PWD;

    private HttpClient httpClient;

    public void initOp() {
        httpClient = new HttpClient();
        httpClient.config.connectTimeout = connectTimeout;
        httpClient.config.readTimeout = readTimeout;
        httpClient.httpConfig.userAgent = "TrustSign FEP";
        httpClient.httpConfig.contentType = MIMEType.JSON;
        httpClient.httpConfig.accept = MIMEType.JSON;
        try {
            if (isSSL) {
                httpClient.initSSL(keyStorePath, keyStorePassword.toCharArray(), trustStorePath, trustStorePassword.toCharArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!url.endsWith("/")) {
//            url += "/";
        	 url += "";
        }
    }

    public String post(String uri, String data, String signature) {
        return deal(uri, "POST", prepare(data, signature, null));
    }

    public String post(String uri, String data, String signature, Map<String, String> map) {
        return deal(uri, "POST", prepare(data, signature, map));
    }

    public String post(String uri, String data, String signature, File file) {
        return dealFile(uri, "POST", data, file, signature);
    }

    public String postOp(String data, String openTs, String signature,String appid) {
        //return dealOp("openPlatTransaction", "POST", data, openTs, signature);
        return dealOp("", "POST", data, openTs, signature,appid);
    }

/*    public String downloadOp(String data, String openTs,String appid) {
        return dealOp("openPlatDownloading", "POST", data, openTs, null,appid);
    }
*/
    
    public String downloadOp(String data, String openTs,String signature,String appid) {
        return dealOp("", "POST", data, openTs, signature,appid);
    }
    
    public byte[] getFile(String uri) {
        HttpURLConnection connection = null;
        try {
            connection = httpClient.connect(url + uri, "GET");
            connection.setRequestProperty("X-OPEN-TS", String.valueOf(System.currentTimeMillis()));

            int responseCode = httpClient.send(connection, null);
            System.out.println("responseCode:" + responseCode);
            if (responseCode != 200) {
                System.out.println(CommonUtil.getString(httpClient.receive(connection)));
                return null;
            }
            return httpClient.receive(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            httpClient.disconnect(connection);
        }
    }

    private String prepare(String data, String signature, Map<String, String> map) {
        try {
            StringBuilder request = new StringBuilder();
            request.append(Request.CHANNEL).append("=").append(URLEncoder.encode(channel, SystemConst.DEFAULT_CHARSET));
            if (CommonUtil.isNotEmpty(data)) {
                request.append("&").append(Request.DATA).append("=").append(URLEncoder.encode(data, SystemConst.DEFAULT_CHARSET));
            }
            if (CommonUtil.isNotEmpty(signature)) {
                request.append("&").append(Request.SIGNATURE).append("=").append(URLEncoder.encode(signature, SystemConst.DEFAULT_CHARSET));
            }
            // 如果要返回英文错误信息需要加上这个参数
            // request.append("&").append(Request.LOCALE).append("=").append(URLEncoder.encode(Locale.US.toString(), SystemConst.DEFAULT_CHARSET));
            if (CommonUtil.isNotEmpty(map)) {
                for (Entry<String, String> pair : map.entrySet()) {
                    request.append("&").append(pair.getKey()).append("=")
                            .append(pair.getValue() == null ? "" : URLEncoder.encode(pair.getValue(), SystemConst.DEFAULT_CHARSET));
                }
            }
            return request.toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String deal(String uri, String method, String request) {
        HttpURLConnection connection = null;
        try {
            connection = httpClient.connect(url + uri, method);
            int responseCode = httpClient.send(connection, request == null ? null : CommonUtil.getBytes(request));
            System.out.println("responseCode:" + responseCode);
            return CommonUtil.getString(httpClient.receive(connection));
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            httpClient.disconnect(connection);
        }
    }

    private String dealFile(String uri, String method, String request, File file, String signature) {
        HttpURLConnection connection = null;
        try (FileInputStream is = new FileInputStream(file)) {
            connection = httpClient.connect(url + uri, method);
            Map<String, String> paramMap = new HashMap<>(4);
            paramMap.put(Request.DATA, request);
            paramMap.put(Request.SIGNATURE, signature);
            int responseCode = httpClient.sendFile(connection, paramMap, is, file.getName(), MIMEType.PDF);
            System.out.println("responseCode:" + responseCode);
            return CommonUtil.getString(httpClient.receive(connection));
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            httpClient.disconnect(connection);
        }
    }

 /*   public String dealOp(String path, String method, String data, String openTs, String signature) {
        HttpURLConnection connection = null;
        try {
            connection = httpClient.connect(url + path, method);
            if (CommonUtil.isNotEmpty(openTs)) {
                connection.setRequestProperty(Request.OPEN_TS, openTs);
            }
            if (CommonUtil.isNotEmpty(signature)) {
                connection.setRequestProperty(Request.OPEN_SIGNATURE, signature);
            }
            connection.setRequestProperty(Request.OPEN_CFCA_NO, UUID.randomUUID().toString());

            int responseCode = httpClient.send(connection, CommonUtil.getBytes(data));
            System.out.println("responseCode:" + responseCode);
            System.out.println(connection.getHeaderFields());
            return CommonUtil.getString(httpClient.receive(connection));
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            httpClient.disconnect(connection);
        }
    }*/
    
    public String dealOp(String path, String method, String data, String openTs, String signature,String appid) {
        HttpURLConnection connection = null;
        try {
            connection = httpClient.connect(url + path, method);
            if (CommonUtil.isNotEmpty(openTs)) {
                connection.setRequestProperty(Request.OPEN_TS, openTs);
            }
            if (CommonUtil.isNotEmpty(signature)) {
                connection.setRequestProperty(Request.OPEN_SIGNATURE, signature);    
            }
            if (CommonUtil.isNotEmpty(appid)) {
                connection.setRequestProperty(Request.OPEN_APPID, appid);    
            }
            
            
            connection.setRequestProperty(Request.OPEN_CFCA_NO, UUID.randomUUID().toString());

            int responseCode = httpClient.send(connection, CommonUtil.getBytes(data));
            System.out.println("responseCode:" + responseCode);
            System.out.println(connection.getHeaderFields());
            return CommonUtil.getString(httpClient.receive(connection));
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            httpClient.disconnect(connection);
        }
    }
}
