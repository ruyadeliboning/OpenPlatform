package cfca.trustsign.demo.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.trustsign.common.vo.cs.ContractTemplateVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3232ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3232 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.initOp();

        ContractTemplateVO contractTemplateVO = ContractTemplateVO.builder().templateId("ZL_513").contractTypeCode("ZL").contractTitle("租赁合同")
                .textKeyInfos(new String[] { "2", "3", "4" }).data(Base64.toBase64String(Files.readAllBytes(Paths.get("./file/租赁合同.pdf")))).build();

        Tx3232ReqVO tx3232ReqVO = new Tx3232ReqVO();
        tx3232ReqVO.setHead(HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.PLAT_ID).txCode("3232").build());
        tx3232ReqVO.setContractTemplate(contractTemplateVO);

        String bodyData = new JsonObjectMapper().writeValueAsString(tx3232ReqVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs), appid = "up_5j03iopkfnp_2ysso";
        System.out.println("bodyData:" + bodyData);
        System.out.println("signature:" + signature);

        String res = httpConnector.postOp(bodyData, openTs, signature,appid);
        System.out.println("res:" + res);
    }
}
