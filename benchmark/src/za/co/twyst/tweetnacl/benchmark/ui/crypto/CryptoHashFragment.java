package za.co.twyst.tweetnacl.benchmark.ui.crypto;

import java.util.Random;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import za.co.twyst.tweetnacl.TweetNaCl;
import za.co.twyst.tweetnacl.benchmark.R;
import za.co.twyst.tweetnacl.benchmark.entity.Benchmark;
import za.co.twyst.tweetnacl.benchmark.entity.Benchmark.TYPE;
import za.co.twyst.tweetnacl.benchmark.ui.widgets.Grid;

public class CryptoHashFragment extends CryptoFragment {
    // CONSTANTS

    private static final String TAG          = CryptoHashFragment.class.getSimpleName();
    private static final int    MESSAGE_SIZE = 16384;
    private static final int    LOOPS        = 65536;
    
    private static final int[] ROWS    = { R.string.results_measured,
                                           R.string.results_average,
                                           R.string.results_min,
                                           R.string.results_max
                                         };

    private static final int[] COLUMNS = { R.string.column_hash, 
                                           R.string.column_hashblocks
                                         };
    
    private static final byte[] IV = { (byte) 0x6a, (byte) 0x09, (byte) 0xe6, (byte) 0x67,
                                       (byte) 0xf3, (byte) 0xbc, (byte) 0xc9, (byte) 0x08,
                                       (byte) 0xbb, (byte) 0x67, (byte) 0xae, (byte) 0x85,
                                       (byte) 0x84, (byte) 0xca, (byte) 0xa7, (byte) 0x3b,
                                       (byte) 0x3c, (byte) 0x6e, (byte) 0xf3, (byte) 0x72,
                                       (byte) 0xfe, (byte) 0x94, (byte) 0xf8, (byte) 0x2b,
                                       (byte) 0xa5, (byte) 0x4f, (byte) 0xf5, (byte) 0x3a,
                                       (byte) 0x5f, (byte) 0x1d, (byte) 0x36, (byte) 0xf1,
                                       (byte) 0x51, (byte) 0x0e, (byte) 0x52, (byte) 0x7f,
                                       (byte) 0xad, (byte) 0xe6, (byte) 0x82, (byte) 0xd1,
                                       (byte) 0x9b, (byte) 0x05, (byte) 0x68, (byte) 0x8c,
                                       (byte) 0x2b, (byte) 0x3e, (byte) 0x6c, (byte) 0x1f,
                                       (byte) 0x1f, (byte) 0x83, (byte) 0xd9, (byte) 0xab,
                                       (byte) 0xfb, (byte) 0x41, (byte) 0xbd, (byte) 0x6b,
                                       (byte) 0x5b, (byte) 0xe0, (byte) 0xcd, (byte) 0x19,
                                       (byte) 0x13, (byte) 0x7e, (byte) 0x21, (byte) 0x79 
                                     };
    
    // INSTANCE VARIABLES
    
    private Measured hash       = new Measured();
    private Measured hashblocks = new Measured();
    
    // CLASS METHODS

    /** Factory constructor for CryptoBoxFragment that ensures correct fragment
     *  initialisation.
     *  
     * @return Initialised CryptoBoxFragment or <code>null</code>.
     */
    public static Fragment newFragment() {
        return new CryptoHashFragment();
    }

    // *** Fragment ***
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        final View        root  = inflater.inflate(R.layout.fragment_hash,container,false);
        final EditText    size  = (EditText) root.findViewById(R.id.size); 
        final EditText    loops = (EditText) root.findViewById(R.id.loops); 
        final Button      run   = (Button) root.findViewById(R.id.run);
        final Grid        grid  = (Grid) root.findViewById(R.id.grid);
        final ProgressBar bar   = (ProgressBar) root.findViewById(R.id.progressbar);

        // ... initialise default setup
        
        size.setText (Integer.toString(MESSAGE_SIZE));
        loops.setText(Integer.toString(LOOPS));

        // ... initialise grid
        
