package cfca.trustsign.demo.test;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.EnterpriseTransactorVO;
import cfca.trustsign.common.vo.cs.EnterpriseVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3039ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3039 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.initOp();

        EnterpriseVO enterprise = EnterpriseVO.builder().enterpriseName("开放平台测试").identTypeCode("8").identNo("110000017039723")
                .mobilePhone("85012584565").landlinePhone("69226906").authenticationMode("公安部").authenticationTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14))
                .build();

        EnterpriseTransactorVO enterpriseTransactor = EnterpriseTransactorVO.builder().transactorName("李铁柱测试").identTypeCode("1").identNo("362321199112050011")
                .address("beijing").build();

        Tx3039ReqVO tx3039ReqVO = new Tx3039ReqVO();
        tx3039ReqVO.setHead(HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.OPEN_PLAT_ID).txCode("3039").build());
        tx3039ReqVO.setEnterprise(enterprise);
        tx3039ReqVO.setEnterpriseTransactor(enterpriseTransactor);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3039ReqVO);
        System.out.println("req:" + req);

        String bodyData = new JsonObjectMapper().writeValueAsString(tx3039ReqVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs),
                appid = "up_5j03iopkfnp_2ysso";
        
        System.out.println("bodyData:" + bodyData);
        System.out.println("signature:" + signature);
        System.out.println("appid:" + appid);

        String res = httpConnector.postOp(bodyData, openTs, signature,appid);
        System.out.println("res:" + res);
    }
}
