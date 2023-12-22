/**
 * author: yqq
 * date: 2023-12
 * description: fantopia
 */
package io.fantopia;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import static io.fantopia.RequestSigner.bytesToHexString;
import static org.junit.jupiter.api.Assertions.*;

class RequestSignerTest {
    @org.junit.jupiter.api.Test
    public  void TestGet() {
        try {
            Map<String, Object> data = new HashMap<>();
            long  timestamp = 1702635696518L ;// System.currentTimeMillis();
            String nonce = "68DmB1SpBfhqAeaE";
            String httpMethod = "GET";
            String userSessionToken = "eyJUeXBlIjoiSnd0IiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJGQU5UT1BJQSIsImpzb25EYXRhIjoie1widXNlcklkXCI6XCI0NDQwNTIxMjg1ODc4NDVcIixcInR5cGVcIjoxLFwibG9naW5UeXBlXCI6XCJ3ZWJcIixcImFyZWFDb2RlXCI6XCJcIixcImNyZWF0ZVRpbWVcIjoxNzAyNjIzNDg4MDg0fSIsImV4cCI6MTcwMzIyODI4OH0.eNgrLJTB3Pc5gPr2oyr41GIySJjkEjmPI59MYz2tzP4";

            Map<String, Object> result = RequestSigner.signRequest(data, timestamp, nonce, httpMethod, userSessionToken);
            assertTrue( RequestSigner.verifySig( result.get("msg").toString(), result.get("base64String").toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void TestPost() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("itemDtoString", "[{\"eventsId\":281,\"eventsSeriesId\":56,\"eventsTypeId\":188,\"goodsType\":0,\"quantity\":1,\"salesPrice\":199900}]");
            data.put("totalPrice", 199900);
            data.put("totalQuantity", 1);
            data.put("cfTurnstileResponse", "");
            data.put("eventsId", 281);

            long  timestamp = 1702641163514L ;
            String nonce = "qAjkNvwfTNTzr6GP";
            String httpMethod = "POST";
            String userSessionToken = "eyJUeXBlIjoiSnd0IiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJGQU5UT1BJQSIsImpzb25EYXRhIjoie1widXNlcklkXCI6XCI0NDQwNTIxMjg1ODc4NDVcIixcInR5cGVcIjoxLFwibG9naW5UeXBlXCI6XCJ3ZWJcIixcImFyZWFDb2RlXCI6XCJcIixcImNyZWF0ZVRpbWVcIjoxNzAyNjIzNDg4MDg0fSIsImV4cCI6MTcwMzIyODI4OH0.eNgrLJTB3Pc5gPr2oyr41GIySJjkEjmPI59MYz2tzP4";

            Map<String, Object> result = RequestSigner.signRequest(data, timestamp, nonce, httpMethod, userSessionToken);
            assertTrue( RequestSigner.verifySig( result.get("msg").toString(), result.get("base64String").toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void signRequest() {
        TestGet();
        TestPost();
    }

    @org.junit.jupiter.api.Test
    void verifyKey() {
        String msg = "hello";
        assertTrue(RequestSigner.verifySig(msg, "coiu6i5ursHxuqihQcRPzxFKPhYT/XSlkWzGpPkNNnZ5/ItlF6c4i1XOlUIj56ly"));
        assertFalse(RequestSigner.verifySig(msg, "oiu6i5ursHxuqihQcRPzxFKPhYT/XSlkWzGpPkNNnZ5/ItlF6c4i1XOlUIj56ly"));
        assertFalse(RequestSigner.verifySig(msg, "oiu6i5ursHxuqihQcRPzxFKPhYT/XSlkWzGpPkNNnZ5/ItlF6c4i1XOlUIj56ly"));
        assertFalse(RequestSigner.verifySig(msg, "Xoiu6i5ursHxuqihQcRPzxFKPhYT/XSlkWzGpPkNNnZ5/ItlF6c4i1XOlUIj56ly"));
        assertFalse(RequestSigner.verifySig("Hello", "coiu6i5ursHxuqihQcRPzxFKPhYT/XSlkWzGpPkNNnZ5/ItlF6c4i1XOlUIj56ly"));
    }

    @org.junit.jupiter.api.Test
    void testHash() {
        try{
            MessageDigest sha3 = MessageDigest.getInstance("SHA3-256");
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");


            System.out.println("sha256="+ bytesToHexString( sha256.digest( "hello".getBytes())));
            System.out.println("sha3="+ bytesToHexString( sha3.digest( "hello".getBytes())));
        }catch (Exception e) {

        }

    }

    @org.junit.jupiter.api.Test
    void generateKey() {
        String msg = "hello";
        byte[] key = RequestSigner.generateKey(msg);
        assertEquals(16, key.length);
    }


}