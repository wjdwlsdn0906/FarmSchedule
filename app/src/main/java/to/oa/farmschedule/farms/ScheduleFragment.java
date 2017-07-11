package to.oa.farmschedule.farms;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Created by JJW on 2017-07-05.
 */

public class ScheduleFragment extends Fragment {
    public View layout;
    public TextView tvDate;

    public GridView gridView;
    public GridAdapter gridAdapter;

    public ListView lstview;
    public ArrayList<String> dayList;

    private Button leftBtn;
    private Button rightBtn;

    public Calendar mCal;
    public ViewHolder vholder;
    public MyDatabaseOpenHelper dbHelper;
    public SQLiteDatabase checkDB;

    int pos,txtPos;
    int start=0;
    int Month_int;
    int Year_int;
    String Month_str;
    String Year_str;



    public ScheduleFragment() {}

////////////////////////////////////
//////
////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
///////////////////////////////////




////////////////////////////////////
////// CreateView
////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_schedule,container, false);

        dbHelper = new MyDatabaseOpenHelper( getActivity(), "CALENDAR", null, 1);
        dbHelper.testDB();


        tvDate = (TextView)layout.findViewById(R.id.tv_date);
        gridView = (GridView)layout.findViewById(R.id.gridview);

        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        Month_str = curMonthFormat.format(date);
        Month_int = Integer.parseInt(curMonthFormat.format(date));
        Year_str = curYearFormat.format(date);

        //현재 날짜 텍스트뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));

        //gridview 요일 표시
        dayList = new ArrayList<String>();
        dayList.add("일");
        dayList.add("월");
        dayList.add("화");
        dayList.add("수");
        dayList.add("목");
        dayList.add("금");
        dayList.add("토");

        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }

        setCalendarDate(mCal.get(Calendar.MONTH) + 1);
        gridAdapter = new GridAdapter(getActivity(), dayList);
        gridView.setAdapter(gridAdapter);

        return layout;
    }
////////////////////////////////////








////////////////////////////////////
////// Calendar 이벤트 처리
////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();

        leftBtn = (Button)layout.findViewById(R.id.last_month);
        rightBtn = (Button)layout.findViewById(R.id.next_month);

        //왼쪽 버튼 클릭 시 이전 달 날짜를 보여줌
        leftBtn.setOnClickListener(new OnClickListener() {
                                       public void onClick(View v) {
                // 현재 날짜에서 Month - 1 하기 위해 변환
                Month_int = Integer.parseInt(Month_str);
                Year_int = Integer.parseInt(Year_str);

                if(Month_int != 1) {
                    Month_int = Month_int-1;
                }
                else {
                    Month_int = 12;
                    Year_int =Year_int-1;
                }

                // Month - 1 값을 표시하기 위해 String으로 형 변환
                Month_str = String.valueOf(Month_int);
                Year_str = String.valueOf(Year_int);

                if(Month_int<10) {
                    tvDate.setText(Year_str + "/0" + Month_str);
                }
                else
                    tvDate.setText(Year_str + "/" + Month_str);


                // 바뀐 달의 날짜를 보여주기 위해 달력 작업을 다시 진행
                dayList = new ArrayList<String>();
                dayList.add("일");
                dayList.add("월");
                dayList.add("화");
                dayList.add("수");
                dayList.add("목");
                dayList.add("금");
                dayList.add("토");

                mCal = Calendar.getInstance();
                mCal.set(Year_int, Month_int-1, 1);
                int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);
                gridAdapter = new GridAdapter(getActivity(), dayList);
                gridView.setAdapter(gridAdapter);
            }
        });

        // 오른쪽 버튼 클릭 시 다음 달의 날짜 표시, 왼쪽과 반대
        rightBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                        Month_int = Integer.parseInt(Month_str);
                        Year_int = Integer.parseInt(Year_str);

                        if(Month_int != 12) {
                            Month_int = Month_int+1;
                        }
                        else {
                            Month_int = 1;
                            Year_int =Year_int+1;
                        }


                        Month_str = String.valueOf(Month_int);
                        Year_str = String.valueOf(Year_int);

                if(Month_int<10) {
                    tvDate.setText(Year_str + "/0" + Month_str);
                }
                else
                    tvDate.setText(Year_str + "/" + Month_str);

                        dayList = new ArrayList<String>();
                        dayList.add("일");
                        dayList.add("월");
                        dayList.add("화");
                        dayList.add("수");
                        dayList.add("목");
                        dayList.add("금");
                        dayList.add("토");

                        mCal = Calendar.getInstance();
                        mCal.set(Year_int, Month_int-1, 1);
                        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

                        for (int i = 1; i < dayNum; i++) {
                            dayList.add("");
                        }
                        setCalendarDate(mCal.get(Calendar.MONTH) + 1);
                        gridAdapter = new GridAdapter(getActivity(), dayList);
                        gridView.setAdapter(gridAdapter);
                    }
                });



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pos = Integer.parseInt(dayList.get(position));
                    start=1;

                    txtPos = position;

