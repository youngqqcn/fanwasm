use js_sys::*;
use wasm_bindgen::prelude::*;

use rand::rngs::StdRng;
use rand::{Rng, SeedableRng};
use sha2::Sha256;
use sha3::{Digest, Sha3_256};

pub fn add(a: i32, b: i32) -> i32 {
    a + b
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
        let h: &[u8] = md5hash.as_ref();
        let rnd_hex: String = h.iter().map(|byte| format!("{:02x}", byte)).collect();

        let msg = pre.clone() + &rnd_hex;
        let bz = msg.as_bytes();
        let hash1 = Sha3_256::digest(bz);
        let hash2 = Sha256::digest(hash1);
        if hash2[0] == 0 && (hash2[1] < 5) {
            return rnd_hex;
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
