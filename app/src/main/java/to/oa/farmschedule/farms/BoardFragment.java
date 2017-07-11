package to.oa.farmschedule.farms;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by JJW on 2017-07-05.
 */

public class BoardFragment extends Fragment implements View.OnClickListener {

    private View layout;
    private LinearLayout notice_board, work_board, trade_board, qna_board;

    public BoardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_board, container, false);

        notice_board = (LinearLayout) layout.findViewById(R.id.notice_board);
        work_board = (LinearLayout) layout.findViewById(R.id.work_board);
        trade_board = (LinearLayout) layout.findViewById(R.id.trade_board);
        qna_board = (LinearLayout) layout.findViewById(R.id.qna_board);

        notice_board.setOnClickListener(this);
        work_board.setOnClickListener(this);
        trade_board.setOnClickListener(this);
        qna_board.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if(v.getId() == R.id.notice_board) {
            fragmentTransaction.replace(R.id.fragment_home, new NoticeBoardFragment());
            fragmentTransaction.commit();
        } else if (v.getId() == R.id.work_board) {
            Toast.makeText(getActivity(), "work_board", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.trade_board) {
            Toast.makeText(getActivity(), "trade_board", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.qna_board) {
            Toast.makeText(getActivity(), "qna_board", Toast.LENGTH_SHORT).show();
        }
    }

}