//                    TextView test = (TextView)layout.findViewById(R.id.tv_item_gridview);
//                    test.setTextColor(getResources().getColor(R.color.blue));
//                    GridView grd = (GridView) layout.findViewById(R.id.gridview);
//                    grd.

                    lstview = (ListView) layout.findViewById(R.id.listView);

                    // ListView를 보여준다.
                    lstview.setVisibility(View.VISIBLE);
                    // DB Helper가 Null이면 초기화 시켜준다.
                    if (dbHelper == null) {
                        dbHelper = new MyDatabaseOpenHelper(getActivity(), "TEST", null, 1);
                    }

                    // 1. Person 데이터를 모두 가져온다.
                    List people = dbHelper.selectDB();
                    // 2. ListView에 Person 데이터를 모두 보여준다.
                    lstview.setAdapter(new PersonListAdapter(people, getActivity()));

            }
        });



        Button btn_Add = (Button)layout.findViewById(R.id.add);
        Button btn_Del = (Button)layout.findViewById(R.id.delete);

        btn_Add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText etMonth = new EditText(getActivity());
                etMonth.setText(Month_int+"월 "+pos+"일");
                final EditText etMemo = (EditText)new EditText(getActivity());
                etMemo.setHint("내용을 입력하세요.");

                layout.addView(etMonth);
                layout.addView(etMemo);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("일정을 입력하세요").setView(layout).setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {

                        int day_no = pos;
                        int month_no= Month_int;
                        String memo_data = etMemo.getText().toString();

                        if( dbHelper == null ) {
                            dbHelper = new MyDatabaseOpenHelper(getActivity(), "TEST", null , 1);
                        }
                        Data data = new Data();
                        data.setDay_no(day_no);
                        data.setMonth_no(month_no);
                        data.setMemo_data(memo_data);
                        dbHelper.insertDB(data);
                    }
                }) .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) { }
                }) .create() .show();
            }
        });

        btn_Del.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dbHelper.deleteDB(Month_int, pos);
            }
        });
    }
////////////////////////////////////














////////////////////////////////////
////// Calendar 해당 월, 일 표시
////////////////////////////////////
    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
    }
////////////////////////////////////




////////////////////////////////////
////// GridView 어댑터, getView 함수 이용해서 현재 날짜 및 초기 날짜 설정
////////////////////////////////////
    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        /*
         * 생성자
         * @param context
         * @param list
         */
        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 오늘에 날짜를 세팅 해준다.
            long Today = System.currentTimeMillis();
            final Date Today_date = new Date(Today);
            //연,월,일을 따로 저장
            final SimpleDateFormat ToDayMonth = new SimpleDateFormat("MM", Locale.KOREA);


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_schedule_calendar, parent, false);
                vholder = new ViewHolder();
                vholder.tvItemGridView = (TextView) convertView.findViewById(R.id.tv_item_gridview);
                convertView.setTag(vholder);
            } else {
                vholder = (ViewHolder) convertView.getTag();
            }

            vholder.tvItemGridView.setText("" + getItem(position));


            if(start == 0) {
                StringBuffer sb = new StringBuffer();
                sb.append(" SELECT DAY FROM CALENDAR ;");
                Data checkData;

                Cursor cursor = checkDB.rawQuery(sb.toString(), null);

                while (cursor.moveToNext()) {
                    checkData = new Data();
                    checkData.setDay_no(cursor.getInt(0));
                    if (String.valueOf(checkData.getDay_no()).equals(getItem(position))) {
                        vholder.tvItemGridView.setTextColor(getResources().getColor(R.color.colorAccent));
                    }

                }
                cursor.moveToFirst();
            }



            //해당 날짜 텍스트 컬러,배경 변경
            mCal = Calendar.getInstance();
            //오늘 day 가져옴
            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);

                if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                    vholder.tvItemGridView.setTextColor(getResources().getColor(R.color.colorPrimary));
                    Toast.makeText(getActivity(), "Date: " +getItem(position), Toast.LENGTH_SHORT).show();
                }

                return convertView;
            }

    }
////////////////////////////////////


    private class ViewHolder {
        TextView tvItemGridView;
    }










    ////////////////////////////////////
