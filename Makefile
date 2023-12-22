all:build

.PHONY: clean
clean:
	rm -rf pkg

.PHONY: build
build: clean
	# wasm-pack build
	wasm-pack build --target web
	node ./script/replace.js
	mv ./pkg/wasm_bg.wasm ./pkg/wasm.wasm

.PHONY: test
test:
	cargo test -- --nocapture