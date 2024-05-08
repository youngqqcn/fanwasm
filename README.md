# wasm签名

- 安装 `wasm-pack`:
  - rust环境: `cargo install wasm-pack`
  - node环境: `npm install -g wasm-pack` 或 `yarn global add wasm-pack`

- 生成 wasm文件: `make build`
  - wasm文件: `./pkg/wasm.wasm`
  - 小程序: `./pkg/wasm-wxmini.js`
  - web端：`./pkg/wasm-web.js`

- Java后端验证代码:
  - `java/fansig`

- 目录结构

```
./
├── Cargo.lock
├── Cargo.toml
├── go
│   ├── go.mod
│   ├── go.sum
│   ├── main.go
│   └── README.md
├── java
│   └── fansig       // Java后端验证代码和测试和用例
├── js
│   ├── node_modules
│   ├── package.json
│   ├── package-lock.json
│   ├── README.md
│   ├── sign.mjs
│   └── sign_test.js
├── Makefile
├── python
│   └── sign.py
├── README.md
├── script
│   ├── encoding_utf8.min.js
│   └── replace.js
└── src
    └── lib.rs      // rust签名算法实现
```