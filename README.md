# fantopia签名

- 安装 `wasm-pack`:
  - rust环境: `cargo install wasm-pack`
  - node环境: `npm install -g wasm-pack` 或 `yarn global add wasm-pack`

- 生成 wasm文件: `make build`
  - wasm文件: `./pkg/wasm.wasm`
  - 小程序: `./pkg/wasm-wxmini.js`
  - web端：`./pkg/wasm-web.js`

- Java后端验证代码:
  - `java/fansig`

