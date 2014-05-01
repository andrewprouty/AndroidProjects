package com.prouty.leagueusa.sdsolschedule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class ImageBismarck {
	private static final String TAG = "ImageBismarck";
	// http://bismarck.sdsu.edu/photoserver/userphotos/2
	private static final String ENDPOINT = "http://bismarck.sdsu.edu/photoserver/photo/";
	private Context context;

	public String fetchImage(PhotoItem photoItem, Context appContext, int width, int height) {
		Log.d(TAG, "fetchImage()");
		context = appContext; // Sets class variable
		String fName = "Image-"+photoItem.getPhotoId()+".jpg";
		String sURL = Uri.parse(ENDPOINT).toString() + photoItem.getPhotoId();
		try {
			if (!isCachedImage(fName)) {
				if(!GETImage(sURL, fName, width, height)) {
					fName = null;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "fetchImage() Exc: "+e.getMessage(),e);
		}
		return fName;
	}

	private Boolean isCachedImage(String fName) {
		// does the file exist? - true/false
		String filePath = context.getFilesDir().getPath()+"/"+fName;
		File file = new File(filePath);
		if(file.exists()) {
			Log.i(TAG, "isCachedImage() YES: "+fName);
			return true;
		}
		else	{
			return false;
		}
	}
	private Boolean GETImage(String sURL, String fName, int width, int height) {
		Bitmap mBitmap;
		try { // #1- download
			mBitmap = getScaledBitmapFromUrl(sURL, width, height);
			if (mBitmap == null) {
				Log.d(TAG, "GETImage null");
				return false;
			}
			else {
				Log.i(TAG, "GETImage YES: "+fName);
			}
		} catch (IOException e) {
			Log.e(TAG, "GETImage() IOException: "+ e.getMessage(), e);
			return false;
		} catch (Exception e) {
			Log.e(TAG, "GETImage() Exc: " +e.getMessage(), e);
			return false;
		}
		// #2 - save to cache (file)... yes must be space
		if (!cacheImage(fName, mBitmap)) {
			return false;
		}
		return true;
	}
	private Boolean cacheImage (String fName, Bitmap photo){
		FileOutputStream cFile;
	    try {
	    	cFile =context.openFileOutput(fName, Context.MODE_PRIVATE);
   			photo.compress(Bitmap.CompressFormat.JPEG,50,cFile);
   			cFile.flush();
	    	cFile.close();
	    } catch (IOException e) {
	        Log.e(TAG, "cacheImage() IOException: "+e.getMessage(), e);
	        return null;
	    } catch (Exception e) {
	        Log.e(TAG, "cacheImage() Exc: "+e.getMessage(), e);
	        return null;
	    }
		Log.i(TAG, "cacheImage():" +context.getFileStreamPath(fName));
    	return true;
	}
	private static Bitmap getScaledBitmapFromUrl(String imageUrl, int requiredWidth, int requiredHeight) throws IOException{
		try { // http://blog.vandzi.com/2013/01/get-scaled-image-from-url-in-android.html
			URL url = new URL(imageUrl);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
			options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
			options.inJustDecodeBounds = false;
			//don't use same inputstream object as in decodestream above. It will not work because 
			//decode stream edit input stream. So if you create 
			//InputStream is =url.openConnection().getInputStream(); and you use this in  decodeStream
			//above and bellow it will not work!
			Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream(),null, options);
			Log.i(TAG, "getScaledBitmapFromUrl(): w="+requiredWidth+" x h="+requiredHeight+"=> sample: < "+options.inSampleSize+" >");
			return bm;

		} catch (IOException e) {
			Log.e(TAG, "getScaledBitmapFromUrl() IOException: "+e.getMessage(), e);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "getScaledBitmapFromUrl() OutOfMemory: "+e.getMessage(), e);
		} catch (Exception e) {
			Log.e(TAG, "getScaledBitmapFromUrl() Exc: "+e.getMessage(), e);
		}
		Log.e("getScaledBitmapFromUrl() url: ", imageUrl);
		return null;
	}
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	        inSampleSize = Math.round((float) height / (float) reqHeight);
	        } else {
	        inSampleSize = Math.round((float) width / (float) reqWidth);
	        }
	    }
	    return inSampleSize;
	}
}