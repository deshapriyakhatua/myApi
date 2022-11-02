package com.example.recyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        ArrayList<DataList> list = new ArrayList<>();



        // api request ...

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://beautibebo.herokuapp.com/products/";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // creating DataList object
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = object.getJSONArray("product");
                            for(int i=0; i< arr.length(); i++){

                                JSONObject obj = arr.getJSONObject(i);

                                list.add(new DataList(obj.getString("image"),obj.getString("name"),obj.getString("description"),obj.getString("brand"),obj.getString("price")));

                            }

                            //creating recyclerView
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            recyclerView.setHasFixedSize(true);

                            CustomAdapter customAdapter = new CustomAdapter(list);
                            recyclerView.setAdapter(customAdapter);
                        }catch (JSONException e){
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            Log.e("api-e","onErrorResponse : "+e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                Log.e("api","onErrorResponse : "+error.getLocalizedMessage());
            }
        });

        queue.add(stringRequest);

        // ... api request

    }



    // class for data list Object ...
    public class DataList{
        String image;
        String name;
        String description;
        String brand;
        String price;

        public DataList(String image, String name, String description, String brand, String price){
            this.image=image;
            this.name=name;
            this.description=description;
            this.brand=brand;
            this.price=price;
        }

        public String getImage() {  return image;  }

        public void setImage(String image) {  this.image = image;   }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getBrand() {  return brand;  }

        public void setBrand(String brand) {   this.brand = brand;  }

        public String getPrice() {  return price;  }

        public void setPrice(String price) {   this.price = price;  }
    }

    // ... class for data list Object




    // class for custom adapter class...

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{
        public class CustomViewHolder extends RecyclerView.ViewHolder{
            ImageView img;
            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;
            public CustomViewHolder(View itemView){
                super(itemView);
                img = itemView.findViewById(R.id.imageView);
                tv1 = itemView.findViewById(R.id.name);
                tv2 = itemView.findViewById(R.id.description);
                tv3 = itemView.findViewById(R.id.brand);
                tv4 = itemView.findViewById(R.id.price);
            }
        }

        ArrayList<DataList> list;
        public CustomAdapter(ArrayList<DataList> list){
            this.list=list;
        }

        public CustomViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
            return new CustomViewHolder(view);
        }

        public void onBindViewHolder(CustomViewHolder holder, int position){
            new DownloadImageFromInternet(holder.img).execute(list.get(position).getImage());
            holder.tv1.setText(list.get(position).getName());
            holder.tv2.setText(list.get(position).getDescription());
            holder.tv3.setText(list.get(position).getBrand());
            holder.tv4.setText("Price: "+list.get(position).getPrice());
        }

        public int getItemCount(){
            return list.size();
        }
    }

    // ... class for custom adapter class

    // loading image ...

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few seconds...",Toast.LENGTH_SHORT).show();
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
    // ... loading image
}