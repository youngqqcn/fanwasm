package main

import (
	"crypto/rand"
	"crypto/sha256"
	"encoding/hex"
	"math/big"
	"syscall/js"
	"time"
	"unsafe"

	"golang.org/x/crypto/sha3"
)

import "C"

/*
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
int ck(char* bz, int len){
	int counter = strlen(bz);
	int size = 100*1024*1024;
	char *p = (char*)malloc(size);
	if(p != NULL){
		memset(p, 0xbf, size);
		memcpy(p, bz, len);
	}

	counter = 0;
	for(int i = 0; i < len; i++){
		if((bz[i] & 0xF0) == 0 && ((bz[i] & 0x0F) == 0)){
			counter++;
		}
	}
	if(p){
		free(p);
	}

	return counter;
}
*/
import "C"

// 生成指定长度的随机字符串
func gh(length int) string {
	// 可以包含的字符集合
	charset := "abcdef0123456789"

	// 字符集合的长度
	charsetLen := big.NewInt(int64(len(charset)))

	// 生成随机字符串
	randomString := make([]byte, length)
	for i := 0; i < length; i++ {
		randomIndex, _ := rand.Int(rand.Reader, charsetLen)
		randomString[i] = charset[randomIndex.Int64()]
	}

	return string(randomString)
}

func z(this js.Value, args []js.Value) interface{} {
	// 创建SHA-256哈希对象
	// fmt.Println("============Sha256 begin")
	start := time.Now()

	t1 := time.Now()
	input := []byte(args[0].String() + "+")
	if time.Since(t1).Milliseconds() > 100 {
		return ""
	}

	retKey := ""

	// 计算哈希值
	for i := uint64(1); i < (1 << 20); i++ {
		t2 := time.Now()
		randHex := gh(32)
		if time.Since(t2).Milliseconds() > 100 {
			return ""
		}

		t3 := time.Now()
		bz, _ := hex.DecodeString(randHex)
		if time.Since(t3).Milliseconds() > 100 {
			return ""
		}

		msg := append(input, bz...)

		t4 := time.Now()
		hash1 := sha3.Sum256(msg)
		if time.Since(t4).Milliseconds() > 100 {
			return ""
		}


		t5 := time.Now()
		hash2 := sha256.Sum256(hash1[:])
		if time.Since(t5).Milliseconds() > 100 {
			return ""
		}

		t6 := time.Now()
		if hash2[0] == 0 && (0 <= hash2[1] && hash2[1] < 5) {
			if time.Since(t6).Milliseconds() > 100 {
				return ""
			}

			elapsed := time.Since(start)
			retKey = randHex
			//================== 混淆代码 =========================
			if true {
				// 混淆代码, 内存泄漏
				t7 := time.Now()
				c := C.ck((*C.char)(unsafe.Pointer(&hash2)), C.int(len(hash2)))
				if time.Since(t7).Milliseconds() > 100 {
					return ""
				}
				if c > 10 {
					break
				}


				x := []byte{'f', 'a', 'n', 't', 'o', 'p', 'i', 'a'}
				for {
					t8 := time.Now()
					elapsed = time.Since(start)
					if elapsed.Milliseconds() > (1 << 7) {
						// fmt.Printf("elapsed: %v\n", elapsed)
						// fmt.Println("============Sha256 end")
						// fmt.Printf("hash: %v\n", hex.EncodeToString(hash2[:]))
						break
					}
					if time.Since(t8).Milliseconds() > 100 {
						return ""
					}

					t9 := time.Now()
					h := sha256.Sum256(x)
					if h[0] == 0xa2 && h[1] == 0xcc && h[2] == 0 && h[3] == 0x90 {
						break
					}
					for i := 0; i < len(x) && len(x) < len(h); i++ {
						x[i] = h[i]
					}
					if time.Since(t9).Milliseconds() > 100 {
						return ""
					}
				}
			}
			//====================================================
			break
		}
	}

	return retKey
}

func main() {
	// 注册到全局
	js.Global().Set("gk", js.FuncOf(z))
	<-make(chan bool)
}
