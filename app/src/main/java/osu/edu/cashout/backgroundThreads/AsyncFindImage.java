//package osu.edu.cashout.backgroundThreads;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.ImageView;
//
//import java.io.InputStream;
//
//public class AsyncFindImage extends AsyncTask<String, Void, Bitmap> {
//    ImageView bmImage;
//
//    public AsyncFindImage(ImageView bmImage) {
//        bmImage = bmImage;
//    }
//
//    @Override
//    protected Bitmap doInBackground(String... strings) {
//        String imageURL = strings[0];
//        Bitmap bmp = null;
//        try {
//            InputStream in = new java.net.URL(imageURL).openStream();
//            bmp = BitmapFactory.decodeStream(in);
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//            e.printStackTrace();
//        }
//        return bmp;
//    }
//
//    protected void onPostExecute(Bitmap result) {
//        bmImage.setImageBitmap(result);
//    }
//
//}
