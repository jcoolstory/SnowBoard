package com.jcoolstory.SnowBoardFree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;
import com.jcoolstory.SnowBoard.Engin.SnowBoard;
import com.jcoolstory.SnowBoardFree.R;
import com.jcoolstory.SnowBoardFree.R.id;
import com.jcoolstory.SnowBoardFree.R.layout;



import android.R.anim;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SetImageActivity extends Activity {
	 	private static final int IMAGE_FROM = 1;
		private static final int CROP_FROM_CAMERA =2;
		private AdView adView;
		private RelativeLayout linear;
		private Button btOk;
		private Button btCancel;
		private Button btNewImage;
		private static final String MY_AD_UNIT_ID = "a14ec364736489a";
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.setimage);

			File toFile = getBaseContext().getCacheDir();
			 
			toFile = new File(toFile.getAbsolutePath() +  "/" + SnowBoard.CAHCE_FILE_NAME);
			ImageView img = (ImageView) findViewById(R.id.setimageView);
			Bitmap bm = BitmapFactory.decodeFile(toFile.toString());
			if (bm == null)
			{
				bm = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_gallery);
			}
			img.setImageBitmap(bm);
			
			btOk = (Button) findViewById(R.id.button1);
			btCancel = (Button) findViewById(R.id.button2);
			btNewImage = (Button) findViewById(R.id.button3);
			
			btOk.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					setResult(SnowBoardPrefer.CHANGED);
					finish();
				}
			});
			btCancel.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setResult(SnowBoardPrefer.NO_CHANGED);
					finish();
				}
			});
			btNewImage.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				        intent.setType("image/*");
				       startActivityForResult(intent, IMAGE_FROM );
				}
			});
			linear = (RelativeLayout)View.inflate(this, R.layout.banner  , null );
			
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			addContentView(linear,params  );
	    }


		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			File toFile;
			File fromFile;
			switch (requestCode) {
			case IMAGE_FROM:
				
				File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp/");
				
				if (!dir.exists())
				{
					dir.mkdir();
				}
				
				toFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp/" + SnowBoard.CAHCE_FILE_NAME);
				Uri cropedImageUri = Uri.fromFile(toFile);
				if (null != data)
				{
					Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
					
					int Width = display.getWidth();
					int Height = display.getHeight();

					Uri uri = data.getData();
					Intent intent = new Intent("com.android.camera.action.CROP");
			        intent.setDataAndType(uri, "image/*");
			        
			        intent.putExtra("outputX", Width);
			        intent.putExtra("outputY", Height);
			        intent.putExtra("aspectX", Width);
			        intent.putExtra("aspectY",Height);
			        intent.putExtra("scale", true);
			        intent.putExtra("return-data", false);
			        intent.putExtra("noFaceDetection", true);
			        intent.putExtra("output", cropedImageUri);
			        
			        startActivityForResult(intent, CROP_FROM_CAMERA );
				}
				break;
			case CROP_FROM_CAMERA:
				
				 
					if (data == null)
					{

					}
					else
					{	
						final Bundle extras = data.getExtras();
					  
				        if(extras != null)
				        {
					        fromFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp/" + SnowBoard.CAHCE_FILE_NAME);
					        toFile = getBaseContext().getCacheDir();
					        
							toFile = new File(toFile.getAbsolutePath() +  "/" + SnowBoard.CAHCE_FILE_NAME);
							
							filecopy(fromFile.toString(), toFile.toString());
							ImageView img = (ImageView) findViewById(R.id.setimageView);
							
							Bitmap bm = BitmapFactory.decodeFile(toFile.toString());
							img.setImageBitmap(bm);

				        }

					}
				break;

			default:
				break;
			}

		}
		public void filecopy(String src, String dest)
		{
			InputStream in;
			try {
				in = new FileInputStream(src);

			   OutputStream out = new FileOutputStream(dest);
			   
			   byte[] buf = new byte[1024];
			   int len;
			   while((len = in.read(buf)) > 0){
			    out.write(buf,0,len);
			   }
			   in.close();
			   out.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		AdListener admobListener = new AdListener() {
			
			public void onReceiveAd(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onPresentScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onLeaveApplication(Ad arg0) {
				// TODO Auto-generated method stub
			}
			
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				// TODO Auto-generated method stub
				
			}
			
			public void onDismissScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			if (adView != null)
			{
				adView.destroy();
			}
		}


		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			if (adView == null)
			{
				AdRequest ar = null;
				
				adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);
				
				adView.setAdListener(admobListener);
				linear.addView(adView);
			
				ar = new AdRequest();		     
				
				adView.loadAd(ar);
			}
		}
}
