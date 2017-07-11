package to.oa.farmschedule.farms;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JJW on 2017-07-05.
 */

public class HomeFragment extends Fragment {

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_NO = "no";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT ="content";

    private View layout;
    private ViewPager viewPager;
    private ListView listview;
    private LinearLayout layout_notice_1st, layout_notice_2nd, layout_notice_3rd;
    private TextView notice_1st, notice_2nd, notice_3rd;

    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArrayList = new ArrayList<>();
        GetData task = new GetData();
        task.execute("http://wjdwlsdn0906.host.whoisweb.net/notice_board.php");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_home, container, false);

        layout_notice_1st = (LinearLayout) layout.findViewById(R.id.layout_notice_1st);
        layout_notice_2nd = (LinearLayout) layout.findViewById(R.id.layout_notice_2nd);
        layout_notice_3rd = (LinearLayout) layout.findViewById(R.id.layout_notice_3rd);

        notice_1st = (TextView) layout.findViewById(R.id.notice_1st);
        notice_2nd = (TextView) layout.findViewById(R.id.notice_2nd);
        notice_3rd = (TextView) layout.findViewById(R.id.notice_3rd);

        viewPager = (ViewPager) layout.findViewById(R.id.customer_pager);
        CustomerAdapter viewAdapter = new CustomerAdapter(inflater, getActivity());
        viewPager.setAdapter(viewAdapter);

        return layout;
    }

    private class GetData extends AsyncTask<String, Void, String> {
//        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = ProgressDialog.show(getActivity(), "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null){
                Toast.makeText(getActivity(), "2 : " + errorString, Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                Log.e("test" , mJsonString);
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try  {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String no = item.getString(TAG_NO);
                String title = item.getString(TAG_TITLE);
                String content = item.getString(TAG_CONTENT);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_NO, no);
                hashMap.put(TAG_TITLE, title);
                hashMap.put(TAG_CONTENT, content);

                if (i == 0) {
                    notice_1st.setText(title);
                } else if (i == 1) {
                    notice_2nd.setText(title);
                } else if (i == 2) {
                    notice_3rd.setText(title);
                }

                mArrayList.add(hashMap);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

}