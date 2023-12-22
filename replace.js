var fs = require("fs");

var content = fs.readFileSync("./pkg/wasm.js", "utf-8");

var encoder_js = fs.readFileSync("./asset/encoding_utf8.min.js", "utf-8");

// 加入 encoder
content = encoder_js + '\n' + content;

// 替换 Webassembly
content = content.replaceAll('WebAssembly', 'WXWebAssembly');

// 删除 init函数中的两个行
content = content.replace(`input = new URL('hello_bg.wasm', import.meta.url);`, '');

content = content.replace(`input = fetch(input);`, '');

fs.writeFileSync("./pkg/wasm-wxmini.js", content);