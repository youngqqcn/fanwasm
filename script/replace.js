var fs = require("fs");

// web端
if (true) {
  var content = fs.readFileSync("./pkg/wasm.js", "utf-8");
  var encoder_js = fs.readFileSync("./script/encoding_utf8.min.js", "utf-8");

  // 删除 init函数中的两个行
    //   content = content.replace(`input = fetch(input);`, "");
  content = content.replace(
    `input = new URL('wasm_bg.wasm', import.meta.url);`,
    ""
  );

  content = content.replace(
    `function __wbg_init_memory(imports, maybe_memory) {`,
    "function __wbg_init_memory(imports, maybe_memory) {\n    //todo __wbg_init_memory"
  );


  // if (typeof Response === 'function' && module instanceof Response)   改成  if(true)
//   content = content.replace(
//     `typeof Response === 'function' && module instanceof Response`,
//     "true"
//   );
//   content = content.replace(
//     `typeof WebAssembly.instantiateStreaming === 'function'`,
//     "true"
//   );
  fs.writeFileSync("./pkg/wasm-web.js", content);
}

// 小程序
if (true) {
  // 删除 init函数中的两个行
  content = content.replace(
    `input = new URL('wasm_bg.wasm', import.meta.url);`,
    ""
  );

  // if (typeof Response === 'function' && module instanceof Response)   改成  if(true)
  content = content.replace(`input = fetch(input);`, "");

  // 替换 Webassembly
  content = encoder_js + "\n" + content;

  content = content.replaceAll("WebAssembly", "WXWebAssembly");

  fs.writeFileSync("./pkg/wasm-wxmini.js", content);
}
