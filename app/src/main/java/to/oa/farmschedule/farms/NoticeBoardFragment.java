package to.oa.farmschedule.farms;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class NoticeBoardFragment extends Fragment {

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_NO = "no";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT ="content";

    private View layout;
    private LinearLayout back;
    private ListView listview;
    private ListViewAdapter adapter;

    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;

    public NoticeBoardFragment() {

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
        layout = inflater.inflate(R.layout.fragment_notice_board, container, false);

        back = (LinearLayout) layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_home, new BoardFragment());
                fragmentTransaction.commit();
            }
        });

        adapter = new ListViewAdapter();
        listview = (ListView) layout.findViewById(R.id.notice_board);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                NoticeContentFragment fragment = new NoticeContentFragment();

                HashMap<String,String> outputHashMap = mArrayList.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("title", outputHashMap.get("title"));
                bundle.putString("content", outputHashMap.get("content"));

                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_home, fragment);
                fragmentTransaction.commit();
            }
        });

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
                Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
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

                adapter.addItem(title);
                adapter.notifyDataSetChanged();

                mArrayList.add(hashMap);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    public class ListViewItem {
        private String title;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }

    public class ListViewAdapter extends BaseAdapter {

        private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
        private ArrayList<ListViewItem> filteredItemList = listViewItemList ;

        public ListViewAdapter() { }

        @Override public int getCount() { return filteredItemList.size() ; }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext(); // "listview_item" Layout을 inflate하여 convertView 참조 획득.

            if (convertView == null) { LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_notice_board, parent, false);
            }

            TextView titleTextView = (TextView) convertView.findViewById(R.id.title);

            ListViewItem listViewItem = filteredItemList.get(position); // 아이템 내 각 위젯에 데이터 반영
            titleTextView.setText(listViewItem.getTitle());

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public Object getItem(int position) {
            return filteredItemList.get(position) ;
        }

        public void addItem(String title) {
            ListViewItem item = new ListViewItem();
            item.setTitle(title);
            listViewItemList.add(item);
        }
    }

}