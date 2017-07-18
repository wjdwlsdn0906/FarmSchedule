package to.oa.farmschedule.farms;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JJW on 2017-07-05.
 */

public class QnaContentBoardFragment extends Fragment {

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_COMMENT ="comment";

    private View layout;
    private LinearLayout back, line;
    private ListView listview;
    private ListViewAdapter adapter;
    private TextView title, content, id, comment;
    private String qna_no, qna_title, qna_content;
    private String mJsonString;
    private ArrayList<HashMap<String, String>> mArrayList;

    public QnaContentBoardFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            qna_no = (String) getArguments().getString("no");
        } catch (Exception e) {
            qna_no = "";
        }

        try {
            qna_title = (String) getArguments().getString("title");
        } catch (Exception e) {
            qna_title = "";
        }

        try {
            qna_content = (String) getArguments().getString("content");
        } catch (Exception e) {
            qna_content = "";
        }

        mArrayList = new ArrayList<>();
        GetData task = new GetData();
        task.execute("http://wjdwlsdn0906.host.whoisweb.net/php/qna_board_comment.php");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_qna_content_board, container, false);

        back = (LinearLayout) layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_home, new QnaBoardFragment());
                fragmentTransaction.commit();
            }
        });

        title = (TextView) layout.findViewById(R.id.title);
        title.setText(qna_title);
        content = (TextView) layout.findViewById(R.id.content);
        content.setText(qna_content);

        adapter = new ListViewAdapter();
        listview = (ListView) layout.findViewById(R.id.comment_board);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO
            }
        });

        final EditText editText = (EditText) layout.findViewById(R.id.searchFilter);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), ""+editText.getText(), Toast.LENGTH_SHORT).show();
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
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "response  - " + result);

            if (result == null){
                Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "no=" + qna_no;

            try  {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST"); // POST
                httpURLConnection.setDoInput(true); // POST
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

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

                Log.d(TAG, "GetData: Error ", e);
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

                String id = item.getString(TAG_ID);
                String comment = item.getString(TAG_COMMENT);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_COMMENT, comment);

                adapter.addItem(id, comment);
                adapter.notifyDataSetChanged();

                mArrayList.add(hashMap);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    public class ListViewItem {
        private String id;
        private String comment;

        public void setId(String id) { this.id = id; }

        public String getId() { return this.id; }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getComment() {
            return this.comment;
        }
    }

    public class ListViewAdapter extends BaseAdapter {

        private Filter listFilter;
        private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
        private ArrayList<ListViewItem> filteredItemList = listViewItemList ;

        public ListViewAdapter() { }

        @Override public int getCount() { return filteredItemList.size() ; }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext(); // "listview_item" Layout을 inflate하여 convertView 참조 획득.

            if (convertView == null) { LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_qna_comment, parent, false);
            }

            id = (TextView) convertView.findViewById(R.id.id);
            comment = (TextView) convertView.findViewById(R.id.comment);

            ListViewItem listViewItem = filteredItemList.get(position); // 아이템 내 각 위젯에 데이터 반영
            id.setText(listViewItem.getId());
            comment.setText(listViewItem.getComment());

            Log.w("test", "요기");

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

        public void addItem(String id, String comment) {
            ListViewItem item = new ListViewItem();
            item.setId(id);
            item.setComment(comment);
            listViewItemList.add(item);
        }
    }
}