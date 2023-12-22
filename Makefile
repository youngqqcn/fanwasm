all:build

.PHONY: clean
clean:
	rm -rf pkg

.PHONY: build
build: clean
	wasm-pack build --target web
	node replace.js
	mv ./pkg/wasm_bg.wasm ./pkg/wasm.wasm
