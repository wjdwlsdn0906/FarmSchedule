package to.oa.farmschedule.farms;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by JJW on 2017-07-05.
 */

public class CustomerAdapter extends PagerAdapter {


    LayoutInflater inflater;
    Context context;


    public CustomerAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.fragment_customer_view, null);
        ImageView img = (ImageView) view.findViewById(R.id.customer_view);

        switch (position) {
            case 0:
                 Picasso.with(context).load("http://wjdwlsdn0906.host.whoisweb.net/image/1st.png").into(img);
                break;
            case 1:
                Picasso.with(context).load("http://wjdwlsdn0906.host.whoisweb.net/image/2nd.png").into(img);
                break;
            case 2:
                Picasso.with(context).load("http://wjdwlsdn0906.host.whoisweb.net/image/3th.png").into(img);
                break;
            case 3:
                Picasso.with(context).load("http://wjdwlsdn0906.host.whoisweb.net/image/4th.png").into(img);
                break;
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == obj;
    }
}