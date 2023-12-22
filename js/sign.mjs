// 导入CryptoJS库 (确保已经安装了CryptoJS库)
import CryptoJS from "crypto-js";

function X() {
  const s =
    "eyJUeXBlIjoiSnd0IiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJGQU5UT1BJQSIsImpzb25EYXRhIjoie1widXNlcklkXCI6XCI0NDQwNTIxMjg1ODc4NDVcIixcInR5cGVcIjoxLFwibG9naW5UeXBlXCI6XCJ3ZWJcIixcImFyZWFDb2RlXCI6XCJcIixcImNyZWF0ZVRpbWVcIjoxNzAyNjIzNDg4MDg0fSIsImV4cCI6MTcwMzIyODI4OH0.eNgrLJTB3Pc5gPr2oyr41GIySJjkEjmPI59MYz2tzP4";
  return s;
}

export const W = (e, t) => {
  var i;
  const s = X(), // X() 返回会话token
    r = {
      platform: "web",
      timestamp: 1702635696518 ,//new Date().getTime(),
      nonce: "68DmB1SpBfhqAeaE",
    };
  s && (r.token = s),
    e &&
      Object.keys(e).forEach((a) => {
        (e[a] === null || e[a] === void 0) && (e[a] = "");
      });
  let n = {
    ...r,
  };
  ((i = t == null ? void 0 : t.method) == null ? void 0 : i.toUpperCase()) ==
    "POST" &&
    t.requestOptions.isEncryptParams &&
    (n = {
      ...r,
      ...e,
    });
  const l = Object.keys(n),
    c = [];
  l.sort().forEach((a, d) => {
    c[d] = "".concat(a.toLowerCase(), "=").concat(n[a]);
  });

  console.log(c.join("&"))
  console.log('====')

  const o = CryptoJS.HmacSHA256(
    c.join("&"),

  );
  tmpO = {
    ...o,
    ...key,
  }
  console.log( o.toString())
  return {
    base64String: CryptoJS.enc.Base64.stringify(o),
    commonParams: r,
  };
};