////// SQLite 부분, DB정의 및 검색,추가,삭제
////////////////////////////////////
    public class MyDatabaseOpenHelper extends SQLiteOpenHelper {
        public MyDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            StringBuffer sb = new StringBuffer();
            sb.append(" CREATE TABLE CALENDAR ( ");
            sb.append(" day INTEGER, ");
            sb.append(" month INTEGER, ");
            sb.append(" data TEXT ) ");

            // SQLite Database로 쿼리 실행
            db.execSQL(sb.toString());

            Toast.makeText(getActivity(), "Table 생성완료", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public void testDB() {
            checkDB = getWritableDatabase();
            SQLiteDatabase db = getReadableDatabase();
        }

        public void dropTB(){
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DROP TABLE CALENDAR;");
        }

        public void insertDB(Data data) {
            // 읽고 쓰기가 가능하게 DB 열기
            SQLiteDatabase db = getWritableDatabase();
            // DB에 입력한 값으로 행 추가
            db.execSQL("INSERT INTO CALENDAR VALUES(" + data.getDay_no() + ", " + data.getMonth_no() + ", '" + data.getMemo_data().toString() + "');");

            Toast.makeText(getActivity(), "Insert 완료", Toast.LENGTH_SHORT).show();
            db.close();


        }


        public void deleteDB(int month, int day) {
            SQLiteDatabase db = getWritableDatabase();
            // 입력한 항목과 일치하는 행 삭제
            db.execSQL("DELETE FROM CALENDAR WHERE month='" + month + "' and day='" + day + "';");
            //db.execSQL("DELETE FROM CALENDAR;");

            db.close();
        }

        public List selectDB() {
            StringBuffer sb = new StringBuffer();

            sb.append(" SELECT DAY, MONTH, DATA FROM CALENDAR WHERE day='" + pos + "' and month='" + Month_int + "';");

            // 읽기 전용 DB 객체를 만든다.
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(sb.toString(), null);

            List schData = new ArrayList();

            Data DBdata = null;

            // moveToNext 다음에 데이터가 있으면 true 없으면 false
            while (cursor.moveToNext()) {
                DBdata = new Data();
                DBdata.setDay_no(cursor.getInt(0));
                DBdata.setMonth_no(cursor.getInt(1));
                DBdata.setMemo_data(cursor.getString(2));

                schData.add(DBdata);
            }
            return schData;
        }


        public List getAllScheduleData() {
            StringBuffer sb = new StringBuffer();

            sb.append(" SELECT DAY, MONTH, DATA FROM CALENDAR ;");

            // 읽기 전용 DB 객체를 만든다.
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(sb.toString(), null);

            List schData = new ArrayList();

            Data DBdata = null;

            // moveToNext 다음에 데이터가 있으면 true 없으면 false
            while (cursor.moveToNext()) {
                DBdata = new Data();
                DBdata.setDay_no(cursor.getInt(0));
                DBdata.setMonth_no(cursor.getInt(1));
                DBdata.setMemo_data(cursor.getString(2));

                schData.add(DBdata);
            }
            return schData;
        }

    }
////////////////////////////////////








////////////////////////////////////
////// Schedule 관련 Data구조 정의 및 어댑터, 홀더
////////////////////////////////////
    public class Data {
        // PK
        private int day_no;
        private int month_no;
        private String memo_data;

        public int getDay_no() { return day_no; }
        public int getMonth_no() { return month_no; }
        public String getMemo_data() { return memo_data; }

        public void setDay_no(int day_no) { this.day_no = day_no; }
        public void setMonth_no(int month_no) { this.month_no = month_no; }
        public void setMemo_data(String memo_data) { this.memo_data = memo_data; }
    }

    private class PersonListAdapter extends BaseAdapter {
        private List people;
        private Context context;
        /* 생성자
         * @param people : Person List
         * @param context
         */
        public PersonListAdapter(List people, Context context) {
            this.people = people;
            this.context = context;
        }
            @Override
            public int getCount () {
                return this.people.size();
            }
            @Override
            public Object getItem ( int position){
                return this.people.get(position);
            }
            @Override
            public long getItemId ( int position){
                return position;
            }
            @Override
            public View getView ( int position, View convertView, ViewGroup parent){
                Holder holder = null;
                if (convertView == null) {
                    // convertView가 없으면 초기화합니다.
                    convertView = new LinearLayout(context);
                    ((LinearLayout) convertView).setOrientation(LinearLayout.HORIZONTAL);

                    TextView tvAge = new TextView(context);
                    tvAge.setPadding(20, 0, 20, 0);
                    tvAge.setTextColor(Color.rgb(0, 0, 0));

                    TextView tvName = new TextView(context);
                    tvName.setPadding(20, 0, 20, 0);
                    tvName.setTextColor(Color.rgb(0, 0, 0));

                    TextView tvPhone = new TextView(context);
                    tvPhone.setPadding(20, 0, 20, 0);
                    tvPhone.setTextColor(Color.rgb(0, 0, 0));

                    ((LinearLayout) convertView).addView(tvName);
                    ((LinearLayout) convertView).addView(tvAge);
                    ((LinearLayout) convertView).addView(tvPhone);

                    holder = new Holder();
                    holder.tvName = tvName;
                    holder.tvAge = tvAge;
                    holder.tvPhone = tvPhone;

                    convertView.setTag(holder);
                } else {
                    // convertView가 있으면 홀더를 꺼냅니다.
                    holder = (Holder) convertView.getTag();
                }
                // 한명의 데이터를 받아와서 입력합니다.
                Data DBdata = (Data) getItem(position);
                holder.tvAge.setText(DBdata.getDay_no() + "");
                holder.tvName.setText(DBdata.getMonth_no() + "");
                holder.tvPhone.setText(DBdata.getMemo_data());
                return convertView;
            }
        }

    private class Holder {
        public TextView tvName;
        public TextView tvAge;
        public TextView tvPhone;
    }
////////////////////////////////////

}