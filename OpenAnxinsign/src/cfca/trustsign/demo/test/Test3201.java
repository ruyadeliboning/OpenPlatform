package cfca.trustsign.demo.test;

import java.util.HashMap;
import java.util.Map;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.CreateContractVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.request.tx3.Tx3201ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3201 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.initOp();

        Map<String, String> fieldMap = new HashMap<>(7);
/*        fieldMap.put("Text1", "1");
        fieldMap.put("Text2", "2");
        fieldMap.put("Text3", "3");
        fieldMap.put("Text4", "孙一");
        fieldMap.put("Text20", "1");*/
        
        fieldMap.put("cardNo1", "29292992999");
        fieldMap.put("accountName", "测试人员");
        fieldMap.put("ICcardType", "借记卡");

        SignInfoVO signInfo1 = SignInfoVO.builder().userId("D10AD2BA94A162C5E05011AC020023C8").signLocation("Signature1").build();
        //SignInfoVO signInfo1 = SignInfoVO.builder().userId("D10AD2BA94A162C5E05011AC020023C8").signLocation("Signature_A").build();
        // SignInfoVO signInfo2 = SignInfoVO.builder().userId("D0A803EFBA0B1483E053AB2EA8C0DBF8").signLocation("Signature_B").build();
/*        CreateContractVO createContract = CreateContractVO.builder().isSign(0).templateId("ZL_511").contractName("123").textValueInfo(fieldMap).isFillInFont(1)
                .signInfos(new SignInfoVO[] { signInfo1 }).build();*/
        CreateContractVO createContract = CreateContractVO.builder().isSign(0).templateId("ZL_539").contractName("租赁合同").textValueInfo(fieldMap).isFillInFont(1)
                .signInfos(new SignInfoVO[] { signInfo1 }).build();
        

        Tx3201ReqVO tx3201ReqVO = new Tx3201ReqVO();
        tx3201ReqVO.setHead(HeadVO.builder().txTime(TimeUtil.getCurrentTime()).platId(Request.PLAT_ID).txCode("3201").build());
        tx3201ReqVO.setCreateContract(createContract);

        String bodyData = new JsonObjectMapper().writeValueAsString(tx3201ReqVO), openTs = String.valueOf(System.currentTimeMillis()),
                signature = SecurityUtil.getOpenPlatSignData(bodyData, openTs), appid = "up_5j03iopkfnp_2ysso";
        System.out.println("bodyData:" + bodyData);
        System.out.println("signature:" + signature);

        String res = httpConnector.postOp(bodyData, openTs, signature,appid);
        System.out.println("res:" + res);
    }
}
