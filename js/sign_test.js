import {W} from './sign.mjs'

let e = {};
let t = {
    "transitional": {
        "silentJSONParsing": true,
        "forcedJSONParsing": true,
        "clarifyTimeoutError": false
    },
    "adapter": [
        "xhr",
        "http"
    ],
    "transformRequest": [
        null
    ],
    "transformResponse": [
        null
    ],
    "timeout": 5000,
    "xsrfCookieName": "XSRF-TOKEN",
    "xsrfHeaderName": "X-XSRF-TOKEN",
    "maxContentLength": -1,
    "maxBodyLength": -1,
    "env": {},
    "headers": {
        "Accept": "application/json, text/plain, */*",
        "Content-Type": "application/json;charset=UTF-8"
    },
    "transform": {},
    "requestOptions": {
        "joinParamsToUrl": false,
        "formatDate": true,
        "isTransformRequestResult": true,
        "isReturnNativeResponse": false,
        "isShowMessage": true,
        "isShowToast": false,
        "successMessageText": "",
        "isShowSuccessMessage": false,
        "isShowErrorMessage": false,
        "errorMessageText": "",
        "joinTime": false,
        "ignoreCancelToken": true,
        "withToken": true,
        "joinPrefix": true,
        "urlPrefix": "",
        "apiUrl": "/fanapiWeb",
        "errorMessageMode": "none",
        "isUserBasicUrl": true,
        "isEncryptParams": true
    },
    "url": "/fanapiWeb/order/getEventsUnSuccessOrder",
    "method": "get",
    "params": {
        "eventsId": 281,
        "seriesId": 56
    },
    "baseURL": ""
}



const a  = W(e, t);
console.log(a.base64String)
console.log(a.commonParams)


/*
2njOemlKlwZak38fkm5GPnI8s4CnVZOZYTZnLS2ap60=

{
  platform: 'web',
  timestamp: 1702635696518,
  nonce: '68DmB1SpBfhqAeaE',
  token: 'eyJUeXBlIjoiSnd0IiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJGQU5UT1BJQSIsImpzb25EYXRhIjoie1widXNlcklkXCI6XCI0NDQwNTIxMjg1ODc4NDVcIixcInR5cGVcIjoxLFwibG9naW5UeXBlXCI6XCJ3ZWJcIixcImFyZWFDb2RlXCI6XCJcIixcImNyZWF0ZVRpbWVcIjoxNzAyNjIzNDg4MDg0fSIsImV4cCI6MTcwMzIyODI4OH0.eNgrLJTB3Pc5gPr2oyr41GIySJjkEjmPI59MYz2tzP4'
}

*/

