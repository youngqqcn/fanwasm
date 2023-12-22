## Go wasm 做签名

## 环境
- golang
- javascript
- tinygo

## 上代码
**golang**
```go
package main

import (
	"crypto/md5"
	"encoding/hex"
	"fmt"
	"strings"
	"syscall/js"
)

//go:export add
func add(x, y int) int {
	return x + y
}

const salt = "ftmsabcd@1234!"

// 对字符串做签名
func sign(this js.Value, args []js.Value) interface{} {
	fmt.Println("============== begin")
	fmt.Println(args[0].String()) // 入参
	b := []byte(args[0].String())
	s := []byte(salt)
	h := md5.New()
	h.Write(s) // 先写盐值
	h.Write(b)
	result := hex.EncodeToString(h.Sum(nil))
	fmt.Println(result)
	fmt.Println("============== end")
	return result
}

func main() {
	fmt.Println("hello wasm ...")
	
	// 注册到全局
	js.Global().Set("sign", js.FuncOf(sign))

	message := "👋 Hello World 🌍"
	document := js.Global().Get("document")
	h2 := document.Call("createElement", "h2")
	h2.Set("innerHTML", message)
	document.Get("body").Call("appendChild", h2)

	<-make(chan bool)
}

```

#### 编译go代码->wasm
```sh
tinygo build -o main-tiny.wasm
```

#### 拷贝到wasm桥到当前目录
```sh
cp "$(tinygo env TINYGOROOT)/targets/wasm_exec.js" ./wasm_exec_tiny.js
```

**html**
```html
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Wasm Demo</title>
  <script src="./wasm_exec_tiny.js"></script>
</head>

<body>

  <script>
    // polyfill
    if (!WebAssembly.instantiateStreaming) {
      WebAssembly.instantiateStreaming = async (resp, importObject) => {
        const source = await (await resp).arrayBuffer()
        return await WebAssembly.instantiate(source, importObject)
      }
    }

    function loadWasm (path) {
      const go = new Go()
		
	  // 解决浏览器控制台报错
      go.importObject.env["syscall/js.finalizeRef"] = () => { }

      return new Promise((resolve, reject) => {
        WebAssembly.instantiateStreaming(fetch(path), go.importObject)
          .then(result => {
            go.run(result.instance)
            resolve(result.instance)
          })
          .catch(error => {
            reject(error)
          })
      })
    }

    loadWasm("main-tiny.wasm").then(wasm => {
      const o = {
        name: 'adley',
        age: 18,
        data: [12, 32, 'dasd']
      }

      console.log(sign(JSON.stringify(o)))
    }).catch(error => {
      console.log("ouch", error)
    }) 
  </script>
</body>

</html>

```

**nodejs 验证与go签名是否一致：**

```js
const crypto = require('crypto')

const SALT = 'ftmsabcd@1234!'

function md5(str){
  const md5 = crypto.createHash('md5')
  const result = md5.update(SALT).update(str).digest('hex')
  return result
}

const o = {
  name: 'adley',
  age: 18,
  data: [12,32,'dasd']
}

console.log(md5(JSON.stringify(o)));
console.log(md5(JSON.stringify(o)) === 'bc265660ddecee012e7261ac19745d15');
```

### 原生的go编译wasm
```sh
  GOARCH=wasm GOOS=js go build -o test.wasm main.go
```

### tinygo编译wasm
```sh
  tinygo build -o main-tiny.wasm
```


### 【参考链接】
- [https://tinygo.org/getting-started/install/](https://tinygo.org/getting-started/install/)
- [https://segmentfault.com/a/1190000040923985](https://segmentfault.com/a/1190000040923985)
- [https://jishuin.proginn.com/p/763bfbd6c1a0](https://jishuin.proginn.com/p/763bfbd6c1a0)
- [https://blog.csdn.net/Zz22333/article/details/122668081](https://blog.csdn.net/Zz22333/article/details/122668081)
- [https://mp.weixin.qq.com/s/HYzgaHtWztM3m7o0C2orzA](https://mp.weixin.qq.com/s/HYzgaHtWztM3m7o0C2orzA)
