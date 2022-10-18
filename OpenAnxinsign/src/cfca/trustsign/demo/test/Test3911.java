package cfca.trustsign.demo.test;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.ShortUrlVO;
import cfca.trustsign.common.vo.request.tx3.Tx3911ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3911 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.initOp();

/*        ShortUrlVO shortUrlVO = ShortUrlVO.builder().userId("D10AD2BA94A162C5E05011AC020023C8").contractNos(new String[] { "ZL20211118000000003" })
                .signLocations(new String[] { "Signature1" }).signMode(0).callbackUrl("http://192.168.113.129:8080/APPServer/").build();*/

        ShortUrlVO shortUrlVO = ShortUrlVO.builder().userId("D10804FF9D4534FEE05011AC02000FC0").contractNos(new String[] { "ZL20220302000000012" })
                .signLocations(new String[] { "Signature1" }).signMode(0).callbackUrl("http://192.168.113.129:8080/APPServer/").build();
        
        Tx3911ReqVO tx3911ReqVO = new Tx3911ReqVO();
        tx3911ReqVO.setHead(HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.PLAT_ID).txCode("3911").build());
        tx3911ReqVO.setGenerateShortUrl(shortUrlVO);

        String bodyData = new JsonObjectMapper().writeValueAsString(tx3911ReqVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs), appid = "up_5j03iopkfnp_2ysso";;
        System.out.println("bodyData:" + bodyData);
        System.out.println("signature:" + signature);

        String res = httpConnector.postOp(bodyData, openTs, signature,appid);
        System.out.println("res:" + res);
    }
}
