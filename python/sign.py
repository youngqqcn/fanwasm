#coding:utf8
# author: yqq
# time: 2023-12-15 18:30
# description: 实现fantopia接口签名算法

import hmac
import hashlib
import base64
import string
import random
from pprint import pprint
import unittest


def hmac_sha256(key, message):
    key = key.encode('utf-8')
    message = message.encode('utf-8')
    hmac_obj = hmac.new(key, message, hashlib.sha256)
    return hmac_obj.digest()

def generate_random_string(length):
    letters = string.ascii_letters + string.digits
    random_string = ''.join(random.choice(letters) for _ in range(length))
    return random_string


def sign_request(data: dict, timestamp: int, nonce: str, http_method: str, user_session_token: str):
    """
    data: 请求参数
    timestamp: 毫秒时间戳， 可以使用： int(time.time() * 1000)
    nonce: 16字符长度的随机字符 , 可以使用： generate_random_string(16)
    method: HTTP请求方法
    user_session_token: 用户会话token
    """

    r = {
        "platform": "web",
        "timestamp": timestamp,
        "nonce": nonce , #
        "token" : user_session_token
    }

    # 将 None 设置为空字符串
    for k in dict(data).keys():
        if data[k] is None:
            data[k] = ''

    # 如果是GET请求
    n = r
    if http_method == "POST":
        n.update(data) # 把参数也加进去

    # 对dict进行排序
    c = []
    for item in sorted(n.items()):
        c.append( str(item[0]).lower() + '=' + str(item[1]))
    msg = '&'.join(c)
    print(msg)
    key = 'e6087a37c50453ac262df54fcc12c66446d369887a37ae39d95b5ad0bd852798'
    o = hmac_sha256(key=key, message=msg)
    s = base64.b64encode(o).decode('utf-8')

    return {
        'base64String': s,
        'commonParams': r,
    }


class FantopiaSignatureTest(unittest.TestCase):
    """测试用例"""

    def test_get(self):
        token = 'eyJUeXBlIjoiSnd0IiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJGQU5UT1BJQSIsImpzb25EYXRhIjoie1widXNlcklkXCI6XCI0NDQwNTIxMjg1ODc4NDVcIixcInR5cGVcIjoxLFwibG9naW5UeXBlXCI6XCJ3ZWJcIixcImFyZWFDb2RlXCI6XCJcIixcImNyZWF0ZVRpbWVcIjoxNzAyNjIzNDg4MDg0fSIsImV4cCI6MTcwMzIyODI4OH0.eNgrLJTB3Pc5gPr2oyr41GIySJjkEjmPI59MYz2tzP4'
        ret = sign_request(
            data={},
            timestamp=1702635696518,
            nonce="68DmB1SpBfhqAeaE",
            http_method="GET",
            user_session_token=token
        )
        # pprint(ret)
        assert ret['base64String'] == '2njOemlKlwZak38fkm5GPnI8s4CnVZOZYTZnLS2ap60=', '签名不匹配'
        print('GET签名测试成功')
        pass

    def test_post(self):
        token = 'eyJUeXBlIjoiSnd0IiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJGQU5UT1BJQSIsImpzb25EYXRhIjoie1widXNlcklkXCI6XCI0NDQwNTIxMjg1ODc4NDVcIixcInR5cGVcIjoxLFwibG9naW5UeXBlXCI6XCJ3ZWJcIixcImFyZWFDb2RlXCI6XCJcIixcImNyZWF0ZVRpbWVcIjoxNzAyNjIzNDg4MDg0fSIsImV4cCI6MTcwMzIyODI4OH0.eNgrLJTB3Pc5gPr2oyr41GIySJjkEjmPI59MYz2tzP4'
        data = {
            "itemDtoString": "[{\"eventsId\":281,\"eventsSeriesId\":56,\"eventsTypeId\":188,\"goodsType\":0,\"quantity\":1,\"salesPrice\":199900}]",
            "totalPrice": 199900,
            "totalQuantity": 1,
            "cfTurnstileResponse": "",
            "eventsId": 281
        }
        ret = sign_request(
            data=data,
            timestamp=1702641163514,
            nonce="qAjkNvwfTNTzr6GP",
            http_method="POST",
            user_session_token=token
        )
        # pprint(ret)
        assert ret['base64String'] == 'urFeRDK+0Ekl42MeozTylphNx64gGdPH/+zvEllz43E=', '签名不匹配'
        print('POST请求签名测试成功')
        pass



if __name__ == '__main__':
    unittest.main()

