var fs = require("fs");

var content = fs.readFileSync("./pkg/wasm.js", "utf-8");

var encoder_js = fs.readFileSync("./script/encoding_utf8.min.js", "utf-8");


content = encoder_js + '\n' + content;

// 删除 init函数中的两个行
content = content.replace(`input = fetch(input);`, '');

content = content.replace(`input = new URL('wasm_bg.wasm', import.meta.url);`, '');

fs.writeFileSync("./pkg/wasm-web.js", content);

// 替换 Webassembly

content = content.replaceAll('WebAssembly', 'WXWebAssembly');

fs.writeFileSync("./pkg/wasm-wxmini.js", content);