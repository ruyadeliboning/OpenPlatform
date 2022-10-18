package cfca.trustsign.demo.test;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.PersonVO;
import cfca.trustsign.common.vo.request.tx3.Tx3032ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3032 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.initOp();

        PersonVO person = PersonVO.builder().personName("李泊宁").identNo("110102200001071160").mobilePhone("18518089141").build();
        Tx3032ReqVO tx3032ReqVO = new Tx3032ReqVO();
        tx3032ReqVO.setHead(HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.PLAT_ID).txCode("3032").build());
        tx3032ReqVO.setPerson(person);

        String bodyData = new JsonObjectMapper().writeValueAsString(tx3032ReqVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs),
                 appid = "up_5j03iopkfnp_2ysso";
        System.out.println("bodyData:" + bodyData);
        System.out.println("signature:" + signature);

        
        String res = httpConnector.postOp(bodyData, openTs, signature,appid);
        System.out.println("res:" + res);
    }
}
