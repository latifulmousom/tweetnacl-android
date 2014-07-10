package za.co.twyst.tweetnacl;

import za.co.twyst.tweetnacl.exceptions.DecryptException;
import za.co.twyst.tweetnacl.exceptions.EncryptException;

public class TweetNaCl 
       { // CONSTANTS

         public static final int BOX_PUBLICKEYBYTES     = 32;
         public static final int BOX_SECRETKEYBYTES     = 32;
         public static final int BOX_BEFORENMBYTES      = 32;
         public static final int BOX_NONCEBYTES         = 24;
         public static final int BOX_ZEROBYTES          = 32;

         public static final int HSALSA20_OUTPUTBYTES   = 32;
         public static final int HSALSA20_INPUTBYTES    = 16;
         public static final int HSALSA20_KEYBYTES      = 32;
         public static final int HSALSA20_CONSTBYTES    = 16;

         public static final int SALSA20_OUTPUTBYTES    = 64;
         public static final int SALSA20_INPUTBYTES     = 16;
         public static final int SALSA20_KEYBYTES       = 32;
         public static final int SALSA20_CONSTBYTES     = 16;

         public static final int HASH_BYTES             = 64;
         public static final int HASHBLOCKS_STATEBYTES  = 64;
         public static final int HASHBLOCKS_BLOCKBYTES  = 128;

         public static final int ONETIMEAUTH_BYTES      = 16;
         public static final int ONETIMEAUTH_KEYBYTES   = 32;

         public static final int SCALARMULT_BYTES       = 32;
         public static final int SCALARMULT_SCALARBYTES = 32;
         
         public static final int SECRETBOX_KEYBYTES     = 32;
         public static final int SECRETBOX_NONCEBYTES   = 24;
         public static final int SECRETBOX_ZEROBYTES    = 32;
         public static final int SECRETBOX_BOXZEROBYTES = 16;

         // NATIVE METHODS

         private native int jniRandomBytes            (byte[] bytes);
         private native int jniCryptoBoxKeyPair       (byte[] publicKey, byte[] secretKey);
         private native int jniCryptoBox              (byte[] ciphertext,byte[] message,   byte[] nonce,byte[] publicKey,byte[] secretKey);
         private native int jniCryptoBoxOpen          (byte[] message,   byte[] ciphertext,byte[] nonce,byte[] publicKey,byte[] secretKey);
         private native int jniCryptoBoxBeforeNM      (byte[] key,       byte[] publicKey, byte[] secretKey);
         private native int jniCryptoBoxAfterNM       (byte[] ciphertext,byte[] message,   byte[] nonce,byte[] key);
         private native int jniCryptoBoxOpenAfterNM   (byte[] ciphertext,byte[] message,   byte[] nonce,byte[] key);
         private native int jniCryptoCoreHSalsa20     (byte[] out,       byte[] in,        byte[] key,  byte[] constant);
         private native int jniCryptoCoreSalsa20      (byte[] out,       byte[] in,        byte[] key,  byte[] constant);
         private native int jniCryptoHash             (byte[] hash,      byte[] message);
         private native int jniCryptoHashBlocks       (byte[] state,     byte[] message);
         private native int jniCryptoOneTimeAuth      (byte[] signature, byte[] message,   byte[] key);
         private native int jniCryptoOneTimeAuthVerify(byte[] signature, byte[] message,   byte[] key);
         private native int jniCryptoScalarMultBase   (byte[] q,         byte[] n);
         private native int jniCryptoScalarMult       (byte[] q,         byte[] n,         byte[] p);
         private native int jniCryptoSecretBox        (byte[] ciphertext,byte[] message,   byte[] nonce,byte[] key);

         // CLASS METHODS

         static { System.loadLibrary("tweetnacl");
                }
    
         // PUBLIC API
    
         /** Releases any resources acquired by the native library.
           * 
           */
         public void release() 
                {
                }
    
         /** Wrapper function for <code>randombytes<code>.
           * <p>
           * The <code>randombytes</code> function is not part of the TweetNaCl API but
           * is exposed here for test purposes. 
           * 
           * @param bytes
           * 
           * @throws Exception
           */
    public void randomBytes(final byte[] bytes) {
        // ... validate
        
        if (bytes == null) {
            throw new IllegalArgumentException("Invalid 'bytes' - may not be null");
        }
        
        // ... fill with random bytes
        
        jniRandomBytes(bytes);
    }    

    /** Wrapper function for crypto_box_keypair.
     * 
     * @return Initialised KeyPair.
     */
    public KeyPair cryptoBoxKeyPair() {
        // ... validate
        
        // ... get key pair
        
        byte[] publicKey = new byte[BOX_PUBLICKEYBYTES];
        byte[] secretKey = new byte[BOX_SECRETKEYBYTES];
        
        jniCryptoBoxKeyPair(publicKey,secretKey);
        
        return new KeyPair(publicKey,secretKey);
    }

    /** Wrapper function for crypto_box.
     * 
     * @param message
     * @param nonce
     * @param publicKey
     * @param secretKey
     * 
     * @return ciphertext
     * 
     * @throws Exception
     */
    public byte[] cryptoBox(final byte[] message,final byte[] nonce,byte[] publicKey,byte[] secretKey) throws EncryptException {
        // ... validate
        
        if (message == null) {
            throw new IllegalArgumentException("Invalid 'message' - may not be null");
        }
        
        if ((nonce == null) || (nonce.length != BOX_NONCEBYTES)) {
            throw new IllegalArgumentException("Invalid 'nonce' - must be 24 bytes");
        }

        if ((publicKey == null) || (publicKey.length != BOX_PUBLICKEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'public key' - must be 32 bytes");
        }

        if ((secretKey == null) || (secretKey.length != BOX_SECRETKEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'secret key' - must be 32 bytes");
        }
        
        // ... encrypt
        
        byte[] ciphertext = new byte[message.length];
        int    rc;

        if ((rc = jniCryptoBox(ciphertext,message,nonce,publicKey,secretKey)) != 0) {
            throw new EncryptException("Error encrypting message [" + Integer.toString(rc) + "]");
        }
        
        return ciphertext;
    }    
    
    public byte[] cryptoBoxOpen(final byte[] ciphertext,final byte[] nonce,byte[] publicKey,byte[] secretKey) throws DecryptException {
        // ... validate
        
        if (ciphertext == null) {
            throw new IllegalArgumentException("Invalid 'ciphertext' - may not be null");
        }
        
        if ((nonce == null) || (nonce.length != BOX_NONCEBYTES)) {
            throw new IllegalArgumentException("Invalid 'nonce' - must be 24 bytes");
        }

        if ((publicKey == null) || (publicKey.length != BOX_PUBLICKEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'public key' - must be 32 bytes");
        }

        if ((secretKey == null) || (secretKey.length != BOX_SECRETKEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'secret key' - must be 32 bytes");
        }
        
        // ... encrypt
        
        byte[] message = new byte[ciphertext.length];
        int    rc;
        
        if ((rc = jniCryptoBoxOpen(message,ciphertext,nonce,publicKey,secretKey)) != 0) {
            throw new DecryptException("Error decrypting message [" + Integer.toString(rc) + "]");
        }
        
        return message;
    }

    /** Wrapper function for crypto_box_beforenm.
     * 
     * @param publicKey
     * @param secretKey
     * 
     * @return key
     * 
     * @throws Exception
     */
    public byte[] cryptoBoxBeforeNM(byte[] publicKey,byte[] secretKey) throws Exception {
        // ... validate
        
        if ((publicKey == null) || (publicKey.length != BOX_PUBLICKEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'public key' - must be 32 bytes");
        }

        if ((secretKey == null) || (secretKey.length != BOX_SECRETKEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'secret key' - must be 32 bytes");
        }
        
        // ... encrypt
        
        byte[] key = new byte[BOX_BEFORENMBYTES];
        int    rc;

        if ((rc = jniCryptoBoxBeforeNM(key,publicKey,secretKey)) != 0) {
            throw new Exception("Error generating message key [" + Integer.toString(rc) + "]");
        }
        
        return key;
    }    

    /** Wrapper function for crypto_box_afternm.
     * 
     * @param message
     * @param nonce
     * @param key
     * 
     * @return ciphertext
     * 
     * @throws Exception
     */
    public byte[] cryptoBoxAfterNM(final byte[] message,final byte[] nonce,byte[] key) throws EncryptException {
        // ... validate
        
        if (message == null) {
            throw new IllegalArgumentException("Invalid 'message' - may not be null");
        }
        
        if ((nonce == null) || (nonce.length != BOX_NONCEBYTES)) {
            throw new IllegalArgumentException("Invalid 'nonce' - must be " + Integer.toString(BOX_NONCEBYTES) + " bytes");
        }

        if ((key == null) || (key.length != BOX_BEFORENMBYTES)) {
            throw new IllegalArgumentException("Invalid 'message key' - must be " + Integer.toString(BOX_BEFORENMBYTES) + " bytes");
        }
        
        // ... encrypt
        
        byte[] ciphertext = new byte[message.length];
        int    rc;

        if ((rc = jniCryptoBoxAfterNM(ciphertext,message,nonce,key)) != 0) {
            throw new EncryptException("Error encrypting message [" + Integer.toString(rc) + "]");
        }
        
        return ciphertext;
    }    
    
    public byte[] cryptoBoxOpenAfterNM(final byte[] ciphertext,final byte[] nonce,byte[] key) throws DecryptException {
        // ... validate
        
        if (ciphertext == null) {
            throw new IllegalArgumentException("Invalid 'ciphertext' - may not be null");
        }
        
        if ((nonce == null) || (nonce.length != BOX_NONCEBYTES)) {
            throw new IllegalArgumentException("Invalid 'nonce' - must be " + Integer.toString(BOX_NONCEBYTES) + " bytes");
        }

        if ((key == null) || (key.length != BOX_BEFORENMBYTES)) {
            throw new IllegalArgumentException("Invalid 'message key' - must be " + Integer.toString(BOX_BEFORENMBYTES) + " bytes");
        }

        // ... encrypt
        
        byte[] message = new byte[ciphertext.length];
        int    rc;
        
        if ((rc = jniCryptoBoxOpenAfterNM(message,ciphertext,nonce,key)) != 0) {
            throw new DecryptException("Error decrypting message [" + Integer.toString(rc) + "]");
        }
        
        return message;
    }

    /** Wrapper function for crypto_core_hsalsa20.
     * 
     * @param in
     * @param key
     * @param constant
     * 
     * @return out
     * 
     * @throws Exception
     */
    public byte[] cryptoCoreHSalsa20(final byte[] in,final byte[] key,byte[] constant) throws EncryptException {
        // ... validate
        
        if ((in == null) || (in.length != HSALSA20_INPUTBYTES)) {
            throw new IllegalArgumentException("Invalid 'in' - must be " + Integer.toString(HSALSA20_INPUTBYTES) + " bytes");
        }

        if ((key == null) || (key.length != HSALSA20_KEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'key' - must be " + Integer.toString(HSALSA20_KEYBYTES) + " bytes");
        }

        if ((constant == null) || (constant.length != HSALSA20_CONSTBYTES)) {
            throw new IllegalArgumentException("Invalid 'constant' - must be " + Integer.toString(HSALSA20_CONSTBYTES) + " bytes");
        }
        
        // ... invoke
        
        byte[] out = new byte[HSALSA20_OUTPUTBYTES];
        int    rc;

        if ((rc = jniCryptoCoreHSalsa20(out,in,key,constant)) != 0) {
            throw new EncryptException("Error encrypting message [" + Integer.toString(rc) + "]");
        }
        
        return out;
    }    

    /** Wrapper function for crypto_core_salsa20.
     * 
     * @param in
     * @param key
     * @param constant
     * 
     * @return out
     * 
     * @throws Exception
     */
    public byte[] cryptoCoreSalsa20(final byte[] in,final byte[] key,byte[] constant) throws EncryptException {
        // ... validate
        
        if ((in == null) || (in.length != SALSA20_INPUTBYTES)) {
            throw new IllegalArgumentException("Invalid 'in' - must be " + Integer.toString(SALSA20_INPUTBYTES) + " bytes");
        }

        if ((key == null) || (key.length != SALSA20_KEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'key' - must be " + Integer.toString(SALSA20_KEYBYTES) + " bytes");
        }

        if ((constant == null) || (constant.length != SALSA20_CONSTBYTES)) {
            throw new IllegalArgumentException("Invalid 'constant' - must be " + Integer.toString(SALSA20_CONSTBYTES) + " bytes");
        }
        
        // ... invoke
        
        byte[] out = new byte[SALSA20_OUTPUTBYTES];
        int    rc;

        if ((rc = jniCryptoCoreSalsa20(out,in,key,constant)) != 0) {
            throw new EncryptException("Error encrypting message [" + Integer.toString(rc) + "]");
        }
        
        return out;
    }
    
    /** Wrapper function for crypto_hash.
     * 
     * @param  message
     * @return hash
     * 
     * @throws Exception
     */
    public byte[] cryptoHash(final byte[] message) throws EncryptException {
        // ... validate
        
        if (message == null) {
            throw new IllegalArgumentException("Invalid 'message'");
        }
        
        // ... invoke
        
        byte[] hash = new byte[HASH_BYTES];
        int    rc;

        if ((rc = jniCryptoHash(hash,message)) != 0) {
            throw new EncryptException("Error calculating message hash [" + Integer.toString(rc) + "]");
        }
        
        return hash;
    }    
    
    /** Wrapper function for crypto_hashblocks.
      * 
      * @param  message
      * @return hash
      * 
      * @throws Exception
      */
    public byte[] cryptoHashBlocks(final byte[] x,final byte[] m) throws EncryptException {
        // ... validate
        
        if ((x == null) || (x.length != HASHBLOCKS_STATEBYTES)) {
            throw new IllegalArgumentException("Invalid 'x' - must be " + Integer.toString(HASHBLOCKS_BLOCKBYTES) + " bytes");
        }
        
        if ((m == null) || ((m.length % HASHBLOCKS_BLOCKBYTES) != 0)) {
            throw new IllegalArgumentException("Invalid 'm' - must be a multiple of " + Integer.toString(HASHBLOCKS_BLOCKBYTES) + " bytes");
        }
        
        // ... invoke
        
        byte[] h = x.clone();

        jniCryptoHashBlocks(h,m);
        
        return h;
    }    
    
    /** Wrapper function for crypto_onetimeauth.
      * 
      * @param  message
      * @param  key
      * @return hash
      * 
      * @throws Exception
      */
    public byte[] cryptoOneTimeAuth(final byte[] message,final byte[] key) throws EncryptException {
        // ... validate
        
        if (message == null) {
            throw new IllegalArgumentException("Invalid 'message'");
        }
        
        if ((key == null) || (key.length  != ONETIMEAUTH_KEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'key' - length must be " + Integer.toString(ONETIMEAUTH_KEYBYTES) + " bytes");
        }
        
        // ... invoke
        
        byte[] authorisation = new byte[ONETIMEAUTH_BYTES];

        jniCryptoOneTimeAuth(authorisation,message,key);
        
        return authorisation;
    }    

    /** Wrapper function for crypto_onetimeauth_verify.
      * 
      * @param  authorisation
      * @param  message
      * @param  key
      * @return verified
      * 
      * @throws Exception
      */
    public boolean cryptoOneTimeAuthVerify(final byte[] authorisation,final byte[] message,final byte[] key) throws EncryptException {
        // ... validate
        
        if ((authorisation == null) || (authorisation.length  != ONETIMEAUTH_BYTES)) {
            throw new IllegalArgumentException("Invalid 'authorisation' - length must be " + Integer.toString(ONETIMEAUTH_KEYBYTES) + " bytes");
        }
        
        if (message == null) {
            throw new IllegalArgumentException("Invalid 'message'");
        }
        
        if ((key == null) || (key.length  != ONETIMEAUTH_KEYBYTES)) {
            throw new IllegalArgumentException("Invalid 'key' - length must be " + Integer.toString(ONETIMEAUTH_KEYBYTES) + " bytes");
        }
        
        // ... invoke

        return jniCryptoOneTimeAuthVerify(authorisation,message,key) == 0;
    }    
    
    /** Wrapper function for crypto_scalarmult_base.
     * 
     * @param  n
     * @return q
     * 
     * @throws Exception
     */
   public byte[] cryptoScalarMultBase(final byte[] n) throws EncryptException {
       // ... validate
       
       if ((n == null) || (n.length  != SCALARMULT_SCALARBYTES)) {
           throw new IllegalArgumentException("Invalid 'n' - length must be " + Integer.toString(SCALARMULT_SCALARBYTES) + " bytes");
       }
       
       // ... invoke

       byte[] q = new byte[SCALARMULT_BYTES];
       int    rc;

       if ((rc = jniCryptoScalarMultBase(q,n)) != 0) {
           throw new EncryptException("Error calculating scalarmult_base [" + Integer.toString(rc) + "]");
       }
       
       return q;
   }    
   
   /** Wrapper function for crypto_scalarmult.
    * 
    * @param  n
    * @param  p
    * @return q
    * 
    * @throws Exception
    */
  public byte[] cryptoScalarMult(final byte[] n,final byte[] p) throws EncryptException {
      // ... validate
      
      if ((n == null) || (n.length  != SCALARMULT_SCALARBYTES)) {
          throw new IllegalArgumentException("Invalid 'n' - length must be " + Integer.toString(SCALARMULT_SCALARBYTES) + " bytes");
      }
      
      if ((p == null) || (p.length  != SCALARMULT_BYTES)) {
          throw new IllegalArgumentException("Invalid 'p' - length must be " + Integer.toString(SCALARMULT_BYTES) + " bytes");
      }
      
      // ... invoke

      byte[] q = new byte[SCALARMULT_BYTES];
      int    rc;

      if ((rc = jniCryptoScalarMult(q,n,p)) != 0) {
          throw new EncryptException("Error calculating scalarmult [" + Integer.toString(rc) + "]");
      }
      
      return q;
  }    

  /** Wrapper function for crypto_secretbox.
   * 
   * @param  message
   * @param  key
   * @param  nonce
   * @return ciphertext
   * 
   * @throws Exception
   */
 public byte[] cryptoSecretBox(final byte[] message,final byte[] nonce,final byte[] key) throws EncryptException {
     // ... validate
     
     if (message == null) {
         throw new IllegalArgumentException("Invalid 'message'");
     }
     
     if ((nonce == null) || (nonce.length  != SECRETBOX_NONCEBYTES)) {
         throw new IllegalArgumentException("Invalid 'nonce' - length must be " + Integer.toString(SECRETBOX_NONCEBYTES) + " bytes");
     }
     
     if ((key == null) || (key.length  != SECRETBOX_KEYBYTES)) {
         throw new IllegalArgumentException("Invalid 'key' - length must be " + Integer.toString(SECRETBOX_KEYBYTES) + " bytes");
     }
     
     // ... invoke

     byte[] ciphertext = new byte[message.length];
     int    rc;

     if ((rc = jniCryptoSecretBox(ciphertext,message,nonce,key)) != 0) {
         throw new EncryptException("Error encrypting message [" + Integer.toString(rc) + "]");
     }
     
     return ciphertext;
 }    

    // INNER CLASSES
    
    public static final class KeyPair {
        public final byte[] publicKey = new byte[BOX_PUBLICKEYBYTES];
        public final byte[] secretKey = new byte[BOX_SECRETKEYBYTES];
        
        private KeyPair(byte[] publicKey,byte[] secretKey) {
            System.arraycopy(publicKey,0,this.publicKey,0,BOX_PUBLICKEYBYTES);
            System.arraycopy(secretKey,0,this.secretKey,0,BOX_SECRETKEYBYTES);
        }
    }
}
