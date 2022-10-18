package cfca.trustsign.demo.test;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.ContractTemplateVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3233ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3233 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.initOp();

        ContractTemplateVO contractTemplateVO = ContractTemplateVO.builder().templateId("ZL_512").build();

        Tx3233ReqVO tx3233ReqVO = new Tx3233ReqVO();
        tx3233ReqVO.setHead(HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.PLAT_ID).txCode("3233").build());
        tx3233ReqVO.setContractTemplate(contractTemplateVO);

        String bodyData = new JsonObjectMapper().writeValueAsString(tx3233ReqVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs),appid = "up_5j03iopkfnp_2ysso";;
        System.out.println("bodyData:" + bodyData);
        System.out.println("signature:" + signature);

        String res = httpConnector.postOp(bodyData, openTs, signature,appid);
        System.out.println("res:" + res);
    }
}