        grid.setRowLabels   (ROWS,   inflater,R.layout.label,R.id.textview);
        grid.setColumnLabels(COLUMNS,inflater,R.layout.value,R.id.textview);
        grid.setValues      (ROWS.length,COLUMNS.length,inflater,R.layout.value,R.id.textview);
        
        // ... attach handlers
        
        run.setOnClickListener(new OnClickListener()
                                   { @Override
                                     public void onClick(View view)
                                            { try
                                                 { int _size  = Integer.parseInt(size.getText ().toString());
                                                   int _loops = Integer.parseInt(loops.getText().toString());
                                                 
                                                   hideKeyboard(size,loops);
                                                   run         (_size,_loops,bar);
                                                 }
                                              catch(Throwable x)
                                                 { // TODO
                                                 }
                                            }
                                   });
        
        return root;
    }
    
    // INTERNAL
    
    private void run(int bytes,int loops,ProgressBar bar) {
        new CryptoHashTask(this,bar,bytes,loops).execute();
    }
    
    @Override
    protected void done(Result...results) {
        View view = getView();
        View busy;
        View bar;

        // ... hide windmill
        
        if (view != null) {
            if ((busy = view.findViewById(R.id.busy)) != null) {
                busy.setVisibility(View.GONE);
            }
            
            if ((bar = view.findViewById(R.id.progressbar)) != null) {
                bar.setVisibility(View.VISIBLE);
            }
        }
        
        // ... update benchmarks
        
        this.hash.update      (results[0].bytes,results[0].dt);
        this.hashblocks.update(results[1].bytes,results[1].dt);


        if (view != null) {
            Grid grid = (Grid) view.findViewById(R.id.grid);
            
            grid.setValue(0,0,format(this.hash.throughput));
            grid.setValue(1,0,format(this.hash.mean));
            grid.setValue(2,0,format(this.hash.minimum));
            grid.setValue(3,0,format(this.hash.maximum));
            
            grid.setValue(0,1,format(this.hashblocks.throughput));
            grid.setValue(1,1,format(this.hashblocks.mean));
            grid.setValue(2,1,format(this.hashblocks.minimum));
            grid.setValue(3,1,format(this.hashblocks.maximum));
        }
        
        // ... update global measurements
        
        this.measured(new Benchmark(TYPE.CRYPTO_HASH,      format(this.hash.mean)),
                      new Benchmark(TYPE.CRYPTO_HASHBLOCKS,format(this.hashblocks.mean)));
    }
    
    // INNER CLASSES
    
    private static class CryptoHashTask extends RunTask {
        private final int                              bytes;
        private final int                              loops;
        private final TweetNaCl                        tweetnacl;

        private CryptoHashTask(CryptoHashFragment fragment,ProgressBar bar,int bytes,int loops) {
            super(fragment,bar);

            this.bytes     = bytes;
            this.loops     = loops;
            this.tweetnacl = new TweetNaCl();
        }

        @Override
        protected Result[] doInBackground(Void... params) {
            try {
                // ... initialise
                
                Random  random  = new Random();
                byte[]  message = new byte[bytes];
                long    start;
                long    total;
                int     progress;

                random.nextBytes(message);

                // ... crypto_hash

                start    = System.currentTimeMillis();
                total    = 0;
                progress = 0;

                for (int i=0; i<loops; i++)
                    { tweetnacl.cryptoHash(message);
                      total += message.length;
                      publishProgress(++progress/(2*loops));
                    }
                
                Result hash = new Result(total,System.currentTimeMillis() - start);
                
                // ... crypto_hashblocks

                start     = System.currentTimeMillis();
                total     = 0;
                
                for (int i=0; i<loops; i++)
                    { tweetnacl.cryptoHashBlocks(IV,message);
                      total += message.length;
                      publishProgress(++progress/(2*loops));
                    }

                Result hashblocks = new Result(total,System.currentTimeMillis() - start);

                // ... done
                
                return new Result[] { hash,hashblocks };
                
            } catch(Throwable x) {
                Log.e(TAG,"Error running crypto_core benchmark",x);
            }

            return null;
        }
    }
}
