package com.prouty.leagueusa.sdsolschedule;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFragment extends Fragment{
	private static final String TAG = "ImageFragment";
	private String mImageFileName;
	private PhotoItem mPhotoItem;
	private Callbacks mCallbacks;

	FetchImageTask mFetchImageTask;

	View view;
	TextView mUserTextView;
	TextView mPhotoTextView;
	ImageView mImageView;

	public ImageFragment init(int position) { //removed static
		ImageFragment frag = new ImageFragment();
		// set position input as an argument available onCreate
		Bundle b = new Bundle();
		b.putInt("position", position);
		frag.setArguments(b);
		return frag;
	}

	public interface Callbacks {
		PhotoItem getPhotoItem(int pos);
		Boolean isTwoPane();
		int handleFieldWidth(int value);
		int handleFieldHeight(int value);
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true); // survive across Activity re-create (i.e. orientation)
	}

	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{       
		int fragPosition = getArguments() != null ? getArguments().getInt("position") : -1;
		mPhotoItem=mCallbacks.getPhotoItem(fragPosition);
		Log.d(TAG, "onCreateView() ["+fragPosition+"] "+mPhotoItem.getPhotoId());

		view = inflater.inflate(R.layout.fragment_image, container,false);

		if (mCallbacks.isTwoPane() == false) { // two-pane displays these elsewhere - NOT this view
			mUserTextView = (TextView)view.findViewById(R.id.image_user_name);
			mPhotoTextView = (TextView)view.findViewById(R.id.image_photo_name);

			mUserTextView.setText(mPhotoItem.getUserName());
			mPhotoTextView.setText(mPhotoItem.getPhotoName());
		}
		
		mImageView = (ImageView)view.findViewById(R.id.image_imageView);
		mImageView.setImageResource(R.drawable.image_pending);
		
		view.post(new Runnable() {
		    @Override
		    public void run() {
		    	// getWidth requires the view to visible
		    	// waiting - to enable faster download by scaling images as part of downloading 
		    	Log.d(TAG,"onCreateView() post run field="+mImageView.getWidth()+" x h="+mImageView.getHeight());
		        mCallbacks.handleFieldWidth(mImageView.getWidth());
		        mCallbacks.handleFieldHeight(mImageView.getHeight());
				mFetchImageTask = new FetchImageTask(mPhotoItem);
				mFetchImageTask.execute();
		    }
		});
		return view;
	}

	private void setupImage(PhotoItem photoItem) {
		if (getActivity() == null || mImageView == null) {
			return;
		}
		// Async downloads to [cache] file, use the file
		Log.d(TAG, "setupImage() request:"+photoItem.getPhotoId()+"-"+photoItem.getPhotoName()+";"
				+" returned: "+mPhotoItem.getPhotoId()+"-"+mPhotoItem.getPhotoName());
		if (mImageFileName == null || mImageFileName.length()==0) {
			mImageView.setImageResource(R.drawable.image_not_available);
			Log.d(TAG, "setupImage() null/no filename");
		}
		else {
			Log.d(TAG, "setupImage():"+mImageFileName);
			Bitmap bmImage=decodeBitmapFromFilename(mImageFileName, mImageView);
			mImageView.setImageBitmap(bmImage);
		}
	}
	
	private Bitmap decodeBitmapFromFilename(String filename, ImageView imageView) {
		// Based upon http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
		// First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options); // To set options width & height
        
        // Calculate inSampleSize
        // Raw height and width of image
        final int width = options.outWidth;
        final int height = options.outHeight;
        final int reqWidth = mCallbacks.handleFieldWidth(imageView.getWidth());
        final int reqHeight = mCallbacks.handleFieldHeight(imageView.getHeight());
        Log.d(TAG,"decodeBitmapFromFilename() field w="+reqWidth+" x h="+reqHeight);

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Log.d(TAG,"decodeBitmapFromFilename() pixels w="+width+" x h="+height+"=> sample: < "+inSampleSize+" >");
        return BitmapFactory.decodeFile(filename, options);
	}

	private class FetchImageTask extends AsyncTask<PhotoItem,Void,String> {
		//<x,y,z> params: 1-doInBackground(x); 2-onProgressUpdate(y); 3-onPostExecute(z) 
		private Context c;
		private PhotoItem photoItem;
		private int width;
		private int height;
		//Constructor
		public FetchImageTask (PhotoItem photoItem) {
			this.c = getActivity().getApplicationContext();
			this.photoItem = photoItem;
			width = mCallbacks.handleFieldWidth(0);
			height= mCallbacks.handleFieldHeight(0);
			Log.d(TAG, "constructor w="+width+" x h="+height);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(PhotoItem... params) {
			Log.d(TAG, "FetchImageTask doInBackground()");
			Log.d(TAG, "w="+width+" x h="+height);
			String fname = null;
			try {
				fname = new ImageBismarck().fetchImage(photoItem, c, width, height);

			} catch (Exception e) {
				Log.e(TAG, "doInBackground() Exception.", e);
			}
			return fname;
		}
		@Override
		protected void onPostExecute(String fName) {
			if (fName == null) {
				mImageFileName = "";
			}
			else {
				mImageFileName = c.getFilesDir().getPath()+"/"+fName;
			}
			setupImage(photoItem);
			cancel(true); // done !
		}
		@Override
		protected void onCancelled() {
			Log.d(TAG, "FetchImageTask onCancelled()");
		}
	}
}
