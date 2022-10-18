package cfca.trustsign.demo.test;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.EnterpriseTransactorVO;
import cfca.trustsign.common.vo.cs.EnterpriseVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3033ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3033 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.initOp();

        EnterpriseVO enterprise = EnterpriseVO.builder().enterpriseName("开放平台公司测试一").identTypeCode("8").identNo("110000017039720").mobilePhone("18518089141")
                .landlinePhone("69226906").authenticationMode("公安部").build();

/*        EnterpriseTransactorVO enterpriseTransactor = EnterpriseTransactorVO.builder().transactorName("平台A王五").identTypeCode("1").identNo("262321199112050011")
                .address("beijing").mobilePhone("85012584561").build();*/

        EnterpriseTransactorVO enterpriseTransactor = EnterpriseTransactorVO.builder().transactorName("平台A王五").identTypeCode("1").identNo("262321199112050011")
                .address("beijing").mobilePhone("18518089141").build();
        
        Tx3033ReqVO tx3033ReqVO = new Tx3033ReqVO();
        tx3033ReqVO.setHead(HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.PLAT_ID).txCode("3033").build());
        tx3033ReqVO.setEnterprise(enterprise);
        tx3033ReqVO.setEnterpriseTransactor(enterpriseTransactor);

        String bodyData = new JsonObjectMapper().writeValueAsString(tx3033ReqVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs), appid = "up_5j03iopkfnp_2ysso";
        System.out.println("bodyData:" + bodyData);
        System.out.println("signature:" + signature);

        String res = httpConnector.postOp(bodyData, openTs, signature,appid);
        System.out.println("res:" + res);
    }
}
