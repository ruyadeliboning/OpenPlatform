package cfca.trustsign.demo.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.trustsign.common.vo.cs.ContractTemplateVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3231ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3231 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.initOp();

/*        ContractTemplateVO contractTemplateVO = ContractTemplateVO.builder().contractTypeCode("ZL").contractTitle("租赁合同")
                .textKeyInfos(new String[] { "1", "2", "3" }).data(Base64.toBase64String(Files.readAllBytes(Paths.get("./file/租赁合同.pdf")))).build();*/
        ContractTemplateVO contractTemplateVO = ContractTemplateVO.builder().contractTypeCode("ZL").contractTitle("租赁合同")
                .textKeyInfos(new String[] { "cardNo1", "accountName", "ICcardType" }).data(Base64.toBase64String(Files.readAllBytes(Paths.get("./file/tmp.pdf")))).build();
        
        Tx3231ReqVO tx3231ReqVO = new Tx3231ReqVO();
        tx3231ReqVO.setHead(HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.PLAT_ID).txCode("3231").build());
        tx3231ReqVO.setContractTemplate(contractTemplateVO);

        String bodyData = new JsonObjectMapper().writeValueAsString(tx3231ReqVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs), appid = "up_5j03iopkfnp_2ysso";
        System.out.println("bodyData:" + bodyData);
        System.out.println("signature:" + signature);

        String res = httpConnector.postOp(bodyData, openTs, signature,appid);
        System.out.println("res:" + res);
    }
}
