package za.co.twyst.tweetnacl.benchmark.entity;

import android.os.Parcel;
import android.os.Parcelable;

/** Container class for a performance measurement.
 * 
 */
public class Benchmark implements Parcelable {
    // CONSTANTS
    
    public enum TYPE { 
        UNKNOWN                  (0),
        CRYPTO_BOX               (1),
        CRYPTO_BOX_OPEN          (2),
        CRYPTO_CORE_HSALSA20     (3),
        CRYPTO_CORE_SALSA20      (4),
        CRYPTO_HASH              (5),
        CRYPTO_HASHBLOCKS        (6),
        CRYPTO_ONETIMEAUTH       (7),
        CRYPTO_ONETIMEAUTH_VERIFY(8),
        CRYPTO_SCALARMULT_BASE   (9),
        CRYPTO_SCALARMULT        (10),
        CRYPTO_SECRETBOX         (11),
        CRYPTO_SECRETBOX_OPEN    (12);
        
        private final int type;
        
        private TYPE(int type) {
            this.type = type;
        }
        
        private static TYPE parse(int type) {
            for (TYPE item: values()) {
                if (item.type == type) {
                    return item;
                }
            }
            
            return UNKNOWN;
        }
    };
    
    // INSTANCE VARIABLES
    
    public final TYPE   type;
    public final String value;
    
    // CONSTRUCTOR
    
    /** Initialises the measurement values.
     * 
     */
    public Benchmark(TYPE type,String value) {
        // ... validate
        
        if (type == null) {
            throw new IllegalArgumentException("Invalid measurement type");
        }
        
        // ... initialise
        
        this.type  = type;
        this.value = value == null ? "" : value.trim();
    }

    // *** Parcelable ***
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel,int flags) {
        parcel.writeInt   (type.type);
        parcel.writeString(value);
    }
    
    public static final Parcelable.Creator<Benchmark> CREATOR = new Parcelable.Creator<Benchmark>() {
        @Override
        public Benchmark createFromParcel(Parcel parcel) { 
            int    type  = parcel.readInt();
            String value = parcel.readString();
            
            return new Benchmark(TYPE.parse(type),value);
        }

        @Override
        public Benchmark[] newArray(int size) {
            return new Benchmark[size];
        }
    };
}
