package br.com.guilhermedellatin.pirataflix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import br.com.guilhermedellatin.pirataflix.MainActivity;
import br.com.guilhermedellatin.pirataflix.model.Category;
import br.com.guilhermedellatin.pirataflix.model.Movie;

public class CategoryTask extends AsyncTask<String, Void, List<Category>> {

    private final WeakReference<Context> context;
    private ProgressDialog dialog;
    private CategoryLoader categoryLoader;

    public CategoryTask(Context context){
        this.context = new WeakReference<>(context);
    }

    public void setCategoryLoader(CategoryLoader categoryLoader) {
        this.categoryLoader = categoryLoader;
    }

    //main-thread
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();

        if(context != null) dialog = ProgressDialog.show(context, "Carregando", "", true);
    }

    //Vai ser executado paralelamente -> thread background
    @Override
    protected List<Category> doInBackground(String... params) {
        String url = params[0];

        try {
            URL requestUrl = new URL(url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2000);

            int responseCode = urlConnection.getResponseCode();
            if(responseCode > 400){
                throw new IOException("Erro na comunicação do servidor");
            }

            //Precisamos converter pra String
            InputStream inputStream = urlConnection.getInputStream();
            //Alocar espaços em memória para manipularmos a string em formato de buffer
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());

            String jsonAsString = toString(in);

            List<Category> categories = getCategories(new JSONObject(jsonAsString));

            in.close();

            return categories;

        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Category> getCategories(JSONObject jsonObject) throws JSONException {
        List<Category> categories = new ArrayList<>();

        JSONArray categoryArray = jsonObject.getJSONArray("category");
        for (int i = 0; i < categoryArray.length(); i++){
            JSONObject category =  categoryArray.getJSONObject(i);
            String title = category.getString("title");

            List<Movie> movies = new ArrayList<>();
            JSONArray movieArray = category.getJSONArray("movie");
            for (int j = 0; j < movieArray.length(); j++){
                JSONObject movie = movieArray.getJSONObject(j);
                String coverUrl = movie.getString("cover_url");

                Movie movieObj = new Movie();
                movieObj.setCoverUrl(coverUrl);
                movies.add(movieObj);
            }

            Category categoryObj = new Category();
            categoryObj.setName(title);
            categoryObj.setMovies(movies);

            categories.add(categoryObj);

        }

        return categories;
    }

    //main-thread
    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);
        dialog.dismiss();
        //listener
        if (categoryLoader != null){
            categoryLoader.onResult(categories);
        }
    }

    private String toString(InputStream is) throws IOException{
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lidos;
        while ((lidos = is.read(bytes)) > 0){
            baos.write(bytes, 0, lidos);
        }
        return new String(baos.toByteArray());
    }

    public interface CategoryLoader {
        void onResult(List<Category> categories);
    }

}
