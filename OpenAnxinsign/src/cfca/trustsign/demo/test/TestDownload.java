package cfca.trustsign.demo.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.trustsign.common.util.CommonUtil;
import cfca.trustsign.common.vo.cs.DownloadVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.demo.connector.HttpConnector2;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;


public class TestDownload {
    public static void main(String[] args) throws IOException, PKIException {
        HttpConnector2 httpConnector2 = new HttpConnector2();
        httpConnector2.initOp();

        String contractNo = "ZL20220317000000117";
        HeadVO headVO = HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.PLAT_ID).build();
        DownloadVO downloadVO = DownloadVO.builder().head(headVO).contractNo(contractNo).build();

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String bodyData = jsonObjectMapper.writeValueAsString(downloadVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs),appid = "up_5j03iopkfnp_2ysso";;
        System.out.println("bodyData:" + bodyData);

        String res = httpConnector2.downloadOp(bodyData, openTs,signature,appid);
        System.out.println("res:" + res);

/*        String res1=res.substring(8, res.length()-32);
        System.out.println("res1:" + res1);*/
        
        DownloadVO resVO = jsonObjectMapper.readValue(res, DownloadVO.class);
        
//        DownloadVO resVO = jsonObjectMapper.readValue(res1, DownloadVO.class);
        String content = resVO.getContent();
        if (CommonUtil.isEmpty(content)) {
            return;
        }
        
                
        String filePath = "./file";
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        Files.write(Paths.get(filePath + File.separator + contractNo + ".pdf"), Base64.decode(content));
    }
}
