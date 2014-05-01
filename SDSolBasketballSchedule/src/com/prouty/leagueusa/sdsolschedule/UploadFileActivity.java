package com.prouty.leagueusa.sdsolschedule;

import java.io.File;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadFileActivity extends Activity {
	private static final String TAG = "UploadFileActivity";
	private static int RESULT_LOAD_IMAGE = 1;
	private EditText mFileNameEditText;
	private Button mButtonUploadFile;
	private String mPicturePath;
	private String mPictureName;
	FileUploadTask mFileUploadTask;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_file);

		mFileNameEditText = (EditText)findViewById(R.id.upload_file_name_editText);
		mFileNameEditText.setEnabled(false);

		Button buttonViewGallery = (Button) findViewById(R.id.buttonViewGallery);
		buttonViewGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
		mButtonUploadFile = (Button) findViewById(R.id.buttonUploadFile);
		mButtonUploadFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String fname = mFileNameEditText.getText().toString();
				if (fname == null || fname.length() == 0) {
					fname = mPictureName;
				}
				if (fname.contains(" ")) {
					fname = fname.trim().replace(" ","-");
					mFileNameEditText.setText(fname);
				}
				startFileUpload(fname);
			}
		});
		mButtonUploadFile.setEnabled(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			mPicturePath = cursor.getString(columnIndex);
			cursor.close();
			Log.d(TAG,"onActivityResult() mPicturePath: "+mPicturePath);  //contains the path of selected Image

			ImageView imageView = (ImageView) findViewById(R.id.upload_imageView);

			final BitmapFactory.Options options=new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;

	        // Calculate inSampleSize
	        BitmapFactory.decodeFile(mPicturePath, options); // sets options width & height
			final int reqWidth = imageView.getWidth();
			final int reqHeight = imageView.getHeight();
	        Log.d(TAG,"onActivityResult() field w="+reqWidth+" x h="+reqHeight);
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
			Bitmap bm=BitmapFactory.decodeFile(mPicturePath, options);
			
			imageView.setImageBitmap(bm);

			String plus = mPicturePath.substring(mPicturePath.lastIndexOf('/')+1, mPicturePath.length() );
			mPictureName = plus.substring(0, plus.lastIndexOf('.')); // no extension
			Log.d(TAG,"onActivityResult() name: "+mPictureName);

			mButtonUploadFile.setEnabled(true);
			mFileNameEditText.setEnabled(true);
			mFileNameEditText.setText(mPictureName);
			
		}
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;

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
        Log.d(TAG,"calculateInSampleSize() pixels w="+width+" x h="+height+" =>inSampleSize: < "+inSampleSize+" >");
        return inSampleSize;
	}

	private void resultToast(Boolean success, String msg) {
		int messageResId = 0;
		if (success) {
			messageResId = R.string.upload_success;
		} else {
			messageResId = R.string.upload_failure;
		}
		Log.d(TAG,"resultToast() messageResId "+messageResId);
		Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
	}
	private void startFileUpload(String desiredFname) {
		mButtonUploadFile.setEnabled(false);
		mFileNameEditText.setEnabled(false);

		Log.d(TAG,"startFileUpload() Tasking...");
		mFileUploadTask = new FileUploadTask();
		mFileUploadTask.execute(mPicturePath, desiredFname); //arg for doInBackground()
		Log.d(TAG,"startFileUpload() Task launched");
	}
	private class FileUploadTask extends AsyncTask<String,Void,String> {
		//<x,y,z> params: 1-doInBackground(x); 2-onProgressUpdate(y); 3-onPostExecute(z) 
		@Override
		protected String doInBackground(String... params) {
			String pictureWithPath = params[0];
			String fname = params[1];
			Log.d(TAG, "FileUploadTask().doInBackground");
			Log.d(TAG, "FileUploadTask().doInBackground from: "+pictureWithPath);
			Log.d(TAG, "FileUploadTask().doInBackground to: "+fname);
			File file= new File (pictureWithPath);
			
			String urlServer = "http://bismarck.sdsu.edu/photoserver/postphoto/Andrew/1161/"+fname;
			Log.d(TAG, "FileUploadTask().doInBackground -"+ urlServer);
			String msg = "";
			try {
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				HttpPost getMethod = new HttpPost(urlServer);
				HttpClient httpclient = new DefaultHttpClient();
				FileEntity photo = new FileEntity(file, "image/jpeg");
				getMethod.setEntity(photo);
				String responseBody = httpclient.execute(getMethod, responseHandler);
				msg = responseBody;
			} catch (Exception e) {
				Log.e(TAG, "FileUploadTask().doInBackground() Exc: "+e.getMessage(),e);
				msg = "Exception: " + e.getMessage();
			}
			return msg;
		}
		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "FileUploadTask().onPostExecute result: "+result);
			resultToast(!result.startsWith("Exception"), result);
			cancel(true);
			Log.d(TAG, "FileUploadTask onPostExecute()");
		}
	}
}
