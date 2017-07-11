package to.oa.farmschedule.farms;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class NoticeContentFragment extends Fragment {
    private String title, content;

    private View layout;
    private LinearLayout back;
    private TextView notice_title, notice_content;

    public NoticeContentFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            title = (String) getArguments().get("title");
        } catch (Exception e) {
            title = "";
        }

        try {
            content = (String) getArguments().get("content");
        } catch (Exception e) {
            content = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_notice_content, container, false);

        back = (LinearLayout) layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_home, new NoticeBoardFragment());
                fragmentTransaction.commit();
            }
        });

        notice_title = (TextView) layout.findViewById(R.id.notice_title);
        notice_content = (TextView) layout.findViewById(R.id.notice_content);

        notice_title.setText(title);
        notice_content.setText(content);

        return layout;
    }
}