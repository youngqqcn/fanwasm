use js_sys::*;
use wasm_bindgen::prelude::*;

use base64::{engine::general_purpose, Engine as _};
use hmac::{Hmac, Mac};
use rand::rngs::StdRng;
use rand::{Rng, SeedableRng};
use sha2::Sha256;
use sha3::{Digest, Sha3_256};
// use base64;

fn hmac_sha256(key: &str, message: &str) -> Vec<u8> {
    let key_bytes = key.as_bytes();
    let message_bytes = message.as_bytes();

    let mut hmac =
        Hmac::<Sha256>::new_from_slice(key_bytes).expect("HMAC can take key of any size");
    hmac.update(message_bytes);

    let result = hmac.finalize().into_bytes();
    result.into_iter().collect()
}

fn bytes2hex(bz: &[u8]) -> String {
    bz.iter().map(|byte| format!("{:02x}", byte)).collect()
}

#[wasm_bindgen]
pub fn genkey(data: String, timestamp: u64) -> String {
    let input: String = data;
    let pre = input.clone() + "+";
    let mut seed: [u8; 32] = [0; 32];
    let hash = Sha3_256::digest(format!("{:?}+{:?}", &pre, timestamp).as_bytes());
    seed.copy_from_slice(&hash);

    // 微信小程序里面不能用获取系统随机源，会报错，只能通过传入参数进行伪随机
    let mut rng = StdRng::from_seed(seed);

    for _i in 0..(1 << 30) {
        let mut rnd_hex = String::from("");
        for _j in 0..16 {
            let random_byte: u8 = rng.gen();
            rnd_hex.push_str(format!("{:02x}", random_byte).as_str())
        }

        let md5hash = md5::compute(rnd_hex.as_bytes());
        let key_bz: &[u8] = md5hash.as_ref();
        let key_hex: String = bytes2hex(key_bz);

        let msg = pre.clone() + &key_hex;
        let bz = msg.as_bytes();
        let hash1 = Sha3_256::digest(bz);
        let hash2 = Sha256::digest(hash1);
        if hash2[0] == 0 && (hash2[1] < 5) {
            // 然后用key进行签名
            let sig = hmac_sha256(&key_hex, &(input.clone().as_str()));

            // 将签名 和 key拼接起来
            let sig_hex: String = bytes2hex(&sig);
            println!("sig_hex = {:?}", sig_hex);
            println!("key_hex = {:?}", key_hex);

            let mut mix = sig.clone();
            mix.extend_from_slice(key_bz);
            let b64 = general_purpose::STANDARD_NO_PAD.encode(mix);
            return b64;
        }
    }
    String::from("timeout")
}

#[cfg(test)]
mod tests {
    #[test]
    fn it_works() {
        let ret = crate::genkey(String::from("hello"), 1234);
        assert!(!ret.is_empty());
    }
}
